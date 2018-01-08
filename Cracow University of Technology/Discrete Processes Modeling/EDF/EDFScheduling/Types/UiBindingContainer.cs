using System.Collections.ObjectModel;
using System.Windows.Controls;

namespace EDFScheduling.Types
{
    public class UiBindingContainer
    {
        public ObservableCollection<ComboBoxItem> CbItems { get; set; }
        public ComboBoxItem SelectedCbItem { get; set; }
        public Label LblUtil { get; set; }
    }
}