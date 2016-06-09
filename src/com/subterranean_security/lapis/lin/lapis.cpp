#include <jni.h>
#include <com_subterranean_security_crimson_core_util_Native.h>

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getActiveWindow(
		JNIEnv *env, jclass cls) {
	char title[256];
	return env->NewStringUTF(title);
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getSystemUptime(
		JNIEnv *env, jclass cls) {
	return 0;
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getCpuTemp(
		JNIEnv *env, jclass cls) {
	return 0;
}
