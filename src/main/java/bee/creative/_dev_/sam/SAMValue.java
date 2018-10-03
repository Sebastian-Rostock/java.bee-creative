package bee.creative._dev_.sam;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterators;

public abstract class SAMValue implements Items<SAMValue>, Iterable<SAMValue> {

	public static SAMValue from(final Object object) throws IllegalArgumentException {
		if (object instanceof SAMValue) return (SAMValue)object;
		if (object instanceof String) return SAMValue.fromString((String)object);
		if (object instanceof List<?>) return SAMValue.fromArray((List<?>)object);
		if (object instanceof Object[]) return SAMValue.fromArray((Object[])object);
		throw new IllegalArgumentException();
	}

	public static SAMValue fromArray(final List<?> array) {
		final SAMValueBuilder result = new SAMValueBuilder().setArray();
		for (final Object item: array) {
			result.put(SAMValue.from(item));
		}
		return result;
	}

	public static SAMValue fromArray(final Object... array) {
		return SAMValue.fromArray(Arrays.asList(array));
	}

	public static SAMValue fromString(final String string) {
		return new SAMValueBuilder().setString(string);
	}

	@Override
	public abstract SAMValue get(int index);

	// länge oder 0
	public abstract int length();

	public boolean isArray() {
		return !this.isString();
	}

	public abstract boolean isString();

	// array oder leer
	public SAMValue[] toArray() {
		final int l = this.length();
		final SAMValue[] result = new SAMValue[l];
		for (int i = 0; i < l; i++) {
			result[i] = this.get(i);
		}
		return result;
	}

	// string oder leer
	@Override
	public abstract String toString();

	@Override
	public Iterator<SAMValue> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

}
