# reset filesystem
rm -rf $1
d=$1/Crimson-ALPHA-$2
mkdir -p $d
mkdir -p $d/DEBIAN
mkdir -p $d/usr/share/java/crimson
mkdir -p $d/usr/bin

# create pkginfo
echo Package: crimson >> $d/DEBIAN/control
echo Version: $2 >> $d/DEBIAN/control
echo Section: base >> $d/DEBIAN/control
echo Priority: optional >> $d/DEBIAN/control
echo Architecture: any >> $d/DEBIAN/control
echo "Maintainer: Tyler Cook <admin@subterranean-security.com>" >> $d/DEBIAN/control
echo Description: Crimson >> $d/DEBIAN/control
echo " Crimson is an advanced administration tool written in Java" >> $d/DEBIAN/control

# create bin script
echo #!/bin/sh >> $d/usr/bin/crimson-server
echo exec /usr/bin/java -jar '/usr/share/java/crimson/Crimson-Server.jar' '$@' >> $d/usr/bin/crimson-server

echo #!/bin/sh >> $d/usr/bin/crimson-viewer
echo exec /usr/bin/java -jar '/usr/share/java/crimson/Crimson-Viewer.jar' '$@' >> $d/usr/bin/crimson-viewer

chmod 755 $d/usr/bin/crimson-server
chmod 755 $d/usr/bin/crimson-viewer

# copy binaries and libraries
cp /home/subterranean/Workspace/Crimson/build/bin/com/subterranean_security/cinstaller/res/bin/Crimson-Server.jar $d/usr/share/java/crimson
cp /home/subterranean/Workspace/Crimson/build/bin/com/subterranean_security/cinstaller/res/bin/Crimson-Viewer.jar $d/usr/share/java/crimson
cp /home/subterranean/Workspace/Crimson/build/bin/com/subterranean_security/cinstaller/res/bin/lib.zip $d/usr/share/java/crimson
cd $d/usr/share/java/crimson
unzip -q lib.zip
rm lib.zip

cd $1
dpkg-deb --build Crimson-ALPHA-$2

cp Crimson-ALPHA-$2.deb $3/