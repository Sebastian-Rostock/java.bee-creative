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

		public final Translator<GSource, GCenter> trans1;

		public final Translator<GCenter, GTarget> trans2;

		public ConcatTranslator(final Translator<GSource, GCenter> trans1, final Translator<GCenter, GTarget> trans2) throws NullPointerException {
			this.trans1 = Objects.notNull(trans1);
			this.trans2 = Objects.notNull(trans2);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.trans2.isTarget(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.trans1.isSource(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.trans2.toTarget(this.trans1.toTarget(object));
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.trans1.toSource(this.trans2.toSource(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.trans1, this.trans2);
		}

	}

	/** Diese Klasse implementiert einen {@link Translator2}, welcher die Übersetzung eines gegebenen {@link Translator} umkehrt.
	 *
	 * @param <GSource> Typ der Quellobjekte dieses sowie der Zielobjekte des gegebenen {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte dieses sowie der Quellobjekte des gegebenen {@link Translator}. */
	@SuppressWarnings ("javadoc")
	public static class ReverseTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GTarget, GSource> target;

		public ReverseTranslator(final Translator<GTarget, GSource> translator) throws NullPointerException {
			this.target = Objects.notNull(translator);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.target.isSource(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.target.isTarget(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.target.toSource(object);
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.target.toTarget(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
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

		public final Translator<GSource, GTarget> target;

		public final Object mutex;

		public SynchronizedTranslator(final Translator<GSource, GTarget> target, final Object mutex) throws NullPointerException {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean isTarget(final Object object) {
			synchronized (this.mutex) {
				return this.target.isSource(object);
			}
		}

		@Override
		public boolean isSource(final Object object) {
			synchronized (this.mutex) {
				return this.target.isTarget(object);
			}
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.target.toTarget(object);
			}
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.target.toSource(object);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
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
	public static <GSource, GTarget> Translator2<GSource, GTarget> from(final Translator<GSource, GTarget> target) {
		if (target == null) return Translators.empty();
		if (target instanceof Translator2) return (Translator2<GSource, GTarget>)target;
		return Translators.reverse(Translators.reverse(target));
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeTranslator new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> from(final Class<GSource> sourceClass, final Class<GTarget> targetClass,
		final Getter<? super GSource, ? extends GTarget> sourceTrans, final Getter<? super GTarget, ? extends GSource> targetTrans) throws NullPointerException {
		return new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatTranslator new ConcatTranslator<>(target, trans)}. */
	public static <GSource, GCenter, GTarget> Translator2<GSource, GTarget> concat(final Translator<GSource, GCenter> target,
		final Translator<GCenter, GTarget> trans) throws NullPointerException {
		return new ConcatTranslator<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseTranslator new ReverseTranslator<>(target)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> reverse(final Translator<GTarget, GSource> target) throws NullPointerException {
		return new ReverseTranslator<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Translator, Object) Translators.synchronize(target, target)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> synchronize(final Translator<GSource, GTarget> target) throws NullPointerException {
		return Translators.synchronize(target, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedTranslator new SynchronizedTranslator<>(target, mutex)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> synchronize(final Translator<GSource, GTarget> target, final Object mutex)
		throws NullPointerException {
		return new SynchronizedTranslator<>(target, mutex);
	}

}
