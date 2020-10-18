package bee.creative.fem;

import bee.creative.fem.FEMFunction.BaseFunction;
import bee.creative.lang.Objects;

public final class FEMBinding extends BaseFunction {

	final FEMFrame frame;

	final FEMFunction target;

	FEMBinding(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
		this.frame = Objects.notNull(frame);
		this.target = function;
	}

	public FEMFrame frame() {
		return this.frame;
	}

	public FEMFunction function() {
		return this.target;
	}

	@Override
	public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
		return this.target.invoke(this.frame.withParams(frame.params()).withContext(frame.context()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.frame, this.target);
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBinding)) return false;
		final FEMBinding that = (FEMBinding)object;
		return this.frame.equals(that.frame) && this.target.equals(that.target);
	}

	/** Diese Methode gibt diese Funktion mit Bindung an den gegebenen {@link FEMFrame Stapelrahmen} zurück.
	 * <p>
	 * Die zugesicherten Parameterwerte sowie das Kontextobjekt für den {@link #invoke(FEMFrame) Aufruf} der gelieferten Funktion entsprechen hierbei denen des in
	 * der Methode {@link #invoke(FEMFrame)} übergeben Stapelrahmen {@code frame}. Die zusätzlichen Parameterwerte stammen dagegen aus dem gegebenen Stapelrahmen
	 * {@code params}, d.h. {@code this.invoke(params.withParams(frame.params()).withContext(frame.context()))}.
	 *
	 * @see FEMBinding
	 * @param params {@link FEMFrame Stapelrahmen} mit den zusätzlichen Parameterwerten.
	 * @return {@link FEMFunction} mit gebundenem {@link FEMFrame Stapelrahmen}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public static FEMFunction from(final FEMFunction f, final FEMFrame params) throws NullPointerException {
		return new FEMBinding(params, f);
	}

}