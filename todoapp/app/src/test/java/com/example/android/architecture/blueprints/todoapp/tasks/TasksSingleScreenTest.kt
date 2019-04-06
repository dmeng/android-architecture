package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

/**
 * Tests for the main tasks screen that do not involve having to navigate to another screen
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
class TasksSingleScreenTest {

  @Before
  fun clearTaskRepository() {
    // Add a task stub to the fake service api layer.
    Injection.provideTasksRepository(ApplicationProvider.getApplicationContext()).apply {
      deleteAllTasks()
    }
  }

  @Test
  fun displayActiveTask() {
    FakeTasksRemoteDataSource.addTasks(Task(TITLE1, DESCRIPTION1))

    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)

    onView(withText(TITLE1)).check(matches(isDisplayed()))

    viewActiveTasks()
    onView(withText(TITLE1)).check(matches(isDisplayed()))

    viewCompletedTasks()
    onView(withText(TITLE1)).check(matches(not(isDisplayed())))
  }

  @Test
  fun displayCompletedTask() {
    FakeTasksRemoteDataSource.addTasks(Task(TITLE1, DESCRIPTION1).apply {
      isCompleted = true
    })

    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)

    onView(withText(TITLE1)).check(matches(isDisplayed()))

    viewActiveTasks()
    onView(withText(TITLE1)).check(matches(not(isDisplayed())))

    viewCompletedTasks()
    onView(withText(TITLE1)).check(matches(isDisplayed()))
  }

  @Test
  fun deleteOneTest() {
    FakeTasksRemoteDataSource.addTasks(Task(TITLE1, DESCRIPTION1))

    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)

    // Open it in details view
    onView(withText(TITLE1)).perform(click())

    // Click delete task in menu
    onView(withId(R.id.menu_delete)).perform(click())

    // Verify it was deleted
    viewAllTasks()
    onView(withText(TITLE1)).check(matches(not(isDisplayed())))
  }

  @Test
  fun deleteOneOfTwoTests() {
    FakeTasksRemoteDataSource.addTasks(Task(TITLE1, DESCRIPTION1))
    FakeTasksRemoteDataSource.addTasks(Task(TITLE2, DESCRIPTION2))
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)

    // Open it in details view
    onView(withText(TITLE1)).perform(click())

    // Click delete task in menu
    onView(withId(R.id.menu_delete)).perform(click())

    // Verify it was deleted
    viewAllTasks()
    onView(withText(TITLE1)).check(doesNotExist())
    // but not the other one
    onView(withText(TITLE2)).check(matches(isDisplayed()))
  }

  private fun viewAllTasks() {
    onView(withId(R.id.menu_filter)).perform(click())
    onView(withText(R.string.nav_all)).perform(click())
  }

  private fun viewActiveTasks() {
    onView(withId(R.id.menu_filter)).perform(click())
    onView(withText(R.string.nav_active)).perform(click())
  }

  private fun viewCompletedTasks() {
    onView(withId(R.id.menu_filter)).perform(click())
    onView(withText(R.string.nav_completed)).perform(click())
  }

  companion object {

    const val TITLE1 = "TITLE1"
    const val TITLE2 = "TITLE2"

    const val DESCRIPTION1 = "DESCRIPTION1"
    const val DESCRIPTION2 = "DESCRIPTION2"
  }
}