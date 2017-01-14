let itemsCount=$(xmllint --xpath 'count(//Lib/Requisites)' /home/subterranean/Workspace/Crimson/src/com/subterranean_security/crimson/nucleus/Dependancies.xml)
declare -a description=( )

cp=""

for (( i=1; i <= $itemsCount; i++ )); do
	if [[ "$(xmllint --xpath '//Lib['$i']/Requisites' /home/subterranean/Workspace/Crimson/src/com/subterranean_security/crimson/nucleus/Dependancies.xml)" == *"${1}"* ]]
	then
		((i=i-1))
		if [ "$i" -lt "10" ]; then
			cp="${cp} lib/java/c0${i}.jar"
		else
			cp="${cp} lib/java/c${i}.jar"
		fi
		((i=i+1))
  		
	fi

done
	
echo $cp