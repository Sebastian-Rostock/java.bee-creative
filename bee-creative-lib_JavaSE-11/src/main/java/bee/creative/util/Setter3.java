package bee.creative.util;

import static bee.creative.util.Fields.emptyField;
import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Setters.aggregatedSetter;
import static bee.creative.util.Setters.optionalizedSetter;
import static bee.creative.util.Setters.synchronizedSetter;
import static bee.creative.util.Setters.translatedSetter;

/** Diese Schnittstelle ergänzt einen {@link Setter2} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Setter3<ITEM, VALUE> extends Setter2<ITEM, VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#translatedSetter(Setter, Getter) translatedSetter(this, trans)}. */
	default <VALUE2> Setter3<ITEM, VALUE2> translate(Getter<? super VALUE2, ? extends VALUE> trans) {
		return translatedSetter(this, trans);
	}

	@Override
	default Setter3<Iterable<? extends ITEM>, VALUE> aggregate() {
		return aggregatedSetter(this, neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregatedSetter(Setter, Getter) aggregatedSetter(this, trans)}. */
	default <VALUE2> Setter3<Iterable<? extends ITEM>, VALUE2> aggregate(Getter<? super VALUE2, ? extends VALUE> trans) {
		return aggregatedSetter(this, trans);
	}

	@Override
	default Setter3<ITEM, VALUE> optionalize() {
		return optionalizedSetter(this);
	}

	@Override
	default Setter3<ITEM, VALUE> synchronize() {
		return this.synchronize(this);
	}

	@Override
	default Setter3<ITEM, VALUE> synchronize(Object mutex) {
		return synchronizedSetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) fieldFrom(emptyField(), this)}. */
	default Field2<ITEM, VALUE> toField() {
		return fieldFrom(emptyField(), this);
	}

}
