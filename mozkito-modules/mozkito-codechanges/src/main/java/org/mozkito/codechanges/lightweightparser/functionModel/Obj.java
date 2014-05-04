/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.codechanges.lightweightparser.functionModel;

/**
 * This class represents an object associated with a function call. One instance of Obj is created for each occurrence
 * of an identifier.
 * 
 * <pre>
 * eg., for the code "f.blub(); f.foo();" two Obj objects are created, each representing the identifier f
 * 
 * </pre>
 * 
 * To determine whether two instances of Obj represent the same object, the equalsObject method should be called.
 * 
 */
public class Obj {
	
	/**
	 * The position indicating how the object is associated with the function call <br>
	 * -1: return value <br>
	 * 0: target <br>
	 * n: nth argument of the function call.
	 */
	int     position;
	
	/**
	 * The identifier representing the object If name starts with '#' or with '%' then this Obj instance is representing
	 * a temporary object.
	 */
	String  name;
	
	/** The name of the function whose call the object is associated with. */
	String  funName;
	
	/**
	 * Indicates whether name is a temporary identifier or not Temporary identifiers are identifiers that were assigned
	 * to temporary objects (ie., ones that are return values of functions that are not assigned to an identifier in the
	 * source code)
	 * 
	 * For example: foo().bar(arg()); The return values of foo() and arg() are temporary objects.
	 */
	boolean temp;
	
	/**
	 * Instantiates a new obj.
	 * 
	 * @param position
	 *            the position indicated how the object is associated with the function call
	 * @param name
	 *            the identifier representing the object
	 * @param funName
	 *            the function name
	 * @param numArgs
	 *            the number of arguments the function takes
	 */
	public Obj(final int position, final String name, final String funName, final int numArgs) {
		
		this.position = position;
		this.name = name.trim().replaceAll("\\*", "");
		this.name = this.name.replaceAll("&", "");
		this.funName = funName.trim() + "(" + numArgs + ")";
		this.temp = name.startsWith("#") || name.startsWith("%");
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Obj other = (Obj) obj;
		if (this.funName == null) {
			if (other.funName != null) {
				return false;
			}
		} else if (!this.funName.equals(other.funName)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.position != other.position) {
			return false;
		}
		return true;
	}
	
	/**
	 * Compares the name fields of two Objs to determine if the two instances of Obj represent the same identifier.
	 * 
	 * @param obj
	 *            an instance of Obj
	 * @return true if obj represents the same identifier as this
	 */
	public boolean equalsObject(final Obj obj) {
		return this.name.equals(obj.name);
	}
	
	/**
	 * Gets the name of the function whose call the object is associated with.
	 * 
	 * @return the function name
	 */
	public String getFunName() {
		return this.funName;
	}
	
	/**
	 * Gets the identifier which the Obj is representing.
	 * 
	 * @return the identifier
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the position indicating the relationship between object and function call. <br>
	 * -1: return value <br>
	 * 0: target <br>
	 * n: nth argument of the function call
	 * 
	 * @return the position
	 */
	public int getPosition() {
		return this.position;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.funName == null)
		                                                   ? 0
		                                                   : this.funName.hashCode());
		result = (prime * result) + ((this.name == null)
		                                                ? 0
		                                                : this.name.hashCode());
		result = (prime * result) + this.position;
		return result;
	}
	
	/**
	 * Determines if the Obj instance is representing a temporary identifier.
	 * 
	 * Temporary identifiers are identifiers that were assigned to temporary objects (ie., ones that are return values
	 * of functions but were not assigned to an identifier in the source code)
	 * 
	 * @return true if the Obj is representing a temporary identifier
	 */
	public boolean isTemp() {
		return this.temp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + "@" + this.position;
	}
}
