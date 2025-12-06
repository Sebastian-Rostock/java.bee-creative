package bee.creative.fem;

import static bee.creative.lang.Objects.notNull;
import bee.creative.util.Consumer;

/** Diese Klasse implementiert eine Konstante als einmalig setzbarer Verweis auf einen Wert.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMConstant implements FEMValue, Consumer<FEMValue> {

	/** Diese Methode liefert eine neue Konstante mit dem gegebenen Namen. */
	public static FEMConstant from(String name) throws NullPointerException {
		return from(FEMString.from(name));
	}

	/** Diese Methode liefert eine neue Konstante mit dem gegebenen Namen. */
	public static FEMConstant from(FEMString name) throws NullPointerException {
		return new FEMConstant(name);
	}

	/** Diese Methode initialisiert den Wert der Konstanten. */
	@Override
	public synchronized void set(FEMValue value) throws NullPointerException, IllegalStateException {
		if (this.value != null) throw new IllegalStateException();
		this.value = notNull(value);
	}

	public FEMString name() {
		return this.name;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn der {@link #result() Ergebniswert} ausgewertet {@link #set(FEMValue) gesetzt} wurde. */
	public boolean ready() {
		return this.value != null;
	}

	/** Diese Methode liefert {@code this.result().data()}. */
	@Override
	public Object data() {
		return this.result().data();
	}

	/** Diese Methode liefert {@code this.result().type()}. */
	@Override
	public FEMType<?> type() {
		return this.result().type();
	}

	@Override
	public synchronized FEMValue result(boolean deep) {
		if (this.value != null) return this.value;
		throw new IllegalStateException();
	}

	@Override
	public FEMFunction trace(FEMTracer tracer) throws NullPointerException {
		return this.result().trace(tracer);
	}

	@Override
	public FEMFunction compose(FEMFunction... params) throws NullPointerException {
		return this.result().compose(params);
	}

	@Override
	public int hashCode() {
		return this.result().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMValue)) return false;
		return this.result().equals(object);
	}

	@Override
	public synchronized String toString() {
		if (this.value != null) return this.value.toString();
		return FEMDomain.DEFAULT.printConst(this.name.toString());
	}

	@Override
	public FEMFunction toFunction() {
		return this.result().toFunction();
	}

	private FEMString name;

	private FEMValue value;

	private FEMConstant(FEMString name) throws NullPointerException {
		this.name = notNull(name);
	}

}