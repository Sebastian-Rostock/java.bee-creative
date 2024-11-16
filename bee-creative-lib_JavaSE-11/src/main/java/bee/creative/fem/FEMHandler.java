package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen unveränderlichen Funktionszeiger, als einen als {@link FEMValue} verpackten Verweis auf eine {@link FEMFunction Funktion}.
 * Das {@link #compose(FEMFunction...) Komponieren} eines Funktionszeigers entspricht dem Komponieren der referenzierten {@link #value() Funktion}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMHandler implements FEMValue  {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 2;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMHandler> TYPE = FEMType.from(FEMHandler.ID);

	/** Dieses Feld speichert den Funktionszeiger auf {@link FEMVoid#INSTANCE}. */
	public static final FEMHandler EMPTY = new FEMHandler(FEMVoid.INSTANCE);

	/** Diese Methode gibt die gegebene Funktion als Funktionszeiger zurück.
	 *
	 * @see FEMFunction#toValue()
	 * @param function Funktion.
	 * @return Funktionszeiger.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public static FEMHandler from(final FEMFunction function) throws NullPointerException {
		return new FEMHandler(Objects.notNull(function));
	}

	final FEMFunction value;

	FEMHandler(final FEMFunction value) {
		this.value = value;
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMHandler data() {
		return this;
	}

	@Override
	public FEMType<FEMHandler> type() {
		return FEMHandler.TYPE;
	}

	/** Diese Methode gibt die Funktion zurück.
	 *
	 * @return Funktion. */
	public FEMFunction value() {
		return this.value;
	}

	@Override
	public FEMFunction compose(final FEMFunction... params) throws NullPointerException {
		return FEMComposite.from(true, this, params.clone());
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMHandler)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMHandler)) return false;
		}
		if (object == null) return false;
		final FEMHandler that = (FEMHandler)object;
		return Objects.equals(this.value, that.value);
	}

	@Override
	public FEMFunction trace(final FEMTracer tracer) throws NullPointerException {
		return FEMHandler.from(this.value.trace(tracer));
	}

	@Override
	public String toString() {
		return FEMDomain.DEFAULT.printScript(this);
	}

	@Override
	public FEMFunction toFunction() {
		return this.value;
	}

}