package bee.creative.util;

import java.util.Comparator;
import bee.creative.lang.Objects.BaseObject;

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
		return Setters.concat(this, target);
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
	public Field2<Iterable<? extends GItem>, GValue> toAggregated() {
		return Fields.toAggregated(this);
	}

	@Override
	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet) {
		return Fields.toAggregated(this, transGet, transSet);
	}

	@Override
	public <GItem2 extends Iterable<? extends GItem>, GValue2> Field2<GItem2, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet, final Getter<? super GItem2, ? extends GValue2> empty,
		final Getter<? super GItem2, ? extends GValue2> mixed) {
		return Fields.toAggregated(this, transGet, transSet, empty, mixed);
	}

	@Override
	public Consumer3<GValue> toConsumer() {
		return Consumers.from(this);
	}

	@Override
	public Consumer3<GValue> toConsumer(final GItem item) {
		return Consumers.from(this, item);
	}

	@Override
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
		return Producers.from(this);
	}

	@Override
	public Producer3<GValue> toProducer(final GItem item) {
		return Producers.from(this, item);
	}

	@Override
	public Field2<GItem, GValue> toField(final Setter<? super GItem, ? super GValue> set) {
		return Fields.from(this, set);
	}

	@Override
	public Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> get) {
		return Fields.from(get, this);
	}

}