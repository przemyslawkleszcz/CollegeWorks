using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using EDFScheduling.Types;

namespace EDFScheduling.UI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private readonly List<Task> _tasks;
        private readonly List<PeriodicTask> _periodicTasks;

        public MainWindow(List<Task> tasks)
        {
            InitializeComponent();

            _tasks = tasks;
            _periodicTasks = new List<PeriodicTask>();

            Application.Current.MainWindow = this;
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            GenerateAndRun();
            Console.WriteLine((MainWindow)Application.Current.MainWindow);
        }

        public void GenerateAndRun()
        {
            foreach (var task in _tasks)
            {
                var pp = new PeriodicTask(task.ExecutionTime, task.Period);
                _periodicTasks.Add(pp);
            }

            try
            {
                var taskManager = new PeriodicTaskManager(_periodicTasks);
                taskManager.ScheduleTasks();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }

        public void AddInfoToWindow(List<PeriodicTask> _p, float utilization)
        {
            var uiContainer = new UiBindingContainer();
            DataContext = uiContainer;
            uiContainer.CbItems = new ObservableCollection<ComboBoxItem>();

            var item = new ComboBoxItem { IsEnabled = false, Content = "Zadanie\tWykonanie\tOkres", Background = new SolidColorBrush(Color.FromRgb(0, 0, 0)) };
            uiContainer.SelectedCbItem = item;
            uiContainer.CbItems.Add(item);

            foreach (var t in _p)
            {
                item = new ComboBoxItem { IsHitTestVisible = false, Content = "Z" + (t.Number + 1) + "\t" + t.ExecutionTime + "\t\t" + (t.Period), Background = new SolidColorBrush(t.Color) };
                uiContainer.CbItems.Add(item);
            }

            uiContainer.LblUtil = new Label {Content = "Utylizacja: " + utilization.ToString("0.00") + "%"};
        }
    }
}
