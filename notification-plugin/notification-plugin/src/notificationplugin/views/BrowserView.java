package notificationplugin.views;


import jakarta.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view with html and javascript content. The view 
 * shows how data can be exchanged between Java and JavaScript.
 */

public class BrowserView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tes1.views.BrowserView";

	@Inject
	Shell shell;


	private Browser fBrowser;

	@Override
	public void createPartControl(Composite parent) {
		fBrowser = new Browser(parent, SWT.EDGE);
		//txtUrl.setText("http://localhost:8080/");

		fBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		parent.getDisplay().asyncExec(() -> {
			setUrl();
		});

	}
	
	private void setUrl() {
		String url = "http://localhost:7777/alertError";
		if (url != null && url.trim().length() > 0) {
			fBrowser.setUrl(url);
		}
	}




	@Override
	public void setFocus() {
		fBrowser.setFocus();
	}



}
