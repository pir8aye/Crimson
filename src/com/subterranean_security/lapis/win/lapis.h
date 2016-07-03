#ifndef LAPIS_H
#define LAPIS_H

#include <com_subterranean_security_crimson_core_util_Native.h>
#include <windows.h>
#include <winuser.h>
#include <comdef.h>
#include <Wbemidl.h>
#include <cstdio>
#include <iostream>
#include <memory>
#include <regex>
#include <string>

jclass nativeCls;
jmethodID sendFrameMethod;

void sendFrame();

#endif
