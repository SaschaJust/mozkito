#!/bin/bash
set -x
export SUBDIR_NAME
export BRANCH_NAME
export SOURCE_DIRECTORY
export WORKING_DIRECTORY

OLDPWD="$PWD"

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc writes the incremental sequence from FROM to TO to stdout
# @retval 0 on success
#
# function sequence(FROM [int, NOT_NEGATIVE],
#                   TO [int, NOT_NEGATIVE])
function sequence() {
	local FROM=$1
	local TO=$2
	
	# check for an seq installation
	if which seq >/dev/null 2>&1; then
		# use seq
		seq ${FROM} ${TO}
	# try to find jot instead
	elif which jot >/dev/null 2>&1; then
		# use jot
		jot ${TO} ${FROM} ${TO}
	# fallback to awk if present
	elif which awk >/dev/null 2>&1; then
		# use awk
		echo ${FROM} ${TO} | awk '{ from = $1; to = $2; } END { for (i=from; i<=to; ++i) { printf ("%s ", i); }  }'
	# abort
	else
		return 1
	fi
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc determines a separator to be used in sed to not collide with the DIRNAME
#       variable. result is printed to stdout.
# @retval 0 on success
#
# function find_separator(DIRNAME [string, directory name])
function find_separator() {
	local DIRNAME=$1
	local CHARS='@#%|<>!=_-'
	
	# try to find a character from the CHARS string that is not present
	# in the DIRNAME variable
	for i in `sequence 0 $((${#CHARS} - 1))`; do 
		if ! echo "$DIRNAME" | grep -q "${CHARS:$i:1}"; then
			echo ${CHARS:$i:1}
			return 0
		fi
	done
	
	# incase we did not find a character yet, try letters a-z
	for i in `sequence 97 122`; do 	
		local CHAR=$(printf \\$(printf '%03o' $i))
		
		if ! echo "$DIRNAME" | grep -q "$CHAR"; then
			echo "$CHAR"
			return 0
		fi
	done

	# incase we did not find a character yet, try letters A-Z
	for i in `sequence 65 90`; do 	
		local CHAR=$(printf \\$(printf '%03o' $i))
		
		if ! echo "$DIRNAME" | grep -q "$CHAR"; then
			echo "$CHAR"
			return 0
		fi
	done
	
	# give up on the search
	return 1
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc aborts the execution of the script and displays an error message on
#       stderr. Use with caution, since this might bypass any clean-ups.
#
# function exception(MESSAGE [string])
function exception() {
	local MESSAGE=$1
	
	echo "ERROR: ${MESSAGE}" >&2
	exit 1
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc fetches a remote repository from SOURCE_DIRECTORY into the specified
#       branch (TEMP_BRANCH)
# @retval 0 on success
#
# function fetch_repo(SOURCE_DIRECTORY [string, path to source directory],
#                     TEMP_BRANCH [string, name of the branch])
function fetch_repo() {
	local SOURCE_DIRECTORY=$1
	local TEMP_BRANCH=$2
	
	git fetch "${SOURCE_DIRECTORY}" "master:${TEMP_BRANCH}"
	
	ret=$?
	
	return $ret
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc determines the id of the first transaction (in the remote repository)
#       that is not empty (occurs when converting svn to git). Prints the
#       result to stdout if any.
# @retval 0 on success
#
# function determine_startid(SOURCE_DIRECTORY [string, path to source directory])
function determine_startid() {
	local SOURCE_DIRECTORY=$1
	local INDEX=1
	local TEMPFILE=$(mktemp -t "gitmerge")
	local MYOLDPWD="$PWD"
	
	cd "${SOURCE_DIRECTORY}"
	
	# save the inverted list of all transactions into a temporary file
	git rev-list --all --topo-order --remotes --branches --reverse >"${TEMPFILE}"
	
	# load the id of the first transaction
	local STARTID=$(head -n"${INDEX}" "${TEMPFILE}")
	
	# get the total number of transactions
	local TOTAL_REVS=$(cat "${TEMPFILE}" | wc -l)
	
	# find a non-empty transaction
	while [[ $(git show --pretty="format:" --name-only "${STARTID}" | wc -l) -eq 0 ]] && [[ ${INDEX} -le $TOTAL_REVS ]]; do
		let INDEX=INDEX+1
		STARTID=$(head -n "${INDEX}" "${TEMPFILE}" | tail -n +"${INDEX}")
	done

	# delete tempfile
	rm -f "${TEMPFILE}"
	
	cd "${MYOLDPWD}"
		
	# check if we found a non-empty transaction
	if [[ $(git show --pretty="format:" --name-only "${STARTID}" | wc -l) -eq 0 ]]; then
		exception "Could not find non-empty transaction."
	else 
		echo "${STARTID}"
	fi
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc uses git filter-branch to rewrite the internal git index. This is
#       nescessary to fix the mapping of the history after importing the
#       repository.
# @retval 0 on success
#
# function filter_branch(SOURCE_DIRECTORY [string, path to the source directory, VALID],
#                        SUBDIR_NAME [string, name of the sub-directory, VALID],
#                        BRANCH_NAME [string, name of the branch the repo has been fetched to, VALID])
function filter_branch() {
	local SOURCE_DIRECTORY=$1
	local SUBDIR_NAME=$2
	local BRANCH_NAME=$3
	local STARTID=$(determine_startid "${SOURCE_DIRECTORY}")
	local SEPARATOR=$(find_separator "${SUBDIR_NAME}")
	ret=$?
	
	while (($ret != 0)); do
		echo "Could not find a separator character that is not contained in the directory name."
		read -p "Please specify a different directory name: " SUBDIR_NAME
		SEPARATOR=$(find_separator "${SUBDIR_NAME}")
		ret=$?
	done
	
	SED_COMMAND="s${SEPARATOR}$(echo -e '\t')${SEPARATOR}&${SUBDIR_NAME}/${SEPARATOR}"
	
	git filter-branch --INDEX-filter \
		'git ls-files -s | \
		    sed '"${SED_COMMAND}"' | \
		    GIT_INDEX_FILE=$GIT_INDEX_FILE.new git update-INDEX --INDEX-info && \
		    mv $GIT_INDEX_FILE.new $GIT_INDEX_FILE
		' "$BRANCH_NAME~$STARTID..$BRANCH_NAME"
	ret=$?
	
	return $ret
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc merges the temporary branch (BRANCH_NAME) into the local branch
# @retval 0 on success
#
# function merge_branch(BRANCH_NAME [string, name of the branch the repo has been fetched to, VALID])
function merge_branch() {
	local BRANCH_NAME=$1
	
	# merge the temporary branch into our branch
	git merge "${BRANCH_NAME}"
	
	ret=$?
	
	return $ret
}

# @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
# @desc deletes the temporary branch (BRANCH_NAME)
# @retval 0 on success
#
# function delete_tempbranch(BRANCH_NAME [string, name of the branch the repo has been fetched to, VALID])
function delete_tempbranch() {
	local BRANCH_NAME=$1
	
	# delete the temporary branch
	git branch -d "${BRANCH_NAME}"
	
	ret=$?
	
	return $ret
}


## READ required information and perform consistency checks

# SUBDIR_NAME - name of the sub-directory we want the remote repository to
# be merged to.
while [ -z "${SUBDIR_NAME}" ]; do
	read -p "Please specify the name of the sub-directory the remote repository should be merged in: " SUBDIR_NAME
	if [ -n "${SUBDIR_NAME}" ]; then
		if [ -x "${SUBDIR_NAME}" ]; then
			if [ -d "${SUBDIR_NAME}" ]; then
				echo "ERROR: Sub-directory already exists."
			else 
				echo "ERROR: '${SUBDIR_NAME}' already exists and is not a directory."
			fi
			unset SUBDIR_NAME
		else
			if ! mkdir -p "${SUBDIR_NAME}"; then
				echo "ERROR: Cannot create sub-directory '${SUBDIR_NAME}'."
				unset SUBDIR_NAME
			else
				rm -rf "${SUBDIR_NAME}"
			fi
		fi
	fi
done

# BRANCH_NAME - name of the temporary branch the remote repository will be 
# fetched to.
while [ -z "${BRANCH_NAME}" ]; do
	read -p "Please specify the name of the temporary branch to be used to merge the repository (use alphanumeric characters only [a-zA-Z0-9]): " BRANCH_NAME
	if [ -n "${BRANCH_NAME}" ]; then
		if [[ $BRANCH_NAME =~ ^[a-zA-Z][A-Za-z0-9]+$ ]]; then
			echo "Using branch name: ${BRANCH_NAME}"
		else
			echo "ERROR: Invalid branch name: ${BRANCH_NAME}"
			unset BRANCH_NAME
		fi
	fi
done

# SOURCE_DIRECTORY - path to the remote repository
while [ -z "${SOURCE_DIRECTORY}" ]; do
	read -p "Please specify the path to the remote repository that shall be merge into a subfolder of this one: " SOURCE_DIRECTORY
	
	if [ -n "${SOURCE_DIRECTORY}" ]; then
		if [ -d "${SOURCE_DIRECTORY}" ]; then
			if [ -f "${SOURCE_DIRECTORY}/config" ] && egrep -q 'bare[ \t]=[ \t]true' "${SOURCE_DIRECTORY}/config" ; then
				echo "Using source directory: ${SOURCE_DIRECTORY}"
			else
				echo "ERROR: Could not find bare repository at location: ${SOURCE_DIRECTORY}"
				unset SOURCE_DIRECTORY
			fi
		else
			echo "ERROR: Source directory does not exist: ${SOURCE_DIRECTORY}"
			unset SOURCE_DIRECTORY
		fi
	fi
done

# WORKING_DIRECTORY - path to our local repository. 
# The one the merge shall be performed in.
while [ -z "${WORKING_DIRECTORY}" ]; do
	read -p "Please specify the path to the local repository clone the remote repository shall be imported to: " WORKING_DIRECTORY
	
	if [ -n "${WORKING_DIRECTORY}" ]; then
		if [ -d "${WORKING_DIRECTORY}" ]; then
			if [ -f "${WORKING_DIRECTORY}/.git/config" ] && ! egrep -q 'bare[ \t]=[ \t]true' "${WORKING_DIRECTORY}/.git/config"; then
				echo "Using working directory: ${WORKING_DIRECTORY}"
			else
				echo "ERROR: Could not find clone at location: ${WORKING_DIRECTORY}"
				unset WORKING_DIRECTORY
			fi
		else
			echo "ERROR: Working directory does not exist: ${WORKING_DIRECTORY}"
			unset WORKING_DIRECTORY
		fi
	fi
done

# switch to the repository and perform the merge
cd "${WORKING_DIRECTORY}" && \
	fetch_repo "${SOURCE_DIRECTORY}" "${BRANCH_NAME}" && \
	filter_branch "${SOURCE_DIRECTORY}" "${SUBDIR_NAME}" "${BRANCH_NAME}" && \
	merge_branch "${BRANCH_NAME}" && \
	delete_tempbranch "${BRANCH_NAME}"
	
ret=$?

if [[ $ret -eq 0 ]]; then
	echo "Success. All done. Please check the changes and push to your remote repository afterwards."
else
	echo "Something went wrong. Please check the error messages for details."
fi

exit $ret