package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Getter2} insb. um eine erweiterte Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter3<GItem, GValue> extends Getter2<GItem, GValue> {

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> aggregate();

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregate(Getter, Getter) Getters.aggregate(this, trans)}. */
	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> trans);

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregate(Getter, Getter, Getter, Getter) Getters.aggregate(this, trans, empty, mixed)}. */
	public <GItem2 extends Iterable<? extends GItem>, GValue2> Getter3<GItem2, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> trans,
		Getter<? super GItem2, ? extends GValue2> empty, Getter<? super GItem2, ? extends GValue2> mixed);

	@Override
	public Getter3<GItem, GValue> optionalize();

	@Override
	public Getter3<GItem, GValue> optionalize(GValue value);

	@Override
	public Getter3<GItem, GValue> synchronize();

	@Override
	public Getter3<GItem, GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Getter) Fields.from(this)}. */
	public Field2<GItem, GValue> toField();

}
