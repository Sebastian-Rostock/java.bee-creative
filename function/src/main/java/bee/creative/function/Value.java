package bee.creative.function;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer {@link Function Funktion} oder als Parameter in einem {@link Scope Ausführungskontext} verwendet werden kann. Ein solcher {@link Value Wert} besitzt dazu einen {@link Value#type() Datentyp}, einen {@link Value#data() Datensatz} sowie Methoden zur Konvertierung des {@link Value#data() Datensatzes} in andere {@link Value#type() Datentypen}.
 * 
 * @see Scope
 * @see Values
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Value {

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@code null}.
	 * 
	 * @see Value#type()
	 */
	public static final int TYPE_VOID = 0;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Value}-Arrays.
	 * 
	 * @see Value#type()
	 */
	public static final int TYPE_ARRAY = 1;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für beliebige {@link Object Objekte}.
	 * 
	 * @see Object
	 * @see Value#type()
	 */
	public static final int TYPE_OBJECT = 2;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link String Zeichenketten}.
	 * 
	 * @see String
	 * @see Value#type()
	 */
	public static final int TYPE_STRING = 3;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Number Zahlenwerte}.
	 * 
	 * @see Number
	 * @see Value#type()
	 */
	public static final int TYPE_NUMBER = 4;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Boolean Wahrheitswerte}.
	 * 
	 * @see Boolean
	 * @see Value#type()
	 */
	public static final int TYPE_BOOLEAN = 5;

	/**
	 * Dieses Feld speichert den {@link Value#type() Datentyp} für {@link Function Funktionen}.
	 * 
	 * @see Function
	 * @see Value#type()
	 */
	public static final int TYPE_FUNCTION = 6;

	/**
	 * Diese Methode gibt den Datentyp zurück.
	 * 
	 * @see #TYPE_VOID
	 * @see #TYPE_ARRAY
	 * @see #TYPE_OBJECT
	 * @see #TYPE_STRING
	 * @see #TYPE_NUMBER
	 * @see #TYPE_FUNCTION
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
	 * Diese Methode gibt den Datensatz als {@link Value}-Array zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>Datentyp</th>
	 * <th>Rückgabewert</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_VOID}</td>
	 * <td>{@code new Value[0]}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_ARRAY}</td>
	 * <td>{@code (Value[])data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_OBJECT}</td>
	 * <td><code>new Value[]{this}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_STRING}</td>
	 * <td><code>new Value[]{this}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_NUMBER}</td>
	 * <td><code>new Value[]{this}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_BOOLEAN}</td>
	 * <td><code>new Value[]{this}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_FUNCTION}</td>
	 * <td><code>new Value[]{this}</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @see Value#type()
	 * @see Value#data()
	 * @return Datensatz als {@link Value}-Array.
	 */
	public Value[] arrayData();

	/**
	 * Diese Methode gibt den Datensatz als {@link String Zeichenkette} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>Datentyp</th>
	 * <th>Rückgabewert</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_VOID}</td>
	 * <td>{@code ""}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_ARRAY}</td>
	 * <td>{@code (arrayData().length != 0) ? arrayData()[0].stringData() : ""}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_OBJECT}</td>
	 * <td>{@code data().toString()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_STRING}</td>
	 * <td>{@code (String)data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_NUMBER}</td>
	 * <td>{@code data().toString()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_BOOLEAN}</td>
	 * <td>{@code data().toString()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_FUNCTION}</td>
	 * <td>{@code data().toString()}</td>
	 * </tr>
	 * </table>
	 * 
	 * @see Value#type()
	 * @see Value#data()
	 * @return Datensatz als {@link String Zeichenkette}.
	 */
	public String stringData();

	/**
	 * Diese Methode gibt den Datensatz als {@link Number Zahlenwert} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>Datentyp</th>
	 * <th>Rückgabewert</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_VOID}</td>
	 * <td>{@code Double.valueOf(NaN)}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_ARRAY}</td>
	 * <td>{@code (arrayData().length != 0) ? arrayData()[0].numberData() : Double.valueOf(NaN)}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_OBJECT}</td>
	 * <td>{@code Double.valueOf(stringData())} bzw. {@code Double.valueOf(NaN)} bei einer {@link NumberFormatException NumberFormatException}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_STRING}</td>
	 * <td>{@code Double.valueOf(stringData())} bzw. {@code Double.valueOf(NaN)} bei einer {@link NumberFormatException NumberFormatException}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_NUMBER}</td>
	 * <td>{@code (Number)data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_BOOLEAN}</td>
	 * <td>{@code booleanData() ? Integer.valueOf(1) : Integer.valueOf(0)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_FUNCTION}</td>
	 * <td>{@code Double.valueOf(NaN)}</td>
	 * </tr>
	 * </table>
	 * 
	 * @see Value#type()
	 * @see Value#data()
	 * @return Datensatz als {@link Number Zahlenwert}.
	 */
	public Number numberData();

	/**
	 * Diese Methode gibt den Datensatz als {@link Boolean Wahrheitswert} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>Datentyp</th>
	 * <th>{@link Boolean}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_VOID}</td>
	 * <td>{@code Boolean.valueOf(false)}</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_ARRAY}</td>
	 * <td>{@code Boolean.valueOf(arrayData().length != 0)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_OBJECT}</td>
	 * <td>{@code Boolean.valueOf(true)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_STRING}</td>
	 * <td>{@code Boolean.valueOf(stringData().length != 0)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_NUMBER}</td>
	 * <td>{@code Boolean.valueOf(numberData().intValue() != 0)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_BOOLEAN}</td>
	 * <td>{@code (Boolean)data()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_FUNCTION}</td>
	 * <td>{@code Boolean.valueOf(true)}</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @see Value#type()
	 * @see Value#data()
	 * @return Datensatz als {@link Boolean Wahrheitswert}.
	 */
	public Boolean booleanData();

	/**
	 * Diese Methode gibt den Datensatz als {@link Function Funktion} zurück.
	 * <p>
	 * <table style="width:auto;">
	 * <tr>
	 * <th>Datentyp</th>
	 * <th>Rückgabewert</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_VOID}</td>
	 * <td>{@code Functions.voidFunction()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_ARRAY}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_OBJECT}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_STRING}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_NUMBER}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_BOOLEAN}</td>
	 * <td>{@code Functions.valueFunction(this)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #TYPE_FUNCTION}</td>
	 * <td>{@code (Function)data()}</td>
	 * </tr>
	 * </table>
	 * 
	 * @see Value#type()
	 * @see Value#data()
	 * @see Functions#voidFunction()
	 * @see Functions#valueFunction(Value)
	 * @return Datensatz als {@link Function Funktion}.
	 */
	public Function functionData();

}
