package bee.creative.function;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer Funktion oder als Parameter in einem Ausführungskontext verwendet werden kann. Ein solcher
 * Wert besitzt dazu {@link Value#data() Nutzdaten}, einen {@link Value#type() Datentyp} und eine {@link #valueTo(Type) Konvertierungsmethode}.
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
	 * Diese Methode konvertiert diesen Wert kontextfrei in einen Wert des gegebenen Datentyps und gibt diesen zurück.<br>
	 * Der Rückgabewert entspricht {@code type.cast(this)}, d.h. {@code Contexts.getDefault().cast(this, type)}.
	 * 
	 * @see Type#valueOf(Value)
	 * @see Context#cast(Value, Type)
	 * @param <GValue> Typ des Werts, in welchen dieser Wert konvertiert wird.
	 * @param type Datentyp.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn der gegebene {@link Type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der Datensatz nicht in den generische Datentyp des gegebenen {@link Type}{@code s} konvertiert werden kann.
	 */
	public <GValue> GValue valueTo(Type<GValue> type) throws NullPointerException, ClassCastException, IllegalArgumentException;

	/**
	 * Diese Methode konvertiert diesen Wert kontextsensitiv in einen Wert des gegebenen Datentyps und gibt diesen zurück.<br>
	 * Der Rückgabewert entspricht {@code type.cast(this, context)}, d.h. {@code context.cast(this, type)}.
	 * 
	 * @see Type#valueOf(Value, Context)
	 * @see Context#cast(Value, Type)
	 * @param <GValue> Typ des Werts, in welchen dieser Wert konvertiert wird.
	 * @param type Datentyp.
	 * @param context Kontext.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der Datensatz nicht in den generische Datentyp des gegebenen {@link Type}{@code s} konvertiert werden kann.
	 */
	public <GValue> GValue valueTo(Type<GValue> type, Context context) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
