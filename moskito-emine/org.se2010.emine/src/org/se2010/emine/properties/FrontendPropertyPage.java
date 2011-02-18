package org.se2010.emine.properties;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FrontendPropertyPage extends org.eclipse.ui.dialogs.PropertyPage{

	protected static final int TEXT_FIELD_WIDTH = 200;
	protected static final int TEXT_FIELD_HEIGHT = 20;
	GridData data;
	Text hovertime;
	
	public FrontendPropertyPage(){
		super();
		this.data = new GridData();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		Composite myComposite = createDefaultComposite(parent);

//        Label space0 = new Label(myComposite,SWT.NONE);
//        Label space1 = new Label(myComposite,SWT.NONE);

		
//		Composite ch_tool = new Composite(myComposite, SWT.NONE);
//		BooleanFieldEditor tooltip = new BooleanFieldEditor("BOOL",
//       		"Enable Tooltip", ch_tool);

		Composite hover = new Composite(myComposite,SWT.NONE);
		GridLayout hover_grid = new GridLayout();
        hover_grid.numColumns = 2;
        hover.setLayout(hover_grid);

        Label hoverLabel = new Label(hover, SWT.NONE);
        hoverLabel.setText("Hovertime in seconds" );

        hovertime = new Text(hover, SWT.WRAP | SWT.BORDER);
        GridData tool_input = new GridData();
        tool_input.widthHint = 30;
        hovertime.setLayoutData(tool_input);
        hovertime.setText("3");
        
//        Composite ch_highl = new Composite(myComposite, SWT.NONE);
//        BooleanFieldEditor highlight = new BooleanFieldEditor(
//        		"BOOL","Enable Highlighting", ch_highl);
//        Label empty = new Label(myComposite,SWT.NONE);
//        Label empty2 = new Label(myComposite,SWT.NONE);
        Composite mch_highl = new Composite(myComposite, SWT.NONE);
        new RadioGroupFieldEditor("choice","  Highlight Servity:",1,
        		new String[][] { { "Info","choice1"}, {"Warning", "choice2"},
        		{"Error", "choice3" }
        		}, mch_highl);
            
        return myComposite;

	}
	
    private Composite createDefaultComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 50;
        layout.verticalSpacing = 10;
        
        composite.setLayout(layout);

        data.verticalAlignment = GridData.FILL_VERTICAL;
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.widthHint =  TEXT_FIELD_WIDTH;
        data.heightHint = TEXT_FIELD_HEIGHT;
        composite.setLayoutData(data);

        return composite;
}


}
