package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;

/** Diese Schnittstelle ergänzt einen {@link Getter} insb. um eine Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter2<GItem, GValue> extends Getter<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translate(Comparable, Getter) Comparables.translate(target, this)}. */
	default Comparable2<GItem> concat(Comparable<? super GValue> target) {
		return Comparables.translate(target, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translate(Comparator, Getter) Comparators.translate(target, this)}. */
	default Comparator2<GItem> concat(Comparator<? super GValue> target) {
		return Comparators.translate(target, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#concalField(Getter, Field) Fields.translate(this, target)}. */
	default <GValue2> Field2<GItem, GValue2> concat(Field<? super GValue, GValue2> target) {
		return Fields.concalField(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translateFilter(Filter, Getter) Filters.translate(target, this)}. */
	default Filter2<GItem> concat(Filter<? super GValue> target) {
		return Filters.translateFilter(target, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#concat(Getter, Getter) Getters.concat(this, target)}. */
	default <GValue2> Getter3<GItem, GValue2> concat(Getter<? super GValue, ? extends GValue2> target) {
		return Getters.concat(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#concatSetter(Getter, Setter) Setters.translate(this, target)}. */
	default <GValue2> Setter3<GItem, GValue2> concat(Setter<? super GValue, ? super GValue2> target) {
		return Setters.concatSetter(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translate(Iterable, Getter) Iterables.translate(target, this)}. */
	default Iterable2<GValue> concat(Iterable<? extends GItem> target) {
		return Iterables.translate(target, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translate(Iterator, Getter) Iterators.translate(target, this)}. */
	default Iterator2<GValue> concat(Iterator<? extends GItem> target) {
		return Iterators.translate(target, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#buffer(Getter) Getters.buffer(this)}. */
	default Getter3<GItem, GValue> buffer() {
		return Getters.buffer(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#buffer(Getter, int, Hasher) Getters.buffer(this, mode, hasher)}. */
	default Getter3<GItem, GValue> buffer(int mode, Hasher hasher) {
		return Getters.buffer(this, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregate(Getter) Getters.aggregate(this)}. */
	default Getter2<Iterable<? extends GItem>, GValue> aggregate() {
		return Getters.aggregate(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalize(Getter) Getters.optionalize(this)}. */
	default Getter2<GItem, GValue> optionalize() {
		return Getters.optionalize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalize(Getter, Object) Getters.optionalize(this, value)}. */
	default Getter2<GItem, GValue> optionalize(GValue value) {
		return Getters.optionalize(this, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#synchronize(Getter) Getters.synchronize(this)}. */
	default Getter2<GItem, GValue> synchronize() {
		return Getters.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#synchronize(Getter, Object) Getters.synchronize(this, mutex)}. */
	default Getter2<GItem, GValue> synchronize(Object mutex) {
		return Getters.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) Fields.from(this, set)}. */
	default Field2<GItem, GValue> toField(Setter<? super GItem, ? super GValue> set) {
		return Fields.fieldFrom(this, set);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#from(Getter) Producers.from(this)}. */
	default Producer3<GValue> toProducer() {
		return Producers.from(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#from(Getter, Object) Producers.from(this, item)}. */
	default Producer3<GValue> toProducer(GItem item) {
		return Producers.from(this, item);
	}

}
