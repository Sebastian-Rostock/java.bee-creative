package bee.creative.bind;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Setter3} als {@link BaseObject}.
 * 
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractSetter<GItem, GValue> extends BaseObject implements Setter3<GItem, GValue> {

	@Override
	public void set(final GItem item, final GValue value) {
	}

	@Override
	public Setter3<Iterable<? extends GItem>, GValue> toAggregated() {
		return Setters.toAggregated(this);
	}

	@Override
	public <GValue2> Setter3<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue2, ? extends GValue> trans) {
		return Setters.toAggregated(this, trans);
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
	public Setter3<GItem, GValue> toDefault() {
		return Setters.toDefault(this);
	}

	@Override
	public Field2<GItem, GValue> toField() {
		return Fields.from(this);
	}

	@Override
	public Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> get) {
		return Fields.from(get, this);
	}

	@Override
	public Setter3<GItem, GValue> toSynchronized() {
		return Setters.toSynchronized(this);
	}

	@Override
	public Setter3<GItem, GValue> toSynchronized(final Object mutex) {
		return Setters.toSynchronized(this, mutex);
	}

	@Override
	public <GValue2> Setter3<GItem, GValue2> toTranslated(final Getter<? super GValue2, ? extends GValue> trans) {
		return Setters.toTranslated(this, trans);
	}

}