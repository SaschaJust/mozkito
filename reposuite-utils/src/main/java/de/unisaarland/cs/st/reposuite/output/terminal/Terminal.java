package de.unisaarland.cs.st.reposuite.output.terminal;

import de.unisaarland.cs.st.reposuite.output.TextSink;
import de.unisaarland.cs.st.reposuite.output.formatting.FontSettings;
import de.unisaarland.cs.st.reposuite.output.formatting.LineStyle;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.formatting.Size.Unit;
import de.unisaarland.cs.st.reposuite.output.formatting.Style;
import de.unisaarland.cs.st.reposuite.output.nodes.Content;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;

public abstract class Terminal implements TextSink {
	
	public static String expand(final char character,
	                            final int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			builder.append(character);
		}
		return builder.toString();
	}
	
	public static String getHorizontalLine(final int width,
	                                       final LineStyle style) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < width;) {
			String line = getHorizontalLine(style, (i + 1) % 2 == 0);
			if (builder.length() + line.length() > width) {
				builder.append(line.substring(0, width - builder.length()));
			} else {
				builder.append(line);
			}
			i += Math.max(line.length(), 1);
		}
		return builder.toString();
	}
	
	public static String getHorizontalLine(final LineStyle style,
	                                       final boolean even) {
		switch (style) {
			case SOLID:
				return "-";
			case DOTTED:
				return even
				           ? " "
				           : "*";
			case DASHED:
				return even
				           ? " "
				           : "-";
			default:
				return "";
		}
	}
	
	public static String getVerticalLine(final int height,
	                                     final LineStyle style) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < height;) {
			String line = getVerticalLine(style, (i + 1) % 2 == 0);
			if (builder.length() + line.length() > height) {
				builder.append(line.substring(0, height - builder.length()));
			} else {
				builder.append(line);
			}
			i += Math.max(line.length(), 1);
		}
		return builder.toString();
	}
	
	public static String getVerticalLine(final LineStyle style,
	                                     final boolean even) {
		switch (style) {
			case SOLID:
				return "|";
			case DOTTED:
				return even
				           ? " "
				           : "*";
			case DASHED:
				return even
				           ? " "
				           : "|";
			default:
				return "";
		}
	}
	
	public static String toText(final Content content) {
		StringBuilder builder = new StringBuilder();
		
		String leftMargin = Terminal.expand(' ', content.getStyle().getMargin().getLeft().convert(Unit.UNIT).getValue());
		String rightMargin = Terminal.expand(' ', content.getStyle().getMargin().getRight().convert(Unit.UNIT)
		                                                 .getValue());
		
		String leftPadding = Terminal.expand(' ', content.getStyle().getPadding().getLeft().convert(Unit.UNIT)
		                                                 .getValue());
		String rightPadding = Terminal.expand(' ', content.getStyle().getPadding().getRight().convert(Unit.UNIT)
		                                                  .getValue());
		
		Terminal terminal = new VT100Terminal();
		Style frameStyle = new Style();
		frameStyle.setBackgroundColor(content.getStyle().getBackgroundColor());
		frameStyle.setFontSettings(content.getStyle().getFontSettings());
		frameStyle.getFontSettings().setColor(content.getStyle().getFrame().getColor());
		String plainLeftFrame = Terminal.getVerticalLine(1, content.getStyle().getFrame().getLeft());
		String plainRightFrame = Terminal.getVerticalLine(1, content.getStyle().getFrame().getRight());
		String leftFrame = terminal.getString(frameStyle, plainLeftFrame);
		String rightFrame = terminal.getString(frameStyle, plainRightFrame);
		
		int contentLength = content.getContent().getLength();
		int totalContentLength = contentLength + plainLeftFrame.length() + leftPadding.length()
		        + plainRightFrame.length() + rightPadding.length();
		StringBuilder empty = new StringBuilder();
		empty.append(leftMargin);
		empty.append(leftFrame);
		empty.append(leftPadding);
		Terminal.expand(' ', contentLength);
		empty.append(rightMargin);
		empty.append(rightFrame);
		empty.append(rightPadding);
		
		String topFrame = terminal.getString(frameStyle,
		                                     content.getStyle().getFrame().getTop() != LineStyle.NONE
		                                                                                             ? Terminal.getHorizontalLine(totalContentLength,
		                                                                                                                          content.getStyle()
		                                                                                                                                 .getFrame()
		                                                                                                                                 .getTop())
		                                                                                             : "");
		
		String bottomFrame = terminal.getString(frameStyle,
		                                        content.getStyle().getFrame().getBottom() != LineStyle.NONE
		                                                                                                   ? Terminal.getHorizontalLine(totalContentLength,
		                                                                                                                                content.getStyle()
		                                                                                                                                       .getFrame()
		                                                                                                                                       .getBottom())
		                                                                                                   : "");
		
		Size topMargin = content.getMargin().getTop().convert(Unit.UNIT);
		for (int i = 0; i < topMargin.getValue(); ++i) {
			builder.append(empty).append(FileUtils.lineSeparator);
		}
		
		if (content.getStyle().getFrame().getTop() != LineStyle.NONE) {
			for (int i = 0; i < content.getStyle().getFrame().getWidth().convert(Unit.UNIT).getValue(); ++i) {
				builder.append(topFrame).append(FileUtils.lineSeparator);
			}
		}
		
		Size topPadding = content.getPadding().getTop().convert(Unit.UNIT);
		for (int i = 0; i < topPadding.getValue(); ++i) {
			builder.append(empty).append(FileUtils.lineSeparator);
		}
		
		String text = content.getContent().toText();
		String[] strings = text.split(FileUtils.lineSeparator);
		StringBuilder contentBuilder = new StringBuilder();
		for (String string : strings) {
			if (string.length() > 0) {
				if (contentBuilder.length() > 0) {
					contentBuilder.append(FileUtils.lineSeparator);
				}
				contentBuilder.append(leftMargin).append(leftFrame).append(leftPadding).append(string)
				              .append(rightPadding).append(rightFrame).append(rightMargin);
			}
		}
		builder.append(contentBuilder);
		
		Size bottomPadding = content.getPadding().getBottom().convert(Unit.UNIT);
		for (int i = 0; i < bottomPadding.getValue(); ++i) {
			builder.append(empty).append(FileUtils.lineSeparator);
		}
		
		if (content.getStyle().getFrame().getBottom() != LineStyle.NONE) {
			for (int i = 0; i < content.getStyle().getFrame().getWidth().convert(Unit.UNIT).getValue(); ++i) {
				builder.append(FileUtils.lineSeparator).append(bottomFrame);
			}
		}
		
		Size bottomMargin = content.getMargin().getBottom().convert(Unit.UNIT);
		for (int i = 0; i < bottomMargin.getValue(); ++i) {
			builder.append(empty).append(FileUtils.lineSeparator);
		}
		
		return builder.toString();
	}
	
	public abstract String getString(Style style,
	                                 String string);
	
	@Override
	public void setFontSettings(final FontSettings settings) {
		setFontColor(settings.getColor());
		setFontDecoration(settings.getDecoration());
		setFontSize(settings.getSize());
		setFontStyle(settings.getStyle());
		setFontWeight(settings.getWeight());
	}
	
}
