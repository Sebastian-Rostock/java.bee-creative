package bee.creative.util;

import java.util.Comparator;
import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Getter3} als {@link BaseObject}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractGetter<GItem, GValue> extends BaseObject implements Getter3<GItem, GValue> {

	@Override
	public GValue get(final GItem item) {
		return null;
	}

	@Override
	public Filter2<GItem> concat(final Filter<? super GValue> target) {
		return Filters.concat(this, target);
	}

	@Override
	public <GValue2> Field2<GItem, GValue2> concat(final Field<? super GValue, GValue2> target) {
		return Fields.concat(this, target);
	}

	@Override
	public <GValue2> Getter3<GItem, GValue2> concat(final Getter<? super GValue, ? extends GValue2> target) {
		return Getters.concat(this, target);
	}

	@Override
	public <GValue2> Setter3<GItem, GValue2> concat(final Setter<? super GValue, ? super GValue2> target) {
		return Setters.toTranslated(this, target);
	}

	@Override
	public Comparable2<GItem> concat(final Comparable<? super GValue> target) {
		return Comparables.toTranslated(target, this);
	}

	@Override
	public Comparator2<GItem> concat(final Comparator<? super GValue> target) {
		return Comparators.toTranslated(target, this);
	}

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> toAggregated() {
		return Getters.toAggregated(this);
	}

	@Override
	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> trans) {
		return Getters.toAggregated(this, trans);
	}

	@Override
	public <GItem2 extends Iterable<? extends GItem>, GValue2> Getter3<GItem2, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> trans,
		final Getter<? super GItem2, ? extends GValue2> empty, final Getter<? super GItem2, ? extends GValue2> mixed) {
		return Getters.toAggregated(this, trans, empty, mixed);
	}

	@Override
	public Getter3<GItem, GValue> toDefault() {
		return Getters.toDefault(this);
	}

	@Override
	public Getter3<GItem, GValue> toDefault(final GValue value) {
		return Getters.toDefault(this, value);
	}

	@Override
	public Field2<GItem, GValue> toField() {
		return Fields.from(this);
	}

	@Override
	public Field2<GItem, GValue> toField(final Setter<? super GItem, ? super GValue> set) {
		return Fields.from(this, set);
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

}