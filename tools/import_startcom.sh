#/bin/bash
if [ -n "$1" ]; then
	if [ $1 == "-h" ] || [ $1 == "--help" ]; then
		cat <<-EOF
1. $0
2. $0 [CERTFILE] 
3. $0 [CERTFILE] [KEYSTORE]

1: automatic mode. $(basename $0) will automatically determine all nescessary data.
2: semiautomatic mode. $(basename $0) will automatically determine the cacerts keystore.
3: manual mode. $(basename $0) will use the certfile and keystore you specified. 
		EOF
		exit 0
	fi
	
	CERTFILE="$1"
	if [ ! -f "$CERTFILE" ]; then
		echo "Path to certification authority certificate is wrong: $CERTFILE"
		exit 1
	fi
else
	CERTFILE="/usr/share/ca-certificates/mozilla/StartCom_Certification_Authority.crt"
fi

if [ ! -f "$CERTFILE" ]; then
	CERTFILE=$(locate StartCom | grep Certification | grep Authority | grep \.crt | head -n1)
fi

if [ -z $CERTFILE ]; then
	echo "Could not locate cert file StartCom Certification Authority. Aborting..."
	exit 1
fi

echo "Using cert file: $CERTFILE" 

TMP=$(mktemp keystore.XXXXXXXX)

SEQUENCE=""
HEAD=$(which head)
TAIL=$(which tail)
KEYTOOL=$(which keytool)


if [ -n "$2" ]; then
	if [ -f "$2" ]; then
		echo "Using keystore $2."
	else
		echo "Keystore file invalid. Aborting..."
		exit 1
	fi
fi
	
if uname | grep -q Darwin; then
	echo "Detected OSX based system."
	LINK="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK"
	set NEXT
	while NEXT=$(readlink "${LINK}"); do 
		if [ ${NEXT::1} = "/" ]; then
			LINK="$NEXT"
		else
			LINK="$(dirname "${LINK}")/$NEXT"
		fi
	done
	
	if [ -f "${LINK}/HOME/lib/security/cacerts" ]; then
		echo "${LINK}/HOME/lib/security/cacerts" >> "${TMP}"
	else
		echo "Could not determine active java home. Trying to locate..."
		locate cacerts | grep 'Home/lib/security/cacerts' | grep jdk | egrep '^/System/Library/' >> "${TMP}"
		if [ $? -ne 0 ]; then
			echo "Could not locate active java installation. Aborting..."
			exit 1
		fi
	fi
else
	echo "Detected Linux based system."
	if [ -n $JAVA_HOME ] && [ -f "${JAVA_HOME}/jre/lib/security/cacerts" ]; then
		echo "${JAVA_HOME}/jre/lib/security/cacerts" >>"${TMP}"
	else
		locate cacerts | grep '/jre/lib/security/cacerts' | grep jdk >>"$TMP"
	fi
fi

echo "Found active java installation: "

COUNT=$(wc -l "$TMP" | awk '{ print $1; }')
if uname | grep -q Darwin; then
	SEQUENCE=$(jot $COUNT 1 $COUNT)
else
	SEQUENCE=$(seq 1 $COUNT)	
fi

while [ -z $ANS ]; do
	if [ $(wc -l "${TMP}" | awk '{ print $1; }') -gt 1 ]; then
		for i in $SEQUENCE; do
			STOREPATH=$(head -n$i "$TMP" | tail -n1)
			echo "$i] $STOREPATH"
		done
		echo "a] all"
		
		read -p "Chose a keystore to use: " ANS
		
		if [ $ANS == "a" ]; then
			echo "Using all."
		elif [[ $ANS =~ [1-9][0-9]* ]] && [ $ANS -le $COUNT ] && [ $ANS -gt 0 ]; then
			STOREPATH=$(head -n$ANS "$TMP" | tail -n1)
			echo "$STOREPATH" >"${TMP}"
		else
			echo "Invalid choice."
			unset ANS
		fi
	else
		cat "${TMP}"
		read -p "Use this keystore? [y|n] " ANS
		if [[ $ANS =~ [yY](es)? ]]; then
			echo "Ok."
		else
			echo "Aborting..."
			exit 1
		fi 
	fi
done


SUDO=""
if [[ $UID -ne 0 ]]; then
	echo "Using sudo to gather root privileges."
	SUDO="sudo "
fi

for i in $SEQUENCE; do
	STOREPATH=$(head -n$i "$TMP" | tail -n1)
	echo "${SUDO}keytool -import -alias StartCom_Certification_Authority -keystore \"$STOREPATH\" -trustcacerts -file \"$CERTFILE\" -keypass changeit -storepass changeit"
	${SUDO}keytool -import -alias StartCom_Certification_Authority -keystore "$STOREPATH" -trustcacerts -file "$CERTFILE" -keypass changeit -storepass changeit
done

rm -f "$TMP"