using System.ComponentModel;
using System.Xml.Serialization;
using GraphX.Controls;
using GraphX.PCL.Common.Models;
using GraphX.PCL.Logic.Models;
using QuickGraph;

namespace ISK.Models
{
    public class GraphAreaExample : GraphArea<DataVertex, DataEdge, BidirectionalGraph<DataVertex, DataEdge>> { }
    public class GraphExample : BidirectionalGraph<DataVertex, DataEdge> { }
    public class GXLogicCoreExample : GXLogicCore<DataVertex, DataEdge, BidirectionalGraph<DataVertex, DataEdge>> { }
    public class DataVertex : VertexBase
    {
        [XmlAttribute("text")]
        [DefaultValue("")]
        public string Text { get; set; }

        public override string ToString()
        {
            return Text;
        }
    }

    public class DataEdge : EdgeBase<DataVertex>
    {
        public DataEdge(DataVertex source, DataVertex target, double weight = 1)
            : base(source, target, weight)
        {
        }

        public DataEdge()
            : base(null, null, 1)
        {
        }

        [XmlAttribute("text")]
        [DefaultValue("")]
        public string Text { get; set; }

        public override string ToString()
        {
            return Text;
        }
    }
}
