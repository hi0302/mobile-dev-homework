package com.example.hw03

import android.content.res.Configuration
import android.content.pm.ActivityInfo
import android.database.DataSetObserver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.anything
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ContactsActivityTest {
    @Test
    fun rotationKeepsPageSelectionAndChosenText() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.btnPage3)).perform(click())
            onData(anything()).inAdapterView(withId(R.id.listContacts)).atPosition(2).perform(click())
            for ((requested, expected) in listOf(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE to Configuration.ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT to Configuration.ORIENTATION_PORTRAIT,
            )) {
                scenario.onActivity { it.requestedOrientation = requested }
                val deadline = SystemClock.uptimeMillis() + 10_000
                var orientation = Configuration.ORIENTATION_UNDEFINED
                do {
                    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                    scenario.onActivity { orientation = it.resources.configuration.orientation }
                    if (orientation != expected) SystemClock.sleep(100)
                } while (orientation != expected && SystemClock.uptimeMillis() < deadline)
                assertEquals(expected, orientation)
                assertPage(scenario, 2)
                onView(withId(R.id.txtChoose)).check(matches(withText("You choose: Võ Văn M")))
                scenario.onActivity { activity ->
                    val adapter = activity.findViewById<ListView>(R.id.listContacts).adapter as ContactAdapter
                    assertEquals(13, adapter.selectedId)
                }
            }
        }
    }

    @Test
    fun launchAndAllThreePagesMatchRepositoryAndButtonStates() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.txtChoose)).check(matches(withText("You choose: ")))
            for (page in 0..2) {
                if (page > 0) onView(withId(buttonIds[page])).perform(click())
                assertPage(scenario, page)
            }
        }
    }

    @Test
    fun selectionSurvivesPageChangesRecreationAndRepeatedNavigation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onData(anything()).inAdapterView(withId(R.id.listContacts)).atPosition(1).perform(click())
            onView(withId(R.id.txtChoose)).check(matches(withText("You choose: Lê Thị B")))
            scenario.onActivity { activity ->
                val list = activity.findViewById<ListView>(R.id.listContacts)
                assertEquals(activity.getColor(R.color.row_selected), (list.getChildAt(1).background as ColorDrawable).color)
            }
            repeat(8) {
                for (page in listOf(1, 2, 0)) {
                    onView(withId(buttonIds[page])).perform(click())
                    assertPage(scenario, page)
                }
            }
            onView(withId(R.id.btnPage2)).perform(click())
            scenario.recreate()
            assertPage(scenario, 1)
            onView(withId(R.id.txtChoose)).check(matches(withText("You choose: Lê Thị B")))
            onView(withId(R.id.btnPage1)).perform(click())
            scenario.onActivity { activity ->
                val list = activity.findViewById<ListView>(R.id.listContacts)
                assertEquals(activity.getColor(R.color.row_selected), (list.getChildAt(1).background as ColorDrawable).color)
                var changes = 0
                (list.adapter as ContactAdapter).registerDataSetObserver(object : DataSetObserver() {
                    override fun onChanged() { changes++ }
                })
                activity.findViewById<Button>(R.id.btnPage1).performClick()
                assertEquals("Clicking current page must not reload the adapter", 0, changes)
            }
        }
    }

    @Test
    fun adapterReusesRowsResetsHighlightAndHandlesInvalidPositions() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val parent = activity.findViewById<ListView>(R.id.listContacts)
                val adapter = ContactAdapter(activity)
                val source = ContactRepository.sample().toMutableList()
                val first = source.first()
                adapter.submit(source)
                source.clear()
                assertEquals(15, adapter.count)
                var changes = 0
                adapter.registerDataSetObserver(object : DataSetObserver() {
                    override fun onChanged() { changes++ }
                })
                adapter.selectedId = first.id
                adapter.selectedId = first.id
                assertEquals(1, changes)
                val row = adapter.getView(0, null, parent)
                assertEquals(activity.getColor(R.color.row_selected), (row.background as ColorDrawable).color)
                assertEquals(activity.getString(R.string.contact_description, first.name, first.phone), row.contentDescription)
                assertSame(row, adapter.getView(1, row, parent))
                assertEquals(Color.TRANSPARENT, (row.background as ColorDrawable).color)
                assertEquals(ContactRepository.sample()[1].name, row.findViewById<TextView>(R.id.txtName).text.toString())
                for (position in listOf(-1, 15, Int.MAX_VALUE)) {
                    assertNull(adapter.getItem(position))
                    adapter.getView(position, row, parent)
                    assertEquals("", row.findViewById<TextView>(R.id.txtName).text.toString())
                    assertEquals("", row.findViewById<TextView>(R.id.txtPhone).text.toString())
                    assertNull(row.contentDescription)
                    assertEquals(Color.TRANSPARENT, (row.background as ColorDrawable).color)
                }
            }
        }
    }

    @Test
    fun pageControllerHidesUnavailableButtonsAndUsesZeroBasedCallbacks() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val buttons = List(3) { Button(activity) }
                val clicks = mutableListOf<Int>()
                val controller = PageButtonController(buttons, clicks::add)
                controller.render(2, 0)
                assertFalse(buttons[0].isEnabled)
                assertEquals(0.35f, buttons[0].alpha, 0.001f)
                assertEquals(View.GONE, buttons[2].visibility)
                assertEquals("Page 2", buttons[1].contentDescription)
                buttons[1].performClick()
                controller.render(3, 1)
                assertEquals(View.VISIBLE, buttons[2].visibility)
                assertTrue(buttons[2].isEnabled)
                assertEquals(1f, buttons[2].alpha, 0.001f)
                buttons[2].performClick()
                assertEquals(listOf(1, 2), clicks)
                controller.render(0, 0)
                assertTrue(buttons.all { it.visibility == View.GONE })
            }
        }
    }

    @Test
    fun selectionStateValidatesRestoresAndSavesBundle() {
        val ids = ContactRepository.sample().map { it.id }.toSet()
        val saved = Bundle().apply {
            putInt(SelectionState.KEY_PAGE, 2)
            putInt(SelectionState.KEY_SELECTED_ID, 12)
        }
        val state = SelectionState.restore(saved, 3, ids)
        assertEquals(2, state.page)
        assertEquals(12, state.selectedId)
        val copy = Bundle()
        state.saveTo(copy)
        assertEquals(2, copy.getInt(SelectionState.KEY_PAGE))
        assertEquals(12, copy.getInt(SelectionState.KEY_SELECTED_ID))
        saved.putInt(SelectionState.KEY_PAGE, Int.MAX_VALUE)
        saved.putInt(SelectionState.KEY_SELECTED_ID, 999)
        val invalid = SelectionState.restore(saved, 3, ids)
        assertEquals(2, invalid.page)
        assertEquals(Contact.NO_ID, invalid.selectedId)
        saved.putInt(SelectionState.KEY_PAGE, -10)
        assertEquals(0, SelectionState.restore(saved, 3, ids).page)
        assertEquals(0, SelectionState.restore(saved, 0, emptySet()).page)
        assertEquals(Contact.NO_ID, SelectionState.restore(null, 3, ids).selectedId)
    }

    @Test
    fun lightAndDarkThemesRenderReadableSelectionAndLauncherNumberOne() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val previousMode = AppCompatDelegate.getDefaultNightMode()
        try {
            for ((name, mode) in listOf("light" to AppCompatDelegate.MODE_NIGHT_NO, "dark" to AppCompatDelegate.MODE_NIGHT_YES)) {
                instrumentation.runOnMainSync { AppCompatDelegate.setDefaultNightMode(mode) }
                ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                    onData(anything()).inAdapterView(withId(R.id.listContacts)).atPosition(0).perform(click())
                    scenario.onActivity { activity ->
                        val night = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        assertEquals(if (name == "dark") Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO, night)
                        assertTrue(androidx.core.graphics.ColorUtils.calculateContrast(activity.getColor(R.color.ink), activity.getColor(R.color.surface)) >= 4.5)
                        assertTrue(androidx.core.graphics.ColorUtils.calculateContrast(activity.getColor(R.color.ink), activity.getColor(R.color.row_selected)) >= 4.5)
                        val list = activity.findViewById<ListView>(R.id.listContacts)
                        assertEquals(activity.getColor(R.color.row_selected), (list.getChildAt(0).background as ColorDrawable).color)
                        val icon = checkNotNull(ContextCompat.getDrawable(activity, R.mipmap.ic_launcher))
                        val bitmap = Bitmap.createBitmap(216, 216, Bitmap.Config.ARGB_8888)
                        icon.setBounds(0, 0, 216, 216)
                        icon.draw(Canvas(bitmap))
                        File(activity.cacheDir, "launcher-$name.png").outputStream().use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        bitmap.recycle()
                    }
                    instrumentation.waitForIdleSync()
                    val screenshot = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
                    File(instrumentation.targetContext.cacheDir, "contacts-$name.png").outputStream().use {
                        screenshot.compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                    screenshot.recycle()
                }
            }
        } finally {
            instrumentation.runOnMainSync { AppCompatDelegate.setDefaultNightMode(previousMode) }
        }
    }

    private fun assertPage(scenario: ActivityScenario<MainActivity>, page: Int) {
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<ListView>(R.id.listContacts).adapter as ContactAdapter
            assertEquals(ContactRepository.sample().drop(page * 5).take(5), (0 until adapter.count).map(adapter::getItem))
            buttonIds.forEachIndexed { index, id ->
                val button = activity.findViewById<Button>(id)
                assertEquals(View.VISIBLE, button.visibility)
                assertEquals(index != page, button.isEnabled)
                assertEquals(if (index == page) 0.35f else 1f, button.alpha, 0.001f)
                assertEquals("Page ${index + 1}", button.contentDescription)
            }
        }
    }

    private val buttonIds = listOf(R.id.btnPage1, R.id.btnPage2, R.id.btnPage3)
}
