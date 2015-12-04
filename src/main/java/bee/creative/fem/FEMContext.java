package bee.creative.fem;

import java.lang.reflect.Array;
import java.util.Collection;
import bee.creative.fem.FEM.BaseValue;

/**
 * Diese Klasse implementiert ein abstraktes Kontextobjekt, das von einem Rahmendaten zur Auswertung von Funktionen bereitgestellt wird und in Funktionen
 * zur Umwandlung von Werten genutzt werden kann.
 * 
 * @see FEMFrame#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMContext {

	static final class EmptyContext extends FEMContext {

		@Override
		@SuppressWarnings ("unchecked")
		public <GData> GData dataOf(final FEMValue value, final FEMType<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if (value.type().is(type)) return (GData)value.data();
			throw new IllegalArgumentException();
		}

		@Override
		public FEMValue valueOf(final Object data) throws IllegalArgumentException {
			if (data == null) return FEM.NULL;
			if (data instanceof FEMValue) return (FEMValue)data;
			if (data instanceof String) return FEM.stringValue((String)data);
			if (data instanceof Number) return FEM.numberValue((Number)data);
			if (data instanceof Boolean) return FEM.booleanValue((Boolean)data);
			if (data instanceof FEMArray) return FEM.arrayValue((FEMArray)data);
			if (data instanceof Collection<?>) return FEM.arrayValue((Collection<?>)data);
			if (data instanceof Iterable<?>) return FEM.arrayValue((Iterable<?>)data);
			if (data instanceof FEMFunction) return FEM.functionValue((FEMFunction)data);
			if (data.getClass().isArray()) return FEM.arrayValue(FEM.arrayFrom(data));
			return FEM.objectValue(data);
		}
	}

	{}

	/**
	 * Dieses Feld speichert den leeren {@link FEMContext}.
	 * <p>
	 * Die {@link #dataOf(FEMValue, FEMType)}-Methode dieses Kontextobjekts gibt die Nutzdaten des ihr übergebenen Werts {@code value} unverändert zurück gibt,
	 * wenn sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} ist, d.h. wenn {@code value.type().is(type)}. Andernfalls löst
	 * sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #valueOf(Object)}-Methode dieses Kontextobjekts liefert {@link FEM#NULL}, wenn die ihr übergebenen Nutzdaten {@code null} sind. Sie liefer
	 * die Nutzdaten unverändert, wenn diese selbst ein {@link FEMValue} sind. Wenn die Nutzdaten ein {@link Class#isArray() natives Array sind}, wird der Wert
	 * über {@link FEM#arrayFrom(Object)} und {@link FEM#arrayValue(FEMArray)} ermittelt. Andernfalls liefert sie abhängig vom Datentyp der Nutzdaten einen
	 * mit {@link FEM#stringValue(String)}, {@link FEM#numberValue(Number)}, {@link FEM#booleanValue(Boolean)}, {@link FEM#arrayValue(FEMArray)},
	 * {@link FEM#arrayValue(Collection)}, {@link FEM#arrayValue(Iterable)}, {@link FEM#functionValue(FEMFunction)} oder
	 * {@link FEM#objectValue(Object)} erzeugten wert.
	 */
	public static final FEMContext EMPTY = new EmptyContext();

	static FEMContext __default = FEMContext.EMPTY;

	{}

	/**
	 * Dieses Feld speichert den {@code default}-{@link FEMContext}, der in den Methoden {@link FEMScope#context()} von {@link FEMScope#EMPTY},
	 * {@link Values#valueOf(Object)}, {@link BaseValue#data(FEMType)} und {@link FEMType#dataOf(FEMValue)} zur kontextfreien Umwandlung der Nutzdaten von Werten
	 * verwendet wird. Wenn dieser {@code null} ist, lösen diese Methoden eine {@link NullPointerException} aus.
	 */
	/**
	 * Diese Methode gibt das  zurück.
	 * @return
	 */
	public static FEMContext DEFAULT() {
		return __default;
	}

	/**
	 * Diese Methode gibt das  zurück.
	 * @param context
	 */
	public static void DEFAULT(FEMContext context) {
		__default = context != null ? context : EMPTY;
	}

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
