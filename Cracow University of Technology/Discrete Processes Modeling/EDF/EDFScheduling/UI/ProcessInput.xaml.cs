using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using EDFScheduling.Types;

namespace EDFScheduling.UI
{
    /// <summary>
    /// Interaction logic for ProcessInput.xaml
    /// </summary>
    public partial class ProcessInput : Window
    {
        private readonly List<Task> _tasks;

        public ProcessInput()
        {
            InitializeComponent();
            _tasks = new List<Task>();
            var uiContainer = new UiBindingContainer();
            DataContext = uiContainer;
        }

        private void BtnAdd_Click(object sender, RoutedEventArgs e)
        {
            var sExecutionTime = TbExecutionTime.Text;
            var sPeriodTime = TbPeriodTime.Text;

            if (sExecutionTime.All(char.IsDigit) && !string.IsNullOrEmpty(sExecutionTime) && sPeriodTime.All(char.IsDigit) && !string.IsNullOrEmpty(sPeriodTime))
            {
                var execution = Convert.ToInt32(TbExecutionTime.Text);
                var period = Convert.ToInt32(TbPeriodTime.Text);

                var taskInfo = new Task(execution, period);
                if (execution <= period && execution > 0 && period > 0)
                {
                    _tasks.Add(taskInfo);
                    AddTaskToComboBox(taskInfo);
                    TbExecutionTime.Text = "";
                    TbPeriodTime.Text = "";
                    var color = Color.FromRgb(221, 221, 221);
                    TbExecutionTime.Background = new SolidColorBrush(color);
                    TbPeriodTime.Background = new SolidColorBrush(color);
                    BtnAdd.Background = new SolidColorBrush(color);
                    UpdateUtilizationLabel();
                    CheckTaskCount();
                }
                else
                    ShowError("Czas wykonania nie może być większy od okresu.");
            }
            else
                ShowError("Niepoprawne dane wejściowe.");
        }

        private void BtnConfirm_Click(object sender, RoutedEventArgs e)
        {
            var main = new MainWindow(_tasks);
            main.Show();
            Close();
        }

        private void ComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (ComboBox.SelectedIndex < 0)
                return;

            var dummyItem = new ComboBoxItem();
            var selectionItem = (ComboBoxItem)ComboBox.Items.GetItemAt(ComboBox.SelectedIndex);
            _tasks.Remove((Task)selectionItem.Tag);
            ComboBox.SelectedItem = dummyItem;
            ComboBox.Items.Remove(selectionItem);
            var removedItemInfo = (Task)selectionItem.Tag;
            var message = "Pomyślnie usunięto: " + removedItemInfo.ExecutionTime + " " + removedItemInfo.Period;
            Console.WriteLine(message);
            CheckTaskCount();
            UpdateUtilizationLabel();
        }

        private void CheckTaskCount()
        {
            LblLimit.Content = "";
            if (_tasks.Count >= 10)
            {
                LblLimit.Content = "Liczba zadań osiągnęła granicę (10)";
                BtnAdd.IsEnabled = false;
            }
            else
            {
                LblLimit.Content = "Liczba zadań : " + _tasks.Count;
                BtnAdd.IsEnabled = true;
            }
        }

        private void AddTaskToComboBox(Task taskInfo)
        {
            var cbItem = new ComboBoxItem { Content = "Wykonanie: " + taskInfo.ExecutionTime + "\tOkres: " + taskInfo.Period, DataContext = taskInfo, Tag = taskInfo };
            ComboBox.Items.Add(cbItem);
            Canvas.UpdateLayout();
        }

        private void UpdateUtilizationLabel()
        {
            double utilization = Utils.CalculateUtilization(_tasks);
            LblUtilization.Content = "Utylizacja: " + utilization.ToString("0.00") + " %";
            if (utilization > 100)
            {
                LblUtilization.Foreground = new SolidColorBrush(Color.FromRgb(255, 0, 0));
                BtnConfirm.IsEnabled = false;
            }
            else
            {
                LblUtilization.Foreground = new SolidColorBrush(Color.FromRgb(0, 0, 0));
                BtnConfirm.IsEnabled = true;
            }
        }

        private void ShowError(string reason)
        {
            MessageBox.Show(reason);
            TbExecutionTime.Background = new SolidColorBrush(Color.FromRgb(255, 51, 51));
            TbPeriodTime.Background = new SolidColorBrush(Color.FromRgb(255, 51, 51));
            BtnAdd.Background = new SolidColorBrush(Color.FromRgb(255, 51, 51));
        }
    }
}
