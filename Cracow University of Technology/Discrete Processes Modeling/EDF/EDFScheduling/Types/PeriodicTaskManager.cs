using System;
using System.Collections.Generic;

namespace EDFScheduling.Types
{
    public class PeriodicTaskManager
    {
        private readonly UiManager _uiMgr;
        private readonly List<PeriodicTask> _tasks;
        private readonly int _leastCommonMultiple;
        private int _cpRemaining; //Current Process Remaining Steps
        private PeriodicTask _currentTask;
        

        public PeriodicTaskManager(List<PeriodicTask> tasks)
        {
            _tasks = tasks;
            var utilization = Utils.CalculateUtilization(_tasks);

            _leastCommonMultiple = Utils.CalculateLcm(_tasks);

            _uiMgr = new UiManager(this._tasks.Count, _leastCommonMultiple); //Initializing UI Manager 
            _uiMgr.AddToUi(_tasks, utilization);
        }

        public void ScheduleTasks()
        {
            var step = 0;
            int taskIndex;
            var taskQueue = new List<PeriodicTask>();

            while (step < _leastCommonMultiple)
            {
                if (_cpRemaining == 1) //Current Process has been finished
                    _currentTask = null;

                if (_currentTask != null) //If there is a process running, Decrease remaining steps
                    _cpRemaining -= 1;

                else // If it is free, run a process
                {
                    foreach (var task in _tasks)
                    {
                        if (step >= task.DeadlineStep)
                        {
                            task.CanProcess = true;
                            task.NextDeadline();
                        }

                        if (task.CanProcess)
                            taskQueue.Add(task);
                    }
                    if (taskQueue.Count > 0)
                    {
                        taskIndex = GetMinPeriodTaskIndex(taskQueue);
                        _currentTask = _tasks.Find(o => o.Number == taskQueue[taskIndex].Number);
                        _cpRemaining = RunTask(_currentTask);
                        Console.WriteLine("Index: " + (_currentTask.Number + 1) + " on step " + step);
                    }
                    else
                        Console.WriteLine("No process executed on step " + step);
                }
                
                if (_currentTask != null)
                    _uiMgr.DrawRect(_currentTask.Number, _currentTask.Color, step);

                _uiMgr.DrawStepLabel(step);

                ++step;
                taskQueue.Clear();
            }
        }

        private int GetMinPeriodTaskIndex(List<PeriodicTask> taskQueue)
        {
            var minPeriod = taskQueue[0].Period;
            var minIndex = 0;
            for (var i = 0; i < taskQueue.Count; i++)
            {
                if (taskQueue[i].Period < minPeriod)
                {
                    minPeriod = taskQueue[i].Period;
                    minIndex = i;
                }
            }

            return minIndex;
        }

        int RunTask(PeriodicTask task)
        {
            task.CanProcess = false;
            return task.ExecutionTime;
        }
    }
}

