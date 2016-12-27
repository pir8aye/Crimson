#include <jni.h>

#include <stdlib.h>
#include <cstdio>
#include <iostream>

#include <com_subterranean_security_crimson_core_util_Native.h>

char* exec(const char* cmd, char* buffer);

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getActiveWindow(
		JNIEnv *env, jclass cls) {
	char buffer[1024];

	return env->NewStringUTF(buffer);
}

// DELETE ME
JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getSystemUptime(
		JNIEnv *env, jclass cls) {
	return 0;
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getCpuTemp(
		JNIEnv *env, jclass cls) {
	return 0;
}

char* exec(const char* cmd, char* buffer) {

	FILE *lsofFile_p = popen(cmd, "r");

	if (lsofFile_p) {
		fgets(buffer, sizeof(buffer), lsofFile_p);
		pclose(lsofFile_p);
	}

	return buffer;
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_poweroff(JNIEnv *env, jclass cls) {
	system("poweroff");
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_restart(JNIEnv *env, jclass cls) {
	system("reboot");
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_standby(JNIEnv *env, jclass cls) {

}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_hibernate(JNIEnv *env, jclass cls) {

}

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_execute(
		JNIEnv *env, jclass cls, jstring cmd) {

	char buffer[128];
	exec(env->GetStringUTFChars(cmd, NULL), buffer);

	return env->NewStringUTF(buffer);
}
