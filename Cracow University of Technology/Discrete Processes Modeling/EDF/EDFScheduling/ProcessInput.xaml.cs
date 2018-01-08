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
using System.Windows.Shapes;

namespace EDFScheduling
{
    /// <summary>
    /// Interaction logic for ProcessInput.xaml
    /// </summary>
    public partial class ProcessInput : Window
    {
        List<Tuple<int, int>> processes;
        UIBindClass uiClass;

        public ProcessInput()
        {
            InitializeComponent();
            processes = new List<Tuple<int, int>>();

            uiClass = new UIBindClass();
            DataContext = uiClass;
        }

        private void btnAdd_Click(object sender, RoutedEventArgs e)
        {
            int bt, pt;

            string btStr, ptStr;
            btStr = burstTime.Text;
            ptStr = periodTime.Text;

            if ((btStr.All(char.IsDigit) && !string.IsNullOrEmpty(btStr)) && (ptStr.All(char.IsDigit) && !string.IsNullOrEmpty(ptStr)))
            {
                bt = Convert.ToInt32(burstTime.Text);
                pt = Convert.ToInt32(periodTime.Text);

                Tuple<int, int> processInfo = new Tuple<int, int>(bt, pt);

                if (bt <= pt && bt > 0 && pt > 0)
                {

                    Console.WriteLine("Successfully added: " + bt + " " + pt);
                    processes.Add(processInfo);
                    AddProcessToCB(processInfo);
                    burstTime.Text = "";
                    periodTime.Text = "";
                    burstTime.Background = new SolidColorBrush(Color.FromRgb(221, 221, 221));
                    periodTime.Background = new SolidColorBrush(Color.FromRgb(221, 221, 221));
                    btnAdd.Background = new SolidColorBrush(Color.FromRgb(221, 221, 221));
                    UpdateUtilizationLabel();

                    CheckProcessCount();
                }
                else
                {
                    BadInput("Burst time cannot be bigger than the period");
                }
            }
            else
            {
                BadInput("Invalid characters or empty string");
            }
        }

        private void CheckProcessCount()
        {
            lblLimit.Content = "";
            if (processes.Count >= 10)
            {
                lblLimit.Content = "Process Count has reached the limit (10)";

                btnAdd.IsEnabled = false;
            }
            else
            {
                lblLimit.Content = "Process Count : " + processes.Count;

                btnAdd.IsEnabled = true;
            }
        }

        private void AddProcessToCB(Tuple<int, int> ProcessInfo)
        {
            var cbItem = new ComboBoxItem { Content = "BT:" + ProcessInfo.Item1 + "\tPT:" + ProcessInfo.Item2, DataContext = ProcessInfo, Tag = ProcessInfo };

            //var cbItem = new ListItem("BT:" + ProcessInfo.Item1 + "\tPT:" + ProcessInfo.Item2, ProcessInfo);
            comboBox.Items.Add(cbItem);
            canvas.UpdateLayout();
        }


        private void comboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (comboBox.SelectedIndex >= 0)
            {
                ComboBoxItem dummyItem = new ComboBoxItem();

                ComboBoxItem selectionItem = (ComboBoxItem)comboBox.Items.GetItemAt(comboBox.SelectedIndex);

                processes.Remove((Tuple<int, int>)selectionItem.Tag);
                comboBox.SelectedItem = dummyItem;
                comboBox.Items.Remove(selectionItem);

                Tuple<int, int> removedItemInfo = (Tuple<int, int>)selectionItem.Tag;
                Console.WriteLine("Successfully removed: " + removedItemInfo.Item1 + " " + removedItemInfo.Item2);
                CheckProcessCount();
                UpdateUtilizationLabel();
            }
        }

        private void UpdateUtilizationLabel()
        {
            double total = 0;
            double utilization;

            for (int i = 0; i < processes.Count; i++)
            {
                total += (double)processes[i].Item1 / processes[i].Item2; // executiontime divided by period
            }
            utilization = total * 100f;

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
                    Fraction bppFraction = Fraction.Parse(BPP);
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
            MainWindow main = new MainWindow(processes);
            main.Show();
            this.Close();
        }
    }

    public class ListItem
    {
        public string Text { get; set; }
        public Tuple<int, int> processInfo { get; set; }

        public ListItem(string _text, Tuple<int, int> _processInfo)
        {
            processInfo = _processInfo;
            Text = _text;
        }

        public override string ToString()
        {
            return Text;
        }
    }

}
