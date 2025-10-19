package bee.creative.util;

import java.util.Map.Entry;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Entry}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Entries {

	/** Diese Klasse implementiert ein unveränderliches {@link Entry2} mit {@code null} als Schlüssel und Wert. */
	public static class EmptyEntry extends AbstractEntry<Object, Object> {

		public static final Entry2<?, ?> INSTANCE = new EmptyEntry();

		@Override
		public Entry2<Object, Object> reverse() {
			return this;
		}

	}

	/** Diese Klasse implementiert ein {@link Entry2} mit veränderlichem Schlüssel und Wert.
	 *
	 * @param <GKey> Typ des Schlüssels.
	 * @param <GValue> Typ des Werts. */
	public static class ValueEnrty<GKey, GValue> extends AbstractEntry<GKey, GValue> {

		public GKey key;

		public GValue value;

		public ValueEnrty(final GKey key, final GValue value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public GKey getKey() {
			return this.key;
		}

		@Override
		public GValue getValue() {
			return this.value;
		}

		@Override
		public Entry2<GKey, GValue> useKey(final GKey key) {
			this.key = key;
			return this;
		}

		@Override
		public Entry2<GKey, GValue> useValue(final GValue value) {
			this.value = value;
			return this;
		}

	}

	/** Diese Klasse implementiert ein umkehrendes {@link Entry2}, welches Schlüssel und Wert eines gegebenen {@link Entry} miteinander tauscht.
	 *
	 * @param <GKey> Typ des Schlüssels.
	 * @param <GValue> Typ des Werts dieses sowie des Schlüssels des gegebenen {@link Entry}. */
	public static class ReverseEntry<GKey, GValue> extends AbstractEntry<GKey, GValue> {

		public final Entry<GValue, GKey> that;

		final Entry2<GValue, GKey> that2;

		public ReverseEntry(final Entry<GValue, GKey> that) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.that2 = that instanceof Entry2<?, ?> ? (Entry2<GValue, GKey>)that : null;
		}

		@Override
		public GKey getKey() {
			return this.that.getValue();
		}

		@Override
		public GValue getValue() {
			return this.that.getKey();
		}

		@Override
		public Entry2<GKey, GValue> useKey(final GKey key) throws UnsupportedOperationException {
			this.that.setValue(key);
			return this;
		}

		@Override
		public Entry2<GKey, GValue> useValue(final GValue value) throws UnsupportedOperationException {
			if (this.that2 == null) throw new UnsupportedOperationException();
			this.that2.setKey(value);
			return this;
		}

		@Override
		public Entry2<GValue, GKey> reverse() {
			if (this.that2 != null) return this.that2;
			return new ReverseEntry<>(this);
		}

	}

	static class KeyGetter extends AbstractGetter<Entry<?, ?>, Object> {

		static final Getter3<?, ?> INSTANCE = new KeyGetter();

		@Override
		public Object get(final Entry<?, ?> input) {
			return input.getKey();
		}

	}

	static class ValueField extends AbstractGetter<Entry<?, ?>, Object> {

		static final Getter3<?, ?> INSTANCE = new ValueField();

		@Override
		public Object get(final Entry<?, ?> item) {
			return item.getValue();
		}

	}

	static class EntryConsumer<GKey, GValue> extends AbstractConsumer<Entry2<GKey, GValue>> {

		public final Setter<? super GKey, ? super GValue> that;

		public EntryConsumer(Setter<? super GKey, ? super GValue> that) {
			this.that = Objects.notNull(that);
		}

		@Override
		public void set(Entry2<GKey, GValue> entry) {
			this.that.set(entry.getKey(), entry.getValue());
		}

	}

	/** Diese Methode liefert das {@link EmptyEntry}. */
	@SuppressWarnings ("unchecked")
	public static <GKey, GValue> Entry2<GKey, GValue> empty() {
		return (Entry2<GKey, GValue>)EmptyEntry.INSTANCE;
	}

	/** Diese Methode liefert das gegebene {@link Entry} als {@link Entry2}. Wenn es {@code null} ist, wird das {@link EmptyEntry} geliefert. */
	public static <GKey, GValue> Entry2<GKey, GValue> from(final Entry<GKey, GValue> that) throws NullPointerException {
		if (that == null) return Entries.empty();
		if (that instanceof Entry2<?, ?>) return (Entry2<GKey, GValue>)that;
		return Entries.reverse(that).reverse();
	}

	/** Diese Methode ist eine Abkürzung für {@link ValueEnrty new ValueEnrty<>(key, value)}. */
	public static <GKey, GValue> Entry2<GKey, GValue> from(final GKey key, final GValue value) {
		return new ValueEnrty<>(key, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Object, Object) Entries.from(key, value.get(key))}. */
	public static <GKey, GValue> Entry2<GKey, GValue> fromKey(final GKey key, final Getter<? super GKey, ? extends GValue> value) throws NullPointerException {
		return Entries.<GKey, GValue>from(key, value.get(key));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Object, Object) Entries.from(key.get(value), value)}. */
	public static <GKey, GValue> Entry2<GKey, GValue> fromValue(final GValue value, final Getter<? super GValue, ? extends GKey> key)
		throws NullPointerException {
		return Entries.<GKey, GValue>from(key.get(value), value);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseEntry new ReverseEntry<>(that)}. */
	public static <GKey, GValue> Entry2<GKey, GValue> reverse(final Entry<GValue, GKey> that) {
		return new ReverseEntry<>(that);
	}

	public static <GKey, GValue> Consumer3<Entry2<GKey, GValue>> consumer(Setter<? super GKey, ? super GValue> that) {
		return new EntryConsumer<>(that);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Entry#getKey()}. */
	@SuppressWarnings ("unchecked")
	public static <GKey> Getter3<Entry<? extends GKey, ?>, GKey> key() {
		return (Getter3<Entry<? extends GKey, ?>, GKey>)KeyGetter.INSTANCE;
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Entry#getValue()}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Getter3<Entry<?, ? extends GValue>, GValue> value() {
		return (Getter3<Entry<?, ? extends GValue>, GValue>)ValueField.INSTANCE;
	}

}
