package bee.creative.app.ft;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import bee.creative.fem.FEMDatetime;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class AppOptionTime implements AppOption {

	@Override
	public String get() {
		return this.value.toString();
	}

	@Override
	public void set(String val) {
		this.useValue(FEMDatetime.from(val).toTime());
	}

	public long getValue() {
		return this.value.toTime();
	}

	public AppOptionTime useValue(long value) {
		this.value = FEMDatetime.from((value / 1000) * 1000).withZone(0);
		return this;
	}

	@Override
	public Control get(Composite item) {
		var lay = new GridLayout(2, true);
		lay.marginWidth = 0;
		lay.marginHeight = 0;
		var dat = new GridData();
		dat.horizontalAlignment = SWT.FILL;
		dat.grabExcessHorizontalSpace = true;
		var res = new Composite(item, SWT.NONE);
		res.setLayout(lay);
		var date = new DateTime(res, SWT.DROP_DOWN);
		date.setLayoutData(dat);
		date.addListener(SWT.Selection, event -> {
			this.value = this.value.withDate(date.getYear(), date.getMonth() + 1, date.getDay());
			System.out.println(this.get());
		});
		var time = new DateTime(res, SWT.TIME);
		time.setLayoutData(dat);
		time.addListener(SWT.Selection, event -> {
			this.value = this.value.withTime(time.getHours(), time.getMinutes(), time.getSeconds(), 0);
			System.out.println(this.get());
		});
		date.setDate(this.value.yearValue(), this.value.monthValue() - 1, this.value.dateValue());
		time.setTime(this.value.hourValue(), this.value.minuteValue(), this.value.secondValue());
		return res;
	}

	private FEMDatetime value;

	{
		this.useValue(System.currentTimeMillis());
	}

}