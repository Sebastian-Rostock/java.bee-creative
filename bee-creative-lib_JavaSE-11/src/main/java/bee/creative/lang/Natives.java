package bee.creative.lang;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import bee.creative.util.Builders.MapBuilder;
import bee.creative.util.Getter;
import bee.creative.util.HashMap;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert Methoden zum Parsen von {@link Class Klassen}, {@link Field Datenfeldern}, {@link Method Methoden} und {@link Constructor
 * Konstruktoren} aus deren Textdarstellung sowie zur Erzeugung dieser Textdarstellungen.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Natives {

	/** Dieses Feld bildet von den Namen der primitiven Datentypen auf deren Klassen ab. */
	static final HashMap<Object, Class<?>> parseClass = MapBuilder.<Object, Class<?>>forHashMap(false).putAllValues(Class::getName, byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class, void.class).get();

	/** Dieses Feld speichert den {@link Getter} zu {@link #printClass(Class)}. */
	static final Getter<Class<?>, Object> printClass = Natives::printClass;

	/** Diese Methode gibt das native Objekt zur gegebenen Pfadangabe zurück. Die Pfadangabe kodiert hierbei eine Klasse, eine Methode, einen Konstruktor oder ein
	 * Datenfeld. Die folgenden Pfadangaben werden unterstützt:
	 * <dl>
	 * <dt>{@code "CLASS_PATH.class"}</dt>
	 * <dd>Dieser Pfad wird über {@link #parseClass(String)} aufgelöst.</dd>
	 * <dt>{@code "CLASS_PATH.FIELD_NAME"}</dt>
	 * <dd>Dieser Pfad wird über {@link #parseField(String)} aufgelöst.</dd>
	 * <dt>{@code "CLASS_PATH.new(TYPE_1,...,TYPE_N)"}</dt>
	 * <dd>Dieser Pfad wird über {@link #parseConstructor(String)} aufgeföst.</dd>
	 * <dt>{@code "CLASS_PATH.METHOD_NAME(TYPE1_1,...,TYPE_N)"}</dt>
	 * <dd>Dieser Pfad wird über {@link #parseMethod(String)} aufgeföst.</dd>
	 * </dl>
	 *
	 * @see #parseClass(String)
	 * @see #parseField(String)
	 * @see #parseMethod(String)
	 * @see #parseConstructor(String)
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktors oder eines Datenfelds.
	 * @return Objekt zur Pfadangabe.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #parseClass}, {@link #parseField(String)}, {@link #parseMethod(String)} bzw. {@link #parseConstructor(String)}
	 *         eine entsprechende Ausnahme auslöst. */
	public static Object parse(final String memberPath) throws NullPointerException, IllegalArgumentException {
		if (memberPath.endsWith(".class")) return Natives.parseClass(memberPath.substring(0, memberPath.length() - 6));
		if (memberPath.contains(".new(")) return Natives.parseConstructor(memberPath);
		if (memberPath.contains("(")) return Natives.parseMethod(memberPath);
		return Natives.parseField(memberPath);
	}

	/** Diese Methode gibt das {@link Field Datenfeld} mit der gegebenen {@link #printField(Field) Textdarstellung} zurück. Das gelieferte Datenfeld entspricht
	 * {@code parseField(parseClass("CLASS_PATH"), "FIELD_NAME")}.
	 *
	 * @see #parseClass(String)
	 * @see #printField(Field)
	 * @see Class#getDeclaredField(String)
	 * @param fieldText Datenfeldtext.
	 * @return Datenfeld.
	 * @throws NullPointerException Wenn {@code fieldPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fieldPath} ungültig ist bzw. das Datenfeld nicht gefunden wurde. */
	public static Field parseField(final String fieldText) throws NullPointerException, IllegalArgumentException {
		try {
			final int pos = fieldText.lastIndexOf('.');
			return Natives.parseField(Natives.parseClass(fieldText.substring(0, pos)), fieldText.substring(pos + 1));
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt das an der gegebenen {@link Class Klasse} definierte {@link Field Datenfeld} mit dem gegebenen Signatur zurück.
	 *
	 * @see Class#getDeclaredField(String)
	 * @param fieldOwner Klasse, an der das Datenfeld definiert ist.
	 * @param fieldName Name des Datenfeldes.
	 * @return Datenfeld.
	 * @throws NullPointerException Wenn {@code fieldOwner} bzw. {@code fieldName} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Datenfeld nicht gefunden wurde. */
	public static Field parseField(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		try {
			return fieldOwner.getDeclaredField(fieldName);
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die {@link Class Klasse} mit der gegebenen {@link #printClass(Class) Textdarstellung} zurück. Die Klassentexte
	 * {@code "java.lang.Object"} und {@code "int[]"} liefert beispielsweise die Klassen {@code Object.class} bzw. {@code int[].class}.
	 *
	 * @see #printClass(Class)
	 * @param classText Klassentext.
	 * @return Klasse.
	 * @throws NullPointerException Wenn {@code classText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code classText} ungültig ist bzw. die Klasse nicht gefunden wurde. */
	public static Class<?> parseClass(final String classText) throws NullPointerException, IllegalArgumentException {
		try {
			if (classText.endsWith("[]")) return Array.newInstance(Natives.parseClass(classText.substring(0, classText.length() - 2)), 0).getClass();
			Class<?> res = Natives.parseClass.get(classText);
			if (res != null) return res;
			res = Class.forName(classText);
			return res;
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die {@link Class Parametertypen} mit der gegebenen {@link #printParams(Class...) Textdarstellung} zurück.
	 *
	 * @see #printParams(Class...)
	 * @param paramsText Parametertypentext.
	 * @return Parametertypen.
	 * @throws NullPointerException Wenn {@code paramsText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code paramsText} ungültig ist bzw. eine der Klassen nicht gefunden wurde. */
	public static Class<?>[] parseParams(final String paramsText) throws NullPointerException, IllegalArgumentException {
		try {
			int pos = paramsText.length() - 1;
			if ((paramsText.charAt(0) != '(') || (paramsText.charAt(pos) != ')')) throw new IllegalArgumentException();
			if (pos == 1) return new Class<?>[0];
			final String[] classTexts = paramsText.substring(1, pos).split(",", -1);
			pos = classTexts.length;
			final Class<?>[] res = new Class<?>[pos];
			for (int j = 0; j < pos; j++) {
				res[j] = Natives.parseClass(classTexts[j]);
			}
			return res;
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die {@link Method Methode} mit der gegebenen {@link #printMethod(Method) Textdarstellung} zurück. Das gelieferte Datenfeld entspricht
	 * {@code parseMethod(parseClass(CLASS_PATH), "METHOD_NAME", parseParams("PARAM_TYPE_1,...,PARAM_TYPE_N"))}.
	 *
	 * @see #printMethod(Method)
	 * @param methodText Methodentext.
	 * @return Methoden.
	 * @throws NullPointerException Wenn {@code methodText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code methodText} ungültig ist bzw. die Methode nicht gefunden wurde. */
	public static Method parseMethod(final String methodText) throws NullPointerException, IllegalArgumentException {
		try {
			final int pos1 = methodText.indexOf('('), pos2 = methodText.lastIndexOf('.', pos1);
			return Natives.parseMethod(Natives.parseClass(methodText.substring(0, pos2)), methodText.substring(pos2 + 1, pos1),
				Natives.parseParams(methodText.substring(pos1)));
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die an der gegebenen {@link Class Klasse} definierte {@link Method Methode} mit der gegebenen Signatur zurück.
	 *
	 * @see Class#getDeclaredField(String)
	 * @param methodOwner Klasse, an der die Methode definiert ist.
	 * @param methodName Name der Methode.
	 * @param methodParams Parametertypen der Methode.
	 * @return Methode.
	 * @throws NullPointerException Wenn {@code methodOwner} bzw. {@code methodName} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Methode nicht gefunden wurde. */
	public static Method parseMethod(final Class<?> methodOwner, final String methodName, final Class<?>... methodParams)
		throws NullPointerException, IllegalArgumentException {
		try {
			return methodOwner.getDeclaredMethod(methodName, methodParams);
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt den {@link Constructor Konstruktor} mit der gegebenen {@link #printConstructor(Constructor) Textdarstellung} zurück. Das gelieferte
	 * Datenfeld entspricht {@code parseConstructor(parseClass(CLASS_PATH), parseParams("PARAM_TYPE_1,...,PARAM_TYPE_N"))}.
	 *
	 * @see #printConstructor(Constructor)
	 * @param constructorText Konstruktortext.
	 * @return Konstruktor.
	 * @throws NullPointerException Wenn {@code constructorText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code constructorText} ungültig ist bzw. der Konstruktor nicht gefunden wurde. */
	public static Constructor<?> parseConstructor(final String constructorText) throws NullPointerException, IllegalArgumentException {
		try {
			final int pos = constructorText.indexOf(".new(");
			return Natives.parseConstructor(Natives.parseClass(constructorText.substring(0, pos)), Natives.parseParams(constructorText.substring(pos + 4)));
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt den an der gegebenen {@link Class Klasse} definierte {@link Constructor Konstruktor} mit der gegebenen Signatur zurück.
	 *
	 * @see Class#getDeclaredConstructor(Class...)
	 * @param constructorOwner Klasse, an der der Konstruktor definiert ist.
	 * @param constructorParams Parametertypen des Konstruktors.
	 * @return Konstruktor.
	 * @throws NullPointerException Wenn {@code constructorOwner} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Konstruktor nicht gefunden wurde. */
	public static Constructor<?> parseConstructor(final Class<?> constructorOwner, final Class<?>... constructorParams)
		throws NullPointerException, IllegalArgumentException {
		try {
			return constructorOwner.getDeclaredConstructor(constructorParams);
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen {@link Field Datenfelds} zurück. Das Format der Textdarstellung ist
	 * {@code "}{@link #printClass(Class) CLASS_PATH}{@code .}{@link Field#getName() FIELD_NAME}{@code "}.
	 *
	 * @see Field#getName()
	 * @see #printClass(Class)
	 * @param field Datenfeld.
	 * @return Datenfeldtext.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static String printField(final Field field) throws NullPointerException {
		return Natives.printClass(field.getDeclaringClass()) + "." + field.getName();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen {@link Class Klasse} zurück. Diese wird über {@link Class#getName()} und {@link Class#isArray()}
	 * ermittelt. Die Klassen {@code Object.class} und {@code int[].class} liefert beispielsweise {@code "java.lang.Object"} bzw. {@code "int[]"}.
	 *
	 * @param clazz Klasse.
	 * @return Klassentext.
	 * @throws NullPointerException Wenn {@code clazz} {@code null} ist. */
	public static String printClass(final Class<?> clazz) throws NullPointerException {
		return clazz.isArray() ? Natives.printClass(clazz.getComponentType()) + "[]" : clazz.getName();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen {@link Class Parametertypen} zurück. Das Format der Textdarstellung ist
	 * {@code "(}{@link #printClass(Class) PARAM_TYPE_1}{@code ,...,}{@link #printClass(Class) PARAM_TYPE_N}{@code )"}.
	 *
	 * @see Method#getParameterTypes()
	 * @see Constructor#getParameterTypes()
	 * @param types Parametertypen.
	 * @return Parametertypentext.
	 * @throws NullPointerException Wenn {@code types} {@code null} ist oder enthält. */
	public static String printParams(final Class<?>... types) throws NullPointerException {
		final StringBuilder res = new StringBuilder().append('(');
		Strings.join(res, ",", Iterables.translate(Arrays.asList(types), Natives.printClass));
		return res.append(')').toString();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen {@link Method Methode} zurück. Das Format der Textdarstellung ist {@code "}{@link #printClass(Class)
	 * CLASS_PATH}{@code .}{@link Method#getName() METHOD_NAME} {@link #printParams(Class...) (PARAM_TYPE_1,...,PARAM_TYPE_N)}{@code "}.
	 *
	 * @see Method#getName()
	 * @see #printClass(Class)
	 * @see #printParams(Class...)
	 * @param method Methode.
	 * @return Methodentext.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static String printMethod(final Method method) throws NullPointerException {
		return Natives.printMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen {@link Constructor Konstruktors} zurück. Das Format der Textdarstellung ist
	 * {@code "}{@link #printClass(Class) CLASS_PATH}{@code .new}{@link #printParams(Class...) (PARAM_TYPE_1,...,PARAM_TYPE_N)}{@code "}.
	 *
	 * @param constructor Konstruktor.
	 * @return Konstruktortext.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static String printConstructor(final Constructor<?> constructor) throws NullPointerException {
		return Natives.printMethod(constructor.getDeclaringClass(), "new", constructor.getParameterTypes());
	}

	static String printMethod(final Class<?> methodOwner, final String methodName, final Class<?>... methodParams) throws NullPointerException {
		return Natives.printClass(methodOwner) + "." + methodName + Natives.printParams(methodParams);
	}

	/** Diese Methode erzwingt die {@link AccessibleObject#setAccessible(boolean) Zugreifbarkeit} des gegebenen Objekts und gibt es zurück.
	 *
	 * @param result Objekt mit Zugreifbarkeit.
	 * @return zugreifbares Objekt.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Objekt nicht zugrifbar ist. */
	public static <GResult extends AccessibleObject> GResult forceAccessible(final GResult result) throws NullPointerException, IllegalArgumentException {
		try {
			result.setAccessible(true);
			return result;
		} catch (final SecurityException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

}
