package bee.creative.fem;

import bee.creative.util.Objects;

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

	/** Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextsensitiv konvertierten {@link #data() Nutzdaten} dieses Werts zurück. Der
	 * Rückgabewert entspricht {@code context.dataFrom(this, type)}.
	 *
	 * @see FEMContext#dataFrom(FEMValue, FEMType)
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
	 * @param type Datentyp.
	 * @param context Kontext.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können. */

	public final <GData> GData data(final FEMType<GData> type, final FEMContext context)
		throws NullPointerException, ClassCastException, IllegalArgumentException {
		return context.dataFrom(this, type);
	}

	/** Diese Methode gibt den Datentyp der {@link #data() Nutzdaten} zurück.
	 *
	 * @return Datentyp. */
	public abstract FEMType<?> type();

	/** Diese Methode gibt die Nutzdaten zurück.
	 *
	 * @return Nutzdaten. */
	public abstract Object data();

	/** Diese Methode ist eine Abkürzung für {@code result(false)}.
	 *
	 * @see #result(boolean)
	 * @return ausgewerteter Ergebniswert. */
	public final FEMValue result() {
		return this.result(false);
	}

	/** Diese Methode gibt diesen Wert als ausgewerteten und optimierten Ergebniswert zurück.
	 *
	 * @see FEMArray#result(boolean)
	 * @see FEMFuture#result(boolean)
	 * @param recursive {@code true}, wenn die in diesem Wert enthaltenen Werte ebenfalls ausgewertet werden sollen, z.B. bei {@link FEMArray}. {@code false},
	 *        wenn nur dieser Wert ausgewertet werden soll, z.B. bei {@link FEMFuture}.
	 * @see FEMFrame#get(int)
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return ausgewerteter Ergebniswert. */
	public FEMValue result(final boolean recursive) {
		return this;
	}

	/** Diese Methode gibt die {@link FEMFunction Funktion} dieses Werts zurück. Bei einem {@link FEMHandler Funktionszeiger} ist dies dessen
	 * {@link FEMHandler#value() referenzierte Funktion}. Jeder andere Wert liefert sich selbst.
	 *
	 * @see FEMHandler
	 * @return Funktion des Werts. */
	public FEMFunction toFunction() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMValue toValue() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction toFuture() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMValue toFuture(final FEMFrame frame) throws NullPointerException {
		Objects.notNull(frame);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction toClosure(final FEMFrame frame) {
		return this;
	}

}