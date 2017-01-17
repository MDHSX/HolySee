/*
 * LidarSimulator.h
 *
 *  Created on: Jan 16, 2017
 *      Author: team4141
 */

#ifndef LIDARSIMULATOR_H_
#define LIDARSIMULATOR_H_

#include <vector>

struct Reading
{
	bool isSynch;
	float angle;
	float distance;
	char quality;
};

class LidarSimulator {
public:
	LidarSimulator();
	virtual ~LidarSimulator();
	void next(std::vector<Reading>&);
};

#endif /* LIDARSIMULATOR_H_ */
