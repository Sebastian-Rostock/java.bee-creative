package bee.creative.bind;

import java.util.Comparator;
import bee.creative.util.Filter;


/** Diese Schnittstelle ergänzt einen {@link Getter} insb. um eine Anbindung an Methoden von {@link Getters}.
*
* @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
* @param <GItem> Typ des Datensatzes.
* @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter2<GItem, GValue> extends Getter<GItem, GValue> {

	Getter2<GItem, GValue> toDefault();

	Getter2<GItem, GValue> toDefault(final GValue value);

	Getter2<Iterable<? extends GItem>, GValue> toAggregated();

	Getter2<GItem, GValue> toSynchronized();

	Getter2<GItem, GValue> toSynchronized(Object mutex);

	Field2<GItem, GValue> toField(Setter<? super GItem, ? super GValue> setter);

	Producer3<GValue> toProducer();

	Producer3<GValue> toProducer(final GItem item);

	Filter<GItem> concat(final Filter<? super GValue> target);

	<GValue2> Field2<GItem, GValue2> concat(final Field<? super GValue, GValue2> target);

	<GValue2> Getter3<GItem, GValue2> concat(final Getter<? super GValue, ? extends GValue2> target);

	<GValue2> Setter3<GItem, GValue2> concat(final Setter<? super GValue, ? super GValue2> target);

	Comparable<GItem> concat(final Comparable<? super GValue> target);

	Comparator<GItem> concat(final Comparator<? super GValue> target);

}
