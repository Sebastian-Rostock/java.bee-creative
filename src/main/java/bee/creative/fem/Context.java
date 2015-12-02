package bee.creative.fem;

import java.util.Collection;
import bee.creative.fem.Values.BaseValue;

/**
 * Diese Klasse implementiert ein abstraktes Kontextobjekt, das von einem Ausführungskontext zur Auswertung von Funktionen bereitgestellt wird und in Funktionen
 * zur Umwandlung von Werten genutzt werden kann.
 * 
 * @see FEMScope#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class Context {

	/**
	 * Dieses Feld speichert den leeren {@link Context}.
	 * <p>
	 * Die {@link #dataOf(FEMValue, FEMType)}-Methode dieses Kontextobjekts gibt die Nutzdaten des ihr übergebenen Werts {@code value} unverändert zurück gibt,
	 * wenn sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} ist, d.h. wenn {@code value.type().is(type)}. Andernfalls löst
	 * sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #valueOf(Object)}-Methode dieses Kontextobjekts liefert {@link Values#NULL}, wenn die ihr übergebenen Nutzdaten {@code null} sind. Sie liefer
	 * die Nutzdaten unverändert, wenn diese selbst ein {@link FEMValue} sind. Wenn die Nutzdaten ein {@link Class#isArray() natives Array sind}, wird der Wert
	 * über {@link FEMArray#from(Object)} und {@link Values#arrayValue(FEMArray)} ermittelt. Andernfalls liefert sie abhängig vom Datentyp der Nutzdaten einen mit
	 * {@link Values#stringValue(String)}, {@link Values#numberValue(Number)}, {@link Values#booleanValue(Boolean)}, {@link Values#arrayValue(FEMArray)},
	 * {@link Values#arrayValue(Collection)}, {@link Values#arrayValue(Iterable)}, {@link Values#functionValue(FEMFunction)} oder
	 * {@link Values#objectValue(Object)} erzeugten wert.
	 */
	public static final Context EMPTY = new Context() {

		@Override
		@SuppressWarnings ("unchecked")
		public <GData> GData dataOf(final FEMValue value, final FEMType<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if (value.type().is(type)) return (GData)value.data();
			throw new IllegalArgumentException();
		}

		@Override
		public FEMValue valueOf(final Object data) throws IllegalArgumentException {
			if (data == null) return Values.NULL;
			if (data instanceof FEMValue) return (FEMValue)data;
			if (data instanceof String) return Values.stringValue((String)data);
			if (data instanceof Number) return Values.numberValue((Number)data);
			if (data instanceof Boolean) return Values.booleanValue((Boolean)data);
			if (data instanceof FEMArray) return Values.arrayValue((FEMArray)data);
			if (data instanceof Collection<?>) return Values.arrayValue((Collection<?>)data);
			if (data instanceof Iterable<?>) return Values.arrayValue((Iterable<?>)data);
			if (data instanceof FEMFunction) return Values.functionValue((FEMFunction)data);
			if (data.getClass().isArray()) return Values.arrayValue(FEMArray.from(data));
			return Values.objectValue(data);
		}

	};

	/**
	 * Dieses Feld speichert den {@code default}-{@link Context}, der in den Methoden {@link FEMScope#context()} von {@link FEMScope#EMPTY},
	 * {@link Values#valueOf(Object)}, {@link BaseValue#data(FEMType)} und {@link FEMType#dataOf(FEMValue)} zur kontextfreien Umwandlung der Nutzdaten von Werten
	 * verwendet wird. Wenn dieser {@code null} ist, lösen diese Methoden eine {@link NullPointerException} aus.
	 */
	public static Context DEFAULT = Context.EMPTY;

	{}

	/**
	 * Diese Methode gibt die in {@link FEMValue#data() Nutzdaten} des gegebenen Werts im gegebenen Datentyp ({@code GData}) zurück.<br>
	 * Hierbei werden die Nutzdaten {@link FEMValue#data() value.data()} in den geforderten Datentyp konvertiert.
	 * 
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten des gegebenen Werts konvertiert werden.
	 * @param value gegebener Wert.
	 * @param type gegebener Datentyp.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können.
	 */
	public abstract <GData> GData dataOf(FEMValue value, FEMType<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException;

	/**
	 * Diese Methode gibt einen {@link FEMValue Wert} mit den gegebenen {@link FEMValue#data() Nutzdaten} zurück.<br>
	 * Welcher Wert- und Datentyp hierfür verwendet wird, ist der Implementation überlassen.
	 * 
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann.
	 */
	public abstract FEMValue valueOf(Object data) throws IllegalArgumentException;

}
