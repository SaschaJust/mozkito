#!/bin/bash

function clean_exit()
{
    cd $script_dir$MAC_OS_ECLIPSE_ZIP_PARENT_PATH
    rm -rf eclipse
    rm -rf /tmp/reposuite_28_01_2011.git
    rm -rf /tmp/reposuite_28_01_2011.git.zip
    exit $1
}

MAC_OS_ECLIPSE_PATH='/../../../../reposuite-ppa-product/bin/mac/eclipse/eclipse.app/Contents/MacOS/'
MAC_OS_ECLIPSE_ZIP_PARENT_PATH='/../../../../reposuite-ppa-product/bin/mac/'

LINUX_OS_ECLIPSE_ZIP_PARENT_PATH='/../../../../reposuite-ppa-product/bin/linux/'
LINUX_OS_ECLIPSE_PATH='/../../../../reposuite-ppa-product/bin/linux/eclipse/'

MAC_ARGS='-Dosgi.requiredJavaVersion=1.5 -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts -XX:MaxPermSize=256m -Xms40m -Xmx512m -Xdock:icon=../Resources/Eclipse.icns -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts'
LINUX_ARGS=''

BASIC_VMARGS='-DdisableCrashEmail -Ddatabase.host=quentin.cs.uni-saarland.de -Ddatabase.name=reposuite_ppa_test -Ddatabase.user=miner -Ddatabase.password=miner -Dlog.level=warn -Drepository.type=GIT '

t="/tmp/t"
echo $0 > $t
script_dir=`sed -e 's_reposuite-ppa-eclipse\\.sh__g' $t`

if [[ ! $script_dir == /* ]]
then
    script_dir="$PWD/$script_dir"
fi

repo_zip=$script_dir"/reposuite_28_01_2011.git.zip"
cp $repo_zip /tmp/ > /dev/null
cd /tmp/
unzip "reposuite_28_01_2011.git.zip"

sys_env=`uname -a`
if [[ $sys_env == Darwin* ]]
then 
    if [ ! -f $script_dir$MAC_OS_ECLIPSE_ZIP_PARENT_PATH"/eclipse.zip" ]
    then
        echo "Could not find eclipse.zip for mac!"
        clean_exit 1
    fi
    ECLIPSE_PARENT_DIR=$script_dir$MAC_OS_ECLIPSE_ZIP_PARENT_PATH
    BASIC_VMARGS="$MAC_ARGS $BASIC_VMARGS"
    ECLIPSE_PATH=$script_dir$MAC_OS_ECLIPSE_PATH
elif  [[ $sys_env == Linux* ]]
then
    if [ ! -f $script_dir$LINUX_OS_ECLIPSE_ZIP_PARENT_PATH"/eclipse.zip" ]
    then
        echo "Could not find eclipse.zip for linux!"
        clean_exit 1
    fi
    ECLIPSE_PARENT_DIR=$script_dir$LINUX_OS_ECLIPSE_ZIP_PARENT_PATH
    BASIC_VMARGS="$LINUX_ARGS $BASIC_VMARGS"
    ECLIPSE_PATH=$script_dir$LINUX_OS_ECLIPSE_PATH
else
    echo "unsupported architecture!"
    clean_exit 1
fi
cd $ECLIPSE_PARENT_DIR
unzip -o -qq eclipse.zip
if [ $? -ne 0 ]
then
    clean_exit $?
fi

echo "TEST CASE 1 checking change operations for transaction f99a3ff4615653855c254874f3d4fe0d084f34d2 ... "
cd $ECLIPSE_PATH
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/tmp/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa.xml -DtestCaseTransactions=f99a3ff4615653855c254874f3d4fe0d084f34d2"
./eclipse $VMARGS

if [ $? -ne 0 ]
then
    clean_exit $?
fi

cd $script_dir
./ppa_xml_diff.py -x ppa_comp.xml -y /tmp/ppa.xml

if [ $? -ne 0 ]
then
    echo "FAILED!"
    clean_exit $?
fi

./ppa_xml_diff.py -x /tmp/ppa.xml -y ppa_comp.xml

if [ $? -ne 0 ]
then
    echo "FAILED!"
    clean_exit $?
else
    echo "done."
fi


rm -rf /tmp/ppa.xml

echo "TEST CASE 2 checking change operations for transaction dfd5e8d5eb594e29f507896a744d2bdabfc55cdf ... "
cd $ECLIPSE_PATH
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/tmp/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa2.xml -DtestCaseTransactions=dfd5e8d5eb594e29f507896a744d2bdabfc55cdf"
./eclipse $VMARGS

if [ $? -ne 0 ]
then
    clean_exit $?
fi

cd $script_dir
./ppa_xml_diff.py -x ppa_comp_2.xml -y /tmp/ppa2.xml

if [ $? -ne 0 ]
then
    echo "checking for alternative XML file. This is a non-deterministic bug in PPA ..."
    ./ppa_xml_diff.py -x ppa_comp_2_altern.xml -y /tmp/ppa2.xml
    if [ $? -ne 0 ]
    then
        echo "FAILED!"
        clean_exit $?
    else
        echo "done."
    fi
else
    echo "done."
fi
rm -rf /tmp/ppa2.xml


echo "TEST CASE 3 checking change operations for transaction 0309f53f798d178aaf519333755c0f62500fcca9 ... "
cd $ECLIPSE_PATH
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/tmp/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa3.xml -DtestCaseTransactions=0309f53f798d178aaf519333755c0f62500fcca9"
./eclipse $VMARGS

if [ $? -ne 0 ]
then
    clean_exit $?
fi

cd $script_dir
./ppa_xml_diff.py -x ppa_comp_3.xml -y /tmp/ppa3.xml

if [ $? -ne 0 ]
then
    echo "FAILED!"
    clean_exit $?
else
    echo "done."
fi
rm -rf /tmp/ppa3.xml

echo "TEST CASE 4 checking change operations for transaction ff1ba504345e9df2b9feb0c678779945017236cc ... "
cd $ECLIPSE_PATH
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/tmp/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa4.xml -DtestCaseTransactions=ff1ba504345e9df2b9feb0c678779945017236cc"
./eclipse $VMARGS

if [ $? -ne 0 ]
then
    clean_exit $?
fi

cd $script_dir
./ppa_xml_diff.py -x ppa_comp_4.xml -y /tmp/ppa4.xml

if [ $? -ne 0 ]
then
    echo "FAILED!"
    clean_exit $?
else
    echo "done."
fi
rm -rf /tmp/ppa4.xml

clean_exit 0
