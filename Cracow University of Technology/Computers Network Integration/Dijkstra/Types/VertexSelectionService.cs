using System.Collections.Generic;
using System.Windows.Media;
using GraphX.Controls;

namespace ISK.Types
{
    public class VertexSelectionService
    {
        public static List<VertexControl> Selected = new List<VertexControl>();
        private static readonly Color DefaultColor = (Color)ColorConverter.ConvertFromString("#FFE3E3E3");
        private const int OFFSET = 2;

        public static void RegisterSelected(VertexControl control)
        {
            SelectOrUnselect(control, Colors.Blue, true);
            Selected.Add(control);

            if (Selected.Count <= OFFSET)
                return;

            Selected.Reverse();
            Selected.GetRange(OFFSET, Selected.Count - OFFSET).ForEach(x => SelectOrUnselect(x, DefaultColor, false));
            Selected.RemoveRange(OFFSET, Selected.Count - OFFSET);
            Selected.Reverse();
        }

        public static void UnregisterSelected(VertexControl control)
        {
            SelectOrUnselect(control, DefaultColor, false);
            Selected.Remove(control);
        }

        public static void UnregisterAll()
        {
            Selected.ForEach(x => SelectOrUnselect(x, DefaultColor, false));
            Selected.RemoveRange(0, Selected.Count);
        }

        public static List<VertexControl> GetSelected()
        {
            return Selected;
        }

        private static void SelectOrUnselect(VertexControl control, Color color, bool isSelected)
        {
            var vertexTag = (VertexTag)control.Tag;
            vertexTag.IsSelected = isSelected;
            control.Background = new SolidColorBrush(color);
        }
    }
}
