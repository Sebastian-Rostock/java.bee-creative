package bee.creative.util;

import static bee.creative.util.Getters.neutralGetter;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Producer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Methode liefert den gegebenen {@link Producer} als {@link Producer3}. Wenn er {@code null} ist, wird der {@link #emptyProducer()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <VALUE> Producer3<VALUE> producerFrom(Producer<? extends VALUE> that) {
		if (that == null) return emptyProducer();
		if (that instanceof Producer3<?>) return (Producer3<VALUE>)that;
		return translatedProducer(that, neutralGetter());
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@link ValueProducer new ValueProducer<>(that)}. Wenn {@code that} {@code null} ist, wird der
	 * {@link #emptyProducer()} geliefert. */
	public static <VALUE> Producer3<VALUE> producerFromValue(VALUE that) {
		return that == null ? emptyProducer() : new ValueProducer<>(that);
	}

	public static <VALUE> Producer3<VALUE> producerFromGetter(Getter<?, ? extends VALUE> that) {
		return Objects.notNull(that) == Getters.emptyGetter() ? emptyProducer() : () -> that.get(null);
	}

	/** Diese Methode liefert den {@link EmptyProducer}. */
	@SuppressWarnings ("unchecked")
	public static <VALUE> Producer3<VALUE> emptyProducer() {
		return (Producer3<VALUE>)EmptyProducer.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedProducer new TranslatedProducer<>(that, trans)}. */
	public static <VALUE, VALUE2> Producer3<VALUE2> translatedProducer(Producer<? extends VALUE> that, Getter<? super VALUE, ? extends VALUE2> trans)
		throws NullPointerException {
		return new TranslatedProducer<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedProducer(Producer, Object) synchronizedProducer(that, that)}. */
	public static <VALUE> Producer3<VALUE> synchronizedProducer(Producer<? extends VALUE> that) throws NullPointerException {
		return synchronizedProducer(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedProducer new SynchronizedProducer<>(that, mutex)}. */
	public static <VALUE> Producer3<VALUE> synchronizedProducer(Producer<? extends VALUE> that, Object mutex) throws NullPointerException {
		return new SynchronizedProducer<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Producer3}, der beim {@link #get() Lesen} stets {@code null} liefert. */
	public static class EmptyProducer extends AbstractProducer<Object> {

		public static final Producer3<?> INSTANCE = new EmptyProducer();

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der beim {@link #get() Lesen} stets einen gegebenen Wert liefert.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class ValueProducer<VALUE> extends AbstractProducer<VALUE> {

		public ValueProducer(VALUE that) {
			this.that = that;
		}

		@Override
		public VALUE get() {
			return this.that;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

		private final VALUE that;

	}

	/** Diese Klasse implementiert einen übersetzten {@link Producer3}, der beim {@link #get() Lesen} einen Wert liefert, der über einen gegebenen {@link Getter}
	 * aus dem Wert eines gegebenen {@link Producer} ermittelt wird. Das Lesen erfolgt über {@code this.trans.get(this.that.get())}.
	 *
	 * @param <VALUE> Typ des Werts.
	 * @param <VALUE2> Typ des Werts des gegebenen {@link Producer}. */
	public static class TranslatedProducer<VALUE, VALUE2> extends AbstractProducer<VALUE> {

		public TranslatedProducer(Producer<? extends VALUE2> that, Getter<? super VALUE2, ? extends VALUE> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public VALUE get() {
			return this.trans.get(this.that.get());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Producer<? extends VALUE2> that;

		private final Getter<? super VALUE2, ? extends VALUE> trans;

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der einen gegebenen {@link Producer} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class SynchronizedProducer<VALUE> extends AbstractProducer<VALUE> {

		public SynchronizedProducer(Producer<? extends VALUE> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public VALUE get() {
			synchronized (this.mutex) {
				return this.that.get();
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

		private final Producer<? extends VALUE> that;

		private final Object mutex;

	}

}
