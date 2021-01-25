package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Setter} insb. um eine Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter2<GItem, GValue> extends Setter<GItem, GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Setters#toAggregated(Setter) Setters.toAggregated(this)}. */
	public Setter2<Iterable<? extends GItem>, GValue> toAggregated();

	/** Diese Methode ist eine Abkürtung für {@link Consumers#from(Setter) Consumer.from(this)}. */
	public Consumer3<GValue> toConsumer();

	/** Diese Methode ist eine Abkürtung für {@link Consumers#from(Setter, Object) Consumer.from(this, item)}. */
	public Consumer3<GValue> toConsumer(final GItem item);

	/** Diese Methode ist eine Abkürtung für {@link Setters#toDefault(Setter) Setters.toDefault(this)}. */
	public Setter2<GItem, GValue> toDefault();

	/** Diese Methode ist eine Abkürtung für {@link Fields#from(Getter, Setter) Fields.from(get, this)}. */
	public Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> get);

	/** Diese Methode ist eine Abkürtung für {@link Setters#toSynchronized(Setter) Setters.toSynchronized(this)}. */
	public Setter2<GItem, GValue> toSynchronized();

	/** Diese Methode ist eine Abkürtung für {@link Setters#toSynchronized(Setter, Object) Setters.toSynchronized(this)}. */
	public Setter2<GItem, GValue> toSynchronized(final Object mutex);

}
