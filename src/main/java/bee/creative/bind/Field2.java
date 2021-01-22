package bee.creative.bind;

/** Diese Schnittstelle ergänzt ein {@link Field} insb. um eine Anbindung an Methoden von {@link Fields}.
 * 
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field2<GItem, GValue> extends Field<GItem, GValue>, Getter2<GItem, GValue>, Setter2<GItem, GValue> {

	@Override
	public Field2<Iterable<? extends GItem>, GValue> toAggregated();

	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toValue,
		Getter<? super GValue2, ? extends GValue> toValue2);

	public <GValue2> Getter2<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget, GValue2 emptyTarget,
		GValue2 mixedTarget);

	public Field2<Iterable<? extends GItem>, GValue> toAggregated(GValue emptyTarget, GValue mixedTarget);

	@Override
	public Field2<GItem, GValue> toDefault();

	@Override
	public Field2<GItem, GValue> toDefault(GValue value);

	public Property2<GValue> toProperty();

	public Property2<GValue> toProperty(final GItem item);

	@Override
	public Field2<GItem, GValue> toSynchronized();

	@Override
	public Field2<GItem, GValue> toSynchronized(Object mutex);

	public <GValue2> Field2<GItem, GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet);

	public <GValue2> Field2<GItem, GValue2> toTranslated(final Translator<GValue, GValue2> trans);

}
