package bee.creative.util;

/** Diese Schnittstelle ergänzt ein {@link Field} insb. um eine Anbindung an Methoden von {@link Fields}.
 * 
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field2<GItem, GValue> extends Field<GItem, GValue>, Getter2<GItem, GValue>, Setter2<GItem, GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Fields#toAggregated(Field) Fields.toAggregated(this)}. */
	@Override
	public Field2<Iterable<? extends GItem>, GValue> toAggregated();

	/** Diese Methode ist eine Abkürtung für {@link Fields#toAggregated(Field, Getter, Getter) Fields.toAggregated(this, transGet, transSet)}. */
	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> transGet,
		Getter<? super GValue2, ? extends GValue> transSet);

	/** Diese Methode ist eine Abkürtung für {@link Fields#toAggregated(Field, Getter, Getter, Getter, Getter) Fields.toAggregated(this, transGet, transSet,
	 * empty, mixed)}. */
	public <GItem2 extends Iterable<? extends GItem>, GValue2> Field2<GItem2, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> transGet,
		Getter<? super GValue2, ? extends GValue> transSet, Getter<? super GItem2, ? extends GValue2> empty, Getter<? super GItem2, ? extends GValue2> mixed);

	/** Diese Methode ist eine Abkürtung für {@link Fields#toDefault(Field) Fields.toDefault(this)}. */
	@Override
	public Field2<GItem, GValue> toDefault();

	/** Diese Methode ist eine Abkürtung für {@link Fields#toDefault(Field, Object) Fields.toDefault(this, value)}. */
	@Override
	public Field2<GItem, GValue> toDefault(GValue value);

	/** Diese Methode ist eine Abkürtung für {@link Properties#from(Field) Properties.from(this)}. */
	public Property2<GValue> toProperty();

	/** Diese Methode ist eine Abkürtung für {@link Properties#from(Field, Object) Properties.from(this, this)}. */
	public Property2<GValue> toProperty(final GItem item);

	/** Diese Methode ist eine Abkürtung für {@link Fields#toSetup(Field, Getter) Fields.toSetup(this, setup)}. */
	public Field2<GItem, GValue> toSetup(final Getter<? super GItem, ? extends GValue> setup);

	/** Diese Methode ist eine Abkürtung für {@link Fields#toSynchronized(Field) Fields.toSynchronized(this)}. */
	@Override
	public Field2<GItem, GValue> toSynchronized();

	/** Diese Methode ist eine Abkürtung für {@link Fields#toSynchronized(Field) Fields.toSynchronized(this, mutex)}. */
	@Override
	public Field2<GItem, GValue> toSynchronized(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Fields#toTranslated(Field, Getter, Getter) Fields.toTranslated(this, transGet, transSet)}. */
	public <GValue2> Field2<GItem, GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet);

	/** Diese Methode ist eine Abkürtung für {@link Fields#toTranslated(Field, Translator) Fields.toTranslated(this, trans)}. */
	public <GValue2> Field2<GItem, GValue2> toTranslated(final Translator<GValue, GValue2> trans);

}
