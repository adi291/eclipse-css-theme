package com.github.csstheme.preferences;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.css.core.dom.ExtendedCSSRule;
import org.eclipse.e4.ui.css.core.dom.ExtendedDocumentCSS;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.WidgetElement;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;

import com.github.csstheme.Activator;

@SuppressWarnings("restriction")
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

			applyTheme(css);
		} catch (Exception e) {
			System.err.println(e.toString());
			return false;
		}
		return true;
	}

	private Display display = Display.getDefault();

	private StyleSheet findCustomThemeSheet(DocumentCSS documentCSS) {
		StyleSheet customThemeSheet = null;
		StyleSheetList styleSheets = documentCSS.getStyleSheets();
		SearchCustomThemeSheet: for (int i = 0; i < styleSheets.getLength(); i++) {
			StyleSheet eachSheet = styleSheets.item(i);
			if (eachSheet instanceof CSSStyleSheet) {
				CSSRuleList cssRules = ((CSSStyleSheet) eachSheet).getCssRules();
				for (int j = 0; j < cssRules.getLength(); j++) {
					ExtendedCSSRule rule = (ExtendedCSSRule) cssRules.item(j);
					SelectorList selectorList = rule.getSelectorList();
					String selectorText = selectorList.item(0).toString();
					if ("*[class=\"MPartStack\"]".equals(selectorText)) {
						customThemeSheet = eachSheet;
						break SearchCustomThemeSheet;
					}
				}
			}
		}
		return customThemeSheet;
	}

	private void applyTheme(String css) throws IOException {
		CSSEngine cssEngine = WidgetElement.getEngine(display);

		ExtendedDocumentCSS documentCSS = (ExtendedDocumentCSS) cssEngine.getDocumentCSS();
		StyleSheet customThemeSheet = findCustomThemeSheet(documentCSS);
		StyleSheet newSheet = cssEngine.parseStyleSheet(new StringReader(css));
		StyleSheetList oldSheetList = documentCSS.getStyleSheets();
		List<StyleSheet> newSheetList = new ArrayList<StyleSheet>();

		for (int i = 0; i < oldSheetList.getLength(); i++) {
			StyleSheet oldSheet = oldSheetList.item(i);
			if (oldSheet != customThemeSheet) {
				if (!newSheetList.contains(oldSheet))
					newSheetList.add(oldSheet);
			} else {
				if (!newSheetList.contains(newSheet))
					newSheetList.add(newSheet);
			}
		}

		documentCSS.removeAllStyleSheets();

		for (StyleSheet each : newSheetList) {
			documentCSS.addStyleSheet(each);
		}

		cssEngine.reapply();

		for (Shell each : Display.getDefault().getShells()) {
			each.layout(true, true);
		}
	}

}