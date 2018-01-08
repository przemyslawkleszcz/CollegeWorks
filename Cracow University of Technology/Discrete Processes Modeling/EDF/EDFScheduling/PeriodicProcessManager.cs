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
            this._processes = processes;
            var utilization = Utils.CalculateUtilization(_processes);

            //if (Utilization > 100)
            //{
            //    throw new ArgumentException(String.Format("The system cannot be scheduled with EDF. {0}% > 100%", Utilization), "Utilization");
            //}

            _leastCommonMultiple = Utils.CalculateLCM(_processes);

            _uiMgr = new UIManager(this._processes.Count, _leastCommonMultiple, utilization); //Initializing UI Manager 
            _uiMgr.AddToUI(this._processes, utilization);
        }

        public void ScheduleTasks()
        {
            int step = 0;
            int ProcessIndex = 0;
            List<PeriodicProcess> ProcessQueue = new List<PeriodicProcess>();

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
                            ProcessQueue.Add(process);
                    }
                    if (ProcessQueue.Count > 0)
                    {
                        ProcessIndex = PickProcess(ProcessQueue);
                        _currentProcess = _processes.Find(o => o.Number == ProcessQueue[ProcessIndex].Number);
                        _cpRemaining = RunProcess(_currentProcess);
                        Console.WriteLine("Index: " + (_currentProcess.Number + 1) + " on step " + step);
                    }
                    else
                        Console.WriteLine("No process executed on step " + step);
                }
                
                if (_currentProcess != null)
                    _uiMgr.DrawRect(_currentProcess.Number, _currentProcess.color, step);

                _uiMgr.DrawStepLabel(step);

                ++step;
                ProcessQueue.Clear();
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

