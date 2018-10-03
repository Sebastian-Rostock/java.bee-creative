package bee.creative._dev_.sam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.util.Objects;

public class SAMValueBuilder extends SAMValue {

	protected List<SAMValue> array;

	protected String string;

	public SAMValueBuilder put(final SAMValue value) throws NullPointerException {
		this.array.add(Objects.notNull(value));
		return this;
	}

	public SAMValueBuilder setArray(final SAMValue... value) throws NullPointerException {
		return this.setArray(Arrays.asList(value));
	}

	public SAMValueBuilder setArray(final List<? extends SAMValue> value) throws NullPointerException {
		final List<SAMValue> array = new ArrayList<>(value);
		if (array.contains(null)) throw new NullPointerException();
		this.array = array;
		this.string = null;
		return this;
	}

	public SAMValueBuilder setString(final String value) throws NullPointerException {
		this.string = Objects.notNull(value);
		this.array = null;
		return this;
	}

	@Override
	public SAMValue get(final int index) {
		if (this.array == null) throw new IndexOutOfBoundsException();
		return this.array.get(index);
	}

	@Override
	public int length() {
		if (this.array == null) return 0;
		return this.array.size();
	}

	@Override
	public boolean isString() {
		return this.string != null;
	}

	@Override
	public String toString() {
		if (this.string == null) return "";
		return this.string;
	}

}
