package de.unisaarland.cs.st.reposuite.output;

import java.util.HashMap;
import java.util.Map;

public class Console {
	
	public enum Color {
		BLACK ("\u001b[0;30m"), BLUE ("\u001b[0;34m"), CYAN ("\u001b[0;36m"), GREEN ("\u001b[0;32m"), MAGENTA (
		        "\u001b[0;35m"), NONE ("\u001b[m"), RED ("\u001b[0;31m"), WHITE ("\u001b[0;37m"), YELLOW (
		        "\u001b[0;33m"), UNDERLINE ("\u001b[04m");
		
		private final String code;
		
		private Color(final String code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code;
		}
	}
	
	public class Table {
		
		int                   width   = -1;
		int                   padding = 1;
		boolean               frame   = true;
		
		Map<Integer, Integer> ths     = new HashMap<Integer, Integer>();
		Map<Integer, Integer> tds     = new HashMap<Integer, Integer>();
		
		public Table(final int width, final int maxcols) {
			this.width = width;
			
			for (int i = 1; i <= maxcols; ++i) {
				this.ths.put(i, width / i - (i - 1));
				this.tds.put(i, width / i + width % i);
			}
		}
		
		public String getLine(final int columns,
		                      final String[] ths,
		                      final Object[] tds) {
			return null;
		}
		
		public void setupFrame(final boolean enable) {
			
		}
		
		public void setupFrameH(final String h) {
			
		}
		
		public void setupFrameV(final String v) {
			
		}
		
		public void setupPadding(final int padding) {
			
		}
		
		public void setupTD(final Color[] colors) {
			
		}
		
		public void setupTD(final int columns,
		                    final int tdwidth) {
			this.ths.put(columns, this.width / columns + this.width % columns - tdwidth);
		}
		
		public void setupTH(final Color[] colors) {
			
		}
		
		public void setupTH(final int columns,
		                    final int thwidth) {
			this.tds.put(columns, this.width / columns + this.width % columns - thwidth);
		}
		
		public void setupTR(final int width) {
			this.width = width;
			// TODO rescale
		}
	}
}
