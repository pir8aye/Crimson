# reset filesystem
rm -rf $1
mkdir $1
mkdir -p $1/usr/share/java/crimson
mkdir -p $1/usr/bin

# create pkginfo
echo pkgname = crimson >> $1/.PKGINFO
echo pkgver = $2 >> $1/.PKGINFO
echo pkgdesc = Crimson is an advanced administration tool written in Java >> $1/.PKGINFO
echo url = https://github.com/Subterranean-Security/Crimson >> $1/.PKGINFO
echo license = APACHE >> $1/.PKGINFO
echo arch = any >> $1/.PKGINFO

# create bin script
echo #!/bin/sh >> $1/usr/bin/crimson-server
echo exec /usr/bin/java -jar '/usr/share/java/crimson/Crimson-Server.jar' '$@' >> $1/usr/bin/crimson-server

echo #!/bin/sh >> $1/usr/bin/crimson-viewer
echo exec /usr/bin/java -jar '/usr/share/java/crimson/Crimson-Viewer.jar' '$@' >> $1/usr/bin/crimson-viewer

chmod 755 $1/usr/bin/crimson-server
chmod 755 $1/usr/bin/crimson-viewer

# install binaries and libraries

java -jar /home/subterranean/Storage/Virtualbox/Shared/dist/CInstaller.jar install $1/usr/share/java/crimson

sleep 3

cd $1
tar -cf - .PKGINFO * | xz -c -z - > $3/Crimson-ALPHA-$2.pkg.tar.xz
