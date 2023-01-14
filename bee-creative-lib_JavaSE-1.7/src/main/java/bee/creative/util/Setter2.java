package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Setter} insb. um eine Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter2<GItem, GValue> extends Setter<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregate(Setter) Setters.aggregate(this)}. */
	public Setter2<Iterable<? extends GItem>, GValue> aggregate();

	/** Diese Methode ist eine Abkürzung für {@link Setters#optionalize(Setter) Setters.optionalize(this)}. */
	public Setter2<GItem, GValue> optionalize();

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronize(Setter) Setters.synchronize(this)}. */
	public Setter2<GItem, GValue> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronize(Setter, Object) Setters.synchronize(this)}. */
	public Setter2<GItem, GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Getter, Setter) Fields.from(get, this)}. */
	public Field2<GItem, GValue> toField(Getter<? super GItem, ? extends GValue> get);

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Setter) Consumer.from(this)}. */
	public Consumer3<GValue> toConsumer();

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Setter, Object) Consumer.from(this, item)}. */
	public Consumer3<GValue> toConsumer(GItem item);

}
