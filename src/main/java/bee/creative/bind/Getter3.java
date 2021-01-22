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

	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget);

	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget, GValue2 emptyTarget,
		GValue2 mixedTarget, Getter<? super GItem, GValue> getter);

	public Getter3<Iterable<? extends GItem>, GValue> toAggregated(GValue emptyTarget, GValue mixedTarget);

	@Override
	public Getter3<GItem, GValue> toSynchronized();

	@Override
	public Getter3<GItem, GValue> toSynchronized(Object mutex);

}
