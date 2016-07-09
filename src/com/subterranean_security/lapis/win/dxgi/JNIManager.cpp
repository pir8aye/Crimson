#include "JNIManager.h"

#pragma comment(lib, "jvm.lib")

JavaVM *jvm = nullptr;
JNIEnv *env = nullptr;
jclass nativeCls;
jmethodID callback_remote_moveRect;
jmethodID callback_remote_dirtyRect;

void initJNIManager(JNIEnv *e) {
	std::cout << "initializing JNIManager" << std::endl;
	e->GetJavaVM(&jvm);
}

void initEnv() {
	std::cout << "Initializing JNIEnv" << std::endl;
	JavaVMAttachArgs args;
	args.version = JNI_VERSION_1_8; // choose your JNI version
	args.name = NULL; // you might want to give the java thread a name
	args.group = NULL; // you might want to assign the java thread to a ThreadGroup
	jvm->AttachCurrentThread((void**) &env, &args);

	std::cout << "Locating Java methods" << std::endl;
	nativeCls = env->FindClass(
			"com/subterranean_security/crimson/core/util/Native");
	if (nativeCls == nullptr) {
		std::cout << "Failed to find Native class" << std::endl;
		return;
	}
	callback_remote_moveRect = env->GetStaticMethodID(nativeCls,
			"callback_remote_moveRect", "(IIIIII)V");
	callback_remote_dirtyRect = env->GetStaticMethodID(nativeCls,
			"callback_remote_dirtyRect", "(IIII[I)V");
	std::cout << "Initialized JNIEnv" << std::endl;

}

void sendFrameJNI(THREAD_DATA* TData, FRAME_DATA *CurrentData) {
	if (!env) {
		initEnv();
	}

	if (!CurrentData->FrameInfo.TotalMetadataBufferSize) {
		return;
	}

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
	HRESULT hr = context->Map(ppTexture2D, 0, D3D11_MAP_READ, 0, &res);
	if (!SUCCEEDED(hr)) {
		std::cout << "MAP FAILED :(" << std::endl;
	}

	if (CurrentData->MoveCount) {
		std::cout << "Processing moves" << std::endl;
		DXGI_OUTDUPL_MOVE_RECT* moveBuffer =
				reinterpret_cast<DXGI_OUTDUPL_MOVE_RECT*>(CurrentData->MetaData);
		for (UINT i = 0; i < CurrentData->MoveCount; ++i) {
			DXGI_OUTDUPL_MOVE_RECT* rect = &(moveBuffer[i]);
			env->CallStaticVoidMethod(nativeCls, callback_remote_moveRect,
					rect->SourcePoint.x, rect->SourcePoint.y,
					rect->DestinationRect.left, rect->DestinationRect.top,
					rect->DestinationRect.right - rect->DestinationRect.left,
					rect->DestinationRect.top - rect->DestinationRect.bottom);
		}
	}

	if (CurrentData->DirtyCount) {

		RECT* rect = reinterpret_cast<RECT*>(CurrentData->MetaData
				+ (CurrentData->MoveCount * sizeof(DXGI_OUTDUPL_MOVE_RECT)));

		UINT dataSize = (rect->right - rect->left) * (rect->bottom - rect->top);
		jintArray data = env->NewIntArray(dataSize);

		int *rand = static_cast<int*>(res.pData);
		int sum = 0;
		for (int i = 0; i < dataSize; i++) {
			sum += rand[i];
		}
		std::cout << "sum: " << sum << std::endl;

		env->SetIntArrayRegion(data, 0, dataSize, (jint*) res.pData);

		env->CallStaticVoidMethod(nativeCls, callback_remote_dirtyRect,
				rect->left, rect->top, rect->right - rect->left,
				rect->bottom - rect->top, data);

	}

//	struct Color {
//		float r, g, b, a;
//	};
//	Color* obj;
//	obj = new Color[(res.RowPitch / sizeof(Color)) * ppDesc.Height];
//	memcpy(obj, res.pData, res.RowPitch * ppDesc.Height);

// unmap
	context->Unmap(ppTexture2D, 0);
}
