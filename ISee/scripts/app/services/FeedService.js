//This service manages communicaitons with the robot
(function(){
    // var robotAddress = "ws://172.22.11.2:5808";
    // var robotAddress = 'ws://roboRIO-4141-FRC.local:5808';
    var robotAddress = "ws://127.0.0.1:5808";

    function service($q,$log,$timeout, $interval,$rootScope){
        $log.info("FeedService");

        var connector;  //variable managing periodic attempt to connect to roborio

        var events = [];
        var eventcapacity = 999;


        var post = function(message){
            // $log.info('sending message to RoboRio');
            // $log.info(message);
            if(ws) ws.send(message);
            else $log.info('ws not valid');
        };

        var clearEvents = function(){
            events.length=0;
        };

        var getEventsCount = function(){
            return events.length;
        };

        var toggleConnected = function(){
            serviceObj.isConnected = !serviceObj.isConnected;
        }


        var config = {
            settings:[]
        };

        var serviceObj = {
                events: events,
                clearEvents: clearEvents,
                getEventsCount: getEventsCount,
                post: post,
                isConnected:false,
                toggleConnected:toggleConnected,
                state: 'Disabled',
                feedConfig: config,
                reconnect:reconnect
            };

        var process=function(event){
            var eventObj=JSON.parse(event);

            if(eventObj.display) {
                // $log.info('events size: '+events.length);
                // $log.info(eventObj);
                $timeout(function(){events.push(eventObj);
                    if(events.length>eventcapacity) events.shift();
                    // $log.info('events size: '+events.length);
                });
            }
            if(eventObj.record) DatabaseService.record(eventObj);      
            if(eventObj.eventType=='ConfigurationNotification') {
                updateConfiguration(eventObj);
            }
            
        };
    
        index = 0;
        var ws;
        var onopen = function(){
            $log.info('WS CONNECTED!');
            $interval.cancel(connector);            
            this.send('{"client":"operator-console"}');
            serviceObj.isConnected = true;
        };
        var onmessage = function(evt){
            // $timeout(function(){
                // $log.info('processing '+evt.data);
                process(evt.data);
            // });
        };
        var onclose = function(){
            ws = undefined;
            serviceObj.isConnected = false;
            if(!serviceObj.isPlayback) {
                $log.info('Robot connection closed.  Attempting to reconnect ...');
                connect();
            }
        };
        var onerror = function(err){
            // $log.info('WS CONNECTION ERROR: ')
            // $log.info(err);
            // ws = undefined;
            // connect();
        };
        var connect = function(){
            connector = $timeout(function(){
                if (ws === undefined){
                    // $log.info('simulation off');
                    $log.info('initializing web socket client...');
                    // ws = new WebSocket('ws://127.0.0.1:5808');
                    ws = new WebSocket(robotAddress);
                    ws.onopen = onopen;
                    ws.onmessage = onmessage;
                    ws.onclose = onclose;
                    ws.onerror = onerror;
                }
            },10);             
        };

        connect();
        return serviceObj;  

        function updateConfiguration(event){
            $log.info('config:');
            $log.info(event);
            // serviceObj.clock.fpgaTime=0.0;
            if(config.hasOwnProperty('settings')){
                $log.info('settings:');
                $log.info(event.settings);
                config.settings = event.settings; 
            //     serviceObj.clock.fpgaTime = robotConfig.fpgaTime;
            }
            // serviceObj.robotConfig.subsystems={};
            // if(robotConfig.hasOwnProperty('subsystems')){
            //     // $log.info('config subsystems');
            //     serviceObj.robotConfig.subsystems = robotConfig.subsystems;
            // }
   
            // serviceObj.robotConfig.consoleOI={};
            // if(robotConfig.hasOwnProperty('consoleOI')){
            //     // $log.info('config consoleOI');
            //     serviceObj.robotConfig.consoleOI = robotConfig.consoleOI;
            // }         
        }

        function reconnect(){
            if(ws) ws.close();
        }

    }
    angular.module('HolySee')
         .service('FeedService', ['$q','$log', '$timeout', '$interval', '$rootScope', service]);
    
})();
