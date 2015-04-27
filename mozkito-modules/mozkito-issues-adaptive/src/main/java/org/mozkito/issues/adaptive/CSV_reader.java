package org.mozkito.issues.adaptive;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;


//Felder die belegt werden:
//	1		nummer
//	2		attachments
//	3		comments
//	4		description
//	5		id
//	6		lastUpdatedTS
//	7		personContainer
//	8		priority
//	9		product
//	10		resolution
//	11		resolutionTS
//	12		severity
//	13		status
//	14		subject
//	15		summary
//	16		type
//	17		keywords


public class CSV_reader {
		
		@SuppressWarnings("hiding")
		public static void main(String[] args) throws Exception{
			
			
			InputStream is = null;

		    try {
		        is = new FileInputStream("/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/csv-test.csv");

		        //is.close(); 
		    } catch (FileNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		    read_in(is);
		}    
			
		public static String[]	read_in(InputStream is){
		    
		    int NUMBER_OF_COLUMNS = 1;
			CSVReader reader = null;
			try {
				reader = new CSVReader(new BufferedReader(new InputStreamReader(is)), ' ');
				String[] line = null;
				String[] reports = new String [18];
		
				while ((line = reader.readNext()) != null) {
					int tmp = 0;
//					System.out.println(line.length);
//					for (int i= 0; i<line.length ; i++){
//						System.out.println(line[i]);
//					}
					if (line.length < NUMBER_OF_COLUMNS) {
						try {
							reader.close();
						} catch (final IOException e) {
							// ignore
						}
						throw new RuntimeException();						
					} else {
						
						reports[tmp] = line[0];
						for (int i= 1; i<line.length ; i++){
							reports[tmp] = reports[tmp] + "," +line[i];
						}
						System.out.println(reports[tmp]);
						// use data in line[]
						// ...
					}
					tmp++;
				}
		
				reader.close();
				return reports;
			} catch (final IOException e) {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (final IOException e2) {
					// ignore
				}
				throw new UnrecoverableError(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException ignore) {
						// ignore
					}
				}
			}
		}		
}
