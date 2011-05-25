#!/bin/bash

function help() {
	echo $(basename $0) [TARGET_DIRECTORY] [SOURCE_REPOSITORY]
}

if [ -z $2 ]; then
	help
	exit 1
fi

TARGET_DIR=$1
SOURCE_REPO=$2

if [ -z $HGFASTEXPORT_DIR ]; then
	EXEC_DIR=$(dirname $0)
	if [ ${EXEC_DIR::1} != "/" ]; then
		EXEC_DIR=$(dirname "$PWD/$0")
	fi
	
	if [ -d "${EXEC_DIR}/libs/hg-fast-export" ] && [ -f "${EXEC_DIR}/libs/hg-fast-export/hg-fast-export.sh" ]; then
		export HGFASTEXPORT_DIR="${EXEC_DIR}/libs/hg-fast-export"
	else
		echo "error: Couldn't find hg-fast-export script at: ${EXEC_DIR}/libs/hg-fast-export/hg-fast-export.sh"
		exit 1
	fi
else
	if [ ! -f "${EXEC_DIR}/libs/hg-fast-export/hg-fast-export.sh" ]; then
		echo "error: Couldn't find hg-fast-export script at: ${EXEC_DIR}/libs/hg-fast-export/hg-fast-export.sh"
		exit 1
	fi
fi

OLDPWD="$PWD"

mkdir -p "${TARGET_DIR}"
cd "${TARGET_DIR}"
git init --bare 

bash "${HGFASTEXPORT_DIR}"/hg-fast-export.sh -r "${SOURCE_REPO}"

if [ $? -ne 0 ]; then
	echo "Converting failed with exit code $?."
	exit 1
fi

while [ -z $ANS ]; do
	read -p "Computing deltas and compressing packages. Shall I use aggressive mode? (this takes upto 3G RAM and several minutes to compute) [y|n] " ANS
	case $ANS in
		y|Y) GC_OPTIONS="--aggressive"
			;;
		n|N) GC_OPTIONS=""
			;;
		*) unset ANS
			;;
	esac
done

git gc "$GC_OPTIONS"

if [[ $? -ne 0 ]] ; then
	if [ -n "${GC_OPTIONS}" ]; then
		git gc 
		
		if [[ $? -eq 0 ]]; then
			git gc "$GC_OPTIONS"
			if [[ $? -eq 0 ]]; then
				echo "Successfully executing garbage collection using '${GC_OPTIONS}'."
			else 	
				echo "Successfully executed garbage collection but had to omit '${GC_OPTIONS}'/"
			fi
		else
			echo "Couldn't execute garbage collection at all."
		fi
	else
		echo "Coudn't execute garbage collection."
	fi
else
	echo "Successfully executing garbage collection."
fi

rm -f "${TARGET_DIR}/hg2git-*"

cd "${OLDPWD}"

echo "Converted repository to '${TARGET_DIR}'"
