#pragma once
class Reading
{
private:
	bool synch;
	float angle;
	float distance;
	unsigned char quality;
	unsigned char quagrant;
	int x;
	int y;
	unsigned short color;
	unsigned short opacity;
	void calculateQuadrant();
	void calculateXY();
	void calculateColor();
public:
	Reading::Reading(bool, float, float, unsigned char);
	~Reading();
	static const float range;
	bool isSynch();
	float getAngle();
	float getDistance();
	unsigned char getQuality();
	int getX();
	int getY();
	unsigned short getColor();
	unsigned short getOpacity();
};

