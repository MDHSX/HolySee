#include "stdafx.h"
#include "Reading.h"
#include <stdio.h>
#define _USE_MATH_DEFINES
#include <math.h>    

void Reading::calculateQuadrant(){
	if(this->angle >= 270) this->quagrant = 4;
	else if(this->angle >= 180) this->quagrant = 3;
	else if (this->angle >= 90) this->quagrant = 2;
	else this->quagrant = 0;
	//printf("angle %5.1f is in quadrant %d\n",this->angle,this->quagrant);
}



void Reading::calculateXY(){
	float _dis = distance;
	if (distance > range) _dis = range;;

	switch (quagrant){
	case 1:
		x = (int)round(((float)sin(M_PI*angle / 180.0f))*_dis + range);
		y = (int)round(-((float)cos(M_PI*angle / 180.0f))*_dis + range);
		break;
	case 2:
		x = (int)round(((float)sin(M_PI*(180.0 - angle) / 180.0f))*_dis + range);
		y = (int)round(((float)cos(M_PI*(180.0 - angle) / 180.0f))*_dis + range);
		break;
	case 3:
		x = (int)round(((float)sin(M_PI*(180.0 - angle) / 180.0f))*_dis + range);
		y = (int)round(((float)cos(M_PI*(180.0 - angle) / 180.0f))*_dis + range);
		break;
	default:
		x = (int)round(((float)sin(M_PI*angle / 180.0f))*_dis + range);
		y = (int)round(-((float)cos(M_PI*angle / 180.0f))*_dis + range);
	}
}

void Reading::calculateColor(){
	//white is 65535
	//black is 0
	//opaque is 65535
	//transparent is 0
	//if distance is 0, make reading transparant (0,0)
	//if distance is close make it very dard, make the far readings light gray
	//y=mx+b can be used to calculate color
	//color=m*distance+offset
	//range is 6000, colors can have 64K values, let's assume a slope of 10
	//we want closest to be closer to 0 and furthest away to be 65535, so slope should be +
	// asssume m=10
	// assume 65000 = 10(6000) + b
	// b = 65000 - 60000   --> b = 5000
	//formula for color is color = distance * 10 + 5000
	if (this->distance > this->range) this->color = (unsigned short)(this->range * 10.0 + 5000.0);
	this->color = (unsigned short)((this->distance * 10.0 + 5000.0)/255);
	if (distance <= 0.0) this->color=255;
}

Reading::Reading(bool isSynch,float angle,float distance,unsigned char quality)
{
	this->synch = isSynch;
	this->angle = angle;
	this->distance = distance;
	this->quality = quality;

	calculateQuadrant();
	calculateXY();
	calculateColor();
}


Reading::~Reading()
{
}

int Reading::getX(){
	return x;
}

int Reading::getY(){
	return y;
}

const float Reading::range = 6000;

unsigned short Reading::getColor(){
	return color;
}

unsigned short Reading::getOpacity(){
	return opacity;
}

float Reading::getAngle(){
	return angle;
}

float Reading::getDistance(){
	return distance;
}

unsigned char Reading::getQuality(){
	return quality;
}

bool Reading::isSynch(){
	return synch;
}