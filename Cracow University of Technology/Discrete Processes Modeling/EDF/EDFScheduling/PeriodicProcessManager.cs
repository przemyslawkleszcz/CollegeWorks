using System;
using System.Collections.Generic;

namespace EDFScheduling
{
    class PeriodicProcessManager
    {
        private readonly UIManager _uiMgr;
        private readonly List<PeriodicProcess> _processes;
        private readonly int _leastCommonMultiple;
        private int _cpRemaining; //Current Process Remaining Steps
        private PeriodicProcess _currentProcess;
        

        public PeriodicProcessManager(List<PeriodicProcess> processes)
        {
            _processes = processes;
            var utilization = Utils.CalculateUtilization(_processes);

            //if (Utilization > 100)
            //{
            //    throw new ArgumentException(String.Format("The system cannot be scheduled with EDF. {0}% > 100%", Utilization), "Utilization");
            //}

            _leastCommonMultiple = Utils.CalculateLCM(_processes);

            _uiMgr = new UIManager(this._processes.Count, _leastCommonMultiple); //Initializing UI Manager 
            _uiMgr.AddToUI(_processes, utilization);
        }

        public void ScheduleTasks()
        {
            var step = 0;
            int processIndex;
            var processQueue = new List<PeriodicProcess>();

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

        int PickProcess(List<PeriodicProcess> processQueue)
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

        int RunProcess(PeriodicProcess _process)
        {
            _process.CanProcess = false;
            return _process.ExecutionTime;
        }
    }
}

