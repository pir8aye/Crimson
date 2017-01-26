let itemsCount=$(xmllint --xpath 'count(//Lib/Requisites)' /home/subterranean/Workspace/Crimson/src/com/subterranean_security/crimson/universal/Dependancies.xml)
declare -a description=( )

total=0

for (( i=1; i <= $itemsCount; i++ )); do
	if [[ "$(xmllint --xpath '//Lib['$i']/Requisites' /home/subterranean/Workspace/Crimson/src/com/subterranean_security/crimson/universal/Dependancies.xml)" == *"C"* ]]
	then
		((i=i-1))
		if [ "$i" -lt "10" ]; then
			total=$((${total} + $(stat -c%s /home/subterranean/Workspace/Crimson/lib/java/c0${i}.jar)))
		else
			total=$((${total} + $(stat -c%s /home/subterranean/Workspace/Crimson/lib/java/c${i}.jar)))
		fi
  		((i=i+1))
	fi

done
	
echo $total
