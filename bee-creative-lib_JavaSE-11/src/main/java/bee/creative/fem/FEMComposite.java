package bee.creative.fem;

import java.util.Iterator;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMFunction.BaseFunction;
import bee.creative.lang.Array2;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert eine komponierte Funktion, welche eine {@link #target() gegebene Funktion} mit den {@link #params() gegebenen
 * Parameterfunktionen} aufruft. Der Ergebniswert der komponierten Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} ist dazu von der
 * {@link #isConcat() Verkettung} abhängig.
 * <p>
 * Ohne Verkettung entspricht der Ergebniswert: <pre>this.target().invoke(frame.newFrame(this.params())</pre><br>
 * Mit Verkettung ist er dagegen <pre>this.target().invoke(frame).toFunction().invoke(frame.newFrame(this.params())</pre><br>
 * Die Verkettung ist damit dann anzuwenden, wenn die aufzurufende Funktion einen {@link FEMHandler Funktionszeiger} liefert.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMComposite extends BaseFunction implements Emuable, Array2<FEMFunction>, UseToString {

	/** Diese Methode gibt eine neue komponierte Funktion mit den gegebenen Eigenschaften zurück.
	 *
	 * @param concat Verkettung.
	 * @param target aufzurufende Funktion.
	 * @param params Parameterfunktionen.
	 * @return komponierte Funktion. */
	public static FEMComposite from(boolean concat, FEMFunction target, FEMFunction[] params) throws NullPointerException {
		return concat ? new FEMCompositeT(target, params) : new FEMCompositeF(target, params);
	}

	/** Diese Methode gibt den {@code index}-ten Kindabschnitt zurück. */
	@Override
	public final FEMFunction get(int index) throws IndexOutOfBoundsException {
		return this.params[index];
	}

	/** Diese Methode gibt die Anzahl der Parameterfunktionen zurück.
	 *
	 * @return Parameteranzahl. */
	@Override
	public final int size() {
		return this.params.length;
	}

	/** Diese Methode gibt die aufzurufende Funktion zurück.
	 *
	 * @return Aufrufziel. */
	public final FEMFunction target() {
		return this.target;
	}

	/** Diese Methode gibt die Parameterfunktionen zurück.
	 *
	 * @return Parameterfunktionen. */
	public final FEMFunction[] params() {
		return this.params.clone();
	}

	/** Diese Methode die {@link FEMComposite Verkettung} zurück.
	 *
	 * @return Verkettung. */
	public abstract boolean isConcat();

	@Override
	public final long emu() {
		return EMU.fromObject(this) + EMU.fromArray(this.params);
	}

	@Override
	public final FEMComposite trace(FEMTracer tracer) throws NullPointerException {
		var params = this.params.clone();
		for (var i = 0; i < params.length; i++) {
			params[i] = params[i].trace(tracer);
		}
		return FEMComposite.from(this.isConcat(), this.target.trace(tracer), params);
	}

	@Override
	public final FEMFunction compose(FEMFunction... params) throws NullPointerException {
		return FEMComposite.from(true, this, params.clone());
	}

	@Override
	public final int hashCode() {
		var result = this.hash;
		if (result != 0) return result;
		result = Objects.hashPush(Objects.hash((Object[])this.params), Objects.hash(this.target));
		return this.hash = result != 0 ? result : -1;
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMComposite)) return false;
		var that = (FEMComposite)object;
		return (this.isConcat() == that.isConcat()) && (this.hashCode() == that.hashCode()) && Objects.equals(this.target, that.target)
			&& Objects.equals(this.params, that.params);
	}

	@Override
	public final Iterator<FEMFunction> iterator() {
		return Iterators.fromArray(this, 0, this.size());
	}

	final FEMFunction target;

	final FEMFunction[] params;

	int hash;

	FEMComposite(FEMFunction target, FEMFunction[] params) {
		this.target = Objects.notNull(target);
		this.params = params.clone();
	}

	static final class FEMCompositeF extends FEMComposite {

		@Override
		public FEMValue invoke(FEMFrame frame) throws NullPointerException {
			return this.target.invoke(frame.newFrame(this.params));
		}

		@Override
		public boolean isConcat() {
			return false;
		}

		FEMCompositeF(FEMFunction target, FEMFunction[] params) {
			super(target, params);
		}

	}

	static final class FEMCompositeT extends FEMComposite {

		@Override
		public FEMValue invoke(FEMFrame frame) throws NullPointerException {
			return this.target.invoke(frame).toFunction().invoke(frame.newFrame(this.params));
		}

		@Override
		public boolean isConcat() {
			return true;
		}

		FEMCompositeT(FEMFunction target, FEMFunction[] params) {
			super(target, params);
		}

	}

}