package bee.creative.util;

import static bee.creative.util.Consumers.consumerFrom;
import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Setters.aggregateSetter;
import static bee.creative.util.Setters.optionalizeSetter;
import static bee.creative.util.Setters.synchronizeSetter;

/** Diese Schnittstelle ergänzt einen {@link Setter} insb. um eine Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Setter2<ITEM, VALUE> extends Setter<ITEM, VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregateSetter(Setter) aggregateSetter(this)}. */
	default Setter2<Iterable<? extends ITEM>, VALUE> aggregate() {
		return aggregateSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#optionalizeSetter(Setter) optionalizeSetter(this)}. */
	default Setter2<ITEM, VALUE> optionalize() {
		return optionalizeSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronizeSetter(Setter) synchronizeSetter(this)}. */
	default Setter2<ITEM, VALUE> synchronize() {
		return synchronizeSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronizeSetter(Setter, Object) synchronizeSetter(this)}. */
	default Setter2<ITEM, VALUE> synchronize(Object mutex) {
		return synchronizeSetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) fieldFrom(get, this)}. */
	default Field2<ITEM, VALUE> toField(Getter<? super ITEM, ? extends VALUE> get) {
		return fieldFrom(get, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Setter) consumerFrom(this)}. */
	default Consumer3<VALUE> toConsumer() {
		return consumerFrom(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Setter, Object) consumerFrom(this, item)}. */
	default Consumer3<VALUE> toConsumer(ITEM item) {
		return consumerFrom(this, item);
	}

}
