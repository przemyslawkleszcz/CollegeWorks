using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EDFScheduling
{
    class PeriodicProcessManager
    {
        UIManager UIMgr;

        List<PeriodicProcess> Processes;

        int LeastCommonMultiple;
        float Utilization;

        PeriodicProcess CurrentProcess = null;
        int CPRemaining = 0; //Current Process Remaining Steps

        public PeriodicProcessManager(List<PeriodicProcess> _processes)
        {
            Processes = _processes;
            Utilization = CalculateUtilization();

            //if (Utilization > 100)
            //{
            //    throw new ArgumentException(String.Format("The system cannot be scheduled with EDF. {0}% > 100%", Utilization), "Utilization");
            //}

            LeastCommonMultiple = CalculateLCM();   //Calculate Least Common Multiple

            UIMgr = new UIManager(Processes.Count, LeastCommonMultiple, Utilization); //Initializing UI Manager 
            UIMgr.AddToUI(Processes, Utilization);
        }

        public void ScheduleTasks()
        {
            int step = 0;
            int ProcessIndex = 0;
            List<PeriodicProcess> ProcessQueue = new List<PeriodicProcess>();

            while (step < LeastCommonMultiple)
            {

                if (CPRemaining == 1) //Current Process has been finished
                {
                    CurrentProcess = null;
                }

                if (CurrentProcess != null) //If there is a process running, Decrease remaining steps
                {
                    CPRemaining -= 1;
                }

                else // If it is free, run a process
                {
                    for (int i = 0; i < Processes.Count; i++)
                    {
                        if (step >= Processes[i].DeadlineStep)
                        {
                            Processes[i].CanProcess = true;
                            Processes[i].NextDeadline();
                        }

                        if (Processes[i].CanProcess)
                        {
                            ProcessQueue.Add(Processes[i]);
                        }
                    }
                    if (ProcessQueue.Count > 0)
                    {
                        ProcessIndex = PickProcess(ProcessQueue);
                        CurrentProcess = Processes.Find(o => o.Number == ProcessQueue[ProcessIndex].Number);
                        CPRemaining = RunProcess(CurrentProcess);
                        Console.WriteLine("Index: " + (CurrentProcess.Number + 1) + " on step " + step);
                    }
                    else
                    {
                        Console.WriteLine("No process executed on step " + step);
                    }
                }
                
                if (CurrentProcess != null)
                    UIMgr.DrawRect(CurrentProcess.Number, CurrentProcess.color, step);

                UIMgr.DrawStepLabel(step);

                ++step;
                ProcessQueue.Clear();
            }
        }

        int PickProcess(List<PeriodicProcess> processQueue)
        {
            int minPeriod = processQueue[0].Period;
            int minIndex = 0;
            for (int i = 0; i < processQueue.Count; i++)
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


        //  Helper Functions

        private float CalculateUtilization()
        {
            float total = 0;

            for (int i = 0; i < Processes.Count; i++)
            {
                total += (float)Processes[i].ExecutionTime / Processes[i].Period;
            }
            return total * 100f;
        }

        private int CalculateLCM()
        {
            int GCD = 1;
            int LCM = 1;
            int currentPeriod;
            for (int i = 0; i < Processes.Count; i++)
            {
                currentPeriod = Processes[i].Period;

                GCD = CalculateGCD(LCM, currentPeriod);
                LCM = LCM * currentPeriod / GCD;
            }

            return LCM;
        }

        static private int CalculateGCD(int a, int b)
        {
            return b == 0 ? a : CalculateGCD(b, a % b);
        }

    }
}

