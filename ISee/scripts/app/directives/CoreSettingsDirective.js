// Subsystem Card directive
(function(){
  //Directive
  var directive = function(){
    return{
      restrict:'E',
      scope:{},
      controller: 'coreSettingsController',
      replace: true,
      template:
        '<div class="core-settings" layout="column" >'+
            '<md-toolbar class="md-primary md-hue-2">'+
              '<div class="md-toolbar-tools">'+
                '<span style="width: 80px;">Settings</span>'+
              '</div>'+
            '</md-toolbar>'+
            '<md-divider></md-divider>'+
            '<md-content>'+
              '<div ng-repeat="setting in settings">'+
                '<setting data-setting="setting"></setting>'+
                '<md-divider></md-divider>'+
              '</div>'+
            '</md-content>'+
        '</div>'
    }
  };


  var controller = function($scope,  $log, FeedService){
    // $log.info('coreSettingsController');
    $scope.config = FeedService.feedConfig;
    $scope.settings = FeedService.feedConfig.settings;

    $scope.$watch(
      function(){return FeedService.feedConfig.settings;},
      function(){$scope.settings = FeedService.feedConfig.settings;});
  };


  angular.module('HolySee')
  .directive('coreSettings',[directive])
  .controller('coreSettingsController',['$scope', '$log', 'FeedService', controller]);
}());
