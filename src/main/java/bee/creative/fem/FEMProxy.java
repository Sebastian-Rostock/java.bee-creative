package bee.creative.fem;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.util.Objects;
import bee.creative.util.Property;

/** Diese Klasse implementiert den benannten Platzhalter einer Funktion, dessen {@link #invoke(FEMFrame)}-Methode an eine {@link #set(FEMFunction) gegebene
 * Funktion} delegiert.
 *
 * @see FEMCompiler#proxy(String)
 * @see FEMCompiler#proxies()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMProxy extends FEMFunction implements Property<FEMFunction>, Emuable {

	public static FEMProxy from(final String id) {
		return FEMProxy.from(FEMString.from(id));
	}

	public static FEMProxy from(final FEMString id) {
		return FEMProxy.from(id, null);
	}

	public static FEMProxy from(final FEMString id, final FEMFunction function) {
		return new FEMProxy(id, id, null);
	}

	/** Dieses Feld speichert die Kennung des Platzhalters. */
	final FEMValue id;

	/** Dieses Feld speichert den Namen. */
	FEMString name = FEMString.EMPTY;

	/** Dieses Feld speichert die Funktion. */
	FEMFunction function;

	/** Dieser Konstruktor initialisiert den Plathhalter.
	 *
	 * @param id {@link #getId() Kennung}.
	 * @param name {@link #getName() Name}.
	 * @param function Funktion
	 * @throws NullPointerException Wenn {@code id} {@code null} ist. */
	public FEMProxy(final FEMValue id, final FEMString name, final FEMFunction function) throws NullPointerException {
		this.id = Objects.notNull(id);
		this.useName(name);
		this.useFunction(function);
	}

	/** Diese Methode gibt die Kennung des Platzhalters zurück. Diese wird im Konstruktor initialisiert und bildet die Grundlage für die Berechnung von
	 * {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz}.
	 *
	 * @return Kennung. */
	public FEMValue getId() {
		return this.id;
	}

	/** Diese Methode gibt den Namen des Platzhalters zurück. Dieser wird in der {@link #toString() Textdarstellung} eingesetzt.
	 *
	 * @return Name. */
	public FEMString getName() {
		return this.name;
	}

	public FEMFunction getFunction() {
		return this.get();
	}

	public FEMProxy useName(final FEMString name) {
		this.name = Objects.notNull(name, FEMString.EMPTY);
		return this;
	}

	public FEMProxy useFunction(final FEMFunction function) {
		this.set(function);
		return this;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #getId() Kennung} dieses Platzhalters gleich der des gegebenen ist.
	 *
	 * @param that Platzhalter.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public boolean equals(final FEMProxy that) throws NullPointerException {
		return this.id.equals(that.id);
	}

	/** Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird. Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch
	 * nicht aufgerufen wurde.
	 *
	 * @return Funktion oder {@code null}. */
	@Override
	public FEMFunction get() {
		return this.function;
	}

	/** Diese Methode setzt die in {@link #invoke(FEMFrame)} aufzurufende Funktion.
	 *
	 * @param function Funktion oder {@code null}. */
	@Override
	public void set(final FEMFunction function) {
		this.function = function;
	}

	/** {@inheritDoc} */
	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.from(this.id) + EMU.from(this.name);
	}

	/** {@inheritDoc} */
	@Override
	public FEMValue invoke(final FEMFrame frame) {
		return this.function.invoke(frame);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMProxy)) return false;
		return this.equals((FEMProxy)object);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return FEMDomain.NORMAL.formatConst(this.name.toString());
	}

}