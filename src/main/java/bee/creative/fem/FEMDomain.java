package bee.creative.fem;

import java.math.BigDecimal;

/** Diese Schnittstelle definiert domänenspezifische Kompilations- und Formatierungsmethoden, die von einem {@link FEMCompiler} zur Übersetzung von Quelltexten
 * in Werte, Funktionen und Parameternamen bzw. von einem {@link FEMFormatter} Übersetzung von Werten und Funktionen in Quelltexte genutzt werden können.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMDomain {

	/** Dieses Feld speichert die native {@link FEMDomain} mit folgendem Verhalten:
	 * <dl>
	 * <dt>{@link #formatData(FEMFormatter, Object)}</dt>
	 * <dd>Siehe {@link #NORMAL}.</dd>
	 * <dt>{@link #formatFunction(FEMFormatter, FEMFunction)}</dt>
	 * <dd>Siehe {@link #NORMAL}.</dd>
	 * <dt>{@link #compileName(FEMCompiler)}</dt>
	 * <dd>Siehe {@link #NORMAL}.</dd>
	 * <dt>{@link #compileFunction(FEMCompiler)}</dt>
	 * <dd>Soweit möglich wird eine Instanz von {@link FEMNative} mit Nutzdaten vom Typ {@code null}, {@link String}, {@link Character}, {@link Boolean} oder
	 * {@link BigDecimal} geliefert.<br>
	 * Andernfalls wird der gelieferten Parameter über {@link FEMReflection#from(String)} ermittelt bzw. ein {@link FEMCompiler#proxy(String) Platzhalter}
	 * geliefert.</dd>
	 * </dl>
	*/
	public static final FEMDomain NATIVE = new FEMDomain() {

		@Override
		public FEMFunction compileFunction(final FEMCompiler compiler) throws IllegalArgumentException {
			String section = compiler.section();
			switch (compiler.symbol()) {
				case '"':
					return new FEMNative(FEMParser.parseString(section));
				case '\'':
					return new FEMNative(new Character(FEMParser.parseString(section).charAt(0)));
				case '!':
					section = FEMParser.parseValue(section);
				default:
					if (section.equals("null")) return FEMNative.NULL;
					if (section.equals("true")) return FEMNative.TRUE;
					if (section.equals("false")) return FEMNative.FALSE;
					try {
						return new FEMNative(new BigDecimal(section));
					} catch (final NumberFormatException cause) {}
					try {
						return FEMReflection.from(section);
					} catch (final Exception cause) {}
					return compiler.proxy(section);
			}
		}

		@Override
		public String toString() {
			return "NATIVE";
		}

	};

	/** Dieses Feld speichert die normale {@link FEMDomain} mit folgendem Verhalten:
	 * <dl>
	 * <dt>{@link #formatData(FEMFormatter, Object)}</dt>
	 * <dd>{@link FEMScript Aufbereitete Quelltexte} werden über {@link FEMCompiler#formatScript(FEMFormatter)} formatiert.<br>
	 * {@link FEMFrame Stapelrahmen} und {@link FEMFunction Funktionen} werden über {@link #formatFunction(FEMFormatter, FEMFunction)} formatiert.<br>
	 * Alle anderen Objekte werden über {@link String#valueOf(Object)} formatieren.</dd>
	 * <dt>{@link #formatFunction(FEMFormatter, FEMFunction)}</dt>
	 * <dd>{@link FEMFunction Funktionen} werden über {@link FEMFunction#toScript(FEMFormatter)} formatiert.</dd>
	 * <dt>{@link #compileName(FEMCompiler)}</dt>
	 * <dd>Als Name wird der {@link FEMCompiler#section() aktuelle Bereich} geliefert.</dd>
	 * <dt>{@link #compileFunction(FEMCompiler)}</dt>
	 * <dd>Soweit möglich wird eine Instanz von {@link FEMString}, {@link FEMVoid}, {@link FEMBoolean}, {@link FEMInteger}, {@link FEMDecimal},
	 * {@link FEMDatetime}, {@link FEMDuration} oder {@link FEMBinary} geliefert.<br>
	 * Andernfalls wird ein {@link FEMCompiler#proxy(String) Platzhalter} geliefert.</dd>
	 * </dl>
	*/
	public static final FEMDomain NORMAL = new FEMDomain();

	{}

	/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.
	 *
	 * @param target {@link FEMFormatter}.
	 * @param data Objekt.
	 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
	public void formatData(final FEMFormatter target, final Object data) throws IllegalArgumentException {
		if (data instanceof FEMScript) {
			new FEMCompiler().useDomain(this).useScript((FEMScript)data).formatScript(target);
		} else if (data instanceof FEMFrame) {
			((FEMFrame)data).toScript(target);
		} else if (data instanceof FEMFunction) {
			((FEMFunction)data).toScript(target);
		} else {
			target.put(String.valueOf(data));
		}
	}

	/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.
	 *
	 * @param target {@link FEMFormatter}.
	 * @param function Funktion.
	 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
	public void formatFunction(final FEMFormatter target, final FEMFunction function) throws IllegalArgumentException {
		function.toScript(target);
	}

	/** Diese Methode gibt den im {@link FEMCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
	 *
	 * @see FEMCompiler#range()
	 * @see FEMCompiler#script()
	 * @param compiler Kompiler mit Bereich und Quelltext.
	 * @return Funktions- bzw. Parametername.
	 * @throws IllegalArgumentException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen). */
	public String compileName(final FEMCompiler compiler) throws IllegalArgumentException {
		return compiler.section();
	}

	/** Diese Methode gibt den im {@link FEMCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebene Parameter als Funktion zurück.<br>
	 * Der Wert des Parameters entspricht dabei dem Ergebniswert der gelieferten Funktion.<br>
	 * Konstante Parameterwerte können als {@link FEMValue} oder {@link FEMProxy} geliefert werden. Funktion als Parameterwert können über
	 * {@link FEMFunction#toValue()} oder {@link FEMFunction#toClosure()} geliefert werden.
	 *
	 * @see FEMCompiler#proxy(String)
	 * @see FEMCompiler#range()
	 * @see FEMCompiler#script()
	 * @see FEMCompiler#section()
	 * @param compiler Kompiler mit Bereich und Quelltext.
	 * @return Parameterfunktion.
	 * @throws IllegalArgumentException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält. */
	public FEMFunction compileFunction(final FEMCompiler compiler) throws IllegalArgumentException {
		String section = compiler.section();
		switch (compiler.symbol()) {
			case '"':
			case '\'':
				return FEMString.from(FEMParser.parseString(section));
			case '!':
				section = FEMParser.parseValue(section);
			default:
				try {
					return FEMVoid.from(section);
				} catch (final IllegalArgumentException cause) {}
				try {
					return FEMBoolean.from(section);
				} catch (final IllegalArgumentException cause) {}
				try {
					return FEMInteger.from(section);
				} catch (final IllegalArgumentException cause) {}
				try {
					return FEMDecimal.from(section);
				} catch (final IllegalArgumentException cause) {}
				try {
					return FEMDatetime.from(section);
				} catch (final IllegalArgumentException cause) {}
				try {
					return FEMDuration.from(section);
				} catch (final IllegalArgumentException cause) {}
				try {
					return FEMBinary.from(section);
				} catch (final IllegalArgumentException cause) {}
				return compiler.proxy(section);
		}
	}

}