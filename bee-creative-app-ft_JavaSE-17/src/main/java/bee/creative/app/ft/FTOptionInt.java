package bee.creative.app.ft;

import java.awt.Component;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import bee.creative.util.Getter;
import bee.creative.util.Producer;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FTOptionInt implements Producer<Component>, Getter<Composite, Control> {

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
	public Component get() {
		final SpinnerNumberModel res = new SpinnerNumberModel(this.val, this.min, this.max, this.inc);
		res.addChangeListener(evt -> this.val = res.getNumber().intValue());
		return new JSpinner(res);
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
