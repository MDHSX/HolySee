#pragma once
#include "Source.h"
#include <vector>
#include "Reading.h"

class LidarSource :
	public Source
{
private:
	std::vector<Reading> scan;
	bool ready;
	std::string fileName;
public:
	LidarSource();
	~LidarSource();
	void simulate();
	bool isReady();
	void showScan();
	void saveScan();
	void imageScan();
};

