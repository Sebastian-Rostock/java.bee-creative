package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Wert mit einem beliebigen nativen Objekt als {@link #data() Nutzdaten}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMNative implements FEMValue {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = -1;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<Object> TYPE = FEMType.from(FEMNative.ID);

	/** Dieses Feld speichert den Wert zu {@code null}. */
	public static final FEMNative NULL = new FEMNative(null);

	/** Dieses Feld speichert den Wert zu {@code true}. */
	public static final FEMNative TRUE = new FEMNative(Boolean.TRUE);

	/** Dieses Feld speichert den Wert zu {@code false}. */
	public static final FEMNative FALSE = new FEMNative(Boolean.FALSE);

	/** Diese Methode gibt das native Objekt als Wert zurück. Wenn das Objekt bereits ein {@link FEMNative} ist, wird dieses geliefert.
	 *
	 * @param data Objekt oder {@code null}.
	 * @return Wert. */
	public static FEMNative from(Object data) {
		if (data == null) return FEMNative.NULL;
		if (data == Boolean.TRUE) return FEMNative.TRUE;
		if (data == Boolean.FALSE) return FEMNative.FALSE;
		if (data instanceof FEMNative) return (FEMNative)data;
		return new FEMNative(data);
	}

	/** Dieser Konstruktor initialisiert das native Objekt.
	 *
	 * @param data Objekt. */
	public FEMNative(Object data) {
		this.data = data;
	}

	/** Diese Methode gibt das native Objekt zurück. */
	@Override
	public Object data() {
		return this.data;
	}

	@Override
	public FEMType<Object> type() {
		return FEMNative.TYPE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMValue)) return false;
		var that = (FEMValue)object;
		return Objects.equals(this.data(), that.data());
	}

	@Override
	public String toString() {
		return Objects.toString(this.data);
	}

	/** Dieses Feld speichert das native Objekt. */
	private	final Object data;

}