package bee.creative.function;

/**
 * Diese Schnittstelle definiert das Kontextobjekt, das von einem Ausführungskontext zur Auswertung von Funktionen bereitgestellt wird und in Funktionen zur
 * Umwandlung von Werten genutzt werden kann.
 * 
 * @see Scope#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Context {

	/**
	 * Diese Methode konvertiert den gegebenen Wert in einen Wert des gegebenen Datentyps und gibt ihn zurück.
	 * 
	 * @see Type#valueOf(Value, Context)
	 * @see Value#valueTo(Type, Context)
	 * @param <GValue> Typ des Rückgabewerts.
	 * @param value gegebener Wert.
	 * @param type gegebener Datentyp.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
	 */
	public <GValue> GValue cast(Value value, Type<GValue> type) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
