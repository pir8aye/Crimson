let itemsCount=$(xmllint --xpath 'count(//Lib/Requisites)' /home/subterranean/Workspace/Crimson/src/com/subterranean_security/crimson/nucleus/Dependancies.xml)
declare -a description=( )

total=0

for (( i=0; i <= $itemsCount; i++ )); do
	if [[ "$(xmllint --xpath '//Lib['$i']/Requisites' /home/subterranean/Workspace/Crimson/src/com/subterranean_security/crimson/nucleus/Dependancies.xml)" == *"C"* ]]
	then
		if [ "$i" -lt "10" ]; then
			total=$((${total} + $(stat -c%s /home/subterranean/Workspace/Crimson/lib/java/c0${i}.jar)))
		else
			total=$((${total} + $(stat -c%s /home/subterranean/Workspace/Crimson/lib/java/c${i}.jar)))
		fi
  		
	fi

done
	
echo $total
