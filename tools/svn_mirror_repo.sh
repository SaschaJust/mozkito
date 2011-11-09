#!/bin/bash
set -x

SOURCE_URL=$2
TARGET_DIR=$1

if [ ! -d "${TARGET_DIR}" ]; then
	mkdir -p "${TARGET_DIR}" || exception "Cannot create target directory: ${TARGET_DIR}"
else
	exception "Target directory already exists: ${TARGET_DIR}"
fi

TARGET_DIR=$(make_absolut_path "${TARGET_DIR}")

function make_absolut_path() {
	local TARGET_DIR=$1
	
	if [ "${TARGET_DIR:1:1}" != "/" ]; then
		echo "${PWD}/${TARGET_DIR}"
	else
		echo "${TARGET_DIR}"
	fi
}

function make_url() {
	local SOURCE_URL=$1
	
	if [ "${TARGET_DIR:1:1}" == "/" ]; then
		echo "file://${TARGET_DIR}"
	elif [ "${TARGET_DIR}" =~ :\/\/ ]; then
		
	fi
}

function help() {
	echo $(basename $0) [TARGET_DIRECTORY] [SOURCE_REPOSITORY]
	echo
	echo "Mirrors a remote Subversion repository to a local directory."
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

svnadmin create "${TARGET_DIR}" || exception "Creating an empty, local SVN repository failed." 

cat <<EOF >"${TARGET_DIR}/hooks/pre-revprop-change"
#!/bin/sh
exit 0
EOF
chmod +x "${TARGET_DIR}/hooks/pre-revprop-change"

svnsycn init "file://${TARGET_DIR}" "${SOURCE_URL}" || exception "Initializing the sync repository failed."
svnsync synchronize "file://${TARGET_DIR}" || exception "Sync failed on ${TARGET_DIR}."

rm -f "${TARGET_DIR}/hooks/pre-revprop-change"

echo "All done."