package com.mibaldi.fitapp.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class MainActivityTest:KoinTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)


    @Before
    fun setUp() {

    }

    @Test
    fun clickAMovieNavigatesToDetail() {
        activityTestRule.launchActivity(null)

        onView(withId(R.id.recycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                4,
                click()
            )
        )

    }
}