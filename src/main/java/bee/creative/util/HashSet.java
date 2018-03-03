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

	{}

	/** Diese Methode gibt ein neues {@link HashSet} zurück, welche Streuwert und Äquivalenz der Elemente über den gegebenen {@link Hasher} ermittelt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param hasher Methoden zum Abgleich der Elemente.
	 * @return An {@link Hasher} gebundenes {@link HashSet}.
	 * @throws NullPointerException Wenn {@code hasher} {@code null} ist. */
	public static <GItem> HashSet<GItem> from(final Hasher hasher) throws NullPointerException {
		Objects.assertNotNull(hasher);
		return new HashSet<GItem>() {

			private static final long serialVersionUID = -1097708178888446196L;

			@Override
			protected int customHash(final Object item) {
				return hasher.hash(item);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object item) {
				return hasher.equals(this.items[entryIndex], item);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object item, final int itemHash) {
				return hasher.equals(this.items[entryIndex], item);
			}

		};
	}

	{}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] items = AbstractHashData.EMPTY_OBJECTS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit dem Inhalt des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(final Set<? extends GItem> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit dem Inhalt der gegebenen {@link Collection}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(final Collection<? extends GItem> source) {
		this.addAll(source);
	}

	{}

	@SuppressWarnings ({"javadoc", "unchecked"})
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			this.putKeyImpl((GItem)stream.readObject());
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
	@SuppressWarnings ("unchecked")
	protected GItem customGetKey(final int entryIndex) {
		return (GItem)this.items[entryIndex];
	}

	/** {@inheritDoc} */
	@Override
	protected void customSetKey(final int entryIndex, final GItem item, final int itemHash) {
		this.items[entryIndex] = item;
	}

	/** {@inheritDoc} */
	@Override
	protected int customHash(final Object item) {
		return Objects.hash(item);
	}

	/** {@inheritDoc} */
	@Override
	protected int customHashKey(final int entryIndex) {
		return Objects.hash(this.items[entryIndex]);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object item) {
		return Objects.equals(this.items[entryIndex], item);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object item, final int keyHash) {
		return Objects.equals(this.items[entryIndex], item);
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
			items2 = AbstractHashData.EMPTY_OBJECTS;
		} else {
			items2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				items2[targetIndex] = HashSet.this.items[sourceIndex];
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
			if (this.capacityImpl() == 0) return result;
			result.items = this.items.clone();
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
