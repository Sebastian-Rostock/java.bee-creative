package bee.creative.fem;

import bee.creative.fem.FEM.BaseValue;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine unveränderliche Dezimalzahl.<br>
 * Intern wird die Dezimalzahl als {@code long} dargestellt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMInteger extends BaseValue implements Comparable<FEMInteger> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 6;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMInteger> TYPE = FEMType.from(FEMInteger.ID, "INTEGER");

	/** Dieses Feld speichert die Dezimalzahl {@code 0}. */
	public static final FEMInteger EMPTY = new FEMInteger(0);

	{}

	/** Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalzahl. */
	public static FEMInteger from(final long value) {
		return new FEMInteger(value);
	}

	/** Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMInteger from(final Number value) throws NullPointerException {
		return new FEMInteger(value.longValue());
	}

	/** Diese Methode gibt eine neue Dezimalzahl mit dem in der gegebenen Zeichenkette kodierten Wert zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString() Textdarstellung}.
	 * 
	 * @see #toString()
	 * @see Long#parseLong(String)
	 * @param value Zeichenkette.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMInteger from(final String value) throws NullPointerException, IllegalArgumentException {
		try {
			return FEMInteger.from(Long.parseLong(value));
		} catch (final NumberFormatException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMInteger.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMInteger from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMInteger.TYPE);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMInteger.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMInteger from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMInteger.TYPE);
	}

	{}

	/** Dieses Feld speichert die interne Darstellung der Dezimalzahl. */
	final long _value_;

	/** Dieser Konstruktor initialisiert die interne Darstellung der Dezimalzahl.
	 * 
	 * @param value interne Darstellung der Dezimalzahl. */
	public FEMInteger(final long value) {
		this._value_ = value;
	}

	{}

	/** Diese Methode gibt die interne Darstellung der Dezimalzahl zurück. Diese ist ein {@code long}.
	 * 
	 * @return interne Darstellung der Dezimalzahl. */
	public final long value() {
		return this._value_;
	}

	/** Diese Methode gibt den Streuwert zurück.
	 * 
	 * @return Streuwert. */
	public final int hash() {
		final long value = this._value_;
		return (int)(value >>> 0) ^ (int)(value >>> 32);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Dezimalzahl gleich der gegebenen ist.
	 * 
	 * @param that Dezimalzahl.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final boolean equals(final FEMInteger that) throws NullPointerException {
		return this._value_ == that._value_;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn diese Dezimalzahl kleiner, gleich oder größer als die gegebene Dezimalzahl ist.
	 * 
	 * @param that Dezimalzahl.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final int compare(final FEMInteger that) throws NullPointerException {
		return Comparators.compare(this._value_, that._value_);
	}

	/** Diese Methode gibt diese Dezimalzahl als {@link Number} zurück.
	 * 
	 * @return {@link Number}. */
	public final Number toNumber() {
		return new Long(this._value_);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMInteger data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMInteger> type() {
		return FEMInteger.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMInteger result(boolean recursive) {
		return this;
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
		if (!(object instanceof FEMInteger)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMInteger)) return false;
		}
		return this.equals((FEMInteger)object);
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final FEMInteger value) {
		return this.compare(value);
	}

	/** Diese Methode gibt die Textdarstellung dieser Dezimalzahl zurück.
	 * 
	 * @see Long#toString()
	 * @return Textdarstellung. */
	@Override
	public final String toString() {
		return Long.toString(this._value_);
	}

}
