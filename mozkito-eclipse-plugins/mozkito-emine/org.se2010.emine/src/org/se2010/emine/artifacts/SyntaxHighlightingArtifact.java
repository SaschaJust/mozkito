/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package org.se2010.emine.artifacts;

import java.util.HashMap;
import java.util.Map;



/**
 * This class holds the details the details which are displayed in the Editor for
 * Syntax Highlighting
 * @author  Jenny
 * @version 02/2011 1.0
 */
public class SyntaxHighlightingArtifact implements IArtifact{

	private final String title;
	private final Map<Integer,HighlightIconType> map;	// map of Int lines->String color (at the moment red,yellow,green)
	private final String message;
	private final String file;
	
	/**
	 * 
	 * @param title of the artifact
	 * @param file path of the file in which the lines should be highlighted
	 * @param map a hashmap from lines to HighlightIconType which is the marker of the specified line
	 * @param message is displayed by hover at a marker
	 */
	public SyntaxHighlightingArtifact(String title,String file,Map<Integer,HighlightIconType> map, String message){
		this.title   = title;
		this.map     = map;
		this.message = message;
		this.file    = file;
	}
	  
	public String getFile(){
		return file;
	}
	
	public Map<Integer,HighlightIconType> getMap(){
		return new HashMap<Integer, HighlightIconType>(map);
	}
	
	public String getMessage(){
		return message;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
}
