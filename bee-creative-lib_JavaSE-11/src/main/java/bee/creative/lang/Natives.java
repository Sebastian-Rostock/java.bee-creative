package bee.creative.lang;

import static bee.creative.lang.Objects.notNull;
import static java.lang.reflect.Modifier.isStatic;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import bee.creative.util.AbstractConsumer;
import bee.creative.util.AbstractField;
import bee.creative.util.AbstractGetter;
import bee.creative.util.AbstractProducer;
import bee.creative.util.AbstractProperty;
import bee.creative.util.AbstractSetter;
import bee.creative.util.Builders.MapBuilder;
import bee.creative.util.Consumer;
import bee.creative.util.Consumer3;
import bee.creative.util.Consumers;
import bee.creative.util.Field2;
import bee.creative.util.Fields;
import bee.creative.util.Getter;
import bee.creative.util.Getter3;
import bee.creative.util.Getters;
import bee.creative.util.HashMap;
import bee.creative.util.Iterables;
import bee.creative.util.Producer;
import bee.creative.util.Producer3;
import bee.creative.util.Producers;
import bee.creative.util.Properties;
import bee.creative.util.Property2;
import bee.creative.util.Setter;
import bee.creative.util.Setter3;
import bee.creative.util.Setters;

/** Diese Klasse implementiert Methoden zum Parsen von {@link Class Klassen}, {@link Field Datenfeldern}, {@link Method Methoden} und {@link Constructor
 * Konstruktoren} aus deren Textdarstellung sowie zur Erzeugung dieser Textdarstellungen.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Natives {

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
	public static Object parseNative(final String memberPath) throws NullPointerException, IllegalArgumentException {
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
		Strings.join(res, ",", Iterables.translatedIterable(Arrays.asList(types), Natives.printClass));
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

	/** Diese Methode ist eine Abkürzung für {@link #nativeSetter(String, boolean) nativeSetter(memberPath, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(String memberPath) throws NullPointerException, IllegalArgumentException {
		return nativeSetter(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code setterFromNative(parseNative(memberPath), forceAccessible)}.
	 *
	 * @see #parseNative
	 * @see #nativeSetter(java.lang.reflect.Field, boolean)
	 * @see #nativeSetter(Method, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(String memberPath, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		var object = parseNative(memberPath);
		if (object instanceof java.lang.reflect.Field) return nativeSetter((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return nativeSetter((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeSetter(java.lang.reflect.Field) setterFromNative(that, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return nativeSetter(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#setterFrom(Setter) setterFrom(fieldFromNative(that, forceAccessible))}.
	 *
	 * @see Natives#nativeField(java.lang.reflect.Field, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(java.lang.reflect.Field that, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Setters.setterFrom(Natives.nativeField(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeSetter(Method, boolean) setterFromNative(that, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(Method that) throws NullPointerException, IllegalArgumentException {
		return nativeSetter(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodSetter new MethodSetter<>(that, forceAccessible)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(Method that, boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return new MethodSetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeSetter(Class, String, boolean) setterFromNative(fieldOwner, fieldName, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(Class<? extends ITEM> fieldOwner, String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return nativeSetter(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#setterFrom(Setter) setterFromNative(fieldFromNative(fieldOwner, fieldName, forceAccessible))}.
	 *
	 * @see Natives#nativeField(Class, String, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> nativeSetter(Class<? extends ITEM> fieldOwner, String fieldName, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Setters.setterFrom(Natives.nativeField(fieldOwner, fieldName, forceAccessible));
	}

	/** Dieses Feld bildet von den Namen der primitiven Datentypen auf deren Klassen ab. */
	static final HashMap<Object, Class<?>> parseClass = MapBuilder.<Object, Class<?>>forHashMap(false)
		.putAllValues(Class::getName, byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class, void.class).get();

	/** Dieses Feld speichert den {@link Getter} zu {@link #printClass(Class)}. */
	static final Getter<Class<?>, Object> printClass = Natives::printClass;

	static String printMethod(final Class<?> methodOwner, final String methodName, final Class<?>... methodParams) throws NullPointerException {
		return Natives.printClass(methodOwner) + "." + methodName + Natives.printParams(methodParams);
	}

	/** Diese Methode ist eine Abkürzung für {@link Natives#nativeField(java.lang.reflect.Field, boolean) Fields.fromNative(that, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> nativeField(final java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeField(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link NativeField new NativeField<>(that, forceAccessible)}. */
	public static <GItem, GValue> Field2<GItem, GValue> nativeField(final java.lang.reflect.Field that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeField<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Natives#nativeField(Method, Method, boolean) Fields.fromNative(getMethod, setMethod, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> nativeField(final Method get, final Method set) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeField(get, set, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) Fields.from(Getters.fromNative(get, forceAccessible), Setters.fromNative(set,
	 * forceAccessible))}.
	 *
	 * @see Natives#nativeGetter(Method, boolean)
	 * @see #nativeSetter */
	public static <GItem, GValue> Field2<GItem, GValue> nativeField(final Method get, final Method set, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fieldFrom(Natives.<GItem, GValue>nativeGetter(get, forceAccessible), nativeSetter(set, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Natives#nativeField(Class, String, boolean) Fields.fromNative(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> nativeField(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativeField(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeField(java.lang.reflect.Field, boolean) Fields.fromNative(Natives.parseField(fieldOwner, fieldName),
	 * forceAccessible)}.
	 *
	 * @see #parseField */
	public static <GItem, GValue> Field2<GItem, GValue> nativeField(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeField(parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeGetter(String, boolean) Getters.fromNative(memberText, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeGetter(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Getters.fromNative(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see #parseNative
	 * @see Natives#nativeGetter(java.lang.reflect.Field, boolean)
	 * @see Natives#nativeGetter(Method, boolean)
	 * @see Natives#nativeGetter(Constructor, boolean) */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = parseNative(memberText);
		if (object instanceof java.lang.reflect.Field) return Natives.nativeGetter((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Natives.nativeGetter((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Natives.nativeGetter((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeGetter(java.lang.reflect.Field, boolean) Getters.fromNative(field, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeGetter(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) Getters.from(Fields.fromNative(that, forceAccessible))}.
	 *
	 * @see #nativeField */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final java.lang.reflect.Field that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Getters.getterFrom(nativeField(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeGetter(Method, boolean) Getters.fromNative(that, true)}. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> nativeGetter(final Method that) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeGetter(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodGetter new MethodGetter<>(that, forceAccessible)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final Method that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodGetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeGetter(Constructor, boolean) Getters.fromNative(that, true)}. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> nativeGetter(final Constructor<?> that) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeGetter(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConstructorGetter new ConstructorGetter<>(that, forceAccessible)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final Constructor<?> that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorGetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeGetter(Class, String, boolean) Getters.fromNative(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativeGetter(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) Getters.from(Fields.fromNative(fieldOwner, fieldName, forceAccessible))}.
	 *
	 * @see #nativeField */
	public static <GItem, GValue> Getter3<GItem, GValue> nativeGetter(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return Getters.getterFrom(nativeField(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeConsumerFrom(String, boolean) Consumers.fromNative(memberText, true)}. */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(String memberText) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeConsumerFrom(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Consumers.fromNative(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see #parseNative
	 * @see #nativeConsumerFrom(java.lang.reflect.Field, boolean)
	 * @see #nativeConsumerFrom(java.lang.reflect.Method, boolean) */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(String memberText, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = parseNative(memberText);
		if (object instanceof java.lang.reflect.Field) return Natives.nativeConsumerFrom((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Natives.nativeConsumerFrom((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeConsumerFrom(java.lang.reflect.Field, boolean) Consumers.fromNative(that, true)}. */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(java.lang.reflect.Field that) {
		return Natives.nativeConsumerFrom(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Consumer) Consumers.from(Properties.fromNative(that, forceAccessible))}.
	 *
	 * @see Natives#nativePropertyFrom(java.lang.reflect.Field, boolean) */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(java.lang.reflect.Field that, boolean forceAccessible) {
		return Consumers.consumerFrom(Natives.nativePropertyFrom(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeConsumerFrom(Method, boolean) Consumers.fromNative(that, true)}. */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(Method that) {
		return Natives.nativeConsumerFrom(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodConsumer new MethodConsumer<>(that, forceAccessible)}. */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(Method that, boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return new MethodConsumer<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeConsumerFrom(Class, String, boolean) Consumers.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(Class<?> fieldOwner, String fieldName) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeConsumerFrom(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Consumer) Consumers.from(Properties.fromNative(fieldOwner, fieldName,
	 * forceAccessible))}.
	 *
	 * @see Natives#nativePropertyFrom(Class, String, boolean) */
	public static <GValue> Consumer3<GValue> nativeConsumerFrom(Class<?> fieldOwner, String fieldName, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Consumers.consumerFrom(Natives.nativePropertyFrom(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(String, boolean) Producers.fromNative(memberPath, true)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeProducerFrom(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Producers.fromNative(Natives.parse(memberPath), forceAccessible)}.
	 *
	 * @see #parseNative
	 * @see #nativeProducerFrom(Class, boolean)
	 * @see #nativeProducerFrom(java.lang.reflect.Field, boolean)
	 * @see #nativeProducerFrom(Method, boolean)
	 * @see #nativeProducerFrom(Constructor, boolean) */
	@SuppressWarnings ("unchecked")
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = parseNative(memberPath);
		if (object instanceof Class<?>) return Natives.nativeProducerFrom((Class<VALUE>)object, forceAccessible);
		if (object instanceof java.lang.reflect.Field) return Natives.nativeProducerFrom((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Natives.nativeProducerFrom((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Natives.nativeProducerFrom((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(java.lang.reflect.Field, boolean) Producers.fromNative(that, true)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final java.lang.reflect.Field that) throws NullPointerException {
		return Natives.nativeProducerFrom(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Natives#nativePropertyFrom(java.lang.reflect.Field, boolean) Producers.from(Properties.fromNative(that,
	 * forceAccessible))}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final java.lang.reflect.Field that, final boolean forceAccessible) throws NullPointerException {
		return Producers.producerFrom(Natives.<VALUE>nativePropertyFrom(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Method, boolean) Producers.fromNative(that, true)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Method that) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeProducerFrom(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodProducer new MethodProducer<>(that, forceAccessible)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Method that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Class, boolean) Producers.fromNative(valueClass, true)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Class<? extends VALUE> valueClass) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeProducerFrom(valueClass, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Constructor, boolean) Producers.fromNative(Natives.parseConstructor(valueClass),
	 * forceAccessible)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Class<? extends VALUE> valueClass, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativeProducerFrom(parseConstructor(valueClass), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Constructor, boolean) Producers.fromNative(that, true)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Constructor<?> that) throws NullPointerException, IllegalArgumentException {
		return Natives.nativeProducerFrom(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConstructorProducer new ConstructorProducer<>(that, forceAccessible)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Constructor<?> that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorProducer<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Class, String, boolean) Producers.fromNative(fieldOwner, fieldName, true)}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativeProducerFrom(fieldOwner, fieldName, true);
	}

	/** MAMA Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Class, String, boolean) Producers.from(Properties.fromNative(fieldOwner, fieldName,
	 * forceAccessible))}. */
	public static <VALUE> Producer3<VALUE> nativeProducerFrom(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.producerFrom(Natives.<VALUE>nativePropertyFrom(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativePropertyFrom(String, boolean) Properties.fromNative(fieldPath, true)}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final String fieldPath) throws NullPointerException, IllegalArgumentException {
		return Natives.nativePropertyFrom(fieldPath, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativePropertyFrom(java.lang.reflect.Field) Properties.fromNative(Natives.parseField(fieldPath),
	 * forceAccessible)}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final String fieldPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativePropertyFrom(parseField(fieldPath), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativePropertyFrom(java.lang.reflect.Field, boolean) Properties.fromNative(field, true)}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Natives.nativePropertyFrom(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link NativeProperty new NativeProperty<>(field, forceAccessible)}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeProperty<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativePropertyFrom(Method, Method, boolean) Properties.fromNative(get, set, true)}. **/
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final Method get, final Method set) throws NullPointerException, IllegalArgumentException {
		return Natives.nativePropertyFrom(get, set, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProducerFrom(Class) Properties.from(Producers.fromNative(get, forceAccessible),
	 * Consumers.fromNative(set, forceAccessible))}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final Method get, final Method set, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.propertyFrom(nativeProducerFrom(set, forceAccessible), nativeConsumerFrom(get, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativePropertyFrom(Class, String, boolean) Properties.fromNative(fieldOwner, fieldName, true)}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativePropertyFrom(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativePropertyFrom(java.lang.reflect.Field, boolean) Properties.fromNative(Natives.parseField(fieldOwner,
	 * fieldName), forceAccessible)}. */
	public static <VALUE> Property2<VALUE> nativePropertyFrom(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Natives.nativePropertyFrom(parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} an eine gegebene {@link Method nativen Methode}
	 * delegiert. Bei einer Klassenmethode erfolgt das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} über
	 * {@link Method#invoke(Object, Object...) this.that.invoke(null, item, value)}, bei einer Objektmethode hingegen über {@link Method#invoke(Object, Object...)
	 * this.that.invoke(item, value)}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class MethodSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public MethodSetter(Method method, boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (method.getParameterTypes().length != (isStatic(method.getModifiers()) ? 2 : 1)) throw new IllegalArgumentException();
			this.method = forceAccessible ? forceAccessible(method) : notNull(method);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			try {
				if (isStatic(this.method.getModifiers())) {
					this.method.invoke(null, item, value);
				} else {
					this.method.invoke(item, value);
				}
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.method, this.forceAccessible);
		}

		private final Method method;

		private final boolean forceAccessible;

	}

	/** Diese Klasse implementiert {@link Field2}, das das {@link #get(Object) Lesen} und {@link #set(Object, Object) Schreiben} an ein gegebenes
	 * {@link java.lang.reflect.Field natives Datenfeld} delegiert. Bei einem statischen nativen Datenfeld wird der Datensatz ignoriert.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class NativeField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final java.lang.reflect.Field that;

		public final boolean forceAccessible;

		public NativeField(final java.lang.reflect.Field target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			this.that = forceAccessible ? forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				return (GValue)this.that.get(item);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				this.that.set(item, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der das {@link #get(Object) Lesen} an eine gegebene {@link Method nativen Methode} delegiert. Bei einer
	 * Klassenmethode liefert er für einen Datensatz {@code item} {@link Method#invoke(Object, Object...) this.that.invoke(null, item)}, bei einer Objektmethode
	 * dagegen {@link Method#invoke(Object, Object...) this.that.invoke(item)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	public static class MethodGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Method that;

		public final boolean forceAccessible;

		public MethodGetter(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (that.getParameterTypes().length != (Modifier.isStatic(that.getModifiers()) ? 1 : 0)) throw new IllegalArgumentException();
			this.that = forceAccessible ? forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				final GValue result;
				if (Modifier.isStatic(this.that.getModifiers())) {
					result = (GValue)this.that.invoke(null, item);
				} else {
					result = (GValue)this.that.invoke(item);
				}
				return result;
			} catch (final IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der das {@link #get(Object) Lesen} an einen gegebenen {@link Constructor nativen Kontruktor} delegiert.
	 * Für einen Datensatz {@code item} liefert er {@link Constructor#newInstance(Object...) this.that.newInstance(item)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	public static class ConstructorGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Constructor<?> that;

		public final boolean forceAccessible;

		public ConstructorGetter(final Constructor<?> that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.that = forceAccessible ? forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		public GValue get(final GItem item) {
			try {
				@SuppressWarnings ("unchecked")
				final GValue result = (GValue)this.that.newInstance(item);
				return result;
			} catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, der das {@link #set(Object) Schreiben} an eine gegebene {@link Method nativen statische Methode}
	 * delegiert. Das Schreiben des Werts {@code value} erfolgt über {@code this.that.invoke(null, value)}.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class MethodConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Method that;

		public final boolean forceAccessible;

		/** Dieser Konstruktor initialisiert Methode und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public MethodConsumer(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.that = forceAccessible ? forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		public void set(final GValue value) {
			try {
				this.that.invoke(null, value);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der das {@link #get() Lesen} an eine gegebene {@link Method nativen statische Methode} delegiert. Das
	 * Lesen erfolgt über {@code this.that.invoke(null)}.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class MethodProducer<VALUE> extends AbstractProducer<VALUE> {

		public final Method that;

		public final boolean forceAccessible;

		/** Dieser Konstruktor initialisiert Methode und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public MethodProducer(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.that = forceAccessible ? forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public VALUE get() {
			try {
				return (VALUE)this.that.invoke(null);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der das {@link #get() Lesen} an einen gegebenen {@link Method nativen statischen Konstruktor}
	 * delegiert. Das Lesen erfolgt über {@code this.that.newInstance()}.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class ConstructorProducer<VALUE> extends AbstractProducer<VALUE> {

		public final Constructor<?> that;

		public final boolean forceAccessible;

		/** Dieser Konstruktor initialisiert Konstruktor und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public ConstructorProducer(final Constructor<?> that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.that = forceAccessible ? forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public VALUE get() {
			try {
				return (VALUE)this.that.newInstance();
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert ein {@link Property2}, das das {@link #get() Lesen} und {@link #set(Object) Schreiben} an ein gegebenes
	 * {@link java.lang.reflect.Field natives statisches Datenfeld} delegiert. *
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class NativeProperty<VALUE> extends AbstractProperty<VALUE> {

		public final java.lang.reflect.Field that;

		public final boolean forceAccessible;

		/** Dieser Konstruktor initialisiert das Datenfeld und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public NativeProperty(final java.lang.reflect.Field that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (!Modifier.isStatic(that.getModifiers())) throw new IllegalArgumentException();
			this.that = forceAccessible ? forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public VALUE get() {
			try {
				return (VALUE)this.that.get(null);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final VALUE value) {
			try {
				this.that.set(null, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

}
