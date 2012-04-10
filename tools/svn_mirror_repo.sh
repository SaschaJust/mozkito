#!/bin/bash

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc makes sure a path is absolute. The absolut path is printed to stdout.
# @retval 0 on success
#
# function make_absolut_path(TARGET_DIR [string, path],
#                   TO [int, NOT_NEGATIVE])
function make_absolut_path() {
	local TARGET_DIR=$1
	
	if [ "${TARGET_DIR:0:1}" != "/" ]; then
		echo "${PWD}/${TARGET_DIR}"
	else
		echo "${TARGET_DIR}"
	fi
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc makes sure SOURCE_URL is given in URL format. Relative and absolute
#       paths are transformed into file:// URLs. Resulting path is printed
#       to stdout. 
# @retval 0 on success
#
# function make_url(SOURCE_URL [string, URL or path])
function make_url() {
	local SOURCE_URL=$1
	
	if [ "${SOURCE_URL:0:1}" == "/" ] && [ -d "${SOURCE_URL}" ]; then
		echo "file://${SOURCE_URL}"
	elif [[ ! "${SOURCE_URL}" =~ :\/\/ ]] && [ -d "${PWD}/${SOURCE_URL}" ]; then
		echo "file://${PWD}/${SOURCE_URL}"
	elif [[ "${SOURCE_URL}" =~ ^[a-z+]+:\/\/ ]]; then
		echo "${SOURCE_URL}"
	else
		return 1
	fi
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc checks if svnadmin is available. Prints information about the installation
#       to stdout. Prints an error to stderr if the tool hasn't been found.
# @retval 0 on success
#
# function check_svnadmin()
function check_svnadmin() {
	TOOL=$(which svnadmin)
	ret=$?
	
	if [ $ret -ne 0 ]; then
		echo "Couldn't find 'svnadmin' tool. Please install the SVN version control system from: http://subversion.tigris.org/" >&2
		return 1
	else
		echo "Using svnadmin installation: ${TOOL}"
	fi
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc checks if svnsync is available. Prints information about the installation
#       to stdout. Prints an error to stderr if the tool hasn't been found.
# @retval 0 on success
#
# function check_svnsync()
function check_svnsync() {
	TOOL=$(which svnsync)
	ret=$?
	
	if [ $ret -ne 0 ]; then
		echo "Couldn't find 'svnadmin' tool. Please install the SVN version control system from: http://subversion.tigris.org/"
		return 1
	else
		echo "Using svnsync installation: ${TOOL}"
	fi
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc displays a help string
# @retval 0 on success
#
# function help()
function help() {
	echo $(basename $0) [TARGET_DIRECTORY] [SOURCE_REPOSITORY] [ [USERNAME] [PASSWORD] ]
	echo
	echo "Mirrors a remote Subversion repository to a local directory."
	exit 0
}


# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc aborts the execution of the script and displays an error message on
#       stderr. Use with caution, since this might bypass any clean-ups.
#
# function exception(MESSAGE [string])
function exception() {
	local MESSAGE=$*
	
	echo "ERROR: ${MESSAGE}" >&2
	exit 1
}

# check if there are 2 or 4 arguments present 
[[ $# -eq 2 ]] || [[ $# -eq 4 ]] || help

check_svnsync || exception "svnsync not available."
check_svnadmin || exception "svnadmin not available."

SOURCE_URL=$2
TARGET_DIR=$1
if [[ -n "$4" ]]; then
	export SVN_USERNAME=$3
	export SVN_PASSWORD=$4
	export SVN_AUTHSTRING=" --source-username=${SVN_USERNAME} --source-password=${SVN_PASSWORD} "
fi

TARGET_DIR=$(make_absolut_path "${TARGET_DIR}")
[[ $? -ne 0 ]] && exception "Target directory invalid."

SOURCE_URL=$(make_url ${SOURCE_URL})
[[ $? -ne 0 ]] && exception "Source URL invalid."

if [ ! -d "${TARGET_DIR}" ]; then
	mkdir -p "${TARGET_DIR}" || exception "Cannot create target directory: ${TARGET_DIR}"
	svnadmin create "${TARGET_DIR}" || exception "Creating an empty, local SVN repository failed." 

	cat <<-EOF >"${TARGET_DIR}/hooks/pre-revprop-change"
	#!/bin/sh
	exit 0
	EOF
	chmod +x "${TARGET_DIR}/hooks/pre-revprop-change"

	svnsync init ${SVN_AUTHSTRING} "file://${TARGET_DIR}" "${SOURCE_URL}" || exception "Initializing the sync repository failed."
else
	while [[ -z ANS ]]; do
		read -p "Repository already exists. Resume? [y|N] " ANS
		case ANS in
			y|Y|yes|Yes)
				ANS=YES
				;;
			*)
			unset ANS
		esac
	done
	
	if [ "$ANS" != "YES" ]; then
		echo "Abort due to user decision."
		exit 0
	fi
fi

svnsync synchronize ${SVN_AUTHSTRING} "file://${TARGET_DIR}" || exception "Sync failed on ${TARGET_DIR}."

#rm -f "${TARGET_DIR}/hooks/pre-revprop-change"

echo "All done."