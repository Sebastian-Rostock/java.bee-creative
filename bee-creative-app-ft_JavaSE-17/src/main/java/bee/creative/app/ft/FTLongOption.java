package bee.creative.app.ft;

import java.awt.Component;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import bee.creative.util.Producer;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FTLongOption implements Producer<Component> {

	public long val;

	public long min;

	public long max;

	public long inc;

	public FTLongOption(final long val, final long min, final long max, final long inc) {
		this.val = val;
		this.min = min;
		this.max = max;
		this.inc = inc;
	}

	@Override
	public Component get() {
		final SpinnerNumberModel res = new SpinnerNumberModel(this.val, this.min, this.max, this.inc);
		res.addChangeListener(evt -> this.val =  res.getNumber().longValue());
		return new JSpinner(res);
	}

	@Override
	public String toString() {
		return Long.toString(this.val);
	}

}
