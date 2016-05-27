package bee.creative.fem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bee.creative.fem.FEM.ScriptFormatter;

/** Diese Klasse implementiert eine unveränderliche Referenz auf ein logisches Objekt, welches im Rahmen seines Besitzers über einen {@link #refValue()
 * Objektschlüssel} identifiziert wird.<br>
 * Datentyp und Besitzer des Objekts werden über eine {@link #typeValue() Typkennung} bzw. {@link #ownerValue() Besitzerkennung} angegeben. Die Besitzerkennung
 * kann beispielsweise eine über den {@link FEMContext} erreichbare Objektliste identifizieren, deren Elemente die referenzierten Objekte darstellen. Der
 * Objektschlüssel könnte hierbei der Position eines Objekts in solch einer Liste entsprechen. Alternativ zur Besitzerkennung könnte hierbei auch die Typkennung
 * genutzt werden.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMObject extends FEMBaseValue implements Comparable<FEMObject> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 10;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMObject> TYPE = FEMType.from(FEMObject.ID, "OBJECT");

	/** Dieses Feld speichert die Referenz, deren Komponenten alle {@code 0} sind. */
	public static final FEMObject EMPTY = new FEMObject(0, 0);

	@SuppressWarnings ("javadoc")
	static final Pattern _pattern_ = Pattern.compile("^#(\\d+)\\.(\\d+):(\\d+)$");

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
			final Matcher matcher = FEMDuration._pattern_.matcher(string);
			if (!matcher.find()) throw new IllegalArgumentException();
			final int ref = Integer.parseInt(matcher.group(1));
			final int type = Integer.parseInt(matcher.group(3));
			final int owner = Integer.parseInt(matcher.group(2));
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
		FEMObject._checkRef_(ref);
		FEMObject._checkType_(type);
		FEMObject._checkOwner_(owner);
		return FEMObject._from_(ref, type, owner);
	}

	/** Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMObject.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMObject from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMObject.TYPE);
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
	static FEMObject _from_(final int ref, final int type, final int owner) throws IllegalArgumentException {
		return new FEMObject(ref, (type << 16) | (owner << 0));
	}

	@SuppressWarnings ("javadoc")
	static void _checkRef_(final int ref) throws IllegalArgumentException {
		if (ref < 0) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void _checkType_(final int type) throws IllegalArgumentException {
		if ((type < 0) || (type > 65535)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void _checkOwner_(final int owner) throws IllegalArgumentException {
		if ((owner < 0) || (owner > 65535)) throw new IllegalArgumentException();
	}

	{}

	/** Dieses Feld speichert die 32 LSB der internen 64 Bit Darstellung dieser Referenz.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>0 - 1 Bit</li>
	 * <li>refValue - 31 Bit</li>
	 * </ul> */
	final int _valueL_;

	/** Dieses Feld speichert die 32 MSB der internen 64 Bit Darstellung dieser Referenz.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>typeValue - 16 Bit</li>
	 * <li>ownerValue - 16 Bit</li>
	 * </ul> */
	final int _valueH_;

	/** Dieser Konstruktor initialisiert die interne Darstellung der Referenz.
	 * 
	 * @see #value()
	 * @param value interne Darstellung der Referenz.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist. */
	public FEMObject(final long value) throws IllegalArgumentException {
		this((int)(value >> 32), (int)(value >> 0));
		FEMObject._checkRef_(this.refValue());
		FEMObject._checkType_(this.typeValue());
		FEMObject._checkOwner_(this.ownerValue());
	}

	@SuppressWarnings ("javadoc")
	FEMObject(final int valueH, final int valueL) {
		this._valueH_ = valueH;
		this._valueL_ = valueL;
	}

	{}

	/** Diese Methode gibt die interne Darstellung der Referenz zurück.
	 * <p>
	 * Die 64 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>0 - 1 Bit</li>
	 * <li>refValue - 31 Bit</li>
	 * <li>ownerValue - 16 Bit</li>
	 * <li>typeValue - 16 Bit</li>
	 * </ul>
	 * 
	 * @return interne Darstellung der Referenz. */
	public final long value() {
		return (((long)this._valueH_) << 32) | (((long)this._valueL_) << 0);
	}

	/** Diese Methode gibt den Objektschlüssel zurück.
	 * 
	 * @return Objektschlüssel ({@code 0..2147483647}). */
	public final int refValue() {
		return this._valueL_;
	}

	/** Diese Methode gibt die Typkennung zurück.
	 * 
	 * @see #withType(int)
	 * @return Typkennung ({@code 0..65535}). */
	public final int typeValue() {
		return (this._valueH_ >> 16) & 0xFFFF;
	}

	/** Diese Methode gibt die Besitzerkennung zurück.
	 * 
	 * @see #withOwner(int)
	 * @return Besitzerkennung ({@code 0..65535}). */
	public final int ownerValue() {
		return (this._valueH_ >> 0) & 0xFFFF;
	}

	/** Diese Methode gibt diese Referenz mit dem gegebenen Objektschlüssel zurück.
	 * 
	 * @see #typeValue()
	 * @param ref Objektschlüssel ({@code 0..2147483647}).
	 * @return Referenz mit Objektschlüssel.
	 * @throws IllegalArgumentException Wenn {@code ref} ungültig ist. */
	public final FEMObject withRef(final int ref) throws IllegalArgumentException {
		FEMObject._checkRef_(ref);
		return FEMObject._from_(ref, this.typeValue(), this.ownerValue());
	}

	/** Diese Methode gibt diese Referenz mit der gegebenen Typkennung zurück.
	 * 
	 * @see #typeValue()
	 * @param type Typkennung ({@code 0..65535}).
	 * @return Referenz mit Typkennung.
	 * @throws IllegalArgumentException Wenn {@code type} ungültig ist. */
	public final FEMObject withType(final int type) throws IllegalArgumentException {
		FEMObject._checkType_(type);
		return FEMObject._from_(this.refValue(), type, this.ownerValue());
	}

	/** Diese Methode gibt diese Referenz mit der gegebenen Besitzerkennung zurück.
	 * 
	 * @see #ownerValue()
	 * @param owner Besitzerkennung ({@code 0..65535}).
	 * @return Referenz mit Besitzerkennung.
	 * @throws IllegalArgumentException Wenn {@code owner} ungültig ist. */
	public final FEMObject withOwner(final int owner) throws IllegalArgumentException {
		FEMObject._checkOwner_(owner);
		return FEMObject._from_(this.refValue(), this.typeValue(), owner);
	}

	/** Diese Methode gibt den Streuwert zurück.
	 * 
	 * @return Streuwert. */
	public final int hash() {
		return this._valueH_ ^ this._valueL_;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Referenz gleich der gegebenen ist.
	 * 
	 * @param that Referenz.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMObject that) throws NullPointerException {
		return (this._valueL_ == that._valueL_) && (this._valueH_ == that._valueH_);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die Ordnung dieser Referenz kleiner, gleich bzw. größer als die der gegebenen
	 * Referenz ist.
	 * 
	 * @param that Referenz.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final int compare(final FEMObject that) {
		int result = this.refValue() - that.refValue();
		if (result != 0) return result;
		result = this.ownerValue() - that.ownerValue();
		if (result != 0) return result;
		return this.typeValue() - that.typeValue();
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMObject data() {
		return this;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(FEM.formatValue(this.toString()));
	}

	/** Diese Methode gibt die Textdarstellung dieser Referenz zurück.<br>
	 * Das Format der Textdarstellung ist {@code #}{@link #refValue() REF}{@code .}{@link #type() TYPE}{@code :}{@link #ownerValue() OWNER}.
	 * 
	 * @return Textdarstellung. */
	@Override
	public final String toString() {
		return String.format("#%s.%s:%s", this.refValue(), this.ownerValue(), this.typeValue());
	}

}
