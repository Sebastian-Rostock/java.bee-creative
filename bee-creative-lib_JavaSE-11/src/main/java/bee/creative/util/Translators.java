package bee.creative.util;

import static bee.creative.util.Getters.neutralGetter;
import java.util.Arrays;
import java.util.Map;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	/** Diese Methode liefert den gegebenen {@link Translator} als {@link Translator2}. Wenn er {@code null} ist, wird der {@link EmptyTranslator} geliefert. */
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> translatorFrom(Translator<SOURCE, TARGET> that) {
		if (that == null) return emptyTranslator();
		if (that instanceof Translator2) return (Translator2<SOURCE, TARGET>)that;
		return reverseTranslator(reverseTranslator(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeTranslator new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans)}. */
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> translatorFrom(Class<SOURCE> sourceClass, Class<TARGET> targetClass,
		Getter<? super SOURCE, ? extends TARGET> sourceTrans, Getter<? super TARGET, ? extends SOURCE> targetTrans) throws NullPointerException {
		return new CompositeTranslator<>(sourceClass, targetClass, sourceTrans, targetTrans);
	}

	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> translatorFromMap(Map<TARGET, ? extends SOURCE> sourceByTarget) throws NullPointerException {
		return translatorFromEnum(sourceByTarget::get, sourceByTarget.keySet());
	}

	public static <ENUM extends Enum<?>> Translator2<String, ENUM> translatorFromEnum(Class<ENUM> enumClass) throws NullPointerException {
		return translatorFromEnum(Enum::name, enumClass.getEnumConstants());
	}

	@SafeVarargs
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> translatorFromEnum(Getter<? super TARGET, ? extends SOURCE> ident, TARGET... targets)
		throws NullPointerException {
		return translatorFromEnum(ident, Arrays.asList(targets));
	}

	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> translatorFromEnum(Getter<? super TARGET, ? extends SOURCE> ident,
		Iterable<? extends TARGET> targets) throws NullPointerException {
		return new EnumTranslator<>(ident, targets);
	}

	/** Diese Methode liefert den {@link EmptyTranslator}. */
	@SuppressWarnings ("unchecked")
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> emptyTranslator() {
		return (Translator2<SOURCE, TARGET>)EmptyTranslator.INSTANCE;
	}

	/** Diese Methode liefert einen neutralen {@link Translator2} und ist eine Abkürzung für {@link Translators#neutralTranslator(Class)
	 * Translators.neutral(Object.class)}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Translator2<GValue, GValue> neutralTranslator() {
		return (Translator2<GValue, GValue>)neutralTranslator(Object.class);
	}

	/** Diese Methode liefert einen neutralen {@link Translator2} und ist eine Abkürzung für {@link Translators#translatorFrom(Class, Class, Getter, Getter)
	 * Translators.from(valueClass, valueClass, Getters.neutral(), Getters.neutral())}. */
	public static <GValue> Translator2<GValue, GValue> neutralTranslator(Class<GValue> valueClass) throws NullPointerException {
		return translatorFrom(valueClass, valueClass, neutralGetter(), neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatTranslator new ConcatTranslator<>(that, trans)}. */
	public static <SOURCE, CENTER, TARGET> Translator2<SOURCE, TARGET> concatTranslator(Translator<SOURCE, CENTER> that, Translator<CENTER, TARGET> trans)
		throws NullPointerException {
		return new ConcatTranslator<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseTranslator new ReverseTranslator<>(that)}. */
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> reverseTranslator(Translator<TARGET, SOURCE> that) throws NullPointerException {
		return new ReverseTranslator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedTranslator new OptionalizedTranslator<>(that, mutex)}. */
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> optionalizeTranslator(Translator<SOURCE, TARGET> that) throws NullPointerException {
		return new OptionalizedTranslator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizeTranslator(Translator, Object) synchronizeTranslator(that, that)}. */
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> synchronizeTranslator(Translator<SOURCE, TARGET> that) throws NullPointerException {
		return synchronizeTranslator(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedTranslator new SynchronizedTranslator<>(that, mutex)}. */
	public static <SOURCE, TARGET> Translator2<SOURCE, TARGET> synchronizeTranslator(Translator<SOURCE, TARGET> that, Object mutex) throws NullPointerException {
		return new SynchronizedTranslator<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Translator2}, der alle Quell- und Zielobjekte ablehnt.
	 *
	 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class EmptyTranslator extends AbstractTranslator<Object, Object> {

		public static final Translator2<?, ?> INSTANCE = new EmptyTranslator();

	}

	public static class EnumTranslator<SOURCE, TARGET> extends AbstractTranslator<SOURCE, TARGET> {

		public EnumTranslator(Getter<? super TARGET, ? extends SOURCE> ident, Iterable<? extends TARGET> targets) {
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
		public TARGET toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.toTargetMap.get(object);
		}

		@Override
		public SOURCE toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.toSourceMap.get(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.ident, this.targets);
		}

		private final Getter<? super TARGET, ? extends SOURCE> ident;

		private final Iterable<? extends TARGET> targets;

		private final HashMap<SOURCE, TARGET> toTargetMap;

		private final HashMap<TARGET, SOURCE> toSourceMap;

	}

	/** Diese Klasse implementiert einen verketteten {@link Translator2}.
	 *
	 * @param <SOURCE> Typ der Quellobjekte dieses sowie des ersten {@link Translator}.
	 * @param <CENTER> Typ der Zielobjekte des ersten sowie der Quellobjekte des zweiten {@link Translator}.
	 * @param <TARGET> Typ der Zielobjekte dieses sowie des zweiten {@link Translator}. */
	public static class ConcatTranslator<SOURCE, CENTER, TARGET> extends AbstractTranslator<SOURCE, TARGET> {

		public ConcatTranslator(Translator<SOURCE, CENTER> that1, Translator<CENTER, TARGET> that2) throws NullPointerException {
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
		public TARGET toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that2.toTarget(this.that1.toTarget(object));
		}

		@Override
		public SOURCE toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that1.toSource(this.that2.toSource(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that1, this.that2);
		}

		private final Translator<SOURCE, CENTER> that1;

		private final Translator<CENTER, TARGET> that2;

	}

	/** Diese Klasse implementiert einen {@link Translator2}, der die Übersetzung eines gegebenen {@link Translator} umkehrt.
	 *
	 * @param <SOURCE> Typ der Quellobjekte dieses sowie der Zielobjekte des gegebenen {@link Translator}.
	 * @param <TARGET> Typ der Zielobjekte dieses sowie der Quellobjekte des gegebenen {@link Translator}. */
	public static class ReverseTranslator<SOURCE, TARGET> extends AbstractTranslator<SOURCE, TARGET> {

		public ReverseTranslator(Translator<TARGET, SOURCE> that) throws NullPointerException {
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
		public TARGET toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that.toSource(object);
		}

		@Override
		public SOURCE toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.that.toTarget(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

		private final Translator<TARGET, SOURCE> that;

	}

	/** Diese Klasse implementiert einen zusammengesetzten {@link Translator2}, der Quell- und Zielobjekte an ihren {@link Class Klassen} erkennt und zur
	 * Umwandlung dieser ineinander entsprechende gegebene {@link Getter} verwendet.
	 *
	 * @param <SOURCE> Typ der Quellobjekte.
	 * @param <TARGET> Typ der Zielobjekte. */
	public static class CompositeTranslator<SOURCE, TARGET> extends AbstractTranslator<SOURCE, TARGET> {

		public CompositeTranslator(Class<SOURCE> sourceClass, Class<TARGET> targetClass, Getter<? super SOURCE, ? extends TARGET> sourceTrans,
			Getter<? super TARGET, ? extends SOURCE> targetTrans) throws NullPointerException {
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
		public TARGET toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return this.sourceTrans.get(this.sourceClass.cast(object));
		}

		@Override
		public SOURCE toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return this.targetTrans.get(this.targetClass.cast(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sourceClass, this.targetClass, this.sourceTrans, this.targetTrans);
		}

		private final Class<SOURCE> sourceClass;

		private final Class<TARGET> targetClass;

		private final Getter<? super SOURCE, ? extends TARGET> sourceTrans;

		private final Getter<? super TARGET, ? extends SOURCE> targetTrans;

	}

	/** Diese Klasse implementiert einen {@link Translator2}, der die Übersetzung eines gegebenen {@link Translator} {@code null}-tollerant macht.
	 *
	 * @param <SOURCE> Typ der Quellobjekte.
	 * @param <TARGET> Typ der Zielobjekte. */
	public static class OptionalizedTranslator<SOURCE, TARGET> extends AbstractTranslator<SOURCE, TARGET> {

		public OptionalizedTranslator(Translator<SOURCE, TARGET> that) throws NullPointerException {
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
		public TARGET toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			return object != null ? this.that.toTarget(object) : null;
		}

		@Override
		public SOURCE toSource(Object object) throws ClassCastException, IllegalArgumentException {
			return object != null ? this.that.toSource(object) : null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

		private final Translator<SOURCE, TARGET> that;

	}

	/** Diese Klasse implementiert einen synchronisierten {@link Translator2}, der einen gegebenen {@link Translator} über {@code synchronized(this.mutex)}
	 * synchronisiert. Wenn dieses Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <SOURCE> Typ der Quellobjekte.
	 * @param <TARGET> Typ der Zielobjekte. */
	public static class SynchronizedTranslator<SOURCE, TARGET> extends AbstractTranslator<SOURCE, TARGET> {

		public SynchronizedTranslator(Translator<SOURCE, TARGET> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean isTarget(Object object) {
			synchronized (this.mutex) {
				return this.that.isTarget(object);
			}
		}

		@Override
		public boolean isSource(Object object) {
			synchronized (this.mutex) {
				return this.that.isSource(object);
			}
		}

		@Override
		public TARGET toTarget(Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.that.toTarget(object);
			}
		}

		@Override
		public SOURCE toSource(Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.that.toSource(object);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

		private final Translator<SOURCE, TARGET> that;

		private final Object mutex;

	}

}
