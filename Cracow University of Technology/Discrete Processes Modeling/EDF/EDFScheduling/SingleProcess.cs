using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EDFScheduling
{
    public class SingleProcess
    {
        public int ExecutionTime;
        public int Period;

        public SingleProcess() {}

        public SingleProcess(int executionTime, int period)
        {
            ExecutionTime = executionTime;
            Period = period;
        }
    }
}
