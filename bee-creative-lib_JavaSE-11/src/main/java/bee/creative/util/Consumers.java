package bee.creative.util;

import static bee.creative.util.Getters.neutralGetter;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Consumer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Consumers {

	/** Diese Methode liefert den gegebenen {@link Consumer} als {@link Consumer3}. Wenn er {@code null} ist, wird der {@link EmptyConsumer} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <VALUE> Consumer3<VALUE> consumerFrom(Consumer<? super VALUE> that) {
		if (that == null) return emptyConsumer();
		if (that instanceof Consumer3<?>) return (Consumer3<VALUE>)that;
		return translatedConsumer(that, neutralGetter());
	}

	public static <ITEM, VALUE> Consumer3<VALUE> consumerFromSetter(Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
		return Consumers.consumerFromSetter(that, Producers.emptyProducer());
	}

	/** Diese Methode ist eine Abkürzung für {@link SetterConsumer new SetterConsumer<>(that, item)}. */
	public static <ITEM, VALUE> Consumer3<VALUE> consumerFromSetter(Setter<? super ITEM, ? super VALUE> that, Producer<? extends ITEM> item)
		throws NullPointerException {
		return new SetterConsumer<>(that, item);
	}

	/** Diese Methode liefert den {@link EmptyConsumer}. */
	@SuppressWarnings ("unchecked")
	public static <VALUE> Consumer3<VALUE> emptyConsumer() {
		return (Consumer3<VALUE>)EmptyConsumer.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedConsumer new TranslatedConsumer<>(that, trans)}. */
	public static <VALUE, VALUE2> Consumer3<VALUE2> translatedConsumer(Consumer<? super VALUE> that, Getter<? super VALUE2, ? extends VALUE> trans)
		throws NullPointerException {
		return new TranslatedConsumer<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedConsumer(Consumer, Object) synchronizeConsumer(that, that)}. */
	public static <VALUE> Consumer3<VALUE> synchronizedConsumer(Consumer<? super VALUE> that) {
		return synchronizedConsumer(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedConsumer new SynchronizedConsumer<>(that, mutex)}. */
	public static <VALUE> Consumer3<VALUE> synchronizedConsumer(Consumer<? super VALUE> that, Object mutex) {
		return new SynchronizedConsumer<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Consumer3}, der das {@link #set(Object) Schreiben} ignoriert. */
	public static class EmptyConsumer extends AbstractConsumer<Object> {

		public static final Consumer3<?> INSTANCE = new EmptyConsumer();

	}

	/** Diese Klasse implementiert einen verketteten {@link Consumer3}, der beim {@link #set(Object) Schreiben} den gegebenen Wert an einen gegebenen
	 * {@link Setter} delegiert und dazu den von einem gegebenen {@link Producer} bereitgestellten Datensatz verwendet. Das Schreiben des Werts {@code value}
	 * erfolgt über {@code this.that.set(this.item.get(), value)}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts. */
	public static class SetterConsumer<ITEM, VALUE> extends AbstractConsumer<VALUE> {

		public SetterConsumer(Setter<? super ITEM, ? super VALUE> that, Producer<? extends ITEM> item) {
			this.that = Objects.notNull(that);
			this.item = Objects.notNull(item);
		}

		@Override
		public void set(VALUE value) {
			this.that.set(this.item.get(), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.that);
		}

		private final Setter<? super ITEM, ? super VALUE> that;

		private final Producer<? extends ITEM> item;

	}

	/** Diese Klasse implementiert übersetzten {@link Consumer3}, der den Wert beim {@link #set(Object) Schreiben} über einen gegebenen {@link Getter} in den Wert
	 * eines gegebenen {@link Consumer} überführt. Das Schreiben des Werts {@code value} erfolgt über {@code this.that.set(this.trans.get(value))}.
	 *
	 * @param <VALUE> Typ des Werts dieses {@link Consumer3}.
	 * @param <VALUE2> Typ des Werts des gegebenen {@link Consumer}. */
	public static class TranslatedConsumer<VALUE, VALUE2> extends AbstractConsumer<VALUE> {

		public TranslatedConsumer(Consumer<? super VALUE2> that, Getter<? super VALUE, ? extends VALUE2> trans) {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(VALUE value) {
			this.that.set(this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Consumer<? super VALUE2> that;

		private final Getter<? super VALUE, ? extends VALUE2> trans;

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, der einen gegebenen {@link Consumer} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class SynchronizedConsumer<VALUE> extends AbstractConsumer<VALUE> {

		public SynchronizedConsumer(Consumer<? super VALUE> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public void set(VALUE value) {
			synchronized (this.mutex) {
				this.that.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

		private final Consumer<? super VALUE> that;

		private final Object mutex;

	}

}
