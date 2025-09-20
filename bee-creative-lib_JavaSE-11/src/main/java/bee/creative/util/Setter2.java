package bee.creative.util;

import static bee.creative.util.Consumers.consumerFromSetter;
import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Producers.producerFromValue;
import static bee.creative.util.Setters.aggregatedSetter;
import static bee.creative.util.Setters.optionalizedSetter;
import static bee.creative.util.Setters.synchronizedSetter;

/** Diese Schnittstelle ergänzt einen {@link Setter} insb. um eine Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Setter2<ITEM, VALUE> extends Setter<ITEM, VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregatedSetter(Setter, Getter) aggregatedSetter(this, neutralGetter())}. */
	default Setter2<Iterable<? extends ITEM>, VALUE> aggregate() {
		return aggregatedSetter(this, neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#optionalizedSetter(Setter) optionalizedSetter(this)}. */
	default Setter2<ITEM, VALUE> optionalize() {
		return optionalizedSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Setter2<ITEM, VALUE> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronizedSetter(Setter, Object) synchronizedSetter(this)}. */
	default Setter2<ITEM, VALUE> synchronize(Object mutex) {
		return synchronizedSetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) fieldFrom(get, this)}. */
	default Field2<ITEM, VALUE> toField(Getter<? super ITEM, ? extends VALUE> get) {
		return fieldFrom(get, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toConsumer(Object) toConsumer(null)}. */
	default Consumer3<VALUE> toConsumer() {
		return this.toConsumer(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFromSetter(Setter, Producer) consumerFrom(producerFromValue(item), this)}. */
	default Consumer3<VALUE> toConsumer(ITEM item) {
		return consumerFromSetter(this, producerFromValue(item));
	}

}
