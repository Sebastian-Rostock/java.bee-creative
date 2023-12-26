package bee.creative.qs.h2;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;
import bee.creative.util.HashSet2;

class H2QTSetNames extends HashSet2<String> implements Array<String> {

	@Override
	public String get(int index) {
		return this.customGetKey(index);
	}

	public int role(String name) throws NullPointerException {
		return this.getIndexImpl(Objects.notNull(name));
	}

	public String name(int index) throws IllegalArgumentException {
		try {
			return this.get(index);
		} catch (IndexOutOfBoundsException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	final List<String> list = new AbstractList<>() {

		@Override
		public String get(int index) {
			return H2QTSetNames.this.get(index);
		}

		@Override
		public int size() {
			return H2QTSetNames.this.size();
		}

	};

	H2QTSetNames(String... names) throws NullPointerException, IllegalArgumentException {
		this(Arrays.asList(names));
	}

	H2QTSetNames(List<String> names) throws NullPointerException, IllegalArgumentException {
		var size = names.size();
		this.allocate(size);
		for (var i = 0; i < size; i++) {
			this.putIndexImpl(Objects.notNull(names.get(i)));
		}
		if ((size == 0) || (size != this.size())) throw new IllegalArgumentException();
	}

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 2301871603459115562L;

}