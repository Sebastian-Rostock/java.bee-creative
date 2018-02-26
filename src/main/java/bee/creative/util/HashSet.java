package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/** Diese Klasse implementiert ein auf {@link AbstractHashSet} aufbauendes {@link Set} mit beliebigen Elementen.<br>
 * Das {@link #contains(Object) Finden} sowie {@link #add(Object) Einfügen} von Elementen benötigt ca. 60 % der Rechenzeit, die ein {@link java.util.HashSet}
 * benötigen würde. Das {@link #remove(Object) Entfernen} von Elementen liegt dazu bei ca. 80 % der Rechenzeit.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet<GItem> extends AbstractHashSet<GItem> implements Serializable {

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 1947961515821394540L;

	/** Dieses Feld speichert den initialwert für {@link #items}. */
	static final Object[] EMPTY_OBJECTS = {};

	{}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] items = HashSet.EMPTY_OBJECTS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} als Kopie des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(final Set<? extends GItem> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} als Kopie des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(final Collection<? extends GItem> source) {
		this.addAll(source);
	}

	{}

	@SuppressWarnings ("javadoc")
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			@SuppressWarnings ("unchecked")
			final GItem item = (GItem)stream.readObject();
			this.putKeyImpl(item);
		}
	}

	@SuppressWarnings ("javadoc")
	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.count);
		for (final GItem item: this) {
			stream.writeObject(item);
		}
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected GItem customGetKey(final int entryIndex) {
		@SuppressWarnings ("unchecked")
		final GItem result = (GItem)this.items[entryIndex];
		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected void customSetKey(final int entryIndex, final GItem key, final int keyHash) {
		this.items[entryIndex] = key;
	}

	/** {@inheritDoc} */
	@Override
	protected int customHash(final Object key) {
		return Objects.hash(key);
	}

	/** {@inheritDoc} */
	@Override
	protected int customHashKey(final int entryIndex) {
		return Objects.hash(this.items[entryIndex]);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key) {
		return Objects.equals(this.items[entryIndex], key);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
		return Objects.equals(this.items[entryIndex], key);
	}

	/** {@inheritDoc} */
	@Override
	protected void customClear() {
		Arrays.fill(this.items, null);
	}

	/** {@inheritDoc} */
	@Override
	protected void customClearKey(final int entryIndex) {
		this.items[entryIndex] = null;
	}

	/** {@inheritDoc} */
	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] items2;
		if (capacity == 0) {
			items2 = HashSet.EMPTY_OBJECTS;
		} else {
			items2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				items2[sourceIndex] = HashSet.this.items[targetIndex];
			}

			@Override
			public void apply() {
				HashSet.this.items = items2;
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			final HashSet<?> result = (HashSet<?>)super.clone();
			if (this.capacityImpl() == 0) {
				result.items = HashSet.EMPTY_OBJECTS;
			} else {
				result.items = this.items.clone();
			}
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
