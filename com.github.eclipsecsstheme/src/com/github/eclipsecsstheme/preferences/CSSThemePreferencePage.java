package com.github.eclipsecsstheme.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.github.eclipsecsstheme.Activator;
import com.github.eclipsecsstheme.CSSUtil;

public class CSSThemePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String CSS_PROP = "preferenceCSS";
	private StringFieldEditor editor;

	public CSSThemePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Customize appearance via CSS");
	}

	public void init(IWorkbench workbench) {
	}

	public void createFieldEditors() {
		editor = new StringFieldEditor(CSS_PROP, "", -1, 25, 0, getFieldEditorParent());
		addField(editor);
	}

	@Override
	public boolean performOk() {
		try {
			String css = editor.getStringValue();
			getPreferenceStore().setValue(CSS_PROP, css);

			CSSUtil.applyTheme(css);
		} catch (Exception e) {
			System.err.println(e.toString());
			return false;
		}
		return true;
	}

}