package bee.creative.fem;

import static bee.creative.lang.Objects.notNull;
import bee.creative.fem.FEMFunction.BaseFunction;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine Parameterfunktion, welche bei ihrer {@link #invoke(FEMFrame) Auswertung} mit einem {@link FEMFrame Stapelrahmen}
 * {@code frame} einen {@link FEMHandler Funktionszeiger} liefert, der diesen Stapelrahmen an eine {@link #target() gegebene Funktion} {@link FEMBinding
 * bindet}. Sie liefert damit {@code FEMHandler.from(FEMBinding.from(this.target(), frame))}. Eine solche Parameterfunktion kann an der Stelle eines
 * {@link FEMHandler Funktionszeigers} verwendet werden, wenn die darin angegebene Funktion auf die zusätzlichen Parameterwerte des Stapelrahmen zugreifen
 * können soll.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMClosure extends BaseFunction {

	/** Diese Methode gibt die gegebene Funktion als Parameterfunktion zurück. */
	public static final FEMClosure from(FEMFunction target) throws NullPointerException {
		return new FEMClosure(notNull(target));
	}

	/** Diese Methode gibt die beim Aufruf an den Stapelrahmen zu bindende Funktion zurück. */
	public FEMFunction target() {
		return this.target;
	}

	@Override
	public FEMFunction trace(FEMTracer tracer) throws NullPointerException {
		return from(this.target.trace(tracer));
	}

	@Override
	public FEMValue invoke(FEMFrame frame) {
		return FEMHandler.from(FEMBinding.from(this.target, frame));
	}

	@Override
	public FEMFunction compose(FEMFunction... params) throws NullPointerException {
		return FEMComposite.from(true, this, params);
	}

	@Override
	public int hashCode() {
		return Objects.hashPush(Objects.hash(this.target), 1233);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMClosure)) return false;
		var that = (FEMClosure)object;
		return Objects.equals(this.target, that.target);
	}

	private final FEMFunction target;

	private FEMClosure(FEMFunction target) {
		this.target = target;
	}

}