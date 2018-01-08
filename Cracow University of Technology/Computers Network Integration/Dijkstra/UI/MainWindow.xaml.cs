using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Windows;
using System.Windows.Media;
using ISK.Models;
using ISK.Types;

namespace ISK
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private readonly GraphProducer _producer;
        private int _position;
        private Dictionary<string, string> _shortestPathEdges = new Dictionary<string, string>();

        public MainWindow()
        {
            InitializeComponent();

            var verticles = new List<Vertex>();
            var edges = new List<Edge>();

            DgVerticles.ItemsSource = verticles;
            DgEdges.ItemsSource = edges;

            _producer = new GraphProducer(DgVerticles, DgEdges, gg_Area);
            gg_Area.VertexDoubleClick += Gg_Area_VertexDoubleClick;
        }

        private void GenerateGraph_OnClick(object sender, RoutedEventArgs e)
        {
            _producer.InitializeGraph();
            LbCost.Content = "0";
            _shortestPathEdges = new Dictionary<string, string>();
        }

        private void RandomizeGraph_OnClick(object sender, RoutedEventArgs e)
        {
            var sourceVertexes = DgVerticles.ItemsSource.Cast<Vertex>().ToList();
            var sourceEdges = DgEdges.ItemsSource.Cast<Edge>().ToList();

            sourceVertexes.Clear();
            sourceEdges.Clear();

            for (var i = 0; i < 25; i++)
            {
                while (true)
                {
                    var vertex = Utils.RandomString(3);
                    if (sourceVertexes.All(x => x.Name != vertex))
                    {
                        sourceVertexes.Add(new Vertex {Name = vertex});
                        break;
                    }
                }
            }

            var random = new Random();
            foreach (var vertex in sourceVertexes)
            {
                var sourceVertexesExceptSelf = sourceVertexes.Where(x => x.Name != vertex.Name).ToList();
                var vertex2 = sourceVertexesExceptSelf[random.Next(0, sourceVertexes.Count - 1)];
                sourceEdges.Add(new Edge
                {
                    Vertex1 = vertex.Name,
                    Vertex2 = vertex2.Name,
                    Cost = random.Next(1, 50)
                });
            }

            DgVerticles.ItemsSource = sourceVertexes;
            DgEdges.ItemsSource = sourceEdges;
        }

        private void Gg_Area_VertexDoubleClick(object sender, GraphX.Controls.Models.VertexSelectedEventArgs args)
        {
            var vertexTag = (VertexTag)args.VertexControl.Tag;
            var selected = !vertexTag.IsSelected;
            if (selected)
                VertexSelectionService.RegisterSelected(args.VertexControl);
            else
                VertexSelectionService.UnregisterSelected(args.VertexControl);
        }


        private void BtnAddVertex_OnClick(object sender, RoutedEventArgs e)
        {
            var dialog = new Dialog("Podaj nazwę wierzchołka:");
            if (dialog.ShowDialog() != true)
                return;

            var vertexName = dialog.Answer;
            var source = DgVerticles.ItemsSource.Cast<Vertex>().ToList();
            if (source.Any(x => x.Name == vertexName))
            {
                MessageBox.Show("Już istnieje wierzchołek o podanej nazwie.");
                return;
            }

            source.Add(new Vertex { Name = vertexName });
            DgVerticles.ItemsSource = source;

            _producer.VertexPositions = gg_Area.GetVertexPositions();

            VertexSelectionService.UnregisterAll();
            GenerateGraph_OnClick(sender, new RoutedEventArgs());
        }

        private void BtnAddEdge_OnClick(object sender, RoutedEventArgs e)
        {
            var selected = VertexSelectionService.GetSelected();
            if (selected.Count != 2)
            {
                MessageBox.Show("Brak zaznaczonych wierzchołków do połączenia.");
                return;
            }

            var dialog = new Dialog("Podaj koszt krawędzi:");
            if (dialog.ShowDialog() != true)
                return;

            var edgeCost = dialog.Answer;
            if (!int.TryParse(edgeCost, out var edgeCostNumeric))
            {
                MessageBox.Show("Podany koszt nie jest liczbą całkowitą.");
                return;
            }

            var dataVertex1 = selected[0].GetDataVertex<DataVertex>();
            var dataVertex2 = selected[1].GetDataVertex<DataVertex>();

            var source = DgEdges.ItemsSource.Cast<Edge>().ToList();
            source.Add(new Edge
            {
                Vertex1 = dataVertex1.Text,
                Vertex2 = dataVertex2.Text,
                Cost = edgeCostNumeric
            });

            DgEdges.ItemsSource = source;

            _producer.VertexPositions = gg_Area.GetVertexPositions();

            VertexSelectionService.UnregisterAll();
            GenerateGraph_OnClick(sender, new RoutedEventArgs());
        }

        private void BtnCalculatePath_OnClick(object sender, RoutedEventArgs e)
        {
            var dialog = new Dialog("Podaj początkowy wierzchołek:");
            if (dialog.ShowDialog() != true)
                return;

            var startVertex = dialog.Answer;
            if (string.IsNullOrWhiteSpace(startVertex))
            {
                MessageBox.Show("Podano pustą wartość.");
                return;
            }

            dialog = new Dialog("Podaj końcowy wierzchołek:");
            if (dialog.ShowDialog() != true)
                return;

            var endVertex = dialog.Answer;
            if (string.IsNullOrWhiteSpace(endVertex))
            {
                MessageBox.Show("Podano pustą wartość.");
                return;
            }

            _shortestPathEdges = _producer.CalculatePath(startVertex, endVertex);
            if (_shortestPathEdges == null || !_shortestPathEdges.Any())
            {
                MessageBox.Show("Nie udało się odnaleźć ścieżki.");
                return;
            }

            _position = _shortestPathEdges.Count - 1;
            foreach (var shortestPathEdge in _shortestPathEdges)
                PerformStep(shortestPathEdge, true);
        }

        private void PerformStep(KeyValuePair<string, string> shortestPathEdge, bool next)
        {
            var shortestPathEdgeControl = gg_Area.EdgesList.FirstOrDefault(x =>
                x.Value.GetDataEdge<DataEdge>().Source.Text == shortestPathEdge.Key &&
                x.Value.GetDataEdge<DataEdge>().Target.Text == shortestPathEdge.Value);

            var control = shortestPathEdgeControl.Value;
            if (next)
            {
                control.Background = new SolidColorBrush(Colors.Red);
                control.Foreground = new SolidColorBrush(Colors.Red);
            }
            else
            {
                control.Background = new SolidColorBrush(Colors.Black);
                control.Foreground = new SolidColorBrush(Colors.Black);
            }

            var currentContent = double.Parse((string)LbCost.Content);
            if (next)
                currentContent += shortestPathEdgeControl.Key.Weight;
            else
                currentContent -= shortestPathEdgeControl.Key.Weight;

            LbCost.Content = currentContent.ToString(CultureInfo.InvariantCulture);
        }

        private void BtnNextCost_OnClick(object sender, RoutedEventArgs e)
        {
            if (!_shortestPathEdges.Any())
                return;

            if (_position + 1 > _shortestPathEdges.Count - 1)
                return;

            var key = _shortestPathEdges.Keys.ElementAt(_position + 1);
            var edge = _shortestPathEdges.First(x => x.Key == key);
            PerformStep(edge, true);
            _position++;
        }

        private void BtnPreviousCost_OnClick(object sender, RoutedEventArgs e)
        {
            if (!_shortestPathEdges.Any())
                return;

            if (_position < 0)
                return;

            var key = _shortestPathEdges.Keys.ElementAt(_position);
            var edge = _shortestPathEdges.First(x => x.Key == key);
            PerformStep(edge, false);
            _position--;
        }
    }
}
