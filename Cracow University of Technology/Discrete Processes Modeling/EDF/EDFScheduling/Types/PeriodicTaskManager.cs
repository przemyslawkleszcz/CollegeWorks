using System;
using System.Collections.Generic;

namespace EDFScheduling.Types
{
    public class PeriodicTaskManager
    {
        private readonly UiManager _uiMgr;
        private readonly List<PeriodicTask> _processes;
        private readonly int _leastCommonMultiple;
        private int _cpRemaining; //Current Process Remaining Steps
        private PeriodicTask _currentProcess;
        

        public PeriodicTaskManager(List<PeriodicTask> processes)
        {
            _processes = processes;
            var utilization = Utils.CalculateUtilization(_processes);

            _leastCommonMultiple = Utils.CalculateLcm(_processes);

            _uiMgr = new UiManager(this._processes.Count, _leastCommonMultiple); //Initializing UI Manager 
            _uiMgr.AddToUi(_processes, utilization);
        }

        public void ScheduleTasks()
        {
            var step = 0;
            int processIndex;
            var processQueue = new List<PeriodicTask>();

            while (step < _leastCommonMultiple)
            {

                if (_cpRemaining == 1) //Current Process has been finished
                    _currentProcess = null;

                if (_currentProcess != null) //If there is a process running, Decrease remaining steps
                    _cpRemaining -= 1;

                else // If it is free, run a process
                {
                    foreach (var process in _processes)
                    {
                        if (step >= process.DeadlineStep)
                        {
                            process.CanProcess = true;
                            process.NextDeadline();
                        }

                        if (process.CanProcess)
                            processQueue.Add(process);
                    }
                    if (processQueue.Count > 0)
                    {
                        processIndex = PickProcess(processQueue);
                        _currentProcess = _processes.Find(o => o.Number == processQueue[processIndex].Number);
                        _cpRemaining = RunProcess(_currentProcess);
                        Console.WriteLine("Index: " + (_currentProcess.Number + 1) + " on step " + step);
                    }
                    else
                        Console.WriteLine("No process executed on step " + step);
                }
                
                if (_currentProcess != null)
                    _uiMgr.DrawRect(_currentProcess.Number, _currentProcess.Color, step);

                _uiMgr.DrawStepLabel(step);

                ++step;
                processQueue.Clear();
            }
        }

        int PickProcess(List<PeriodicTask> processQueue)
        {
            var minPeriod = processQueue[0].Period;
            var minIndex = 0;
            for (var i = 0; i < processQueue.Count; i++)
            {
                if (processQueue[i].Period < minPeriod)
                {
                    minPeriod = processQueue[i].Period;
                    minIndex = i;
                }
            }

            return minIndex;
        }

        int RunProcess(PeriodicTask process)
        {
            process.CanProcess = false;
            return process.ExecutionTime;
        }
    }
}

