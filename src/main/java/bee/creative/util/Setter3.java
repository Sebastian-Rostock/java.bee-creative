package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Setter2} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter3<GItem, GValue> extends Setter2<GItem, GValue> {

	@Override
	public Setter3<Iterable<? extends GItem>, GValue> aggregate();

	/** Diese Methode ist eine Abkürtung für {@link Setters#aggregate(Setter, Getter) Setters.aggregate(this, trans)}. */
	public <GValue2> Setter3<Iterable<? extends GItem>, GValue2> aggregate(Getter<? super GValue2, ? extends GValue> trans);

	@Override
	public Setter3<GItem, GValue> optionalize();

	/** Diese Methode ist eine Abkürtung für {@link Fields#from(Setter) Fields.from(this)}. */
	public Field2<GItem, GValue> toField();

	@Override
	public Setter3<GItem, GValue> synchronize();

	@Override
	public Setter3<GItem, GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Setters#translate(Setter, Getter) Setters.translate(this, trans)}. */
	public <GValue2> Setter3<GItem, GValue2> translate(final Getter<? super GValue2, ? extends GValue> trans);

}
