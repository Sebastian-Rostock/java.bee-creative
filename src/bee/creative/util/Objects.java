package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Berechnung von {@link Object#hashCode() Streuwerten},
 * {@link Object#equals(Object) Äquivalenzen} und {@link Object#toString() Textdarstelungen}.
 * 
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @see Object#toString()
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Objects {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dessen {@link Object#toString() Textdarstelung} der via
	 * {@link Objects#toString(boolean, Object)} ermittelten {@link Object#toString() Textdarstelung} des gegebenen
	 * Objekts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class BaseObject {

		/**
		 * Dieses Feld speichert das Objekt.
		 */
		final Object object;

		/**
		 * Dieser Konstrukteur initialisiert das Objekt.
		 * 
		 * @param object Objekt.
		 */
		public BaseObject(final Object object) {
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BaseObject data = (BaseObject)object;
			return Objects.equals(this.object, data.object);
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt, dessen {@link Object#toString() Textdarstelung} der via
	 * {@link Objects#toString(boolean, Object)} ermittelten {@link Object#toString() Textdarstelung} eines gegebenen
	 * Objekts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class NormalObject extends BaseObject {

		/**
		 * Dieser Konstrukteur initialisiert das Objekt.
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
			if(object == this) return true;
			if(!(object instanceof NormalObject)) return false;
			return super.equals(object);
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
	 * Diese Klasse implementiert ein Objekt, dessen {@link Object#toString() Textdarstelung} der via
	 * {@link Objects#toString(boolean, Object)} ermittelten formatierten {@link Object#toString() Textdarstelung} eines
	 * gegebenen Objekts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class FormatObject extends BaseObject {

		/**
		 * Dieser Konstrukteur initialisiert das Objekt.
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
			if(object == this) return true;
			if(!(object instanceof FormatObject)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.object);
		}

	}

	/**
	 * Diese Schnittstelle definiert eine Markierung für die Methode {@link Objects#toString(boolean, boolean, Object)},
	 * sodass diese für Objekte mit dieser Schnittstelle den Rückgabewert derer {@link Object#toString()}-Methode
	 * verwendet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface UseToString {
	}

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe via
	 * {@link Objects#toString(boolean, Object)} in eine {@link Object#toString() Textdarstelung} umwandelt.
	 */
	static final Converter<Object, String> NORMAL_STRING_CONVERTER = new Converter<Object, String>() {

		@Override
		public String convert(final Object input) {
			return Objects.toString(false, input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("toStringConverter", false);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe via
	 * {@link Objects#toString(boolean, Object)} in eine formatierte {@link Object#toString() Textdarstelung} umwandelt.
	 */
	static final Converter<Object, String> FORMAT_STRING_CONVERTER = new Converter<Object, String>() {

		@Override
		public String convert(final Object input) {
			return Objects.toString(false, input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("toStringConverter", true);
		}

	};

	/**
	 * Diese Methode gibt die gegebenen Zeichenkette mit erhöhtem Einzug zurück. Dazu wird jedes Vorkommen von
	 * {@code "\n"} durch {@code "\n&nbsp;&nbsp;"} ersetzt.
	 * 
	 * @param value Zeichenkette.
	 * @return Zeichenkette mit erhöhtem Einzug.
	 */
	static String indent(final String value) {
		if(value == null) return "null";
		final StringBuilder output = new StringBuilder();
		int last = -1;
		final int size = value.length();
		for(int next = 0; next < size; next++)
			if(value.charAt(next) == '\n'){
				output.append(value.substring(last + 1, last = next)).append("\n  ");
			}
		return output.append(value.substring(last + 1, size)).toString();
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für
	 * {@link Map Maps}, {@link Array Arrays}, {@link String Strings} und {@link Iterable Iterables} je eine einene eigene
	 * Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette können deren hierarchische Formatierung
	 * sowie die Erhöhung des Einzugs aktiviert werden. Sollte das Objekt eine Instanz von {@link UseToString UseToString}
	 * sein, wird das ergenis {@link Object#toString() toString()}-Methode zurück gegeben.
	 * 
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param indent Aktivierung der Erhöhung des Einzugs.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String toString(final boolean format, final boolean indent, final Object object) {
		if(object == null) return "null";
		final String output;
		if(object.getClass().isArray()){
			output = Objects.arrayToString(format, object);
		}else if(object instanceof String){
			output = Objects.stringToString(format, (String)object);
		}else if(object instanceof UseToString){
			output = String.valueOf(object);
		}else if(object instanceof Map<?, ?>){
			output = Objects.mapToString(format, (Map<?, ?>)object);
		}else if(object instanceof Iterable<?>){
			output = Objects.iterableToString(format, (Iterable<?>)object);
		}else{
			output = String.valueOf(object);
		}
		return (indent ? Objects.indent(output) : output);
	}

	/**
	 * Diese Methode gibt das gegebene {@link Map Map} als {@link Object#toString() Textdarstelung} zurück. Für eine
	 * bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * 
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Map Map} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String mapToString(final boolean format, final Map<?, ?> object) {
		if(object == null) return "null";
		if(object.isEmpty()) return "{}";
		String space = (format ? "{\n  " : "{ ");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		for(final Entry<?, ?> entry: object.entrySet()){
			output.append(space).append(Objects.toString(format, format, entry.getKey())).append(": ")
				.append(Objects.toString(format, format, entry.getValue()));
			space = comma;
		}
		return output.append((format ? "\n}" : " }")).toString();
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Für eine bessere
	 * Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * 
	 * @param object Objekt oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt kein {@link Array Array} ist.
	 */
	static String arrayToString(final boolean format, final Object object) throws IllegalArgumentException {
		if(object == null) return "null";
		final int size = Array.getLength(object);
		if(size == 0) return "[]";
		String space = (format ? "[\n  " : "[ ");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		for(int i = 0; i < size; i++){
			output.append(space).append(Objects.toString(format, format, Array.get(object, i)));
			space = comma;
		}
		return output.append((format ? "\n]" : " ]")).toString();
	}

	/**
	 * Diese Methode gibt den gegebenen {@link String String} als {@link Object#toString() Textdarstelung} zurück. Für
	 * eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * 
	 * @param object {@link String String} oder {@code null}.
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String stringToString(final boolean format, final String object) {
		if(object == null) return "null";
		final String space = (format ? "\\n\"+\n\"" : "\\n");
		final StringBuilder output = new StringBuilder("\"");
		int last = -1, next = 0;
		final int size = object.length();
		while(next < size){
			switch(object.charAt(next)){
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
	 * Diese Methode gibt das gegebene {@link Iterable Iterable} als {@link Object#toString() Textdarstelung} zurück. Für
	 * eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden.
	 * 
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @param object {@link Iterable Iterable} oder {@code null}.
	 * @return {@link Object#toString() Textdarstelung}.
	 */
	static String iterableToString(final boolean format, final Iterable<?> object) {
		if(object == null) return "null";
		final Iterator<?> iter = object.iterator();
		if(!iter.hasNext()) return "[]";
		String space = (format ? "[\n  " : "[ ");
		final String comma = (format ? ",\n  " : ", ");
		final StringBuilder output = new StringBuilder();
		do{
			output.append(space).append(Objects.toString(format, format, iter.next()));
			space = comma;
		}while(iter.hasNext());
		return output.append((format ? "\n]" : " ]")).toString();
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts oder {@code 0} zurück.
	 * 
	 * @see Arrays#hashCode(int[])
	 * @see Arrays#hashCode(long[])
	 * @see Arrays#hashCode(byte[])
	 * @see Arrays#hashCode(char[])
	 * @see Arrays#hashCode(short[])
	 * @see Arrays#hashCode(float[])
	 * @see Arrays#hashCode(double[])
	 * @see Arrays#hashCode(boolean[])
	 * @see Objects#hash(Object...)
	 * @param object Objekt oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hash(final Object object) {
		if(object == null) return 0;
		final Class<?> clazz = object.getClass();
		if(!clazz.isArray()) return object.hashCode();
		if(clazz == int[].class) return Arrays.hashCode((int[])object);
		if(clazz == long[].class) return Arrays.hashCode((long[])object);
		if(clazz == byte[].class) return Arrays.hashCode((byte[])object);
		if(clazz == char[].class) return Arrays.hashCode((char[])object);
		if(clazz == short[].class) return Arrays.hashCode((short[])object);
		if(clazz == float[].class) return Arrays.hashCode((float[])object);
		if(clazz == double[].class) return Arrays.hashCode((double[])object);
		if(clazz == boolean[].class) return Arrays.hashCode((boolean[])object);
		return Objects.hash((Object[])object);
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Objekte oder {@code 0} zurück.
	 * 
	 * @see Objects#hash(Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} oder {@code 0}.
	 */
	public static int hash(final Object... objects) {
		if(objects == null) return 0;
		int hash = 1;
		for(final Object object: objects){
			hash = (31 * hash) + Objects.hash(object);
		}
		return hash;
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte zurück und
	 * tolleriert dabei {@code null}-Eingaben. Verglichen werden jeweils die Objekte {@code objects[i]} und
	 * {@code objects[i+1]} der geraden Positionen {@code i} via {@link Objects#equals(Object, Object)}.
	 * 
	 * @see Objects#equals(Object, Object)
	 * @param objects Objekte oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der in Paaren gegebenen Objekte.
	 */
	public static boolean equals(final Object... objects) {
		if(objects == null) return true;
		for(int i = 0, size = objects.length; i < size; i += 2){
			if(!Objects.equals(objects[i], objects[i + 1])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück und tolleriert dabei
	 * {@code null}-Eingaben und {@link Array Arrays}. Wenn beide Objekte keine {@link Array Arrays} sind, entspricht der
	 * Rückgabewert:
	 * 
	 * <pre>(object1 == object2) || ((object1 != null) &amp;&amp; (object2 != null) &amp;&amp; object1.equals(object2))</pre>
	 * 
	 * @see Arrays#equals(int[], int[])
	 * @see Arrays#equals(long[], long[])
	 * @see Arrays#equals(byte[], byte[])
	 * @see Arrays#equals(char[], char[])
	 * @see Arrays#equals(short[], short[])
	 * @see Arrays#equals(float[], float[])
	 * @see Arrays#equals(double[], double[])
	 * @see Arrays#equals(boolean[], boolean[])
	 * @see Objects#equals(Object[], Object[])
	 * @param object1 Objekt 1 oder {@code null}.
	 * @param object2 Objekt 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	public static boolean equals(final Object object1, final Object object2) {
		if(object1 == object2) return true;
		if((object1 == null) || (object2 == null)) return false;
		final Class<?> c1 = object1.getClass();
		if(!c1.isArray()) return !object2.getClass().isArray() && object1.equals(object2);
		final Class<?> c2 = object2.getClass();
		if(!c2.isArray()) return false;
		if(c1 == int[].class) return (c2 == int[].class) && Arrays.equals((int[])object1, (int[])object2);
		if(c1 == long[].class) return (c2 == long[].class) && Arrays.equals((long[])object1, (long[])object2);
		if(c1 == byte[].class) return (c2 == byte[].class) && Arrays.equals((byte[])object1, (byte[])object2);
		if(c1 == char[].class) return (c2 == char[].class) && Arrays.equals((char[])object1, (char[])object2);
		if(c1 == short[].class) return (c2 == short[].class) && Arrays.equals((short[])object1, (short[])object2);
		if(c1 == float[].class) return (c2 == float[].class) && Arrays.equals((float[])object1, (float[])object2);
		if(c1 == double[].class) return (c2 == double[].class) && Arrays.equals((double[])object1, (double[])object2);
		if(c1 == boolean[].class) return (c2 == boolean[].class) && Arrays.equals((boolean[])object1, (boolean[])object2);
		return Objects.equals((Object[])object1, (Object[])object2);
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen {@link Array Arrays} zurück und
	 * tolleriert dabei {@code null}-Eingaben. Die {@link Object#equals(Object) Äquivalenz} der Elemente der {@link Array
	 * Arrays} wird via {@link Objects#equals(Object, Object)} ermittelt.
	 * 
	 * @see Objects#equals(Object, Object)
	 * @param objects1 {@link Array Array} 1 oder {@code null}.
	 * @param objects2 {@link Array Array} 2 oder {@code null}.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte.
	 */
	public static boolean equals(final Object[] objects1, final Object[] objects2) {
		if(objects1 == objects2) return true;
		if((objects1 == null) || (objects2 == null)) return false;
		final int length = objects1.length;
		if(length != objects2.length) return false;
		for(int i = 0; i < length; i++){
			if(!Objects.equals(objects1[i], objects2[i])) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für
	 * {@link Map Maps}, {@link Array Arrays}, {@link String Strings} und {@link Iterable Iterables} je eine einene eigene
	 * Darstellungsform verwendet. Der Rückgabewert entspricht:
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
	 * Diese Methode gibt das gegebene Objekt als {@link Object#toString() Textdarstelung} zurück. Hierbei wird für
	 * {@link Map Maps}, {@link Array Arrays}, {@link String Strings} und {@link Iterable Iterables} je eine einene eigene
	 * Darstellungsform verwendet. Für eine bessere Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung
	 * aktiviert werden. Sollte das gegebene Objekt eine Instanz von {@link UseToString UseToString} sein, wird das
	 * Ergebnis seiner {@link Object#toString() toString()}-Methode zurück gegeben.
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
	 * Diese Methode erzeugt ein neues Objekt, dessen {@link Object#toString() Textdarstelung} der via
	 * {@link Objects#toString(boolean, Object)} ermittelten {@link Object#toString() Textdarstelung} des gegebenen
	 * Objekts entspricht, und gibt es zurück.
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
	 * Diese Methode gibt einen {@link Converter Converter} zurück, der seine Eingabe via
	 * {@link Objects#toString(boolean, Object)} in eine {@link Object#toString() Textdarstelung} umwandelt.
	 * 
	 * @see Converter
	 * @see Objects#toString(boolean, Object)
	 * @param format Aktivierung der hierarchische Formatierung.
	 * @return {@link Objects#toString(boolean, Object)}-{@link Converter Converter}.
	 */
	public static Converter<Object, String> toStringConverter(final boolean format) {
		return (format ? Objects.FORMAT_STRING_CONVERTER : Objects.NORMAL_STRING_CONVERTER);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Der Rückgabewert
	 * entspricht:
	 * 
	 * <pre>Objects.toStringCall(false, name, args)</pre>
	 * 
	 * @see Objects#toStringCall(boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param args Argumente.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn der gegebenen Funktionsname bzw. das gegebenen Argument-Array {@code null} ist.
	 */
	public static String toStringCall(final String name, final Object... args) throws NullPointerException {
		return Objects.toStringCall(false, name, args);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Für eine bessere
	 * Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden. Der Rückgabewert entspricht:
	 * 
	 * <pre>Objects.toStringCall(format, false, name, args)</pre>
	 * 
	 * @see Objects#toStringCall(boolean, boolean, String, Object...)
	 * @param name Funktionsname.
	 * @param format Formatiermodus.
	 * @param args Argumente.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn der gegebenen Funktionsname bzw. das gegebenen Argument-Array {@code null} ist.
	 */
	public static String toStringCall(final boolean format, final String name, final Object... args)
		throws NullPointerException {
		return Objects.toStringCall(format, false, name, args);
	}

	/**
	 * Diese Methode gibt einen Funktionsaufruf als {@link Object#toString() Textdarstelung} zurück. Für eine bessere
	 * Lesbarkeit der Zeichenkette kann deren hierarchische Formatierung aktiviert werden. Wenn die Argumentbeschriftung
	 * aktiviert wird, werden die Argumente beschriftete Parameter interpretiert. Ein beschrifteter Parameter besteht
	 * hierbei aus einem Namen {@code args[i]} und einem Wert {@code args[i+1]} für jede gerade Position {@code i}.
	 * 
	 * <pre>TODO beeispiele</pre>
	 * 
	 * @param name Funktionsname.
	 * @param format Formatiermodus.
	 * @param label Aktivierung der Argumentbeschriftung.
	 * @param args Argumente bzw. Parameter.
	 * @return {@link Object#toString() Textdarstelung}.
	 * @throws NullPointerException Wenn der gegebenen Funktionsname bzw. das gegebenen Argument-/Parameter-Array
	 *         {@code null} ist.
	 */
	public static String toStringCall(final boolean format, final boolean label, final String name, final Object... args)
		throws NullPointerException {
		if((name == null) || (args == null)) throw new NullPointerException();
		final StringBuilder output = new StringBuilder(name);
		if(args.length != 0){
			String join = (format ? "(\n  " : "( ");
			final String comma = (format ? ",\n  " : ", ");
			if(label){
				for(int i = 0, size = args.length - 1; i < size; i += 2){
					output.append(join).append(Objects.toString(format, format, args[i])).append(" = ")
						.append(Objects.toString(format, format, args[i + 1]));
					join = comma;
				}
			}else{
				for(int i = 0, size = args.length; i < size; i++){
					output.append(join).append(Objects.toString(format, format, args[i]));
					join = comma;
				}
			}
			output.append((format ? "\n)" : " )"));
		}else{
			output.append("()");
		}
		return output.toString();
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Objects() {
	}

}
