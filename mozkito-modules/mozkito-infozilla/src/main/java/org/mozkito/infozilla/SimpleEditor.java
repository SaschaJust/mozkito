/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.infozilla;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * The Class SimpleEditor.
 */
public class SimpleEditor extends JFrame implements Runnable, ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				new SimpleEditor();
			}
		});
	}
	
	/** The t pane. */
	private final JTextPane     tPane;
	
	/** The latch. */
	private CountDownLatch      latch        = null;
	
	/** The Constant COMMAND_NEXT. */
	private static final String COMMAND_NEXT = "command_next";
	
	private final JButton       nextButton;
	
	/**
	 * Instantiates a new simple editor.
	 */
	public SimpleEditor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(800, 1200);
		setLocation(0, 0);
		getContentPane().setLayout(new BorderLayout());
		
		this.tPane = new JTextPane();
		final JScrollPane jsp = new JScrollPane(this.tPane);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		final JToolBar toolBar = new JToolBar("Still draggable");
		this.nextButton = addButtons(toolBar);
		this.nextButton.setEnabled(false);
		add(toolBar, BorderLayout.PAGE_START);
		getContentPane().add(jsp, BorderLayout.CENTER);
		setVisible(true);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (COMMAND_NEXT.equals(e.getActionCommand())) {
				this.nextButton.setEnabled(false);
				if (this.latch != null) {
					this.latch.countDown();
					this.latch = null;
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Adds the buttons.
	 * 
	 * @param toolBar
	 *            the tool bar
	 * @return
	 */
	private JButton addButtons(final JToolBar toolBar) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final JButton button = makeNavigationButton("next", COMMAND_NEXT, "Get next entry", "Next");
			toolBar.add(button);
			return button;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Append to pane.
	 * 
	 * @param tp
	 *            the tp
	 * @param msg
	 *            the msg
	 * @param c
	 *            the c
	 */
	private void appendToPane(final JTextPane tp,
	                          final String msg,
	                          final Color c) {
		final StyleContext sc = StyleContext.getDefaultStyleContext();
		
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.white);
		
		final int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}
	
	/**
	 * Highlight.
	 * 
	 * @param region
	 *            the region
	 * @param color
	 *            the color
	 */
	public synchronized void highlight(final Region region,
	                                   final Color color) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				replaceInPane(SimpleEditor.this.tPane, region, color);
			}
		});
		
	}
	
	/**
	 * Load.
	 * 
	 * @param text
	 *            the text
	 * @param title
	 *            the title
	 * @return the count down latch
	 */
	public synchronized CountDownLatch load(final String text,
	                                        final String title) {
		this.latch = new CountDownLatch(1);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				try {
					SimpleEditor.this.tPane.getDocument().remove(0, SimpleEditor.this.tPane.getDocument().getLength());
					appendToPane(SimpleEditor.this.tPane, text, Color.DARK_GRAY);
					setTitle(title);
					SimpleEditor.this.nextButton.setEnabled(true);
				} catch (final BadLocationException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return this.latch;
	}
	
	/**
	 * Make navigation button.
	 * 
	 * @param imageName
	 *            the image name
	 * @param actionCommand
	 *            the action command
	 * @param toolTipText
	 *            the tool tip text
	 * @param altText
	 *            the alt text
	 * @return the j button
	 */
	protected JButton makeNavigationButton(final String imageName,
	                                       final String actionCommand,
	                                       final String toolTipText,
	                                       final String altText) {
		// Look for the image.
		final String imgLocation = "/images/" + imageName + ".png";
		final URL imageURL = getClass().getResource(imgLocation);
		
		// Create and initialize the button.
		final JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		
		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}
		
		return button;
	}
	
	/**
	 * Replace in pane.
	 * 
	 * @param tp
	 *            the tp
	 * @param region
	 *            the region
	 * @param c
	 *            the c
	 */
	private void replaceInPane(final JTextPane tp,
	                           final Region region,
	                           final Color c) {
		
		setVisible(true);
		final StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, c);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Menlo");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		
		// tp.getDocument().getLength();
		// tp.setCaretPosition(region.getFrom());
		tp.setSelectionStart(region.getFrom());
		tp.setSelectionEnd(region.getTo());
		tp.setCharacterAttributes(aset, false);
		tp.setCaretPosition(region.getFrom());
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			SwingUtilities.invokeLater(this);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
