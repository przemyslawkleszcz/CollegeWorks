using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace EDFScheduling
{
    /// <summary>
    /// Interaction logic for ProcessInput.xaml
    /// </summary>
    public partial class ProcessInput : Window
    {
        private readonly List<SingleProcess> _processes;

        public ProcessInput()
        {
            InitializeComponent();
            _processes = new List<SingleProcess>();
            var uiClass = new UIBindClass();
            DataContext = uiClass;
        }

        private void btnAdd_Click(object sender, RoutedEventArgs e)
        {
            var btStr = burstTime.Text;
            var ptStr = periodTime.Text;

            if (btStr.All(char.IsDigit) && !string.IsNullOrEmpty(btStr) && ptStr.All(char.IsDigit) && !string.IsNullOrEmpty(ptStr))
            {
                var bt = Convert.ToInt32(burstTime.Text);
                var pt = Convert.ToInt32(periodTime.Text);

                var processInfo = new SingleProcess(bt, pt);
                if (bt <= pt && bt > 0 && pt > 0)
                {
                    Console.WriteLine("Successfully added: " + bt + " " + pt);
                    _processes.Add(processInfo);
                    AddProcessToCB(processInfo);
                    burstTime.Text = "";
                    periodTime.Text = "";
                    var color = Color.FromRgb(221, 221, 221);
                    burstTime.Background = new SolidColorBrush(color);
                    periodTime.Background = new SolidColorBrush(color);
                    btnAdd.Background = new SolidColorBrush(color);
                    UpdateUtilizationLabel();

                    CheckProcessCount();
                }
                else
                    BadInput("Burst time cannot be bigger than the period");
            }
            else
                BadInput("Invalid characters or empty string");
        }

        private void CheckProcessCount()
        {
            lblLimit.Content = "";
            if (_processes.Count >= 10)
            {
                lblLimit.Content = "Process Count has reached the limit (10)";
                btnAdd.IsEnabled = false;
            }
            else
            {
                lblLimit.Content = "Process Count : " + _processes.Count;
                btnAdd.IsEnabled = true;
            }
        }

        private void AddProcessToCB(SingleProcess ProcessInfo)
        {
            var cbItem = new ComboBoxItem { Content = "BT:" + ProcessInfo.ExecutionTime + "\tPT:" + ProcessInfo.Period, DataContext = ProcessInfo, Tag = ProcessInfo };
            comboBox.Items.Add(cbItem);
            canvas.UpdateLayout();
        }

        private void comboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (comboBox.SelectedIndex < 0)
                return;

            var dummyItem = new ComboBoxItem();
            var selectionItem = (ComboBoxItem)comboBox.Items.GetItemAt(comboBox.SelectedIndex);
            _processes.Remove((SingleProcess)selectionItem.Tag);
            comboBox.SelectedItem = dummyItem;
            comboBox.Items.Remove(selectionItem);
            var removedItemInfo = (SingleProcess)selectionItem.Tag;
            Console.WriteLine("Successfully removed: " + removedItemInfo.ExecutionTime + " " + removedItemInfo.Period);
            CheckProcessCount();
            UpdateUtilizationLabel();
        }

        private void UpdateUtilizationLabel()
        {
            double total = 0;
            double utilization = Utils.CalculateUtilization(_processes);
            lblUtilization.Content = "Utilization: " + utilization.ToString("0.00") + " %";
            if (utilization > 100)
            {
                lblUtilization.Foreground = new SolidColorBrush(Color.FromRgb(255, 0, 0));
                btnDone.IsEnabled = false;
                lblPossibleProcess.Content = "";
            }
            else
            {
                lblUtilization.Foreground = new SolidColorBrush(Color.FromRgb(0, 0, 0));
                btnDone.IsEnabled = true;
                if (utilization != 100)
                {
                    //calculate the best possible process
                    double BPP = 1 - total;
                    var bppFraction = Fraction.Parse(BPP);
                    lblPossibleProcess.Content = "Best possible process: " + bppFraction.Numerator + "/" + bppFraction.Denominator;
                }
                else
                {
                    lblPossibleProcess.Content = "";
                }
            }
        }

        private void BadInput(string reason)
        {
            Console.WriteLine("Bad Input: " + reason);
            burstTime.Background = new SolidColorBrush(Color.FromRgb(255, 51, 51));
            periodTime.Background = new SolidColorBrush(Color.FromRgb(255, 51, 51));
            btnAdd.Background = new SolidColorBrush(Color.FromRgb(255, 51, 51));
        }

        private void btnDone_Click(object sender, RoutedEventArgs e)
        {
            var main = new MainWindow(_processes);
            main.Show();
            Close();
        }
    }
}
