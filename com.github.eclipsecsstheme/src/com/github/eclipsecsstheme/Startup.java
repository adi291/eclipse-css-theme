package com.github.eclipsecsstheme;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import com.github.eclipsecsstheme.preferences.CSSThemePreferencePage;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				String css = store.getString(CSSThemePreferencePage.CSS_PROP);
				try {
					CSSUtil.applyTheme(css);
				} catch (IOException e) {
					System.err.println(e.toString());
				}
			}
		});
	}

}
