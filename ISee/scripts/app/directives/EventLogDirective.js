// Subsystem Card directive
(function(){
  //Directive
  var directive = function(){
    return{
      restrict:'E',
      scope:{},
      controller: 'eventLogController',
      replace: true,
      template:
        '<div class="message-log" layout="column" >'+
            '<md-toolbar class="md-primary md-hue-2">'+
              '<div class="md-toolbar-tools">'+
                '<span style="width: 80px;">Events</span>'+
                '<span style="width: 80px;">({{events.length}})</span>'+
                '<span flex></span>'+
                '<md-button ng-click="toggleError()" ng-class="{\'md-warn md-raised\':showErrors, \'md-primary\':!showErrors}"><md-icon ng-show="showErrors">visibility</md-icon><md-icon ng-hide="showErrors">visibility_off</md-icon> <md-icon>error</md-icon></md-button>'+
                '<md-button ng-click="toggleWarning()"  ng-class="{\'md-accent  md-raised\':showWarnings, \'md-primary\':!showWarnings}"><md-icon ng-show="showWarnings">visibility</md-icon><md-icon ng-hide="showWarnings">visibility_off</md-icon> <md-icon>warning</md-icon></md-button>'+
                '<md-button ng-click="toggleInfo()"  ng-class="{\'md-accent  md-raised md-hue-3\':showInfo, \'md-primary\':!showInfo}"><md-icon ng-show="showInfo">visibility</md-icon><md-icon ng-hide="showInfo">visibility_off</md-icon> <md-icon>info_outline</md-icon></md-button>'+
                '<span flex></span>'+
                '<md-switch ng-model="autoscroll" aria-label="autoscroll" class="md-accent" ng-class="{\'switch-off\':!autoscroll}">autoscroll</md-switch>'+
                '<md-button ng-disabled="events.length<1" ng-click="clear()"><md-icon>delete<md-icon></md-button>'+
              '</div>'+
            '</md-toolbar>'+
            '<md-divider></md-divider>'+
          '<md-content scroll-bottom="events">'+
              '<div ng-repeat="event in events  track by $index">'+
                 '<div ng-if="event.eventType==\'RobotConfigurationNotification\'"><log-configuration ng-show="showEvent(event)" data-event="event"></log-configuration></div>'+
                 '<div ng-if="event.eventType==\'Heartbeat\'"><log-heartbeat ng-show="showEvent(event)" data-event="event"></log-heartbeat></div>'+
                 '<div ng-if="event.eventType==\'ConsoleRumbleNotification\'"><log-rumble ng-show="showEvent(event)" data-event="event"></log-rumble></div>'+
                 '<div ng-if="event.eventType==\'RobotLogNotification\'"><log-message data-event="event" ng-show="showEvent(event)"></log-message></div>'+
                // '<div>{{event}}</div>'+
              '</div>'+
          '</md-content>'+
        '</div>'
    }
  };


  var controller = function($scope,  $log, FeedService){
    // $log.info('eventLogController');
    $scope.events = FeedService.events;
    $scope.autoscroll = true;

    $scope.clear = function(){
      FeedService.clearEvents();
    };

    $scope.showErrors = true;
    $scope.toggleError = function(){
      $scope.showErrors = !$scope.showErrors;
    };

    $scope.showWarnings= true;
    $scope.toggleWarning = function(){
      $scope.showWarnings = !$scope.showWarnings;
    };

    $scope.showInfo = true;
    $scope.toggleInfo = function(){
      $scope.showInfo = !$scope.showInfo;
    };

    $scope.showEvent = function(event){
      var show = false;
      if(event.eventType == 'RobotLogNotification'){
        // $log.info('show for RobotLogNotification');
        switch(event.level){
          case 'ERROR':
            return $scope.showErrors;
          case 'INFO':
            return $scope.showInfo;
          case 'WARNING':
            return $scope.showWarnings;
        }
      }
      else if(event.eventType == 'Heartbeat' || event.eventType == 'ConsoleRumbleNotification' || event.eventType == 'RobotConfigurationNotification'){
        return $scope.showInfo;
      }
      return show; 
    };

  };


  angular.module('HolySee')
  .directive('eventLog',[directive])
  .controller('eventLogController',['$scope', '$log', 'FeedService', controller]);
}());
