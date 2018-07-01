//app.service("ApiCall", ["$http", "$mdDialog", "apiSettings", function ($http, $mdDialog, apiSettings) {
//    var result;    

//    this.GetApiCall = function (controller, action, obj) {
//        var url = apiSettings.apiUri + "/api/" + controller + "/" + action;
//        var firstArgument = true;
//        for (var key in obj) {
//            if (firstArgument) {
//                url += "?" + key + "=" + obj[key];
//                firstArgument = false;
//            }
//            else
//                url += "&" + key + "=" + obj[key];                
//        }
        
//        result = $http.get(url).success(function (data, status) {
//            result = data;
//        }).error(function () {
//            alert("Api error");
//        });

//        return result;
//    }

//    this.PostApiCall = function (controller, action, obj) {
//        var url = apiSettings.apiUri + "/api/" + controller + "/" + action;
//        result = $http.post(url, obj).success(function (data, status) {
//            result = data;
//        }).error(function (err) {
//            if (err.ModelState)
//                showErrors(err.ModelState);
//            else if (err.error_description)
//                showErrors(err.error_description);
//            else if (err.Message)
//                showErrors(err.Message);
//        })

//        return result;
//    }

//    //TODO: Move to another angular service 
//    this.ShowMessage = function (message) {
//        alert = $mdDialog.alert({
//            title: "Informacja",
//            htmlContent: message,
//            ok: 'Zamknij'
//        });

//        $mdDialog
//        .show(alert)
//        .finally(function () {
//            alert = undefined;
//        });
//    }

//    this.ShowPrompt = function (title, text, placeholder, confirmCallback, declineCallback) {
//        var confirm = $mdDialog.prompt()
//          .title(title)
//          .textContent(text)
//          .placeholder(placeholder)          
//          .ok('Ok')
//          .cancel('Cancel');
//        $mdDialog.show(confirm).then(confirmCallback, declineCallback);
//    };

//    this.ShowConfirm = function (title, text, confirmCallback) {
//        var confirm = $mdDialog.confirm()
//                  .title(title)
//                  .textContent(text)                                    
//                  .ok('Ok')
//                  .cancel('Cancel');

//        $mdDialog.show(confirm).then(confirmCallback, declineCallback);
//    }

//    function showErrors(modelState) {
//        var message = "";
//        if (modelState.constructor === Object) {
//            for (var err in modelState) {
//                if (modelState.hasOwnProperty(err)) {
//                    if (err == "$id")
//                        continue;

//                    message += modelState[err][0] + "<br>";
//                }
//            }
//        }
//        else
//            message = modelState;

//        if (message == "")
//            return;

//        alert = $mdDialog.alert({
//            title: "Informacja",
//            htmlContent: message,
//            ok: 'Zamknij'
//        });

//        $mdDialog
//        .show(alert)
//        .finally(function () {
//            alert = undefined;
//        });
//    }
//}]);