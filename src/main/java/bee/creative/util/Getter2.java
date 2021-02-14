package bee.creative.util;

import java.util.Comparator;

/** Diese Schnittstelle ergänzt einen {@link Getter} insb. um eine Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter2<GItem, GValue> extends Getter<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translate(Comparable, Getter) Comparables.from(this, target)}. */
	public Comparable2<GItem> concat(final Comparable<? super GValue> target);

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translate(Comparator, Getter) Comparators.from(this, target)}. */
	public Comparator2<GItem> concat(final Comparator<? super GValue> target);

	/** Diese Methode ist eine Abkürzung für {@link Fields#translate(Getter, Field) Fields.concat(this, target)}. */
	public <GValue2> Field2<GItem, GValue2> concat(final Field<? super GValue, GValue2> target);

	/** Diese Methode ist eine Abkürzung für {@link Filters#translate(Getter, Filter) Filters.from(this, target)}. */
	public Filter2<GItem> concat(final Filter<? super GValue> target);

	/** Diese Methode ist eine Abkürzung für {@link Getters#concat(Getter, Getter) Getters.concat(this, target)}. */
	public <GValue2> Getter3<GItem, GValue2> concat(final Getter<? super GValue, ? extends GValue2> target);

	/** Diese Methode ist eine Abkürzung für {@link Setters#translate(Getter, Setter) Setters.translate(this, target)}. */
	public <GValue2> Setter3<GItem, GValue2> concat(final Setter<? super GValue, ? super GValue2> target);

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregate(Getter) Getters.aggregate(this)}. */
	public Getter2<Iterable<? extends GItem>, GValue> aggregate();

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalize(Getter) Getters.optionalize(this)}. */
	public Getter2<GItem, GValue> optionalize();

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalize(Getter, Object) Getters.optionalize(this, value)}. */
	public Getter2<GItem, GValue> optionalize(final GValue value);

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Getter, Setter) Fields.from(this, target)}. */
	public Field2<GItem, GValue> toField(Setter<? super GItem, ? super GValue> target);

	/** Diese Methode ist eine Abkürzung für {@link Producers#from(Getter) Producers.from(this)}. */
	public Producer3<GValue> toProducer();

	/** Diese Methode ist eine Abkürzung für {@link Producers#from(Getter, Object) Producers.from(this, item)}. */
	public Producer3<GValue> toProducer(final GItem item);

	/** Diese Methode ist eine Abkürzung für {@link Getters#synchronize(Getter) Getters.synchronize(this)}. */
	public Getter2<GItem, GValue> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Getters#synchronize(Getter, Object) Getters.synchronize(this, mutex)}. */
	public Getter2<GItem, GValue> synchronize(Object mutex);

}
