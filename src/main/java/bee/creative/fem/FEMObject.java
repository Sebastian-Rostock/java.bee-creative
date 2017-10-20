package bee.creative.fem;

import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine unveränderliche Referenz auf ein logisches Objekt, welches im Rahmen seines Besitzers über einen {@link #refValue()
 * Objektschlüssel} identifiziert wird.<br>
 * Datentyp und Besitzer des Objekts werden über eine {@link #typeValue() Typkennung} bzw. {@link #ownerValue() Besitzerkennung} angegeben. Die Besitzerkennung
 * kann beispielsweise eine über den {@link FEMContext} erreichbare Objektliste identifizieren, deren Elemente die referenzierten Objekte darstellen. Der
 * Objektschlüssel könnte hierbei der Position eines Objekts in solch einer Liste entsprechen. Alternativ zur Besitzerkennung könnte hierbei auch die Typkennung
 * genutzt werden.<br>
 * Die Wertebereiche für Objektschlüssel, Typkennungen und Besitzerkennungen sind {@code 0..2147483647}, {@code 0..65535} bzw. {@code 0..65535}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMObject extends FEMValue implements Comparable<FEMObject> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 10;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMObject> TYPE = FEMType.from(FEMObject.ID);

	/** Dieses Feld speichert die Referenz, deren Komponenten alle {@code 0} sind. */
	public static final FEMObject EMPTY = new FEMObject(0, (short)0, (short)0);

	{}

	/** Diese Methode gibt eine neue Referenz mit dem in der gegebenen Zeichenkette kodierten Wert zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString() Textdarstellung}.
	 *
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMObject from(final String string) throws NullPointerException, IllegalArgumentException {
		try {
			final int index2 = string.indexOf('.');
			if (index2 < 0) throw new IllegalArgumentException();
			final int index3 = string.indexOf(':');
			if (index3 < 0) throw new IllegalArgumentException();
			if (string.charAt(0) != '@') throw new IllegalArgumentException();
			final int ref = Integer.parseInt(string.substring(1, index2));
			final int type = Integer.parseInt(string.substring(index2 + 1, index3));
			final int owner = Integer.parseInt(string.substring(index3 + 1));
			return FEMObject.from(ref, type, owner);
		} catch (final NumberFormatException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt eine neue Referenz mit den gegebenen Eigenschaften zurück.
	 *
	 * @param ref Objektschlüssel ({@code 0..2147483647}).
	 * @param type Typkennung ({@code 0..65535}).
	 * @param owner Besitzerkennung ({@code 0..65535}).
	 * @return Referenz.
	 * @throws IllegalArgumentException Wenn {@code ref}, {@code type} bzw. {@code owner} ungültig ist. */
	public static FEMObject from(final int ref, final int type, final int owner) throws IllegalArgumentException {
		FEMObject.checkMin(ref);
		FEMObject.checkMax(type);
		FEMObject.checkMax(owner);
		return new FEMObject(ref, (short)type, (short)owner);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMObject.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMObject from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMObject.TYPE);
	}

	@SuppressWarnings ("javadoc")
	static void checkMin(int value) throws IllegalArgumentException {
		if (value < 0) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkMax(int value) throws IllegalArgumentException {
		FEMObject.checkMin(value);
		if (value > 65535) throw new IllegalArgumentException();
	}

	{}

	@SuppressWarnings ("javadoc")
	final int refValue;

	@SuppressWarnings ("javadoc")
	final short typeValue;

	@SuppressWarnings ("javadoc")
	final short ownerValue;

	/** Dieser Konstruktor initialisiert die interne Darstellung der Referenz.
	 *
	 * @see #value()
	 * @param value interne Darstellung der Referenz.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist. */
	public FEMObject(final long value) throws IllegalArgumentException {
		this((int)(value >> 32), (short)(value >> 0), (short)(value >> 16));
	}

	@SuppressWarnings ("javadoc")
	FEMObject(final int refValue, final short typeValue, final short ownerValue) {
		this.refValue = refValue;
		this.typeValue = typeValue;
		this.ownerValue = ownerValue;
	}

	{}

	/** Diese Methode gibt die interne Darstellung der Referenz zurück.
	 * <p>
	 * Die 64 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>refValue - 32 Bit</li>
	 * <li>ownerValue - 16 Bit</li>
	 * <li>typeValue - 16 Bit</li>
	 * </ul>
	 *
	 * @return interne Darstellung der Referenz. */
	public final long value() {
		return (((long)this.refValue) << 32) | (this.ownerValue << 16) | (this.typeValue << 0);
	}

	/** Diese Methode gibt den Objektschlüssel zurück.
	 *
	 * @return Objektschlüssel ({@code 0..2147483647}). */
	public final int refValue() {
		return this.refValue;
	}

	/** Diese Methode gibt die Typkennung zurück.
	 *
	 * @see #withType(int)
	 * @return Typkennung ({@code 0..65535}). */
	public final int typeValue() {
		return this.typeValue & 0xFFFF;
	}

	/** Diese Methode gibt die Besitzerkennung zurück.
	 *
	 * @see #withOwner(int)
	 * @return Besitzerkennung ({@code 0..65535}). */
	public final int ownerValue() {
		return this.ownerValue & 0xFFFF;
	}

	/** Diese Methode gibt diese Referenz mit dem gegebenen Objektschlüssel zurück.
	 *
	 * @see #typeValue()
	 * @param ref Objektschlüssel ({@code 0..2147483647}).
	 * @return Referenz mit Objektschlüssel.
	 * @throws IllegalArgumentException Wenn {@code ref} ungültig ist. */
	public final FEMObject withRef(final int ref) throws IllegalArgumentException {
		FEMObject.checkMin(ref);
		return new FEMObject(ref, this.typeValue, this.ownerValue);
	}

	/** Diese Methode gibt diese Referenz mit der gegebenen Typkennung zurück.
	 *
	 * @see #typeValue()
	 * @param type Typkennung ({@code 0..65535}).
	 * @return Referenz mit Typkennung.
	 * @throws IllegalArgumentException Wenn {@code type} ungültig ist. */
	public final FEMObject withType(final int type) throws IllegalArgumentException {
		FEMObject.checkMax(type);
		return new FEMObject(this.refValue, (short)type, this.ownerValue);
	}

	/** Diese Methode gibt diese Referenz mit der gegebenen Besitzerkennung zurück.
	 *
	 * @see #ownerValue()
	 * @param owner Besitzerkennung ({@code 0..65535}).
	 * @return Referenz mit Besitzerkennung.
	 * @throws IllegalArgumentException Wenn {@code owner} ungültig ist. */
	public final FEMObject withOwner(final int owner) throws IllegalArgumentException {
		FEMObject.checkMax(owner);
		return new FEMObject(this.refValue, this.typeValue, (short)owner);
	}

	/** Diese Methode gibt den Streuwert zurück.
	 *
	 * @return Streuwert. */
	public final int hash() {
		return this.refValue ^ this.typeValue ^ this.ownerValue;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Referenz gleich der gegebenen ist.
	 *
	 * @param that Referenz.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMObject that) throws NullPointerException {
		return (this.refValue == that.refValue) && (this.typeValue == that.typeValue) && (this.ownerValue == that.ownerValue);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die Ordnung dieser Referenz kleiner, gleich bzw. größer als die der gegebenen
	 * Referenz ist.
	 *
	 * @param that Referenz.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final int compare(final FEMObject that) throws NullPointerException {
		int result = Comparators.compare(this.refValue(), that.refValue());
		if (result != 0) return result;
		result = Comparators.compare(this.ownerValue(), that.ownerValue());
		if (result != 0) return result;
		return Comparators.compare(this.typeValue(), that.typeValue());
	}

	{}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMObject data() {
		return this;
	}

	/** Diese Methode gibt {@link #TYPE} zurück. */
	@Override
	public final FEMType<FEMObject> type() {
		return FEMObject.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMObject result() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMObject result(final boolean recursive) {
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
		if (!(object instanceof FEMObject)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMObject)) return false;
		}
		return this.equals((FEMObject)object);
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final FEMObject that) {
		return this.compare(that);
	}

	/** Diese Methode gibt die Textdarstellung dieser Referenz zurück.<br>
	 * Das Format der Textdarstellung ist {@code @}{@link #refValue() REF}{@code .}{@link #ownerValue() OWNER}{@code :}{@link #typeValue() TYPE}.
	 *
	 * @return Textdarstellung. */
	@Override
	public final String toString() {
		return "@" + this.refValue() + "." + this.ownerValue() + ":" + this.typeValue();
	}

}
