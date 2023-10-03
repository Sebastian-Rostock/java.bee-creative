package bee.creative.app.ft;

import org.eclipse.swt.graphics.Rectangle;
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

	public static void center(final Shell shell) {
		final var srcRect = shell.getBounds();
		final var tgtRect = shell.getDisplay().getPrimaryMonitor().getBounds();
		shell.setLocation(tgtRect.x + ((tgtRect.width - srcRect.width) / 2), tgtRect.y + ((tgtRect.height - srcRect.height) / 2));
	}

	public static void openAndWait(final Shell shell) {
		shell.open();
		wait(shell);
	}

}
