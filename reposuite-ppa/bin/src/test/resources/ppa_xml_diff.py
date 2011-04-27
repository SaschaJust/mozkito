#!/usr/bin/env python
# encoding: utf-8
"""
untitled.py

Created by Kim Herzig on 2011-02-24.
Copyright (c) 2011 __MyCompanyName__. All rights reserved.
"""

import sys
import getopt
from lxml import etree
from lxml import objectify

help_message = '''
The help message goes here.
'''


class Usage(Exception):
    def __init__(self, msg):
        self.msg = msg


def xml_compare(x1, x2, reporter=None):
    if x1.tag != x2.tag:
        if reporter:
            reporter('Tags do not match: %s and %s' % (x1.tag, x2.tag))
        return False
    for name, value in x1.attrib.items():
        if x2.attrib.get(name) != value:
            if reporter:
                reporter('Attributes do not match: %s=%r, %s=%r'
                         % (name, value, name, x2.attrib.get(name)))
            return False
    for name in x2.attrib.keys():
        if name not in x1.attrib:
            if reporter:
                reporter('x2 has an attribute x1 is missing: %s'
                         % name)
            return False
    if x1.text is None:
        x1.text = ""
    if x2.text is None:
        x2.text = ""
    if not x1.text.strip() == x2.text.strip():
        if reporter:
            reporter('text: %r != %r' % (x1.text, x2.text))
        return False
    if not x1.tail.strip() == x2.tail.strip():
        if reporter:
            reporter('tail: %r != %r' % (x1.tail, x2.tail))
        return False
    cl1 = x1.getchildren()
    cl2 = x2.getchildren()
    if len(cl1) != len(cl2):
        if reporter:
            reporter('children length differs, %i != %i'
                     % (len(cl1), len(cl2)))
        return False
    i = 0
    for c1, c2 in zip(cl1, cl2):
        i += 1
        if not xml_compare(c1, c2, reporter=reporter):
            if reporter:
                reporter('children %i do not match: %s'
                         % (i, c1.tag))
            return False
    return True


def tree_compare(x1, x2, reporter=None):
    for i in range(len(x1)):
        result = False
        for j in range(len(x2)):
            if reporter:
                reporter("Comparing x1["+str(i)+"] with x2["+str(j)+"] ...")
            result = result | xml_compare(x1[i],x2[j],reporter)
            if result:
                reporter("FOUND!")
                break
        if not result:
            return False
    return True

def main(argv=None):
    if argv is None:
        argv = sys.argv
    try:
        try:
            opts, args = getopt.getopt(argv[1:], "hx:y:v", ["help", "xml1=", "xml2="])
        except getopt.error, msg:
            raise Usage(msg)
        
        x = None
        y = None
        
        # option processing
        for option, value in opts:
            if option == "-v":
                verbose = True
            if option in ("-h", "--help"):
                raise Usage(help_message)
            if option in ("-x", "--xml1"):
                x = value
            if option in ("-y", "--xml2"):
                y = value
    
        if (x is None) or (y is None):
            raise Usage("You must provide two xml files")
        
        xtree = etree.parse(x)
        ytree = etree.parse(y)
        
        xroot = xtree.getroot()
        yroot = ytree.getroot()
        
        assert xroot.tag == "javaChangeOperations"
        assert xroot.tag == "javaChangeOperations"

        assert len(xroot) == len(yroot)
        
        for i in range(len(xroot)):
            assert xroot[i].tag == yroot[i].tag
            assert xroot[i].attrib == yroot[i].attrib
            result = []
            r = tree_compare(xroot[i].getchildren(), yroot[i].getchildren(), reporter=result.append)
            if not r :
                print 'Difference report:\n%s\n' % '\n'.join(result)
                return -1
        
    except Usage, err:
        print >> sys.stderr, sys.argv[0].split("/")[-1] + ": " + str(err.msg)
        print >> sys.stderr, "\t for help use --help"
        return 2


if __name__ == "__main__":
    sys.exit(main())
