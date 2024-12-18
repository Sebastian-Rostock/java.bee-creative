package bee.creative.fem;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.util.Getter3;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein abstraktes Kontextobjekt, das über einen {@link FEMFrame Stapelrahmen} der Auswertung von Funktionen bereitgestellt wird und
 * in Funktionen zur Umwandlung von Werten genutzt werden kann. Nachfahren sollten die Methoden {@link #dataFrom(FEMValue, FEMType)}, {@link #arrayFrom(Object)}
 * , {@link #valueFrom(Object)} und {@link #objectFrom(FEMValue)}.
 *
 * @see FEMFrame#context()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMContext extends BaseObject {

	/** Dieses Feld speichert das leere Kontextobjekt.
	 * <p>
	 * Die {@link #dataFrom(FEMValue, FEMType)}-Methode dieses Kontextobjekts gibt die Nutzdaten des ihr übergebenen Werts {@code value} unverändert zurück, wenn
	 * sein Datentyp gleich oder einem Nachfahren des ihr übergebenen Datentyps {@code type} {@link FEMType#id() ist}, d.h. wenn {@code value.type().is(type)}.
	 * Andernfalls löst sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #valueFrom(Object)}-Methode dieses Kontextobjekts gibt einen gegebenen {@link FEMValue} unverändert zurück und konvertiert {@code null} zu
	 * {@link FEMVoid}, {@code char[]} und {@link String} zu {@link FEMString}, {@code byte[]} zu {@link FEMBinary}, {@link Float}, {@link Double} und
	 * {@link BigDecimal} zu {@link FEMDecimal}, alle anderen {@link Number} zu {@link FEMInteger}, {@link Boolean} zu {@link FEMBoolean}, {@link Calendar} zu
	 * {@link FEMDatetime}, {@link FEMFunction} zu {@link FEMHandler} und alle anderen Eingaben über {@link #arrayFrom(Object)} in ein {@link FEMArray}. Im
	 * Fehlerfall löst sie eine {@link IllegalArgumentException} aus.
	 * <p>
	 * Die {@link #objectFrom(FEMValue)}-Methode dieses Kontextobjekts konvertiert {@link FEMVoid} zu {@code null}, {@link FEMArray} und die darin enthaltenen
	 * Werte rekursiv zu {@code Object[]}, {@link FEMBinary} zu {@code byte[]}, {@link FEMString} zu {@link String}, {@link FEMInteger} und {@link FEMDecimal} zu
	 * {@link Number}, {@link FEMDatetime} zu {@link Calendar}, {@link FEMBoolean} zu {@link Boolean} und alle anderen Werte ihren {@link FEMValue#data()
	 * Nutzdatensatz}. */
	public static final FEMContext EMPTY = new FEMContext();

	/** Diese Methode gibt einen {@link Getter3} zurück, der seine Eingabe {@code input} über {@link #dataFrom(FEMValue, FEMType) dataFrom(input, type)} in seine
	 * Ausgabe überführt.
	 *
	 * @param <GData> Typ der Nutzdaten des gegebenen Datentyps sowie der Ausgebe des erzeugten {@link Getter3}.
	 * @param type Datentyp.
	 * @return {@code dataFrom}-{@link Getter3}.
	 * @throws NullPointerException Wenn {@code type} {@code null} ist. */
	public <GData> Getter3<FEMValue, GData> dataFrom(FEMType<? extends GData> type) throws NullPointerException {
		Objects.notNull(type);
		return value -> this.dataFrom(value, type);
	}

	/** Diese Methode gibt die in {@link FEMValue#data() Nutzdaten} des gegebenen Werts im gegebenen Datentyp ({@code GData}) zurück. Hierbei werden die Nutzdaten
	 * {@link FEMValue#data() value.data()} in den geforderten Datentyp konvertiert.
	 *
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten des gegebenen Werts konvertiert werden.
	 * @param value gegebener Wert.
	 * @param type gegebener Datentyp.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können. */
	public <GData> GData dataFrom(FEMValue value, FEMType<GData> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
		if (!value.type().is(type)) throw new IllegalArgumentException();
		@SuppressWarnings ("unchecked")
		var result = (GData)value.data();
		return result;
	}

	/** Diese Methode gibt einen {@link Getter3} zurück, der seine Eingabe {@code input} über {@link #arrayFrom(Object) valueFrom(input)} in seine Ausgabe
	 * überführt.
	 *
	 * @return {@code arrayFrom}-{@link Getter3}. */
	public Getter3<Object, FEMArray> arrayFrom() {
		return this::arrayFrom;
	}

	/** Diese Methode konvertiert das gegebene Objekt in eine Wertliste und gibt diese zurück.
	 * <ol>
	 * <li>Wenn das Objekt ein {@link FEMArray} ist, wird es unverändert zurück gegeben.</li>
	 * <li>Wenn es ein natives Array ist, wird jedes seiner Elemente über {@link #valueFrom(Object)} in einen Wert überführt und die so entstandene Wertliste
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
	public FEMArray arrayFrom(Object data) throws NullPointerException, IllegalArgumentException {
		if (data instanceof FEMArray) return (FEMArray)data;
		if (data instanceof Iterable<?>) return this.arrayFromImpl(Iterables.toArray((Iterable<?>)data));
		return this.arrayFromImpl(data);
	}

	/** Diese Methode gibt einen {@link Getter3} zurück, der seine Eingabe {@code input} über {@link #valueFrom(Object) valueFrom(input)} in seine Ausgabe
	 * überführt.
	 *
	 * @return {@code valueFrom}-{@link Getter3}. */
	public Getter3<Object, FEMValue> valueFrom() {
		return this::valueFrom;
	}

	/** Diese Methode gibt einen {@link FEMValue Wert} mit den gegebenen {@link FEMValue#data() Nutzdaten} zurück. Welcher Wert- und Datentyp hierfür verwendet
	 * wird, ist der Implementation überlassen.
	 *
	 * @param object Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann. */
	public FEMValue valueFrom(Object object) throws IllegalArgumentException {
		if (object == null) return FEMVoid.INSTANCE;
		if (object instanceof FEMValue) return (FEMValue)object;
		if (object instanceof FEMFunction) return ((FEMFunction)object).toValue();
		if (object instanceof char[]) return FEMString.from((char[])object);
		if (object instanceof String) return FEMString.from((String)object);
		if (object instanceof byte[]) return FEMBinary.from((byte[])object);
		if ((object instanceof Float) || (object instanceof Double) || (object instanceof BigDecimal)) return FEMDecimal.from((Number)object);
		if (object instanceof Number) return FEMInteger.from((Number)object);
		if (object instanceof Boolean) return FEMBoolean.from((Boolean)object);
		if (object instanceof Calendar) return FEMDatetime.from((Calendar)object);
		return this.arrayFrom(object);
	}

	/** Diese Methode gibt einen {@link Getter3} zurück, der seine Eingabe {@code input} über {@link #objectFrom(FEMValue) objectFrom(input)} in seine Ausgabe
	 * überführt.
	 *
	 * @return {@code objectFrom}-{@link Getter3}. */
	public Getter3<FEMValue, Object> objectFrom() {
		return this::objectFrom;
	}

	/** Diese Methode gibt ein {@link Object} zurück, welches über {@link #valueFrom(Object)} in einen Wert überführt werden kann, der zum gegebenen Wert
	 * äquivalenten ist.
	 *
	 * @see #valueFrom(Object)
	 * @param value Wert.
	 * @return Objekt
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist. */
	public Object objectFrom(FEMValue value) throws NullPointerException, IllegalArgumentException {
		switch (value.type().id()) {
			case FEMVoid.ID:
				return null;
			case FEMArray.ID:
				return this.objectFromImpl((FEMArray)value.data());
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

	private FEMArray arrayFromImpl(Object data) throws NullPointerException, IllegalArgumentException {
		var length = Array.getLength(data);
		if (length == 0) return FEMArray.EMPTY;
		var values = new FEMValue[length];
		for (var i = 0; i < length; i++) {
			values[i] = this.valueFrom(Array.get(data, i));
		}
		return FEMArray.from(values);
	}

	private Object[] objectFromImpl(FEMArray array) {
		var length = array.length();
		var result = new Object[length];
		for (var i = 0; i < length; i++) {
			result[i] = this.objectFrom(array.get(i));
		}
		return result;
	}

}
