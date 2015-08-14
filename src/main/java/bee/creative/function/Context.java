package bee.creative.function;

/**
 * Diese Klasse implementiert ein abstraktes Kontextobjekt, das von einem Ausführungskontext zur Auswertung von Funktionen bereitgestellt wird und in Funktionen
 * zur Umwandlung von Werten genutzt werden kann.
 * 
 * @see Scope#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class Context {

	/**
	 * Dieses Feld speichert den leeren {@link Context}, dessen {@link #cast(Value, Type)}-Methode den ihr übergebenen Wert {@code value} unverändert zurück gibt,
	 * wenn sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} ist, d.h. wenn {@code value.type().is(type)}, und welche sonst
	 * eine {@link IllegalArgumentException} auslöst.
	 */
	public static final Context EMPTY = new Context() {

		@Override
		@SuppressWarnings ("unchecked")
		public <GValue> GValue cast(final Value value, final Type<GValue> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if (value.type().is(type)) return (GValue)value;
			throw new IllegalArgumentException();
		}

	};

	/**
	 * Dieses Feld speichert den {@code default}-{@link Context}, der in den Methoden {@link Value#valueTo(Type)} und {@link Type#valueOf(Value)} zur
	 * kontextfreien Umwandlung von Werten verwendet wird. Wenn dieser {@code null} ist, lösen diese Methoden eine {@link NullPointerException} aus.
	 */
	public static Context DEFAULT = Context.EMPTY;

	{}

	/**
	 * Diese Methode konvertiert den gegebenen Wert in einen Wert des gegebenen Datentyps und gibt ihn zurück.
	 * 
	 * @see Type#valueOf(Value, Context)
	 * @see Value#valueTo(Type, Context)
	 * @param <GValue> Typ des Rückgabewerts.
	 * @param value gegebener Wert.
	 * @param type gegebener Datentyp.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
	 */
	public abstract <GValue> GValue cast(Value value, Type<GValue> type) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
