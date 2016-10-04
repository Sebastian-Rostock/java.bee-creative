package bee.creative.fem;

/** Diese Klasse implementiert eine Funktion, welche die zusätzlichen Parameterwerte des in der Methode {@link #invoke(FEMFrame)} gegebenen {@link FEMFrame
 * Stapelrahmens} an eine gegebene Funktion {@link #withFrame(FEMFrame) bindet} und diese gebundene Funktion anschließend als {@link FEMHandler} liefert.
 * 
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMClosure extends FEMFunction {

	/** Diese Methode gibt die gegebene Funktion als {@link FEMClosure} zurück.
	 * 
	 * @see FEMFunction#toClosure()
	 * @param function Funktion.
	 * @return {@link FEMClosure}.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public static FEMClosure from(final FEMFunction function) throws NullPointerException {
		return function.toClosure();
	}

	{}

	/** Dieses Feld speichert die auszuwertende Funktion. */
	final FEMFunction _function_;

	@SuppressWarnings ("javadoc")
	FEMClosure(final FEMFunction function) throws NullPointerException {
		this._function_ = function;
	}

	{}

	/** Diese Methode gibt die auszuwertende Funktion zurück.
	 * 
	 * @return auszuwertende Funktion. */
	public final FEMFunction function() {
		return this._function_;
	}

	{}

	/** {@inheritDoc}
	 * <p>
	 * Der Ergebniswert entspricht {@code this.function().withFrame(frame).toHandler()}. Damit wird der gegebene Stapelrahmen an die {@link #function() Funktion}
	 * gebunden und als {@link FEMHandler Funktionszeiger} geliefert. */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return this._function_.withFrame(frame).toHandler();
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
		return FEMClosure.from(this._function_.withTracer(tracer));
	}

	/** {@inheritDoc} */
	@Override
	public FEMFunction withoutTracer() {
		return FEMClosure.from(this._function_.withoutTracer());
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
		target.putHandler(this._function_);
	}

}