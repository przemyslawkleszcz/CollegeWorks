using System.Collections.Generic;
using System.Linq;

namespace EDFScheduling.Types
{
    public class Utils
    {
        public static float CalculateUtilization(IEnumerable<Task> tasks)
        {
            var total = tasks.Sum(t => (float) t.ExecutionTime / t.Period);
            return total * 100f;
        }

        /// <summary>
        /// Calculates Least Common Multiple
        /// </summary>
        /// <param name="tasks"></param>
        /// <returns></returns>
        public static int CalculateLcm(List<PeriodicTask> tasks)
        {
            var lcm = 1;

            foreach (var t in tasks)
            {
                var currentPeriod = t.Period;
                var gcd = CalculateGcd(lcm, currentPeriod);
                lcm = lcm * currentPeriod / gcd;
            }

            return lcm;
        }

        private static int CalculateGcd(int a, int b)
        {
            return b == 0 
                ? a 
                : CalculateGcd(b, a % b);
        }
    }
}
