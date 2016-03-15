SET G++32=C:\MinGW\bin\g++.exe
SET G++64=C:\MinGW64\mingw64\bin\g++.exe

%G++32% -c -DCRIMSON -I "C:\Users\dev\Desktop\lapis\include" -I "C:\Program Files\Java\jdk1.8.0_73\include" -I "C:\Program Files\Java\jdk1.8.0_73\include\win32" C:\Users\dev\Desktop\lapis\win\util.cpp
%G++32% -shared -o crimson32.dll util.o

%G++64% -c -DCRIMSON -I "C:\Users\dev\Desktop\lapis\include" -I "C:\Program Files\Java\jdk1.8.0_73\include" -I "C:\Program Files\Java\jdk1.8.0_73\include\win32" C:\Users\dev\Desktop\lapis\win\util.cpp
%G++64% -shared -o crimson64.dll util.o

rmdir /S /Q C:\Users\dev\Desktop\lapis