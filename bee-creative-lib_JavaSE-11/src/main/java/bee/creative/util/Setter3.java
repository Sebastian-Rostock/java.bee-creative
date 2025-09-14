package bee.creative.util;

import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Setters.aggregateSetter;
import static bee.creative.util.Setters.optionalizeSetter;
import static bee.creative.util.Setters.synchronizeSetter;
import static bee.creative.util.Setters.translateSetter;

/** Diese Schnittstelle ergänzt einen {@link Setter2} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Setter3<ITEM, VALUE> extends Setter2<ITEM, VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#translateSetter(Setter, Getter) Setters.translate(this, trans)}. */
	default <VALUE2> Setter3<ITEM, VALUE2> translate(Getter<? super VALUE2, ? extends VALUE> trans) {
		return translateSetter(this, trans);
	}

	@Override
	default Setter3<Iterable<? extends ITEM>, VALUE> aggregate() {
		return aggregateSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregateSetter(Setter, Getter) aggregateSetter(this, trans)}. */
	default <VALUE2> Setter3<Iterable<? extends ITEM>, VALUE2> aggregate(final Getter<? super VALUE2, ? extends VALUE> trans) {
		return aggregateSetter(this, trans);
	}

	@Override
	default Setter3<ITEM, VALUE> optionalize() {
		return optionalizeSetter(this);
	}

	@Override
	default Setter3<ITEM, VALUE> synchronize() {
		return synchronizeSetter(this);
	}

	@Override
	default Setter3<ITEM, VALUE> synchronize(final Object mutex) {
		return synchronizeSetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Setter) fieldFrom(this)}. */
	default Field2<ITEM, VALUE> toField() {
		return fieldFrom(this);
	}

}
