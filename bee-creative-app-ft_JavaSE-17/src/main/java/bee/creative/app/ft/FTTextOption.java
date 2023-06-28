package bee.creative.app.ft;

import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import bee.creative.util.Getter;
import bee.creative.util.Producer;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FTTextOption implements Producer<Component> {

	public String val;

	public FTTextOption(final String val) {
		this.val = val;
	}

	@Override
	public Component get() {
		final JTextField res = new JTextField(this.val);
		res.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent event) {
				FTTextOption.this.val = res.getText();
			}

			@Override
			public void insertUpdate(final DocumentEvent event) {
				FTTextOption.this.val = res.getText();
			}

			@Override
			public void changedUpdate(final DocumentEvent event) {
			}

		});
		return res;
	}

	@Override
	public String toString() {
		return String.valueOf(this.val);
	}

}