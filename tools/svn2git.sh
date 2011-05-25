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

if [ ${SOURCE_REPO::1} != "/" ]; then
	SOURCE_REPO="${PWD}/${SOURCE_REPO}"
fi

if [ -z $GITSVNMIGRATE_DIR ]; then
	EXEC_DIR=$(dirname $0)
	if [ ${EXEC_DIR::1} != "/" ]; then
		EXEC_DIR=$(dirname "$PWD/$0")
	fi
	
	if [ -d "${EXEC_DIR}/libs/git-svn-migrate" ] && [ -f "${EXEC_DIR}/libs/git-svn-migrate/git-svn-migrate.sh" ]; then
		export GITSVNMIGRATE_DIR="${EXEC_DIR}/libs/git-svn-migrate"
	else
		echo "error: Couldn't find git-svn-migrate script at: ${EXEC_DIR}/libs/git-svn-migrate/git-svn-migrate.sh"
		exit 1
	fi
else
	if [ ! -f "${EXEC_DIR}/libs/git-svn-migrate/git-svn-migrate.sh" ]; then
		echo "error: Couldn't find git-svn-migrate script at: ${EXEC_DIR}/libs/git-svn-migrate/git-svn-migrate.sh"
		exit 1
	fi
fi

OLDPWD="$PWD"

REPOURI=$(mktemp -t "svn2git_uri")
echo "file://${SOURCE_REPO}" > "${REPOURI}"

AUTHORFILE=$(mktemp -t "svn2git_authors")
bash "${GITSVNMIGRATE_DIR}/fetch-svn-authors.sh" --url-file="${REPOURI}" > "${AUTHORFILE}"

if [ $? -ne 0 ]; then
	echo "Fetching authors failed with exit code $?."
	exit 1
fi

TEMPDIR=$(mktemp -d -t svn2git_target)

bash "${GITSVNMIGRATE_DIR}/git-svn-migrate.sh" --url-file="${REPOURI}" --authors-file="${AUTHORFILE}" --destination "${TEMPDIR}"

ret=$?

rm -f "${REPOURI}"
rm -f "${AUTHORFILE}"

if [ $ret -ne 0 ]; then
	rm -rf "${TEMPDIR}"
	echo "Converting failed with exit code $?."
	exit 1
else
	REPONAME=$(basename ${SOURCE_REPO})
	cp -r "${TEMPDIR}/${REPONAME}.git/" "${TARGET_DIR}"
	rm -rf "${TEMPDIR}"
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

cd "$OLDPWD"