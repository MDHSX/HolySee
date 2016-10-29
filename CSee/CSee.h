
/*
 * CSee.h
 *
 *  Created on: Oct 28, 2016
 *      Author: team4141
 */

#ifndef CSEE_H_
#define CSEE_H_



#include <map>
#include <iostream>
#include <string>

#include "opencv/cv.hpp"
#include "Poco/JSON/Object.h"


namespace CSee{
std::map<int, std::string> createPropertyNames();
extern const std::map<int, std::string> cameraPropertyNames = createPropertyNames();
std::string disvocerCameras();
std::string decodeFourCC(int fourcc);
Poco::JSON::Object makeCameraPropertyNode(int propertyId, std::string& key, std::string& value);

}
#endif /* CSEE_H_ */
