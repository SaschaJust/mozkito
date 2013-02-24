function mvn() {
	/usr/bin/mvn $@ | tee maven.log | grcat conf.mvn
	rm -f maven.err maven.warn
	cat maven.log | awk '
		BEGIN {
			line=1;
		}
		
		/^\[ERROR\]/ {
			print "[#"line"]"$0 >>"maven.err";
		}

		/^\[WARNING\]/ {
			print "[#"line"]"$0 >>"maven.warn";
		}
		
		{
			++line;
		}
	'
	[ -f maven.warn ] && echo "Detected $(wc -l maven.warn | awk '{ print $1 }') maven warnings. Enter 'mcat ${PWD}/maven.warn' for details."
	[ -f maven.err ] && echo "Detected $(wc -l maven.err | awk '{ print $1 }') maven errors. Enter 'mcat ${PWD}/maven.err' for details."
}

function mcat() {
	cut -d] -f 2- $@ | grcat conf.mvn
}
