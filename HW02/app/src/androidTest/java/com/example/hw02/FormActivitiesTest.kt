package com.example.hw02

import android.content.Intent
import android.graphics.Bitmap
import android.os.SystemClock
import android.text.method.PasswordTransformationMethod
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FormActivitiesTest {
    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun resultDisplaysBundleAndKeepsPasswordMaskedAfterRecreation() {
        val intent = Intent(context, resultform::class.java).apply {
            putExtra("username", "DangKhoa")
            putExtra("password", "Secret123")
            putExtra("birthdate", "20/10/1989")
            putExtra("gender", "Male")
            putExtra("hobbies", "Tennis, Futbal")
        }
        ActivityScenario.launch<resultform>(intent).use { scenario ->
            fun checkDetails() {
                onView(withId(R.id.txtUsername)).check(matches(withText("DangKhoa")))
                onView(withId(R.id.txtPassword)).check(matches(withText("*********")))
                onView(withId(R.id.txtBirthdate)).check(matches(withText("20/10/1989")))
                onView(withId(R.id.txtGender)).check(matches(withText("Male")))
                onView(withId(R.id.txtHobbies)).check(matches(withText("Tennis, Futbal")))
            }
            checkDetails()
            captureScreenshot("resultform")
            scenario.recreate()
            checkDetails()
        }
    }

    @Test
    fun resultShowsNoneForEmptyOrWhitespaceHobbies() {
        for (hobbies in listOf("", "   ")) {
            val intent = Intent(context, resultform::class.java).putExtra("hobbies", hobbies)
            ActivityScenario.launch<resultform>(intent).use {
                onView(withId(R.id.txtHobbies)).check(matches(withText("None")))
            }
        }
    }

    @Test
    fun resultHandlesMissingBundle() {
        ActivityScenario.launch(resultform::class.java).use {
            onView(withId(R.id.txtUsername)).check(matches(withText("—")))
            onView(withId(R.id.txtPassword)).check(matches(withText("—")))
            onView(withId(R.id.txtBirthdate)).check(matches(withText("—")))
            onView(withId(R.id.txtGender)).check(matches(withText("—")))
            onView(withId(R.id.txtHobbies)).check(matches(withText("None")))
        }
    }

    @Test
    fun resultKeepsLongValuesIntact() {
        val username = "Khoa".repeat(50)
        val hobbies = "Tennis, Futbal, Others, ".repeat(20)
        val intent = Intent(context, resultform::class.java).apply {
            putExtra("username", username)
            putExtra("hobbies", hobbies)
        }
        ActivityScenario.launch<resultform>(intent).use { scenario ->
            scenario.onActivity { activity ->
                assertEquals(username, activity.findViewById<TextView>(R.id.txtUsername).text.toString())
                assertEquals(hobbies, activity.findViewById<TextView>(R.id.txtHobbies).text.toString())
            }
            onView(withId(R.id.btnExit)).perform(scrollTo()).check(matches(isDisplayed()))
        }
    }

    @Test
    fun resetClearsEveryFieldAndSelectionAndFocusesUsername() {
        ActivityScenario.launch(registerform::class.java).use { scenario ->
            captureScreenshot("registerform")
            scenario.onActivity { activity ->
                activity.edtUsername.setText("DiemThuy")
                activity.edtPassword.setText("Secret123")
                activity.edtRetype.setText("Secret123")
                activity.edtBirthdate.setText("05/03/2001")
                activity.rgGender.check(R.id.rbFemale)
                activity.cbTennis.isChecked = true
                activity.cbFutbal.isChecked = true
                activity.cbOthers.isChecked = true
                assertTrue(activity.edtPassword.transformationMethod is PasswordTransformationMethod)
                assertTrue(activity.edtRetype.transformationMethod is PasswordTransformationMethod)
            }
            onView(withId(R.id.btnReset)).perform(scrollTo())
            captureScreenshot("registerform-options")
            onView(withId(R.id.btnReset)).perform(click())
            scenario.onActivity { activity ->
                listOf(activity.edtUsername, activity.edtPassword, activity.edtRetype, activity.edtBirthdate)
                    .forEach { assertEquals("", it.text.toString()) }
                assertEquals(-1, activity.rgGender.checkedRadioButtonId)
                assertFalse(activity.cbTennis.isChecked)
                assertFalse(activity.cbFutbal.isChecked)
                assertFalse(activity.cbOthers.isChecked)
                assertTrue(activity.edtUsername.hasFocus())
            }
        }
    }

    @Test
    fun exitFinishesRegistrationAndResultActivitiesTogether() {
        ActivityScenario.launch(registerform::class.java).use { scenario ->
            val destroyed = CountDownLatch(1)
            scenario.onActivity { activity ->
                activity.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_DESTROY) destroyed.countDown()
                })
                activity.edtUsername.setText("DangKhoa")
                activity.edtPassword.setText("Secret123")
                activity.edtRetype.setText("Secret123")
                activity.edtBirthdate.setText("12/12/1989")
                activity.rgGender.check(R.id.rbFemale)
                activity.cbTennis.isChecked = true
                activity.cbFutbal.isChecked = true
                activity.cbOthers.isChecked = true
            }
            onView(withId(R.id.btnSignUp)).perform(scrollTo(), click())
            onView(withId(R.id.txtUsername)).check(matches(withText("DangKhoa")))
            onView(withId(R.id.txtPassword)).check(matches(withText("*********")))
            onView(withId(R.id.txtBirthdate)).check(matches(withText("12/12/1989")))
            onView(withId(R.id.txtGender)).check(matches(withText("Female")))
            onView(withId(R.id.txtHobbies)).check(matches(withText("Tennis, Futbal, Others")))
            captureScreenshot("signup-resultform")
            onView(withId(R.id.btnExit)).perform(scrollTo(), click())
            assertTrue("Exit should destroy the registration activity", destroyed.await(5, TimeUnit.SECONDS))
            assertEquals(Lifecycle.State.DESTROYED, scenario.state)
        }
    }

    @Test
    fun signupSendsMaleGenderAndShowsNoneWithoutHobbies() {
        ActivityScenario.launch(registerform::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.edtUsername.setText("DangKhoa")
                activity.edtPassword.setText("Secret123")
                activity.edtRetype.setText("Secret123")
                activity.edtBirthdate.setText("12/12/1989")
                activity.rgGender.check(R.id.rbMale)
            }
            onView(withId(R.id.btnSignUp)).perform(scrollTo(), click())
            onView(withId(R.id.txtUsername)).check(matches(withText("DangKhoa")))
            onView(withId(R.id.txtGender)).check(matches(withText("Male")))
            onView(withId(R.id.txtHobbies)).check(matches(withText("None")))
            onView(withId(R.id.btnExit)).perform(scrollTo(), click())
        }
    }

    // Optional review artifacts, enabled with -Pandroid.testInstrumentationRunnerArguments.captureScreenshots=true.
    private fun captureScreenshot(name: String) {
        if (InstrumentationRegistry.getArguments().getString("captureScreenshots") != "true") return
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        // Allow pending scroll/draw frames to appear in this optional screenshot.
        SystemClock.sleep(300)
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(context.cacheDir, "$name.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }
}
