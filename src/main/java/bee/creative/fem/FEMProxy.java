package bee.creative.fem;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.util.Objects;
import bee.creative.util.Property;

/** Diese Klasse implementiert den benannten Platzhalter einer Funktion, dessen {@link #invoke(FEMFrame)}-Methode an eine {@link #set(FEMFunction) gegebene
 * Funktion} delegiert. {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} beziehen sich auf den {@link #name() Namen} des Platzhalters.
 *
 * @see FEMCompiler#proxy(String)
 * @see FEMCompiler#proxies()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMProxy extends FEMFunction implements Property<FEMFunction>, Emuable {

	/** Dieses Feld speichert den Namen. */
	final String name;

	/** Dieses Feld speichert die Funktion. */
	FEMFunction function;

	/** Dieser Konstruktor initialisiert den Namen.
	 *
	 * @param name Name.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public FEMProxy(final String name) throws NullPointerException {
		this.name = Objects.notNull(name);
	}

	/** Dieser Konstruktor initialisiert Namen und Funktion.
	 *
	 * @see #set(FEMFunction)
	 * @param name Name.
	 * @param function Funktion.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public FEMProxy(final String name, final FEMFunction function) throws NullPointerException {
		this(name);
		this.set(function);
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

	/** Diese Methode gibt den Namen des Platzhalters zurück.
	 *
	 * @return Name. */
	public String name() {
		return this.name;
	}

	/** {@inheritDoc} */
	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.from(this.name);
	}

	/** {@inheritDoc} */
	@Override
	public FEMValue invoke(final FEMFrame frame) {
		return this.function.invoke(frame);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMProxy)) return false;
		final FEMProxy that = (FEMProxy)object;
		return this.name.equals(that.name);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final String result = FEMDomain.NORMAL.formatConst(this.name);
		if (this.function == null) return result;
		return result + FEMHandler.from(this.function).toString();
	}

}