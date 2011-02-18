package org.se2010.emine.artifacts;

import javax.swing.text.html.HTML;

public class TooltipArtifact implements IArtifact {

	private String title;
	private HTML content;
	
	TooltipArtifact(String title, HTML content){
		this.title=title;
		this.content=content;
	}
	
	public HTML getContent(){
		return content;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

}
