using System;
using System.Windows.Media;

namespace EDFScheduling.Types
{
    public class PeriodicTask : Task
    {
        public static int ProcessCount;
        public Color Color;
        public int Number;
        public bool CanProcess;
        public int DeadlineStep;

        public PeriodicTask(int executionTime, int period)
        {
            Number = ProcessCount;
            CanProcess = true;
            ExecutionTime = executionTime;
            Period = period;
            DeadlineStep = Period;
            Color = GenerateRndColor();
            ProcessCount = ProcessCount + 1;
        }

        public void NextDeadline()
        {
            DeadlineStep = DeadlineStep + Period;
        }

        Color GenerateRndColor()
        {
            var r = new Random(Number * 3);
            var randomColor = new Color
            {
                A = 255, //alpha channel of the color
                R = (byte)r.Next(0, 255),
                G = (byte)r.Next(0, 255),
                B = (byte)r.Next(0, 255)
            };

            return randomColor;
        }
    }
}
