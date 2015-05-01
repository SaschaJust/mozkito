package org.mozkito.issues.adaptive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.JDOMException;

public class GenerateXML {
	
	final Main main = new Main();
	
	public static void main(String[] args) throws Exception{
		
		final GenerateXML generate = new GenerateXML();
		generate.generate_jira_xml();
	}
	
	public void generate_jira_xml() throws IOException, JDOMException{
		String[] report_names = {"MTEST-1","MTEST-2","MTEST-3","MTEST-4","MTEST-5","MTEST-6","MTEST-7","MTEST-8",
				"MTEST-9","MTEST-10","MTEST-11","MTEST-13","MTEST-14","MTEST-15","MTEST-16","MTEST-17","MTEST-18"};
		
		int size = report_names.length;
		
		for(int i = 0 ; i < size; i++){
			System.out.println("https://issues.mozkito.org/browse/" + report_names[i]);
			Document doc = main.fetch("https://issues.mozkito.org/browse/" + report_names[i]);
			
			String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/JiraMozkito/" + report_names[i] + ".xml";  
			parse_test.write_genfile(doc, filepath);													//speichere xml-doc lokal als file
			
			
		}
	}
}
