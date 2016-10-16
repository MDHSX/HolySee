// Subsystem Card directive
(function(){
  //Directive
  var directive = function(){
    return{
      restrict:'E',
      scope:{state:"="},
      controller: 'robotStateController',
      replace: true,
      template: '<div class="robot-state">'+
                  '<md-divider></md-divider>'+
                  '<div class="label">state</div>'+
                  '<div  class="value">{{state}}</div>'+
                  '<md-divider></md-divider>'+
                '</div>'
    }
  };

  var controller = function($scope,  $log){
  };

  angular.module('HolySee')
  .directive('robotState',[directive])
  .controller('robotStateController',['$scope', '$log', controller]);
}());