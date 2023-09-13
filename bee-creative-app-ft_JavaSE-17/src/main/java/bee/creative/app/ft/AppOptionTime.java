package bee.creative.app.ft;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import bee.creative.fem.FEMDatetime;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class AppOptionTime implements AppOption {

	@Override
	public String get() {
		return this.val.toString();
	}

	@Override
	public void set(final String val) {
		this.useValue(FEMDatetime.from(val).toTime());
	}

	public long getValue() {
		return this.val.toTime();
	}

	public AppOptionTime useValue(long value) {
		this.val = FEMDatetime.from((value / 1000) * 1000).withZone(0);
		return this;
	}

	@Override
	public Control get(final Composite item) {
		final var lay = new GridLayout(2, true);

		lay.marginWidth = 0;
		lay.marginHeight = 0;
		var dat = new GridData();
		dat.horizontalAlignment = SWT.FILL;
		dat.grabExcessHorizontalSpace = true;
		final var res = new Composite(item, SWT.NONE);
		res.setLayout(lay);
		var date = new DateTime(res, SWT.DROP_DOWN);
		date.setLayoutData(dat);
		date.addListener(SWT.Selection, event -> {
			this.val = this.val.withDate(date.getYear(), date.getMonth() + 1, date.getDay());
			System.out.println(this.get());
		});
		var time = new DateTime(res, SWT.TIME);
		time.setLayoutData(dat);
		time.addListener(SWT.Selection, event -> {
			this.val = this.val.withTime(time.getHours(), time.getMinutes(), time.getSeconds(), 0);
			System.out.println(this.get());
		});
		date.setDate(this.val.yearValue(), this.val.monthValue() - 1, this.val.dateValue());
		time.setTime(this.val.hourValue(), this.val.minuteValue(), this.val.secondValue());
		return res;
	}

	private FEMDatetime val;

	{
		this.useValue(System.currentTimeMillis());
	}

}