package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

public class CMDExecutor {
	
	public static Tuple<Integer, List<String>> execute(String cmd, File dir) {
		List<String> lines = new ArrayList<String>();
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmd, new String[0], dir);
			int returnCode = p.waitFor();
			if (returnCode != 0) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Process `" + cmd.toString() + "` terminated abnomally.");
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
	
}
