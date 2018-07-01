var app = angular.module('TAI', ['ngMaterial', 'ngRoute', 'kendo.directives', 'ngResource', 'ngCookies', 'ngSanitize']);

app.config(function ($routeProvider) {

    $routeProvider.when("/exchangerates", {
        controller: "exchangeRatesController",        
        templateUrl: "/app/views/exchangeRates.html"
    });

    $routeProvider.when("/goldvalue", {
        controller: "goldValueController",
        templateUrl: "/app/views/goldValue.html"
    });

    $routeProvider.otherwise({ redirectTo: "/exchangerates" });
});

//app.config(function ($httpProvider) {
//    $httpProvider.interceptors.push('authInterceptorService');
//});

var serviceBase = "http://localhost:53365";
//var serviceBase = "http://localhost:99";
app.constant('apiSettings', {
    apiUri: serviceBase    
});

//app.run(function ($location, $rootScope) {
    //var postLogInRoute;

    //$rootScope.$on('$routeChangeStart', function (event, nextRoute, currentRoute) {        
    //    if (nextRoute.loginRequired && !authService.authentication.isAuth) {
    //        postLogInRoute = $location.path();
    //        var language = translationService.getCurrentLanguage();
    //        $location.path("/" + language + "/login").replace();
    //    } else if (postLogInRoute && authService.authentication.isAuth) {            
    //        $location.path(postLogInRoute).replace();
    //        postLogInRoute = null;
    //    }
    //});
//});