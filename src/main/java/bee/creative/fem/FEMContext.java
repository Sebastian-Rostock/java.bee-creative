package bee.creative.fem;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import bee.creative.util.Converter;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein abstraktes Kontextobjekt, das über einen {@link FEMFrame Stapelrahmen} der Auswertung von Funktionen bereitgestellt wird und
 * in Funktionen zur Umwandlung von Werten genutzt werden kann. Nachfahren sollten die Methoden {@link #dataFrom(FEMValue, FEMType)}, {@link #arrayFrom(Object)}
 * , {@link #valueFrom(Object)} und {@link #objectFrom(FEMValue)}.
 * 
 * @see FEMFrame#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMContext {

	/** Dieses Feld speichert das leere Kontextobjekt.
	 * <p>
	 * Die {@link #dataFrom(FEMValue, FEMType)}-Methode dieses Kontextobjekts gibt die Nutzdaten des ihr übergebenen Werts {@code value} unverändert zurück, wenn
	 * sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} {@link FEMType#id() ist}, d.h. wenn {@code value.type().is(type)}.
	 * Andernfalls löst sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #valueFrom(Object)}-Methode dieses Kontextobjekts gibt einen gegebenen {@link FEMValue} unverändert zurück und konvertiert {@code null} zu
	 * {@link FEMVoid}, {@code char[]} und {@link String} zu {@link FEMString}, {@code byte[]} zu {@link FEMBinary}, {@link Float}, {@link Double} und
	 * {@link BigDecimal} zu {@link FEMDecimal}, alle anderen {@link Number} zu {@link FEMInteger}, {@link Boolean} zu {@link FEMBoolean}, {@link Calendar} zu
	 * {@link FEMDatetime}, {@link FEMFunction} zu {@link FEMHandler} und alle anderen Eingaben via {@link #arrayFrom(Object)} in ein {@link FEMArray}. Im
	 * Fehlerfall löst sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #objectFrom(FEMValue)}-Methode dieses Kontextobjekts konvertiert {@link FEMVoid} zu {@code null}, {@link FEMArray} und die darin enthaltenen
	 * Werte rekursiv zu {@code Object[]}, {@link FEMBinary} zu {@code byte[]}, {@link FEMString} zu {@link String}, {@link FEMInteger} und {@link FEMDecimal} zu
	 * {@link Number}, {@link FEMDatetime} zu {@link Calendar}, {@link FEMBoolean} zu {@link Boolean} und alle anderen Werte ihren {@link FEMValue#data()
	 * Nutzdatensatz}. */
	public static final FEMContext EMPTY = new FEMContext();

	/** Dieses Feld speichert das Rückfallkontextobjekt. */
	static FEMContext _default_ = FEMContext.EMPTY;

	{}

	/** Diese Methode gibt das Kontextobjekt zurück, das als Rückfallebene für kontextfeie {@link FEMType#dataFrom(FEMValue) Datentypumwandlungen} genutzt wird.<br>
	 * Dieses Rückfallkontextobjekt wird in den Methoden {@link FEM#valueFrom(Object)}, {@link FEMBaseValue#data(FEMType)} und {@link FEMType#dataFrom(FEMValue)}
	 * verwendet.
	 * 
	 * @return Rückfallkontextobjekt */
	public static FEMContext DEFAULT() {
		return FEMContext._default_;
	}

	/** Diese Methode setzt den {@link #DEFAULT() Rückfallkontextobjekt}.<br>
	 * Wenn das gegebene Kontextobjekt {@code null} ist, wird {@link #EMPTY} verwendet.
	 * 
	 * @param context Rückfallkontextobjekt oder {@code null}. */
	public static void DEFAULT(final FEMContext context) {
		FEMContext._default_ = context != null ? context : FEMContext.EMPTY;
	}

	{}

	/** Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe {@code input} via {@link #dataFrom(FEMValue, FEMType) dataFrom(input, type)} in seine
	 * Ausgabe überführt.
	 * 
	 * @param <GData> Typ der Nutzdaten des gegebenen Datentyps sowie der Ausgebe des erzeugten {@link Converter}.
	 * @param type Datentyp.
	 * @return {@code dataFrom}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code type} {@code null} ist. */
	public final <GData> Converter<FEMValue, GData> dataFrom(final FEMType<? extends GData> type) throws NullPointerException {
		if (type == null) throw new NullPointerException("type = null");
		return new Converter<FEMValue, GData>() {

			@Override
			public GData convert(final FEMValue value) {
				return FEMContext.this.dataFrom(value, type);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("dataFrom", type);
			}

		};
	}

	/** Diese Methode gibt die in {@link FEMValue#data() Nutzdaten} des gegebenen Werts im gegebenen Datentyp ({@code GData}) zurück.<br>
	 * Hierbei werden die Nutzdaten {@link FEMValue#data() value.data()} in den geforderten Datentyp konvertiert.
	 * 
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten des gegebenen Werts konvertiert werden.
	 * @param value gegebener Wert.
	 * @param type gegebener Datentyp.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können. */
	public <GData> GData dataFrom(final FEMValue value, final FEMType<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
		if (!value.type().is(type)) throw new IllegalArgumentException();
		@SuppressWarnings ("unchecked")
		final GData result = (GData)value.data();
		return result;
	}

	/** Diese Methode konvertiert das gegebene Objekt in eine Wertliste und gibt diese zurück.<br>
	 * <ol>
	 * <li>Wenn das Objekt ein {@link FEMArray} ist, wird es unverändert zurück gegeben.</li>
	 * <li>Wenn es ein natives Array ist, wird jedes seiner Elemente via {@link #valueFrom(Object)} in einen Wert überführt und die so entstandene Wertliste
	 * geliefert.</li>
	 * <li>Wenn es eine {@link Collection} ist, wird diese in ein natives Array überführt, welches anschließend in eine Wertliste umgewandelt wird.</li>
	 * <li>Wenn es ein {@link Iterable} ist, wird dieses in eine {@link Collection} überführt, welche anschließend in eine Wertliste umgewandelt wird.</li>
	 * <li>Andernfalls wird eine Ausnahme ausgelöst.</li>
	 * </ol>
	 * 
	 * @see Array#get(Object, int)
	 * @see Array#getLength(Object)
	 * @see Collection#toArray()
	 * @param data Wertliste, natives Array, {@link Iterable} oder {@link Collection}.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt bzw. eines der Elemente nicht umgewandelt werden kann. */
	public FEMArray arrayFrom(final Object data) throws NullPointerException, IllegalArgumentException {
		if (data instanceof FEMArray) return (FEMArray)data;
		if (data instanceof Object[]) return this._arrayFrom_((Object[])data);
		if (data instanceof Collection<?>) return this._arrayFrom_((Collection<?>)data);
		if (data instanceof Iterable<?>) return this._arrayFrom_((Iterable<?>)data);
		final int length = Array.getLength(data);
		if (length == 0) return FEMArray.EMPTY;
		final FEMValue[] values = new FEMValue[length];
		for (int i = 0; i < length; i++) {
			values[i] = this.valueFrom(Array.get(data, i));
		}
		return FEMArray.from(values);
	}

	@SuppressWarnings ("javadoc")
	final FEMArray _arrayFrom_(final Object[] data) throws NullPointerException, IllegalArgumentException {
		final int length = data.length;
		if (length == 0) return FEMArray.EMPTY;
		final FEMValue[] values = new FEMValue[length];
		for (int i = 0; i < length; i++) {
			values[i] = this.valueFrom(data[i]);
		}
		return FEMArray.from(values);
	}

	@SuppressWarnings ("javadoc")
	final FEMArray _arrayFrom_(final Iterable<?> data) throws NullPointerException, IllegalArgumentException {
		final List<Object> array = new ArrayList<>();
		Iterables.appendAll(array, data);
		return this._arrayFrom_(array);
	}

	@SuppressWarnings ("javadoc")
	final FEMArray _arrayFrom_(final Collection<?> data) throws NullPointerException, IllegalArgumentException {
		return this._arrayFrom_(data.toArray());
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe {@code input} via {@link #valueFrom(Object) valueFrom(input)} in seine Ausgabe
	 * überführt.
	 * 
	 * @return {@code valueFrom}-{@link Converter}. */
	public final Converter<Object, FEMValue> valueFrom() {
		return new Converter<Object, FEMValue>() {

			@Override
			public FEMValue convert(final Object object) {
				return FEMContext.this.valueFrom(object);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("valueFrom");
			}

		};
	}

	/** Diese Methode gibt einen {@link FEMValue Wert} mit den gegebenen {@link FEMValue#data() Nutzdaten} zurück.<br>
	 * Welcher Wert- und Datentyp hierfür verwendet wird, ist der Implementation überlassen.
	 * 
	 * @param object Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann. */
	public FEMValue valueFrom(final Object object) throws IllegalArgumentException {
		if (object == null) return FEMVoid.INSTANCE;
		if (object instanceof FEMValue) return (FEMValue)object;
		if (object instanceof char[]) return FEMString.from((char[])object);
		if (object instanceof String) return FEMString.from((String)object);
		if (object instanceof byte[]) return FEMBinary.from((byte[])object);
		if (object instanceof Float) return FEMDecimal.from((Number)object);
		if (object instanceof Double) return FEMDecimal.from((Number)object);
		if (object instanceof BigDecimal) return FEMDecimal.from((Number)object);
		if (object instanceof Number) return FEMInteger.from((Number)object);
		if (object instanceof Boolean) return FEMBoolean.from((Boolean)object);
		if (object instanceof Calendar) return FEMDatetime.from((Calendar)object);
		if (object instanceof FEMFunction) return FEMHandler.from((FEMFunction)object);
		return this.arrayFrom(object);
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe {@code input} via {@link #objectFrom(FEMValue) objectFrom(input)} in seine Ausgabe
	 * überführt.
	 * 
	 * @return {@code objectFrom}-{@link Converter}. */
	public final Converter<FEMValue, Object> objectFrom() {
		return new Converter<FEMValue, Object>() {

			@Override
			public Object convert(final FEMValue value) {
				return FEMContext.this.objectFrom(value);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("objectFrom");
			}

		};
	}

	/** Diese Methode gibt ein {@link Object} zurück, welches via {@link #valueFrom(Object)} in einen Wert überführt werden kann, der zum gegebenen Wert
	 * äquivalenten ist.
	 * 
	 * @see #valueFrom(Object)
	 * @param value Wert.
	 * @return Objekt
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist. */
	public Object objectFrom(final FEMValue value) throws NullPointerException, IllegalArgumentException {
		switch (value.type().id()) {
			case FEMVoid.ID:
				return null;
			case FEMArray.ID:
				return this._objectFrom_((FEMArray)value.data());
			case FEMBinary.ID:
				return ((FEMBinary)value.data()).value();
			case FEMString.ID:
				return ((FEMString)value.data()).toString();
			case FEMInteger.ID:
				return ((FEMInteger)value.data()).toNumber();
			case FEMDecimal.ID:
				return ((FEMDecimal)value.data()).toNumber();
			case FEMDatetime.ID:
				return ((FEMDatetime)value.data()).toCalendar();
			case FEMBoolean.ID:
				return ((FEMBoolean)value.data()).toBoolean();
		}
		return value.data();
	}

	@SuppressWarnings ("javadoc")
	final Object[] _objectFrom_(final FEMArray array) {
		final int length = array.length();
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.objectFrom(array.get(i));
		}
		return result;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this);
	}

}
