package bee.creative.fem;

/** Diese Schnittstelle definiert einen Wert, der als Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} einer {@link FEMFunction Funktion} oder als
 * {@link FEMFrame#get(int) Parameterwert} in einem {@link FEMFrame Stapelrahmen} zur Auswertung einer Funktion verwendet werden kann.
 * <p>
 * Ein solcher Wert besitzt dazu {@link FEMValue#data() Nutzdaten} mit einem bestimmten {@link FEMValue#type() Datentyp}. Die Konvertierung der Nutzdaten in
 * einen gegebenen Datentyp {@code type} kann im Rahmen eines gegebenen {@link FEMContext Kontextobjekts} {@code context} über den Befehl
 * {@code context.dataFrom(this, type)} erfolgen.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FEMValue {

	/** Diese Methode gibt den Datentyp zurück.
	 * 
	 * @return Datentyp. */
	public FEMType<?> type();

	/** Diese Methode gibt die Nutzdaten zurück.
	 * 
	 * @return Nutzdaten. */
	public Object data();

	/** Diese Methode ist eine Abkürzung für {@code result(false)}.
	 * 
	 * @see #result(boolean)
	 * @return ausgewerteter Ergebniswert. */
	public FEMValue result();

	/** Diese Methode gibt diesen Wert als ausgewerteten und optimierten Ergebniswert zurück.
	 * 
	 * @see FEMArray#result(boolean)
	 * @see FEMResult#result(boolean)
	 * @param recursive {@code true}, wenn die in diesem Wert enthaltenen Werte ebenfalls ausgewertet werden sollen, z.B. bei {@link FEMArray}.<br>
	 *        {@code false}, wenn nur dieser Wert ausgewertet werden soll, z.B. bei {@link FEMResult}.
	 * @see FEMFrame#get(int)
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return ausgewerteter Ergebniswert. */
	public FEMValue result(boolean recursive);

}
