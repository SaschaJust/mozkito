#!/bin/bash
for f in `find . -type f -name '*.java'`; do cat $f | awk '
	BEGIN { dele=0 } 

	/^ *\/\*\*/ {
		dele=1;
	}
	
	/^ *\* *[@a-zA-Z0-9].*/ {
		dele=0;
	}
	
	/^ *\*\// {
		if (dele == 1) {
			exit(1);
		} else {
			exit (0);
		}
	}
' || head $f;  done 
