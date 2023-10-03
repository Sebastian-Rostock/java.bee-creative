package bee.creative.app.ft;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class AppOptionLong implements AppOption {

	public long getValue() {
		return this.value;
	}

	public AppOptionLong useValue(long value) {
		this.value = Math.min(Math.max(value, this.minimum), this.maximum);
		return this;
	}

	public long getIncrease() {
		return this.increase;
	}

	public AppOptionLong useIncrease(long inc) {
		this.increase = Math.max(inc, 0);
		return this;
	}

	public long getMinimum() {
		return this.minimum;
	}

	public AppOptionLong useMinimum(long min) {
		this.minimum = min;
		return this.useMaximum(this.maximum);
	}

	public long getMaximum() {
		return this.maximum;
	}

	public AppOptionLong useMaximum(long value) {
		this.maximum = Math.max(this.minimum, value);
		return this.useValue(this.value);
	}

	@Override
	public String get() {
		return NumberFormat.getInstance(Locale.getDefault()).format(this.value);
	}

	@Override
	public Control get(Composite item) {
		var res = new Text(item, SWT.BORDER);
		res.addListener(SWT.KeyDown, event -> {
			if (!(event.keyCode == SWT.ARROW_DOWN) && !(event.keyCode == SWT.ARROW_UP)) return;
			try {
				this.set(res.getText());
				this.value = event.keyCode == SWT.ARROW_DOWN //
					? ((this.value - this.minimum) > this.increase ? this.value - this.increase : this.minimum) //
					: ((this.maximum - this.value) > this.increase ? this.value + this.increase : this.maximum);
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
		try {
			this.useValue(NumberFormat.getInstance(Locale.getDefault()).parse(value).longValue());
		} catch (ParseException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	private long value;

	private long increase = 1;

	private long minimum = Long.MIN_VALUE;

	private long maximum = Long.MAX_VALUE;

}
