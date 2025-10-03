package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.lang.Objects.toInvokeString;
import static bee.creative.util.Getters.neutralGetter;

/** Diese Klasse implementiert grundlegende {@link Setter}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	/** Diese Methode liefert den gegebenen {@link Setter} als {@link Setter3}. Wenn er {@code null} ist, wird der {@link EmptySetter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFrom(Setter<? super ITEM, ? super VALUE> that) {
		if (that == null) return emptySetter();
		if (that instanceof Setter3) return (Setter3<ITEM, VALUE>)that;
		return translatedSetter(that, neutralGetter());
	}

	/** Diese Methode liefert den gegebenen {@link Setter3} zu {@link Consumer#set(Object)}. */
	public static <VALUE> Setter3<Object, VALUE> setterFrom(Consumer<? super VALUE> that) {
		notNull(that);
		return (item, value) -> that.set(value);
	}

	/** Diese Methode liefert den {@link EmptySetter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> emptySetter() {
		return (Setter3<ITEM, VALUE>)EmptySetter.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatSetter new ConcatSetter<>(that, trans)}. */
	public static <ITEM, ITEM2, VALUE> Setter3<ITEM, VALUE> concatSetter(Getter<? super ITEM, ? extends ITEM2> trans, Setter<? super ITEM2, ? super VALUE> that)
		throws NullPointerException {
		return new ConcatSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSetter new TranslatedSetter<>(that, trans)}. */
	public static <ITEM, VALUE, VALUE2> Setter3<ITEM, VALUE> translatedSetter(Setter<? super ITEM, ? super VALUE2> that,
		Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
		return new TranslatedSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link AggregatedSetter new AggregatedSetter<>(that, trans)}. */
	public static <ITEM, VALUE, VALUE2> Setter3<Iterable<? extends ITEM>, VALUE> aggregatedSetter(Setter<? super ITEM, ? super VALUE2> that,
		Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
		return new AggregatedSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedSetter new OptionalizedSetter<>(that)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> optionalizedSetter(Setter<? super ITEM, VALUE> that) throws NullPointerException {
		return new OptionalizedSetter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedSetter new SynchronizedSetter<>(that, mutex)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> synchronizedSetter(Setter<? super ITEM, ? super VALUE> that, Object mutex) throws NullPointerException {
		return new SynchronizedSetter<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} ignoriert. */
	public static class EmptySetter extends AbstractSetter<Object, Object> {

		public static final Setter3<?, ?> INSTANCE = new EmptySetter();

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, der das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen {@link Getter}
	 * übersetzten Datensatz an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item}
	 * erfolgt über {@code this.that.set(this.trans.get(item), value)}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <ITEM2> Typ des Datensatzes des gegebenen {@link Setter}.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class ConcatSetter<ITEM, ITEM2, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public ConcatSetter(Setter<? super ITEM2, ? super VALUE> that, Getter<? super ITEM, ? extends ITEM2> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			this.that.set(this.trans.get(item), value);
		}

		@Override
		public String toString() {
			return toInvokeString(this, this.that, this.trans);
		}

		private final Setter<? super ITEM2, ? super VALUE> that;

		private final Getter<? super ITEM, ? extends ITEM2> trans;

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, der das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen {@link Getter}
	 * übersetzten Wert an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} erfolgt
	 * über {@code this.that.set(item, this.trans.get(value))}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft.
	 * @param <VALUE2> Typ des Datensatzes des Übersetzers sowie des Werts der Eigenschaft. */
	public static class TranslatedSetter<ITEM, VALUE, VALUE2> extends AbstractSetter<ITEM, VALUE> {

		public TranslatedSetter(Setter<? super ITEM, ? super VALUE2> that, Getter<? super VALUE, ? extends VALUE2> trans) {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			this.that.set(item, this.trans.get(value));
		}

		@Override
		public String toString() {
			return toInvokeString(this, this.that, this.trans);
		}

		private final Setter<? super ITEM, ? super VALUE2> that;

		private final Getter<? super VALUE, ? extends VALUE2> trans;

	}

	/** Diese Klasse implementiert einen aggregierten {@link Setter3}, der das {@link #set(Iterable, Object) Schreiben} mit dem über einen gegebenen
	 * {@link Getter} übersetzten Wert für jedes Element des iterierbaren Datensatzes an einen gegebenen {@link Setter} delegeirt. Wenn der iterierbare Datensatz
	 * {@code null} oder leer ist, wird das Setzen ignoriert.
	 *
	 * @param <ITEM> Typ der Elemente des iterierbaren Datensatzes.
	 * @param <VALUE> Typ des Werts.
	 * @param <VALUE2> Typ des Werts der Eigenschaft der Elemente. */
	public static class AggregatedSetter<ITEM, VALUE, VALUE2> extends AbstractSetter<Iterable<? extends ITEM>, VALUE> {

		public AggregatedSetter(Setter<? super ITEM, VALUE2> that, Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public void set(Iterable<? extends ITEM> item, VALUE value) {
			if (item == null) return;
			var iterator = item.iterator();
			if (!iterator.hasNext()) return;
			var value2 = this.trans.get(value);
			do {
				this.that.set(iterator.next(), value2);
			} while (iterator.hasNext());
		}

		@Override
		public String toString() {
			return toInvokeString(this, this.that, this.trans);
		}

		private final Setter<? super ITEM, VALUE2> that;

		private final Getter<? super VALUE, ? extends VALUE2> trans;

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} nur dann an einen gegebenen {@link Setter} delegiert,
	 * wenn die Eingabe nicht {@code null} ist.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class OptionalizedSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public OptionalizedSetter(Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
			this.that = notNull(that);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			if (item == null) return;
			this.that.set(item, value);
		}

		@Override
		public String toString() {
			return toInvokeString(this, this.that);
		}

		private final Setter<? super ITEM, ? super VALUE> that;

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der einen gegebenen {@link Setter} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class SynchronizedSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public SynchronizedSetter(Setter<? super ITEM, ? super VALUE> that, Object mutex) throws NullPointerException {
			this.that = notNull(that);
			this.mutex = notNull(mutex, this);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			synchronized (this.mutex) {
				this.that.set(item, value);
			}
		}

		@Override
		public String toString() {
			return toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

		private final Setter<? super ITEM, ? super VALUE> that;

		private final Object mutex;

	}

}
