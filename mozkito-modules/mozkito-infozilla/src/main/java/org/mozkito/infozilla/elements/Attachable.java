/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.infozilla.elements;

import org.mozkito.infozilla.model.attachment.Attachment;

/**
 * The Interface Attachable.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface Attachable {
	
	/**
	 * Gets the attachment.
	 * 
	 * @return the attachment
	 */
	Attachment getOrigin();
	
	/**
	 * @param attachment
	 */
	void setOrigin(Attachment attachment);
}
