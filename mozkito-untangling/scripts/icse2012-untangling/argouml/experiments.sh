#*******************************************************************************
# Copyright 2012 Kim Herzig, Sascha Just
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#*******************************************************************************
#!/bin/bash

cd /data/kim/reposuite/

## blob-size = 2
for i in `seq 1 25`; do 
java -ea -Xmx2048m -DrepoSuiteSettings=/data/kim/reposuite/settings/reposuite_argouml_june2010.txt -Dblobsize.min=2 -Dblobsize.max=2 -Dn=50 -DblobWindow=15 -Dvote.callgraph=true -Dvote.changecouplings=true -Dvote.datadependency=true -Dvote.testimpact=false -Dout.file=/data/kim/result.untangling/argouml/tmp_blobsize=2_${i}.csv -jar reposuite-untangling/target/reposuite-untangling-0.1-SNAPSHOT-jar-with-dependencies.jar; 
cp linearRegressionModel.ser /data/kim/result.untangling/argouml/lr_blobsize=2_${i}.ser; 
done;

