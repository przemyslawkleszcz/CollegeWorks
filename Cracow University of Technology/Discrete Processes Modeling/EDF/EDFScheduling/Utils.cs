using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EDFScheduling
{
    public class Utils
    {
        public static float CalculateUtilization(IEnumerable<SingleProcess> processes)
        {
            var total = processes.Sum(t => (float) t.ExecutionTime / t.Period);
            return total * 100f;
        }

        /// <summary>
        /// Calculates Least Common Multiple
        /// </summary>
        /// <param name="processes"></param>
        /// <returns></returns>
        public static int CalculateLCM(List<PeriodicProcess> processes)
        {
            var lcm = 1;

            foreach (var t in processes)
            {
                var currentPeriod = t.Period;
                var gcd = CalculateGCD(lcm, currentPeriod);
                lcm = lcm * currentPeriod / gcd;
            }

            return lcm;
        }

        private static int CalculateGCD(int a, int b)
        {
            return b == 0 ? a : CalculateGCD(b, a % b);
        }
    }
}
