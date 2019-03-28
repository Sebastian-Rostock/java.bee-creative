package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.bind.Getter;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Berechnung von {@link Object#hashCode() Streuwerten}, {@link Object#equals(Object)
 * Äquivalenzen} und {@link Object#toString() Textdarstelungen}.
 *
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @see Object#toString()
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Objects {

	/** Diese Schnittstelle definiert eine Markierung für die Methode {@link Objects#toString(boolean, Object)}. Sie ist nur sinnvoll für eigene Implementationen
	 * von {@link Map}, {@link Iterable} und {@link CharSequence}, für welche nicht die in {@link Objects#toString(boolean, Object)} realisierten, gesonderten
	 * Textdarstellungen verwendet werden sollen.
	 *
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface UseToString {

	}

	/** Diese Klasse implementiert ein Objekt, dessen {@link #toString() Textdarsellung} über {@link Objects#toInvokeString(Object, Object...)
	 * Objects.toInvokeString(this)} ermittelt wird. Sie kann als Basis von Klassen mit nur einer Instanz eingesetzt werden. */
	public static class BaseObject {

		@Override
		public String toString() {
			return Objects.toInvokeString(this);
		}

	}

	/** Diese Klasse implementiert {@link Objects#HASHER}. */
	public static class BaseHasher extends BaseObject implements Hasher {

		@Override
		public int hash(final Object input) {
			return Objects.hash(input);
		}

		@Override
		public boolean equals(final Object input1, final Object input2) {
			return Objects.equals(input1, input2);
		}

	}

	/** Diese Klasse implementiert {@link Objects#DEEP_HASHER}. */
	public static class DeepHasher extends BaseHasher {

		@Override
		public int hash(final Object input) {
			return Objects.deepHash(input);
		}

		@Override
		public boolean equals(final Object input1, final Object input2) {
			return Objects.deepEquals(input1, input2);
		}

	}

	/** Diese Klasse implementiert {@link Objects#IDENTITY_HASHER}. */
	public static class IdentityHasher extends BaseHasher {

		@Override
		public int hash(final Object input) {
			return System.identityHashCode(input);
		}

		@Override
		public boolean equals(final Object input1, final Object input2) {
			return input1 == input2;
		}

	}

	/** Diese Klasse implementiert {@link Objects#translatedHasher(Getter, Hasher)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedHasher implements Hasher {

		public final Hasher hasher;

		public final Getter<? super Object, ? extends Object> navigator;

		public TranslatedHasher(final Hasher hasher, final Getter<? super Object, ? extends Object> navigator) {
			this.hasher = Objects.notNull(hasher);
			this.navigator = Objects.notNull(navigator);
		}

		@Override
		public int hash(final Object input) {
			return this.hasher.hash(this.navigator.get(input));
		}

		@Override
		public boolean equals(final Object input1, final Object input2) {
			return this.hasher.equals(this.navigator.get(input1), this.navigator.get(input2));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.navigator, this.hasher);
		}

	}

	/** Diese Klasse implementiert {@link Objects#toStringObject(String)}. */
	@SuppressWarnings ("javadoc")
	public static class StringObject {

		public final String string;

		public StringObject(final String string) {
			this.string = Objects.notNull(string);
		}

		@Override
		public String toString() {
			return this.string;
		}

	}

	/** Diese Klasse implementiert {@link Objects#toStringObject(boolean, Object)}. */
	@SuppressWarnings ("javadoc")
	public static class FormatObject {

		public final boolean format;

		public final Object object;

		public FormatObject(final boolean format, final Object object) {
			this.format = format;
			this.object = object;
		}

		@Override
		public String toString() {
			return Objects.toString(this.format, this.object);
		}

	}

	/** Dieses Feld speichert den {@link Hasher}, der an {@link Objects#hash(Object)} und {@link Objects#equals(Object, Object)} delegiert. */
	public static final Hasher HASHER = new BaseHasher();

	/** Dieses Feld speichert den {@link Hasher}, der an {@link Objects#deepHash(Object)} und {@link Objects#deepEquals(Object, Object)} delegiert. */
	public static final Hasher DEEP_HASHER = new DeepHasher();

	/** Dieses Feld speichert den {@link Hasher}, der an {@link System#identityHashCode(Object)} und die Objekte über ihre Referenz vergleicht. */
	public static final Hasher IDENTITY_HASHER = new IdentityHasher();

	/** Diese Methode gibt die gegebenen Zeichenkette mit erhöhtem Einzug zurück. Dazu wird jedes Vorkommen von {@code "\n"} durch {@code "\n  "} ersetzt.
	 *
	 * @param value Zeichenkette.
	 * @return Zeichenkette mit erhöhtem Einzug. */
	public static String indent(final String value) {
		if (value == null) return "null";
		final StringBuilder result = new StringBuilder();
		int last = -1, next = 0;
		final int size = value.length();
		while ((next = value.indexOf('\n', next)) >= 0) {
			result.append(value.substring(last + 1, last = next)).append("\n  ");
			next++;
		}
		return result.append(value.substring(last + 1, size)).toString();
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für {@link Map}, native Arrays,
	 * {@link CharSequence} und {@link Iterable} je eine eigene Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette können deren
	 * hierarchische Formatierung sowie die Erhöhung des Einzugs aktiviert werden. Sollte das Objekt eine Instanz von {@link UseToString} sein, wird das Ergebnis
	 * der {@link Object#toString()}-Methode geliefert.
	 *
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param indent Aktivierung der Erhöhung des Einzugs.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String format(final boolean format, final boolean indent, final Object object) {
		if (object == null) return "null";
		final String result;
		if (object.getClass().isArray()) {
			result = Objects.formatArray(format, object);
		} else if (object instanceof Class<?>) {
			result = Natives.formatClass((Class<?>)object);
		} else if (object instanceof java.lang.reflect.Field) {
			result = Natives.formatField((java.lang.reflect.Field)object);
		} else if (object instanceof java.lang.reflect.Method) {
			result = Natives.formatMethod((java.lang.reflect.Method)object);
		} else if (object instanceof java.lang.reflect.Constructor<?>) {
			result = Natives.formatConstructor((java.lang.reflect.Constructor<?>)object);
		} else if (object instanceof Character) {
			result = Objects.formatChar((Character)object);
		} else if (object instanceof UseToString) {
			result = String.valueOf(object);
		} else if (object instanceof CharSequence) {
			result = Objects.formatString(format, (CharSequence)object);
		} else if (object instanceof Map<?, ?>) {
			result = Objects.formatMap(format, (Map<?, ?>)object);
		} else if (object instanceof Iterable<?>) {
			result = Objects.formatIterable(format, (Iterable<?>)object);
		} else {
			result = String.valueOf(object);
		}
		return (indent ? Objects.indent(result) : result);
	}

	/** Diese Methode gibt das gegebene {@link Map} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden.
	 *
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Map} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String formatMap(final boolean format, final Map<?, ?> object) {
		if (object == null) return "null";
		if (object.isEmpty()) return "";
		String space = (format ? "{\n  " : "{");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder result = new StringBuilder();
		for (final Entry<?, ?> entry: object.entrySet()) {
			result.append(space).append(Objects.format(format, format, entry.getKey())).append(" = ").append(Objects.format(format, format, entry.getValue()));
			space = comma;
		}
		return result.append((format ? "\n}" : "}")).toString();
	}

	/** Diese Methode gibt den gegebenen {@link Character} als {@link Object#toString() Textdarstelung} zurück.
	 *
	 * @param object {@link Character} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String formatChar(final Character object) {
		if (object == null) return "null";
		final StringBuilder result = new StringBuilder(4).append('\'');
		switch (object.charValue()) {
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
	public static String formatArray(final boolean format, final Object object) throws IllegalArgumentException {
		if (object == null) return "null";
		final int size = Array.getLength(object);
		if (size == 0) return "[]";
		String space = (format ? "[\n  " : "[");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < size; i++) {
			result.append(space).append(Objects.format(format, format, Array.get(object, i)));
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
	public static String formatString(final boolean format, final CharSequence object) {
		if (object == null) return "null";
		final String space = (format ? "\\n\"+\n\"" : "\\n");
		final StringBuilder result = new StringBuilder("\"");
		int last = -1, next = 0;
		final int size = object.length();
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
	public static String formatIterable(final boolean format, final Iterable<?> object) {
		if (object == null) return "null";
		final Iterator<?> iter = object.iterator();
		if (!iter.hasNext()) return "[]";
		final StringBuilder result = new StringBuilder();
		Object next = iter.next();
		if (!iter.hasNext()) return result.append("[").append(Objects.format(format, format, next)).append("]").toString();
		result.append(format ? "[\n  " : "[").append(Objects.format(format, format, next));
		while (iter.hasNext()) {
			next = iter.next();
			result.append(format ? ",\n  " : ", ").append(Objects.format(format, format, next));
		}
		return result.append(format ? "\n]" : "]").toString();
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder {@code 0} zurück.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int hash(final Object object) {
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
		int result = Objects.hashInit();
		for (final Object object: objects) {
			result = Objects.hashPush(result, Objects.hash(object));
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
	public static int hash(final Object object1, final Object object2) {
		return Objects.hashPush(Objects.hashPush(Objects.hashInit(), Objects.hash(object1)), Objects.hash(object2));
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 *
	 * @see Objects#hash(Object)
	 * @see Objects#hash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @param object3 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int hash(final Object object1, final Object object2, final Object object3) {
		return Objects.hashPush(Objects.hash(object1, object2), Objects.hash(object3));
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
	public static int hashPush(final int prev, final int next) {
		return (prev * 0x01000193) ^ next;
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
		final int length = objects1.length;
		if (length != objects2.length) return false;
		for (int i = 0; i < length; i++) {
			if (!Objects.equals(objects1[i], objects2[i])) return false;
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
		return Objects.deepHash((Object[])object);
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Arrays oder {@code 0} zurück. Der {@link Object#hashCode() Streuwert} der
	 * Elemente des Arrays wird über {@link Objects#deepHash(Object)} ermittelt.
	 *
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}. */
	public static int deepHash(final Object... objects) {
		if (objects == null) return 0;
		int result = Objects.hashInit();
		for (final Object object: objects) {
			result = Objects.hashPush(result, Objects.deepHash(object));
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
		return Objects.hashPush(Objects.hashPush(Objects.hashInit(), Objects.deepHash(object1)), Objects.deepHash(object2));
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
		return Objects.hashPush(Objects.deepHash(object1, object2), Objects.deepHash(object3));
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
		return Objects.deepEquals((Object[])object1, (Object[])object2);
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
		final int length = objects1.length;
		if (length != objects2.length) return false;
		for (int i = 0; i < length; i++) {
			if (!Objects.deepEquals(objects1[i], objects2[i])) return false;
		}
		return true;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Arrays zurück und tolleriert dabei {@code null}-Eingaben. Die
	 * {@link Object#equals(Object) Äquivalenz} der Elemente der {@link Array Arrays} wird über {@coe ==} ermittelt.
	 *
	 * @param objects1 Array 1 oder {@code null}.
	 * @param objects2 Array 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	public static boolean identityEquals(final Object[] objects1, final Object[] objects2) {
		if (objects1 == objects2) return true;
		if ((objects1 == null) || (objects2 == null)) return false;
		final int length = objects1.length;
		if (length != objects2.length) return false;
		for (int i = 0; i < length; i++) {
			if (objects1[i] != objects2[i]) return false;
		}
		return true;
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@code Objects.toString(false, object)}.
	 *
	 * @see Objects#toString(boolean, Object)
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String toString(final Object object) {
		return Objects.toString(false, object);
	}

	/** Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für {@link Map}, Arrays, {@link CharSequence} und
	 * {@link Iterable} je eine einene eigene Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung
	 * aktiviert werden.
	 * <p>
	 * Sollte das gegebene Objekt eine Instanz von {@link UseToString} sein, wird das Ergebnis seiner {@link Object#toString() toString()}-Methode geliefert.
	 *
	 * @see Objects#format(boolean, boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}. */
	public static String toString(final boolean format, final Object object) {
		return Objects.format(format, false, object);
	}

	/** Diese Methode gibt ein Objekt zurück, dessen {@link Object#toString() Textdarstelung} der gegebene Zeichenkette entspricht.
	 *
	 * @param string Textdarstelung.
	 * @return Textdarstelung-Objekt. */
	public static Object toStringObject(final String string) {
		if (string == null) return "null";
		return new StringObject(string);
	}

	/** Diese Methode gibt ein Objekt zurück, dessen {@link Object#toString() Textdarstelung} der über {@link Objects#toString(boolean, Object)} ermittelten
	 * Textdarstelung des gegebenen Objekts entspricht.
	 *
	 * @see Objects#toString(boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder {@code null}.
	 * @return {@link Objects#toString(boolean, Object)}-Objekt. */
	public static Object toStringObject(final boolean format, final Object object) {
		return new FormatObject(format, object);
	}

	/** Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@code Objects.toStringCall(false, false, name, args)}.
	 *
	 * @see Objects#toFormatString(boolean, boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param args Argumente.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn {@code name} bzw. {@code args} {@code null} ist. */
	public static String toInvokeString(final String name, final Object... args) throws NullPointerException {
		return Objects.toFormatString(false, false, name, args);
	}

	/** Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht
	 * {@code Objects.toStringCall(false, false, object.getClass().getSimpleName(), args)}.
	 *
	 * @see #toFormatString(boolean, boolean, String, Object...)
	 * @param object {@link Object}.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn {@code object} bzw. {@code args} {@code null} ist. */
	public static String toInvokeString(final Object object, final Object... args) throws NullPointerException {
		return Objects.toFormatString(false, false, object.getClass().getSimpleName(), args);
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
	public static String toFormatString(final boolean format, final boolean label, final String name, final Object... args) throws NullPointerException {
		final StringBuilder result = new StringBuilder(name.length() + 128);
		result.append(name);
		if (args.length != 0) {
			String join = (format ? "(\n  " : "(");
			final String comma = (format ? ",\n  " : ", ");
			if (label) {
				for (int i = 0, size = args.length - 1; i < size; i += 2) {
					result.append(join).append(Objects.format(format, format, args[i])).append(": ").append(Objects.format(format, format, args[i + 1]));
					join = comma;
				}
			} else {
				for (int i = 0, size = args.length; i < size; i++) {
					result.append(join).append(Objects.format(format, format, args[i]));
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
	 * {@code Objects.toStringCall(format, label, object.getClass().getSimpleName(), args)}.
	 *
	 * @see #toFormatString(boolean, boolean, String, Object...)
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param object {@link Object}.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
	public static String toFormatString(final boolean format, final boolean label, final Object object, final Object... args) throws NullPointerException {
		return Objects.toFormatString(format, label, object.getClass().getSimpleName(), args);
	}

	/** Diese Methode gibt das gegebene Objekt zurück, wenn dieses nicht {@code null} ist.
	 *
	 * @param <GResult> Typ des Objekts.
	 * @param result Objekt oder {@code null}.
	 * @return Objekt.
	 * @throws NullPointerException Wenn {@code object} {@code null} ist. */
	public static <GResult> GResult notNull(final GResult result) throws NullPointerException {
		if (result != null) return result;
		throw new NullPointerException();
	}

	/** Diese Methode gibt das erste gegebene Objekt zurück, wenn dieses nicht {@code null} ist. Andernfalls wird das zweite geliefert.
	 *
	 * @param <GResult> Typ der Objekte.
	 * @param result Objekt oder {@code null}.
	 * @param result2 Objekt oder {@code null}.
	 * @return Objekt. */
	public static <GResult> GResult notNull(final GResult result, final GResult result2) {
		if (result != null) return result;
		return result2;
	}

	/** Diese Methode gibt einen navigierten {@link Hasher} zurück. Der erzeugte {@link Hasher} liefert für eine Eingabe {@code input} den Streuwert
	 * {@code hasher.hash(navigator.get(input))}.
	 *
	 * @param navigator {@link Getter} zur Navigation.
	 * @param hasher {@link Hasher} zur Abgleich.
	 * @return {@code translated}-{@link Hasher}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code hasher} {@code null} ist. */
	public static Hasher translatedHasher(final Getter<? super Object, ? extends Object> navigator, final Hasher hasher) throws NullPointerException {
		return new TranslatedHasher(hasher, navigator);
	}

}
