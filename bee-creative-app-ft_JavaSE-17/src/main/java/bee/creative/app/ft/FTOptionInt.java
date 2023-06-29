package bee.creative.app.ft;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import bee.creative.util.Getter;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTOptionInt implements Getter<Composite, Control> {

	public int val;

	public int min;

	public int max;

	public int inc;

	public FTOptionInt(final int val, final int min, final int max, final int inc) {
		this.val = val;
		this.min = min;
		this.max = max;
		this.inc = inc;
	}

	@Override
	public Control get(final Composite item) {
		final var res = new Spinner(item, SWT.BORDER);
		res.setMinimum(this.min);
		res.setMaximum(this.max);
		res.setSelection(this.val);
		res.setIncrement(this.inc);
		res.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> this.val = res.getSelection()));
		return res;
	}

	@Override
	public String toString() {
		return Long.toString(this.val);
	}

}
