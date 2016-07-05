#include "JNIManager.h"

#pragma comment(lib, "jvm.lib")

JavaVM *jvm = nullptr;
JNIEnv *env = nullptr;
jclass nativeCls;
jmethodID callback_remote_moveRect;
jmethodID callback_remote_dirtyRect;

void initJNIManager() {
	//TODO debug
	//create jvm
	JavaVMInitArgs vm_args;                        // Initialization arguments
	JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
	options[0].optionString = "-Djava.class.path=."; // where to find java .class
	vm_args.version = JNI_VERSION_1_6;             // minimum Java version
	vm_args.nOptions = 1;                          // number of options
	vm_args.options = options;
	vm_args.ignoreUnrecognized = false; // invalid options make the JVM init fail
	//=============== load and initialize Java VM and JNI interface =============
	jint rc = JNI_CreateJavaVM(&jvm, (void**) &env, &vm_args);
	delete options;

	//jvm->AttachCurrentThread((void**) &env, &args);

	nativeCls = env->FindClass(
			"com/subterranean_security/crimson/core/util/Native");
	if (nativeCls == nullptr) {
		std::cout << "Failed to find Native class" << std::endl;
		return;
	}
	callback_remote_moveRect = env->GetStaticMethodID(nativeCls,
			"callback_remote_moveRect", "(IIIIII)V");
	callback_remote_dirtyRect = env->GetStaticMethodID(nativeCls,
			"callback_remote_dirtyRect", "(IIII[B)V");

}

void sendFrameJNI(THREAD_DATA* TData, FRAME_DATA *CurrentData) {
	if (!env) {
		initJNIManager();
	}

	if (!CurrentData->FrameInfo.TotalMetadataBufferSize) {
		std::cout << "No update needed" << std::endl;
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
	context->Map(ppTexture2D, 0, D3D11_MAP_READ, 0, &res);

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

		std::cout << "Processing dirty rect with coordinates: left: "
				<< rect->left << " right: " << rect->right << " top: "
				<< rect->top << " bottom: " << rect->bottom << std::endl;

		UINT dataSize = 128;
		// UINT dataSize = 4 * (rect->right - rect->left) * (rect->bottom - rect->top)
		std::cout << "Creating java array with size: " << dataSize << std::endl;
		jbyteArray data = env->NewByteArray(dataSize);

		std::cout << "Setting byte region" << std::endl;
		env->SetByteArrayRegion(data, 0, dataSize, (jbyte*) res.pData);

		std::cout << "Calling Java method" << std::endl;
		env->CallStaticVoidMethod(nativeCls, callback_remote_dirtyRect,
				rect->left, rect->top, rect->right - rect->left,
				rect->top - rect->bottom, data);
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
