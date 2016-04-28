package bee.creative.fem;

import bee.creative.fem.FEM.BaseValue;
import bee.creative.fem.FEM.ScriptFormatter;

/** Diese Klasse implementiert einen Wert mit einem beliebigen nativen Objekt als {@link #data() Nutzdaten}.
 * 
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMNative extends BaseValue {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = -1;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<Object> TYPE = FEMType.from(FEMNative.ID, "NATIVE");

	/** Dieses Feld speichert den Wert zu {@code null}. */
	public static final FEMNative NULL = new FEMNative(null);

	/** Dieses Feld speichert den Wert zu {@code true}. */
	public static final FEMNative TRUE = new FEMNative(Boolean.TRUE);

	/** Dieses Feld speichert den Wert zu {@code false}. */
	public static final FEMNative FALSE = new FEMNative(Boolean.FALSE);

	{}

	/** Diese Methode gibt das native Objekt als Wert zurück.<br>
	 * Wenn das Objekt bereits ein {@link FEMNative} ist, wird dieses geliefert.
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

	{}

	/** Dieses Feld speichert das native Objekt. */
	final Object _data_;

	/** Dieser Konstruktor initialisiert das native Objekt.
	 * 
	 * @param data Objekt. */
	public FEMNative(final Object data) {
		this._data_ = data;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final Object data() {
		return this._data_;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<Object> type() {
		return FEMNative.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putData(this._data_);
	}

}