package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEM.ScriptFormatterInput;

/**
 * Diese Klasse implementiert eine Referenz auf ein logisches Objekt.<br>
 * Das Objekt wird im Rahmen seines Besitzers über einen {@link #refValue() Objektschlüssel} identifiziert.<br>
 * Datentyp und Besitzer des Objekts werden über eine {@link #typeValue() Typkennung} bzw. {@link #ownerValue() Besitzerkennung} angegeben.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMObject implements Comparable<FEMObject>, ScriptFormatterInput {

	/**
	 * Dieses Feld speichert die Referenz {@code 0.0:0}.
	 */
	public static final FEMObject EMPTY = new FEMObject(2147483647, 0);

	{}

	/**
	 * Diese Methode gibt eine neue Referenz mit den gegebenen Eigenschaften zurück.
	 * 
	 * @param ref Objektschlüssel ({@code 0..2147483647}).
	 * @param type Typkennung ({@code 0..65535}).
	 * @param owner Besitzerkennung ({@code 0..65535}).
	 * @return Referenz.
	 * @throws IllegalArgumentException Wenn {@code ref}, {@code type} bzw. {@code owner} ungültig ist.
	 */
	public static final FEMObject from(final int ref, final int type, final int owner) throws IllegalArgumentException {
		FEMObject.__checkRef(ref);
		FEMObject.__checkType(type);
		FEMObject.__checkOwner(owner);
		return FEMObject.__from(ref, type, owner);
	}

	@SuppressWarnings ("javadoc")
	static final FEMObject __from(final int ref, final int type, final int owner) throws IllegalArgumentException {
		return new FEMObject(ref, (type << 16) | (owner << 0));
	}

	@SuppressWarnings ("javadoc")
	static final void __checkRef(final int ref) throws IllegalArgumentException {
		if (ref < 0) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkType(final int type) throws IllegalArgumentException {
		if ((type < 0) || (type > 65535)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkOwner(final int owner) throws IllegalArgumentException {
		if ((owner < 0) || (owner > 65535)) throw new IllegalArgumentException();
	}

	{}

	/**
	 * Dieses Feld speichert die 32 LSB der internen 64 Bit Darstellung dieser Referenz.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>refValue - 32 Bit</li>
	 * </ul>
	 */
	final int __valueL;

	/**
	 * Dieses Feld speichert die 32 MSB der internen 64 Bit Darstellung dieser Referenz.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>typeValue - 16 Bit</li>
	 * <li>ownerValue - 16 Bit</li>
	 * </ul>
	 */
	final int __valueH;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung der Referenz.
	 * 
	 * @see #value()
	 * @param value interne Darstellung der Referenz.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist.
	 */
	public FEMObject(final long value) throws IllegalArgumentException {
		this((int)(value >> 32), (int)(value >> 0));
		FEMObject.__checkRef(this.refValue());
		FEMObject.__checkType(this.typeValue());
		FEMObject.__checkOwner(this.ownerValue());
	}

	@SuppressWarnings ("javadoc")
	private FEMObject(final int valueH, final int valueL) {
		this.__valueH = valueH;
		this.__valueL = valueL;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung der Referenz zurück.
	 * <p>
	 * Die 64 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>refValue - 32 Bit</li>
	 * <li>ownerValue - 16 Bit</li>
	 * <li>typeValue - 16 Bit</li>
	 * </ul>
	 * 
	 * @return interne Darstellung der Referenz.
	 */
	public final long value() {
		return (((long)this.__valueH) << 32) | (((long)this.__valueL) << 0);
	}

	/**
	 * Diese Methode gibt den Objektschlüssel zurück.
	 * 
	 * @return Objektschlüssel ({@code 0..2147483647}).
	 */
	public final int refValue() {
		return this.__valueL;
	}

	/**
	 * Diese Methode gibt die Typkennung zurück.
	 * 
	 * @see #withType(int)
	 * @return Typkennung ({@code 0..65535}).
	 */
	public final int typeValue() {
		return (this.__valueH >> 16) & 0xFFFF;
	}

	/**
	 * Diese Methode gibt die Besitzerkennung zurück.
	 * 
	 * @see #withOwner(int)
	 * @return Besitzerkennung ({@code 0..65535}).
	 */
	public final int ownerValue() {
		return (this.__valueH >> 0) & 0xFFFF;
	}

	/**
	 * Diese Methode gibt diese Referenz mit dem gegebenen Objektschlüssel zurück.
	 * 
	 * @see #typeValue()
	 * @param ref Objektschlüssel ({@code 0..2147483647}).
	 * @return Referenz mit Objektschlüssel.
	 * @throws IllegalArgumentException Wenn {@code ref} ungültig ist.
	 */
	public final FEMObject withRef(final int ref) throws IllegalArgumentException {
		FEMObject.__checkRef(ref);
		return FEMObject.__from(ref, this.typeValue(), this.ownerValue());
	}

	/**
	 * Diese Methode gibt diese Referenz mit der gegebenen Typkennung zurück.
	 * 
	 * @see #typeValue()
	 * @param type Typkennung ({@code 0..65535}).
	 * @return Referenz mit Typkennung.
	 * @throws IllegalArgumentException Wenn {@code type} ungültig ist.
	 */
	public final FEMObject withType(final int type) throws IllegalArgumentException {
		FEMObject.__checkType(type);
		return FEMObject.__from(this.refValue(), type, this.ownerValue());
	}

	/**
	 * Diese Methode gibt diese Referenz mit der gegebenen Besitzerkennung zurück.
	 * 
	 * @see #ownerValue()
	 * @param owner Besitzerkennung ({@code 0..65535}).
	 * @return Referenz mit Besitzerkennung.
	 * @throws IllegalArgumentException Wenn {@code owner} ungültig ist.
	 */
	public final FEMObject withOwner(final int owner) throws IllegalArgumentException {
		FEMObject.__checkOwner(owner);
		return FEMObject.__from(this.refValue(), this.typeValue(), owner);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Referenz gleich der gegebenen ist.
	 * 
	 * @param that Referenz.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMObject that) throws NullPointerException {
		return (this.__valueL == that.__valueL) && (this.__valueH == that.__valueH);
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung dieser Referenz kleiner, gleich bzw. größer als die der
	 * gegebenen Referenz ist.
	 * 
	 * @param that Referenz.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEMObject that) {
		int result = this.refValue() - that.refValue();
		if (result != 0) return result;
		result = this.ownerValue() - that.ownerValue();
		if (result != 0) return result;
		return this.typeValue() - that.typeValue();
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.__valueH ^ this.__valueL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMObject)) return false;
		final FEMObject that = (FEMObject)object;
		return (this.__valueL == that.__valueL) && (this.__valueH == that.__valueH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final FEMObject that) {
		return this.compare(that);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(FEM.formatValue(this.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return String.format("%s.%s:%s", this.refValue(), this.ownerValue(), this.typeValue());
	}

}
