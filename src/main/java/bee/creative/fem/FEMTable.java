package bee.creative.fem;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.util.AbstractHashData;
import bee.creative.util.Builders.MapBuilder;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Getter;
import bee.creative.util.HashMap;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte, unveränderliche Tabelle mit einer Schlüssel- und einer Wertspalte als {@link FEMValue}-Variante einer
 * {@link Map}. Der {@link Entry#getValue() Wert} zu einem {@link Entry#getKey() Schlüssel} kann über {@link #get(FEMValue)} ermittelt werden.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMTable extends FEMValue implements Items<Entry<FEMValue, FEMValue>>, Getter<FEMValue, FEMValue>, Iterable<Entry<FEMValue, FEMValue>> {

	@SuppressWarnings ("javadoc")
	public static final class HashTable extends FEMTable implements Emuable {

		static class Keys extends FEMArray {

			final Entries entries;

			Keys(final Entries entries) throws IllegalArgumentException {
				super(entries.size());
				this.entries = entries;
			}

			@Override
			protected FEMValue customGet(final int index) {
				return this.entries.keysGetImpl(index);
			}

			@Override
			public FEMArray compact() {
				return this;
			}

		}

		static class Values extends Keys {

			Values(final Entries entries) throws IllegalArgumentException {
				super(entries);
			}

			@Override
			protected FEMValue customGet(final int index) {
				return this.entries.valuesGetImpl(index);
			}

		}

		static class Entries extends HashMap<FEMValue, FEMValue> {

			private static final long serialVersionUID = 4632797606591182579L;

			FEMValue keysGetImpl(final int index) {
				return this.customGetKey(index);
			}

			FEMValue valuesGetImpl(final int index) {
				return this.customGetValue(index);
			}

			Entry<FEMValue, FEMValue> tableGetImpl(final int index) {
				if ((index < 0) || (index >= this.countImpl())) throw new IllegalArgumentException();
				return new HashEntry2<>(this, index);
			}

			int tableFindImpl(final FEMValue key) {
				return super.getIndexImpl(key);
			}

		}

		public final FEMArray keys;

		public final FEMArray values;

		final Entries entries = new Entries();

		HashTable(final Iterable<? extends Entry<? extends FEMValue, ? extends FEMValue>> entries) {
			for (final Entry<? extends FEMValue, ? extends FEMValue> entry: entries) {
				this.entries.put(entry.getKey().result(true), entry.getValue());
			}
			this.entries.compact();
			this.keys = new Keys(this.entries);
			this.values = new Values(this.entries);
		}

		@Override
		public Entry<FEMValue, FEMValue> get(final int index) {
			if ((index < 0) || (index >= this.length())) throw new IndexOutOfBoundsException();
			return this.entries.tableGetImpl(index);
		}

		@Override
		public FEMValue get(final FEMValue key) {
			return this.entries.get(key.result());
		}

		@Override
		public int find(final FEMValue key) throws NullPointerException {
			return this.entries.tableFindImpl(key.result());
		}

		@Override
		public FEMArray keys() {
			return this.keys;
		}

		@Override
		public FEMArray values() {
			return this.values;
		}

		@Override
		public int length() {
			return this.keys.length;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.keys) + EMU.from(this.values) + EMU.from(this.entries);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class CompositeTable extends FEMTable implements Emuable {

		public final FEMArray keys;

		public final FEMArray values;

		CompositeTable(final FEMArray keys, final FEMArray values) {
			if (keys.length() != values.length()) throw new IllegalArgumentException();
			this.keys = keys;
			this.values = values;
		}

		@Override
		public Entry<FEMValue, FEMValue> get(final int index) throws IndexOutOfBoundsException {
			return new SimpleImmutableEntry<>(this.keys.get(index), this.values.get(index));
		}

		@Override
		public FEMValue get(final FEMValue key) {
			final int index = this.find(key);
			return index < 0 ? null : this.values.get(index);
		}

		@Override
		public int find(final FEMValue key) throws NullPointerException {
			return this.keys.find(key, 0);
		}

		@Override
		public FEMArray keys() {
			return this.keys;
		}

		@Override
		public FEMArray values() {
			return this.values;
		}

		@Override
		public int length() {
			return this.keys.length;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.keys) + EMU.from(this.values);
		}

	}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 11;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMTable> TYPE = FEMType.from(FEMTable.ID);

	/** Dieses Feld speichert die Dezimalzahl {@code 0}. */
	public static final FEMTable EMPTY = FEMTable.from(FEMArray.EMPTY, FEMArray.EMPTY);

	public static FEMTable from(final Map<? extends FEMValue, ? extends FEMValue> entries) throws NullPointerException {
		return new HashTable(entries.entrySet());
	}

	public static FEMTable from(final Iterable<? extends Entry<? extends FEMValue, ? extends FEMValue>> entries) throws NullPointerException {
		if (entries instanceof FEMTable) return (FEMTable)entries;
		return new HashTable(entries);
	}

	/* Diese Methode gibt eine Tabelle mit den gegebenen Spalten zurück.
	 *
	 * @param keys Schlüsselspalte.
	 * @param values Wertspalte. */
	public static FEMTable from(final FEMArray keys, final FEMArray values) throws NullPointerException, IllegalArgumentException {
		return new CompositeTable(keys, values);
	}

	/* Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMTable.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMTable from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMTable.TYPE);
	}

	/** Diese Methode gibt den {@code index}-ten Eintrag zurück. Dieser ist nicht veränderbar.
	 *
	 * @return {@code index}-ter Eintrag. */
	@Override
	public abstract Entry<FEMValue, FEMValue> get(final int index) throws IndexOutOfBoundsException;

	/** Diese Methode gibt den Wert des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @param key gesuchter Schlüssel. */
	@Override
	public abstract FEMValue get(final FEMValue key) throws NullPointerException;

	/** Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @see FEMArray#find(FEMValue, int)
	 * @param key gesuchter Schlüssel.
	 * @return Position des gesuchten Schlüssels in {@link #keys()} oder {@code -1}.
	 * @throws NullPointerException Wenn {@code key} {@code null} ist. */
	public abstract int find(final FEMValue key) throws NullPointerException;

	/** Diese Methode gibt die Liste der {@link Entry#getKey() Schlüssel} der {@link #get(int) Einträge} zurück. Die Ordnugn der Schlüssel entspricht der der
	 * Einträge.
	 *
	 * @return Schlüsselspalte dieser Tabelle. */
	public abstract FEMArray keys();

	/** Diese Methode gibt die Liste der {@link Entry#getValue() Werte} der {@link #get(int) Einträge} zurück. Die Ordnugn der Werte entspricht der der Einträge.
	 *
	 * @return Wertspalte dieser Tabelle. */
	public abstract FEMArray values();

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Einträge in der Tabelle zurück.
	 *
	 * @return Länge der Tabelle. */
	public abstract int length();

	/** Diese Methode gibt den Streuwert zurück.
	 *
	 * @return Streuwert. */
	public final int hash() {
		return Objects.hash(this.keys(), this.values());
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Tabelle gleich der gegebenen ist. Dies ist der Fall, wenn die {@link #keys() Schlüssel-} und
	 * {@link #values() Wertspalten} dieser Tabelle gleich denen der gegebenen sind.
	 *
	 * @param that Tabelle.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMTable that) throws NullPointerException {
		return (this.length() == that.length()) && Objects.equals(this.keys(), that.keys()) && Objects.equals(this.values(), that.values());
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMTable data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMTable> type() {
		return FEMTable.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMTable)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMTable)) return false;
		}
		return this.equals((FEMTable)object);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<Entry<FEMValue, FEMValue>> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/** Diese Methode gibt die Einträge dieser Tabelle als {@link HashMap} zurück.
	 *
	 * @return neue {@link HashMap} mit den Einträge dieser Tabelle. */
	public Map<FEMValue, FEMValue> toMap() {
		return MapBuilder.<FEMValue, FEMValue>forHashMap().putAll(this).get();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toString(this.toMap());
	}

}
