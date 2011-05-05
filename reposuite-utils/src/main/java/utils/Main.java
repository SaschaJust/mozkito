/**
 * 
 */
package utils;

import de.unisaarland.cs.st.reposuite.output.formatting.BasicColor;
import de.unisaarland.cs.st.reposuite.output.formatting.FontDecoration;
import de.unisaarland.cs.st.reposuite.output.formatting.FontStyle;
import de.unisaarland.cs.st.reposuite.output.formatting.FontWeight;
import de.unisaarland.cs.st.reposuite.output.formatting.Frame;
import de.unisaarland.cs.st.reposuite.output.formatting.LineStyle;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Cell;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Row;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Table;
import de.unisaarland.cs.st.reposuite.output.terminal.Terminal;
import de.unisaarland.cs.st.reposuite.output.terminal.TerminalTable;
import de.unisaarland.cs.st.reposuite.output.terminal.VT100Terminal;
import de.unisaarland.cs.st.reposuite.output.terminal.VT100TerminalBackgroundColor;
import de.unisaarland.cs.st.reposuite.output.terminal.VT100TerminalFontColor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		Terminal terminal = new VT100Terminal();
		for (BasicColor foreground : BasicColor.values()) {
			for (BasicColor background : BasicColor.values()) {
				for (FontDecoration decoration : FontDecoration.values()) {
					for (FontStyle style : FontStyle.values()) {
						for (FontWeight weight : FontWeight.values()) {
							terminal.setFontColor(new VT100TerminalFontColor(foreground));
							terminal.setBackground(new VT100TerminalBackgroundColor(background));
							terminal.setFontDecoration(decoration);
							terminal.setFontStyle(style);
							terminal.setFontWeight(weight);
							terminal.printLine("Foreground: " + foreground + ", background: " + background
							        + ", decoration: " + decoration + ", style: " + style + ", weight: " + weight);
						}
					}
				}
			}
		}
		
		Table table = new TerminalTable(4);
		Row row = table.createRow();
		row.setFrame(new Frame(LineStyle.DOTTED, new VT100TerminalFontColor(BasicColor.RED)));
		Cell cell = table.createCell("Hello");
		cell.setFrame(new Frame(LineStyle.SOLID, new VT100TerminalFontColor(BasicColor.GREEN)));
		row.setCell(0, cell);
		row.setCell(2, row.createCell("world"));
		table.addRow(row);
		terminal.printTable(table);
	}
}
