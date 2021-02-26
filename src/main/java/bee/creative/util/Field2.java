package bee.creative.util;

/** Diese Schnittstelle ergänzt ein {@link Field} insb. um eine Anbindung an Methoden von {@link Fields}.
 * 
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field2<GItem, GValue> extends Field<GItem, GValue>, Getter2<GItem, GValue>, Setter2<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregate(Field) Fields.aggregate(this)}. */
	@Override
	public Field2<Iterable<? extends GItem>, GValue> aggregate();

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregate(Field, Getter, Getter) Fields.aggregate(this, transGet, transSet)}. */
	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> transGet,
		Getter<? super GValue2, ? extends GValue> transSet);

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregate(Field, Getter, Getter, Getter, Getter) Fields.aggregate(this, transGet, transSet, empty,
	 * mixed)}. */
	public <GItem2 extends Iterable<? extends GItem>, GValue2> Field2<GItem2, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> transGet,
		Getter<? super GValue2, ? extends GValue> transSet, Getter<? super GItem2, ? extends GValue2> empty, Getter<? super GItem2, ? extends GValue2> mixed);

	/** Diese Methode ist eine Abkürzung für {@link Fields#optionalize(Field) Fields.optionalize(this)}. */
	@Override
	public Field2<GItem, GValue> optionalize();

	/** Diese Methode ist eine Abkürzung für {@link Fields#optionalize(Field, Object) Fields.optionalize(this, value)}. */
	@Override
	public Field2<GItem, GValue> optionalize(GValue value);

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Field) Properties.from(this)}. */
	public Property2<GValue> toProperty();

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Field, Object) Properties.from(this, this)}. */
	public Property2<GValue> toProperty(GItem item);

	/** Diese Methode ist eine Abkürzung für {@link Fields#setup(Field, Getter) Fields.setup(this, setup)}. */
	public Field2<GItem, GValue> setup(Getter<? super GItem, ? extends GValue> setup);

	/** Diese Methode ist eine Abkürzung für {@link Fields#synchronize(Field) Fields.synchronize(this)}. */
	@Override
	public Field2<GItem, GValue> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Fields#synchronize(Field) Fields.synchronize(this, mutex)}. */
	@Override
	public Field2<GItem, GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Fields#translate(Field, Getter, Getter) Fields.translate(this, transGet, transSet)}. */
	public <GValue2> Field2<GItem, GValue2> translate(Getter<? super GValue, ? extends GValue2> transGet, Getter<? super GValue2, ? extends GValue> transSet);

	/** Diese Methode ist eine Abkürzung für {@link Fields#translate(Field, Translator) Fields.translate(this, trans)}. */
	public <GValue2> Field2<GItem, GValue2> translate(Translator<GValue, GValue2> trans);

}
