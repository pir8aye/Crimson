set CL32="C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\bin\cl"
set CL64="C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\bin\amd64\cl"

set I1="C:\Users\dev\Desktop\lapis\include"
set I2="C:\Program Files\Java\jdk1.8.0_91\include"
set I3="C:\Program Files\Java\jdk1.8.0_91\include\win32"

set I4="C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\include"
set I5="C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\atlmfc\include"
set I6="C:\Program Files (x86)\Windows Kits\10\Include\10.0.10240.0\ucrt"
set I7="C:\Program Files (x86)\Windows Kits\8.1\Include\um"
set I8="C:\Program Files (x86)\Windows Kits\8.1\Include\shared"
set I9="C:\Program Files (x86)\Windows Kits\8.1\Include\winrt"


cd C:\Users\dev\Desktop\lapis

#%CL32% /I %I1% /I %I2% /I %I3% /I %I4% /I %I5% /I %I6% /I %I7% /I %I8% /I %I9% /D_USRDLL /D_WINDLL C:\Users\dev\Desktop\lapis\win\*.cpp /link /LIBPATH:"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\lib" /LIBPATH:"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\atlmfc\lib" /LIBPATH:"C:\Program Files (x86)\Windows Kits\10\lib\10.0.10240.0\ucrt\x86" /LIBPATH:"C:\Program Files (x86)\Windows Kits\8.1\lib\winv6.3\um\x86" /LIBPATH:"C:\Program Files (x86)\Windows Kits\NETFXSDK\4.6.1\Lib\um\x86" /DEF:win\lapis.def /DLL /OUT:crimson32.dll

%CL64% /I %I1% /I %I2% /I %I3% /I %I4% /I %I5% /I %I6% /I %I7% /I %I8% /I %I9% /D_USRDLL /D_WINDLL C:\Users\dev\Desktop\lapis\win\*.cpp C:\Users\dev\Desktop\lapis\win\dxgi\*.cpp /link /LIBPATH:"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\lib\amd64" /LIBPATH:"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\atlmfc\lib\amd64" /LIBPATH:"C:\Program Files (x86)\Windows Kits\10\lib\10.0.10240.0\ucrt\x64" /LIBPATH:"C:\Program Files (x86)\Windows Kits\8.1\lib\winv6.3\um\x64" /LIBPATH:"C:\Program Files (x86)\Windows Kits\NETFXSDK\4.6.1\Lib\um\x64" /DEF:win\lapis.def /DLL /OUT:crimson64.dll
