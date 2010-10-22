package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

public class CMDExecutor {
	
	public static Tuple<Integer, List<String>> execute(String cmd, File dir) {
		return execute(cmd, dir, (Collection<String>) null);
	}
	
	public static Tuple<Integer, List<String>> execute(String cmd, File dir, Collection<String> pipeTo) {
		List<String> lines = new ArrayList<String>();
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmd, new String[0], dir);
			if (pipeTo != null) {
				for (String input : pipeTo) {
					p.getOutputStream().write(input.getBytes());
				}
			}
			int returnCode = p.waitFor();
			if (returnCode != 0) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Process `" + cmd.toString() + "` terminated abnormally.");
				}
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			if (returnCode != 0) {
				while ((line = input.readLine()) != null) {
					if (RepoSuiteSettings.logError()) {
						Logger.error(line);
					}
				}
			} else {
				while ((line = input.readLine()) != null) {
					lines.add(line);
				}
			}
			input.close();
			return new Tuple<Integer, List<String>>(returnCode, lines);
		} catch (Exception err) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(err.getMessage());
			}
			return new Tuple<Integer, List<String>>(-1, lines);
		}
	}
	
	public static Tuple<Integer, List<String>> execute(String cmd, File dir, String line) {
		LinkedList<String> list = new LinkedList<String>();
		list.add(line);
		return execute(cmd, dir, list);
	}
}
