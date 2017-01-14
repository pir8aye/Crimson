sed -i 's|Crimson-ALPHA-[0-9].[0-9].[0-9].[0-9]\+|Crimson-ALPHA-'$1'|g' /home/subterranean/Workspace/Crimson/build/platform/exe/l4j.xml
/opt/launch4j/launch4j /home/subterranean/Workspace/Crimson/build/platform/exe/l4j.xml
