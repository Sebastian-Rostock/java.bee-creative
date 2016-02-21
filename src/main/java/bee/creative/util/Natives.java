package bee.creative.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Diese Klasse implementiert Methoden zum Parsen von {@link Class Klassen}, {@link Field Datenfeldern}, {@link Method Methoden} und {@link Constructor
 * Konstruktoren} aus deren Textdarstellung sowie zur Erzeugung dieser Textdarstellungen.
 * 
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Natives {

	/** Dieses Feld bildet von den Namen der primitiven Datentypen auf deren Klassen ab. */
	static final Map<String, Class<?>> _parseClass_ = new HashMap<>(9);

	/** Dieses Feld speichert den {@link Converter} zu {@link #formatClass(Class)}. */
	static final Converter<Class<?>, Object> _formatClass_ = new Converter<Class<?>, Object>() {

		@Override
		public Object convert(final Class<?> input) {
			return Natives.formatClass(input);
		}

	};

	static {
		final Class<?>[] classes = {byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class, void.class};
		for (final Class<?> clazz: classes) {
			Natives._parseClass_.put(clazz.getName(), clazz);
		}
	}

	/** Diese Methode gibt das {@link Field Datenfeld} mit der gegebenen {@link #formatField(Field) Textdarstellung} zurück.
	 * 
	 * @see #formatField(Field)
	 * @param fieldPath Datenfeldtext.
	 * @return Datenfeld.
	 * @throws NullPointerException Wenn {@code fieldPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fieldPath} ungültig ist bzw. das Datenfeld nicht gefunden wurde. */
	public static final Field parseField(final String fieldPath) throws NullPointerException, IllegalArgumentException {
		try {
			final int i = fieldPath.lastIndexOf('.');
			return Natives.parseField(Natives.parseClass(fieldPath.substring(0, i)), fieldPath.substring(i + 1));
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
	public static final Field parseField(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		try {
			return fieldOwner.getDeclaredField(fieldName);
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die {@link Class Klasse} mit der gegebenen {@link #formatClass(Class) Textdarstellung} zurück.<br>
	 * Die Klassentexte {@code "java.lang.Object"} und {@code "int[]"} liefert beispielsweise die Klassen {@code Object.class} bzw. {@code int[].class}.
	 * 
	 * @see #formatClass(Class)
	 * @param classText Klassentext.
	 * @return Klasse.
	 * @throws NullPointerException Wenn {@code classText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code classText} ungültig ist bzw. die Klasse nicht gefunden wurde. */
	public static final Class<?> parseClass(final String classText) throws NullPointerException, IllegalArgumentException {
		try {
			if (classText.endsWith("[]")) return Array.newInstance(Natives.parseClass(classText.substring(0, classText.length() - 2)), 0).getClass();
			Class<?> result = Natives._parseClass_.get(classText);
			if (result != null) return result;
			result = Class.forName(classText);
			return result;
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die {@link Class Parametertypen} mit der gegebenen {@link #formatParams(Class...) Textdarstellung} zurück.
	 * 
	 * @see #formatParams(Class...)
	 * @param paramsText Parametertypentext.
	 * @return Parametertypen.
	 * @throws NullPointerException Wenn {@code paramsText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code paramsText} ungültig ist bzw. eine der Klassen nicht gefunden wurde. */
	public static final Class<?>[] parseParams(final String paramsText) throws NullPointerException, IllegalArgumentException {
		try {
			int i = paramsText.length() - 1;
			if ((paramsText.charAt(0) != '(') || (paramsText.charAt(i) != ')')) throw new IllegalArgumentException();
			if (i == 1) return new Class<?>[0];
			final String[] classTexts = paramsText.substring(1, i).split(",", -1);
			i = classTexts.length;
			final Class<?>[] result = new Class<?>[i];
			for (int j = 0; j < i; j++) {
				result[j] = Natives.parseClass(classTexts[j]);
			}
			return result;
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die {@link Method Methode} mit der gegebenen {@link #formatMethod(Method) Textdarstellung} zurück.
	 * 
	 * @see #formatMethod(Method)
	 * @param methodText Methodentext.
	 * @return Methoden.
	 * @throws NullPointerException Wenn {@code methodText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code methodText} ungültig ist bzw. die Methode nicht gefunden wurde. */
	public static final Method parseMethod(final String methodText) throws NullPointerException, IllegalArgumentException {
		try {
			final int i = methodText.indexOf('('), j = methodText.lastIndexOf('.', i);
			return Natives.parseMethod(Natives.parseClass(methodText.substring(0, j)), methodText.substring(j + 1, i), Natives.parseParams(methodText.substring(i)));
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
	public static final Method parseMethod(final Class<?> methodOwner, final String methodName, final Class<?>... methodParams) throws NullPointerException,
		IllegalArgumentException {
		try {
			return methodOwner.getDeclaredMethod(methodName, methodParams);
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt den {@link Constructor Konstruktor} mit der gegebenen {@link #formatConstructor(Constructor) Textdarstellung} zurück.
	 * 
	 * @see #formatConstructor(Constructor)
	 * @param constructorText Konstruktortext.
	 * @return Konstruktor.
	 * @throws NullPointerException Wenn {@code constructorText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code constructorText} ungültig ist bzw. der Konstruktor nicht gefunden wurde. */
	public static final Constructor<?> parseConstructor(final String constructorText) throws NullPointerException, IllegalArgumentException {
		try {
			final int i = constructorText.indexOf(".new(");
			return Natives.parseConstructor(Natives.parseClass(constructorText.substring(0, i)), Natives.parseParams(constructorText.substring(i + 4)));
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
	public static final Constructor<?> parseConstructor(final Class<?> constructorOwner, final Class<?>... constructorParams) throws NullPointerException,
		IllegalArgumentException {
		try {
			return constructorOwner.getDeclaredConstructor(constructorParams);
		} catch (NullPointerException | IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen {@link Field Datenfelds} zurück.<br>
	 * Das Format der Textdarstellung ist {@code "}{@link #formatClass(Class) CLASS_PATH}{@code .}{@link Field#getName() FIELD_NAME}{@code "}.
	 * 
	 * @see Field#getName()
	 * @see #formatClass(Class)
	 * @param field Datenfeld.
	 * @return Datenfeldtext.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static final String formatField(final Field field) throws NullPointerException {
		return Natives.formatClass(field.getDeclaringClass()) + "." + field.getName();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen {@link Class Klasse} zurück.<br>
	 * Diese wird via {@link Class#getCanonicalName()} ermittelt. Die Klassen {@code Object.class} und {@code int[].class} liefert beispielsweise
	 * {@code "java.lang.Object"} bzw. {@code "int[]"}.
	 * 
	 * @see Class#getCanonicalName()
	 * @param clazz Klasse.
	 * @return Klassentext.
	 * @throws NullPointerException Wenn {@code clazz} {@code null} ist. */
	public static final String formatClass(final Class<?> clazz) throws NullPointerException {
		return clazz.getCanonicalName();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen {@link Class Parametertypen} zurück.<br>
	 * Das Format der Textdarstellung ist {@code "(}{@link #formatClass(Class) PARAM_TYPE_1}{@code ,...,}{@link #formatClass(Class) PARAM_TYPE_N}{@code )"}.
	 * 
	 * @see Method#getParameterTypes()
	 * @see Constructor#getParameterTypes()
	 * @param types Parametertypen.
	 * @return Parametertypentext.
	 * @throws NullPointerException Wenn {@code types} {@code null} ist oder enthält. */
	public static final String formatParams(final Class<?>... types) throws NullPointerException {
		return "(" + Strings.join(",", Iterables.convertedIterable(Natives._formatClass_, Arrays.asList(types))) + ")";
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen {@link Method Methode} zurück.<br>
	 * Das Format der Textdarstellung ist {@code "}{@link #formatClass(Class) CLASS_PATH}{@code .}{@link Method#getName() METHOD_NAME}
	 * {@link #formatParams(Class...) (PARAM_TYPE_1,...,PARAM_TYPE_N)}{@code "}.
	 * 
	 * @see Method#getName()
	 * @see #formatClass(Class)
	 * @see #formatParams(Class...)
	 * @param method Methode.
	 * @return Methodentext.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static final String formatMethod(final Method method) throws NullPointerException {
		return Natives._formatMethod_(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
	}

	/** /** Diese Methode gibt die Textdarstellung des gegebenen {@link Constructor Konstruktors} zurück.<br>
	 * Das Format der Textdarstellung ist {@code "}{@link #formatClass(Class) CLASS_PATH}{@code .new}{@link #formatParams(Class...)
	 * (PARAM_TYPE_1,...,PARAM_TYPE_N)}{@code "}.
	 * 
	 * @param constructor Konstruktor.
	 * @return Konstruktortext.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static final String formatConstructor(final Constructor<?> constructor) throws NullPointerException {
		return Natives._formatMethod_(constructor.getDeclaringClass(), "new", constructor.getParameterTypes());
	}

	@SuppressWarnings ("javadoc")
	static final String _formatMethod_(final Class<?> methodOwner, final String methodName, final Class<?>... methodParams) throws NullPointerException {
		return Natives.formatClass(methodOwner) + "." + methodName + Natives.formatParams(methodParams);
	}

}
