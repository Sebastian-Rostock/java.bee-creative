package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Berechnung von {@link Object#hashCode() Streuwerten}, {@link Object#equals(Object)
 * Äquivalenzen} und {@link Object#toString() Textdarstelungen}.
 * 
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @see Object#toString()
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Objects {

	/**
	 * Diese Schnittstelle definiert eine Markierung für die Methode {@link Objects#toString(boolean, boolean, Object)}. Für Objekte mit dieser Schnittstelle
	 * nutzt diese Methode die von den Objekten bereitgestellten {@link Object#toString()}-Methoden. Diese Markierung ist nur sinnvoll für {@link Map},
	 * {@link Iterable} und {@link CharSequence}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface UseToString {
	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dessen {@link Object#toString() Textdarstelung} der via {@link Objects#toString(boolean, Object)}
	 * ermittelten {@link Object#toString() Textdarstelung} des gegebenen Objekts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class AbstractObject {

		/**
		 * Dieses Feld speichert das Objekt.
		 */
		final Object object;

		/**
		 * Dieser Konstruktor initialisiert das Objekt.
		 * 
		 * @param object Objekt.
		 */
		public AbstractObject(final Object object) {
			this.object = object;
		}

		/**
		 * Diese Methode gibt das Objekt zurück.
		 * 
		 * @return Objekt.
		 */
		public Object object() {
			return this.object;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.object);
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt, dessen {@link Object#toString() Textdarstelung} der via {@link Objects#toString(boolean, Object)} ermittelten
	 * {@link Object#toString() Textdarstelung} eines gegebenen Objekts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class NormalObject extends AbstractObject {

		/**
		 * Dieser Konstruktor initialisiert das Objekt.
		 * 
		 * @param object Objekt.
		 */
		public NormalObject(final Object object) {
			super(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof NormalObject)) return false;
			final NormalObject data = (NormalObject)object;
			return Objects.equals(this.object, data.object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(false, this.object);
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt, dessen {@link Object#toString() Textdarstelung} der via {@link Objects#toString(boolean, Object)} ermittelten
	 * formatierten {@link Object#toString() Textdarstelung} eines gegebenen Objekts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class FormatObject extends AbstractObject {

		/**
		 * Dieser Konstruktor initialisiert das Objekt.
		 * 
		 * @param object Objekt.
		 */
		public FormatObject(final Object object) {
			super(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FormatObject)) return false;
			final FormatObject data = (FormatObject)object;
			return Objects.equals(this.object, data.object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.object);
		}

	}

	{}

	/**
	 * Diese Methode gibt die gegebenen Zeichenkette mit erhöhtem Einzug zurück. Dazu wird jedes Vorkommen von {@code "\n"} durch {@code "\n  "} ersetzt.
	 * 
	 * @param value Zeichenkette.
	 * @return Zeichenkette mit erhöhtem Einzug.
	 */
	static String indent(final String value) {
		if (value == null) return "null";
		final StringBuilder output = new StringBuilder();
		int last = -1, next = 0;
		final int size = value.length();
		while ((next = value.indexOf('\n', next)) >= 0) {
			output.append(value.substring(last + 1, last = next)).append("\n  ");
			next++;
		}
		return output.append(value.substring(last + 1, size)).toString();
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für {@link Map}, native Arrays,
	 * {@link CharSequence} und {@link Iterable} je eine eigene Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette können deren
	 * hierarchische Formatierung sowie die Erhöhung des Einzugs aktiviert werden. Sollte das Objekt eine Instanz von {@link UseToString} sein, wird das Ergebnis
	 * der {@link Object#toString()}-Methode geliefert.
	 * 
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param indent Aktivierung der Erhöhung des Einzugs.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String toString(final boolean format, final boolean indent, final Object object) {
		if (object == null) return "null";
		final String output;
		if (object.getClass().isArray()) {
			output = Objects.arrayToString(format, object);
		} else if (object instanceof UseToString) {
			output = String.valueOf(object);
		} else if (object instanceof CharSequence) {
			output = Objects.stringToString(format, (CharSequence)object);
		} else if (object instanceof Map<?, ?>) {
			output = Objects.mapToString(format, (Map<?, ?>)object);
		} else if (object instanceof Iterable<?>) {
			output = Objects.iterableToString(format, (Iterable<?>)object);
		} else {
			output = String.valueOf(object);
		}
		return (indent ? Objects.indent(output) : output);
	}

	/**
	 * Diese Methode gibt das gegebene {@link Map} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden.
	 * 
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Map} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String mapToString(final boolean format, final Map<?, ?> object) {
		if (object == null) return "null";
		if (object.isEmpty()) return "{}";
		String space = (format ? "{\n  " : "{");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		for (final Entry<?, ?> entry: object.entrySet()) {
			output.append(space).append(Objects.toString(format, format, entry.getKey())).append(": ").append(Objects.toString(format, format, entry.getValue()));
			space = comma;
		}
		return output.append((format ? "\n}" : "}")).toString();
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden.
	 * 
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt kein Array ist.
	 */
	static String arrayToString(final boolean format, final Object object) throws IllegalArgumentException {
		if (object == null) return "null";
		final int size = Array.getLength(object);
		if (size == 0) return "[]";
		String space = (format ? "[\n  " : "[");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		for (int i = 0; i < size; i++) {
			output.append(space).append(Objects.toString(format, format, Array.get(object, i)));
			space = comma;
		}
		return output.append((format ? "\n]" : "]")).toString();
	}

	/**
	 * Diese Methode gibt den gegebenen {@link String} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann
	 * deren hierarchische Formatierung aktiviert werden.
	 * 
	 * @param object {@link String} oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String stringToString(final boolean format, final CharSequence object) {
		if (object == null) return "null";
		final String space = (format ? "\\n\"+\n\"" : "\\n");
		final StringBuilder output = new StringBuilder("\"");
		int last = -1, next = 0;
		final int size = object.length();
		while (next < size) {
			switch (object.charAt(next)) {
				case '\"':
					output.append(object.subSequence(last + 1, last = next)).append("\\\"");
					break;
				case '\t':
					output.append(object.subSequence(last + 1, last = next)).append("\\t");
					break;
				case '\n':
					output.append(object.subSequence(last + 1, last = next)).append(space);
					break;
				case '\r':
					output.append(object.subSequence(last + 1, last = next)).append("\\r");
					break;
			}
			next++;
		}
		return output.append(object.subSequence(last + 1, size)).append("\"").toString();
	}

	/**
	 * Diese Methode gibt das gegebene {@link Iterable} als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann
	 * deren hierarchische Formatierung aktiviert werden.
	 * 
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Iterable} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String iterableToString(final boolean format, final Iterable<?> object) {
		if (object == null) return "null";
		final Iterator<?> iter = object.iterator();
		if (!iter.hasNext()) return "[]";
		String space = (format ? "[\n  " : "[");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		do {
			output.append(space).append(Objects.toString(format, format, iter.next()));
			space = comma;
		} while (iter.hasNext());
		return output.append((format ? "\n]" : "]")).toString();
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder {@code 0} zurück.
	 * 
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hash(final Object object) {
		return ((object == null) ? 0 : object.hashCode());
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 * 
	 * @see Objects#hash(Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hash(final Object... objects) {
		if (objects == null) return 0;
		int hash = 0x811C9DC5;
		for (final Object object: objects) {
			hash = (hash * 0x01000193) ^ Objects.hash(object);
		}
		return hash;
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 * 
	 * @see Objects#hash(Object)
	 * @see Objects#hash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hash(final Object object1, final Object object2) {
		return ((0x50C5D1F ^ Objects.hash(object1)) * 0x01000193) ^ Objects.hash(object2);
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 * 
	 * @see Objects#hash(Object)
	 * @see Objects#hash(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @param object3 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hash(final Object object1, final Object object2, final Object object3) {
		return (Objects.hash(object1, object2) * 0x01000193) ^ Objects.hash(object3);
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} verwendet.
	 * 
	 * @see Arrays#hashCode(int[])
	 * @see Arrays#hashCode(long[])
	 * @see Arrays#hashCode(byte[])
	 * @see Arrays#hashCode(char[])
	 * @see Arrays#hashCode(short[])
	 * @see Arrays#hashCode(float[])
	 * @see Arrays#hashCode(double[])
	 * @see Arrays#hashCode(boolean[])
	 * @see Objects#hashEx(Object...)
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hashEx(final Object object) {
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
		return Objects.hashEx((Object[])object);
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} verwendet.
	 * 
	 * @see Objects#hashEx(Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hashEx(final Object... objects) {
		if (objects == null) return 0;
		int hash = 0x811C9DC5;
		for (final Object object: objects) {
			hash = (hash * 0x01000193) ^ Objects.hashEx(object);
		}
		return hash;
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} verwendet.
	 * 
	 * @see Objects#hashEx(Object)
	 * @see Objects#hashEx(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hashEx(final Object object1, final Object object2) {
		return ((0x50C5D1F ^ Objects.hashEx(object1)) * 0x01000193) ^ Objects.hashEx(object2);
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück. Für Arrays werden die entsprechenden Hilfsmethoden
	 * aus der Hilfsklasse {@link Arrays} verwendet.
	 * 
	 * @see Objects#hashEx(Object)
	 * @see Objects#hashEx(Object...)
	 * @param object1 Objekt oder {@code null}.
	 * @param object2 Objekt oder {@code null}.
	 * @param object3 Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hashEx(final Object object1, final Object object2, final Object object3) {
		return (Objects.hashEx(object1, object2) * 0x01000193) ^ Objects.hashEx(object3);
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben.
	 * Verglichen werden jeweils die Objekte {@code objects[i]} und {@code objects[i+1]} der geraden Positionen {@code i} via
	 * {@link Objects#equals(Object, Object)}.
	 * 
	 * @see Objects#equals(Object, Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte.
	 */
	public static boolean equals(final Object... objects) {
		if (objects == null) return true;
		for (int i = 0, size = objects.length; i < size; i += 2) {
			if (!Objects.equals(objects[i], objects[i + 1])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben. Der Rückgabewert
	 * entspricht:
	 * 
	 * <pre>
	 * (object1 == object2) || ((object1 != null) &amp;&amp; (object2 != null) &amp;&amp; object1.equals(object2))
	 * </pre>
	 * 
	 * @param object1 Objekt 1 oder {@code null}.
	 * @param object2 Objekt 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	public static boolean equals(final Object object1, final Object object2) {
		return (object1 == object2) || ((object1 != null) && (object2 != null) && object1.equals(object2));
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Arrays zurück und tolleriert dabei {@code null}-Eingaben. Die
	 * {@link Object#equals(Object) Äquivalenz} der Elemente der {@link Array Arrays} wird via {@link Objects#equals(Object, Object)} ermittelt.
	 * 
	 * @see Objects#equals(Object, Object)
	 * @param objects1 Array 1 oder {@code null}.
	 * @param objects2 Array 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
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

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben.
	 * Verglichen werden jeweils die Objekte {@code objects[i]} und {@code objects[i+1]} der geraden Positionen {@code i} via
	 * {@link Objects#equalsEx(Object, Object)}. Für Arrays werden die entsprechenden Hilfsmethoden aus der Hilfsklasse {@link Arrays} verwendet.
	 * 
	 * @see Objects#equalsEx(Object, Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte.
	 */
	public static boolean equalsEx(final Object... objects) {
		if (objects == null) return true;
		for (int i = 0, size = objects.length; i < size; i += 2) {
			if (!Objects.equalsEx(objects[i], objects[i + 1])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei {@code null}-Eingaben und Arrays. Für
	 * Arrays werden die entsprechenden Hilfsmethoden aus der Hilfsklasse {@link Arrays} verwendet. Wenn beide Objekte keine Arrays sind, entspricht der
	 * Rückgabewert:
	 * 
	 * <pre>
	 * (object1 == object2) || ((object1 != null) &amp;&amp; (object2 != null) &amp;&amp; object1.equalsEx(object2))
	 * </pre>
	 * 
	 * @see Arrays#equals(int[], int[])
	 * @see Arrays#equals(long[], long[])
	 * @see Arrays#equals(byte[], byte[])
	 * @see Arrays#equals(char[], char[])
	 * @see Arrays#equals(short[], short[])
	 * @see Arrays#equals(float[], float[])
	 * @see Arrays#equals(double[], double[])
	 * @see Arrays#equals(boolean[], boolean[])
	 * @see Objects#equalsEx(Object[], Object[])
	 * @param object1 Objekt 1 oder {@code null}.
	 * @param object2 Objekt 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	public static boolean equalsEx(final Object object1, final Object object2) {
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
		return Objects.equalsEx((Object[])object1, (Object[])object2);
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Arrays zurück und tolleriert dabei {@code null}-Eingaben. Die
	 * {@link Object#equals(Object) Äquivalenz} der Elemente der {@link Array Arrays} wird via {@link Objects#equalsEx(Object, Object)} ermittelt. Für Arrays
	 * werden die entsprechenden Hilfsmethoden aus der Hilfsklasse {@link Arrays} verwendet.
	 * 
	 * @see Objects#equalsEx(Object, Object)
	 * @param objects1 Array 1 oder {@code null}.
	 * @param objects2 Array 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	public static boolean equalsEx(final Object[] objects1, final Object[] objects2) {
		if (objects1 == objects2) return true;
		if ((objects1 == null) || (objects2 == null)) return false;
		final int length = objects1.length;
		if (length != objects2.length) return false;
		for (int i = 0; i < length; i++) {
			if (!Objects.equalsEx(objects1[i], objects2[i])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für {@link Map}s, Arrays, {@link String}s und
	 * {@link Iterable}s je eine einene eigene Darstellungsform verwendet. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toString(false, object)
	 * </pre>
	 * 
	 * @see Objects#toString(boolean, Object)
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	public static String toString(final Object object) {
		return Objects.toString(false, object);
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für {@link Map}s, Arrays, {@link String}s und
	 * {@link Iterable}s je eine einene eigene Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung
	 * aktiviert werden. Sollte das gegebene Objekt eine Instanz von {@link UseToString} sein, wird das Ergebnis seiner {@link Object#toString() toString()}
	 * -Methode zurück gegeben.
	 * 
	 * @see Objects#toString(boolean, boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	public static String toString(final boolean format, final Object object) {
		return Objects.toString(format, false, object);
	}

	/**
	 * Diese Methode erzeugt ein neues Objekt, dessen {@link Object#toString() Textdarstelung} der via {@link Objects#toString(boolean, Object)} ermittelten
	 * {@link Object#toString() Textdarstelung} des gegebenen Objekts entspricht, und gibt es zurück.
	 * 
	 * @see Objects#toString(boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object Objekt oder {@code null}.
	 * @return {@link Objects#toString(boolean, Object)}-Objekt.
	 */
	public static Object toStringObject(final boolean format, final Object object) {
		return ((object == null) ? "null" : (format ? new FormatObject(object) : new NormalObject(object)));
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringCallFormat(false, false, name, args)
	 * </pre>
	 * 
	 * @see Objects#toStringCallFormat(boolean, boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param args Argumente.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn der gegebenen Funktionsname bzw. das gegebenen Argument-Array {@code null} ist.
	 */
	public static String toStringCall(final String name, final Object... args) throws NullPointerException {
		return Objects.toStringCallFormat(false, false, name, args);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringCallFormat(false, false, object.getClass().getSimpleName(), args)
	 * </pre>
	 * 
	 * @see #toStringCallFormat(boolean, boolean, String, Object...)
	 * @param object {@link Object}.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static String toStringCall(final Object object, final Object... args) throws NullPointerException {
		return Objects.toStringCallFormat(false, false, object.getClass().getSimpleName(), args);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Für eine bessere Lesbarkeit der Zeichenkette kann deren
	 * hierarchische Formatierung aktiviert werden. Wenn die Argumentbeschriftung aktiviert wird, werden die Argumente beschriftete Parameter interpretiert. Ein
	 * beschrifteter Parameter besteht hierbei aus einem Namen {@code args[i]} und einem Wert {@code args[i+1]} für jede gerade Position {@code i}.
	 * 
	 * @param name Funktionsname.
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn der gegebenen Funktionsname bzw. das gegebenen Argument-/Parameter-Array {@code null} ist.
	 */
	public static String toStringCallFormat(final boolean format, final boolean label, final String name, final Object... args) throws NullPointerException {
		if ((name == null) || (args == null)) throw new NullPointerException();
		final StringBuilder output = new StringBuilder(name);
		if (args.length != 0) {
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
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Objects.toStringCallFormat(format, label, object.getClass().getSimpleName(), args)
	 * </pre>
	 * 
	 * @see #toStringCallFormat(boolean, boolean, String, Object...)
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param object {@link Object}.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static String toStringCallFormat(final boolean format, final boolean label, final Object object, final Object... args) throws NullPointerException {
		if (object == null) throw new NullPointerException();
		return Objects.toStringCallFormat(format, label, object.getClass().getSimpleName(), args);
	}

	/**
	 * Diese Methode gibt das gegebenen Objekt nur dann zurück, wenn es {@code null} ist oder ein Nachfahre von {@link UseToString} ist. Andernfalls wird die
	 * Klasse des Objekts geliefert.
	 * 
	 * @see UseToString
	 * @see Object#getClass()
	 * @return {@link Object}, dass für {@link #iterator} in {@link #toString()} verwendet werden sollte.
	 */
	public static Object useObjectOrClass(Object object) {
		if (object == null || object instanceof UseToString) return object;
		return object.getClass();
	}

}