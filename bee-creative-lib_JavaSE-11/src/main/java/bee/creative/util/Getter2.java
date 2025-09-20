package bee.creative.util;

import static bee.creative.util.Comparators.translatedComparator;
import static bee.creative.util.Fields.concatField;
import static bee.creative.util.Filters.translatedFilter;
import static bee.creative.util.Getters.aggregatedGetter;
import static bee.creative.util.Getters.bufferedGetter;
import static bee.creative.util.Getters.concatGetter;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Getters.optionalizedGetter;
import static bee.creative.util.Getters.synchronizedGetter;
import static bee.creative.util.Getters.BufferedGetter.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;
import static bee.creative.util.Iterables.translatedIterable;
import static bee.creative.util.Iterators.translatedIterator;
import static bee.creative.util.Producers.producerFromValue;
import static bee.creative.util.Setters.concatSetter;
import java.util.Comparator;
import java.util.Iterator;

/** Diese Schnittstelle ergänzt einen {@link Getter} insb. um eine Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Getter2<ITEM, VALUE> extends Getter<ITEM, VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Fields#concatField(Getter, Field) concatField(this, that)}. */
	default <VALUE2> Field2<ITEM, VALUE2> concat(Field<? super VALUE, VALUE2> that) {
		return concatField(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#concatGetter(Getter, Getter) concatGetter(this, that)}. */
	default <VALUE2> Getter3<ITEM, VALUE2> concat(Getter<? super VALUE, ? extends VALUE2> that) {
		return concatGetter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#concatSetter(Getter, Setter) concatSetter(this, that)}. */
	default <VALUE2> Setter3<ITEM, VALUE2> concat(Setter<? super VALUE, ? super VALUE2> that) {
		return concatSetter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translatedFilter(Filter, Getter) translatedFilter(that, this)}. */
	default Filter2<ITEM> translate(Filter<? super VALUE> that) {
		return translatedFilter(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translate(Comparable, Getter) Comparables.translate(that, this)}. */
	default Comparable2<ITEM> translate(Comparable<? super VALUE> that) {
		return Comparables.translate(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translatedComparator(Comparator, Getter) translatedComparator(that, this)}. */
	default Comparator2<ITEM> translate(Comparator<? super VALUE> that) {
		return translatedComparator(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translatedIterable(Iterable, Getter) translateIterable(that, this)}. */
	default Iterable2<VALUE> translate(Iterable<? extends ITEM> that) {
		return translatedIterable(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translatedIterator(Iterator, Getter) translateIterator(that, this)}. */
	default Iterator2<VALUE> translate(Iterator<? extends ITEM> that) {
		return translatedIterator(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Getter3<ITEM, VALUE> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, int, Hasher) bufferedGetter(this, mode, hasher)}. */
	default Getter3<ITEM, VALUE> buffer(int mode, Hasher hasher) {
		return bufferedGetter(this, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Getter, Getter, Getter, Getter) aggregatedGetter(this, neutralGetter(),
	 * emptyGetter(), emptyGetter())}. */
	default Getter2<Iterable<? extends ITEM>, VALUE> aggregate() {
		return aggregatedGetter(this, neutralGetter(), emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalize(Object) this.optionalize(null)}. */
	default Getter2<ITEM, VALUE> optionalize() {
		return this.optionalize(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalizedGetter(Getter, Object) optionalizedGetter(this, value)}. */
	default Getter2<ITEM, VALUE> optionalize(VALUE value) {
		return optionalizedGetter(this, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Getter2<ITEM, VALUE> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#synchronizedGetter(Getter, Object) synchronizedGetter(this, mutex)}. */
	default Getter2<ITEM, VALUE> synchronize(Object mutex) {
		return synchronizedGetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toProducer(Object) toProducer(null)}. */
	default Producer3<VALUE> toProducer() {
		return this.toProducer(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#producerFromValue(Object) producerFromValue(item).translate(this)}. */
	default Producer3<VALUE> toProducer(ITEM item) {
		return producerFromValue(item).translate(this);
	}

}
