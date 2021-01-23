package bee.creative.bind;

/** Diese Schnittstelle ergänzt einen {@link Getter2} insb. um eine erweiterte Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter3<GItem, GValue> extends Getter2<GItem, GValue> {

	public Field2<GItem, GValue> toField();

	@Override
	public Getter3<GItem, GValue> toDefault();

	@Override
	public Getter3<GItem, GValue> toDefault(GValue value);

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> toAggregated();

	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> trans);

	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> trans, GValue2 empty, GValue2 mixed);

	public Getter3<Iterable<? extends GItem>, GValue> toAggregated(GValue empty, GValue mixed);

	@Override
	public Getter3<GItem, GValue> toSynchronized();

	@Override
	public Getter3<GItem, GValue> toSynchronized(Object mutex);

}
