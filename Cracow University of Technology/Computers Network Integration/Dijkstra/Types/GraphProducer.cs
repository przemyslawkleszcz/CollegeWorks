using System.Collections.Generic;
using System.Linq;
using System.Windows.Controls;
using GraphX.Measure;
using ISK.Models;

namespace ISK.Types
{
    public class GraphProducer
    {
        public Dictionary<DataVertex, Point> VertexPositions { get; set; } = new Dictionary<DataVertex, Point>();

        private readonly DataGrid _dgVertexes;
        private readonly DataGrid _dgEdges;
        private readonly GraphAreaExample _graphArea;

        public GraphProducer(DataGrid dgVertexes, DataGrid dgEdges, GraphAreaExample graphArea)
        {
            _dgVertexes = dgVertexes;
            _dgEdges = dgEdges;
            _graphArea = graphArea;
        }

        public void InitializeGraph()
        {
            var graph = new GraphExample();
            var verticles = _dgVertexes.ItemsSource.Cast<Vertex>();
            var id = 1;
            foreach (var verticle in verticles)
            {
                graph.AddVertex(new DataVertex { ID = id, Text = verticle.Name });
                id++;
            }

            var edges = _dgEdges.ItemsSource.Cast<Edge>();
            foreach (var edge in edges)
            {
                var verticle1 = graph.Vertices.FirstOrDefault(x => x.Text == edge.Vertex1);
                var verticle2 = graph.Vertices.FirstOrDefault(x => x.Text == edge.Vertex2);
                if (!(verticle1 != null & verticle2 != null))
                    continue;

                var dataEdge = new DataEdge(verticle1, verticle2, edge.Cost) { Text = edge.Cost.ToString() };
                graph.AddEdge(dataEdge);
            }

            var logicCore = new GXLogicCoreExample();
            _graphArea.LogicCore = logicCore;
            _graphArea.GenerateGraph(graph);
            _graphArea.ShowAllEdgesLabels();
            _graphArea.SetVerticesDrag(true);

            foreach (var vertex in _graphArea.VertexList)
            {
                var control = vertex.Value;
                control.Tag = new VertexTag
                {
                    IsSelected = false
                };

                var pos = VertexPositions.FirstOrDefault(x => x.Key.ID == vertex.Key.ID);
                if (!pos.Equals(new KeyValuePair<DataVertex, Point>()))
                    control.SetPosition(new System.Windows.Point(pos.Value.X, pos.Value.Y));
            }
        }

        public Dictionary<string, string> CalculatePath(string startVertex, string endVertex)
        {
            var graph = new Graph();
            var vertexes = _dgVertexes.ItemsSource.Cast<Vertex>().ToList();
            var edges = _dgEdges.ItemsSource.Cast<Edge>().ToList();

            foreach (var vertex in vertexes)
            {
                var vertexEdges = edges.Where(x => x.Vertex1 == vertex.Name);
                var dict = vertexEdges.ToDictionary(vertexEdge => vertexEdge.Vertex2, vertexEdge => vertexEdge.Cost);
                graph.add_vertex(vertex.Name, dict);
            }

            var shortestPathVertexes = graph.shortest_path(startVertex, endVertex);
            if (shortestPathVertexes == null)
                return null;

            shortestPathVertexes.Add(startVertex);
            shortestPathVertexes.Reverse();

            var shortestPathEdges = new Dictionary<string, string>();
            for (var i = 0; i < shortestPathVertexes.Count; i++)
            {
                if (i + 1 < shortestPathVertexes.Count)
                    shortestPathEdges.Add(shortestPathVertexes[i], shortestPathVertexes[i + 1]);
            }

            return shortestPathEdges;
        }
    }
}
