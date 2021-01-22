package bee.creative.bind;

/** Diese Schnittstelle ergänzt einen {@link Setter2} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter3<GItem, GValue> extends Setter2<GItem, GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Fields#from(Setter) Fields.from(this)}. */
	public Field2<GItem, GValue> toField();

	@Override
	public Setter3<GItem, GValue> toDefault();

	@Override
	public Setter3<Iterable<? extends GItem>, GValue> toAggregated();

	/** Diese Methode ist eine Abkürtung für {@link Setters#toAggregated(Setter, Getter) Setters.toAggregated(this, trans)}. */
	public <GValue2> Setter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue2, ? extends GValue> trans);

	/** Diese Methode ist eine Abkürtung für {@link Setters#toTranslated(Setter, Getter) Setters.toTranslated(this, trans)}. */
	public <GValue2> Setter3<GItem, GValue2> toTranslated(final Getter<? super GValue2, ? extends GValue> trans);

	@Override
	public Setter3<GItem, GValue> toSynchronized();

	@Override
	public Setter3<GItem, GValue> toSynchronized(Object mutex);

}
