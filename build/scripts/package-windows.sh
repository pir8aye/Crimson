sed -i 's|[0-9].[0-9].[0-9].[0-9]\+|'$1'|g' /home/subterranean/Workspace/Crimson/build/platform/exe/l4j.xml
/opt/launch4j/launch4j /home/subterranean/Workspace/Crimson/build/platform/exe/l4j.xml
