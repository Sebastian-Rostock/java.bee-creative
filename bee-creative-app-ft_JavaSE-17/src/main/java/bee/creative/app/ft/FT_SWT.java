package bee.creative.app.ft;

import org.eclipse.swt.widgets.Display;

public class FT_SWT {

	
	public static void main(String[] args) throws Exception {
		var d = new Display();
		while (!d.isDisposed()) {
			if (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		
	}
	
}
