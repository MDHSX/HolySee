// Subsystem Card directive
(function(){
  //Directive
  var directive = function($log,$timeout){
    return {
      scope: false,
      link: function ($scope, $element) {
        $scope.$watchCollection('events', function (newValue) {
          if (newValue && $scope.autoscroll) {
            $timeout(function(){
              $element[0].scrollTop = ($element[0].scrollHeight);
            }, 0);
          }
        });
      }
    };
  };

  angular.module('HolySee')
  .directive('scrollBottom',['$log', '$timeout', directive])
}());