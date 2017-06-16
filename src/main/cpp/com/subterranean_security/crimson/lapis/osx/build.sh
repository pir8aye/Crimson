
export I1=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home/include
export I2=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home/include/darwin
export I3=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home/jre/lib/server/
export I4=/Users/dev/lapis/include

cd /Users/dev/lapis

g++ -I$I1 -I$I2 -I$I3 -I$I4 -dynamiclib -o libcrimson64.dylib -fPIC lapis.cpp

#g++ -I$I1 -I$I2 -I$I3 -m32 -dynamiclib -o libcrimson32.dylib -fPIC lapis.cpp
