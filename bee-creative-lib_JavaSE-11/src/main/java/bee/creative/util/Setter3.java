package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Setter2} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter3<GItem, GValue> extends Setter2<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#translateSetter(Setter, Getter) Setters.translate(this, trans)}. */
	default <GValue2> Setter3<GItem, GValue2> translate(final Getter<? super GValue2, ? extends GValue> trans) {
		return Setters.translateSetter(this, trans);
	}

	@Override
	default Setter3<Iterable<? extends GItem>, GValue> aggregate() {
		return Setters.aggregateSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregateSetter(Setter, Getter) Setters.aggregate(this, trans)}. */
	default <GValue2> Setter3<Iterable<? extends GItem>, GValue2> aggregate(final Getter<? super GValue2, ? extends GValue> trans) {
		return Setters.aggregateSetter(this, trans);
	}

	@Override
	default Setter3<GItem, GValue> optionalize() {
		return Setters.optionalizeSetter(this);
	}

	@Override
	default Setter3<GItem, GValue> synchronize() {
		return Setters.synchronizeSetter(this);
	}

	@Override
	default Setter3<GItem, GValue> synchronize(final Object mutex) {
		return Setters.synchronizeSetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Setter) Fields.from(this)}. */
	default Field2<GItem, GValue> toField() {
		return Fields.from(this);
	}

}
