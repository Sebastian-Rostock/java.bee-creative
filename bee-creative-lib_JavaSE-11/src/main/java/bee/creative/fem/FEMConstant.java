package bee.creative.fem;

import static bee.creative.fem.FEMString.from;
import static bee.creative.lang.Objects.notNull;
import bee.creative.util.Consumer;

/** Diese Klasse implementiert eine Konstante als einmalig setzbarer Verweis auf einen Wert. Solange der Wert nicht initialisiert ist, liefert {@link #type()}
 * den {@link #TYPE} und {@link #toString()} den {@link #name()}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMConstant implements FEMValue, Consumer<FEMValue> {

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMConstant> TYPE = new FEMType<>(FEMConstant.TYPE_ID);

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int TYPE_ID = -2;

	/** Diese Methode liefert eine neue Konstante mit dem gegebenen Namen. */
	public static FEMConstant femConstFrom(String name) throws NullPointerException {
		return femConstFrom(from(name));
	}

	/** Diese Methode liefert eine neue Konstante mit dem gegebenen Namen. */
	public static FEMConstant femConstFrom(FEMString name) throws NullPointerException {
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

	@Override
	public Object data() {
		return this.result().data();
	}

	@Override
	public synchronized FEMType<?> type() {
		return this.value != null ? this.value.type() : TYPE;
	}

	@Override
	public synchronized FEMValue result() {
		if (this.value != null) return this.value;
		throw new IllegalStateException();
	}

	@Override
	public synchronized FEMValue result(boolean deep) {
		return this.result();
	}

	@Override
	public FEMValue toValue() {
		return this.result().toValue();
	}

	@Override
	public FEMFunction toFuture() {
		return this.result().toFuture();
	}

	@Override
	public FEMValue toFuture(FEMFrame frame) throws NullPointerException {
		return this.result().toFuture(frame);
	}

	@Override
	public FEMFunction toFunction() {
		return this.result().toFunction();
	}

	@Override
	public FEMValue invoke(FEMFrame frame) throws NullPointerException {
		return this.result().invoke(frame);
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
	public FEMFunction compose(Iterable<? extends FEMFunction> params) throws NullPointerException {
		return this.result().compose(params);
	}

	@Override
	public int hashCode() {
		return this.result().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this.result().equals(object);
	}

	@Override
	public synchronized String toString() {
		if (this.value != null) return this.value.toString();
		return FEMDomain.DEFAULT.printConst(this.name.toString());
	}

	private FEMString name;

	private FEMValue value;

	private FEMConstant(FEMString name) throws NullPointerException {
		this.name = notNull(name);
	}

}