// LidarSim.cpp : main project file.

#include "stdafx.h"
#include "LidarSource.h"


int main(array<System::String ^> ^args)
{
	LidarSource* lidar = new LidarSource();
	lidar->showScan();
	delete lidar;
    return 0;
}
