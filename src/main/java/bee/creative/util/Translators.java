package bee.creative.util;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	/** Diese Klasse implementiert einen Translator2, welcher kein Quell- und Zielobjekt akzeptiert.
	 *
	 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	@SuppressWarnings ("javadoc")
	public static class EmptyTranslator extends AbstractTranslator<Object, Object> {

		public static final Translator2<?, ?> INSTANCE = new EmptyTranslator();

	}

	/** Diese Klasse implementiert einen verkettenden {@link Translator2}.
	 *
	 * @param <GSource> Typ der Quellobjekte dieses sowie des ersten {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte dieses sowie des zweiten {@link Translator}.
	 * @param <GCenter> Typ der Zielobjekte des ersten sowie der Quellobjekte des zweiten {@link Translator}. */
	@SuppressWarnings ("javadoc")
	public static class ConcatTranslator<GSource, GTarget, GCenter> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GSource, GCenter> that1;

		public final Translator<GCenter, GTarget> that2;

		public ConcatTranslator(final Translator<GSource, GCenter> that1, final Translator<GCenter, GTarget> that2) throws NullPointerException {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.that2.isTarget(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.that1.isSource(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.that2.toTarget(this.that1.toTarget(object));
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.that1.toSource(this.that2.toSource(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that1, this.that2);
		}

	}

	/** Diese Klasse implementiert einen {@link Translator2}, welcher die Übersetzung eines gegebenen {@link Translator} umkehrt.
	 *
	 * @param <GSource> Typ der Quellobjekte dieses sowie der Zielobjekte des gegebenen {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte dieses sowie der Quellobjekte des gegebenen {@link Translator}. */
	@SuppressWarnings ("javadoc")
	public static class ReverseTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GTarget, GSource> that;

		public ReverseTranslator(final Translator<GTarget, GSource> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.that.isSource(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.that.isTarget(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.that.toSource(object);
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.that.toTarget(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen zusammengesetzten {@link Translator2}, welcher Quell- und Zielobjekte an ihren {@link Class Klassen} erkennt und zur
	 * Umwandlung dieser ineinander entsprechende {@link Getter} nutzt.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte. */
	@SuppressWarnings ("javadoc")
	public static class CompositeTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Class<GSource> sourceClass;

		public final Class<GTarget> targetClass;

		public final Getter<? super GSource, ? extends GTarget> sourceTrans;

		public final Getter<? super GTarget, ? extends GSource> targetTrans;

		public CompositeTranslator(final Class<GSource> sourceClass, final Class<GTarget> targetClass, final Getter<? super GSource, ? extends GTarget> sourceTrans,
			final Getter<? super GTarget, ? extends GSource> targetTrans) throws NullPointerException {
			this.sourceClass = Objects.notNull(sourceClass);
			this.targetClass = Objects.notNull(targetClass);
			this.sourceTrans = Objects.notNull(sourceTrans);
			this.targetTrans = Objects.notNull(targetTrans);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.targetClass.isInstance(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.sourceClass.isInstance(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.sourceTrans.get(this.sourceClass.cast(object));
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.targetTrans.get(this.targetClass.cast(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sourceClass, this.targetClass, this.sourceTrans, this.targetTrans);
		}

	}

	/** Diese Klasse implementiert einen synchronisierten {@link Translator2}, welcher einen gegebenen {@link Translator} über {@code synchronized(this.mutex)}
	 * synchronisiert. Wenn dieses Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GSource, GTarget> that;

		public final Object mutex;

		public SynchronizedTranslator(final Translator<GSource, GTarget> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean isTarget(final Object object) {
			synchronized (this.mutex) {
				return this.that.isSource(object);
			}
		}

		@Override
		public boolean isSource(final Object object) {
			synchronized (this.mutex) {
				return this.that.isTarget(object);
			}
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.that.toTarget(object);
			}
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.that.toSource(object);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	/** Diese Methode liefert {@link EmptyTranslator EmptyTranslator.INSTANCE}. */
	@SuppressWarnings ("unchecked")
	public static <GSource, GTarget> Translator2<GSource, GTarget> empty() {
		return (Translator2<GSource, GTarget>)EmptyTranslator.INSTANCE;
	}

	/** Diese Methode liefert einen neutralen {@link Translator2} und ist eine Abkürzung für {@link Translators#neutral(Class)
	 * Translators.neutral(Object.class)}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Translator2<GValue, GValue> neutral() {
		return (Translator2<GValue, GValue>)neutral(Object.class);
	}

	/** Diese Methode liefert einen neutralen {@link Translator2} und ist eine Abkürzung für {@link Translators#from(Class, Class, Getter, Getter)
	 * Translators.from(valueClass, valueClass, Getters.neutral(), Getters.neutral())}. */
	public static <GValue> Translator2<GValue, GValue> neutral(final Class<GValue> valueClass) throws NullPointerException {
		return Translators.from(valueClass, valueClass, Getters.<GValue>neutral(), Getters.<GValue>neutral());
	}

	/** Diese Methode gibt den gegebenen {@link Translator} als {@link Translator2} zurück. Wenn er {@code null} ist, wird {@link Translators#empty()
	 * Translators.empty()} geliefert. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> from(final Translator<GSource, GTarget> that) {
		if (that == null) return Translators.empty();
		if (that instanceof Translator2) return (Translator2<GSource, GTarget>)that;
		return Translators.reverse(Translators.reverse(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeTranslator new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> from(final Class<GSource> sourceClass, final Class<GTarget> targetClass,
		final Getter<? super GSource, ? extends GTarget> sourceTrans, final Getter<? super GTarget, ? extends GSource> targetTrans) throws NullPointerException {
		return new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatTranslator new ConcatTranslator<>(that, trans)}. */
	public static <GSource, GCenter, GTarget> Translator2<GSource, GTarget> concat(final Translator<GSource, GCenter> that,
		final Translator<GCenter, GTarget> trans) throws NullPointerException {
		return new ConcatTranslator<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseTranslator new ReverseTranslator<>(that)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> reverse(final Translator<GTarget, GSource> that) throws NullPointerException {
		return new ReverseTranslator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Translator, Object) Translators.synchronize(that, that)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> synchronize(final Translator<GSource, GTarget> that) throws NullPointerException {
		return Translators.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedTranslator new SynchronizedTranslator<>(that, mutex)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> synchronize(final Translator<GSource, GTarget> that, final Object mutex)
		throws NullPointerException {
		return new SynchronizedTranslator<>(that, mutex);
	}

}
