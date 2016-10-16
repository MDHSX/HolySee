// Subsystem Card directive
(function(){
  //Directive
  var directive = function(){
    return{
      restrict:'E',
      scope:{count:"="},
      controller: 'badgeController',
      replace: true,
      template:'<div class="badge">{{count}}</div>'
    }
  };

  var controller = function($scope,  $log){
    // $log.info('badgeController');
    // $log.info('count='+$scope.count);
  };

  angular.module('HolySee')
  .directive('badge',[directive])
  .controller('badgeController',['$scope', '$log', controller]);
}());