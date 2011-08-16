package de.unisaarland.cs.st.reposuite.genealogies;


public enum ChangeGenealogyGranularity {
	TRANSACTION;
	
	public static String[] getStringValues() {
		ChangeGenealogyGranularity[] values = ChangeGenealogyGranularity.values();
		String[] result = new String[values.length];
		for (int i = 0; i < values.length; ++i) {
			result[i] = values[i].toString();
		}
		return result;
	}
}
