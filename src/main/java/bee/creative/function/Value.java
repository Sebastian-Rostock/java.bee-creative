package bee.creative.function;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer Funktion oder als Parameter in einem Ausführungskontext verwendet werden kann. Ein solcher
 * Wert besitzt dazu {@link Value#data() Nutzdaten}, einen {@link Value#type() Datentyp} und eine {@link #dataTo(Type) Konvertierungsmethode}.
 * 
 * @see Scope
 * @see Values
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Value {

	/**
	 * Diese Methode gibt den Datentyp zurück.
	 * 
	 * @return Datentyp.
	 */
	public Type<?> type();

	/**
	 * Diese Methode gibt die Nutzdaten zurück.
	 * 
	 * @return Nutzdaten.
	 */
	public Object data();

	/**
	 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextfrei konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code Context.DEFAULT.cast(this, type)}.
	 * 
	 * @see Context#DEFAULT
	 * @see Context#cast(Value, Type)
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
	 * @param type Datentyp.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code type} bzw. {@code Context.DEFAUL} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können.
	 */
	public <GData> GData dataTo(Type<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException;

	/**
	 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextsensitiv konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code context.cast(this, type)}.
	 * 
	 * @see Context#cast(Value, Type)
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
	 * @param type Datentyp.
	 * @param context Kontext.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können.
	 */
	public <GData> GData dataTo(Type<GData> type, Context context) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
