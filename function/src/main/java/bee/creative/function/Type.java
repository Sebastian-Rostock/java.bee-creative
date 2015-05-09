package bee.creative.function;

/**
 * Diese Schnittstelle definiert den Datentyp eines Werts, analog zur {@link Class} eines {@link Object}s. Ein solcher Datentyp besitzt Methoden zum
 * Konvertieren eines gegebenen Werts sowie zur Prüfung der Kompatibilität zu anderen Datentypen.
 * 
 * @see Value
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts, in welchen ein gegebener Wert via {@link #valueOf(Value)} oder {@link #valueOf(Value, Context)} konvertiert werden kann.
 */
public interface Type<GValue> {

	/**
	 * Diese Methode gibt den Identifikator dieses Datentyps zurück. Dieser sollte über eine statische Konstante definiert werden, um Fallunterscheidungen mit
	 * einem {@code switch}-Statement umsetzen zu können.
	 * 
	 * @return Identifikator dieses Datentyps.
	 */
	public int id();

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist. Dies kann der Fall sein, wenn der gegebene
	 * Datentyp gleich zu diesem oder ein Vorfahre dieses Datentyps ist. Wenn der gegebene Datentyp {@code null} ist, wird {@code false} zurück gegeben.
	 * 
	 * @see Class#isAssignableFrom(Class)
	 * @param type Datentyp.
	 * @return {@code true}, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist.
	 */
	public boolean is(Type<?> type);

	/**
	 * Diese Methode konvertiert den gegebenen Wert kontextfrei in einen Wert dieses Datentyps und gibt ihn zurück.<br>
	 * Der Rückgabewert entspricht {@code Contexts.getDefaultContext().cast(value, this)}.
	 * 
	 * @see Type#id()
	 * @see Value#type()
	 * @see Contexts#getDefaultContext()
	 * @see #valueOf(Value, Context)
	 * @param value gegebener Wert.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
	 */
	public GValue valueOf(Value value) throws NullPointerException, ClassCastException, IllegalArgumentException;

	/**
	 * Diese Methode konvertiert den gegebenen Wert kontextsensitiv in einen Wert dieses Datentyps und gibt ihn zurück.<br>
	 * Der Rückgabewert entspricht {@code context.cast(value, this)}.
	 * 
	 * @see Type#id()
	 * @see Value#type()
	 * @see Context#cast(Value, Type)
	 * @param value gegebener Wert.
	 * @param context Kontextobjekt.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
	 */
	public GValue valueOf(Value value, Context context) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
