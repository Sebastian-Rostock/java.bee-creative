package bee.creative.emu;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/** Diese Klasse implementiert Methoden zur Schätzung des Speicherverbrauchs von Objekten (<i>Estimated Memory Usage</i>).
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("rawtypes")
public class EMU {

	/** Dieses Feld speichert den {@link Emuator} zu {@link #fromClass(Class)}. */
	private static final Emuator<?> defaultEmuator = new Emuator<Object>() {

		@Override
		public long emu(final Object input) throws NullPointerException {
			return EMU.fromClass(input.getClass());
		}

	};

	/** Dieses Feld speichert die in {@link #use(Class, Emuator)} registrierten {@link Emuator}. */
	private static final Map<Class<?>, Emuator<?>> emuatorMap = new bee.creative.util.HashMap<>();

	/** Dieses Feld speichert die in {@link #from(Object)} gepufferten {@link Emuator}. */
	private static final Map<Class<?>, Emuator<?>> emuatorCache = new bee.creative.util.HashMap<>();

	static {
		EMU.use(String.class, new Emuator<String>() {

			@Override
			public long emu(final String input) throws NullPointerException {
				return EMU.fromObject(input) + EMU.fromArray(char.class, input.length());
			}

		});
		EMU.use(ArrayList.class, new Emuator<ArrayList>() {

			@Override
			public long emu(final ArrayList input) throws NullPointerException {
				return EMU.fromObject(input) + EMU.fromArray(Object.class, input.size());
			}

		});
		EMU.use(LinkedList.class, new Emuator<LinkedList>() {

			@Override
			public long emu(final LinkedList input) throws NullPointerException {
				return EMU.fromObject(input) + (24 * input.size());
			}

		});
		EMU.use(HashSet.class, new Emuator<HashSet>() {

			@Override
			public long emu(final HashSet input) throws NullPointerException {
				return EMU.fromObject(input) + EMU.fromClass(HashMap.class) + (36 * input.size());
			}

		});
		EMU.use(HashMap.class, new Emuator<HashMap>() {

			@Override
			public long emu(final HashMap input) throws NullPointerException {
				return EMU.fromClass(HashMap.class) + (36 * input.size());
			}

		});
	}

	/** Diese Methode registriert den gegebenen {@link Emuator} zur Schätzung des Speicherverbrauchs der Instanzen der gegebenen Klasse in {@link #from(Object)}.
	 * Wenn der {@link Emuator} {@code null} ist, wird die Sonderbehandlung für die Instanzen der gegebenen Klasse aufgehoben.
	 *
	 * @param <GItem> Typ der Instanzen.
	 * @param clazz Klasse der Instanzen.
	 * @param emuator {@link Emuator} oder {@code null}.
	 * @throws NullPointerException Wenn {@code clazz} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Klasse primitiv oder ein Array ist. */
	public static <GItem> void use(final Class<GItem> clazz, final Emuator<? super GItem> emuator) throws NullPointerException, IllegalArgumentException {
		if (clazz.isArray() || clazz.isPrimitive()) throw new IllegalArgumentException();
		if (emuator == null) {
			EMU.emuatorMap.remove(clazz);
		} else {
			EMU.emuatorMap.put(clazz, emuator);
		}
		EMU.emuatorCache.clear();
	}

	/** Diese Methode gibt den gegebenen Speicherverbrauch auf ein Vielfaches von {@code 8} aufgerundet zurück.
	 *
	 * @param size Speicherverbrauch.
	 * @return aufgerundeter Speicherverbrauch. */
	public static int align(final int size) {
		return (size + 7) & -8;
	}

	/** Diese Methode gibt den gegebenen Speicherverbrauch auf ein Vielfaches von {@code 8} aufgerundet zurück.
	 *
	 * @param size Speicherverbrauch.
	 * @return aufgerundeter Speicherverbrauch. */
	public static long align(final long size) {
		return (size + 7) & -8L;
	}

	/** Diese Methode gibt den geschätztern Speicherverbrauch des gegebenen Objekts zurück. Wenn es {@code null} ist, wird {@code 0} geliefert. Wenn es ein
	 * {@link Emuable} ist, wird der Speicherverbrauch über {@link Emuable#emu()} geschätzt. Wenn es ein Array ist, wird er über {@link #fromArray(Object)}
	 * geschätzt. Wenn für seine Klasse ein {@link Emuator} {@link #use(Class, Emuator) registiriert wurde}, wird er über {@link Emuator#emu(Object)} geschätzt.
	 * Andernfalls wird er über {@link #fromClass(Class)} bezüglich seiner Klasse geschätzt.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return geschätzter Speicherverbrauch. */
	@SuppressWarnings ("unchecked")
	public static long from(final Object object) {
		if (object == null) return 0;
		if (object instanceof Emuable) return ((Emuable)object).emu();
		final Class<?> clazz = object.getClass();
		if (clazz.isArray()) return EMU.fromArray(clazz.getComponentType(), Array.getLength(object));
		Emuator<Object> emuator = (Emuator<Object>)EMU.emuatorCache.get(clazz);
		if (emuator != null) return emuator.emu(object);
		for (Class<?> key = clazz; key != null; key = key.getSuperclass()) {
			emuator = (Emuator<Object>)EMU.emuatorMap.get(key);
			if (emuator != null) {
				EMU.emuatorCache.put(clazz, emuator);
				return emuator.emu(object);
			}
		}
		EMU.emuatorCache.put(clazz, EMU.defaultEmuator);
		return EMU.fromClass(clazz);
	}

	/** Diese Methode gibt die Summe der für jedes gegebenen Objekt {@link #from(Object) geschätzten Speicherverbräuche} zurück.
	 *
	 * @see #from(Object)
	 * @param items Objekte.
	 * @return geschätzter Speicherverbrauch. */
	@SafeVarargs
	public static <GItem> long fromAll(final GItem... items) {
		long result = 0;
		for (final Object item: items) {
			result += EMU.from(item);
		}
		return result;
	}

	/** Diese Methode gibt die Summe der für jedes gegebenen Objekt {@link #from(Object) geschätzten Speicherverbräuche} zurück.
	 *
	 * @see #from(Object)
	 * @param items Objekte.
	 * @return geschätzter Speicherverbrauch. */
	public static long fromAll(final Iterable<?> items) {
		long result = 0;
		for (final Object item: items) {
			result += EMU.from(item);
		}
		return result;
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch eines Datenfeldes mit dem gegebenen Datentyp zurück. Für {@link Byte#TYPE} sowie {@link Boolean#TYPE}
	 * liefert sie {@code 1}, für {@link Short#TYPE} sowie {@link Character#TYPE} liefert sie {@code 2}, für {@link Long#TYPE} sowie {@link Double#TYPE} liefert
	 * sie {@code 8} und für alle anderen Datentyp liefert sie {@code 4}.
	 *
	 * @param type Datentyp oder {@code null}.
	 * @return geschätzter Speicherverbrauch. */
	public static int fromType(final Class<?> type) {
		if ((type == Byte.TYPE) || (type == Boolean.TYPE)) return 1;
		if ((type == Short.TYPE) || (type == Character.TYPE)) return 2;
		if ((type == Long.TYPE) || (type == Double.TYPE)) return 8;
		return 4;
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch des gegebenen Datenfeldes zurück.
	 *
	 * @param field Datenfeld.
	 * @return geschätzter Speicherverbrauch.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static int fromField(final Field field) throws NullPointerException {
		return EMU.fromType(field.getType());
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch des gegebenen Arrays zurück. Wenn es {@code null} ist, wird {@code 0} geliefert. Der
	 * Speicherverbrauch der von den Elementen des Arrays referenzierten Objekte wird hierbei nicht berücksichtigt.
	 *
	 * @see #fromArray(Class, int)
	 * @param array Array oder {@code null}.
	 * @return geschätzter Speicherverbrauch.
	 * @throws IllegalArgumentException Wenn das gegebene Objewkt kein Array ist. */
	public static long fromArray(final Object array) throws IllegalArgumentException {
		if (array == null) return 0;
		return EMU.fromArray(array.getClass().getComponentType(), Array.getLength(array));
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch eines Arrays mit der gegebenen Länge und Elementen vom gegebenen Datentyp zurück.
	 *
	 * @see #align(long)
	 * @see #fromType(Class)
	 * @param type Datentyp der Elemete oder {@code null}.
	 * @param length Länge des Arrays.
	 * @return geschätzter Speicherverbrauch. */
	public static long fromArray(final Class<?> type, final int length) {
		return EMU.align(12 + (EMU.fromType(type) * (long)length));
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch für die Kopfdaten sowie die Instanzdatenfelder der gegebenen Klasse zurück. Wenn sie {@code null}
	 * ist, wird {@code 8} geliefert.
	 *
	 * @see #align(int)
	 * @see #fromField(Field)
	 * @param clazz Klasse oder {@code null}.
	 * @return geschätzter Speicherverbrauch. */
	public static int fromClass(final Class<?> clazz) {
		int result = 8;
		for (Class<?> type = clazz; type != null; type = type.getSuperclass()) {
			for (final Field field: type.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					result += EMU.fromField(field);
				}
			}
		}
		return EMU.align(result);
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch für die Kopfdaten sowie die Instanzdatenfelder des gegebenen Objekts zurück. Wenn es {@code null}
	 * ist, wird {@code 0} geliefert.
	 *
	 * @see #fromArray(Object)
	 * @see #fromClass(Class)
	 * @param object Objekt oder {@code null}.
	 * @return geschätzter Speicherverbrauch. */
	public static long fromObject(final Object object) {
		if (object == null) return 0;
		final Class<?> clazz = object.getClass();
		return clazz.isArray() ? EMU.fromArray(clazz.getComponentType(), Array.getLength(object)) : EMU.fromClass(clazz);
	}

}
