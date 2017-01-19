// LidarSim.cpp : main project file.

#include "LidarSource.h"


int main(int argc,char *argv[]){
	LidarSource* lidar = new LidarSource();
	lidar->showScan();
	delete lidar;
    return 0;
}
