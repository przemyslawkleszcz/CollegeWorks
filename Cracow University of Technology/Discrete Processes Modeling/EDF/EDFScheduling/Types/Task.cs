namespace EDFScheduling.Types
{
    public class Task
    {
        public int ExecutionTime;
        public int Period;

        public Task() {}

        public Task(int executionTime, int period)
        {
            ExecutionTime = executionTime;
            Period = period;
        }
    }
}
