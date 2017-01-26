// LidarSim.cpp : main project file.

#include "LidarSource.h"


int main(int argc,char *argv[]){
	LidarSource* lidar = new LidarSource("/dev/ttyUSB0");
	lidar->setSimulate(true);
	lidar->initialize();
	lidar->showScan();
	lidar->shutdown();
	delete lidar;
    return 0;
}
