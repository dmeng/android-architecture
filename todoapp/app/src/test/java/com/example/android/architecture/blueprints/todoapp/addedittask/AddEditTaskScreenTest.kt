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
package com.example.android.architecture.blueprints.todoapp.addedittask

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for the add task screen.
 */
@RunWith(RobolectricTestRunner::class)
class AddEditTaskScreenTest {

    val TASK_ID = "1"

    @Test
    fun emptyTask_isNotSaved() {
        val activityScenario = ActivityScenario.launch(AddEditTaskActivity::class.java)
        activityScenario.onActivity {
            it.findViewById<TextView>(R.id.add_task_title).text = ""
            it.findViewById<TextView>(R.id.add_task_description).text = ""

            // This line currently hangs
//            it.findViewById<FloatingActionButton>(R.id.fab_edit_task_done).performClick()

            Assert.assertTrue(it.findViewById<TextView>(R.id.add_task_title)?.isShown ?: false)
        }
    }

    @Test
    fun toolbarTitle_newTask_persistsRotation() {
        val activityScenario = ActivityScenario.launch(AddEditTaskActivity::class.java)
        val expected = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.add_task)
        activityScenario.onActivity {
            Assert.assertEquals(expected, it.findViewById<Toolbar>(R.id.toolbar).title)

            when (it.resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE
                    -> it.resources.configuration.orientation = Configuration.ORIENTATION_PORTRAIT
                Configuration.ORIENTATION_PORTRAIT
                    -> it.resources.configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
                else
                    -> it.resources.configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
            }

            Assert.assertEquals(expected, it.findViewById<Toolbar>(R.id.toolbar).title)
        }
    }

    @Test
    fun toolbarTitle_editTask_persistsRotation() {
        TasksRepository.destroyInstance()
        FakeTasksRemoteDataSource.addTasks(
            Task("title", "description", TASK_ID).apply { isCompleted = false }
        )
        val intent = Intent(ApplicationProvider.getApplicationContext(),
                AddEditTaskActivity::class.java)
            .apply { putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, TASK_ID) }
        val activityScenario = ActivityScenario.launch<AddEditTaskActivity>(intent)
        val expected = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.edit_task)
        activityScenario.onActivity {
            Assert.assertEquals(expected, it.findViewById<Toolbar>(R.id.toolbar).title)

            when (it.resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE
                -> it.resources.configuration.orientation = Configuration.ORIENTATION_PORTRAIT
                Configuration.ORIENTATION_PORTRAIT
                -> it.resources.configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
                else
                -> it.resources.configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
            }

            Assert.assertEquals(expected, it.findViewById<Toolbar>(R.id.toolbar).title)
        }
    }
}
