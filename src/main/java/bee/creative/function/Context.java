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
	 * Dieses Feld speichert den leeren {@link Context}, dessen {@link #cast(Value, Type)}-Methode die Nutzdaten des ihr übergebenen Werts {@code value}
	 * unverändert zurück gibt, wenn sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} ist, d.h. wenn
	 * {@code value.type().is(type)}, und welche sonst eine {@link IllegalArgumentException} auslöst.
	 */
	public static final Context EMPTY = new Context() {

		@Override
		@SuppressWarnings ("unchecked")
		public <GData> GData cast(final Value value, final Type<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if (value.type().is(type)) return (GData)value.data();
			throw new IllegalArgumentException();
		}

	};

	/**
	 * Dieses Feld speichert den {@code default}-{@link Context}, der in den Methoden {@link Value#dataTo(Type)} und {@link Type#dataOf(Value)} zur kontextfreien
	 * Umwandlung von Werten verwendet wird. Wenn dieser {@code null} ist, lösen diese Methoden eine {@link NullPointerException} aus.
	 */
	public static Context DEFAULT = Context.EMPTY;

	{}

	/**
	 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) konvertierten {@link Value#data() Nutzdaten} des gegebenen Werts zurück.<br>
	 * Hierbei werden die Nitzdaten {@link Value#data() value.data()} in den geforderten Datentyp konvertiert.
	 * 
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten des gegebenen Werts konvertiert werden.
	 * @param value gegebener Wert.
	 * @param type gegebener Datentyp.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können.
	 */
	public abstract <GData> GData cast(Value value, Type<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
