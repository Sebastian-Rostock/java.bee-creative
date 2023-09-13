package bee.creative.app.ft;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import bee.creative.lang.Objects;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class AppOptionText implements AppOption {

	public String getValue() {
		return this.value;
	}

	public AppOptionText useValue(String value) {
		this.value = value.toString();
		return this;
	}

	public AppOptionText putValue() {
		if (this.value.isEmpty()) return this;
		this.options.remove(this.value);
		this.options.add(0, this.value);
		return this;
	}

	public List<String> getOptions() {
		return new ArrayList<>(this.options);
	}

	public AppOptionText useOptions(List<String> value) {
		this.options = Iterables.filter(value, Filters.negate(String::isEmpty)).toList();
		return this;
	}

	@Override
	public String get() {
		return this.value;
	}

	@Override
	public Control get(final Composite item) {
		final var res = new Combo(item, SWT.BORDER);
		res.setText(Objects.notNull(this.value, ""));
		res.setItems(this.options.toArray(new String[0]));
		res.addModifyListener(event -> this.value = res.getText());
		return res;
	}

	@Override
	public void set(String value) {
		this.value = value;
	}

	private String value = "";

	private List<String> options = new ArrayList<>();

}