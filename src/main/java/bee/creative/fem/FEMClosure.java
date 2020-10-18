package bee.creative.fem;

import bee.creative.fem.FEMFunction.BaseFunction;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine Parameterfunktion, welche bei ihrer {@link #invoke(FEMFrame) Auswertung} mit einem {@link FEMFrame Stapelrahmen}
 * {@code frame} einen {@link FEMHandler Funktionszeiger} liefert, der diesen Stapelrahmen an eine {@link #target() gegebene Funktion} {@link FEMBinding
 * bindet}. Sie liefert damit {@code FEMHandler.from(FEMBinding.from(this.target(), frame))}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMClosure extends BaseFunction {

	/** Diese Methode gibt die gegebene Funktion als Parameterfunktion zurück. */
	public static final FEMClosure from(final FEMFunction target) throws NullPointerException {
		return new FEMClosure(Objects.notNull(target));
	}

	final FEMFunction target;

	FEMClosure(final FEMFunction function) {
		this.target = function;
	}

	/** Diese Methode gibt die beim Aufruf an den Stapelrahmen zu bindende Funktion zurück. */
	public FEMFunction target() {
		return this.target;
	}

	@Override
	public FEMFunction compose(final FEMFunction... params) throws NullPointerException {
		return new CompositeFunction2(this, params.clone());
	}

	@Override
	public FEMValue invoke(final FEMFrame frame) {
		return FEMHandler.from(FEMBinding.from(this.target, frame));
	}

	@Override
	public FEMFunction trace(final FEMTracer tracer) throws NullPointerException {
		return FEMClosure.from(this.target.trace(tracer));
	}

	@Override
	public int hashCode() {
		return Objects.hashPush(Objects.hash(this.target), 1233);
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMClosure)) return false;
		final FEMClosure that = (FEMClosure)object;
		return Objects.equals(this.target, that.target);
	}

}