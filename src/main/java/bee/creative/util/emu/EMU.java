package bee.creative.util.emu;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

// Estimated Memory Usage
@SuppressWarnings ("rawtypes")
public class EMU {

	/** Dieses Feld speichert die in {@link #use(Class, Emuator)} registrierten {@link Emuator}. */
	private static final Map<Class<?>, Emuator<?>> emuators = new bee.creative.util.HashMap<>(16);

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
				return EMU.fromObject(input) + EMU.fromClass(HashMap.class) + 36 * input.size();
			}

		});

	}

	public static void main(final String[] args) throws Exception {
		System.out.println(EMU.from(new ArrayList<>(Arrays.asList(123, 45, 567, 79, 6))));
		System.out.println(EMU.from(new LinkedList<>(Arrays.asList(123, 45, 567, 79, 6))));
		System.out.println(EMU.from(new HashSet<>(Arrays.asList(123, 45, 567, 79, 6))));
	}

	public static <GInput> void use(final Class<GInput> clazz, final Emuator<? super GInput> emuator) {
		if (clazz.isArray() || clazz.isPrimitive()) throw new IllegalArgumentException();
		if (emuator == null) {
			EMU.emuators.remove(clazz);
		} else {
			EMU.emuators.put(clazz, emuator);
		}
	}

	public static int align(final int size) {
		return (size + 7) & -8;
	}

	public static long align(final long size) {
		return (size + 7) & -8L;
	}

	public static long from(final Object object) {
		if (object == null) return 0;
		if (object instanceof Emuable) return ((Emuable)object).emu();
		for (Class<?> clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			@SuppressWarnings ("unchecked")
			final Emuator<Object> emuator = (Emuator<Object>)EMU.emuators.get(clazz);
			if (emuator != null) return emuator.emu(object);
		}
		return EMU.fromObject(object);
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch eines Datenfeldes mit dem gegebenen Datentyp zurück.<br>
	 * Für {@link Byte#TYPE} sowie {@link Boolean#TYPE} liefert sie {@code 1}, für {@link Short#TYPE} sowie {@link Character#TYPE} liefert sie {@code 2}, für
	 * {@link Long#TYPE} sowie {@link Double#TYPE} liefert sie {@code 8} und für alle anderen Datentyp liefert sie {@code 4}.
	 *
	 * @param type Datentyp.
	 * @return Speicherverbrauch. */
	public static int fromType(final Class<?> type) {
		if ((type == Byte.TYPE) || (type == Boolean.TYPE)) return 1;
		if ((type == Short.TYPE) || (type == Character.TYPE)) return 2;
		if ((type == Long.TYPE) || (type == Double.TYPE)) return 8;
		return 4;
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch des gegebenen Datenfeldes zurück.
	 *
	 * @param field Datenfeld.
	 * @return Speicherverbrauch.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static int fromField(final Field field) throws NullPointerException {
		return EMU.fromType(field.getType());
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch des gegebenen Arrays zurück. Wenn es {@code null} ist, wird {@code 0} geliefert. Der
	 * Speicherverbrauch der von den Elementen des Arrays referenzierten Objekte wird hierbei nicht berücksichtigt.
	 *
	 * @param array Array oder {@code null}.
	 * @return Speicherverbrauch.
	 * @throws IllegalArgumentException Wenn das gegebene Objewkt kein Array ist. */
	public static long fromArray(final Object array) throws IllegalArgumentException {
		if (array == null) return 0;
		return EMU.fromArray(array.getClass().getComponentType(), Array.getLength(array));
	}

	public static long fromArray(final Class<?> type, final int length) {
		return EMU.align(12 + (EMU.fromType(type) * (long)length));
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch für die Kopfdaten sowie die Instanzdatenfelder der Instanzen der gegebenen Klasse zurück. Wenn sie
	 * {@code null} ist, wird {@code 8} geliefert.
	 *
	 * @param clazz Klasse oder {@code null}.
	 * @return Speicherverbrauch. */
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

	public static int fromFields(final Class<?>... fieldTypes) {
		int result = 8;
		for (final Class<?> type: fieldTypes) {
			result += EMU.fromType(type);
		}
		return EMU.align(result);
	}

	/** Diese Methode gibt den geschätzten Speicherverbrauch für die Kopfdaten sowie die Instanzdatenfelder des gegebenen Objekts zurück. Wenn es {@code null}
	 * ist, wird {@code 0} geliefert.
	 *
	 * @see Emuable#emu()
	 * @param object Objekt oder {@code null}.
	 * @return Speicherverbrauch. */
	public static long fromObject(final Object object) {
		if (object == null) return 0;
		final Class<?> type = object.getClass();
		if (type.isArray()) return EMU.fromArray(object);
		return EMU.fromClass(object.getClass());
	}

}
