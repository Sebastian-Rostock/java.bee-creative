package bee.creative.app.ft;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AppUtil {

	public static void wait(final Shell shell) {
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
