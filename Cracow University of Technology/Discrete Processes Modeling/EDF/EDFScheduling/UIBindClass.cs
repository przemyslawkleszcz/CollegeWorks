using System.Collections.ObjectModel;
using System.Windows.Controls;

namespace EDFScheduling
{
    public class UIBindClass
    {
        public ObservableCollection<ComboBoxItem> cbItems { get; set; }
        public ComboBoxItem SelectedcbItem { get; set; }
        public Label LblUtil { get; set; }
    }
}