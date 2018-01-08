using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace EDFScheduling
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        List<PeriodicProcess> processes;
        List<SingleProcess> BurstPeriodTimes;

        // Main Window constructor

        public MainWindow(List<SingleProcess> bpt)
        {
            InitializeComponent();

            BurstPeriodTimes = bpt;
            processes = new List<PeriodicProcess>();

            System.Windows.Application.Current.MainWindow = this;
        }

        //  This is initiated when the window is loaded
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            GenerateAndRun();
            Console.WriteLine((MainWindow)System.Windows.Application.Current.MainWindow);

        }

        public void GenerateAndRun()
        {
            for (int i = 0; i < BurstPeriodTimes.Count; i++)
            {
                PeriodicProcess pp = new PeriodicProcess(BurstPeriodTimes[i].ExecutionTime, BurstPeriodTimes[i].Period);
                processes.Add(pp);
            }

            //  Generating the process manager, giving the list as the parameter
            try
            {
                PeriodicProcessManager ProcessMgr = new PeriodicProcessManager(processes);
                ProcessMgr.ScheduleTasks();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }

        // Editing the Dropdown and the Utilization Label
        public void AddInfoToWindow(List<PeriodicProcess> _p, float Utilization)
        {
            UIBindClass uiClass = new UIBindClass();
            DataContext = uiClass;
            uiClass.cbItems = new ObservableCollection<ComboBoxItem>();

            var cbItem = new ComboBoxItem { IsEnabled = false, Content = "PN\tBT\tPT", Background = new SolidColorBrush(Color.FromRgb(0, 0, 0)) };

            uiClass.SelectedcbItem = cbItem;
            uiClass.cbItems.Add(cbItem);
            for (int i = 0; i < _p.Count; i++)
            {
                cbItem = new ComboBoxItem { IsHitTestVisible = false, Content = "P" + (_p[i].Number + 1) + "\t" + (_p[i].ExecutionTime) + "\t" + (_p[i].Period), Background = new SolidColorBrush(_p[i].color) };
                uiClass.cbItems.Add(cbItem);
            }
            uiClass.lblUtil = new Label();
            uiClass.lblUtil.Content = "Utilization: " + Utilization.ToString("0.00") + "%";
        }

    }
}
