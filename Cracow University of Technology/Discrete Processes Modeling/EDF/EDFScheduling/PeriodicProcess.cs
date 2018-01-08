using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media;

namespace EDFScheduling
{
   public class PeriodicProcess
    {
        static public int ProcessCount = 0;

        public Color color;
        
        public int Number;
        public int Period;
        public int ExecutionTime;
        public bool CanProcess;
        public int DeadlineStep;

        public PeriodicProcess(int _executionTime, int _period)
        {
            Number = ProcessCount;
            CanProcess = true;
            ExecutionTime = _executionTime;
            Period = _period;
            DeadlineStep = Period;

            color = GenerateRndColor();

            ProcessCount = ProcessCount + 1;
        }

        public void NextDeadline()
        {
            DeadlineStep = DeadlineStep + Period;
        }

        Color GenerateRndColor()
        {
            Random r = new Random(Number*3);
            Color randomColor = new Color();
            randomColor.A = 255; //alpha channel of the color
            randomColor.R = (byte)r.Next(0, 255); //red channel
            randomColor.G = (byte)r.Next(0, 255); //green channel
            randomColor.B = (byte)r.Next(0, 255); //blue channel

            return randomColor;
        }

        //~PeriodicProcess()
        //{
        //    ProcessCount = ProcessCount - 1;
        //}
    }
}
