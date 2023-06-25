package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Setter} insb. um eine Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter2<GItem, GValue> extends Setter<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregate(Setter) Setters.aggregate(this)}. */
	default Setter2<Iterable<? extends GItem>, GValue> aggregate() {
		return Setters.aggregate(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#optionalize(Setter) Setters.optionalize(this)}. */
	default Setter2<GItem, GValue> optionalize() {
		return Setters.optionalize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronize(Setter) Setters.synchronize(this)}. */
	default Setter2<GItem, GValue> synchronize() {
		return Setters.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronize(Setter, Object) Setters.synchronize(this)}. */
	default Setter2<GItem, GValue> synchronize(final Object mutex) {
		return Setters.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Getter, Setter) Fields.from(get, this)}. */
	default Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> get) {
		return Fields.from(get, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Setter) Consumer.from(this)}. */
	default Consumer3<GValue> toConsumer() {
		return Consumers.from(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Setter, Object) Consumer.from(this, item)}. */
	default Consumer3<GValue> toConsumer(final GItem item) {
		return Consumers.from(this, item);
	}
}
