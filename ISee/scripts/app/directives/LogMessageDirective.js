// Subsystem Card directive
(function(){
  //Directive
  var directive = function(){
    return{
      restrict:'E',
      scope:{event:"="},
      controller: 'logMessageController',
      replace: true,
      template: 
            '<div layout="row" ng-class="{\'selected\':hovering}" class="log-message" ng-mouseenter="hovering = true;" ng-mouseleave="hovering = false;" ng-click="clicked(\$event)">'+
              '<div style="width:20px;" flex="none"><md-icon class="md-warn" ng-show="event.level==\'ERROR\'">error</md-icon><md-icon  class="md-accent" ng-show="event.level==\'WARNING\'">warning</md-icon><md-icon ng-show="event.level==\'INFO\'">info_outline</md-icon></div>'+
              '<div flex="initial">{{event.fpgaTime}}:</div>'+
              '<div flex="initial">{{event.source}}:</div>'+
              '<div flex="auto" class="truncate" title="{{event.message}}">{{event.message}}</div>'+
              '<div style="width:30px;position:relative" flex="none" ng-show="hovering" ng-click="copy(\$event)" title="Copy to Clipboard">'+
                 '<md-icon>content_copy</md-icon>'+
              '</div>'+
            '</div>'
    }
  };

  var controller = function($scope, $log, service){
    $scope.hovering = false;
    // $log.info($scope.event);

    $scope.copy = function(event){
      event.stopPropagation();

      var t = document.createElement('textarea')
      t.id = 't'
      // Optional step to make less noise in the page, if any!
      t.style.height = 0
      // You have to append it to your page somewhere, I chose <body>
      document.body.appendChild(t)
      // Copy whatever is in your div to our new textarea
      t.value = event.target.parentElement.previousElementSibling.innerText;
      // $log.info(t.value);
      // Now copy whatever inside the textarea to clipboard
      let selector = document.querySelector('#t')
      selector.select()
      document.execCommand('copy')
      // Remove the textarea
      document.body.removeChild(t)      
    }

    $scope.isEllipsisActive = function(event) {
      $scope.truncated = event.target.offsetWidth < event.target.scrollWidth;
    };
  };

  angular.module('HolySee')
  .directive('logMessage',[directive])
  .controller('logMessageController',['$scope', '$log', controller]);
}());

