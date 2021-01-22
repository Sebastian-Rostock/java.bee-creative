package bee.creative.bind;

import java.util.Comparator;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.util.Comparables;
import bee.creative.util.Comparators;
import bee.creative.util.Filter;
import bee.creative.util.Filters;

/** Diese Klasse implementiert ein abstraktes {@link Field2} als {@link BaseObject}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractField<GItem, GValue> extends BaseObject implements Field2<GItem, GValue> {

	@Override
	public GValue get(final GItem item) {
		return null;
	}

	@Override
	public void set(final GItem item, final GValue value) {
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

	@Override
	public Field2<Iterable<? extends GItem>, GValue> toAggregated() {
		return null;
	}

	@Override
	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> toValue,
		final Getter<? super GValue2, ? extends GValue> toValue2) {
		return null;
	}

	@Override
	public <GValue2> Getter2<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> toTarget, final GValue2 emptyTarget,
		final GValue2 mixedTarget) {
		return null;
	}

	@Override
	public Field2<Iterable<? extends GItem>, GValue> toAggregated(final GValue emptyTarget, final GValue mixedTarget) {
		return null;
	}

	@Override
	public Consumer3<GValue> toConsumer() {
		return null;
	}

	@Override
	public Consumer3<GValue> toConsumer(final GItem item) {
		return null;
	}

	public Field2<GItem, GValue> toSetup(final Getter<? super GItem, ? extends GValue> setup) {
		return Fields.toSetup(this, setup);
	}

	@Override
	public Field2<GItem, GValue> toDefault() {
		return Fields.toDefault(this);
	}

	@Override
	public Field2<GItem, GValue> toDefault(final GValue value) {
		return Fields.toDefault(this, value);
	}

	@Override
	public Property2<GValue> toProperty() {
		return Properties.from(this);
	}

	@Override
	public Property2<GValue> toProperty(final GItem item) {
		return Properties.from(this, item);
	}

	@Override
	public <GValue2> Field2<GItem, GValue2> toTranslated(final Translator<GValue, GValue2> trans) {
		return Fields.toTranslated(this, trans);
	}

	@Override
	public <GValue2> Field2<GItem, GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet) {
		return Fields.toTranslated(this, transGet, transSet);
	}

	@Override
	public Field2<GItem, GValue> toSynchronized() {
		return Fields.toSynchronized(this);
	}

	@Override
	public Field2<GItem, GValue> toSynchronized(final Object mutex) {
		return Fields.toSynchronized(this, mutex);
	}

	@Override
	public Producer3<GValue> toProducer() {
		return null;
	}

	@Override
	public Producer3<GValue> toProducer(final GItem item) {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField(final Setter<? super GItem, ? super GValue> setter) {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> getter) {
		return null;
	}

}