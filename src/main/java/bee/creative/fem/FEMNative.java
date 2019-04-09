package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Wert mit einem beliebigen nativen Objekt als {@link #data() Nutzdaten}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMNative extends FEMValue {

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
	public static FEMNative from(final Object data) {
		if (data == null) return FEMNative.NULL;
		if (data == Boolean.TRUE) return FEMNative.TRUE;
		if (data == Boolean.FALSE) return FEMNative.FALSE;
		if (data instanceof FEMNative) return (FEMNative)data;
		return new FEMNative(data);
	}

	/** Dieses Feld speichert das native Objekt. */
	final Object data;

	/** Dieser Konstruktor initialisiert das native Objekt.
	 *
	 * @param data Objekt. */
	public FEMNative(final Object data) {
		this.data = data;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieser Wert gleich dem gegebenen ist. Dies ist nur dann der Fall, wenn die {@link #data() Nutzdaten}
	 * einander gleichen, d.h. {@code Objects.equals(this.data(), that.data())}.
	 *
	 * @param that Wert.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMNative that) throws NullPointerException {
		if (this == that) return true;
		return Objects.equals(this.data, that.data);
	}

	/** Diese Methode gibt das native Objekt zurück. */
	@Override
	public final Object data() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<Object> type() {
		return FEMNative.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return Objects.hash(this.data);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMNative)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMNative)) return false;
		}
		return this.equals((FEMNative)object);
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toString(this.data);
	}

}