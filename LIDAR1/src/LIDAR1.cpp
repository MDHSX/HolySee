//============================================================================
// Name        : LIDAR1.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include "rplidar/rplidar.h" //RPLIDAR standard sdk, all-in-one header

#ifndef _countof
#define _countof(_Array) (int)(sizeof(_Array) / sizeof(_Array[0]))
#endif

using namespace rp::standalone::rplidar;



bool checkRPLIDARHealth(RPlidarDriver * drv)
{
    u_result     op_result;
    rplidar_response_device_health_t healthinfo;


    op_result = drv->getHealth(healthinfo);
    if (IS_OK(op_result)) { // the macro IS_OK is the preperred way to judge whether the operation is succeed.
        printf("RPLidar health status : %d\n", healthinfo.status);
        if (healthinfo.status == RPLIDAR_STATUS_ERROR) {
            fprintf(stderr, "Error, rplidar internal error detected. Please reboot the device to retry.\n");
            // enable the following code if you want rplidar to be reboot by software
            // drv->reset();
            return false;
        } else {
            return true;
        }

    } else {
        fprintf(stderr, "Error, cannot retrieve the lidar health code: %x\n", op_result);
        return false;
    }
}

int main() {
	std::cout << "!!!Hello LIDAR!!!" << std::endl; // prints !!!Hello World!!!

    const char * opt_com_path = "/dev/ttyUSB0";
    _u32         opt_com_baudrate = 115200;
    u_result     op_result;


    printf("Connecting to LIDAR: %s using %d\n",opt_com_path,opt_com_baudrate);

    // create the driver instance
    RPlidarDriver * drv = RPlidarDriver::CreateDriver(RPlidarDriver::DRIVER_TYPE_SERIALPORT);


    if (!drv) {
        fprintf(stderr, "insufficent memory, exit\n");
        exit(-2);
    }

    // make connection...
    if (IS_FAIL(drv->connect(opt_com_path, opt_com_baudrate))) {
        fprintf(stderr, "Error, cannot bind to the specified serial port %s.\n"
            , opt_com_path);
        RPlidarDriver::DisposeDriver(drv);
        return 0;
    }

    printf("Connected!\n");

    rplidar_response_device_info_t devinfo;

	// retrieving the device info
    ////////////////////////////////////////
    op_result = drv->getDeviceInfo(devinfo);

    if (IS_FAIL(op_result)) {
        fprintf(stderr, "Error, cannot get device info.\n");
        RPlidarDriver::DisposeDriver(drv);
        return 0;
    }

    // print out the device serial number, firmware and hardware version number..
    printf("RPLIDAR S/N: ");
    for (int pos = 0; pos < 16 ;++pos) {
        printf("%02X", devinfo.serialnum[pos]);
    }

    printf("\n"
            "Firmware Ver: %d.%02d\n"
            "Hardware Rev: %d\n"
            , devinfo.firmware_version>>8
            , devinfo.firmware_version & 0xFF
            , (int)devinfo.hardware_version);



    // check health...
    if (!checkRPLIDARHealth(drv)) {
    	RPlidarDriver::DisposeDriver(drv);
    	return 0;
    }
    bool support = false;
    u_result result = drv->checkExpressScanSupported(support);
    printf("checkExpressScanSupported() returned u_result:0%xx, support %x, timeout:%d\n",result,support);

	drv->startMotor();
    // start scan...
    //drv->startScan();
	drv->startScanExpress(false);

    // fetech result and print it out...
//    while (1) {
        rplidar_response_measurement_node_t nodes[360*2];
        size_t   count = _countof(nodes);

        op_result = drv->grabScanData(nodes, count);

        if (IS_OK(op_result)) {
//            drv->ascendScanData(nodes, count);

            for (int pos = 0; pos < (int)count ; ++pos) {
            	bool isSynch = nodes[pos].sync_quality & RPLIDAR_RESP_MEASUREMENT_SYNCBIT;
            	float angle = (nodes[pos].angle_q6_checkbit >> RPLIDAR_RESP_MEASUREMENT_ANGLE_SHIFT)/64.0f;
            	float distance = nodes[pos].distance_q2/4.0f;
            	char quality = nodes[pos].sync_quality >> RPLIDAR_RESP_MEASUREMENT_QUALITY_SHIFT;

//            	printf("readings.push_back({%d,%f,%f,%d});\n",isSynch,angle,distance,quality);

                printf("%s theta: %03.2f Dist: %08.2f Q: %d \n",
                    (isSynch) ?"S ":"  ",
                    angle,
                    distance,
                    quality);
            }
        }
//    }

    drv->stop();
    drv->stopMotor();
    RPlidarDriver::DisposeDriver(drv);
    return 0;
}


