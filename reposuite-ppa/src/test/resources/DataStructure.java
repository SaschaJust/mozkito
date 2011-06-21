/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package org.junit.sample.test;

public class DataStructure {
    //create an array
    private final static int SIZE = 15;
    private int[] arrayOfInts = new int[SIZE];
    
    public DataStructure() {
        //fill the array with ascending integer values
        for (int i = 0; i < SIZE; i++) {
            arrayOfInts[i] = i;
        }
    }
    
    public void printEven() {
        //print out values of even indices of the array
        InnerEvenIterator iterator = this.new InnerEvenIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.getNext() + " ");
        }
    }
    
//inner class implements the Iterator pattern
    private class InnerEvenIterator {
        //start stepping through the array from the beginning
        private int next = 0;
        
        public boolean hasNext() {
            //check if a current element is the last in the array
            return (next <= SIZE - 1);
        }
        
        public int getNext() {
            //record a value of an even index of the array
            int retValue = arrayOfInts[next];
            //get the next even element
            next += 2;
            return retValue;
        }
        
        public java.util.Enumeration enumerate() {
        	  // The anonymous class is defined as part of the return statement
        	  return new java.util.Enumeration() { 
        	    Linkable current; = head;
        	    { current = head; }  // Replace constructor with an instance initializer
        	    public boolean hasMoreElements() {  return (current != null); }
        	    public Object nextElement() {
        	      if (current == null) throw new java.util.NoSuchElementException();
        	      Object value = current;
        	      current = current.getNext();
        	      return value;
        	    }
        	  };  // Note the required semicolon: it terminates the return statement
        	}
        
    }
    
    public static void main(String s[]) {
        //fill the array with integer values and print out only values of even indices
        DataStructure ds = new DataStructure();
        ds.printEven();
    }
}
