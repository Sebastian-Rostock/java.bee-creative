package bee.creative.fem;

import bee.creative.fem.FEMFunction.BaseFunction;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine bindende Funktion, welche eine {@link #target() gegebene Funktion} an einen gegebenen {@link #params() gebundenen
 * Stapelrahmen} bindet. Die zugesicherten Parameterwerte sowie das Kontextobjekt für den {@link #invoke(FEMFrame) Aufruf} der gegebenen Funktion entsprechen
 * hierbei denen des in der Methode {@link #invoke(FEMFrame)} übergeben Stapelrahmen {@code frame}. Die zusätzlichen Parameterwerte stammen dagegen aus dem
 * {@link #params() gebundenen Stapelrahmen}, sodass sich der Ergebniswert der bindenden Funktion aus
 * {@code this.target().invoke(this.params().newFrame(frame.params()).withContext(frame.context()))} ergibt.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMBinding extends BaseFunction {

	/** Diese Methode gibt diese Funktion mit Bindung an den gegebenen {@link FEMFrame Stapelrahmen} zurück.
	 * <p>
	 *
	 * @see FEMBinding
	 * @param params {@link FEMFrame Stapelrahmen} mit den zusätzlichen Parameterwerten.
	 * @return {@link FEMFunction} mit gebundenem {@link FEMFrame Stapelrahmen}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public static FEMBinding from(final FEMFunction target, final FEMFrame params) throws NullPointerException {
		return new FEMBinding(Objects.notNull(target), Objects.notNull(params));
	}

	final FEMFunction target;

	final FEMFrame params;

	FEMBinding(final FEMFunction target, final FEMFrame params) {
		this.target = target;
		this.params = params;
	}

	/** Diese Methode gibt die gebundene Funktion zurück, welche mit den gebundenen zusätzlichen Parameterwerten aufgerufen wird.
	 *
	 * @return gebundene Funktion. */
	public FEMFunction target() {
		return this.target;
	}

	/** Diese Methode gibt den gebundenen Stapelrahmen zurück, welcher die zusätzlichen Parameterwerte bereitstellt.
	 *
	 * @return Stapelrahmen. */
	public FEMFrame params() {
		return this.params;
	}

	@Override
	public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
		return this.target.invoke(this.params.newFrame(frame.params()).withContext(frame.context()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.params, this.target);
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBinding)) return false;
		final FEMBinding that = (FEMBinding)object;
		return Objects.equals(this.params, that.params) && Objects.equals(this.target, that.target);
	}

}