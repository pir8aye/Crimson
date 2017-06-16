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
			CLSCTX_INPROC_SERVER, IID_IWbemLocator, (LPVOID*)&pLocator);
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