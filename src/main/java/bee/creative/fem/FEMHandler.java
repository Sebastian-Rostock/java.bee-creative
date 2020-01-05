package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen unveränderlichen Funktionszeiger, d.h. ein als {@link FEMValue} verpackter Verweis auf eine {@link FEMFunction Funktion}.
 * Intern wird der Funktionszeiger als {@link FEMFunction} dargestellt.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMHandler extends FEMValue {

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

	/** Dieses Feld speichert die Nutzdaten. */
	final FEMFunction value;

	FEMHandler(final FEMFunction value) {
		this.value = value;
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMHandler data() {
		return this;
	}

	@Override
	public final FEMType<FEMHandler> type() {
		return FEMHandler.TYPE;
	}

	/** Diese Methode gibt die Funktion zurück.
	 *
	 * @return Funktion. */
	public final FEMFunction value() {
		return this.value;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieser Funktionszeiger gleich der gegebenen ist.
	 *
	 * @param that Methode.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMHandler that) throws NullPointerException {
		return this.value.equals(that.value);
	}

	@Override
	public final int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMHandler)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMHandler)) return false;
		}
		if (object == null) return false;
		return this.equals((FEMHandler)object);
	}

	@Override
	public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
		return FEMHandler.from(this.value.withTracer(tracer));
	}

	@Override
	public final String toString() {
		final FEMFormatter target = new FEMFormatter();
		FEMDomain.NORMAL.formatHandler(target, this);
		return target.format();
	}

	@Override
	public final FEMFunction toFunction() {
		return this.value;
	}

}