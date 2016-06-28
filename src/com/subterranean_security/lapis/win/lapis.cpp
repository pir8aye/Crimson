#include <windows.h>
#include <winuser.h>
#include <comdef.h>
#include <Wbemidl.h>
#include <cstdio>
#include <iostream>
#include <memory>
#include <regex>
#include <string>
#include <com_subterranean_security_crimson_core_util_Native.h>

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

HRESULT GetCpuTemperature(LPLONG pTemperature) {
	if (pTemperature == NULL)
		return E_INVALIDARG;

	*pTemperature = -1;
	HRESULT ci = CoInitialize(NULL); // needs comdef.h
	HRESULT hr = CoInitializeSecurity(NULL, -1, NULL, NULL,
			RPC_C_AUTHN_LEVEL_DEFAULT, RPC_C_IMP_LEVEL_IMPERSONATE, NULL,
			EOAC_NONE, NULL);
	if (SUCCEEDED(hr)) {
		IWbemLocator *pLocator; // needs Wbemidl.h & Wbemuuid.lib
		hr = CoCreateInstance(CLSID_WbemAdministrativeLocator, NULL,
				CLSCTX_INPROC_SERVER, IID_IWbemLocator, (LPVOID*) &pLocator);
		if (SUCCEEDED(hr)) {
			IWbemServices *pServices;
			BSTR ns = SysAllocString(L"root\\WMI");
			hr = pLocator->ConnectServer(ns, NULL, NULL, NULL, 0, NULL, NULL,
					&pServices);
			pLocator->Release();
			SysFreeString(ns);
			if (SUCCEEDED(hr)) {
				BSTR query = SysAllocString(
						L"SELECT * FROM MSAcpi_ThermalZoneTemperature");
				BSTR wql = SysAllocString(L"WQL");
				IEnumWbemClassObject *pEnum;
				hr = pServices->ExecQuery(wql, query,
						WBEM_FLAG_RETURN_IMMEDIATELY | WBEM_FLAG_FORWARD_ONLY,
						NULL, &pEnum);
				SysFreeString(wql);
				SysFreeString(query);
				pServices->Release();
				if (SUCCEEDED(hr)) {
					IWbemClassObject *pObject;
					ULONG returned;
					hr = pEnum->Next(WBEM_INFINITE, 1, &pObject, &returned);
					pEnum->Release();
					if (SUCCEEDED(hr)) {
						BSTR temp = SysAllocString(L"CurrentTemperature");
						VARIANT v;
						VariantInit(&v);
						hr = pObject->Get(temp, 0, &v, NULL, NULL);
						pObject->Release();
						SysFreeString(temp);
						if (SUCCEEDED(hr)) {
							*pTemperature = V_I4(&v);
						}
						VariantClear(&v);
					}
				}
			}
			if (ci == S_OK) {
				CoUninitialize();
			}
		}
	}
	return hr;
}

JNIEXPORT jlong JNICALL Java_com_subterranean_1security_crimson_core_util_Native_getCpuTemp(
		JNIEnv *env, jclass cls) {
	LONG temp;
	HRESULT hr = GetCpuTemperature(&temp);
	return temp;
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

void GetJStringContent(JNIEnv *AEnv, jstring AStr, std::string &ARes) {
	if (!AStr) {
		ARes.clear();
		return;
	}

	const char *s = AEnv->GetStringUTFChars(AStr, NULL);
	ARes = s;
	AEnv->ReleaseStringUTFChars(AStr, s);
}

JNIEXPORT jstring JNICALL Java_com_subterranean_1security_crimson_core_util_Native_execute(
		JNIEnv *env, jclass cls, jstring cmd) {

	std::string str;
	GetJStringContent(env, cmd, str);
	return env->NewStringUTF(exec(str.c_str()).c_str());

}
