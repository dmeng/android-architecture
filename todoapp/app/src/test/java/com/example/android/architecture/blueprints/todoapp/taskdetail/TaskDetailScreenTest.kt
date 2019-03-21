/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for the tasks screen, the main screen which contains a list of all tasks.
 */
@RunWith(RobolectricTestRunner::class)
class TaskDetailScreenTest {

    private lateinit var activityScenario : ActivityScenario<TaskDetailActivity>

    /**
     * Setup your test fixture with a fake task id. The [TaskDetailActivity] is started with
     * a particular task id, which is then loaded from the service API.
     *
     *
     * Note that this test runs hermetically and is fully isolated using a fake implementation of
     * the service API. This is a great way to make your tests more reliable and faster at the same
     * time, since they are isolated from any outside dependencies.
     */
    private fun startActivityWithWithStubbedTask(task: Task) {
        // Add a task stub to the fake service api layer.
        Injection.provideTasksRepository(ApplicationProvider.getApplicationContext()).apply {
            deleteAllTasks()
        }
        FakeTasksRemoteDataSource.addTasks(task)

        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        val startIntent = Intent(ApplicationProvider.getApplicationContext(),
                TaskDetailActivity::class.java).apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
        }

        activityScenario = ActivityScenario.launch(startIntent)
    }

    @Test
    fun activeTaskDetails_DisplayedInUi() {
        startActivityWithWithStubbedTask(ACTIVE_TASK)
        activityScenario.onActivity {
            Assert.assertEquals(TASK_TITLE, it.findViewById<TextView>(R.id.task_detail_title)?.text)
            Assert.assertEquals(TASK_DESCRIPTION, it.findViewById<TextView>(R.id.task_detail_description)?.text)

            // this line makes me feel a little dirty, like there's a more idiomatic way to check for it
            Assert.assertFalse(it.findViewById<CheckBox>(R.id.task_detail_complete)?.isChecked ?: true)
        }
    }

    @Test
    fun completedTaskDetails_DisplayedInUi() {
        startActivityWithWithStubbedTask(COMPLETED_TASK)
        activityScenario.onActivity {
            Assert.assertEquals(TASK_TITLE, it.findViewById<TextView>(R.id.task_detail_title)?.text)
            Assert.assertEquals(TASK_DESCRIPTION, it.findViewById<TextView>(R.id.task_detail_description)?.text)
            Assert.assertTrue(it.findViewById<CheckBox>(R.id.task_detail_complete)?.isChecked ?: false)
        }
    }

    @Test
    fun orientationChange_menuAndTaskPersist() {
        startActivityWithWithStubbedTask(ACTIVE_TASK)
        activityScenario.onActivity {
            Assert.assertEquals(TASK_TITLE, it.findViewById<TextView>(R.id.task_detail_title)?.text)
            Assert.assertEquals(TASK_DESCRIPTION, it.findViewById<TextView>(R.id.task_detail_description)?.text)
//            Assert.assertTrue(it.findViewById<MenuItem>(R.id.menu_delete)?.isShown ?: false)

          when (it.resources.configuration.orientation) {
              Configuration.ORIENTATION_LANDSCAPE
                  -> it.resources.configuration.orientation = Configuration.ORIENTATION_PORTRAIT
              Configuration.ORIENTATION_PORTRAIT
                  -> it.resources.configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
              else
                  -> it.resources.configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
          }

          Assert.assertEquals(TASK_TITLE, it.findViewById<TextView>(R.id.task_detail_title)?.text)
          Assert.assertEquals(TASK_DESCRIPTION, it.findViewById<TextView>(R.id.task_detail_description)?.text)
//            Assert.assertTrue(it.findViewById<MenuItem>(R.id.menu_delete)?.isShown ?: false)
        }
    }

    companion object {

        private val TASK_TITLE = "ATSL"

        private val TASK_DESCRIPTION = "Rocks"

        /**
         * [Task] stub that is added to the fake service API layer.
         */
        private var ACTIVE_TASK = Task(TASK_TITLE, TASK_DESCRIPTION).apply {
            isCompleted = false
        }

        /**
         * [Task] stub that is added to the fake service API layer.
         */
        private var COMPLETED_TASK = Task(TASK_TITLE, TASK_DESCRIPTION).apply {
            isCompleted = true
        }
    }
}
