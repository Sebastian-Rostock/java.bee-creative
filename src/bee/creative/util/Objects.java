package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Berechnung von {@link Object#hashCode() Streuwerten},
 * {@link Object#equals(Object) Äquivalenzen} und {@link Object#toString() Textdarstelungen}.
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Objects {

	/**
	 * Diese Schnittstelle definiert eine Markierung für die Methode {@link Objects#toString(boolean, boolean, Object)},
	 * sodass diese für Objekte mit dieser Schnittstelle den ückgabewert derer {@link Object#toString() toString()}
	 * -Methode zurück geben kann.
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static public interface UseToString {}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder <code>0</code> zurück.
	 * @see Arrays#hashCode(int[])
	 * @see Arrays#hashCode(long[])
	 * @see Arrays#hashCode(byte[])
	 * @see Arrays#hashCode(char[])
	 * @see Arrays#hashCode(short[])
	 * @see Arrays#hashCode(float[])
	 * @see Arrays#hashCode(double[])
	 * @see Arrays#hashCode(boolean[])
	 * @see Objects#hashAll(Object...)
	 * @param object Objekt oder <code>null</code>.
	 * @return {@link Object#hashCode() Streuwert} oder <code>0</code>.
	 */
	static public final int hash(final Object object) {
		if (object == null) return 0;
		if (!object.getClass().isArray()) return object.hashCode();
		if (object instanceof int[]) return Arrays.hashCode((int[]) object);
		if (object instanceof long[]) return Arrays.hashCode((long[]) object);
		if (object instanceof byte[]) return Arrays.hashCode((byte[]) object);
		if (object instanceof char[]) return Arrays.hashCode((char[]) object);
		if (object instanceof short[]) return Arrays.hashCode((short[]) object);
		if (object instanceof float[]) return Arrays.hashCode((float[]) object);
		if (object instanceof double[]) return Arrays.hashCode((double[]) object);
		if (object instanceof boolean[]) return Arrays.hashCode((boolean[]) object);
		return Objects.hashAll((Object[]) object);
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder <code>0</code> zurück.
	 * @see Objects#hash(Object)
	 * @param objects Objekte oder <code>null</code>.
	 * @return {@link Object#hashCode() Streuwert} oder <code>0</code>.
	 */
	static public final int hashAll(final Object... objects) {
		if (objects == null) return 0;
		int hash = 1;
		for (final Object object : objects) {
			hash = (31 * hash) + Objects.hash(object);
		}
		return hash;
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei
	 * <code>null</code>-Eingaben und {@link Array Arrays}. Wenn beide Objekte keine {@link Array Arrays} sind,
	 * entspricht der Rückgabewert:
	 * 
	 * <pre>
	 * (object1 == object2) || ((object1 != null) &amp;&amp; (object2 != null) &amp;&amp; object1.equals(object2))
	 * </pre>
	 * @see Arrays#equals(int[], int[])
	 * @see Arrays#equals(long[], long[])
	 * @see Arrays#equals(byte[], byte[])
	 * @see Arrays#equals(char[], char[])
	 * @see Arrays#equals(short[], short[])
	 * @see Arrays#equals(float[], float[])
	 * @see Arrays#equals(double[], double[])
	 * @see Arrays#equals(boolean[], boolean[])
	 * @see Objects#equals(Object[], Object[])
	 * @param object1 Objekt 1 oder <code>null</code>.
	 * @param object2 Objekt 2 oder <code>null</code>.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	static public final boolean equals(final Object object1, final Object object2) {
		if (object1 == object2) return true;
		if ((object1 == null) || (object2 == null)) return false;
		if (!object1.getClass().isArray()) return !object2.getClass().isArray() && object1.equals(object2);
		if (!object2.getClass().isArray()) return false;
		if (object1 instanceof int[]) return (object2 instanceof int[]) && Arrays.equals((int[]) object1, (int[]) object2);
		if (object1 instanceof long[]) return (object2 instanceof long[]) && Arrays.equals((long[]) object1, (long[]) object2);
		if (object1 instanceof byte[]) return (object2 instanceof byte[]) && Arrays.equals((byte[]) object1, (byte[]) object2);
		if (object1 instanceof char[]) return (object2 instanceof char[]) && Arrays.equals((char[]) object1, (char[]) object2);
		if (object1 instanceof short[]) return (object2 instanceof short[]) && Arrays.equals((short[]) object1, (short[]) object2);
		if (object1 instanceof float[]) return (object2 instanceof float[]) && Arrays.equals((float[]) object1, (float[]) object2);
		if (object1 instanceof double[]) return (object2 instanceof double[]) && Arrays.equals((double[]) object1, (double[]) object2);
		if (object1 instanceof boolean[]) return (object2 instanceof boolean[]) && Arrays.equals((boolean[]) object1, (boolean[]) object2);
		return Objects.equals((Object[]) object1, (Object[]) object2);
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei
	 * <code>null</code>-Eingaben.
	 * @see Objects#equals(Object, Object)
	 * @param objects1 Objekte 1 oder <code>null</code>.
	 * @param objects2 Objekte 2 oder <code>null</code>.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	static public final boolean equals(final Object[] objects1, final Object[] objects2) {
		if (objects1 == objects2) return true;
		if ((objects1 == null) || (objects2 == null)) return false;
		final int length = objects1.length;
		if (length != objects2.length) return false;
		for (int i = 0; i < length; i++) {
			if (!Objects.equals(objects1[i], objects2[i])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte zurück und
	 * tolleriert dabei <code>null</code>-Eingaben. Verglichen werden jeweils die Objekte <code>objects[i]</code> und
	 * <code>objects[i+1]</code> der geraden Positionen <code>i</code>.
	 * @see Objects#equals(Object, Object)
	 * @param objects Objekte oder <code>null</code>.
	 * @return {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte.
	 */
	static public final boolean equalsAll(final Object... objects) {
		if (objects == null) return true;
		for (int i = 0, size = objects.length; i < size; i += 2) {
			if (!Objects.equals(objects[i], objects[i + 1])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für
	 * {@link Map Maps}, {@link Array Arrays}, {@link String Strings} und {@link Iterable Iterables} je eine einene
	 * eigene Darstellungsform verwendet. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toString(false, object)
	 * </pre>
	 * @see Objects#toString(boolean, Object)
	 * @param object Objekt oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toString(final Object object) {
		return Objects.toString(false, object);
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für
	 * {@link Map Maps}, {@link Array Arrays}, {@link String Strings} und {@link Iterable Iterables} je eine einene
	 * eigene Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische
	 * Formatierung aktiviert werden. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toString(format, false, object)
	 * </pre>
	 * @see Objects#toString(boolean, boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toString(final boolean format, final Object object) {
		return Objects.toString(format, false, object);
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für
	 * {@link Map Maps}, {@link Array Arrays}, {@link String Strings} und {@link Iterable Iterables} je eine einene
	 * eigene Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette können deren hierarchische
	 * Formatierung sowie die Erhöhung des Einzugs aktiviert werden.
	 * @param object Objekt oder <code>null</code>.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param indent Aktivierung der Erhöhung des Einzugs.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toString(final boolean format, final boolean indent, final Object object) {
		if (object == null) return "null";
		final String output;
		if (object.getClass().isArray()) {
			output = Objects.toStringArray(format, object);
		} else if (object instanceof String) {
			output = Objects.toStringString(format, (String) object);
		} else if (object instanceof UseToString) {
			output = String.valueOf(object);
		} else if (object instanceof Map<?, ?>) {
			output = Objects.toStringMap(format, (Map<?, ?>) object);
		} else if (object instanceof Iterable<?>) {
			output = Objects.toStringIterable(format, (Iterable<?>) object);
		} else {
			output = String.valueOf(object);
		}
		return (indent ? Objects.toStringIndent(output) : output);
	}

	/**
	 * Diese Methode gibt das gegebene {@link Map Map} als {@link Object#toString() Textdarstelung} zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringMap(false, object)
	 * </pre>
	 * @see Objects#toStringMap(boolean, Map)
	 * @param object {@link Map Map} oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringMap(final Map<?, ?> object) {
		return Objects.toStringMap(false, object);
	}

	/**
	 * Diese Methode gibt das gegebene {@link Map Map} als {@link Object#toString() Textdarstelung} zurück. Für eine
	 * bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Map Map} oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringMap(final boolean format, final Map<?, ?> object) {
		if (object == null) return "null";
		if (object.isEmpty()) return "{}";
		String space = (format ? "{\n  " : "{ ");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		for (final Entry<?, ?> entry : object.entrySet()) {
			output.append(space).append(Objects.toString(format, format, entry.getKey())).append(": ").append(Objects.toString(format, format, entry.getValue()));
			space = comma;
		}
		return output.append((format ? "\n}" : " }")).toString();
	}

	/**
	 * Diese Methode gibt das gegebene {@link Array Array} als {@link Object#toString() Textdarstelung} zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringArray(false, object)
	 * </pre>
	 * @see Objects#toStringArray(boolean, Object)
	 * @param object {@link Array Array} oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringArray(final Object object) {
		return Objects.toStringArray(false, object);
	}

	/**
	 * Diese Methode gibt das gegebene {@link Array Array} als {@link Object#toString() Textdarstelung} zurück. Für eine
	 * bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * @param object {@link Array Array} oder <code>null</code>.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringArray(final boolean format, final Object object) {
		if (object == null) return "null";
		final int size = Array.getLength(object);
		if (size == 0) return "[]";
		String space = (format ? "[\n  " : "[ ");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		for (int i = 0; i < size; i++) {
			output.append(space).append(Objects.toString(format, format, Array.get(object, i)));
			space = comma;
		}
		return output.append((format ? "\n]" : " ]")).toString();
	}

	/**
	 * Diese Methode gibt den gegebenen {@link String String} als {@link Object#toString() Textdarstelung} zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringString(false, object)
	 * </pre>
	 * @see Objects#toStringString(boolean, String)
	 * @param object {@link String String} oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringString(final String object) {
		return Objects.toString(false, object);
	}

	/**
	 * Diese Methode gibt den gegebenen {@link String String} als {@link Object#toString() Textdarstelung} zurück. Für
	 * eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * @param object {@link String String} oder <code>null</code>.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringString(final boolean format, final String object) {
		if (object == null) return "null";
		final String space = (format ? "\\n\"+\n\"" : "\\n");
		final StringBuilder output = new StringBuilder("\"");
		int last = -1, next = 0;
		final int size = object.length();
		while (next < size) {
			switch (object.charAt(next)) {
				case '\"':
					output.append(object.substring(last + 1, last = next)).append("\\\"");
				break;
				case '\t':
					output.append(object.substring(last + 1, last = next)).append("\\t");
				break;
				case '\n':
					output.append(object.substring(last + 1, last = next)).append(space);
				break;
				case '\r':
					output.append(object.substring(last + 1, last = next)).append("\\r");
				break;
			}
			next++;
		}
		return output.append(object.substring(last + 1, size)).append("\"").toString();
	}

	/**
	 * Diese Methode gibt die gegebenen Zeichenkette mit erhöhtem Einzug zurück.
	 * @param value Zeichenkette.
	 * @return Zeichenkette mit erhöhtem Einzug.
	 */
	static public final String toStringIndent(final String value) {
		if (value == null) return "null";
		final StringBuilder output = new StringBuilder();
		int last = -1;
		final int size = value.length();
		for (int next = 0; next < size; next++)
			if (value.charAt(next) == '\n') {
				output.append(value.substring(last + 1, last = next)).append("\n  ");
			}
		return output.append(value.substring(last + 1, size)).toString();
	}

	/**
	 * Diese Methode gibt das gegebene {@link Iterable Iterable} als {@link Object#toString() Textdarstelung} zurück.
	 * Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringIterable(false, object)
	 * </pre>
	 * @see Objects#toStringIterable(boolean, Iterable)
	 * @param object {@link Iterable Iterable} oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringIterable(final Iterable<?> object) {
		return Objects.toStringIterable(false, object);
	}

	/**
	 * Diese Methode gibt das gegebene {@link Iterable Iterable} als {@link Object#toString() Textdarstelung} zurück.
	 * Für eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Iterable Iterable} oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringIterable(final boolean format, final Iterable<?> object) {
		if (object == null) return "null";
		final Iterator<?> iter = object.iterator();
		if (!iter.hasNext()) return "[]";
		String space = (format ? "[\n  " : "[ ");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		do {
			output.append(space).append(Objects.toString(format, format, iter.next()));
			space = comma;
		} while (iter.hasNext());
		return output.append((format ? "\n]" : " ]")).toString();
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert
	 * entspricht:
	 * 
	 * <pre>
	 * Objects.toStringCall(false, name, args)
	 * </pre>
	 * @see Objects#toStringCall(boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param args Argumente bzw. Parameter oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringCall(final String name, final Object... args) {
		return Objects.toStringCall(false, name, args);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Für eine bessere
	 * Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringCall(format, false, name, args)
	 * </pre>
	 * @see Objects#toStringCall(boolean, boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param format Formatiermodus.
	 * @param args Argumente bzw. Parameter oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringCall(final boolean format, final String name, final Object... args) {
		return Objects.toStringCall(format, false, name, args);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Für eine bessere
	 * Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden. Wenn die Argumentbeschriftung
	 * aktiviert wird, werden die Argumente beschriftete Parameter interpretiert. Ein beschrifteter Parameter besteht
	 * hierbei aus einem Namen <code>args[i]</code> und einem Wert <code>args[i+1]</code> für jede gerade Position
	 * <code>i</code>.
	 * @param name Funktionsname.
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param args Argumente bzw. Parameter oder <code>null</code>.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static public final String toStringCall(final boolean format, final boolean label, final String name, final Object... args) {
		final StringBuilder output = new StringBuilder(name);
		if ((args == null) || (args.length != 0)) {
			String join = (format ? "(\n  " : "( ");
			final String comma = (format ? ",\n  " : ", ");
			if (label) {
				for (int i = 0, size = args.length - 1; i < size; i += 2) {
					output.append(join).append(Objects.toString(format, format, args[i])).append(" = ").append(Objects.toString(format, format, args[i + 1]));
					join = comma;
				}
			} else {
				for (int i = 0, size = args.length; i < size; i++) {
					output.append(join).append(Objects.toString(format, format, args[i]));
					join = comma;
				}
			}
			output.append((format ? "\n)" : " )"));
		} else {
			output.append("()");
		}
		return output.toString();
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Objects() {}

}
