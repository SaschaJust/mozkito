#!/bin/bash

function clean_exit()
{
    cd $script_dir§$MAC_OS_ECLIPSE_ZIP_PARENT_PATH
    rm -rf eclipse
    exit $1
}

MAC_OS_ECLIPSE_PATH='/../../../../reposuite-ppa-product/bin/mac/eclipse/eclipse.app/Contents/MacOS/'
MAC_OS_ECLIPSE_ZIP_PATH='/../../../../reposuite-ppa-product/bin/mac/eclipse.zip'
MAC_OS_ECLIPSE_ZIP_PARENT_PATH='/../../../../reposuite-ppa-product/bin/mac/'
MAC_ARGS='-Dosgi.requiredJavaVersion=1.5 -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts -XX:MaxPermSize=256m -Xms40m -Xmx512m -Xdock:icon=../Resources/Eclipse.icns -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts'

BASIC_VMARGS='-DdisableCrashEmail -Ddatabase.host=quentin.cs.uni-saarland.de -Ddatabase.name=reposuite_ppa_test -Ddatabase.user=miner -Ddatabase.password=miner -Dlog.level=warn -Drepository.type=GIT '

t="/tmp/t"
echo $0 > $t
script_dir=`sed -e 's_reposuite-ppa-eclipse\\.sh__g' $t`

if [[ ! $script_dir == /* ]]
then
    script_dir="$PWD/$script_dir"
fi

sys_env=`uname -a`
if [[ $sys_env == Darwin* ]]
then 
    if [ ! -f $script_dir$MAC_OS_ECLIPSE_ZIP_PATH ]
    then
        echo "Could not find eclipse.zip for mac!"
        clean_exit 1
    fi
    cd $script_dir$MAC_OS_ECLIPSE_ZIP_PARENT_PATH
    unzip -o -qq eclipse.zip
    ECLIPSE_PATH=$script_dir$MAC_OS_ECLIPSE_PATH;
elif  [[ $sys_env == Linux* ]]
then
    echo "eclipse application for Linux not found!"
    clean_exit 1
else
    echo "unsupported architecture!"
    clean_exit 1
fi


echo "TEST CASE 1 checking change operations for transaction f99a3ff4615653855c254874f3d4fe0d084f34d2 ... "
cd $ECLIPSE_PATH
BASIC_VMARGS="$MAC_ARGS $BASIC_VMARGS"
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/scratch/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa.xml -DtestCaseTransactions=f99a3ff4615653855c254874f3d4fe0d084f34d2"
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
else
    echo "done."
fi
rm -rf /tmp/ppa.xml

echo "TEST CASE 2 checking change operations for transaction dfd5e8d5eb594e29f507896a744d2bdabfc55cdf ... "
cd $ECLIPSE_PATH
BASIC_VMARGS="$MAC_ARGS $BASIC_VMARGS"
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/scratch/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa2.xml -DtestCaseTransactions=dfd5e8d5eb594e29f507896a744d2bdabfc55cdf"
./eclipse $VMARGS

if [ $? -ne 0 ]
then
    clean_exit $?
fi

cd $script_dir
./ppa_xml_diff.py -x ppa_comp_2.xml -y /tmp/ppa2.xml

if [ $? -ne 0 ]
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
clean_exit 0
