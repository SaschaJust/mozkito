#!/bin/bash

function help() {
	echo $(basename $0) [TARGET_DIRECTORY] [SOURCE_REPOSITORY]
}

function check_python_bindings() {
	local MYOLDPWD="$PWD"
	TEMPFILE=$(mktemp -t hg2git_fastimport_check)
	cat >"${TEMPFILE}" <<-EOF
#!/usr/bin/python
from mercurial import repo,hg,cmdutil,util,ui,revlog,node
exit(0)
	EOF
	
	python "${TEMPFILE}" >/dev/null 2>&1
	ret=$?
	
	rm -f "${TEMPFILE}"
	
	if [ $ret -ne 0 ]; then
		echo "This tool requires the python mercurial bindings to be installed."
		echo "Please install mercurial with the corresponding python libraries."
		return 1
	else
		echo "Found python module requirements."
	fi
}

function check_git() {
	TOOL=$(which git)
	ret=$?
	
	if [ $ret -ne 0 ]; then
		echo "Couldn't find 'git' tool. Please install the git version control system from: http://git-scm.com"
		return 1
	else
		echo "Using git installation: ${TOOL}"
	fi
}

function check_hg() {
	TOOL=$(which hg)
	ret=$?
	
	if [ $ret -ne 0 ]; then
		echo "Couldn't find 'hg' tool. Please install the git version control system from: http://mercurial.selenic.com"
		return 1
	else
		echo "Using hg installation: ${TOOL}"
	fi
}

function check_targetdir() {
	local TARGET_DIR=$1
	
	if [ -x "${TARGET_DIR}" ]; then
		if [ -d "${TARGET_DIR}" ]; then
			TESTFILE=$(mktemp -q "${TARGET_DIR}/filewrite.XXXXXX")
			ret=$?
			rm -f "${TESTFILE}"
			retrun $ret
		else
			echo "Eror: target directory exists, but is not of type file."
			echo "Aborting..."
			return 1
		fi
	else
		mkdir -p "${TARGET_DIR}"
		return $?
	fi
}

if [ -z $2 ]; then
	help
	exit 1
fi

TARGET_DIR=$1
SOURCE_REPO=$2

if [ ${SOURCE_REPO::1} != "/" ]; then
	SOURCE_REPO="${PWD}/${SOURCE_REPO}"
fi

if [ ${TARGET_DIR::1} != "/" ]; then
	TARGET_DIR="${PWD}/${TARGET_DIR}"
fi

check_git || exit 1
check_hg || exit 1
check_targetdir "${TARGET_DIR}" || exit 1

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
