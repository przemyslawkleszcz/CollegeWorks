using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ZTBD.Types
{
    public class PerformanceResult
    {
        public PerformanceResult(int attempt, double time)
        {
            this.Attempt = attempt;
            this.Time = time;
        }
        public int Attempt
        {
            get;
            set;
        }
        public double Time
        {
            get;
            set;
        }
    }
}
