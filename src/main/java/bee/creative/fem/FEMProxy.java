package bee.creative.fem;

import bee.creative.util.Objects;

/** Diese Klasse implementiert den benannten Platzhalter einer Funktion, dessen {@link #invoke(FEMFrame)}-Methode an eine {@link #set(FEMFunction) gegebene
 * Funktion} delegiert.
 *
 * @see FEMCompiler#proxy(String)
 * @see FEMCompiler#proxies()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMProxy extends FEMFunction {

	/** Diese Methode gibt eine neue {@link FEMProxy} mit dem gegebenen Namen zurück.
	 *
	 * @param name Name.
	 * @return {@link FEMProxy}.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public static FEMProxy from(final String name) throws NullPointerException {
		return new FEMProxy(name);
	}

	{}

	/** Dieses Feld speichert den Namen. */
	final String name;

	/** Dieses Feld speichert die Funktion. */
	FEMFunction function;

	/** Dieser Konstruktor initialisiert den Namen.
	 *
	 * @param name Name.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public FEMProxy(final String name) throws NullPointerException {
		this.name = Objects.assertNotNull(name);
	}

	{}

	/** Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird.<br>
	 * Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch nicht aufgerufen wurde.
	 *
	 * @return Funktion oder {@code null}. */
	public final FEMFunction get() {
		return this.function;
	}

	/** Diese Methode setzt die in {@link #invoke(FEMFrame)} aufzurufende Funktion.
	 *
	 * @param function Funktion oder {@code null}. */
	public final void set(final FEMFunction function) {
		this.function = function;
	}

	/** Diese Methode gibt den Namen des Platzhalters zurück.
	 *
	 * @return Name. */
	public final String name() {
		return this.name;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return this.function.invoke(frame);
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
		target.put(FEMParser.formatValue(this.name));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final FEMFunction function = this.function;
		if (function != null) return new FEMFormatter().put(FEMParser.formatValue(this.name)).putHandler(this.function).format();
		return FEMParser.formatValue(this.name);
	}

}