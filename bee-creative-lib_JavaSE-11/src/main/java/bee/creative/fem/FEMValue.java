package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen abstrakten Wert, der als Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} einer {@link FEMFunction Funktion}
 * oder als {@link FEMFrame#get(int) Parameterwert} in einem {@link FEMFrame Stapelrahmen} zur Auswertung einer Funktion verwendet werden kann.
 * <p>
 * Ein solcher Wert besitzt dazu {@link FEMValue#data() Nutzdaten} mit einem bestimmten {@link FEMValue#type() Datentyp}. Die Konvertierung der Nutzdaten in
 * einen gegebenen Datentyp {@code type} kann im Rahmen eines gegebenen {@link FEMContext Kontextobjekts} {@code context} über den Befehl
 * {@code context.dataFrom(this, type)} erfolgen.
 * <p>
 * Die Methode {@link #invoke(FEMFrame)} des Werts liefert {@code this}, sodass ein Wert direkt als Parameterfunktion eingesetzt werden kann.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMValue extends FEMFunction {

	/** Diese Methode gibt den Datentyp der {@link #data() Nutzdaten} zurück.
	 *
	 * @return Datentyp. */
	public abstract FEMType<?> type();

	/** Diese Methode gibt die Nutzdaten zurück.
	 *
	 * @return Nutzdaten. */
	public abstract Object data();

	/** Diese Methode ist eine Abkürzung für {@link #result(boolean) result(false)}.
	 *
	 * @return ausgewerteter Ergebniswert. */
	public final FEMValue result() {
		return this.result(false);
	}

	/** Diese Methode gibt diesen Wert als ausgewerteten und optimierten Ergebniswert zurück.
	 *
	 * @see FEMArray#result(boolean)
	 * @see FEMFuture#result(boolean)
	 * @param deep {@code true}, wenn die in diesem Wert enthaltenen Werte ebenfalls ausgewertet werden sollen, z.B. bei {@link FEMArray}. {@code false}, wenn nur
	 *        dieser Wert ausgewertet werden soll, z.B. bei {@link FEMFuture}.
	 * @see FEMFrame#get(int)
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return ausgewerteter Ergebniswert. */
	public FEMValue result(final boolean deep) {
		return this;
	}

	@Override
	public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
		Objects.notNull(frame);
		return this;
	}

	@Override
	public FEMFunction compose(final FEMFunction... params) throws NullPointerException {
		Objects.notNull(params);
		return this;
	}

	@Override
	public final FEMValue toValue() {
		return this;
	}

	@Override
	public final FEMFunction toFuture() {
		return this;
	}

	@Override
	public final FEMValue toFuture(final FEMFrame frame) throws NullPointerException {
		Objects.notNull(frame);
		return this;
	}

	/** Diese Methode gibt die {@link FEMFunction Funktion} dieses Werts zurück. Bei einem {@link FEMHandler Funktionszeiger} ist dies dessen
	 * {@link FEMHandler#value() referenzierte Funktion}. Jeder andere Wert liefert sich selbst.
	 *
	 * @see FEMHandler
	 * @return Funktion des Werts. */
	public FEMFunction toFunction() { // DONE
		return this;
	}

}