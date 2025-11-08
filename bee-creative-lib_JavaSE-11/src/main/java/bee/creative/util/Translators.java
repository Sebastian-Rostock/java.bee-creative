package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Filters.rejectFilter;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Iterables.iterableFromArray;
import java.util.Map;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	/** Diese Methode liefert den gegebenen {@link Translator} als {@link Translator3}. */
	public static <S, T> Translator3<S, T> translatorFrom(Translator<S, T> that) throws NullPointerException {
		notNull(that);
		if (that instanceof Translator3) return (Translator3<S, T>)that;
		return translatorFrom(object -> that.isTarget(object), object -> that.isSource(object), object -> that.toTarget(object), object -> that.toSource(object));
	}

	/** Diese Methode liefert einen {@link Translator3} mit den gegebenen Methoden. */
	public static <S, T> Translator3<S, T> translatorFrom(Filter<Object> isTarget, Filter<Object> isSource, Getter<Object, ? extends T> toTarget,
		Getter<Object, ? extends S> toSource) throws NullPointerException {
		notNull(isTarget);
		notNull(isSource);
		notNull(toTarget);
		notNull(toSource);
		return new Translator3<>() {

			@Override
			public boolean isTarget(Object object) {
				return isTarget.accepts(object);
			}

			@Override
			public boolean isSource(Object object) {
				return isSource.accepts(object);
			}

			@Override
			public T toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return toTarget.get(object);
			}

			@Override
			public S toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return toSource.get(object);
			}

		};
	}

	public static <S, T> Translator3<S, T> translatorFromMap(Map<T, ? extends S> sourceByTarget) throws NullPointerException {
		notNull(sourceByTarget);
		return translatorFromEnum(target -> sourceByTarget.get(target), sourceByTarget.keySet());
	}

	public static <T extends Enum<?>> Translator3<String, T> translatorFromEnum(Class<T> enumClass) throws NullPointerException {
		notNull(enumClass);
		return translatorFromEnum(target -> target.name(), enumClass.getEnumConstants());
	}

	@SafeVarargs
	public static <S, T> Translator3<S, T> translatorFromEnum(Getter<? super T, ? extends S> ident, T... targets) throws NullPointerException {
		return translatorFromEnum(ident, iterableFromArray(targets));
	}

	public static <S, T> Translator3<S, T> translatorFromEnum(Getter<? super T, ? extends S> ident, Iterable<? extends T> targets) throws NullPointerException {
		notNull(ident);
		var toTargetMap = new HashMap<S, T>(100);
		var toSourceMap = new HashMap<T, S>(100);
		targets.forEach(target -> {
			var source = ident.get(target);
			toTargetMap.put(source, target);
			toSourceMap.put(target, source);
		});
		toTargetMap.compact();
		toSourceMap.compact();
		return translatorFrom(object -> toSourceMap.containsKey(object), object -> toTargetMap.containsKey(object), object -> toTargetMap.get(object),
			object -> toSourceMap.get(object));
	}

	/** Diese Methode liefert einen {@link Translator3}, der Quell- und Zielobjekte an ihren {@link Class Klassen} erkennt und zur Umwandlung dieser ineinander
	 * die gegebenen {@link Getter} verwendet. */
	public static <S, T> Translator3<S, T> translatorFromClass(Class<S> sourceClass, Class<T> targetClass, Getter<? super S, ? extends T> sourceTrans,
		Getter<? super T, ? extends S> targetTrans) throws NullPointerException {
		return translatorFrom(object -> targetClass.isInstance(object), object -> sourceClass.isInstance(object),
			object -> sourceTrans.get(sourceClass.cast(object)), object -> targetTrans.get(targetClass.cast(object)));
	}

	/** Diese Methode liefert einen {@link Translator3}, der alle Quell- und Zielobjekte ablehnt. */
	@SuppressWarnings ("unchecked")
	public static <S, T> Translator3<S, T> emptyTranslator() {
		return (Translator3<S, T>)emptyTranslator;
	}

	/** Diese Methode liefert einen neutralen {@link Translator3} und ist eine Abkürzung für {@link Translators#neutralTranslator(Class)
	 * neutralTranslator(Object.class)}. */
	@SuppressWarnings ("unchecked")
	public static <V> Translator3<V, V> neutralTranslator() {
		return (Translator3<V, V>)neutralTranslator(Object.class);
	}

	/** Diese Methode liefert einen neutralen {@link Translator3} und ist eine Abkürzung für {@link Translators#translatorFromClass(Class, Class, Getter, Getter)
	 * translatorFrom(valueClass, valueClass, neutralGetter(), neutralGetter())}. */
	public static <V> Translator3<V, V> neutralTranslator(Class<V> valueClass) throws NullPointerException {
		return translatorFromClass(valueClass, valueClass, neutralGetter(), neutralGetter());
	}

	/** Diese Methode liefert einen verketteten {@link Translator3}. */
	public static <S, C, T> Translator3<S, T> concatTranslator(Translator<S, C> that1, Translator<C, T> that2) throws NullPointerException {
		notNull(that1);
		notNull(that2);
		return translatorFrom(object -> that2.isTarget(object), object -> that1.isSource(object), object -> that2.toTarget(that1.toTarget(object)),
			object -> that1.toSource(that2.toSource(object)));
	}

	/** Diese Methode liefert einen {@link Translator3}, der die Übersetzung des gegebenen {@link Translator} umkehrt. */
	public static <S, T> Translator3<S, T> reversedTranslator(Translator<T, S> that) throws NullPointerException {
		notNull(that);
		return translatorFrom(object -> that.isSource(object), object -> that.isTarget(object), object -> that.toSource(object), object -> that.toTarget(object));
	}

	/** Diese Methode liefert einen {@link Translator3}, der den gegebenen {@link Translator} {@code null}-tollerant macht. */
	public static <S, T> Translator3<S, T> optionalizedTranslator(Translator<S, T> that) throws NullPointerException {
		return translatorFrom(object -> (object == null) || that.isTarget(object), object -> (object == null) || that.isSource(object),
			object -> object != null ? that.toTarget(object) : null, object -> object != null ? that.toSource(object) : null);
	}

	/** Diese Methode liefert einen synchronisierten {@link Translator3}, der einen gegebenen {@link Translator} über {@code synchronized(mutex)} synchronisiert.
	 * Wenn dieses Synchronisationsobjekt {@code null} ist, wird der gelieferte {@link Translator} verwendet. */
	public static <S, T> Translator3<S, T> synchronizedTranslator(Translator<S, T> that, Object mutex) throws NullPointerException {
		notNull(that);
		return new Translator3<>() {

			@Override
			public boolean isTarget(Object object) {
				synchronized (notNull(mutex, this)) {
					return that.isTarget(object);
				}
			}

			@Override
			public boolean isSource(Object object) {
				synchronized (notNull(mutex, this)) {
					return that.isSource(object);
				}
			}

			@Override
			public T toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				synchronized (notNull(mutex, this)) {
					return that.toTarget(object);
				}
			}

			@Override
			public S toSource(Object object) throws ClassCastException, IllegalArgumentException {
				synchronized (notNull(mutex, this)) {
					return that.toSource(object);
				}
			}

		};
	}

	private static final Translator3<?, ?> emptyTranslator = translatorFrom(rejectFilter(), rejectFilter(), emptyGetter(), emptyGetter());

}
