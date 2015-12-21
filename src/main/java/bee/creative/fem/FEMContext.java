package bee.creative.fem;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import bee.creative.fem.FEM.BaseValue;
import bee.creative.util.Converter;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert ein abstraktes Kontextobjekt, das über einen {@link FEMFrame Stapelrahmen} der Auswertung von Funktionen bereitgestellt wird und
 * in Funktionen zur Umwandlung von Werten genutzt werden kann.
 * 
 * @see FEMFrame#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMContext {

	@SuppressWarnings ("javadoc")
	static final class EmptyContext extends FEMContext {

		@Override
		@SuppressWarnings ("unchecked")
		public <GData> GData dataOf(final FEMValue value, final FEMType<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if (value.type().is(type)) return (GData)value.data();
			throw new IllegalArgumentException();
		}

		@Override
		public FEMValue valueOf(final Object data) throws IllegalArgumentException {
			if (data == null) return FEM.voidValue();
			if (data instanceof FEMValue) return (FEMValue)data;
			if (data instanceof FEMVoid) return FEM.voidValue();
			if (data instanceof char[]) return FEM.stringValue((char[])data);
			if (data instanceof String) return FEM.stringValue((String)data);
			if (data instanceof FEMString) return FEM.stringValue((FEMString)data);
			if (data instanceof byte[]) return FEM.binaryValue((byte[])data);
			if (data instanceof FEMBinary) return FEM.binaryValue((FEMBinary)data);
			if (data instanceof Float) return FEM.decimalValue((Number)data);
			if (data instanceof Double) return FEM.decimalValue((Number)data);
			if (data instanceof BigDecimal) return FEM.decimalValue((Number)data);
			if (data instanceof Number) return FEM.integerValue((Number)data);
			if (data instanceof Boolean) return FEM.booleanValue((Boolean)data);
			if (data instanceof FEMBoolean) return FEM.booleanValue((FEMBoolean)data);
			if (data instanceof Calendar) return FEM.datetimeValue((Calendar)data);
			if (data instanceof FEMDatetime) return FEM.datetimeValue((FEMDatetime)data);
			if (data instanceof FEMDuration) return FEM.durationValue((FEMDuration)data);
			if (data instanceof FEMFunction) return FEM.functionValue((FEMFunction)data);
			return FEM.arrayValue(this.arrayOf(data));
		}

	}

	{}

	/**
	 * Dieses Feld speichert das leere Kontextobjekt.
	 * <p>
	 * Die {@link #dataOf(FEMValue, FEMType)}-Methode dieses Kontextobjekts gibt die Nutzdaten des ihr übergebenen Werts {@code value} unverändert zurück, wenn
	 * sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} {@link FEMType#id() ist}, d.h. wenn {@code value.type().is(type)}.
	 * Andernfalls löst sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #valueOf(Object)}-Methode dieses Kontextobjekts konvertiert die ihr übergebenen Nutzdaten über die {@link FEMValue}-liefernden Methoden der
	 * Klasse {@link FEM} sowie falls möglich über {@link #arrayOf(Object)}. Im Fehlerfall löst sie eine {@link IllegalArgumentException} aus.
	 */
	public static final FEMContext EMPTY = new EmptyContext();

	/**
	 * Dieses Feld speichert das Rückfallkontextobjekt.
	 */
	static FEMContext __default = FEMContext.EMPTY;

	{}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe {@code input} via {@code type.dataOf(input)} in siene Ausgabe überführt.
	 * 
	 * @param <GData> Typ der Nutzdaten des gegebenen Datentyps sowie der Ausgebe des erzeugten {@link Converter}.
	 * @param type Datentyp.
	 * @return {@code dataOf}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code type} {@code null} ist.
	 */
	public static final <GData> Converter<FEMValue, GData> dataOf(final FEMType<? extends GData> type) throws NullPointerException {
		if (type == null) throw new NullPointerException("type = null");
		return new Converter<FEMValue, GData>() {
	
			@Override
			public GData convert(final FEMValue input) {
				return FEMContext.__default.dataOf(input, type);
			}
	
			@Override
			public String toString() {
				return Objects.toInvokeString("dataOf", type);
			}
	
		};
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe {@code input} via {@code type.dataOf(input, context)} in siene Ausgabe überführt.
	 * 
	 * @param <GData> Typ der Nutzdaten des gegebenen Datentyps sowie der Ausgebe des erzeugten {@link Converter}.
	 * @param type Datentyp.
	 * @param context Kontextobjekt.
	 * @return {@code dataOf}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
	 */
	public static final <GData> Converter<FEMValue, GData> dataOf(final FEMType<? extends GData> type, final FEMContext context) throws NullPointerException {
		if (type == null) throw new NullPointerException("type = null");
		if (context == null) throw new NullPointerException("context = null");
		return new Converter<FEMValue, GData>() {
	
			@Override
			public GData convert(final FEMValue input) {
				return context.dataOf(input, type);
			}
	
			@Override
			public String toString() {
				return Objects.toInvokeString("dataOf", type, context);
			}
	
		};
	}

	/**
	 * Diese Methode gibt das Kontextobjekt zurück, das als Rückfallebene für kontextfeie {@link FEMType#dataOf(FEMValue) Datentypumwandlungen} genutzt wird.<br>
	 * Dieses Rückfallkontextobjekt wird in den Methoden {@link FEM#valueOf(Object)}, {@link BaseValue#data(FEMType)} und {@link FEMType#dataOf(FEMValue)}
	 * verwendet.
	 * 
	 * @return Rückfallkontextobjekt
	 */
	public static final FEMContext DEFAULT() {
		return FEMContext.__default;
	}

	/**
	 * Diese Methode setzt den {@link #DEFAULT() Rückfallkontextobjekt}.<br>
	 * Wenn das gegebene Kontextobjekt {@code null} ist
	 * 
	 * @param context Rückfallkontextobjekt oder {@code null}.
	 */
	public static final void DEFAULT(final FEMContext context) {
		FEMContext.__default = context != null ? context : FEMContext.EMPTY;
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
	 * Diese Methode konvertiert das gegebene Objekt in eine Wertliste und gibt diese zurück.<br>
	 * <ol>
	 * <li>Wenn das Objekt ein {@link FEMArray} ist, wird es unverändert zurück gegeben.</li>
	 * <li>Wenn es ein natives Array ist, wird jedes seiner Elemente via {@link #valueOf(Object)} in einen Wert überführt und die so entstandene Wertliste
	 * geliefert.</li>
	 * <li>Wenn es eine {@link Collection} ist, wird diese in ein natives Array überführt, welches anschließend in eine Wertliste umgewandelt wird.</li>
	 * <li>Wenn es ein {@link Iterable} ist, wird dieses in eine {@link Collection} überführt, welche anschließend in eine Wertliste umgewandelt wird.</li>
	 * <li>Andernfalls wird eine Ausnahme ausgelöst.</li> </ul>
	 * 
	 * @see Array#get(Object, int)
	 * @see Array#getLength(Object)
	 * @see Collection#toArray()
	 * @param data Wertliste, natives Array, {@link Iterable} oder {@link Collection}.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt bzw. eines der Elemente nicht umgewandelt werden kann.
	 */
	public final FEMArray arrayOf(final Object data) throws NullPointerException, IllegalArgumentException {
		if (data instanceof FEMArray) return (FEMArray)data;
		if (data instanceof Object[]) return this.__arrayOf((Object[])data);
		if (data instanceof Collection<?>) return this.__arrayOf((Collection<?>)data);
		if (data instanceof Iterable<?>) return this.__arrayOf((Iterable<?>)data);
		final int length = Array.getLength(data);
		if (length == 0) return FEMArray.EMPTY;
		final FEMValue[] values = new FEMValue[length];
		for (int i = 0; i < length; i++) {
			values[i] = this.valueOf(Array.get(data, i));
		}
		return FEMArray.from(values);
	}

	@SuppressWarnings ("javadoc")
	final FEMArray __arrayOf(final Object[] data) throws NullPointerException, IllegalArgumentException {
		final int length = data.length;
		if (length == 0) return FEMArray.EMPTY;
		final FEMValue[] values = new FEMValue[length];
		for (int i = 0; i < length; i++) {
			values[i] = this.valueOf(data[i]);
		}
		return FEMArray.from(values);
	}

	@SuppressWarnings ("javadoc")
	final FEMArray __arrayOf(final Iterable<?> data) throws NullPointerException, IllegalArgumentException {
		final List<Object> array = new ArrayList<>();
		Iterables.appendAll(array, data);
		return this.__arrayOf(array);
	}

	@SuppressWarnings ("javadoc")
	final FEMArray __arrayOf(final Collection<?> data) throws NullPointerException, IllegalArgumentException {
		return this.__arrayOf(data.toArray());
	}

	/**
	 * Diese Methode gibt einen {@link FEMValue Wert} mit den gegebenen {@link FEMValue#data() Nutzdaten} zurück.<br>
	 * Welcher Wert- und Datentyp hierfür verwendet wird, ist der Implementation überlassen.
	 * 
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann.
	 */
	public abstract FEMValue valueOf(Object data) throws IllegalArgumentException;

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this);
	}

}
