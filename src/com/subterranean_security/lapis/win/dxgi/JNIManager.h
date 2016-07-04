#ifndef _JNIMANAGER_H_
#define _JNIMANAGER_H_

#include "CommonTypes.h"
#include "../lapis.h"

void initJNIManager(JNIEnv *env);
void sendFrameJNI(THREAD_DATA* TData, FRAME_DATA *CurrentData);

#endif
