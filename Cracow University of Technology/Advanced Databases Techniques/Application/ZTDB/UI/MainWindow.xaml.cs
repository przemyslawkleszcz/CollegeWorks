using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Linq;
using System.Windows;
using ZTBD.Types;
using Telerik.Windows.Controls.Charting;

namespace ZTBD
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private List<PerformanceResult> CalculateSqlServer()
        {
            var results = new List<PerformanceResult>();
            var sw = new Stopwatch();
            var conString = Properties.Settings.Default.SqlServerConnectionString;
            for (var i = 1; i < Properties.Settings.Default.AttemptsCounter + 1; i++)
            {
                sw.Start();
                var data = new DataSet();

                var connection = new SqlConnection(conString);
                try
                {
                    connection.Open();
                    var sda = new SqlDataAdapter(Properties.Settings.Default.SqlServerQuery, connection);
                    sda.Fill(data);
                    sw.Stop();
                    results.Add(new PerformanceResult(i, sw.Elapsed.TotalSeconds));
                }
                catch (Exception)
                {
                    MessageBox.Show("Wystąpił błąd.");
                    break;
                }
                finally
                {
                    connection.Close();
                }

                sw.Reset();
            }

            return results;
        }

        private List<PerformanceResult> CalculateCouchDB()
        {
            var results = new List<PerformanceResult>();
            var sw = new Stopwatch();
            var server = Properties.Settings.Default.CouchDbServer;
            var databaseName = Properties.Settings.Default.CouchDbDataBase;
            var sharpCouchDb = new DB();
            for (var i = 1; i < Properties.Settings.Default.AttemptsCounter + 1; i++)
            {
                sw.Start();
                sharpCouchDb.GetAllDocuments(server, databaseName);
                sw.Stop();
                results.Add(new PerformanceResult(i, sw.Elapsed.TotalSeconds));
                sw.Reset();
            }

            return results;
        }

        private void ConfigureMapping(string label, List<PerformanceResult> results)
        {
            var mapping = new SeriesMapping
            {
                LegendLabel = label,
                SeriesDefinition = new SplineSeriesDefinition(),
                ItemsSource = results
            };

            mapping.ItemMappings.Add(new ItemMapping("Attempt", DataPointMember.XValue));
            mapping.ItemMappings.Add(new ItemMapping("Time", DataPointMember.YValue));
            radChart.SeriesMappings.Add(mapping);
        }

        public MainWindow()
        {
            InitializeComponent();
            Loaded += MainWindow_Loaded;
        }

        private void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            var sqlServerResults = CalculateSqlServer();
            var couchDbResults = CalculateCouchDB();
            ConfigureMapping("Sql Server", sqlServerResults);
            ConfigureMapping("Couch db", couchDbResults);
            var sqlServerAvg = sqlServerResults.Select(x => x.Time).Average();
            var couchDbAvg = couchDbResults.Select(x => x.Time).Average();
            MessageBox.Show($"Średni czas dostępu:\nSqlServer: {sqlServerAvg} [s]\nCouchDb: {couchDbAvg} [s]");
        }
    }
}
