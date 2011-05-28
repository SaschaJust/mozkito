#!/bin/bash
## TODO check for bzr executable
## TODO check for python-fastimport
#@ TODO check for git executable

function help() {
	echo $(basename $0) [TARGET_DIRECTORY] [SOURCE_REPOSITORY]
}

function check_bzr() {
	TOOL=$(which bzr)
	ret=$?
	
	if [ ret -ne 0 ]; then
		echo "Couldn't find 'bzr' tool. Please install the bazaar version control system from: http://bazaar-vcs.org"
		return 1
	else
		echo "Using bzr installation: ${TOOL}"
	fi
}

function check_git() {
	TOOL=$(which git)
	ret=$?
	
	if [ ret -ne 0 ]; then
		echo "Couldn't find 'git' tool. Please install the git version control system from: http://git-scm.com"
		return 1
	else
		echo "Using git installation: ${TOOL}"
	fi
}

function downloadfile() {
	local URL=$1
	local DIR=$2
	local MYOLDPWD="$PWD"
	
	if which wget >/dev/null 2>&1 ; then
		wget "http://launchpad.net/python-fastimport/trunk/0.9.0/+download/python-fastimport-0.9.0.tar.gz"
	elif which curl >/dev/null 2>&1 ; then
		mkdir -p "${DIR}"
		cd "${DIR}"
		curl -L -C - -O "http://launchpad.net/python-fastimport/trunk/0.9.0/+download/python-fastimport-0.9.0.tar.gz"
		cd "$MYOLDPWD"
	else
		echo "Couldn't download file '${FILE}'."
	fi
}

function check_python_fastimport() {
	local MYOLDPWD="$PWD"
	TEMPFILE=$(mktemp -t bzr2git_fastimport_check)
	cat >"${TEMPFILE}" <<-EOF
#!/usr/bin/python
import fastimport
exit(0)
	EOF
	
	python "${TEMPFILE}" >/dev/null 2>&1
	ret=$?
	
	rm -f "${TEMPFILE}"
	
	if [ $ret -ne 0 ]; then
		echo "This tool requires the python fastimport module to be installed."
		echo "You can manually download the lastest version of fastimport from launchpad "
		echo "or automatically installa version 0.9 (requires sudo)."
		
		read -p "[I]nstall | [A]bort " ANS
		while [ -z $ANS ]; do
			case $ANS in
				I|i)	FASTIMPORTDIR=$(mktemp -d -t bzr2git_fastimport_dir)
				    	if ! downloadfile http://launchpad.net/python-fastimport/trunk/0.9.0/+download/python-fastimport-0.9.0.tar.gz "${FASTIMPORTDIR}"; then 
							rm -rf "${FASTIMPORTDIR}"
							return 1
						fi
						
						cd "${FASTIMPORTDIR}"
						tar xzvf python-fastimport-0.9.0.tar.gz
						cd python-fastimport-0.9.0
						python setup.py config
						
						if [ $UID -eq 0 ]; then
							python setup.py config
							ret=$?
						else
							sudo python setup.py config
							ret=$?
						fi
						
						cd "${MYOLDPWD}"
						rm -rf "${FASTIMPORTDIR}"
						
						check_python_fastimport
						return $?
				    	;;
				A|a)	echo "Aborting."
				    	return 1
				    	;;
				*)  	unset ANS
				    	;;
			esac
		done
	else
		echo "Found python module requirements."
	fi
		
}

check_bzr_fastimport() {
	if [ ! -d ~/.bazaar/plugins/fastimport ]; then
		echo "Missing bzr plugin: fastimport."
		echo "Installing..."
		mkdir -p ~/.bazaar/plugins
		cd ~/.bazaar/plugins
		bzr branch lp:bzr-fastimport fastimport
		ret=$?
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

MYOLDPWD="$PWD"

check_git || exit 1
check_bzr || exit 1
check_python_fastimport || exit 1
check_bzr_fastimport || exit 1

mkdir -p "${TARGET_DIR}"
cd "${TARGET_DIR}"

git init --bare

bzr fast-export --plain "${SOURCE_REPO}" | git fast-import

ret=$?

if [ $ret -ne 0 ]; then
	echo "Converting failed with exit code $?."
	exit 1
fi


cd "${TARGET_DIR}"

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

echo "Converted repository to '${TARGET_DIR}'"

cd "$MYOLDPWD"