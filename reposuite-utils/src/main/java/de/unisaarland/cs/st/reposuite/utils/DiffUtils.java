package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import difflib.Chunk;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.Patch;

public class DiffUtils {
	
	private static List<String> fileToLines(final String filename) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public static HashSet<Integer> getLineNumbers(final Chunk chunk) {
		HashSet<Integer> result = new HashSet<Integer>();
		int startPos = chunk.getPosition();
		for (int i = 0; i < chunk.getSize(); ++i) {
			result.add(startPos + i + 1);
		}
		return result;
	}
	
	public static void main(final String[] args) {
		List<String> original = fileToLines("/Users/kim/Downloads/1.txt");
		List<String> revised = fileToLines("/Users/kim/Downloads/2.txt");
		
		// Compute diff. Get the Patch object. Patch is the container for computed deltas.
		Patch patch = difflib.DiffUtils.diff(original, revised);
		
		for (Delta delta : patch.getDeltas()) {
			System.out.println(delta);
			System.out.println(delta instanceof DeleteDelta);
			System.out.println(delta.getRevised().getSize());
		}
	}
}
