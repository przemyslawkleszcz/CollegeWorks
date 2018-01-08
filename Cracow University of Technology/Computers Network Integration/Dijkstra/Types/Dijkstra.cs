using System.Collections.Generic;

namespace ISK.Types
{
    public class Graph
    {
        readonly Dictionary<string, Dictionary<string, int>> _vertices = new Dictionary<string, Dictionary<string, int>>();
        public void add_vertex(string name, Dictionary<string, int> edges)
        {
            _vertices[name] = edges;
        }

        public List<string> shortest_path(string start, string finish)
        {
            var previous = new Dictionary<string, string>();
            var distances = new Dictionary<string, int>();
            var nodes = new List<string>();

            List<string> path = null;

            foreach (var vertex in _vertices)
            {
                if (vertex.Key == start)
                    distances[vertex.Key] = 0;
                else
                    distances[vertex.Key] = int.MaxValue;

                nodes.Add(vertex.Key);
            }

            while (nodes.Count != 0)
            {
                nodes.Sort((x, y) => distances[x] - distances[y]);

                var smallest = nodes[0];
                nodes.Remove(smallest);

                if (smallest == finish)
                {
                    path = new List<string>();
                    while (previous.ContainsKey(smallest))
                    {
                        path.Add(smallest);
                        smallest = previous[smallest];
                    }

                    break;
                }

                if (distances[smallest] == int.MaxValue)
                    break;

                foreach (var neighbor in _vertices[smallest])
                {
                    var alt = distances[smallest] + neighbor.Value;
                    if (alt >= distances[neighbor.Key])
                        continue;

                    distances[neighbor.Key] = alt;
                    previous[neighbor.Key] = smallest;
                }
            }

            return path;
        }
    }
}
