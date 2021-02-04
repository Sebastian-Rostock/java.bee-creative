package bee.creative.util;

import java.util.Map.Entry;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Entry2}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Entries {

	/** Diese Klasse implementiert ein unveränderliches {@link Entry2} mit {@code null} als Schlüssel und Wert. */
	@SuppressWarnings ("javadoc")
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
	@SuppressWarnings ("javadoc")
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
	@SuppressWarnings ("javadoc")
	public static class ReverseEntry<GKey, GValue> extends AbstractEntry<GKey, GValue> {

		public final Entry<GValue, GKey> source;

		public final Entry2<GValue, GKey> source2;

		public ReverseEntry(final Entry<GValue, GKey> source) throws NullPointerException {
			this.source = Objects.notNull(source);
			this.source2 = source instanceof Entry2<?, ?> ? (Entry2<GValue, GKey>)source : null;
		}

		@Override
		public GKey getKey() {
			return this.source.getValue();
		}

		@Override
		public GValue getValue() {
			return this.source.getKey();
		}

		@Override
		public Entry2<GKey, GValue> useKey(final GKey key) throws UnsupportedOperationException {
			this.source.setValue(key);
			return this;
		}

		@Override
		public Entry2<GKey, GValue> useValue(final GValue value) throws UnsupportedOperationException {
			if (this.source2 == null) throw new UnsupportedOperationException();
			this.source2.setKey(value);
			return this;
		}

		@Override
		public Entry2<GValue, GKey> reverse() {
			if (this.source2 != null) return this.source2;
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

	/** Diese Methode liefert das {@link EmptyEntry}. */
	@SuppressWarnings ("unchecked")
	public static <GKey, GValue> Entry2<GKey, GValue> empty() {
		return (Entry2<GKey, GValue>)EmptyEntry.INSTANCE;
	}

	/** Diese Methode liefert das gegebene {@link Entry} als {@link Entry2}. Wenn es {@code null} ist, wird das {@link EmptyEntry} geliefert. */
	public static <GKey, GValue> Entry2<GKey, GValue> from(final Entry<GKey, GValue> source) throws NullPointerException {
		if (source == null) return Entries.empty();
		if (source instanceof Entry2<?, ?>) return (Entry2<GKey, GValue>)source;
		return Entries.reverse(source).reverse();
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

	/** Diese Methode ist eine Abkürzung für {@link ReverseEntry new ReverseEntry<>(source)}. */
	public static <GKey, GValue> Entry2<GKey, GValue> reverse(final Entry<GValue, GKey> source) {
		return new ReverseEntry<>(source);
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
