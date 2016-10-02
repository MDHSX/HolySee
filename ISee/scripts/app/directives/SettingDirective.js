// Subsystem Card directive
(function(){
  //Directive
  var directive = function(){
  	return{
  		restrict:'E',
  		scope:{setting:"="},
  		controller: 'settingController',
  		replace: true,
      // templateUrl: 'scripts/app/views/SubsystemCard.html'
      /*
        <md-content ng-controller="RightCtrl" layout-padding>
          <form>
            <md-input-container>
              <label for="testInput">Test input</label>
              <input type="text" id="testInput"
                     ng-model="data" md-autofocus>
            </md-input-container>
          </form>
          <md-button ng-click="close()" class="md-primary">
            Close Sidenav Right
          </md-button>
        </md-content>
      */
      template: '<div class="setting">'+
                // '<form>'+
                  '<div><md-input-container ng-if="setting.type==\'string\'">'+
                    '<label for="{{setting.name}}Input">{{setting.name}}</label>'+
                    '<input type="text" id="{{setting.name}}Input"  ng-model="setting.value" ng-change="change()" md-autofocus>'+
                  '</md-input-container></div>'+
                  '<div ng-if="setting.type==\'decimal\' || setting.type==\'integer\'" layout="row" layout-wrap>'+
                      '<div flex="70" layout="column">'+
                        '<div layout="row">'+
                          '<div flex class="title truncate">{{setting.name}}</div>'+
                          '<div flex="none" class="title-control" ng-hide="configure" ng-click="openConfigure()"><md-icon>build</md-icon></div>'+
                        '</div>'+
                        '<div ng-hide="configure">'+
                           '<md-slider aria-label="{{setting.name}}" style="padding-left:6px" ng-change="change()" min="{{setting.min}}" max="{{setting.max}}" ng-model="setting.value" step="{{setting.step}}"></md-slider>'+
                        '</div>'+
                      '</div>'+
                      '<span class="value" ng-hide="configure" flex="30">{{setting.value}}</span>'+
                      '<div class="setting-configuration-panel" ng-show="configure" flex="100">'+
                          '<div>'+
                          '<span>range:</span>'+
                          '<div class="inline-numeric"><md-input-container md-no-float>'+
                              '<input placeholder="min" type="text" id="{{setting.name}}min" ng-change="calcStep()" ng-model="setting.min" numeric>'+
                          '</md-input-container></div>'+
                          '<span> - </span>'+
                          '<div class="inline-numeric"><md-input-container md-no-float>'+
                              '<input placeholder="max" type="text" id="{{setting.name}}max" ng-change="calcStep()" ng-model="setting.max" numeric>'+
                          '</md-input-container></div>'+
                          '<md-icon class="md-primary config-control" ng-click="closeConfigure()">close</md-icon>'+
                          '</div>'+
                    '</div>'+  
                  '</div>'+

                  '<div ng-if="setting.type==\'binary\'">'+
                    '<md-switch class="md-primary" aria-label="{{setting.name}} setting" ng-model="setting.value"  ng-change="change()">{{setting.name}}</md-switch>'+
                  '</div>'+
                // '</form>'+
                '</div>'
  	}
  };

  var controller = function($scope, $log, $mdSidenav, RobotService){
    // $log.info('settingController');
    // $log.info('setting:');
    // $log.info($scope.setting);
    $scope.step = 1;
    function calcStep(){
      // $log.info('calcStep');
      if($scope.setting.type == 'integer' ){
        $scope.setting.step = 1;
      }
      if($scope.min > $scope.value) $scope.min = $scope.value+"";
      if($scope.max < $scope.value) $scope.max = $scope.value+"";
      if($scope.setting.type == 'decimal'){
        $scope.setting.step = (($scope.setting.max - $scope.setting.min)/100).toFixed(2);
        // $log.info('step = '+$scope.setting.step);
      }
    }
    calcStep();
    $scope.calcStep = calcStep;
    $scope.change = function(){
            var message='{"type":"settingUpdate", '+
                       '"subsystem":"'+ $scope.setting.subsystem + 
                       '", "settingName":"'+ $scope.setting.name + 
                       '", "value":"'+ $scope.setting.value +
                       '"';
            if($scope.setting.type == 'decimal' || $scope.setting.type == 'integer' ){
                if($scope.setting.min){
                  message += (', "min":'+$scope.setting.min);
                }
                if($scope.setting.max){
                  message += (', "max":'+$scope.setting.max);
                }
            }
            message+='}';
                      //TODO check for setting min and max and add to message
      $log.info($scope.setting);
      $log.info(message);
      RobotService.post(message);
    };
    $scope.configure=false;
    $scope.openConfigure = function(){
      $scope.configure=true;
    };
    $scope.closeConfigure = function(){
      $scope.configure=false;
    };
    
  };

  angular.module('HolySee')
  .directive('setting',[directive])
  .controller('settingController',['$scope', '$log', '$mdSidenav','RobotService', controller]);
}());
