package bee.creative.bind;

import java.util.Comparator;
import bee.creative.util.Comparables;
import bee.creative.util.Comparators;
import bee.creative.util.Filter;
import bee.creative.util.Filters;

/** Diese Schnittstelle ergänzt einen {@link Getter} insb. um eine Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter2<GItem, GValue> extends Getter<GItem, GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Comparables#concat(Getter, Comparable) Comparables.from(this, target)}. */
	public Comparable<GItem> concat(final Comparable<? super GValue> target);

	/** Diese Methode ist eine Abkürtung für {@link Comparators#concat(Getter, Comparator) Comparators.from(this, target)}. */
	public Comparator<GItem> concat(final Comparator<? super GValue> target);

	/** Diese Methode ist eine Abkürtung für {@link Fields#concat(Getter, Field) Fields.concat(this, target)}. */
	public <GValue2> Field2<GItem, GValue2> concat(final Field<? super GValue, GValue2> target);

	/** Diese Methode ist eine Abkürtung für {@link Filters#concat(Getter, Filter) Filters.from(this, target)}. */
	public Filter<GItem> concat(final Filter<? super GValue> target);

	/** Diese Methode ist eine Abkürtung für {@link Getters#concat(Getter, Getter) Getters.concat(this, target)}. */
	public <GValue2> Getter3<GItem, GValue2> concat(final Getter<? super GValue, ? extends GValue2> target);

	/** Diese Methode ist eine Abkürtung für {@link Setters#concat(Getter, Setter) Setters.concat(this, target)}. */
	public <GValue2> Setter3<GItem, GValue2> concat(final Setter<? super GValue, ? super GValue2> target);

	/** Diese Methode ist eine Abkürtung für {@link Getters#toAggregated(Getter) Getters.toAggregated(this)}. */
	public Getter2<Iterable<? extends GItem>, GValue> toAggregated();

	/** Diese Methode ist eine Abkürtung für {@link Getters#toDefault(Getter) Getters.toDefault(this)}. */
	public Getter2<GItem, GValue> toDefault();

	/** Diese Methode ist eine Abkürtung für {@link Getters#toDefault(Getter, Object) Getters.toDefault(this, value)}. */
	public Getter2<GItem, GValue> toDefault(final GValue value);

	/** Diese Methode ist eine Abkürtung für {@link Fields#from(Getter, Setter) Fields.from(this, target)}. */
	public Field2<GItem, GValue> toField(Setter<? super GItem, ? super GValue> target);

	/** Diese Methode ist eine Abkürtung für {@link Producers#from(Getter) Producers.from(this)}. */
	public Producer3<GValue> toProducer();

	/** Diese Methode ist eine Abkürtung für {@link Producers#from(Getter, Object) Producers.from(this, item)}. */
	public Producer3<GValue> toProducer(final GItem item);

	/** Diese Methode ist eine Abkürtung für {@link Getters#toSynchronized(Getter) Getters.toSynchronized(this)}. */
	public Getter2<GItem, GValue> toSynchronized();

	/** Diese Methode ist eine Abkürtung für {@link Getters#toSynchronized(Getter, Object) Getters.toSynchronized(this, mutex)}. */
	public Getter2<GItem, GValue> toSynchronized(Object mutex);

}