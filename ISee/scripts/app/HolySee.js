//declare the module and the app level controller
(function(){
	console.log('bootstrapping angular');
	//defining the application controller
	var appController = function($scope, $log, FeedService){
		$log.info('HolySeeController');

	    $scope.state = FeedService.state;
	    $scope.isConnected = FeedService.isConnected;

	    $scope.toggleConnected = function(){
	      FeedService.toggleConnected();
	    };

	    $scope.$watch(function(){return FeedService.state;},function(){$scope.state = FeedService.state;});      
	    $scope.$watch(function(){return FeedService.isConnected;},function(){$scope.isConnected = FeedService.isConnected;});      
	};

	angular.module('HolySee',['ngMaterial'])
	.config(['$httpProvider', function ($httpProvider) {
	            // enable http caching
	           $httpProvider.defaults.cache = false;
	      }])
  	 	.config(function($mdThemingProvider) {
		    var greyPalette = $mdThemingProvider.extendPalette('grey',{
		    	'50':'#ffffff'
		    });
		    $mdThemingProvider.definePalette('grey2',greyPalette);

	  		$mdThemingProvider.theme('default')
			    .primaryPalette('blue-grey')
			    .accentPalette('red')
			    // .warnPalette('red')
			    .backgroundPalette('grey2')
			    ; 	 	
		})
		.controller('HolySeeController',['$scope','$log', 'FeedService', appController])
		;
}());
