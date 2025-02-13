package bee.creative.util;

import java.util.Arrays;
import java.util.Map;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	/** Diese Methode liefert den {@link EmptyTranslator}. */
	@SuppressWarnings ("unchecked")
	public static <GSource, GTarget> Translator2<GSource, GTarget> empty() {
		return (Translator2<GSource, GTarget>)EmptyTranslator.INSTANCE;
	}

	/** Diese Methode liefert einen neutralen {@link Translator2} und ist eine Abkürzung für {@link Translators#neutral(Class)
	 * Translators.neutral(Object.class)}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Translator2<GValue, GValue> neutral() {
		return (Translator2<GValue, GValue>)Translators.neutral(Object.class);
	}

	/** Diese Methode liefert einen neutralen {@link Translator2} und ist eine Abkürzung für {@link Translators#from(Class, Class, Getter, Getter)
	 * Translators.from(valueClass, valueClass, Getters.neutral(), Getters.neutral())}. */
	public static <GValue> Translator2<GValue, GValue> neutral(Class<GValue> valueClass) throws NullPointerException {
		return Translators.from(valueClass, valueClass, Getters.<GValue>neutral(), Getters.<GValue>neutral());
	}

	/** Diese Methode liefert den gegebenen {@link Translator} als {@link Translator2}. Wenn er {@code null} ist, wird der {@link EmptyTranslator} geliefert. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> from(Translator<GSource, GTarget> that) {
		if (that == null) return Translators.empty();
		if (that instanceof Translator2) return (Translator2<GSource, GTarget>)that;
		return Translators.reverse(Translators.reverse(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeTranslator new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> from(Class<GSource> sourceClass, Class<GTarget> targetClass,
		Getter<? super GSource, ? extends GTarget> sourceTrans, Getter<? super GTarget, ? extends GSource> targetTrans) throws NullPointerException {
		return new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans);
	}

	public static <GEnum extends Enum<?>> Translator2<String, GEnum> fromEnum(Class<GEnum> enumClass) throws NullPointerException {
		return Translators.fromEnum(Enum::name, enumClass.getEnumConstants());
	}

	public static <GSource, GTarget> Translator2<GSource, GTarget> fromEnum(Map<GTarget, GSource> sourceByTarget) throws NullPointerException {
		return Translators.fromEnum(sourceByTarget::get, sourceByTarget.keySet());
	}

	@SafeVarargs
	public static <GSource, GTarget> Translator2<GSource, GTarget> fromEnum(Getter<GTarget, GSource> ident, GTarget... targets) throws NullPointerException {
		return Translators.fromEnum(ident, Arrays.asList(targets));
	}

	public static <GSource, GTarget> Translator2<GSource, GTarget> fromEnum(Getter<GTarget, GSource> ident, Iterable<GTarget> targets)
		throws NullPointerException {
		return new EnumTranslator<>(ident, targets);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatTranslator new ConcatTranslator<>(that, trans)}. */
	public static <GSource, GCenter, GTarget> Translator2<GSource, GTarget> concat(Translator<GSource, GCenter> that, Translator<GCenter, GTarget> trans)
		throws NullPointerException {
		return new ConcatTranslator<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseTranslator new ReverseTranslator<>(that)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> reverse(Translator<GTarget, GSource> that) throws NullPointerException {
		return new ReverseTranslator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedTranslator new OptionalizedTranslator<>(that, mutex)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> optionalize(Translator<GSource, GTarget> that) throws NullPointerException {
		return new OptionalizedTranslator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Translator, Object) Translators.synchronize(that, that)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> synchronize(Translator<GSource, GTarget> that) throws NullPointerException {
		return Translators.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedTranslator new SynchronizedTranslator<>(that, mutex)}. */
	public static <GSource, GTarget> Translator2<GSource, GTarget> synchronize(Translator<GSource, GTarget> that, Object mutex) throws NullPointerException {
		return new SynchronizedTranslator<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Translator2}, der alle Quell- und Zielobjekte ablehnt.
	 *
	 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class EmptyTranslator extends AbstractTranslator<Object, Object> {

		public static final Translator2<?, ?> INSTANCE = new EmptyTranslator();

	}

	/** Diese Klasse implementiert einen {@link Translator2}, der alle Quellobjekte in deren Textdarstellung übersetzt.
	 *
	 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class StringTranslator extends AbstractTranslator<Object, String> {

		public static final StringTranslator INSTANCE = new StringTranslator();

		@Override
		public boolean isSource(Object object) {
			return object != null;
		}

		@Override
		public boolean isTarget(Object object) {
			return object instanceof String;
		}

		@Override
		public Object toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return object;
		}

		@Override
		public String toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return object.toString();
		}

	}

	public static class EnumTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Getter<GTarget, GSource> ident;

		public final Iterable<GTarget> targets;

		public EnumTranslator(Getter<GTarget, GSource> ident, Iterable<GTarget> targets) {
			this.ident = ident;
			this.targets = targets;
			this.toTargetMap = new HashMap<>(100);
			this.toSourceMap = new HashMap<>(100);
			targets.forEach(target -> {
				var source = ident.get(target);
				this.toTargetMap.put(source, target);
				this.toSourceMap.put(target, source);
			});
			this.toTargetMap.compact();
			this.toSourceMap.compact();
		}

		@Override
		public boolean isTarget(Object object) {
			return this.toSourceMap.containsKey(object);
		}

		@Override
		public boolean isSource(Object object) {
			return this.toTargetMap.containsKey(object);
		}

		@Override
		public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.toTargetMap.get(object);
		}

		@Override
		public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.toSourceMap.get(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.ident, this.targets);
		}

		private final HashMap<GSource, GTarget> toTargetMap;

		private final HashMap<GTarget, GSource> toSourceMap;

	}

	/** Diese Klasse implementiert einen verketteten {@link Translator2}.
	 *
	 * @param <GSource> Typ der Quellobjekte dieses sowie des ersten {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte dieses sowie des zweiten {@link Translator}.
	 * @param <GCenter> Typ der Zielobjekte des ersten sowie der Quellobjekte des zweiten {@link Translator}. */
	public static class ConcatTranslator<GSource, GTarget, GCenter> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GSource, GCenter> that1;

		public final Translator<GCenter, GTarget> that2;

		public ConcatTranslator(Translator<GSource, GCenter> that1, Translator<GCenter, GTarget> that2) throws NullPointerException {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public boolean isTarget(Object object) {
			return this.that2.isTarget(object);
		}

		@Override
		public boolean isSource(Object object) {
			return this.that1.isSource(object);
		}

		@Override
		public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that2.toTarget(this.that1.toTarget(object));
		}

		@Override
		public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that1.toSource(this.that2.toSource(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that1, this.that2);
		}

	}

	/** Diese Klasse implementiert einen {@link Translator2}, der die Übersetzung eines gegebenen {@link Translator} umkehrt.
	 *
	 * @param <GSource> Typ der Quellobjekte dieses sowie der Zielobjekte des gegebenen {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte dieses sowie der Quellobjekte des gegebenen {@link Translator}. */
	public static class ReverseTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GTarget, GSource> that;

		public ReverseTranslator(Translator<GTarget, GSource> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean isTarget(Object object) {
			return this.that.isSource(object);
		}

		@Override
		public boolean isSource(Object object) {
			return this.that.isTarget(object);
		}

		@Override
		public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that.toSource(object);
		}

		@Override
		public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that.toTarget(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen zusammengesetzten {@link Translator2}, der Quell- und Zielobjekte an ihren {@link Class Klassen} erkennt und zur
	 * Umwandlung dieser ineinander entsprechende gegebene {@link Getter} verwendet.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte. */
	public static class CompositeTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Class<GSource> sourceClass;

		public final Class<GTarget> targetClass;

		public final Getter<? super GSource, ? extends GTarget> sourceTrans;

		public final Getter<? super GTarget, ? extends GSource> targetTrans;

		public CompositeTranslator(Class<GSource> sourceClass, Class<GTarget> targetClass, Getter<? super GSource, ? extends GTarget> sourceTrans,
			Getter<? super GTarget, ? extends GSource> targetTrans) throws NullPointerException {
			this.sourceClass = Objects.notNull(sourceClass);
			this.targetClass = Objects.notNull(targetClass);
			this.sourceTrans = Objects.notNull(sourceTrans);
			this.targetTrans = Objects.notNull(targetTrans);
		}

		@Override
		public boolean isTarget(Object object) {
			return this.targetClass.isInstance(object);
		}

		@Override
		public boolean isSource(Object object) {
			return this.sourceClass.isInstance(object);
		}

		@Override
		public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.sourceTrans.get(this.sourceClass.cast(object));
		}

		@Override
		public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.targetTrans.get(this.targetClass.cast(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sourceClass, this.targetClass, this.sourceTrans, this.targetTrans);
		}

	}

	/** Diese Klasse implementiert einen {@link Translator2}, der die Übersetzung eines gegebenen {@link Translator} {@code null}-tollerant macht.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte. */
	public static class OptionalizedTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GSource, GTarget> that;

		public OptionalizedTranslator(Translator<GSource, GTarget> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean isTarget(Object object) {
			return (object == null) || this.that.isTarget(object);
		}

		@Override
		public boolean isSource(Object object) {
			return (object == null) || this.that.isSource(object);
		}

		@Override
		public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return object != null ? this.that.toTarget(object) : null;
		}

		@Override
		public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return object != null ? this.that.toSource(object) : null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen synchronisierten {@link Translator2}, der einen gegebenen {@link Translator} über {@code synchronized(this.mutex)}
	 * synchronisiert. Wenn dieses Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte. */
	public static class SynchronizedTranslator<GSource, GTarget> extends AbstractTranslator<GSource, GTarget> {

		public final Translator<GSource, GTarget> that;

		public final Object mutex;

		public SynchronizedTranslator(Translator<GSource, GTarget> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean isTarget(Object object) {
			synchronized (this.mutex) {
				return this.that.isSource(object);
			}
		}

		@Override
		public boolean isSource(Object object) {
			synchronized (this.mutex) {
				return this.that.isTarget(object);
			}
		}

		@Override
		public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.that.toTarget(object);
			}
		}

		@Override
		public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.that.toSource(object);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

}
