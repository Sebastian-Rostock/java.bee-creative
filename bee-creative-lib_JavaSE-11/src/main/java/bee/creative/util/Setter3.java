package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Setter2} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter3<GItem, GValue> extends Setter2<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#translate(Setter, Getter) Setters.translate(this, trans)}. */
	default <GValue2> Setter3<GItem, GValue2> translate(final Getter<? super GValue2, ? extends GValue> trans) {
		return Setters.translate(this, trans);
	}

	@Override
	default Setter3<Iterable<? extends GItem>, GValue> aggregate() {
		return Setters.aggregate(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregate(Setter, Getter) Setters.aggregate(this, trans)}. */
	default <GValue2> Setter3<Iterable<? extends GItem>, GValue2> aggregate(final Getter<? super GValue2, ? extends GValue> trans) {
		return Setters.aggregate(this, trans);
	}

	@Override
	default Setter3<GItem, GValue> optionalize() {
		return Setters.optionalize(this);
	}

	@Override
	default Setter3<GItem, GValue> synchronize() {
		return Setters.synchronize(this);
	}

	@Override
	default Setter3<GItem, GValue> synchronize(final Object mutex) {
		return Setters.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Setter) Fields.from(this)}. */
	default Field2<GItem, GValue> toField() {
		return Fields.from(this);
	}

}
