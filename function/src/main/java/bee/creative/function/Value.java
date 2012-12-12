package bee.creative.function;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer {@link Function Funktion} oder als Parameter in einem {@link Scope Ausführungskontext} verwendet werden kann. Ein solcher {@link Value Wert} besitzt dazu einen {@link Value#type() Datentyp}, einen {@link Value#data() Datensatz} sowie Methoden zur Konvertierung des {@link Value#data() Datensatzes} in andere {@link Value#type() Datentypen}.
 * 
 * @see Scope
 * @see Value
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Value {

	/**
	 * Diese Methode gibt den {@link Type} zurück.
	 * 
	 * @return {@link Type}.
	 */
	public Type<?> type();

	/**
	 * Diese Methode gibt den Datensatz zurück.
	 * 
	 * @return Datensatz.
	 */
	public Object data();

	/**
	 * Diese Methode gibt den Datensatz mit dem generischen Datentyp des gegebenen {@link Type}{@code s} zurück. <br>
	 * Die Implementation entspricht {@code if(this.type().is(type)) return (GData)this.data(); else throw new ClassCastException();}.
	 * 
	 * @see ClassCastException
	 * @param <GData> Typ des Datensatzes.
	 * @param type {@link Type}.
	 * @return Datensatz.
	 * @throws NullPointerException Wenn der gegebene {@link Type} {@code null} ist.
	 * @throws ClassCastException Wenn ein {@code cast} in den gegebenen {@link Type} unzulässig ist.
	 */
	public <GData> GData dataAs(Type<GData> type) throws NullPointerException, ClassCastException;

	/**
	 * Diese Methode gibt den in den generischen Datentyp des gegebenen {@link Type}s konvertierten Datensatz zurück. Der Rückgabewert entspricht {@code type.dataOf(this)}.
	 * 
	 * @see Type#dataOf(Value)
	 * @see Value#data()
	 * @param <GData> Typ des Datensatzes.
	 * @param type {@link Type}.
	 * @return konvertierter Datensatz.
	 * @throws NullPointerException Wenn der gegebene {@link Type} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Datensatz nicht in den generische Datentyp des gegebenen {@link Type}{@code s} konvertiert werden kann.
	 */
	public <GData> GData dataTo(Type<GData> type) throws NullPointerException, IllegalArgumentException;

}
