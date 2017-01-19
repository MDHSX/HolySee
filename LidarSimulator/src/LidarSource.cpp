#include "stdafx.h"
#include <iostream>
#include <fstream>
#include <string>
#include "LidarSource.h"

#include <cstdint>
#include "opencv2\opencv.hpp"
#include <opencv2/core/types_c.h>

template<int> struct BaseType { };
template<> struct BaseType<CV_8S>  { using base_type = int8_t; };
template<> struct BaseType<CV_8U>  { using base_type = uint8_t; };
template<> struct BaseType<CV_16S> { using base_type = int16_t; };
template<> struct BaseType<CV_16U> { using base_type = uint16_t; };
template<> struct BaseType<CV_32S> { using base_type = int32_t; };
template<> struct BaseType<CV_32F> { using base_type = float; };
template<> struct BaseType<CV_64F> { using base_type = double; };

LidarSource::LidarSource()
{
	ready = false;
}

LidarSource::~LidarSource()
{
	scan.clear();
}

bool LidarSource::isReady(){
	return ready;
}

void LidarSource::showScan()
{
	cv::namedWindow("LIDAR", cv::WINDOW_AUTOSIZE);// Create a window for display.
	while (cv::waitKey(20) < 0){
		if (!isReady()){
			simulate();
			printf("next frame\n");
		}
		if (isReady() && scan.size() > 0){
			cv::Mat img(2 * (int)(Reading::range / 10), 2 * ((int)Reading::range / 10), CV_8UC1, 255);
			for (Reading reading : scan){
				if (reading.getQuality() != 0){
					//printf("(%d,%d)=%d\n", reading.getY()/10, reading.getX()/10, reading.getColor());
					//img.at<uint8_t>(reading.getY()/10, reading.getX()/10) = (unsigned char)reading.getColor();
					cv::circle(img, cv::Point(reading.getX() / 10, reading.getY() / 10), 2, cv::Scalar(reading.getColor()), 2, CV_AA, 0);
				}
			}
			cv::imshow("LIDAR", img);
			this->ready = false;
		}
	}
}

void LidarSource::imageScan()
{
	//create an image (png) representing the scan
	//use opencv
	if (scan.size() > 0){
		cv::Mat img(2 * (int)Reading::range, 2 * (int)Reading::range, CV_8UC1,255);
		for (Reading reading : scan){
			if (reading.getQuality() != 0){
				printf("(%d,%d)=%d\n", reading.getY(), reading.getX(), reading.getColor());
				//img.at<uint8_t>(reading.getY(), reading.getX()) = (unsigned char)reading.getColor();
				cv::circle(img, cv::Point(reading.getX(), reading.getY()), 4, cv::Scalar(reading.getColor()/4), 5, CV_AA, 0);

			}			
		}

		std::vector<int> compression_params;
		//compression_params.push_back(CV_8UC1);
		//compression_params.push_back(9);

		try {
			cv::imwrite("scan.png", img, compression_params);
		}
		catch (cv::Exception& ex) {
			fprintf(stderr, "Exception converting image to PNG format: %s\n", ex.what());
		}
	}
}

void LidarSource::saveScan()
{
	std::ofstream file;
	if (this->fileName.size() > 0){
		file.open(this->fileName);
	}
	else{
		file.open("scan.dat");
	}
	if (scan.size() > 0){
		bool isFirst = true;
		file << "[\n";
		for (Reading reading : scan){
			if (isFirst) isFirst = !isFirst;
			else file << ", ";
			file << "{";
			if (reading.isSynch()) file << "\"S\":true,";
			file << "\"angle\":" << std::to_string(reading.getAngle());
			file << ", \"distance\":" << std::to_string(reading.getDistance());
			file << ", \"quality\":" << std::to_string((int)reading.getQuality());
			file << ", \"x\":" << std::to_string(reading.getX());
			file << ", \"y\":" << std::to_string(reading.getY());
			file << ", \"color\":" << std::to_string(reading.getColor());
			file << ", \"opacity\":" << std::to_string(reading.getOpacity());
			file << "}\n";
		}
		file << "]\n";
	}
	file.flush();
	file.close();
}

void LidarSource::simulate()
{
	//simulate a scan
	scan.clear();
	scan.push_back({ 1, 352.718750, 1706.000000, 47 });
	scan.push_back({ 0, 353.921875, 1687.000000, 47 });
	scan.push_back({ 0, 356.312500, 1663.000000, 47 });
	scan.push_back({ 0, 357.515625, 1652.000000, 47 });
	scan.push_back({ 0, 358.843750, 1643.000000, 47 });
	scan.push_back({ 0, 0.046875, 1640.000000, 47 });
	scan.push_back({ 0, 1.250000, 1637.000000, 47 });
	scan.push_back({ 0, 2.375000, 0.000000, 0 });
	scan.push_back({ 0, 2.453125, 1622.000000, 47 });
	scan.push_back({ 0, 3.656250, 1617.000000, 47 });
	scan.push_back({ 0, 4.859375, 1615.000000, 47 });
	scan.push_back({ 0, 6.046875, 1611.000000, 47 });
	scan.push_back({ 0, 7.125000, 1604.000000, 47 });
	scan.push_back({ 0, 8.453125, 1603.000000, 47 });
	scan.push_back({ 0, 9.656250, 1604.000000, 47 });
	scan.push_back({ 0, 10.859375, 1597.000000, 47 });
	scan.push_back({ 0, 12.062500, 1598.000000, 47 });
	scan.push_back({ 0, 13.265625, 1598.000000, 47 });
	scan.push_back({ 0, 14.468750, 1598.000000, 47 });
	scan.push_back({ 0, 15.656250, 1599.000000, 47 });
	scan.push_back({ 0, 16.859375, 1598.000000, 47 });
	scan.push_back({ 0, 18.062500, 1599.000000, 47 });
	scan.push_back({ 0, 19.140625, 1605.000000, 47 });
	scan.push_back({ 0, 20.468750, 1607.000000, 47 });
	scan.push_back({ 0, 21.671875, 1614.000000, 47 });
	scan.push_back({ 0, 22.875000, 1616.000000, 47 });
	scan.push_back({ 0, 24.078125, 1628.000000, 47 });
	scan.push_back({ 0, 25.281250, 1640.000000, 47 });
	scan.push_back({ 0, 26.484375, 1643.000000, 47 });
	scan.push_back({ 0, 27.562500, 1654.000000, 47 });
	scan.push_back({ 0, 28.765625, 1660.000000, 47 });
	scan.push_back({ 0, 30.093750, 1672.000000, 47 });
	scan.push_back({ 0, 31.296875, 1679.000000, 47 });
	scan.push_back({ 0, 32.500000, 1694.000000, 47 });
	scan.push_back({ 0, 33.578125, 1709.000000, 47 });
	scan.push_back({ 0, 34.781250, 1722.000000, 47 });
	scan.push_back({ 0, 35.984375, 1744.000000, 47 });
	scan.push_back({ 0, 37.187500, 1759.000000, 47 });
	scan.push_back({ 0, 38.390625, 1778.000000, 47 });
	scan.push_back({ 0, 39.593750, 1797.000000, 47 });
	scan.push_back({ 0, 40.796875, 1816.000000, 47 });
	scan.push_back({ 0, 42.000000, 1838.000000, 47 });
	scan.push_back({ 0, 43.203125, 1858.000000, 47 });
	scan.push_back({ 0, 44.406250, 1881.000000, 47 });
	scan.push_back({ 0, 45.484375, 1907.000000, 47 });
	scan.push_back({ 0, 46.687500, 1941.000000, 47 });
	scan.push_back({ 0, 47.890625, 1966.000000, 47 });
	scan.push_back({ 0, 49.093750, 1995.000000, 47 });
	scan.push_back({ 0, 50.296875, 2026.000000, 47 });
	scan.push_back({ 0, 51.500000, 2056.000000, 47 });
	scan.push_back({ 0, 52.703125, 2085.000000, 47 });
	scan.push_back({ 0, 53.890625, 2124.000000, 47 });
	scan.push_back({ 0, 55.078125, 2156.000000, 47 });
	scan.push_back({ 0, 56.281250, 2196.000000, 47 });
	scan.push_back({ 0, 57.468750, 2231.000000, 47 });
	scan.push_back({ 0, 58.671875, 2280.000000, 47 });
	scan.push_back({ 0, 59.734375, 2321.000000, 47 });
	scan.push_back({ 0, 60.921875, 2369.000000, 47 });
	scan.push_back({ 0, 62.125000, 2422.000000, 47 });
	scan.push_back({ 0, 63.312500, 2479.000000, 47 });
	scan.push_back({ 0, 64.515625, 2539.000000, 47 });
	scan.push_back({ 0, 65.703125, 2606.000000, 47 });
	scan.push_back({ 0, 66.890625, 2674.000000, 47 });
	scan.push_back({ 0, 68.093750, 2758.000000, 47 });
	scan.push_back({ 0, 69.281250, 2834.000000, 47 });
	scan.push_back({ 0, 70.359375, 2918.000000, 47 });
	scan.push_back({ 0, 71.546875, 3025.000000, 47 });
	scan.push_back({ 0, 72.734375, 3130.000000, 47 });
	scan.push_back({ 0, 81.562500, 0.000000, 0 });
	scan.push_back({ 0, 75.250000, 2758.000000, 47 });
	scan.push_back({ 0, 83.953125, 0.000000, 0 });
	scan.push_back({ 0, 77.515625, 3401.000000, 47 });
	scan.push_back({ 0, 78.703125, 3401.000000, 47 });
	scan.push_back({ 0, 79.906250, 3354.000000, 47 });
	scan.push_back({ 0, 81.093750, 3331.000000, 47 });
	scan.push_back({ 0, 82.296875, 3292.000000, 47 });
	scan.push_back({ 0, 91.109375, 0.000000, 0 });
	scan.push_back({ 0, 92.296875, 0.000000, 0 });
	scan.push_back({ 0, 85.875000, 3218.000000, 47 });
	scan.push_back({ 0, 87.062500, 3207.000000, 47 });
	scan.push_back({ 0, 88.265625, 3183.000000, 47 });
	scan.push_back({ 0, 89.453125, 3066.000000, 47 });
	scan.push_back({ 0, 90.656250, 3059.000000, 47 });
	scan.push_back({ 0, 91.843750, 3054.000000, 47 });
	scan.push_back({ 0, 93.031250, 3050.000000, 47 });
	scan.push_back({ 0, 94.218750, 3047.000000, 47 });
	scan.push_back({ 0, 95.421875, 3042.000000, 47 });
	scan.push_back({ 0, 96.609375, 3033.000000, 47 });
	scan.push_back({ 0, 97.796875, 3034.000000, 47 });
	scan.push_back({ 0, 99.000000, 3016.000000, 47 });
	scan.push_back({ 0, 100.187500, 3017.000000, 47 });
	scan.push_back({ 0, 101.375000, 3029.000000, 47 });
	scan.push_back({ 0, 102.578125, 3032.000000, 47 });
	scan.push_back({ 0, 103.765625, 3033.000000, 47 });
	scan.push_back({ 0, 104.953125, 3024.000000, 47 });
	scan.push_back({ 0, 106.156250, 3105.000000, 47 });
	scan.push_back({ 0, 114.968750, 0.000000, 0 });
	scan.push_back({ 0, 116.156250, 0.000000, 0 });
	scan.push_back({ 0, 117.359375, 0.000000, 0 });
	scan.push_back({ 0, 118.546875, 0.000000, 0 });
	scan.push_back({ 0, 119.734375, 0.000000, 0 });
	scan.push_back({ 0, 119.171875, 242.000000, 47 });
	scan.push_back({ 0, 122.125000, 0.000000, 0 });
	scan.push_back({ 0, 123.312500, 0.000000, 0 });
	scan.push_back({ 0, 124.500000, 0.000000, 0 });
	scan.push_back({ 0, 125.703125, 0.000000, 0 });
	scan.push_back({ 0, 126.890625, 0.000000, 0 });
	scan.push_back({ 0, 128.078125, 0.000000, 0 });
	scan.push_back({ 0, 129.281250, 0.000000, 0 });
	scan.push_back({ 0, 130.468750, 0.000000, 0 });
	scan.push_back({ 0, 131.656250, 0.000000, 0 });
	scan.push_back({ 0, 132.859375, 0.000000, 0 });
	scan.push_back({ 0, 134.046875, 0.000000, 0 });
	scan.push_back({ 0, 135.234375, 0.000000, 0 });
	scan.push_back({ 0, 136.437500, 0.000000, 0 });
	scan.push_back({ 0, 137.625000, 0.000000, 0 });
	scan.push_back({ 0, 131.187500, 3506.000000, 47 });
	scan.push_back({ 0, 132.390625, 3555.000000, 47 });
	scan.push_back({ 0, 133.578125, 3262.000000, 47 });
	scan.push_back({ 0, 134.781250, 3206.000000, 47 });
	scan.push_back({ 0, 135.968750, 3187.000000, 47 });
	scan.push_back({ 0, 137.171875, 3169.000000, 47 });
	scan.push_back({ 0, 138.359375, 3158.000000, 47 });
	scan.push_back({ 0, 139.562500, 3108.000000, 47 });
	scan.push_back({ 0, 140.750000, 3111.000000, 47 });
	scan.push_back({ 0, 141.953125, 3191.000000, 47 });
	scan.push_back({ 0, 143.140625, 3244.000000, 47 });
	scan.push_back({ 0, 151.968750, 0.000000, 0 });
	scan.push_back({ 0, 145.531250, 3119.000000, 47 });
	scan.push_back({ 0, 146.734375, 3114.000000, 47 });
	scan.push_back({ 0, 147.921875, 3193.000000, 47 });
	scan.push_back({ 0, 149.109375, 3433.000000, 47 });
	scan.push_back({ 0, 157.937500, 0.000000, 0 });
	scan.push_back({ 0, 159.125000, 0.000000, 0 });
	scan.push_back({ 0, 160.328125, 0.000000, 0 });
	scan.push_back({ 0, 161.515625, 0.000000, 0 });
	scan.push_back({ 0, 162.718750, 0.000000, 0 });
	scan.push_back({ 0, 163.906250, 0.000000, 0 });
	scan.push_back({ 0, 165.109375, 0.000000, 0 });
	scan.push_back({ 0, 166.296875, 0.000000, 0 });
	scan.push_back({ 0, 167.500000, 0.000000, 0 });
	scan.push_back({ 0, 168.687500, 0.000000, 0 });
	scan.push_back({ 0, 169.890625, 0.000000, 0 });
	scan.push_back({ 0, 171.078125, 0.000000, 0 });
	scan.push_back({ 0, 172.281250, 0.000000, 0 });
	scan.push_back({ 0, 173.468750, 0.000000, 0 });
	scan.push_back({ 0, 174.671875, 0.000000, 0 });
	scan.push_back({ 0, 175.859375, 0.000000, 0 });
	scan.push_back({ 0, 177.062500, 0.000000, 0 });
	scan.push_back({ 0, 178.265625, 0.000000, 0 });
	scan.push_back({ 0, 179.468750, 0.000000, 0 });
	scan.push_back({ 0, 180.671875, 0.000000, 0 });
	scan.push_back({ 0, 181.859375, 0.000000, 0 });
	scan.push_back({ 0, 183.062500, 0.000000, 0 });
	scan.push_back({ 0, 184.265625, 0.000000, 0 });
	scan.push_back({ 0, 185.468750, 0.000000, 0 });
	scan.push_back({ 0, 186.671875, 0.000000, 0 });
	scan.push_back({ 0, 187.859375, 0.000000, 0 });
	scan.push_back({ 0, 189.062500, 0.000000, 0 });
	scan.push_back({ 0, 190.265625, 0.000000, 0 });
	scan.push_back({ 0, 191.468750, 0.000000, 0 });
	scan.push_back({ 0, 192.671875, 0.000000, 0 });
	scan.push_back({ 0, 193.875000, 0.000000, 0 });
	scan.push_back({ 0, 195.062500, 0.000000, 0 });
	scan.push_back({ 0, 196.265625, 0.000000, 0 });
	scan.push_back({ 0, 197.468750, 0.000000, 0 });
	scan.push_back({ 0, 198.671875, 0.000000, 0 });
	scan.push_back({ 0, 199.875000, 0.000000, 0 });
	scan.push_back({ 0, 201.062500, 0.000000, 0 });
	scan.push_back({ 0, 202.265625, 0.000000, 0 });
	scan.push_back({ 0, 203.468750, 0.000000, 0 });
	scan.push_back({ 0, 204.671875, 0.000000, 0 });
	scan.push_back({ 0, 205.875000, 0.000000, 0 });
	scan.push_back({ 0, 207.062500, 0.000000, 0 });
	scan.push_back({ 0, 200.890625, 2243.000000, 47 });
	scan.push_back({ 0, 202.093750, 2237.000000, 47 });
	scan.push_back({ 0, 203.296875, 2210.000000, 47 });
	scan.push_back({ 0, 204.500000, 2231.000000, 47 });
	scan.push_back({ 0, 213.078125, 0.000000, 0 });
	scan.push_back({ 0, 206.890625, 2111.000000, 47 });
	scan.push_back({ 0, 208.093750, 2124.000000, 47 });
	scan.push_back({ 0, 209.281250, 2132.000000, 47 });
	scan.push_back({ 0, 210.484375, 2147.000000, 47 });
	scan.push_back({ 0, 211.671875, 2159.000000, 47 });
	scan.push_back({ 0, 212.875000, 2176.000000, 47 });
	scan.push_back({ 0, 214.078125, 2191.000000, 47 });
	scan.push_back({ 0, 215.265625, 2207.000000, 47 });
	scan.push_back({ 0, 216.468750, 2225.000000, 47 });
	scan.push_back({ 0, 217.656250, 2241.000000, 47 });
	scan.push_back({ 0, 220.359375, 673.000000, 47 });
	scan.push_back({ 0, 221.562500, 659.000000, 47 });
	scan.push_back({ 0, 222.750000, 643.000000, 47 });
	scan.push_back({ 0, 224.078125, 629.000000, 47 });
	scan.push_back({ 0, 227.890625, 296.000000, 47 });
	scan.push_back({ 0, 229.718750, 290.000000, 47 });
	scan.push_back({ 0, 231.296875, 277.000000, 47 });
	scan.push_back({ 0, 232.109375, 271.000000, 47 });
	scan.push_back({ 0, 234.187500, 264.000000, 47 });
	scan.push_back({ 0, 235.250000, 260.000000, 47 });
	scan.push_back({ 0, 236.703125, 259.000000, 47 });
	scan.push_back({ 0, 236.906250, 256.000000, 47 });
	scan.push_back({ 0, 238.093750, 256.000000, 47 });
	scan.push_back({ 0, 239.546875, 257.000000, 47 });
	scan.push_back({ 0, 240.734375, 258.000000, 47 });
	scan.push_back({ 0, 241.937500, 258.000000, 47 });
	scan.push_back({ 0, 242.640625, 262.000000, 47 });
	scan.push_back({ 0, 244.203125, 264.000000, 47 });
	scan.push_back({ 0, 247.781250, 0.000000, 0 });
	scan.push_back({ 0, 245.968750, 283.000000, 47 });
	scan.push_back({ 0, 247.921875, 281.000000, 47 });
	scan.push_back({ 0, 249.000000, 276.000000, 47 });
	scan.push_back({ 0, 250.562500, 274.000000, 47 });
	scan.push_back({ 0, 251.750000, 275.000000, 47 });
	scan.push_back({ 0, 252.687500, 281.000000, 47 });
	scan.push_back({ 0, 254.000000, 274.000000, 47 });
	scan.push_back({ 0, 255.437500, 277.000000, 47 });
	scan.push_back({ 0, 256.125000, 280.000000, 47 });
	scan.push_back({ 0, 257.203125, 280.000000, 47 });
	scan.push_back({ 0, 258.265625, 281.000000, 47 });
	scan.push_back({ 0, 259.078125, 283.000000, 47 });
	scan.push_back({ 0, 263.265625, 0.000000, 0 });
	scan.push_back({ 0, 264.453125, 0.000000, 0 });
	scan.push_back({ 0, 261.890625, 323.000000, 47 });
	scan.push_back({ 0, 263.718750, 325.000000, 47 });
	scan.push_back({ 0, 264.406250, 330.000000, 47 });
	scan.push_back({ 0, 265.468750, 336.000000, 47 });
	scan.push_back({ 0, 266.781250, 343.000000, 47 });
	scan.push_back({ 0, 267.593750, 358.000000, 47 });
	scan.push_back({ 0, 272.781250, 0.000000, 0 });
	scan.push_back({ 0, 273.968750, 0.000000, 0 });
	scan.push_back({ 0, 275.171875, 0.000000, 0 });
	scan.push_back({ 0, 276.359375, 0.000000, 0 });
	scan.push_back({ 0, 277.546875, 0.000000, 0 });
	scan.push_back({ 0, 271.984375, 1182.000000, 47 });
	scan.push_back({ 0, 273.046875, 1178.000000, 47 });
	scan.push_back({ 0, 274.234375, 1174.000000, 47 });
	scan.push_back({ 0, 275.437500, 1169.000000, 47 });
	scan.push_back({ 0, 276.625000, 1169.000000, 47 });
	scan.push_back({ 0, 277.937500, 1167.000000, 47 });
	scan.push_back({ 0, 279.125000, 1160.000000, 47 });
	scan.push_back({ 0, 280.312500, 1160.000000, 47 });
	scan.push_back({ 0, 281.375000, 1156.000000, 47 });
	scan.push_back({ 0, 282.578125, 1138.000000, 47 });
	scan.push_back({ 0, 283.890625, 1136.000000, 47 });
	scan.push_back({ 0, 284.968750, 1139.000000, 47 });
	scan.push_back({ 0, 286.281250, 1160.000000, 47 });
	scan.push_back({ 0, 287.484375, 1161.000000, 47 });
	scan.push_back({ 0, 288.687500, 1135.000000, 47 });
	scan.push_back({ 0, 289.750000, 1142.000000, 47 });
	scan.push_back({ 0, 297.828125, 0.000000, 0 });
	scan.push_back({ 0, 299.031250, 0.000000, 0 });
	scan.push_back({ 0, 300.218750, 0.000000, 0 });
	scan.push_back({ 0, 301.421875, 0.000000, 0 });
	scan.push_back({ 0, 302.625000, 0.000000, 0 });
	scan.push_back({ 0, 303.812500, 0.000000, 0 });
	scan.push_back({ 0, 298.015625, 1313.000000, 47 });
	scan.push_back({ 0, 306.218750, 0.000000, 0 });
	scan.push_back({ 0, 307.406250, 0.000000, 0 });
	scan.push_back({ 0, 308.609375, 0.000000, 0 });
	scan.push_back({ 0, 309.812500, 0.000000, 0 });
	scan.push_back({ 0, 304.250000, 1111.000000, 47 });
	scan.push_back({ 0, 312.203125, 0.000000, 0 });
	scan.push_back({ 0, 313.406250, 0.000000, 0 });
	scan.push_back({ 0, 314.593750, 0.000000, 0 });
	scan.push_back({ 0, 315.796875, 0.000000, 0 });
	scan.push_back({ 0, 310.000000, 1418.000000, 47 });
	scan.push_back({ 0, 311.187500, 1395.000000, 47 });
	scan.push_back({ 0, 312.390625, 1358.000000, 47 });
	scan.push_back({ 0, 313.718750, 1317.000000, 47 });
	scan.push_back({ 0, 314.781250, 1339.000000, 47 });
	scan.push_back({ 0, 316.109375, 1215.000000, 47 });
	scan.push_back({ 0, 317.312500, 1197.000000, 47 });
	scan.push_back({ 0, 318.500000, 1206.000000, 47 });
	scan.push_back({ 0, 319.828125, 1169.000000, 47 });
	scan.push_back({ 0, 321.031250, 1161.000000, 47 });
	scan.push_back({ 0, 322.234375, 1173.000000, 47 });
	scan.push_back({ 0, 330.187500, 0.000000, 0 });
	scan.push_back({ 0, 324.265625, 1530.000000, 47 });
	scan.push_back({ 0, 325.468750, 1561.000000, 47 });
	scan.push_back({ 0, 326.671875, 1592.000000, 47 });
	scan.push_back({ 0, 327.875000, 1624.000000, 47 });
	scan.push_back({ 0, 329.078125, 1657.000000, 47 });
	scan.push_back({ 0, 330.281250, 1692.000000, 47 });
	scan.push_back({ 0, 331.359375, 1740.000000, 47 });
	scan.push_back({ 0, 332.562500, 1781.000000, 47 });
	scan.push_back({ 0, 334.140625, 1208.000000, 47 });
	scan.push_back({ 0, 342.218750, 0.000000, 0 });
	scan.push_back({ 0, 343.421875, 0.000000, 0 });
	scan.push_back({ 0, 337.625000, 1378.000000, 47 });
	scan.push_back({ 0, 338.828125, 1440.000000, 47 });
	scan.push_back({ 0, 340.046875, 1414.000000, 47 });
	scan.push_back({ 0, 341.125000, 1508.000000, 47 });
	scan.push_back({ 0, 342.328125, 1441.000000, 47 });
	scan.push_back({ 0, 350.656250, 0.000000, 0 });
	scan.push_back({ 0, 345.109375, 1063.000000, 47 });
	scan.push_back({ 0, 346.312500, 1062.000000, 47 });
	scan.push_back({ 0, 347.390625, 1222.000000, 47 });
	scan.push_back({ 0, 348.468750, 1260.000000, 47 });
	scan.push_back({ 0, 349.546875, 1506.000000, 47 });
	scan.push_back({ 0, 350.625000, 1730.000000, 47 });
	ready = true;
}

