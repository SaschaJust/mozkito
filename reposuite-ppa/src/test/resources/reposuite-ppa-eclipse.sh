#!/bin/bash

MAC_OS_ECLIPSE_PATH='/../../../../reposuite-ppa-product/bin/eclipse/eclipse.app/Contents/MacOS/'
MAC_ARGS='-Dosgi.requiredJavaVersion=1.5 -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts -XX:MaxPermSize=256m -Xms40m -Xmx512m -Xdock:icon=../Resources/Eclipse.icns -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts'

BASIC_VMARGS='-DdisableCrashEmail -Ddatabase.host=quentin.cs.uni-saarland.de -Ddatabase.name=reposuite_ppa_test -Ddatabase.user=miner -Ddatabase.password=miner -Dlog.level=warn -Drepository.type=GIT '

t="/tmp/t"
echo $0 > $t
script_dir=`sed -e 's_reposuite-ppa-eclipse\\.sh__g' $t`
script_dir="$PWD/$script_dir"

sys_env=`uname -a`
if [[ $sys_env == Darwin* ]]
then 
    ECLIPSE_PATH=$script_dir$MAC_OS_ECLIPSE_PATH; 
elif  [[ $sys_env == Linux* ]]
then
    echo "eclipse apprication for Linux not found!"
    exit 1
else
    echo "unsupported architecture!"
    exit 1
fi


# TEST CASE 1 checking change operations for transaction f99a3ff4615653855c254874f3d4fe0d084f34d2
cd $ECLIPSE_PATH
BASIC_VMARGS="$MAC_ARGS $BASIC_VMARGS"
VMARGS="-vmargs $BASIC_VMARGS -Drepository.uri=/scratch/reposuite_28_01_2011.git/ -Doutput.xml=/tmp/ppa.xml -DtestCaseTransactions=f99a3ff4615653855c254874f3d4fe0d084f34d2"
# ./eclipse $VMARGS

if [ $? -ne 0 ]
then
    exit $?
fi

cd $script_dir
./ppa_xml_diff.py -x ppa_comp.xml -y /tmp/ppa.xml

if [ $? -ne 0 ]
then
    exit $?
fi
