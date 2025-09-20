package bee.creative.util;

import static bee.creative.util.Fields.emptyField;
import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Getters.aggregatedGetter;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Getters.optionalizedGetter;
import static bee.creative.util.Getters.synchronizedGetter;

/** Diese Schnittstelle ergänzt einen {@link Getter2} insb. um eine erweiterte Anbindung an Methoden von {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter3<GItem, GValue> extends Getter2<GItem, GValue> {

	@Override
	default Getter3<Iterable<? extends GItem>, GValue> aggregate() {
		return aggregatedGetter(this, neutralGetter(), emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Getter, Getter, Getter, Getter) aggregatedGetter(this, trans, emptyGetter(),
	 * emptyGetter())}. */
	default <GValue2> Getter3<Iterable<? extends GItem>, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> trans) {
		return aggregatedGetter(this, trans, emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Getter, Getter, Getter, Getter) aggregatedGetter(this, trans, empty, mixed)}. */
	default <GItem2 extends Iterable<? extends GItem>, GValue2> Getter3<GItem2, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> trans,
		Getter<? super GItem2, ? extends GValue2> empty, Getter<? super GItem2, ? extends GValue2> mixed) {
		return aggregatedGetter(this, trans, empty, mixed);
	}

	@Override
	default Getter3<GItem, GValue> optionalize() {
		return this.optionalize(null);
	}

	@Override
	default Getter3<GItem, GValue> optionalize(GValue value) {
		return optionalizedGetter(this, value);
	}

	@Override
	default Getter3<GItem, GValue> synchronize() {
		return this.synchronize(this);
	}

	@Override
	default Getter3<GItem, GValue> synchronize(Object mutex) {
		return synchronizedGetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) fieldFrom(this, emptyField())}. */
	default Field2<GItem, GValue> toField() {
		return fieldFrom(this, emptyField());
	}

}
