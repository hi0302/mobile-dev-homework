package com.example.ic03

import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.os.SystemClock
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryActivityTest {
    @Test
    fun galleryInflatesFramesAndShowsFirstImage() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForPreview(scenario)
            scenario.onActivity { activity ->
                val group = activity.findViewById<LinearLayout>(R.id.viewgroup)
                val scroller = activity.findViewById<HorizontalScrollView>(R.id.thumbnailScroller)
                assertEquals(1, scroller.childCount)
                assertEquals(10, group.childCount)
                assertEquals(10, (0 until group.childCount).map { group.getChildAt(it).id }.toSet().size)
                assertTrue(group.getChildAt(0).isSelected)
                assertFalse(activity.findViewById<ImageButton>(R.id.previousPhoto).isEnabled)
                assertNotNull(activity.findViewById<ImageView>(R.id.imageSelected).drawable)
            }
            onView(withId(R.id.txtMsg)).check(matches(withText("Photo-1 · Selected position: 0")))
            onView(withId(R.id.selectionCount)).check(matches(withText("01 / 10")))
        }
    }

    @Test
    fun tapThumbnailUpdatesPreviewAndOnlyOneFrameIsSelected() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForPreview(scenario)
            var frameId = 0
            var initialDrawable: Any? = null
            scenario.onActivity { activity ->
                frameId = activity.findViewById<LinearLayout>(R.id.viewgroup).getChildAt(5).id
                initialDrawable = activity.findViewById<ImageView>(R.id.imageSelected).drawable.constantState
            }
            onView(withId(frameId)).perform(scrollTo(), click())
            waitForPreview(scenario)
            onView(withId(R.id.txtMsg)).check(matches(withText("Photo-6 · Selected position: 5")))
            onView(withId(R.id.photoTitle)).check(matches(withText("Hidden falls")))
            scenario.onActivity { activity ->
                val group = activity.findViewById<LinearLayout>(R.id.viewgroup)
                assertEquals(listOf(5), (0 until group.childCount).filter { group.getChildAt(it).isSelected })
                val preview = activity.findViewById<ImageView>(R.id.imageSelected)
                assertTrue(preview.contentDescription.contains("Photo-6"))
                assertNotNull(preview.drawable)
                assertFalse(initialDrawable === preview.drawable.constantState)
            }
        }
    }

    @Test
    fun selectionSurvivesRecreationAndSelectedThumbnailIsVisible() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            var frameId = 0
            scenario.onActivity { activity ->
                frameId = activity.findViewById<LinearLayout>(R.id.viewgroup).getChildAt(9).id
            }
            onView(withId(frameId)).perform(scrollTo(), click())
            scenario.recreate()
            waitForPreview(scenario)
            onView(withId(R.id.txtMsg)).check(matches(withText("Photo-10 · Selected position: 9")))
            onView(withId(R.id.selectionCount)).check(matches(withText("10 / 10")))
            scenario.onActivity { activity ->
                val group = activity.findViewById<LinearLayout>(R.id.viewgroup)
                assertTrue(group.getChildAt(9).isSelected)
                for (position in 0 until group.childCount) {
                    assertEquals(
                        if (position == 9) View.VISIBLE else View.GONE,
                        group.getChildAt(position).findViewById<ImageView>(R.id.selectionCheck).visibility
                    )
                }
                assertTrue(activity.findViewById<HorizontalScrollView>(R.id.thumbnailScroller).scrollX > 0)
                assertFalse(activity.findViewById<ImageButton>(R.id.nextPhoto).isEnabled)
            }
        }
    }

    @Test
    fun previousAndNextStayWithinCollectionBounds() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.nextPhoto)).perform(scrollTo(), click())
            onView(withId(R.id.txtMsg)).check(matches(withText("Photo-2 · Selected position: 1")))
            onView(withId(R.id.previousPhoto)).perform(click())
            onView(withId(R.id.txtMsg)).check(matches(withText("Photo-1 · Selected position: 0")))
            scenario.onActivity { activity ->
                assertFalse(activity.findViewById<ImageButton>(R.id.previousPhoto).isEnabled)
                assertTrue(activity.findViewById<ImageButton>(R.id.nextPhoto).isEnabled)
                repeat(9) { activity.findViewById<ImageButton>(R.id.nextPhoto).performClick() }
                assertFalse(activity.findViewById<ImageButton>(R.id.nextPhoto).isEnabled)
                assertEquals("Photo-10 · Selected position: 9", activity.findViewById<TextView>(R.id.txtMsg).text)
            }
        }
    }

    @Test
    fun stockPhotosHaveRealPairsAndShowPhotographerCredit() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            var frameId = 0
            scenario.onActivity { activity ->
                val photos = PhotoCatalog.load(activity)
                for (position in photos.indices) {
                    val photo = photos[position]
                    assertTrue(photo.hasLargePhoto)
                    assertFalse(photo.hasThumbnailOnly)
                    assertEquals("small_photo_${photo.slot}", activity.resources.getResourceEntryName(photo.thumbnail))
                    assertEquals("large_photo_${photo.slot}", activity.resources.getResourceEntryName(photo.largeImage))
                }
                frameId = activity.findViewById<LinearLayout>(R.id.viewgroup).getChildAt(0).id
            }
            onView(withId(frameId)).perform(scrollTo(), click())
            waitForPreview(scenario)
            onView(withId(R.id.photoTitle)).check(matches(withText("Beyond the pines")))
            onView(withId(R.id.selectionCount)).check(matches(withText("01 / 10")))
            onView(withId(R.id.photoNote)).check(matches(withText("Photo by Paul Jarvis · Unsplash")))
        }
    }

    // Raster photos load off the UI thread; also keep these tests useful after photos are added.
    private fun waitForPreview(scenario: ActivityScenario<MainActivity>) {
        val deadline = SystemClock.uptimeMillis() + 5000
        while (SystemClock.uptimeMillis() < deadline) {
            var ready = false
            scenario.onActivity { activity ->
                ready = activity.findViewById<ImageView>(R.id.imageSelected).drawable != null
            }
            if (ready) return
            SystemClock.sleep(50)
        }
        throw AssertionError("Selected preview did not finish loading")
    }
}
