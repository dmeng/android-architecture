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
package com.example.android.architecture.blueprints.todoapp.statistics

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ViewModelFactory
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for the statistics screen.
 */
@RunWith(RobolectricTestRunner::class)
class StatisticsScreenTest {

    private lateinit var activityScenario : ActivityScenario<StatisticsActivity>

    @Before
    fun setup() {
        ViewModelFactory.destroyInstance()
        // Given some tasks
        val tasksRepository =
            Injection.provideTasksRepository(ApplicationProvider.getApplicationContext())
        val task1 = Task("Title1").apply { isCompleted = false }
        val task2 = Task("Title2").apply { isCompleted = true }
        tasksRepository.run {
            deleteAllTasks()
            saveTask(task1)
            saveTask(task2)
        }
        activityScenario = ActivityScenario.launch(StatisticsActivity::class.java)
    }

    @Test
    fun tasks_showsNonEmptyMessage() {
        val expectedActiveTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_active_tasks, 1)
        val expectedCompletedTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_completed_tasks, 1)
        activityScenario.onActivity {
            Assert.assertTrue(it.findViewById<TextView>(R.id.stats_active_text).isShown)
            Assert.assertTrue(it.findViewById<TextView>(R.id.stats_active_text).text.contains(expectedActiveTaskText))
            Assert.assertTrue(it.findViewById<TextView>(R.id.stats_completed_text).isShown)
            Assert.assertTrue(it.findViewById<TextView>(R.id.stats_completed_text).text.contains(expectedCompletedTaskText))
        }
    }
}
