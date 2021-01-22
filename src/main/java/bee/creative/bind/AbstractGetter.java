package bee.creative.bind;

import java.util.Comparator;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.util.Comparables;
import bee.creative.util.Comparators;
import bee.creative.util.Filter;
import bee.creative.util.Filters;

/** Diese Klasse implementiert einen abstrakten {@link Getter3} als {@link BaseObject}. 
@author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]  
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft.
 */
public abstract class AbstractGetter<GItem, GValue> extends BaseObject implements Getter3<GItem, GValue> {

	@Override
	public GValue get(final GItem item) {
		return null;
	}

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> toAggregated() {
		return null;
	}

	@Override
	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> toTarget) {
		return null;
	}

	@Override
	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> toTarget, final GValue2 emptyTarget,
		final GValue2 mixedTarget, final Getter<? super GItem, GValue> getter) {
		return null;
	}

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> toAggregated(final GValue emptyTarget, final GValue mixedTarget) {
		return null;
	}

	@Override
	public Getter3<GItem, GValue> toDefault() {
		return null;
	}

	@Override
	public Getter3<GItem, GValue> toDefault(final GValue value) {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField() {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField(final Setter<? super GItem, ? super GValue> setter) {
		return null;
	}

	@Override
	public Producer3<GValue> toProducer() {
		return Producers.from(this);
	}

	@Override
	public Producer3<GValue> toProducer(final GItem item) {
		return Producers.from(this, item);
	}

	@Override
	public Getter3<GItem, GValue> toSynchronized() {
		return Getters.toSynchronized(this);
	}

	@Override
	public Getter3<GItem, GValue> toSynchronized(final Object mutex) {
		return Getters.toSynchronized(this, mutex);
	}

	@Override
	public Filter<GItem> concat(Filter<? super GValue> target) {
		return Filters.concat(this, target);
	}

	@Override
	public <GValue2> Field2<GItem, GValue2> concat(Field<? super GValue, GValue2> target) {
		return Fields.concat(this, target);
	}

	@Override
	public <GValue2> Getter3<GItem, GValue2> concat(Getter<? super GValue, ? extends GValue2> target) {
		return Getters.concat(this, target);
	}

	@Override
	public <GValue2> Setter3<GItem, GValue2> concat(Setter<? super GValue, ? super GValue2> target) {
		return Setters.concat(this, target);
	}

	@Override
	public Comparable<GItem> concat(Comparable<? super GValue> target) {
		return Comparables.concat(this, target);
	}

	@Override
	public Comparator<GItem> concat(Comparator<? super GValue> target) {
		return Comparators.concat(this, target);
	}

}