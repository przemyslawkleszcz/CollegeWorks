using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace EDFScheduling
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public readonly List<SingleProcess> _burstPeriodTimes;
        private readonly List<PeriodicProcess> _processes;

        public MainWindow(List<SingleProcess> bpt)
        {
            InitializeComponent();

            _burstPeriodTimes = bpt;
            _processes = new List<PeriodicProcess>();

            Application.Current.MainWindow = this;
        }

        //  This is initiated when the window is loaded
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            GenerateAndRun();
            Console.WriteLine((MainWindow)Application.Current.MainWindow);
        }

        public void GenerateAndRun()
        {
            foreach (var t in _burstPeriodTimes)
            {
                var pp = new PeriodicProcess(t.ExecutionTime, t.Period);
                _processes.Add(pp);
            }

            //  Generating the process manager, giving the list as the parameter

            try
            {
                var processMgr = new PeriodicProcessManager(_processes);
                processMgr.ScheduleTasks();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }

        // Editing the Dropdown and the Utilization Label
        public void AddInfoToWindow(List<PeriodicProcess> _p, float Utilization)
        {
            var uiClass = new UIBindClass();
            DataContext = uiClass;
            uiClass.cbItems = new ObservableCollection<ComboBoxItem>();

            var cbItem = new ComboBoxItem { IsEnabled = false, Content = "PN\tBT\tPT", Background = new SolidColorBrush(Color.FromRgb(0, 0, 0)) };

            uiClass.SelectedcbItem = cbItem;
            uiClass.cbItems.Add(cbItem);
            foreach (var t in _p)
            {
                cbItem = new ComboBoxItem { IsHitTestVisible = false, Content = "P" + (t.Number + 1) + "\t" + t.ExecutionTime + "\t" + (t.Period), Background = new SolidColorBrush(t.Color) };
                uiClass.cbItems.Add(cbItem);
            }

            uiClass.LblUtil = new Label {Content = "Utilization: " + Utilization.ToString("0.00") + "%"};
        }
    }
}
