package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Properties.emptyProperty;
import static bee.creative.util.Properties.propertyFrom;
import static bee.creative.util.Properties.propertyWithValue;
import java.util.Map.Entry;

/** Diese Klasse implementiert grundlegende {@link Entry}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Entries {

	/** Diese Methode liefert das gegebene {@link Entry} als {@link Entry3}. */
	public static <K, V> Entry3<K, V> entryFrom(Entry<K, V> that) throws NullPointerException {
		notNull(that);
		if (that instanceof Entry3<?, ?>) return (Entry3<K, V>)that;
		return entryFrom(that::getKey, propertyFrom(that::getValue, that::setValue));
	}

	/** Diese Methode liefert ein {@link Entry3} mit dem gegebenen Schlüssel und Wert. */
	public static <K, V> Entry3<K, V> entryFrom(Property<K> keyProp, Property<V> valueProp) {
		notNull(keyProp);
		notNull(valueProp);
		return new AbstractEntry3<>() {

			@Override
			public Property3<K> key() {
				return propertyFrom(keyProp);
			}

			@Override
			public Property3<V> value() {
				return propertyFrom(valueProp);
			}

			@Override
			public K getKey() {
				return keyProp.get();
			}

			@Override
			public V getValue() {
				return valueProp.get();
			}

			@Override
			public Entry3<K, V> useKey(K key) throws UnsupportedOperationException {
				keyProp.set(key);
				return this;
			}

			@Override
			public Entry3<K, V> useValue(V value) throws UnsupportedOperationException {
				valueProp.set(value);
				return this;
			}

		};
	}

	/** Diese Methode liefert ein {@link Entry3} mit dem gegebenen Schlüssel und Wert. */
	public static <K, V> Entry3<K, V> entryFrom(Producer<? extends K> keyVal, Property<V> valueProp) {
		notNull(keyVal);
		notNull(valueProp);
		return new AbstractEntry3<>() {

			@Override
			public Property3<V> value() {
				return propertyFrom(valueProp);
			}

			@Override
			public K getKey() {
				return keyVal.get();
			}

			@Override
			public V getValue() {
				return valueProp.get();
			}

			@Override
			public Entry3<K, V> useValue(V value) throws UnsupportedOperationException {
				valueProp.set(value);
				return this;
			}

		};
	}

	/** Diese Methode liefert ein {@link Entry3} mit veränderlichem Schlüssel und Wert. */
	public static <K, V> Entry3<K, V> entryWith(K key, V value) {
		return entryFrom(propertyWithValue(key), propertyWithValue(value));
	}

	/** Diese Methode ist eine Abkürzung für {@link #entryWith(Object, Object) entryWith(key, value.get(key))}. */
	public static <K, V> Entry3<K, V> entryWithKey(K key, Getter<? super K, ? extends V> value) throws NullPointerException {
		return entryWith(key, value.get(key));
	}

	/** Diese Methode ist eine Abkürzung für {@link #entryWith(Object, Object) entryWith(key.get(value), value)}. */
	public static <K, V> Entry3<K, V> entryWithValue(V value, Getter<? super V, ? extends K> key) throws NullPointerException {
		return entryWith(key.get(value), value);
	}

	/** Diese Methode liefert ein unveränderliches {@link Entry3} mit {@code null} als Schlüssel und Wert. */
	@SuppressWarnings ("unchecked")
	public static <GKey, GValue> Entry3<GKey, GValue> emptyEntry() {
		return (Entry3<GKey, GValue>)emptyEntry;
	}

	/** Diese Methode ist eine Abkürzung für {@link Entry3#reverse() entryFrom(that).reverse()}. */
	public static <GKey, GValue> Entry3<GKey, GValue> reverseEntry(Entry<GValue, GKey> that) throws NullPointerException {
		return entryFrom(that).reverse();
	}

	/** Diese Methode ist eine Abkürzung für {@link Entry3#translate(Translator, Translator) entryFrom(that).translate(keyTrans, valueTrans)}. */
	public static <K, V, K2, V2> Entry3<K, V> translatedEntry(Entry<K2, V2> that, Translator<K2, K> keyTrans, Translator<V2, V> valueTrans)
		throws NullPointerException {
		return entryFrom(that).translate(keyTrans, valueTrans);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Entry#getKey()}. */
	@SuppressWarnings ("unchecked")
	public static <K> Getter3<Entry<? extends K, ?>, K> entryKey() {
		return (Getter3<Entry<? extends K, ?>, K>)entryKey;
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Entry#getValue()}. */
	@SuppressWarnings ("unchecked")
	public static <V> Getter3<Entry<?, ? extends V>, V> entryValue() {
		return (Getter3<Entry<?, ? extends V>, V>)entryValue;
	}

	private static final Getter3<? extends Entry<?, ?>, ?> entryKey = Entry::getKey;

	private static final Getter3<? extends Entry<?, ?>, ?> entryValue = Entry::getValue;

	private static final Entry3<?, ?> emptyEntry = entryFrom(emptyProperty(), emptyProperty());

}
