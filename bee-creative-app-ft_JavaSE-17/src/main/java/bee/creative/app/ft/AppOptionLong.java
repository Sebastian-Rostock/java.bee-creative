package bee.creative.app.ft;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class AppOptionLong implements AppOption {

	public long getValue() {
		return this.val;
	}

	public AppOptionLong useValue(long value) {
		this.val = Math.min(Math.max(value, this.min), this.max);
		return this;
	}

	public long getMinimum() {
		return this.min;
	}

	public AppOptionLong useMinimum(long min) {
		this.min = min;
		return this.useMaximum(this.max);
	}

	public long getMaximum() {
		return this.max;
	}

	public AppOptionLong useMaximum(long value) {
		this.max = Math.max(this.min, value);
		return this.useValue(this.val);
	}

	public long getIncrease() {
		return this.inc;
	}

	public AppOptionLong useIncrease(long inc) {
		this.inc = Math.max(inc, 0);
		return this;
	}

	@Override
	public String get() {
		return Long.toString(this.val);
	}

	@Override
	public Control get(final Composite item) {
		final var res = new Text(item, SWT.BORDER);
		res.addListener(SWT.KeyDown, event -> {
			if (!(event.keyCode == SWT.ARROW_DOWN) && !(event.keyCode == SWT.ARROW_UP)) return;
			try {
				this.set(res.getText());
				this.val = event.keyCode == SWT.ARROW_DOWN //
					? ((this.val - this.min) > this.inc ? this.val - this.inc : this.min) //
					: ((this.max - this.val) > this.inc ? this.val + this.inc : this.max);
				var sel = res.getSelection();
				res.setText(this.get());
				res.setSelection(sel);
				event.doit = false;
			} catch (Exception ignore) {}
		});
		res.addListener(SWT.Modify, event -> {
			try {
				this.set(res.getText());
			} catch (Exception ignore) {
				event.doit = false;
			}
		});
		res.addListener(SWT.FocusOut, event -> {
			res.setText(this.get());
		});
		res.setText(this.get());
		return res;
	}

	@Override
	public void set(String value) {
		this.useValue(Long.parseLong(value));
	}

	private long val = 0;

	private long min = Long.MIN_VALUE;

	private long max = Long.MAX_VALUE;

	private long inc = 1;

}
