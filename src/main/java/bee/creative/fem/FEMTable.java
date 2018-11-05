package bee.creative.fem;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Getter;
import bee.creative.util.HashMap;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine unveränderliche Abbildung von Schlüsseln auf Werte als Tabelle mit einer Schlüssel- und einer Wertspalte. Sie dient der
 * effizienten {@link #find(FEMValue) Suche} eines Schlüssels bzw. {@link #get(FEMValue) Ermittlung} des einem Schlüssel zugeordnenten Werts. Dieser Wert
 * befindet sich in der {@link #values() Wertspalte} an der Position, an der sich auch der Schlüssel in der {@link #keys() Schlüsselspalte} befindet.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMTable extends FEMValue implements Items<FEMArray>, Getter<FEMValue, FEMValue>, Iterable<FEMArray> {

	@SuppressWarnings ("javadoc")
	static class TableEntry extends FEMArray {

		public final FEMValue key;

		public final FEMValue value;

		public TableEntry(final FEMValue key, final FEMValue value) {
			super(2);
			this.key = key;
			this.value = value;
		}

		@Override
		protected FEMValue customGet(final int index) {
			return index == 0 ? this.key : this.value;
		}

		@Override
		public FEMArray compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class CompactTable extends CompositeTable {

		static class Keys extends Values implements Emuable {

			Keys(final Entries entries) {
				super(entries);
			}

			@Override
			protected FEMValue customGet(final int index) {
				return this.entries.getKey(index);
			}

			@Override
			protected int customFind(final FEMValue that, final int offset) {
				final int result = this.entries.findKey(that);
				return result < offset ? -1 : result;
			}

			@Override
			public long emu() {
				return EMU.fromObject(this) + this.entries.emu();
			}

		}

		static class Values extends FEMArray {

			final Entries entries;

			Values(final Entries entries) {
				super(entries.size());
				this.entries = entries;
			}

			@Override
			protected FEMValue customGet(final int index) {
				return this.entries.getValue(index);
			}

		}

		static class Entries extends HashMap<FEMValue, FEMValue> {

			private static final long serialVersionUID = -7608213911033741973L;

			public Entries() {
			}

			public Entries(final int capacity, final Iterable<? extends FEMArray> entries) throws NullPointerException, IllegalArgumentException {
				super(capacity);
				for (final FEMArray entry: entries) {
					if (entry.length() != 2) throw new IllegalArgumentException();
					this.put(entry.customGet(0).result(true), entry.customGet(1));
				}
				this.compact();
			}

			public Entries(final Map<? extends FEMValue, ? extends FEMValue> entries) throws NullPointerException {
				super(entries.size());
				for (final Entry<? extends FEMValue, ? extends FEMValue> entry: entries.entrySet()) {
					this.put(entry.getKey().result(true), entry.getValue());
				}
				this.compact();
			}

			FEMValue getKey(final int index) {
				return this.customGetKey(index);
			}

			FEMValue getValue(final int index) {
				return this.customGetValue(index);
			}

			int findKey(final FEMValue key) {
				return this.getIndexImpl(key);
			}

		}

		CompactTable(final Entries entries) {
			super(new Keys(entries), new Values(entries));
		}

		@Override
		public FEMTable compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class CompositeTable extends FEMTable implements Emuable {

		public final FEMArray keys;

		public final FEMArray values;

		CompositeTable(final FEMArray keys, final FEMArray values) throws IllegalArgumentException {
			if (keys.length() != values.length()) throw new IllegalArgumentException();
			this.keys = keys;
			this.values = values;
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

	/** Diese Methode gibt eine kompakte Tabelle mit den gegebenen Einträgen zurück. Jeder Eintrag muss hierbei als eine zweielementige Wertliste aus einem
	 * Schlüssel und dem dazugehörigen Wert gegeben sein.
	 * 
	 * @param entries Einträge.
	 * @return {@code compact}-{@link FEMTable}.
	 * @throws NullPointerException Wenn {@code entries} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Einträge nicht genau aus zwei Elementen (Schlüssel und Wert) bestehen. */
	public static FEMTable from(final Iterable<? extends FEMArray> entries) throws NullPointerException, IllegalArgumentException {
		if (entries instanceof FEMTable) return (FEMTable)entries;
		return new CompactTable(new CompactTable.Entries(0, entries));
	}

	/** Diese Methode gibt eine kompakte Tabelle mit den gegebenen Einträgen zurück.
	 *
	 * @param entries Einträge.
	 * @return {@code compact}-{@link FEMTable}.
	 * @throws NullPointerException Wenn {@code entries} {@code null} ist oder enthält. */
	public static FEMTable from(final Map<? extends FEMValue, ? extends FEMValue> entries) throws NullPointerException {
		return new CompactTable(new CompactTable.Entries(entries));
	}

	/** Diese Methode gibt eine aus den gegebenen Schlüssel- und Wertspalten zusammengesetzte Tabelle zurück. Die Reihenfolge der Elemente in den Spalten wird
	 * nicht verändert und Duplikate in der Schlüsselspalte werden ignoriert.
	 *
	 * @param keys Schlüsselspalte.
	 * @param values Wertspalte.
	 * @return {@code composite}-{@link FEMTable}.
	 * @throws NullPointerException Wenn {@code keys} bzw. {@code values} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Spalten unterschiedlich lang sind. */
	public static FEMTable from(final FEMArray keys, final FEMArray values) throws NullPointerException, IllegalArgumentException {
		return new CompositeTable(keys, values);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMTable.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMTable from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMTable.TYPE);
	}

	/** Diese Methode gibt den {@code index}-ten Eintrag als Wertliste aus Schlüssel und Wert zurück. Die gelieferte Wertliste entspricht damit
	 * {@code FEMArray.from(this.keys().get(index), this.values().get(index))}.
	 *
	 * @return {@code index}-ter Eintrag. */
	@Override
	public FEMArray get(final int index) throws IndexOutOfBoundsException {
		return new TableEntry(this.keys().get(index), this.values().get(index));
	}

	/** Diese Methode gibt den Wert des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @param key gesuchter Schlüssel. */
	@Override
	public FEMValue get(final FEMValue key) throws NullPointerException {
		final int index = this.find(key);
		return index < 0 ? null : this.values().get(index);
	}

	/** Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert. Die
	 * gelieferte Position entspricht damit {@code this.keys().find(key, 0)}.
	 *
	 * @param key gesuchter Schlüssel.
	 * @return Position des gesuchten Schlüssels oder {@code -1}.
	 * @throws NullPointerException Wenn {@code key} {@code null} ist. */
	public int find(final FEMValue key) throws NullPointerException {
		return this.keys().find(key, 0);
	}

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
	public int length() {
		return this.keys().length();
	}

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

	/** Diese Methode gibt die {@link #get(FEMValue) Einträge dieser Tabelle} in einer performanteren oder zumindest gleichwertigen Tabelle zurück. Die
	 * Reihenfolge der gelieferten Tabelle kann von der dieser Tabelle abweichen.
	 *
	 * @return performanteren Tabelle oder {@code this}. */
	public FEMTable compact() {
		return new CompactTable(new CompactTable.Entries(this.length(), this));
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
	public Iterator<FEMArray> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/** Diese Methode gibt die {@link #get(int) Einträge} dieser Tabelle als {@link HashMap} zurück.
	 *
	 * @return neue {@link HashMap} mit den Einträge dieser Tabelle. */
	public Map<FEMValue, FEMValue> toMap() {
		return new CompactTable.Entries(this.length(), this);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toString(this.toMap());
	}

}
