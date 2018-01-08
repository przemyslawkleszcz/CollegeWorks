using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Dynamic;
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
    class UIManager : UserControl
    {
        int boxWH = 25; //pixels
        Canvas canvas;
        //Canvas canvasOutside;
        int RowCount;
        int ColumnCount;
        ScrollViewer sv;
        Grid innerGrid1;
        ComboBox processCombo;

        public UIManager(int processCount, int LCM, float Utilization)
        {
            RowCount = processCount;
            ColumnCount = LCM;

            canvas = ((MainWindow)System.Windows.Application.Current.MainWindow).myCanvas;

            sv = ((MainWindow)System.Windows.Application.Current.MainWindow).sv;
            innerGrid1 = ((MainWindow)System.Windows.Application.Current.MainWindow).InnerGrid1;
            processCombo = ((MainWindow)System.Windows.Application.Current.MainWindow).processCombo;
        }

        public void DrawRect(int rowNo, Color color, int step)
        {
            System.Windows.Shapes.Rectangle rect;
            rect = new System.Windows.Shapes.Rectangle();
            rect.Stroke = new SolidColorBrush(color);
            rect.Fill = new SolidColorBrush(color);
            rect.Width = boxWH;
            rect.Height = boxWH;
            Canvas.SetLeft(rect, step * boxWH);
            Canvas.SetTop(rect, rowNo * boxWH);
            canvas.Width = boxWH * ColumnCount;
            canvas.Height = boxWH * RowCount;

            canvas.Children.Add(rect);
            
        }

        public void DrawStepLabel(int step)
        {
            Label label = new Label();
            label.HorizontalAlignment = HorizontalAlignment.Left;
            label.Margin = new Thickness(step * boxWH, innerGrid1.Height - canvas.Height - boxWH, 0, 0);
            label.Width = boxWH;
            label.Content = step;
            innerGrid1.Children.Add(label);
        }


        public void AddToUI(List<PeriodicProcess> _processes, float LCM)
        {
            ((MainWindow)System.Windows.Application.Current.MainWindow).AddInfoToWindow(_processes, LCM);
        }


    }


    public class UIBindClass
    {
        public ObservableCollection<ComboBoxItem> cbItems { get; set; }
        public ComboBoxItem SelectedcbItem { get; set; }

        public Label lblUtil { get; set; }

        public UIBindClass()
        {

        }
    }
}
