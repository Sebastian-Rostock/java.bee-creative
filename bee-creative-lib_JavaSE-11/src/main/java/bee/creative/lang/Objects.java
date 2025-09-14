package bee.creative.lang;

import static bee.creative.lang.Natives.printClass;
import static bee.creative.lang.Natives.printConstructor;
import static bee.creative.lang.Natives.printField;
import static bee.creative.lang.Natives.printMethod;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Berechnung von {@link Object#hashCode() Streuwerten}, {@link Object#equals(Object)
 * Äquivalenzen} und {@link Object#toString() Textdarstelungen}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Objects {

	/** Diese Methode gibt das gegebene Objekt zurück, wenn dieses nicht {@code null} ist.
	 *
	 * @param <VALUE> Typ des Objekts.
	 * @param result Objekt oder {@code null}.
	 * @return Objekt.
	 * @throws NullPointerException Wenn {@code object} {@code null} ist. */
	public static <VALUE> VALUE notNull(VALUE result) throws NullPointerException {
		if (result != null) return result;
		throw new NullPointerException();
	}

	/** Diese Methode gibt das erste gegebene Objekt zurück, wenn dieses nicht {@code null} ist. Andernfalls wird das zweite geliefert.
	 *
	 * @param <VALUE> Typ der Objekte.
	 * @param result Objekt oder {@code null}.
	 * @param result2 Objekt oder {@code null}.
	 * @return Objekt. */
	public static <VALUE> VALUE notNull(VALUE result, VALUE result2) {
		return result != null ? result : result2;
	}

	/** Diese Methode gibt das gegebene {@link Map} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden.
	 *
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Map} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String printMap(boolean format, Map<?, ?> object) {
		if (object == null) return "null";
		if (object.isEmpty()) return "{}";
		var space = (format ? "{\n  " : "{");
		var comma = (format ? ",\n  " : ", ");
		var result = new StringBuilder();
		for (var entry: object.entrySet()) {
			result.append(space).append(toString(format, format, entry.getKey())).append(" = ").append(toString(format, format, entry.getValue()));
			space = comma;
		}
		return result.append((format ? "\n}" : "}")).toString();
	}

	/** Diese Methode gibt den gegebenen {@link Character} als {@link Object#toString() Textdarstelung} zurück.
	 *
	 * @param object {@link Character} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String printChar(Character object) {
		if (object == null) return "null";
		var result = new StringBuilder(4).append('\'');
		switch (object) {
			case '\'':
				result.append("\\\'");
			break;
			case '\t':
				result.append("\\t");
			break;
			case '\n':
				result.append("\\n");
			break;
			case '\r':
				result.append("\\r");
			break;
			default:
				result.append(object.charValue());
		}
		return result.append('\'').toString();
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden.
	 *
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt kein Array ist. */
	public static String printArray(boolean format, Object object) throws IllegalArgumentException {
		if (object == null) return "null";
		var size = Array.getLength(object);
		if (size == 0) return "[]";
		var space = (format ? "[\n  " : "[");
		var comma = (format ? ",\n  " : ", ");
		var result = new StringBuilder();
		for (var i = 0; i < size; i++) {
			result.append(space).append(toString(format, format, Array.get(object, i)));
			space = comma;
		}
		return result.append((format ? "\n]" : "]")).toString();
	}

	/** Diese Methode gibt den gegebenen {@link String} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann
	 * deren hierarchische Formatierung aktiviert werden.
	 *
	 * @param object {@link String} oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String printString(boolean format, CharSequence object) {
		if (object == null) return "null";
		var space = (format ? "\\n\"+\n\"" : "\\n");
		var result = new StringBuilder("\"");
		int last = -1, next = 0;
		var size = object.length();
		while (next < size) {
			switch (object.charAt(next)) {
				case '\"':
					result.append(object.subSequence(last + 1, last = next)).append("\\\"");
				break;
				case '\t':
					result.append(object.subSequence(last + 1, last = next)).append("\\t");
				break;
				case '\n':
					result.append(object.subSequence(last + 1, last = next)).append(space);
				break;
				case '\r':
					result.append(object.subSequence(last + 1, last = next)).append("\\r");
				break;
			}
			next++;
		}
		return result.append(object.subSequence(last + 1, size)).append("\"").toString();
	}

	/** Diese Methode gibt das gegebene {@link Iterable} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann
	 * deren hierarchische Formatierung aktiviert werden.
	 *
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Iterable} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String printIterable(boolean format, Iterable<?> object) {
		return printIterable(format, Integer.MAX_VALUE, object);
	}

	public static String printIterable(boolean format, int limit, Iterable<?> object) {
		if (object == null) return "null";
		var iter = object.iterator();
		if (!iter.hasNext()) return "[]";
		var result = new StringBuilder();
		var next = iter.next();
		if (!iter.hasNext()) return result.append("[").append(toString(format, format, next)).append("]").toString();
		result.append(format ? "[\n  " : "[").append(toString(format, format, next));
		while (iter.hasNext() && (0 < limit--)) {
			next = iter.next();
			result.append(format ? ",\n  " : ", ").append(toString(format, format, next));
		}
		if (iter.hasNext()) {
			result.append(format ? ",\n  " : ", ").append("…");
		}
		return result.append(format ? "\n]" : "]").toString();
	}

	/** Diese Methode gibt ein Objekt zurück, dessen {@link Object#toString() Textdarstelung} der über {@link Objects#toString(boolean, Object)} ermittelten
	 * Textdarstelung des gegebenen Objekts entspricht.
	 *
	 * @see Objects#toString(boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder {@code null}.
	 * @return {@link Objects#toString(boolean, Object)}-Objekt. */
	public static Object printFuture(boolean format, Object object) {
		return new PrintFuture(object, format);
	}

	/** Diese Methode gibt den Streuwert des gegebenen Zahl zurück.
	 *
	 * @param value Zahl.
	 * @return {@link Object#hashCode() Streuwert}. */
	public static int hash(long value) {
		return Integers.toIntH(value) ^ Integers.toIntL(value);
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder {@code 0} zurück.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int hash(Object object) {
		return object == null ? 0 : object.hashCode();
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Arrays oder {@code 0} zurück. Der {@link Object#hashCode() Streuwert} der
	 * Elemente des Arrays wird über {@link Objects#hash(Object)} ermittelt.
	 *
	 * @see Objects#hash(Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int hash(final Object... objects) {
		if (objects == null) return 0;
		var result = hashInit();
		for (final Object object: objects) {
			result = hashPush(result, hash(object));
		}
		return result;
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 *
	 * @see Objects#hash(Object)
	 * @see Objects#hash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int hash(Object object1, Object object2) {
		return hashPush(hashPush(hashInit(), hash(object1)), hash(object2));
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 *
	 * @see Objects#hash(Object)
	 * @see Objects#hash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @param object3 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int hash(Object object1, Object object2, Object object3) {
		return hashPush(hash(object1, object2), hash(object3));
	}

	/** Diese Methode gibt den Startwert für die Streuwertberechnung in {@link #hash(Object...)} zurück.
	 *
	 * @see #hash(Object...)
	 * @see #hash(Object, Object)
	 * @see #hash(Object, Object, Object)
	 * @return Startstreuwert. */
	public static int hashInit() {
		return 0x811C9DC5;
	}

	/** Diese Methode verbindet die gegebenen Streuwerte in der gegebenen Reihenfolge und gibt deren Kombination zurück.
	 *
	 * @see #hash(Object...)
	 * @see #hash(Object, Object)
	 * @see #hash(Object, Object, Object)
	 * @param prev vorheriger Streuwert.
	 * @param next nächater Streuwert.
	 * @return kombinierter Streuwert. */
	public static int hashPush(int prev, int next) {
		return (prev * 0x01000193) ^ next;
	}

	/** Diese Methode gibt die Bitmaske zurück, die der Umrechnung des {@link Object#hashCode() Streuwerts} eines gesuchten Schlüssels in den Index des einzigen
	 * Schlüsselbereichs dient, in dem ein gesuchter Schlüssel enthalten sein kann. Die Bitmaske ist eine um {@code 1} verringerte Potenz von {@code 2}. Ein
	 * Algorithmus zur Ermittlung der Bitmaske ist:<pre>
	 * int result = 2;
	 * while (result < entryCount) result = result << 1;
	 * return (result – 1) & 536870911;</pre>
	 *
	 * @param entryCount Anzahl der Einträge der Abbildung.
	 * @return Bitmaske. */
	public static int hashMask(int entryCount) {
		if (entryCount <= 0) return 0;
		--entryCount;
		entryCount |= (entryCount >> 1);
		entryCount |= (entryCount >> 2);
		entryCount |= (entryCount >> 4);
		entryCount |= (entryCount >> 8);
		entryCount |= (entryCount >> 16);
		return entryCount & 536870911;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben. Der Rückgabewert
	 * entspricht: <pre>
	 * (object1 == object2) || ((object1 != null) &amp;&amp; (object2 != null) &amp;&amp; object1.equals(object2))
	 * </pre>
	 *
	 * @param object1 Objekt 1 oder {@code null}.
	 * @param object2 Objekt 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean equals(final Object object1, final Object object2) {
		return (object1 == object2) || ((object1 != null) && (object2 != null) && object1.equals(object2));
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Arrays zurück und tolleriert dabei {@code null}-Eingaben. Die
	 * {@link Object#equals(Object) Äquivalenz} der Elemente der Arrays wird über {@link Objects#equals(Object, Object)} ermittelt.
	 *
	 * @see Objects#equals(Object, Object)
	 * @param objects1 Array 1 oder {@code null}.
	 * @param objects2 Array 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean equals(final Object[] objects1, final Object[] objects2) {
		if (objects1 == objects2) return true;
		if ((objects1 == null) || (objects2 == null)) return false;
		final var length = objects1.length;
		if (length != objects2.length) return false;
		for (var i = 0; i < length; i++) {
			if (!equals(objects1[i], objects2[i])) return false;
		}
		return true;
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} bzw. {@link #deepHash(Object...)} verwendet.
	 *
	 * @see Arrays#hashCode(int[])
	 * @see Arrays#hashCode(long[])
	 * @see Arrays#hashCode(byte[])
	 * @see Arrays#hashCode(char[])
	 * @see Arrays#hashCode(short[])
	 * @see Arrays#hashCode(float[])
	 * @see Arrays#hashCode(double[])
	 * @see Arrays#hashCode(boolean[])
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int deepHash(final Object object) {
		if (object == null) return 0;
		final Class<?> clazz = object.getClass();
		if (!clazz.isArray()) return object.hashCode();
		if (clazz == int[].class) return Arrays.hashCode((int[])object);
		if (clazz == long[].class) return Arrays.hashCode((long[])object);
		if (clazz == byte[].class) return Arrays.hashCode((byte[])object);
		if (clazz == char[].class) return Arrays.hashCode((char[])object);
		if (clazz == short[].class) return Arrays.hashCode((short[])object);
		if (clazz == float[].class) return Arrays.hashCode((float[])object);
		if (clazz == double[].class) return Arrays.hashCode((double[])object);
		if (clazz == boolean[].class) return Arrays.hashCode((boolean[])object);
		return deepHash((Object[])object);
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Arrays oder {@code 0} zurück. Der {@link Object#hashCode() Streuwert} der
	 * Elemente des Arrays wird über {@link Objects#deepHash(Object)} ermittelt.
	 *
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int deepHash(final Object... objects) {
		if (objects == null) return 0;
		var result = hashInit();
		for (final Object object: objects) {
			result = hashPush(result, deepHash(object));
		}
		return result;
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} verwendet.
	 *
	 * @see Objects#deepHash(Object)
	 * @see Objects#deepHash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int deepHash(final Object object1, final Object object2) {
		return hashPush(hashPush(hashInit(), deepHash(object1)), deepHash(object2));
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} verwendet.
	 *
	 * @see Objects#deepHash(Object)
	 * @see Objects#deepHash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @param object3 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int deepHash(final Object object1, final Object object2, final Object object3) {
		return hashPush(deepHash(object1, object2), deepHash(object3));
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben und Arrays. Für
	 * Arrays werden die entsprechenden Hilfsmethoden aus der Hilfsklasse {@link Arrays} verwendet. Wenn beide Objekte keine Arrays sind, entspricht der
	 * Rückgabewert dem von {@link #equals(Object, Object)}.
	 *
	 * @see Arrays#equals(int[], int[])
	 * @see Arrays#equals(long[], long[])
	 * @see Arrays#equals(byte[], byte[])
	 * @see Arrays#equals(char[], char[])
	 * @see Arrays#equals(short[], short[])
	 * @see Arrays#equals(float[], float[])
	 * @see Arrays#equals(double[], double[])
	 * @see Arrays#equals(boolean[], boolean[])
	 * @see Objects#deepEquals(Object[], Object[])
	 * @param object1 Objekt 1 oder {@code null}.
	 * @param object2 Objekt 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean deepEquals(final Object object1, final Object object2) {
		if (object1 == object2) return true;
		if ((object1 == null) || (object2 == null)) return false;
		final Class<?> c1 = object1.getClass();
		if (!c1.isArray()) return !object2.getClass().isArray() && object1.equals(object2);
		final Class<?> c2 = object2.getClass();
		if (!c2.isArray()) return false;
		if (c1 == int[].class) return (c2 == int[].class) && Arrays.equals((int[])object1, (int[])object2);
		if (c1 == long[].class) return (c2 == long[].class) && Arrays.equals((long[])object1, (long[])object2);
		if (c1 == byte[].class) return (c2 == byte[].class) && Arrays.equals((byte[])object1, (byte[])object2);
		if (c1 == char[].class) return (c2 == char[].class) && Arrays.equals((char[])object1, (char[])object2);
		if (c1 == short[].class) return (c2 == short[].class) && Arrays.equals((short[])object1, (short[])object2);
		if (c1 == float[].class) return (c2 == float[].class) && Arrays.equals((float[])object1, (float[])object2);
		if (c1 == double[].class) return (c2 == double[].class) && Arrays.equals((double[])object1, (double[])object2);
		if (c1 == boolean[].class) return (c2 == boolean[].class) && Arrays.equals((boolean[])object1, (boolean[])object2);
		return deepEquals((Object[])object1, (Object[])object2);
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Arrays zurück und tolleriert dabei {@code null}-Eingaben. Die
	 * {@link Object#equals(Object) Äquivalenz} der Elemente der Arrays wird über {@link Objects#deepEquals(Object, Object)} ermittelt.
	 *
	 * @see Objects#deepEquals(Object, Object)
	 * @param objects1 Array 1 oder {@code null}.
	 * @param objects2 Array 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean deepEquals(final Object[] objects1, final Object[] objects2) {
		if (objects1 == objects2) return true;
		if ((objects1 == null) || (objects2 == null)) return false;
		final var length = objects1.length;
		if (length != objects2.length) return false;
		for (var i = 0; i < length; i++) {
			if (!deepEquals(objects1[i], objects2[i])) return false;
		}
		return true;
	}

	/** Diese Methode gibt den {@link System#identityHashCode(Object) nativen Streuwert} des gegebenen Objekts oder {@code 0} zurück.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int identityHash(final Object object) {
		return System.identityHashCode(object);
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben. Der Rückgabewert
	 * entspricht: <pre>
	 * object1 == object2
	 * </pre>
	 *
	 * @param object1 Objekt 1 oder {@code null}.
	 * @param object2 Objekt 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean identityEquals(final Object object1, final Object object2) {
		return object1 == object2;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Arrays zurück und tolleriert dabei {@code null}-Eingaben. Die
	 * {@link Object#equals(Object) Äquivalenz} der Elemente der {@link Array Arrays} wird über {@code ==} ermittelt.
	 *
	 * @param objects1 Array 1 oder {@code null}.
	 * @param objects2 Array 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean identityEquals(final Object[] objects1, final Object[] objects2) {
		if (objects1 == objects2) return true;
		if ((objects1 == null) || (objects2 == null)) return false;
		final var length = objects1.length;
		if (length != objects2.length) return false;
		for (var i = 0; i < length; i++) {
			if (objects1[i] != objects2[i]) return false;
		}
		return true;
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht {@link #toString(boolean, Object)
	 * toString(false, object)}.
	 *
	 * @see #toString(boolean, Object)
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String toString(Object object) {
		return toString(false, object);
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@link #toString(boolean, boolean, Object) toString(format, false, object)}.
	 *
	 * @see #toString(boolean, boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String toString(boolean format, Object object) {
		return toString(format, false, object);
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette können deren
	 * hierarchische Formatierung sowie die Erhöhung des Einzugs aktiviert werden. Sollte das Objekt eine Instanz von {@link UseToString} sein, wird das Ergebnis
	 * der {@link Object#toString()}-Methode geliefert.
	 *
	 * @see Objects#printMap(boolean, Map)
	 * @see Objects#printChar(Character)
	 * @see Objects#printArray(boolean, Object)
	 * @see Objects#printString(boolean, CharSequence)
	 * @see Objects#printIterable(boolean, Iterable)
	 * @see Natives#printClass(Class)
	 * @see Natives#printField(java.lang.reflect.Field)
	 * @see Natives#printMethod(java.lang.reflect.Method)
	 * @see Natives#printConstructor(java.lang.reflect.Constructor)
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param indent Aktivierung der Erhöhung des Einzugs.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String toString(boolean format, boolean indent, Object object) {
		if (object == null) return "null";
		String result;
		if (object.getClass().isArray()) {
			result = printArray(format, object);
		} else if (object instanceof Class<?>) {
			result = printClass((Class<?>)object);
		} else if (object instanceof java.lang.reflect.Field) {
			result = printField((java.lang.reflect.Field)object);
		} else if (object instanceof java.lang.reflect.Method) {
			result = printMethod((java.lang.reflect.Method)object);
		} else if (object instanceof java.lang.reflect.Constructor<?>) {
			result = printConstructor((java.lang.reflect.Constructor<?>)object);
		} else if (object instanceof Character) {
			result = printChar((Character)object);
		} else if (object instanceof UseToString) {
			result = object.toString();
		} else if (object instanceof CharSequence) {
			result = printString(format, (CharSequence)object);
		} else if (object instanceof Map<?, ?>) {
			result = printMap(format, (Map<?, ?>)object);
		} else if (object instanceof Iterable<?>) {
			result = printIterable(format, (Iterable<?>)object);
		} else {
			result = String.valueOf(object);
		}
		return (indent ? Strings.indent(result) : result);
	}

	/** Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden. Wenn die Argumentbeschriftung aktiviert wird, werden die Argumente als beschriftete Parameter interpretiert.
	 * Ein beschrifteter Parameter besteht hierbei aus einem Namen {@code args[i]} und dem darauf folgenden Wert {@code args[i+1]} für jede gerade Position
	 * {@code i}.
	 *
	 * @param name Funktionsname.
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn {@code name} bzw. {@code args} {@code null} ist. */
	public static String toStringCall(final boolean format, final boolean label, final String name, final Object... args) throws NullPointerException {
		final var result = new StringBuilder(name.length() + 128);
		result.append(name);
		if (args.length != 0) {
			var join = (format ? "(\n  " : "(");
			final var comma = (format ? ",\n  " : ", ");
			if (label) {
				for (int i = 0, size = args.length - 1; i < size; i += 2) {
					result.append(join).append(toString(format, format, args[i])).append(": ").append(toString(format, format, args[i + 1]));
					join = comma;
				}
			} else {
				for (Object arg: args) {
					result.append(join).append(toString(format, format, arg));
					join = comma;
				}
			}
			result.append((format ? "\n)" : ")"));
		} else {
			result.append("()");
		}
		return result.toString();
	}

	/** Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@link #toStringCall(boolean, boolean, String, Object...) toStringCall(format, label, Strings.substringAfterLast(object.getClass().getName(), '.'), args)}.
	 *
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param object {@link Object}.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
	public static String toStringCall(final boolean format, final boolean label, final Object object, final Object... args) throws NullPointerException {
		return toStringCall(format, label, Strings.substringAfterLast(object.getClass().getName(), '.'), args);
	}

	/** Diese Methode gibt ein Objekt zurück, dessen {@link Object#toString() Textdarstelung} der des gegebenen Objekts entspricht.
	 *
	 * @param object Textdarstelung.
	 * @return Textdarstelung-Objekt. */
	public static Object toStringFuture(final Object object) {
		return object == null ? "null" : new StringFuture(object);
	}

	/** Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@code toStringCall(false, false, name, args)}.
	 *
	 * @see Objects#toStringCall(boolean, boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param args Argumente.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn {@code name} bzw. {@code args} {@code null} ist. */
	public static String toInvokeString(final String name, final Object... args) throws NullPointerException {
		return toStringCall(false, false, name, args);
	}

	/** Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@code toStringCall(false, false, object, args)}.
	 *
	 * @see #toStringCall(boolean, boolean, Object, Object...)
	 * @param object {@link Object}.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn {@code object} bzw. {@code args} {@code null} ist. */
	public static String toInvokeString(final Object object, final Object... args) throws NullPointerException {
		return toStringCall(false, false, object, args);
	}

	/** Diese Schnittstelle definiert eine Markierung für die Methode {@link Objects#toString(boolean, Object)}. Sie ist nur sinnvoll für eigene Implementationen
	 * von {@link Map}, {@link Iterable} und {@link CharSequence}, für welche nicht die in {@link Objects#toString(boolean, Object)} realisierten, gesonderten
	 * Textdarstellungen verwendet werden sollen.
	 *
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface UseToString {

	}

	/** Diese Klasse implementiert ein Objekt, dessen {@link #toString() Textdarsellung} über {@link Objects#toInvokeString(Object, Object...)
	 * toInvokeString(this)} ermittelt wird. Sie kann als Basis von Klassen mit nur einer Instanz eingesetzt werden. */
	public static class BaseObject {

		@Override
		public String toString() {
			return toInvokeString(this);
		}

	}

	private static final class PrintFuture {

		final Object object;

		final boolean format;

		PrintFuture(final Object object, final boolean format) {
			this.object = object;
			this.format = format;
		}

		@Override
		public String toString() {
			return Objects.toString(this.format, this.object);
		}

	}

	private static final class StringFuture {

		final Object object;

		StringFuture(final Object object) {
			this.object = notNull(object);
		}

		@Override
		public String toString() {
			return this.object.toString();
		}

	}

}
