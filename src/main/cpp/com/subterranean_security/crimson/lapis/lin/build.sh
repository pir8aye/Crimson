
export I1=/usr/lib/jvm/java-1.8.0-openjdk-amd64/include
export I2=/usr/lib/jvm/java-1.8.0-openjdk-amd64/include/linux
export I3=/home/dev/lapis/include

cd /home/dev/lapis

g++ -I$I1 -I$I2 -I$I3 -shared -o libcrimson64.so -fPIC lapis.cpp

g++ -I$I1 -I$I2 -I$I3 -m32 -shared -o libcrimson32.so -fPIC lapis.cpp
