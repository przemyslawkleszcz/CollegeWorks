app.controller('exchangeRatesController', ['$scope', '$rootScope', '$location', '$cookies', '$http', function ($scope, $rootScope, $location, $cookies, $http) {
    var self = this;

    self.inputValue = "";
    self.getExchangeRates = function () {
        var url = "http://api.nbp.pl/api/exchangerates/tables/" + self.inputValue + "?format=json";
        $http.get(url).success(function (data) {
            self.rates = data[0].rates;
            self.data = new kendo.data.DataSource({
                data: self.rates
            });
        }).error(function () {
            alert("Api error");
        });
    }

    self.mainGridOptions = {
        sortable: true,
        pageable: true,
        columns: [{
            field: "currency",
            title: "currency",
            width: "120px"
        }, {
            field: "code",
            title: "code",
            width: "120px"
        },
        {
            field: "mid",
            title: "mid",
            width: "120px"
        }]
    }
}]);