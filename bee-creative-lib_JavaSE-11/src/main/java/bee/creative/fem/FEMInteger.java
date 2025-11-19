package bee.creative.fem;

import bee.creative.lang.Integers;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine unveränderliche Dezimalzahl. Intern wird die Dezimalzahl als {@code long} dargestellt.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMInteger implements FEMValue, Comparable<FEMInteger> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 6;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMInteger> TYPE = new FEMType<>(FEMInteger.ID);

	/** Dieses Feld speichert die Dezimalzahl {@code 0}. */
	public static final FEMInteger EMPTY = new FEMInteger(0);

	/** Dieses Feld speichert die größte negative Dezimalzahl. */
	public static final FEMInteger MINIMUM = new FEMInteger(Long.MIN_VALUE);

	/** Dieses Feld speichert die größte positive Dezimalzahl. */
	public static final FEMInteger MAXIMUM = new FEMInteger(Long.MAX_VALUE);

	/** Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 *
	 * @param value Wert.
	 * @return Dezimalzahl. */
	public static FEMInteger from(long value) {
		return new FEMInteger(value);
	}

	/** Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 *
	 * @param value Wert.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMInteger femIntegerFrom(Number value) throws NullPointerException {
		return from(value.longValue());
	}

	/** Diese Methode gibt eine neue Dezimalzahl mit dem in der gegebenen Zeichenkette kodierten Wert zurück. Das Format der Zeichenkette entspricht dem der
	 * {@link #toString() Textdarstellung}.
	 *
	 * @see #toString()
	 * @see Long#parseLong(String)
	 * @param value Zeichenkette.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMInteger from(String value) throws NullPointerException, IllegalArgumentException {
		try {
			return from(Long.parseLong(value));
		} catch (NumberFormatException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMInteger data() {
		return this;
	}

	@Override
	public FEMType<FEMInteger> type() {
		return TYPE;
	}

	/** Diese Methode gibt die interne Darstellung der Dezimalzahl zurück.
	 *
	 * @return interne Darstellung der Dezimalzahl. */
	public long value() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return Integers.toIntL(this.value) ^ Integers.toIntH(this.value);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMInteger)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMInteger)) return false;
		}
		var that = (FEMInteger)object;
		return this.value == that.value;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn diese Dezimalzahl kleiner, gleich oder größer als die gegebene Dezimalzahl ist.
	 *
	 * @param that Dezimalzahl.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	@Override
	public int compareTo(FEMInteger that) throws NullPointerException {
		return Comparators.compare(this.value, that.value);
	}

	/** Diese Methode gibt die Textdarstellung dieser Dezimalzahl zurück.
	 *
	 * @see Long#toString()
	 * @return Textdarstellung. */
	@Override
	public String toString() {
		return Long.toString(this.value);
	}

	/** Diese Methode gibt diese Dezimalzahl als {@link Long} zurück.
	 *
	 * @return {@link Long}. */
	public Long toNumber() {
		return this.value;
	}

	private final long value;

	private FEMInteger(long value) {
		this.value = value;
	}

}
