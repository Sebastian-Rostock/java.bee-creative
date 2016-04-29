package bee.creative.fem;

/** Diese Schnittstelle definiert einen Wert, der als Ergebnis der Auswertung einer {@link FEMFunction Funktion} oder als Parameter in einem {@link FEMFrame
 * Stapelrahmen} zur Auswertung einer Funktion verwendet werden kann. Ein solcher Wert besitzt dazu {@link FEMValue#data() Nutzdaten} mit einem bestimmten
 * {@link FEMValue#type() Datentyp}.<br>
 * Die Konvertierung der Nutzdaten in einen gegebenen Datentyp {@code type} kann im Rahmen eines gegebenen {@link FEMContext Kontextobjekts} {@code context} über
 * den Befehl {@code context.dataFrom(this, type)} erfolgen.
 * <p>
 * Wenn ein Wert mit <em>call-by-reference</em>- bzw. <em>return-by-reference</em>-Semantik bereitgestellt wird, kann dieser über die Methode {@link #result()}
 * in einen Wert mit <em>call-by-value</em>- bzw. <em>return-by-value</em>-Semantik umgewandelt werden.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FEMValue {

	/** Diese Methode gibt den Datentyp zurück.<br>
	 * 
	 * @return Datentyp. */
	public FEMType<?> type();

	/** Diese Methode gibt die Nutzdaten zurück.
	 * 
	 * @return Nutzdaten. */
	public Object data();

	/** Diese Methode ist eine Abkürzung für {@code this.result(false)}.
	 * 
	 * @see #result(boolean)
	 * @return Ergebniswert. */
	public FEMValue result();

	/** Diese Methode gibt diesen Wert evaluiert zurück.<br>
	 * Wenn dieser Wert aus der <em>call-by-value</em>- bzw. <em>return-by-value</em>-Semantik angehört, wird {@code this} geliefert.
	 * 
	 * @param recursive {@code true}, wenn die in diesem Wert enthaltenen Werte ebenfalls evaluiert werden sollen.<br>
	 *        {@code false}, wenn nur dieser Wert evaluiert werden soll.
	 * @see FEMFrame#get(int)
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return Ergebniswert. */
	public FEMValue result(boolean recursive);

}
