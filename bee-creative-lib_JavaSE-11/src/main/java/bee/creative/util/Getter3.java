package bee.creative.util;

import static bee.creative.util.Comparables.translatedComparable;
import static bee.creative.util.Comparators.translatedComparator;
import static bee.creative.util.Fields.concatField;
import static bee.creative.util.Fields.emptyField;
import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Filters.translatedFilter;
import static bee.creative.util.Getters.aggregatedGetter;
import static bee.creative.util.Getters.bufferedGetter;
import static bee.creative.util.Getters.concatGetter;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Getters.optionalizedGetter;
import static bee.creative.util.Getters.synchronizedGetter;
import static bee.creative.util.Getters.RefMode.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;
import static bee.creative.util.Iterables.translatedIterable;
import static bee.creative.util.Iterators.translatedIterator;
import static bee.creative.util.Producers.producerFromValue;
import static bee.creative.util.Setters.concatSetter;
import java.util.Comparator;
import java.util.Iterator;
import bee.creative.util.Getters.RefMode;

/** Diese Schnittstelle ergänzt einen {@link Getter3} insb. um eine erweiterte Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Getter3<T, V> extends Getter<T, V> {

	/** Diese Methode ist eine Abkürzung für {@link Fields#concatField(Getter, Field) concatField(this, that)}. */
	default <V2> Field3<T, V2> concat(Field<? super V, V2> that) {
		return concatField(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#concatGetter(Getter, Getter) concatGetter(this, that)}. */
	default <V2> Getter3<T, V2> concat(Getter<? super V, ? extends V2> that) {
		return concatGetter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#concatSetter(Getter, Setter) concatSetter(this, that)}. */
	default <V2> Setter3<T, V2> concat(Setter<? super V, ? super V2> that) {
		return concatSetter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translatedFilter(Filter, Getter) translatedFilter(that, this)}. */
	default Filter3<T> translate(Filter<? super V> that) {
		return translatedFilter(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translatedIterable(Iterable, Getter) translatedIterable(that, this)}. */
	default Iterable2<V> translate(Iterable<? extends T> that) {
		return translatedIterable(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translatedIterator(Iterator, Getter) translatedIterator(that, this)}. */
	default Iterator2<V> translate(Iterator<? extends T> that) {
		return translatedIterator(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translatedComparable(Comparable, Getter) translatedComparable(that, this)}. */
	default Comparable3<T> translate(Comparable<? super V> that) {
		return translatedComparable(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translatedComparator(Comparator, Getter) translatedComparator(that, this)}. */
	default Comparator3<T> translate(Comparator<? super V> that) {
		return translatedComparator(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(RefMode, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Getter3<T, V> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, RefMode, Hasher) bufferedGetter(this, mode, hasher)}. */
	default Getter3<T, V> buffer(RefMode mode, Hasher hasher) {
		return bufferedGetter(this, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter) this.aggregate(neutralGetter())}. */
	default Getter3<Iterable<? extends T>, V> aggregate() {
		return this.aggregate(neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter, Getter, Getter) this.aggregate(trans, emptyGetter(), emptyGetter())}. */
	default <V2> Getter3<Iterable<? extends T>, V2> aggregate(Getter<? super V, ? extends V2> trans) {
		return this.aggregate(trans, emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Getter, Getter, Getter, Getter) aggregatedGetter(this, trans, empty, mixed)}. */
	default <T2 extends Iterable<? extends T>, V2> Getter3<T2, V2> aggregate(Getter<? super V, ? extends V2> trans, Getter<? super T2, ? extends V2> empty,
		Getter<? super T2, ? extends V2> mixed) {
		return aggregatedGetter(this, trans, empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalize(Object) this.optionalize(null)}. */
	default Getter3<T, V> optionalize() {
		return this.optionalize(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalizedGetter(Getter, Object) optionalizedGetter(this, value)}. */
	default Getter3<T, V> optionalize(V value) {
		return optionalizedGetter(this, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Getter3<T, V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#synchronizedGetter(Getter, Object) synchronizedGetter(this, mutex)}. */
	default Getter3<T, V> synchronize(Object mutex) {
		return synchronizedGetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asProducer(Object) this.asProducer(null)}. */
	default Producer3<V> asProducer() {
		return this.asProducer(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#producerFromValue(Object) producerFromValue(item).translate(this)}. */
	default Producer3<V> asProducer(T item) {
		return producerFromValue(item).translate(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) fieldFrom(this, emptyField())}. */
	default Field3<T, V> asField() {
		return fieldFrom(this, emptyField());
	}

}
