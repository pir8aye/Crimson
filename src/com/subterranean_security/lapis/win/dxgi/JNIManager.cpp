#include "JNIManager.h"

JNIEnv *env;
jclass nativeCls;
jmethodID sendFrameMethod;

void initJNIManager(JNIEnv *e) {
	// setup calls to jvm
	env = e; //TODO check
	nativeCls = env->FindClass(
			"com/subterranean_security/crimson/core/util/Native");
	if (nativeCls == nullptr) {
		std::cout << "Failed to find Native class" << std::endl;
		return;
	}
	sendFrameMethod = env->GetStaticMethodID(nativeCls, "callback_sendFrame",
			"([B)V");

}

void sendFrameJNI(THREAD_DATA* TData, FRAME_DATA *CurrentData) {
	//new ID3D11Texture2D
	ID3D11Texture2D *ppTexture2D;

	// get context
	ID3D11DeviceContext *context;
	TData->DxRes.Device->GetImmediateContext(&context);

	// get description of original texture
	D3D11_TEXTURE2D_DESC ppDesc;
	CurrentData->Frame->GetDesc(&ppDesc);

	//modify original description
	ppDesc.Usage = D3D11_USAGE_STAGING;
	ppDesc.BindFlags = 0;
	ppDesc.CPUAccessFlags = D3D11_CPU_ACCESS_READ;

	// get ID3D11Device from thread data and create texture
	TData->DxRes.Device->CreateTexture2D(&ppDesc, NULL, &ppTexture2D);

	// perform copy
	context->CopyResource(CurrentData->Frame, ppTexture2D);

	// get mapped subresource
	D3D11_MAPPED_SUBRESOURCE res;
	context->Map(ppTexture2D, 0, D3D11_MAP_READ, 0, &res);

	std::cout << "Subresource RowPitch: " << res.RowPitch << " DepthPitch"
			<< res.DepthPitch << std::endl;

	jbyteArray data = env->NewByteArray(100);
	env->SetByteArrayRegion(data, 0, 100, (jbyte*) res.pData);

	struct Color {
		float r, g, b, a;
	};
	Color* obj;
	obj = new Color[(res.RowPitch / sizeof(Color)) * ppDesc.Height];
	memcpy(obj, res.pData, res.RowPitch * ppDesc.Height);

	env->CallStaticVoidMethod(nativeCls, sendFrameMethod, data);

	// unmap
	context->Unmap(ppTexture2D, 0);
}
