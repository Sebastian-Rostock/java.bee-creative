package bee.creative.function;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis eienr {@link Function Funktion} oder als Parameter in
 * einem {@link Scope Ausführungskontext} verwendet werden kann. Ein {@link Value Wert} besitzt einen
 * {@link Value#type() Datentyp}, einen {@link Value#data() Datensatz} sowie Methoden zur Konvertierung des Datensatzes
 * in Wertelisten, Zeichenketten, Zahlenwerte sowie Wahrheitswerte.
 * 
 * @see Values
 * @see Scope
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Value {

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@code null}.
	 */
	public static final int TYPE_VOID = 0;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Value Wertlisten}.
	 */
	public static final int TYPE_ARRAY = 1;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für beliebige {@link Object Objekte}.
	 */
	public static final int TYPE_OBJECT = 2;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link String Zeichenketten}.
	 */
	public static final int TYPE_STRING = 3;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Number Zahlenwerte}.
	 */
	public static final int TYPE_NUMBER = 4;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Boolean Wahrheitswerte}.
	 */
	public static final int TYPE_BOOLEAN = 5;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Function Funktionen}.
	 */
	public static final int TYPE_FUNCTION = 6;

	/**
	 * Diese Methode gibt den Datentyp zurück.
	 * 
	 * @return Datentyp.
	 */
	public int type();

	/**
	 * Diese Methode gibt den Datensatz zurück.
	 * 
	 * @return Datensatz.
	 */
	public Object data();

	/**
	 * Diese Methode gibt den Datensatz als {@link Value Wertliste} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>{@link Value#type() Datentyp}</th>
	 * <th>{@link Number Zahlenwert}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_VOID TYPE_VOID}</td>
	 * <td>{@code new Value[0]}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_ARRAY TYPE_ARRAY}</td>
	 * <td>{@code (Value[])data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_OBJECT TYPE_OBJECT}</td>
	 * <td>{@code new Value[0]}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_STRING TYPE_STRING}</td>
	 * <td>{@code new Value[0]}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_NUMBER TYPE_NUMBER}</td>
	 * <td>{@code new Value[0]}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_BOOLEAN TYPE_BOOLEAN}</td>
	 * <td>{@code new Value[0]}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_FUNCTION TYPE_FUNCTION}</td>
	 * <td>{@code new Value[0]}</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @return Datensatz als {@link Value Wertliste}.
	 */
	public Value[] arrayData();

	/**
	 * Diese Methode gibt den Datensatz als {@link String Zeichenkette} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>{@link Value#type() Datentyp}</th>
	 * <th>{@link Number Zahlenwert}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_VOID TYPE_VOID}</td>
	 * <td>{@code ""}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_ARRAY TYPE_ARRAY}</td>
	 * <td>{@code (arrayData().length != 0) ? arrayData()[0].stringData() : ""}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_OBJECT TYPE_OBJECT}</td>
	 * <td>{@code String.valueOf(data())}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_STRING TYPE_STRING}</td>
	 * <td>{@code (String)data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_NUMBER TYPE_NUMBER}</td>
	 * <td>{@code String.valueOf(numberData())}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_BOOLEAN TYPE_BOOLEAN}</td>
	 * <td>{@code String.valueOf(booleanData())}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_FUNCTION TYPE_FUNCTION}</td>
	 * <td>{@code String.valueOf(functionData())}</td>
	 * </tr>
	 * </table>
	 * 
	 * @return Datensatz als {@link String Zeichenkette}.
	 */
	public String stringData();

	/**
	 * Diese Methode gibt den Datensatz als {@link Number Zahlenwert} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>{@link Value#type() Datentyp}</th>
	 * <th>{@link Number Zahlenwert}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_VOID TYPE_VOID}</td>
	 * <td>{@code Double.valueOf(NaN)}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_ARRAY TYPE_ARRAY}</td>
	 * <td>{@code (arrayData().length != 0) ? arrayData()[0].numberData() : Double.valueOf(NaN)}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_OBJECT TYPE_OBJECT}</td>
	 * <td>{@code Double.valueOf(stringData())} bzw. {@code Double.valueOf(NaN)} bei einer {@link NumberFormatException
	 * NumberFormatException}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_STRING TYPE_STRING}</td>
	 * <td>{@code Double.valueOf(stringData())} bzw. {@code Double.valueOf(NaN)} bei einer {@link NumberFormatException
	 * NumberFormatException}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_NUMBER TYPE_NUMBER}</td>
	 * <td>{@code (Number)data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_BOOLEAN TYPE_BOOLEAN}</td>
	 * <td>{@code booleanData() ? Integer.valueOf(1) : Integer.valueOf(0)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_FUNCTION TYPE_FUNCTION}</td>
	 * <td>{@code Double.valueOf(NaN)}</td>
	 * </tr>
	 * </table>
	 * 
	 * @return Datensatz als {@link Number Zahlenwert}.
	 */
	public Number numberData();

	/**
	 * Diese Methode gibt den Datensatz als {@link Boolean Wahrheitswert} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>{@link Value#type() Datentyp}</th>
	 * <th>{@link Number Zahlenwert}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_VOID TYPE_VOID}</td>
	 * <td>{@code Boolean.valueOf(false)}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_ARRAY TYPE_ARRAY}</td>
	 * <td>{@code (arrayData().length != 0) ? Boolean.valueOf(true) : Boolean.valueOf(false)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_OBJECT TYPE_OBJECT}</td>
	 * <td>{@code Boolean.valueOf(true)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_STRING TYPE_STRING}</td>
	 * <td>{@code (stringData().length != 0) ? Boolean.valueOf(true) : Boolean.valueOf(false)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_NUMBER TYPE_NUMBER}</td>
	 * <td>{@code (numberData().intValue() != 0) ? Boolean.valueOf(true) : Boolean.valueOf(false)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_BOOLEAN TYPE_BOOLEAN}</td>
	 * <td>{@code (Boolean)data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_FUNCTION TYPE_FUNCTION}</td>
	 * <td>{@code Boolean.valueOf(true)}</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @return Datensatz als {@link Boolean Wahrheitswert}.
	 */
	public Boolean booleanData();

	/**
	 * Diese Methode gibt den Datensatz als {@link Function Funktion} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>{@link Value#type() Datentyp}</th>
	 * <th>{@link Number Zahlenwert}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_VOID TYPE_VOID}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_ARRAY TYPE_ARRAY}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_OBJECT TYPE_OBJECT}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_STRING TYPE_STRING}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_NUMBER TYPE_NUMBER}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_BOOLEAN TYPE_BOOLEAN}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * </tr>
	 * <tr>
	 * <td>{@link Value#TYPE_FUNCTION TYPE_FUNCTION}</td>
	 * <td>{@code (Function)data()}</td>
	 * </tr>
	 * </table>
	 * 
	 * @return Datensatz als {@link Function Funktion}.
	 */
	public Function functionData();

}
