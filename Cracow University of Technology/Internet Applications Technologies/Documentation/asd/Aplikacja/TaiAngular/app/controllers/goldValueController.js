app.controller('goldValueController', ['$scope', '$rootScope', '$location', '$cookies', '$http', function ($scope, $rootScope, $location, $cookies, $http) {
    var self = this;

    self.inputValue = 0;
    self.getGoldValues = function () {
        var url = "http://api.nbp.pl/api/cenyzlota/last/" + self.inputValue + "?format=json";
        $http.get(url).success(function (data) {
            for (var i = 0; i < data.length; i++)
                data[i].userColor = "#ffd600";

            self.rates = data;
            self.data = new kendo.data.DataSource({
                data: self.rates
            });
        }).error(function () {
            alert("Api error");
        });
    }
}]);