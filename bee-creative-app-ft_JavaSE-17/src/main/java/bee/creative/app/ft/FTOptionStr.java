package bee.creative.app.ft;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import bee.creative.lang.Objects;
import bee.creative.util.Getter;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FTOptionStr implements Getter<Composite, Control> {

	public String val;

	public FTOptionStr(final String val) {
		this.val = val;
	}

	@Override
	public Control get(final Composite item) {
		final var res = new Text(item, SWT.BORDER);
		res.setText(Objects.notNull(this.val, ""));
		res.addModifyListener(event -> this.val = res.getText());
		return res;
	}

	@Override
	public String toString() {
		return String.valueOf(this.val);
	}

}