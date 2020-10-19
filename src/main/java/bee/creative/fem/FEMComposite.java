package bee.creative.fem;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMFunction.BaseFunction;
import bee.creative.lang.Objects;
import bee.creative.util.Comparables.Items;

/** Diese Klasse implementiert eine komponierte Funktion, welche eine {@link #target() gegebene Funktion} mit den {@link #params() gegebenen
 * Parameterfunktionen} aufruft. Der Ergebniswert der komponierten Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} ist dazu von der
 * {@link #concat() Verkettung} abhängig.
 * <p>
 * Ohne Verkettung entspricht der Ergebniswert: <pre>this.target().invoke(frame.newFrame(this.params())</pre><br>
 * Mit Verkettung ist er dagegen <pre>this.target().invoke(frame).toFunction().invoke(frame.newFrame(this.params())</pre><br>
 * Die Verkettung ist damit dann anzuwenden, wenn die aufzurufende Funktion einen {@link FEMHandler Funktionszeiger} liefert.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMComposite extends BaseFunction implements Emuable, Items<FEMFunction> {

	@SuppressWarnings ("javadoc")
	public static final class FEMCompositeF extends FEMComposite {

		FEMCompositeF(final FEMFunction target, final FEMFunction[] params) {
			super(target, params);
		}

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this.target.invoke(frame.newFrame(this.params));
		}

		@Override
		public boolean concat() {
			return false;
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class FEMCompositeT extends FEMComposite {

		FEMCompositeT(final FEMFunction target, final FEMFunction[] params) {
			super(target, params);
		}

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this.target.invoke(frame).toFunction().invoke(frame.newFrame(this.params));
		}

		@Override
		public boolean concat() {
			return true;
		}

	}

	/** Diese Methode gibt eine neue komponierte Funktion mit den gegebenen Eigenschaften zurück.
	 *
	 * @param concat Verkettung.
	 * @param target aufzurufende Funktion.
	 * @param params Parameterfunktionen.
	 * @return komponierte Funktion. */
	public static FEMComposite from(final boolean concat, final FEMFunction target, final FEMFunction[] params) throws NullPointerException {
		return concat ? new FEMCompositeT(target, params) : new FEMCompositeF(target, params);
	}

	int hash;

	final FEMFunction target;

	final FEMFunction[] params;

	FEMComposite(final FEMFunction target, final FEMFunction[] params) {
		this.target = Objects.notNull(target);
		this.params = params.clone();
	}

	/** Diese Methode gibt den {@code index}-ten Kindabschnitt zurück. */
	@Override
	public FEMFunction get(final int index) throws IndexOutOfBoundsException {
		return this.params[index];
	}

	/** Diese Methode gibt die Anzahl der Parameterfunktionen zurück.
	 *
	 * @return Parameteranzahl. */
	public int size() {
		return this.params.length;
	}

	/** Diese Methode gibt die aufzurufende Funktion zurück.
	 *
	 * @return Aufrufziel. */
	public FEMFunction target() {
		return this.target;
	}

	/** Diese Methode gibt die Parameterfunktionen zurück.
	 *
	 * @return Parameterfunktionen. */
	public FEMFunction[] params() {
		return this.params.clone();
	}

	/** Diese Methode die {@link FEMComposite Verkettung} zurück.
	 *
	 * @return Verkettung. */
	public abstract boolean concat();

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromArray(this.params);
	}

	@Override
	public FEMComposite trace(final FEMTracer tracer) throws NullPointerException {
		final FEMFunction[] params = this.params.clone();
		for (int i = 0, size = params.length; i < size; i++) {
			params[i] = params[i].trace(tracer);
		}
		return FEMComposite.from(this.concat(), this.target.trace(tracer), params);
	}

	@Override
	public FEMFunction compose(final FEMFunction... params) throws NullPointerException {
		return FEMComposite.from(true, this, params.clone());
	}

	@Override
	public int hashCode() {
		int result = this.hash;
		if (result != 0) return result;
		result = Objects.hashPush(Objects.hash((Object[])this.params), Objects.hash(this.target));
		return this.hash = result != 0 ? result : -1;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMComposite)) return false;
		final FEMComposite that = (FEMComposite)object;
		return (this.concat() == that.concat()) && (this.hashCode() == that.hashCode()) && Objects.equals(this.target, that.target)
			&& Objects.equals(this.params, that.params);
	}

}