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
		std::cout << "Unable to get onInitialized() method ref" << std::endl;
	}


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

}

JNIEXPORT void JNICALL Java_team4141_robotvision_msee_CSee_switchTo
  (JNIEnv * env, jobject jObj, jstring jStreamName){
	const char *resultCStr = env->GetStringUTFChars(jStreamName, NULL);
	std::string streamName(resultCStr);
	env->ReleaseStringUTFChars(jStreamName, resultCStr);
	std::cout<<"switch to "<< streamName << std::endl;
}
