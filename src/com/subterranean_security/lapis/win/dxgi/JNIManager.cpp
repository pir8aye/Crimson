#include "CommonTypes.h"
#include "../lapis.h"

void sendFrameJNI(THREAD_DATA* TData, FRAME_DATA *CurrentData) {
	//new ID3D11Texture2D
	ID3D11Texture2D *ppTexture2D;

	// get context
	ID3D11DeviceContext *context;
	std::cout << TData->DxRes.Device->GetImmediateContext(&context)
			<< std::endl;

	// get description of original texture
	D3D11_TEXTURE2D_DESC ppDesc;
	std::cout << CurrentData->Frame->GetDesc(&ppDesc) << std::endl;

	//modify original description
	ppDesc.Usage = D3D11_USAGE_STAGING;
	ppDesc.BindFlags = 0;

	// get ID3D11Device from thread data and create texture
	std::cout
			<< TData->DxRes.Device->CreateTexture2D(&ppDesc, NULL, &ppTexture2D)
			<< std::endl;

	// perform copy
	std::cout << context->CopyResource(CurrentData->Frame, ppTexture2D)
			<< std::endl;

	// get mapped subresource
	D3D11_MAPPED_SUBRESOURCE res;
	std::cout << context->Map(ppTexture2D, 0, D3D11_MAP_READ, 0, &res)
			<< std::endl;

	std::cout << "Subresource RowPitch: " << res.RowPitch << " DepthPitch"
			<< res.DepthPitch << std::endl;

	//byte[] data = new byte[textureDesc.Width * textureDesc.Height * 3 * sizeof(byte)];
	//memcpy(data, mapResource.pData, this->size);

	// unmap
	context->Unmap(ppTexture2D, 0);
}
