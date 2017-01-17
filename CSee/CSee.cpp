/*
 * CSee.cpp
 *
 *  Created on: Oct 28, 2016
 *      Author: team4141
 */



#include "CSee.h"
#include "team4141_robotvision_msee_CSee.h"


//Globals
// cached refs for later callbacks

jobject g_obj;
JavaVM* jvm;
std::map<int,cv::VideoCapture> cameras;

JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_init (JNIEnv *env, jobject jObj){
	//convert local to global reference
	g_obj = env->NewGlobalRef(jObj);
	env->GetJavaVM(&jvm);

//	std::cout << "JNI globals cached" << std::endl;


	JNIEnv* myNewEnv;
	jint getEnvStat = jvm->GetEnv((void**)&myNewEnv, JNI_VERSION_1_8);

	if(getEnvStat == JNI_EDETACHED){
		std::cout << "GetEnv: not attached" << std::endl;
		if(jvm->AttachCurrentThread((void **)&myNewEnv,NULL)!=0){
			std::cout << "Failed to attach" << std::endl;
		}
	}
	else if(getEnvStat == JNI_OK){
//		std::cout << "GetEnv: JNI OK" << std::endl;
	}
	else if(getEnvStat == JNI_EVERSION){
		std::cout << "GetEnv: JNI version not supported" << std::endl;
	}

//	save refs for callback
	jclass g_class = myNewEnv->GetObjectClass(jObj);
	if(g_class == NULL){
		std::cout << "Failed to find class" << std::endl;
	}

	jmethodID g_mid = myNewEnv->GetMethodID(g_class,"onInitialized", "()V");
	if(g_mid == NULL){
		std::cout << "Unable to get method ref" << std::endl;
	}

	CSee::disvocerCameras();

	myNewEnv->CallVoidMethod(g_obj, g_mid);

	if(myNewEnv->ExceptionCheck()){
		myNewEnv->ExceptionDescribe();
	}
}


JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_start
  (JNIEnv *, jobject){
	std::cout<<"start streaming"<<std::endl;
}

JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_stop
  (JNIEnv *, jobject){
	std::cout<<"stop streaming"<<std::endl;
	for(std::map<int,cv::VideoCapture>::const_iterator ct = cameras.begin(); ct != cameras.end(); ++ct) {
		if(ct->second.isOpened()){
			cameras[ct->first].release();
		}
	}
}

JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_switchTo
  (JNIEnv * env, jobject jObj, jstring jStreamName){
	const char *resultCStr = env->GetStringUTFChars(jStreamName, NULL);
	std::string streamName(resultCStr);
	env->ReleaseStringUTFChars(jStreamName, resultCStr);
	std::cout<<"switch to "<< streamName << std::endl;
}

JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_setCameraProperty
  (JNIEnv * env, jobject jObj, jint cameraId, jint propertyId, jstring jValue){
	const char *resultCStr = env->GetStringUTFChars(jValue, NULL);
	double value;
	int key = (int) cameraId;
	if ( cameras.find(key) == cameras.end() ) {
	  // not found
	} else {
		cv::VideoCapture cap = cameras[key];
		if((int)propertyId == cv::CAP_PROP_FOURCC){
			value = cv::VideoWriter::fourcc(resultCStr[0],resultCStr[1],resultCStr[2],resultCStr[3]);
			std::cout<<"setting camera[" << (int)cameraId << "]: "<< (int)propertyId <<" to "<< CSee::decodeFourCC((int)value) << std::endl;
		}
		else{
			value = std::atof(resultCStr);
			std::cout<<"setting camera[" << (int)cameraId << "]: "<< (int)propertyId <<" to "<< std::to_string(value) << std::endl;
		}
		cap.set((int)propertyId,value);
	}
	env->ReleaseStringUTFChars(jValue, resultCStr);
}

JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_setFilter
  (JNIEnv * env, jobject jObj, jstring jStreamName, jstring jFilterName){
	const char *resultCStr = env->GetStringUTFChars(jStreamName, NULL);
	std::string streamName(resultCStr);
	env->ReleaseStringUTFChars(jStreamName, resultCStr);

	resultCStr = env->GetStringUTFChars(jFilterName, NULL);
	std::string filterName(resultCStr);
	env->ReleaseStringUTFChars(jFilterName, resultCStr);

	std::cout<<"setting " << streamName << " to "<< filterName << std::endl;

}


JNIEXPORT jstring JNICALL Java_team4141_robotvision_msee_CSee_getConfig
  (JNIEnv *env, jobject jObj){
	//convert local to global reference
	g_obj = env->NewGlobalRef(jObj);
	env->GetJavaVM(&jvm);

//	std::cout << "JNI globals cached" << std::endl;


	JNIEnv* myNewEnv;
	jint getEnvStat = jvm->GetEnv((void**)&myNewEnv, JNI_VERSION_1_8);

	if(getEnvStat == JNI_EDETACHED){
		std::cout << "GetEnv: not attached" << std::endl;
		if(jvm->AttachCurrentThread((void **)&myNewEnv,NULL)!=0){
			std::cout << "Failed to attach" << std::endl;
		}
	}
	else if(getEnvStat == JNI_OK){
//		std::cout << "GetEnv: JNI OK" << std::endl;
	}
	else if(getEnvStat == JNI_EVERSION){
		std::cout << "GetEnv: JNI version not supported" << std::endl;
	}

	Poco::JSON::Object cameraConfig(true);

	for(std::map<int,cv::VideoCapture>::const_iterator ct = cameras.begin(); ct != cameras.end(); ++ct) {
		if(ct->second.isOpened()){
			cv::VideoCapture cap = cameras[ct->first];
			Poco::JSON::Object camera(true);
			camera.set("id",ct->first);
			for(std::map<int,std::string>::const_iterator it = CSee::cameraPropertyNames.begin(); it != CSee::cameraPropertyNames.end(); ++it) {
				double val = cap.get(it->first);
				if(val > 0){
					std::string key = it->second;
					std::string result;
					if(it->first == cv::CAP_PROP_FOURCC) {
						result = CSee::decodeFourCC((int)val);
					}
					else{
						result = std::to_string(val);
					}
					camera.set(key,CSee::makeCameraPropertyNode(it->first,key,result));
				}
			}
			cameraConfig.set("cap"+std::to_string(ct->first),camera);
		}
	}

	std::stringstream data;
	cameraConfig.stringify(data);
	//	 Construct a String
	jstring jstr = myNewEnv->NewStringUTF(data.str().c_str());
	return jstr;
}


std::map<int, std::string> CSee::createPropertyNames() {
	std::map<int, std::string> names;
//	names[cv::CAP_PROP_GUID]="CAP_PROP_GUID";
//	names[cv::CAP_PROP_SETTINGS]="CAP_PROP_SETTINGS";
	names[cv::CAP_PROP_FOURCC]="CAP_PROP_FOURCC";
	names[cv::CAP_PROP_FRAME_WIDTH]="CAP_PROP_FRAME_WIDTH";
	names[cv::CAP_PROP_FRAME_HEIGHT]="CAP_PROP_FRAME_HEIGHT";
//	names[cv::CAP_PROP_GAIN]="CAP_PROP_GAIN";
//	names[cv::CAP_PROP_GAMMA]="CAP_PROP_GAMMA";
	names[cv::CAP_PROP_SATURATION]="CAP_PROP_SATURATION";
//	names[cv::CAP_PROP_SHARPNESS]="CAP_PROP_SHARPNESS";
	names[cv::CAP_PROP_FPS]="CAP_PROP_FPS";
//	names[cv::CAP_PROP_POS_FRAMES]="CAP_PROP_POS_FRAMES";
	names[cv::CAP_PROP_POS_MSEC]="CAP_PROP_POS_MSEC";
//	names[cv::CAP_PROP_APERTURE]="CAP_PROP_APERTURE";
//	names[cv::CAP_PROP_AUTOFOCUS]="CAP_PROP_AUTOFOCUS";
//	names[cv::CAP_PROP_BACKLIGHT]="CAP_PROP_BACKLIGHT";
//	names[cv::CAP_PROP_BUFFERSIZE]="CAP_PROP_BUFFERSIZE";
//	names[cv::CAP_PROP_FOCUS]="CAP_PROP_FOCUS";
//	names[cv::CAP_PROP_IRIS]="CAP_PROP_IRIS";
//	names[cv::CAP_PROP_ISO_SPEED]="CAP_PROP_ISO_SPEED";
	return names;
}




void CSee::disvocerCameras(){
	for(int i=0;i<10;i++){
		cv::VideoCapture cap(i);
		if(cap.isOpened()){
			cameras[i]=cap;
		}
	}
}

std::string CSee::decodeFourCC(int fourcc){
	char c1 = (char)(fourcc & 0xFF);
	char c2 = (char)((fourcc & 0xFF00) >> 8);
	char c3 = (char)((fourcc & 0xFF0000) >> 16);
	char c4 = (char)((fourcc & 0xFF000000) >> 24);
	char u_array[4] = {c1,c2,c3,c4};
	return std::string(u_array,sizeof u_array / sizeof u_array[0]);
}

Poco::JSON::Object CSee::makeCameraPropertyNode(int propertyId, std::string& key, std::string& value){
	Poco::JSON::Object node(true);
	node.set("id",propertyId);
	node.set("name",key);
	node.set("value",value);
	return node;
}
