#include <jni.h>

#include <linux/unistd.h>
#include <linux/kernel.h>
#include <sys/sysinfo.h>
#include <stdlib.h>
#include <cstdio>
#include <iostream>
#include <memory>

#include <com_subterranean_security_crimson_core_util_Native.h>

char* exec(const char* cmd, char* buffer);

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getActiveWindow(
		JNIEnv *env, jclass cls) {
	char buffer[1024];
	exec(
			"xprop -id $(xprop -root 32x \'\t$0\' _NET_ACTIVE_WINDOW | cut -f 2) _NET_WM_NAME | cut -d \\\" -f2",
			buffer);

	return env->NewStringUTF(buffer);
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getSystemUptime(
		JNIEnv *env, jclass cls) {
	struct sysinfo s_info;
	if (sysinfo(&s_info) != 0) {
		return -1;
	}
	return s_info.uptime;
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
