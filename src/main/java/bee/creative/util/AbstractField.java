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
		return Filters.translate(target, this);
	}

	@Override
	public <GValue2> Field2<GItem, GValue2> concat(final Field<? super GValue, GValue2> target) {
		return Fields.translate(this, target);
	}

	@Override
	public <GValue2> Getter3<GItem, GValue2> concat(final Getter<? super GValue, ? extends GValue2> target) {
		return Getters.concat(this, target);
	}

	@Override
	public <GValue2> Setter3<GItem, GValue2> concat(final Setter<? super GValue, ? super GValue2> target) {
		return Setters.translate(this, target);
	}

	@Override
	public Comparable2<GItem> concat(final Comparable<? super GValue> target) {
		return Comparables.translate(target, this);
	}

	@Override
	public Comparator2<GItem> concat(final Comparator<? super GValue> target) {
		return Comparators.translate(target, this);
	}

	@Override
	public Getter3<GItem, GValue> buffer() {
		return Getters.buffer(this);
	}

	@Override
	public Getter3<GItem, GValue> buffer(final int mode, final Hasher hasher) {
		return Getters.buffer(this, mode, hasher);
	}

	@Override
	public Field2<Iterable<? extends GItem>, GValue> aggregate() {
		return Fields.aggregate(this);
	}

	@Override
	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> aggregate(final Getter<? super GValue, ? extends GValue2> getTrans,
		final Getter<? super GValue2, ? extends GValue> setTrans) {
		return Fields.aggregate(this, getTrans, setTrans);
	}

	@Override
	public <GItem2 extends Iterable<? extends GItem>, GValue2> Field2<GItem2, GValue2> aggregate(final Getter<? super GValue, ? extends GValue2> getTrans,
		final Getter<? super GValue2, ? extends GValue> setTrans, final Getter<? super GItem2, ? extends GValue2> empty,
		final Getter<? super GItem2, ? extends GValue2> mixed) {
		return Fields.aggregate(this, getTrans, setTrans, empty, mixed);
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
	public Field2<GItem, GValue> setup(final Getter<? super GItem, ? extends GValue> setup) {
		return Fields.setup(this, setup);
	}

	@Override
	public Field2<GItem, GValue> optionalize() {
		return Fields.optionalize(this);
	}

	@Override
	public Field2<GItem, GValue> optionalize(final GValue value) {
		return Fields.optionalize(this, value);
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
	public <GValue2> Field2<GItem, GValue2> translate(final Translator<GValue, GValue2> trans) {
		return Fields.translate(this, trans);
	}

	@Override
	public <GValue2> Field2<GItem, GValue2> translate(final Getter<? super GValue, ? extends GValue2> getTrans,
		final Getter<? super GValue2, ? extends GValue> setTrans) {
		return Fields.translate(this, getTrans, setTrans);
	}

	@Override
	public Field2<GItem, GValue> synchronize() {
		return Fields.synchronize(this);
	}

	@Override
	public Field2<GItem, GValue> synchronize(final Object mutex) {
		return Fields.synchronize(this, mutex);
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