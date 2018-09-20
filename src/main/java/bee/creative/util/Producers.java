package bee.creative.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;
import bee.creative.ref.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Producer}.
 *
 * @see Producer
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Klasse implementiert {@link Producers#itemProducer(Object)}. */
	@SuppressWarnings ("javadoc")
	public static class ItemProducer<GItem> implements Producer<GItem> {

		public final GItem item;

		public ItemProducer(final GItem item) {
			this.item = item;
		}

		@Override
		public GItem get() {
			return this.item;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item);
		}

	}

	/** Diese Klasse implementiert {@link Producers#nativeProducer(Method)}. */
	@SuppressWarnings ("javadoc")
	public static class MethodProducer<GItem> implements Producer<GItem> {

		public final Method method;

		public MethodProducer(final Method method) {
			if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.method = method;
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GItem get() {
			try {
				return (GItem)this.method.invoke(null);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, Natives.formatMethod(this.method));
		}

	}

	/** Diese Klasse implementiert {@link Producers#nativeProducer(Constructor)}. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorProducer<GItem> implements Producer<GItem> {

		public final Constructor<?> constructor;

		public ConstructorProducer(final Constructor<?> constructor) {
			this.constructor = Objects.assertNotNull(constructor);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GItem get() {
			try {
				return (GItem)this.constructor.newInstance();
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, Natives.formatConstructor(this.constructor));
		}

	}

	/** Diese Klasse implementiert {@link Producers#bufferedProducer(int, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class BufferedProducer<GItem> implements Producer<GItem> {

		public final Producer<? extends GItem> producer;

		public final int mode;

		protected Pointer<GItem> pointer;

		public BufferedProducer(final int mode, final Producer<? extends GItem> producer) {
			Pointers.pointer(mode, null);
			this.mode = mode;
			this.producer = Objects.assertNotNull(producer);
		}

		@Override
		public GItem get() {
			final Pointer<GItem> pointer = this.pointer;
			if (pointer != null) {
				final GItem data = pointer.get();
				if (data != null) return data;
				if (pointer == Pointers.NULL) return null;
			}
			final GItem data = this.producer.get();
			this.pointer = Pointers.pointer(this.mode, data);
			return data;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.mode, this.producer);
		}

	}

	/** Diese Klasse implementiert {@link Producers#navigatedBuilder(Getter, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class NavigatedProducer<GInput, GOutput> implements Producer<GOutput> {

		public final Getter<? super GInput, ? extends GOutput> navigator;

		public final Producer<? extends GInput> producer;

		public NavigatedProducer(final Getter<? super GInput, ? extends GOutput> navigator, final Producer<? extends GInput> producer) {
			this.navigator = Objects.assertNotNull(navigator);
			this.producer = Objects.assertNotNull(producer);
		}

		@Override
		public GOutput get() {
			return this.navigator.get(this.producer.get());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.navigator, this.producer);
		}

	}

	/** Diese Klasse implementiert {@link Producers#synchronizedProducer(Object, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProducer<GItem> implements Producer<GItem> {

		public final Object mutex;

		public final Producer<? extends GItem> producer;

		public SynchronizedProducer(final Object mutex, final Producer<? extends GItem> producer) throws NullPointerException {
			this.mutex = mutex != null ? mutex : this;
			this.producer = Objects.assertNotNull(producer);
		}

		@Override
		public GItem get() {
			synchronized (this.mutex) {
				return this.producer.get();
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.producer);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Erzeugung eines Datensatzes.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseBuilder<GItem, GThis> implements Producer<GItem> {

		/** Diese Methode gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		protected abstract GThis customThis();

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator das auf Feld {@link #result} gespeicherte Objekt.
	 *
	 * @param <GResult> Typ des Werts.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseBuilder2<GResult, GThis> extends BaseBuilder<GResult, GThis> {

		/** Dieses Feld speichert den Wert. */
		protected GResult result;

		/** {@inheritDoc} */
		@Override
		public GResult get() {
			return this.result;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toString(true, this.result);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert. Der {@link #iterator()} liefert diesen, sofern er nicht {@code null} ist.
	 *
	 * @param <GResult> Typ des Werts.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseValueBuilder<GResult, GThis> extends BaseBuilder2<GResult, GThis> implements Iterable<GResult> {

		/** Diese Methode setzt den Wert und gibt {@code this} zurück.
		 *
		 * @see #get()
		 * @param value Wert.
		 * @return {@code this}. */
		public GThis use(final GResult value) {
			this.result = value;
			return this.customThis();
		}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #use(Object)
		 * @param source Konfigurator.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GThis use(final BaseValueBuilder<? extends GResult, ?> source) throws NullPointerException {
			return this.use(source.result);
		}

		/** Diese Methode setzt den Wert auf {@code null} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public GThis clear() {
			this.result = null;
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<GResult> iterator() {
			final GResult result = this.result;
			if (result == null) return Iterators.emptyIterator();
			return Iterators.itemIterator(result);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Collection}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link Collection}.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSetBuilder<GItem, GResult extends Collection<GItem>, GThis> extends BaseBuilder2<GResult, GThis> implements Iterable<GItem> {

		/** Dieser Konstruktor initialisiert die interne Sammlung.
		 *
		 * @param result Sammlung.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected BaseSetBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #clear()
		 * @see #putAll(Iterable)
		 * @param source Konfigurator.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GThis use(final BaseSetBuilder<? extends GItem, ?, ?> source) throws NullPointerException {
			Objects.assertNotNull(source);
			this.clear();
			return this.putAll(source.result);
		}

		/** Diese Methode {@link Collection#add(Object) fügt das gegebene Element} der {@link #get() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param item Element.
		 * @return {@code this}. */
		public GThis put(final GItem item) {
			this.result.add(item);
			return this.customThis();
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #get() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		@SuppressWarnings ("unchecked")
		public GThis putAll(final GItem... items) {
			return this.putAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #get() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis putAll(final Iterator<? extends GItem> items) {
			while (items.hasNext()) {
				this.result.add(items.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #get() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis putAll(final Iterable<? extends GItem> items) {
			return this.putAll(items.iterator());
		}

		/** Diese Methode {@link Collection#remove(Object) entfern das gegebene Element} und gibt {@code this} zurück.
		 *
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public GThis pop(final Object key) {
			this.result.remove(key);
			return this.customThis();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern die gegebenen Elemente} und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis popAll(final Object... items) {
			return this.popAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#remove(Object) entfern die gegebenen Elemente} und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis popAll(final Iterator<?> items) {
			while (items.hasNext()) {
				this.result.remove(items.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die gegebenen Elemente} und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis popAll(final Iterable<?> items) {
			return this.popAll(items.iterator());
		}

		/** Diese Methode {@link Collection#clear() entfern alle Elemente} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public GThis clear() {
			this.result.clear();
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> iterator() {
			return this.result.iterator();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GItem> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseMapBuilder<GKey, GItem, GResult extends Map<GKey, GItem>, GThis> extends BaseBuilder2<GResult, GThis>
		implements Iterable<Entry<GKey, GItem>> {

		/** Dieses Feld speichert den über {@link #forKey(Object)} gewählten Schlüssel, der in {@link #useValue(Object)} eingesetzt wird. */
		protected GKey key;

		/** Dieser Konstruktor initialisiert die interne {@link Map}.
		 *
		 * @param result interne {@link Map}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected BaseMapBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		@SuppressWarnings ("javadoc")
		protected void putImpl(final Entry<? extends GKey, ? extends GItem> entry) {
			this.result.put(entry.getKey(), entry.getValue());
		}

		@SuppressWarnings ("javadoc")
		protected void putKeyFromImpl(final Getter<? super GItem, ? extends GKey> key, final GItem value) {
			this.result.put(key.get(value), value);
		}

		@SuppressWarnings ("javadoc")
		protected void putValueFromImpl(final Getter<? super GKey, ? extends GItem> value, final GKey key) {
			this.result.put(key, value.get(key));
		}

		@SuppressWarnings ("javadoc")
		protected void putInverseImpl(final Entry<? extends GItem, ? extends GKey> entry) {
			this.result.put(entry.getValue(), entry.getKey());
		}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #clear()
		 * @see #putAll(Map)
		 * @param source Konfigurator.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GThis use(final BaseMapBuilder<? extends GKey, ? extends GItem, ?, ?> source) throws NullPointerException {
			Objects.assertNotNull(source);
			return this.putAll(source.result);
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param entry Eintrag.
		 * @return {@code this}. */
		public GThis put(final Entry<? extends GKey, ? extends GItem> entry) {
			this.putImpl(entry);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param key Schlüssel des Eintrags.
		 * @param value Wert des Eintrags.
		 * @return {@code this}. */
		public GThis put(final GKey key, final GItem value) {
			this.result.put(key, value);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung des Schlüssels zum gegebenen Wert des Eintrags.
		 * @param value Wert des Eintrags.
		 * @return {@code this}. */
		public GThis putKeyFrom(final Getter<? super GItem, ? extends GKey> key, final GItem value) {
			this.putKeyFromImpl(key, value);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung des Werts zum gegebenen Schlüssel des Eintrags.
		 * @param key Schlüssel des Eintrags.
		 * @return {@code this}. */
		public GThis putValueFrom(final Getter<? super GKey, ? extends GItem> value, final GKey key) {
			this.putValueFromImpl(value, key);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag invertiert ein} und gibt {@code this} zurück.<br>
		 * Schlüssel und Wert des Eintrags werden dazu als Wert bzw. Schlüssel verwendet.
		 *
		 * @param entry Eintrag.
		 * @return {@code this}. */
		public GThis putInverse(final Entry<? extends GItem, ? extends GKey> entry) {
			this.putInverseImpl(entry);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Map<? extends GKey, ? extends GItem> entries) {
			return this.putAll(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterator<? extends Entry<? extends GKey, ? extends GItem>> entries) {
			while (entries.hasNext()) {
				this.putImpl(entries.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterable<? extends Entry<? extends GKey, ? extends GItem>> entries) {
			return this.putAll(entries.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAll(final GKey[] keys, final GItem[] values) {
			return this.putAll(Arrays.asList(keys), Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterator<? extends GKey> keys, final Iterator<? extends GItem> values) {
			while (keys.hasNext() && values.hasNext()) {
				this.put(keys.next(), values.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterable<? extends GKey> keys, final Iterable<? extends GItem> values) {
			return this.putAll(keys.iterator(), values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		@SuppressWarnings ("unchecked")
		public GThis putAllKeysFrom(final Getter<? super GItem, ? extends GKey> key, final GItem... values) {
			return this.putAllKeysFrom(key, Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAllKeysFrom(final Getter<? super GItem, ? extends GKey> key, final Iterator<? extends GItem> values) {
			while (values.hasNext()) {
				this.putKeyFromImpl(key, values.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAllKeysFrom(final Getter<? super GItem, ? extends GKey> key, final Iterable<? extends GItem> values) {
			return this.putAllKeysFrom(key, values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		@SuppressWarnings ("unchecked")
		public GThis putAllValuesFrom(final Getter<? super GKey, ? extends GItem> value, final GKey... keys) {
			return this.putAllValuesFrom(value, Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis putAllValuesFrom(final Getter<? super GKey, ? extends GItem> value, final Iterator<? extends GKey> keys) {
			while (keys.hasNext()) {
				this.putValueFromImpl(value, keys.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis putAllValuesFrom(final Getter<? super GKey, ? extends GItem> value, final Iterable<? extends GKey> keys) {
			return this.putAllValuesFrom(value, keys.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAllInverse(final Map<? extends GItem, ? extends GKey> entries) {
			return this.putAllInverse(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAllInverse(final Iterator<? extends Entry<? extends GItem, ? extends GKey>> entries) {
			while (entries.hasNext()) {
				this.putInverseImpl(entries.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAllInverse(final Iterable<? extends Entry<? extends GItem, ? extends GKey>> entries) {
			return this.putAllInverse(entries.iterator());
		}

		/** Diese Methode {@link Map#remove(Object) entfern den Eintrag mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public GThis pop(final Object key) {
			this.result.remove(key);
			return this.customThis();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis popAll(final Object... keys) {
			return this.popAll(Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis popAll(final Iterator<?> keys) {
			while (keys.hasNext()) {
				this.result.remove(keys.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis popAll(final Iterable<?> keys) {
			return this.popAll(keys.iterator());
		}

		/** Diese Methode {@link Map#clear() entfern alle Einträge} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public GThis clear() {
			this.result.clear();
			return this.customThis();
		}

		/** Diese Methode wählt den gegebenen Schlüssel und gibt {@code this} zurück. Dieser Schlüssel wird in den nachfolgenden Aufrufen von {@link #getValue()}
		 * und {@link #useValue(Object)} verwendet und dort wieder zurück gesetzt.
		 *
		 * @see #getValue()
		 * @see #useValue(Object)
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public GThis forKey(final GKey key) {
			this.key = key;
			return this.customThis();
		}

		/** Diese Methode gibt den Wert zum {@link #forKey(Object) gewählten Schlüssel} zurück und setzt diesen Schlüssel auf {@code null}.
		 *
		 * @see #forKey(Object)
		 * @see #useValue(Object)
		 * @return Wert zum gewählten Schlüssel. */
		public GItem getValue() {
			try {
				return this.result.get(this.key);
			} finally {
				this.forKey(null);
			}
		}

		/** Diese Methode setzt den Wert zum {@link #forKey(Object) gewählten Schlüssel}, setzt diesen Schlüssel auf {@code null} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see #getValue()
		 * @param value Wert.
		 * @return {@code this}. */
		public GThis useValue(final GItem value) {
			try {
				this.put(this.key, value);
			} finally {
				this.forKey(null);
			}
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<Entry<GKey, GItem>> iterator() {
			return this.result.entrySet().iterator();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link HashSet}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSetData<GItem, GThis> extends BaseSetBuilder<GItem, HashSet<GItem>, GThis> {

		/** Dieser Konstruktor initialisiert das interne {@link HashSet}. */
		public BaseSetData() {
			super(new HashSet<GItem>());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link HashMap}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GItem> Typ der Werte.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseMapData<GKey, GItem, GThis> extends BaseMapBuilder<GKey, GItem, HashMap<GKey, GItem>, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link HashMap}. */
		public BaseMapData() {
			super(new HashMap<GKey, GItem>());
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GItem> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}. */
	public static class MapBuilder<GKey, GItem, GResult extends Map<GKey, GItem>> extends BaseMapBuilder<GKey, GItem, GResult, MapBuilder<GKey, GItem, GResult>> {

		/** Diese Methode gibt einen {@link MapBuilder} zur gegebenen {@link Map} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param <GResult> Typ der {@link Map}.
		 * @param result {@link Map}.
		 * @return {@link MapBuilder}. */
		public static <GKey, GItem, GResult extends Map<GKey, GItem>> MapBuilder<GKey, GItem, GResult> from(final GResult result) {
			return new MapBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} mit natürlicher Ordnung zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, TreeMap<GKey, GItem>> forTreeMap() {
			return MapBuilder.forTreeMap(null);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param comparator Ordnung der Schlüssel.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, TreeMap<GKey, GItem>> forTreeMap(final Comparator<? super GKey> comparator) {
			return MapBuilder.from(new TreeMap<GKey, GItem>(comparator));
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} mit Steuwertpuffer zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, HashMap<GKey, GItem>> forHashMap() {
			return MapBuilder.forHashMap(true);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, HashMap<GKey, GItem>> forHashMap(final boolean withHashCache) {
			return MapBuilder.from(withHashCache ? new HashMap2<GKey, GItem>() : new HashMap<GKey, GItem>());
		}

		/** Dieser Konstruktor initialisiert die interne {@link Map}.
		 *
		 * @param result interne {@link Map}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected MapBuilder(final GResult result) throws NullPointerException {
			super(result);
		}

		/** {@inheritDoc} */
		@Override
		protected MapBuilder<GKey, GItem, GResult> customThis() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur datentypsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return neuer {@link MapBuilder} zur {@code checkedMap}. */
		public MapBuilder<GKey, GItem, Map<GKey, GItem>> toChecked(final Class<GKey> keyClazz, final Class<GItem> valueClazz) {
			return MapBuilder.from(Collections.checkedMap(this.result, keyClazz, valueClazz));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur übersetzten {@link #get() Abbildung} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedMap(Map, Translator, Translator)
		 * @param <GKey2> Typ der übersetzten Schlüssel.
		 * @param <GValue2> Typ der übersetzten Werte.
		 * @param keyTranslator {@link Translator} zur Übersetzung der Schlüssel.
		 * @param valueTranslator {@link Translator} zur Übersetzung der Werte.
		 * @return neuer {@link MapBuilder} zur {@code translatedMap}. */
		public <GKey2, GValue2> MapBuilder<GKey2, GValue2, Map<GKey2, GValue2>> toTranslated(final Translator<GKey, GKey2> keyTranslator,
			final Translator<GItem, GValue2> valueTranslator) {
			return MapBuilder.from(bee.creative.util.Collections.translatedMap(this.result, keyTranslator, valueTranslator));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur threadsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code synchronizedMap}. */
		public MapBuilder<GKey, GItem, Map<GKey, GItem>> toSynchronized() {
			return MapBuilder.from(Collections.synchronizedMap(this.result));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur unveränderliche {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code unmodifiableMap}. */
		public MapBuilder<GKey, GItem, Map<GKey, GItem>> toUnmodifiable() {
			return MapBuilder.from(Collections.unmodifiableMap(this.result));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ des {@link Set}. */
	public static class SetBuilder<GItem, GResult extends Set<GItem>> extends BaseSetBuilder<GItem, GResult, SetBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link SetBuilder} zum gegebenen {@link Set} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param <GResult> Typ des {@link Set}.
		 * @param result {@link Set}.
		 * @return {@link SetBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem, GResult extends Set<GItem>> SetBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new SetBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} mit natürlicher Ordnung zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link SetBuilder} eines {@link TreeSet}. */
		public static <GItem> SetBuilder<GItem, TreeSet<GItem>> forTreeSet() {
			return SetBuilder.forTreeSet(null);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param comparator Ordnung der Elemente.
		 * @return {@link SetBuilder} eines {@link TreeSet}. */
		public static <GItem> SetBuilder<GItem, TreeSet<GItem>> forTreeSet(final Comparator<? super GItem> comparator) {
			return SetBuilder.from(new TreeSet<>(comparator));
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} mit Steuwertpuffer zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <GItem> SetBuilder<GItem, HashSet<GItem>> forHashSet() {
			return SetBuilder.forHashSet(true);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <GItem> SetBuilder<GItem, HashSet<GItem>> forHashSet(final boolean withHashCache) {
			return SetBuilder.from(withHashCache ? new HashSet2<GItem>() : new HashSet<GItem>());
		}

		/** Dieser Konstruktor initialisiert das {@link Set}.
		 *
		 * @param result {@link Set}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected SetBuilder(final GResult result) throws NullPointerException {
			super(result);
		}

		/** {@inheritDoc} */
		@Override
		protected SetBuilder<GItem, GResult> customThis() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Vereinigungsmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#unionSet(Set, Set)
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public SetBuilder<GItem, Set<GItem>> toUnion(final Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.unionSet(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für das Kartesische Produkt dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#cartesianSet(Set, Set)
		 * @param <GItem2> Typ der Elemente in der zweiten Menge.
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code cartesianSet}. */
		public <GItem2> SetBuilder<Entry<GItem, GItem2>, Set<Entry<GItem, GItem2>>> toCartesian(final Set<? extends GItem2> items) {
			return SetBuilder.from(bee.creative.util.Collections.cartesianSet(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Schnittmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#intersectionSet(Set, Set)
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code intersectionSet}. */
		public SetBuilder<GItem, Set<GItem>> toIntersection(final Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.intersectionSet(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public SetBuilder<GItem, Set<GItem>> toChecked(final Class<GItem> itemClazz) {
			return SetBuilder.from(Collections.checkedSet(this.result, itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedSet(Set, Translator)
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code translatedSet}. */
		public <GItem2> SetBuilder<GItem2, Set<GItem2>> toTranslated(final Translator<GItem, GItem2> itemTranslator) {
			return SetBuilder.from(bee.creative.util.Collections.translatedSet(this.result, itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die threadsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code synchronizedSet}. */
		public SetBuilder<GItem, Set<GItem>> toSynchronized() {
			return SetBuilder.from(Collections.synchronizedSet(this.result));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die unveränderliche {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code unmodifiableSet}. */
		public SetBuilder<GItem, Set<GItem>> toUnmodifiable() {
			return SetBuilder.from(Collections.unmodifiableSet(this.result));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link List}. */
	public static class ListBuilder<GItem, GResult extends List<GItem>> extends BaseSetBuilder<GItem, GResult, ListBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link ListBuilder} zur gegebenen {@link List} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param <GResult> Typ des {@link List}.
		 * @param result {@link List}.
		 * @return {@link ListBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem, GResult extends List<GItem>> ListBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new ListBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link ArrayList} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link ArrayList}. */
		public static <GItem> ListBuilder<GItem, ArrayList<GItem>> forArrayList() {
			return ListBuilder.from(new ArrayList<GItem>());
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link LinkedList} mit zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link LinkedList}. */
		public static <GItem> ListBuilder<GItem, LinkedList<GItem>> forLinkedList() {
			return ListBuilder.from(new LinkedList<GItem>());
		}

		/** Dieser Konstruktor initialisiert das {@link List}.
		 *
		 * @param result {@link List}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected ListBuilder(final GResult result) throws NullPointerException {
			super(result);
		}

		/** {@inheritDoc} */
		@Override
		protected ListBuilder<GItem, GResult> customThis() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die rückwärts geordnete {@link #get() Liste} zurück.
		 *
		 * @see bee.creative.util.Collections#reverseList(List)
		 * @return neuer {@link ListBuilder} zur {@code reverseList}. */
		public ListBuilder<GItem, List<GItem>> toReverse() {
			return ListBuilder.from(bee.creative.util.Collections.reverseList(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @see bee.creative.util.Collections#chainedList(List, List)
		 * @param items zweite Liste.
		 * @return neuer {@link ListBuilder} zur {@code chained}. */
		public ListBuilder<GItem, List<GItem>> toChained(final List<GItem> items) {
			return ListBuilder.from(bee.creative.util.Collections.chainedList(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @see bee.creative.util.Collections#chainedList(List, List, boolean)
		 * @param items zweite Liste.
		 * @param extendMode Erweiterungsmodus.
		 * @return neuer {@link ListBuilder} zur {@code chained}. */
		public ListBuilder<GItem, List<GItem>> toChained(final List<GItem> items, final boolean extendMode) {
			return ListBuilder.from(bee.creative.util.Collections.chainedList(this.result, items, extendMode));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#checkedList(List, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code checkedList}. */
		public ListBuilder<GItem, List<GItem>> toChecked(final Class<GItem> itemClazz) {
			return ListBuilder.from(Collections.checkedList(this.result, itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedList(List, Translator)
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code translatedList}. */
		public <GItem2> ListBuilder<GItem2, List<GItem2>> toTranslated(final Translator<GItem, GItem2> itemTranslator) {
			return ListBuilder.from(bee.creative.util.Collections.translatedList(this.result, itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die threadsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#synchronizedList(List)
		 * @return neuer {@link ListBuilder} zur {@code synchronizedList}. */
		public ListBuilder<GItem, List<GItem>> toSynchronized() {
			return ListBuilder.from(Collections.synchronizedList(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die unveränderliche {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#unmodifiableList(List)
		 * @return neuer {@link ListBuilder} zur {@code unmodifiableList}. */
		public ListBuilder<GItem, List<GItem>> toUnmodifiable() {
			return ListBuilder.from(Collections.unmodifiableList(this.result));
		}

	}

	public static class IteratorBuilder<GItem, GResult extends Iterator<GItem>> extends BaseBuilder2<GResult, IteratorBuilder<GItem, GResult>> {

		public static <GItem, GResult extends Iterator<GItem>> IteratorBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new IteratorBuilder<>(result);
		}

		protected IteratorBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		@Override
		protected IteratorBuilder<GItem, GResult> customThis() {
			return this;
		}

		public IteratorBuilder<GItem, Iterator<GItem>> toUnique() {
			return IteratorBuilder.from(Iterators.uniqueIterator(this.get()));
		}

		public IteratorBuilder<GItem, Iterator<GItem>> toChained(final Iterator<? extends GItem> i) {
			return IteratorBuilder.from(Iterators.chainedIterator(this.get(), i));
		}

		public IteratorBuilder<GItem, Iterator<GItem>> toLimited(final int count) {
			return IteratorBuilder.from(Iterators.limitedIterator(count, this.get()));
		}

		public IteratorBuilder<GItem, Iterator<GItem>> toFiltered(final Filter<? super GItem> filter) {
			return IteratorBuilder.from(Iterators.filteredIterator(filter, this.get()));
		}

		public <GItem2> IteratorBuilder<GItem2, Iterator<GItem2>> toNavigated(final Getter<? super GItem, ? extends GItem2> getter) {
			return IteratorBuilder.from(Iterators.navigatedIterator(getter, this.get()));
		}

		public IteratorBuilder<GItem, Iterator<GItem>> toUnmodifiable() {
			return IteratorBuilder.from(Iterators.unmodifiableIterator(this.get()));
		}

	}

	public static class IterableBuilder<GItem, GResult extends Iterable<GItem>> extends BaseBuilder2<GResult, IterableBuilder<GItem, GResult>> {

		public static <GItem, GResult extends Iterable<GItem>> IterableBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new IterableBuilder<>(result);
		}

		protected IterableBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		@Override
		protected IterableBuilder<GItem, GResult> customThis() {
			return this;
		}

		public SetBuilder<GItem, Set<GItem>> toSet() {
			return SetBuilder.from(Iterables.toSet(this.get()));
		}

		public ListBuilder<GItem, List<GItem>> toList() {
			return ListBuilder.from(Iterables.toList(this.get()));
		}

		public IterableBuilder<GItem, Iterable<GItem>> toUnique() {
			return IterableBuilder.from(Iterables.uniqueIterable(this.get()));
		}

		public IterableBuilder<GItem, Iterable<GItem>> toChained(final Iterable<? extends GItem> i) {
			return IterableBuilder.from(Iterables.chainedIterable(this.get(), i));
		}

		public IterableBuilder<GItem, Iterable<GItem>> toLimited(final int count) {
			return IterableBuilder.from(Iterables.limitedIterable(count, this.get()));
		}

		public IterableBuilder<GItem, Iterable<GItem>> toFiltered(final Filter<? super GItem> filter) {
			return IterableBuilder.from(Iterables.filteredIterable(filter, this.get()));
		}

		public <GItem2> IterableBuilder<GItem2, Iterable<GItem2>> toNavigated(final Getter<? super GItem, ? extends GItem2> getter) {
			return IterableBuilder.from(Iterables.navigatedIterable(getter, this.get()));
		}

		public IterableBuilder<GItem, Iterable<GItem>> toUnmodifiable() {
			return IterableBuilder.from(Iterables.unmodifiableIterable(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für einen {@link Comparator}.
	 *
	 * @param <GItem> Typ der verglichenen Elemente.
	 * @param <GResult> Typ des {@link Comparator}. */
	public static class ComparatorBuilder<GItem, GResult extends Comparator<GItem>> extends BaseBuilder2<GResult, ComparatorBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link ComparatorBuilder} zum gegebenen {@link Comparator} zurück.
		 *
		 * @param <GItem> Typ der verglichenen Elemente.
		 * @param <GResult> Typ des {@link Comparator}.
		 * @param result {@link Comparator}.
		 * @return {@link ComparatorBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem, GResult extends Comparator<GItem>> ComparatorBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new ComparatorBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link ComparatorBuilder} zur {@link Comparators#naturalComparator() natürlichen Ordnung} zurück.
		 *
		 * @param <GItem> Typ der {@link Comparable} Elemente.
		 * @return {@link ComparatorBuilder}. */
		public static <GItem extends Comparable<? super GItem>> ComparatorBuilder<GItem, Comparator<GItem>> fromNatural() {
			return ComparatorBuilder.from(Comparators.<GItem>naturalComparator());
		}

		/** Dieser Konstruktor initialisiert den internen {@link Comparator}.
		 *
		 * @param result interner {@link Comparator}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected ComparatorBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		/** {@inheritDoc} */
		@Override
		protected ComparatorBuilder<GItem, GResult> customThis() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum {@code null} vergleichenden {@link #get() Comparator} zurück.
		 *
		 * @see Comparators#defaultComparator(Comparator)
		 * @return neuer {@link ComparatorBuilder} zu {@code defaultComparator}. */
		public ComparatorBuilder<GItem, Comparator<GItem>> toDefault() {
			return ComparatorBuilder.from(Comparators.defaultComparator(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum umkehrenden {@link #get() Comparator} zurück.
		 *
		 * @see Comparators#reverseComparator(Comparator)
		 * @return neuer {@link ComparatorBuilder} zu {@code reverseComparator}. */
		public ComparatorBuilder<GItem, Comparator<GItem>> toReverse() {
			return ComparatorBuilder.from(Comparators.reverseComparator(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum iterierenden {@link #get() Comparator} zurück.
		 *
		 * @see Comparators#iterableComparator(Comparator)
		 * @return neuer {@link ComparatorBuilder} zu {@code iterableComparator}. */
		public ComparatorBuilder<Iterable<? extends GItem>, Comparator<Iterable<? extends GItem>>> toIterable() {
			return ComparatorBuilder.from(Comparators.iterableComparator(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum verkettenden {@link #get() Comparator} zurück.
		 *
		 * @see Comparators#chainedComparator(Comparator, Comparator)
		 * @param comparator zweiter {@link Comparator}, das nach dem {@link #get() aktuell konfigurierten} angewandt wird.
		 * @return neuer {@link ComparatorBuilder} zu {@code chainedComparator}. */
		public ComparatorBuilder<GItem, Comparator<GItem>> toChained(final Comparator<? super GItem> comparator) {
			return ComparatorBuilder.from(Comparators.chainedComparator(this.result, comparator));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum navigierenden {@link #get() Comparator} zurück.
		 *
		 * @see Comparators#navigatedComparator(Getter, Comparator)
		 * @param <GItem2> Typ der Eingabe des {@link Getter} sowie der Elemente des erzeugten {@link Comparator}.
		 * @param navigator {@link Getter} zur Navigation.
		 * @return neuer {@link ComparatorBuilder} zu {@code navigatedComparator}. */
		public <GItem2> ComparatorBuilder<GItem2, Comparator<GItem2>> toNavigated(final Getter<? super GItem2, ? extends GItem> navigator) {
			return ComparatorBuilder.from(Comparators.navigatedComparator(navigator, this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum gegebenen Element sowie dem {@link #get() aktuell konfigurierten Comparator} zurück.
		 *
		 * @see Comparables#itemComparable(Getter, Comparator)
		 * @param item Element, welches über den {@link #get() aktuellen Comparator} mit den Eingabe des erzeugten {@link Comparable} vergleichen wird.
		 * @return neuer {@link ComparableBuilder} zu {@code itemComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toComparable(final GItem item) {
			return ComparableBuilder.from(Comparables.itemComparable(item, this.result));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für einen {@link Comparable}.
	 *
	 * @param <GItem> Typ der verglichenen Elemente.
	 * @param <GResult> Typ des {@link Comparable}. */
	public static class ComparableBuilder<GItem, GResult extends Comparable<GItem>> extends BaseBuilder2<GResult, ComparableBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link ComparableBuilder} zum gegebenen {@link Comparable} zurück.
		 *
		 * @param <GItem> Typ der verglichenen Elemente.
		 * @param <GResult> Typ des {@link Comparable}.
		 * @param result {@link Comparable}.
		 * @return {@link ComparableBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem, GResult extends Comparable<GItem>> ComparableBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new ComparableBuilder<>(result);
		}

		/** Dieser Konstruktor initialisiert das interne {@link Comparable}.
		 *
		 * @param result internes {@link Comparable}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected ComparableBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		/** {@inheritDoc} */
		@Override
		protected ComparableBuilder<GItem, GResult> customThis() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum {@code null} vergleichenden {@link #get() Comparable} zurück.
		 *
		 * @see Comparables#defaultComparable(Comparable)
		 * @return neuer {@link ComparableBuilder} zu {@code defaultComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toDefault() {
			return ComparableBuilder.from(Comparables.defaultComparable(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum umkehrenden {@link #get() Comparable} zurück.
		 *
		 * @see Comparables#reverseComparable(Comparable)
		 * @return neuer {@link ComparableBuilder} zu {@code reverseComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toReverse() {
			return ComparableBuilder.from(Comparables.reverseComparable(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum iterierenden {@link #get() Comparable} zurück.
		 *
		 * @see Comparables#iterableComparable(Comparable)
		 * @return neuer {@link ComparableBuilder} zu {@code iterableComparable}. */
		public ComparableBuilder<Iterable<? extends GItem>, Comparable<Iterable<? extends GItem>>> toIterable() {
			return ComparableBuilder.from(Comparables.iterableComparable(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum verkettenden {@link #get() Comparable} zurück.
		 *
		 * @see Comparables#chainedComparable(Comparable, Comparable)
		 * @param comparable zweites {@link Comparable}, das nach dem {@link #get() aktuell konfigurierten} angewandt wird.
		 * @return neuer {@link ComparableBuilder} zu {@code chainedComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toChained(final Comparable<? super GItem> comparable) {
			return ComparableBuilder.from(Comparables.chainedComparable(this.result, comparable));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum navigierenden {@link #get() Comparable} zurück.
		 *
		 * @see Comparables#navigatedComparable(Getter, Comparable)
		 * @param <GItem2> Typ der Eingabe des {@link Getter} sowie der Elemente des erzeugten {@link Comparable}.
		 * @param navigator {@link Getter} zur Navigation.
		 * @return neuer {@link ComparableBuilder} zu {@code iterableComparable}. */
		public <GItem2> ComparableBuilder<GItem2, Comparable<GItem2>> toNavigated(final Getter<? super GItem2, ? extends GItem> navigator) {
			return ComparableBuilder.from(Comparables.navigatedComparable(navigator, this.result));
		}

	}

	/** Diese Methode gibt einen {@link Producer} zurück, der den gegebenen Datensatz bereitstellt.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param item Datensatz.
	 * @return {@code value}-{@link Producer}. */
	public static <GItem> Producer<GItem> itemProducer(final GItem item) {
		return new ItemProducer<>(item);
	}

	/** Diese Methode ist eine Abkürzung für {@code nativeProducer(Natives.parse(memberText))}.
	 *
	 * @see Natives#parse(String)
	 * @see #nativeProducer(java.lang.reflect.Method)
	 * @see #nativeProducer(java.lang.reflect.Constructor)
	 * @param <GItem> Typ des Datensatzes.
	 * @param memberText Methoden- oder Konstruktortext.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws ReflectiveOperationException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst. */
	public static <GItem> Producer<GItem> nativeProducer(final String memberText)
		throws NullPointerException, IllegalArgumentException, ReflectiveOperationException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Method) return Producers.nativeProducer((java.lang.reflect.Method)object);
		if (object instanceof java.lang.reflect.Constructor<?>) return Producers.nativeProducer((java.lang.reflect.Constructor<?>)object);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt einen {@link Producer} zur gegebenen {@link java.lang.reflect.Method nativen statischen Methode} zurück.<br>
	 * Der vom gelieferten {@link Producer} erzeugte Datensatz entspricht {@code method.invoke(null)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GItem> Typ des Datensatzes.
	 * @param method Native statische Methode.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Methode nicht statisch oder nicht parameterlos ist. */
	public static <GItem> Producer<GItem> nativeProducer(final java.lang.reflect.Method method) throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(method);
	}

	/** Diese Methode gibt einen {@link Producer} zum gegebenen {@link java.lang.reflect.Constructor nativen Kontruktor} zurück.<br>
	 * Der vom gelieferten {@link Producer} erzeugte Datensatz entspricht {@code constructor.newInstance()}.
	 *
	 * @see java.lang.reflect.Constructor#newInstance(Object...)
	 * @param <GItem> Typ des Datensatzes.
	 * @param constructor Nativer Kontruktor.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static <GItem> Producer<GItem> nativeProducer(final java.lang.reflect.Constructor<?> constructor) throws NullPointerException {
		return new ConstructorProducer<>(constructor);
	}

	/** Diese Methode gibt einen gepufferten {@link Producer} zurück, der den vonm gegebenen {@link Producer} erzeugten Datensatz mit Hilfe eines
	 * {@link SoftPointer} verwaltet.
	 *
	 * @see #bufferedProducer(int, Producer)
	 * @param <GItem> Typ des Datensatzes.
	 * @param producer {@link Producer}.
	 * @return {@code buffered}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist. */
	public static <GItem> Producer<GItem> bufferedProducer(final Producer<? extends GItem> producer) throws NullPointerException {
		return Producers.bufferedProducer(Pointers.SOFT, producer);
	}

	/** Diese Methode gibt einen gepufferten {@link Producer} zurück, der den vonm gegebenen {@link Producer} erzeugten Datensatz mit Hilfe eines {@link Pointer}
	 * im gegebenenen Modus verwaltet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param producer {@link Producer}.
	 * @return {@code buffered}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code mode} ungültig ist. */
	public static <GItem> Producer<GItem> bufferedProducer(final int mode, final Producer<? extends GItem> producer)
		throws NullPointerException, IllegalArgumentException {
		return new BufferedProducer<>(mode, producer);
	}

	/** Diese Methode gibt einen umgewandelten {@link Producer} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Getter} aus dem Datensatz des gegebenen
	 * {@link Producer} ermittelt wird.
	 *
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Producer} sowie der Eingabe des gegebenen {@link Getter}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Getter} sowie des Datensatzes.
	 * @param navigator {@link Getter}.
	 * @param producer {@link Producer}.
	 * @return {@code navigated}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code producer} {@code null} ist. */
	public static <GInput, GOutput> Producer<GOutput> navigatedBuilder(final Getter<? super GInput, ? extends GOutput> navigator,
		final Producer<? extends GInput> producer) throws NullPointerException {
		return new NavigatedProducer<>(navigator, producer);
	}

	/** Diese Methode ist eine Abkürzung für {@code synchronizedProducer(producer, producer)}.
	 *
	 * @see #synchronizedProducer(Producer, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem> Producer<GItem> synchronizedProducer(final Producer<? extends GItem> producer) throws NullPointerException {
		return Producers.synchronizedProducer(producer, producer);
	}

	/** Diese Methode gibt einen synchronisierten {@link Producer} zurück, der den gegebenen {@link Producer} via {@code synchronized(mutex)} synchronisiert.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param producer {@link Producer}.
	 * @param mutex Synchronisationsobjekt.
	 * @return {@code synchronized}-{@link Producer}.
	 * @throws NullPointerException Wenn der {@code producer} bzw. {@code mutex} {@code null} ist. */
	public static <GItem> Producer<GItem> synchronizedProducer(final Object mutex, final Producer<? extends GItem> producer) throws NullPointerException {
		return new SynchronizedProducer<>(mutex, producer);
	}

}
