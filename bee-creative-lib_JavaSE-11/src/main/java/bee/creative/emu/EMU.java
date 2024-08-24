package bee.creative.emu;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import bee.creative.util.HashMap2;
import bee.creative.util.Hashers;

/** Diese Klasse implementiert Methoden zur Schätzung des Speicherverbrauchs von Objekten (<i>Estimated Memory Usage</i>).
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class EMU {

	/** Diese Methode registriert den gegebenen {@link Emuator} zur Schätzung des Speicherverbrauchs der Instanzen der gegebenen Klasse in {@link #from(Object)}.
	 * Wenn der {@link Emuator} {@code null} ist, wird die Sonderbehandlung für die Instanzen der gegebenen Klasse aufgehoben.
	 *
	 * @param <GItem> Typ der Instanzen.
	 * @param clazz Klasse der Instanzen.
	 * @param emuator {@link Emuator} oder {@code null}.
	 * @throws NullPointerException Wenn {@code clazz} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Klasse primitiv oder ein Array ist. */
	public static <GItem> void use(Class<GItem> clazz, Emuator<GItem> emuator) throws NullPointerException, IllegalArgumentException {
		if (clazz.isArray() || clazz.isPrimitive()) throw new IllegalArgumentException();
		synchronized (EMU.useMap) {
			if (emuator == null) {
				EMU.useMap.remove(clazz);
			} else {
				EMU.useMap.put(clazz, emuator);
			}
			EMU.emuatorCache.clear();
		}
	}

	/** Diese Methode gibt den gegebenen Speicherverbrauch auf ein Vielfaches von {@code 8} aufgerundet zurück.
	 *
	 * @param size Speicherverbrauch.
	 * @return aufgerundeter Speicherverbrauch. */
	public static int align(int size) {
		return (size + 7) & -8;
	}

	/** Diese Methode gibt den gegebenen Speicherverbrauch auf ein Vielfaches von {@code 8} aufgerundet zurück.
	 *
	 * @param size Speicherverbrauch.
	 * @return aufgerundeter Speicherverbrauch. */
	public static long align(long size) {
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
	public static long from(Object object) {
		if (object == null) return 0;
		if (object instanceof Emuable) return ((Emuable)object).emu();
		var clazz = object.getClass();
		if (clazz.isArray()) return EMU.fromArray(clazz.getComponentType(), Array.getLength(object));
		synchronized (EMU.useMap) {
			return ((Emuator<Object>)EMU.emuatorCache.install(clazz)).emu(object);
		}
	}

	/** Diese Methode gibt die Summe der für jedes gegebenen Objekt {@link #from(Object) geschätzten Speicherverbräuche} zurück.
	 *
	 * @see #from(Object)
	 * @param items Objekte.
	 * @return geschätzter Speicherverbrauch. */
	@SafeVarargs
	public static <GItem> long fromAll(GItem... items) {
		var result = 0L;
		for (var item: items) {
			result += EMU.from(item);
		}
		return result;
	}

	/** Diese Methode gibt die Summe der für jedes gegebenen Objekt {@link #from(Object) geschätzten Speicherverbräuche} zurück.
	 *
	 * @see #from(Object)
	 * @param items Objekte.
	 * @return geschätzter Speicherverbrauch. */
	public static long fromAll(Iterable<?> items) {
		var result = 0L;
		for (var item: items) {
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
	public static int fromType(Class<?> type) {
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
	public static int fromField(Field field) throws NullPointerException {
		return EMU.fromType(field.getType());
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch des gegebenen Arrays zurück. Wenn es {@code null} ist, wird {@code 0} geliefert. Der
	 * Speicherverbrauch der von den Elementen des Arrays referenzierten Objekte wird hierbei nicht berücksichtigt.
	 *
	 * @see #fromArray(Class, int)
	 * @param array Array oder {@code null}.
	 * @return geschätzter Speicherverbrauch.
	 * @throws IllegalArgumentException Wenn das gegebene Objewkt kein Array ist. */
	public static long fromArray(Object array) throws IllegalArgumentException {
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
	public static long fromArray(Class<?> type, int length) {
		return EMU.align(12 + (EMU.fromType(type) * (long)length));
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch für die Kopfdaten sowie die Instanzdatenfelder der gegebenen Klasse zurück. Wenn sie {@code null}
	 * ist, wird {@code 8} geliefert.
	 *
	 * @see #align(int)
	 * @see #fromField(Field)
	 * @param clazz Klasse oder {@code null}.
	 * @return geschätzter Speicherverbrauch. */
	public static int fromClass(Class<?> clazz) {
		synchronized (EMU.useMap) {
			return EMU.integerCache.install(clazz);
		}
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch für die Kopfdaten sowie die Instanzdatenfelder des gegebenen Objekts zurück. Wenn es {@code null}
	 * ist, wird {@code 0} geliefert.
	 *
	 * @see #fromArray(Object)
	 * @see #fromClass(Class)
	 * @param object Objekt oder {@code null}.
	 * @return geschätzter Speicherverbrauch. */
	public static long fromObject(Object object) {
		if (object == null) return 0;
		var clazz = object.getClass();
		return clazz.isArray() ? EMU.fromArray(clazz.getComponentType(), Array.getLength(object)) : EMU.fromClass(clazz);
	}

	/** Dieses Feld speichert die in {@link #use(Class, Emuator)} registrierten {@link Emuator}. */
	private static final HashMap2<Class<?>, Emuator<?>> useMap = new HashMap2<>();

	/** Dieses Feld speichert die in {@link #fromClass(Class)} gepufferten Größen. */
	private static final HashMap2<Class<?>, Integer> integerCache = HashMap2.from(Hashers.identity(), clazz -> {
		var result = 8;
		for (var type = clazz; type != null; type = type.getSuperclass()) {
			for (var field: type.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					result += EMU.fromField(field);
				}
			}
		}
		return EMU.align(result);
	});

	/** Dieses Feld speichert die in {@link #from(Object)} gepufferten {@link Emuator}. */
	private static final HashMap2<Class<?>, Emuator<?>> emuatorCache = HashMap2.from(Hashers.identity(), clazz -> {
		for (var type = clazz; type != null; type = type.getSuperclass()) {
			var emuator = EMU.useMap.get(type);
			if (emuator != null) return emuator;
		}
		var result = EMU.fromClass(clazz);
		return ignored -> result;
	});

	static {
		EMU.use(String.class, input -> EMU.fromObject(input) + EMU.fromArray(char.class, input.length()));
		EMU.use(ArrayList.class, input -> EMU.fromObject(input) + EMU.fromArray(Object.class, input.size()));
		EMU.use(LinkedList.class, input -> EMU.fromObject(input) + (24 * input.size()));
		EMU.use(HashSet.class, input -> EMU.fromObject(input) + EMU.fromClass(HashMap.class) + (36 * input.size()));
		EMU.use(HashMap.class, input -> EMU.fromClass(HashMap.class) + (36 * input.size()));
	}

}
