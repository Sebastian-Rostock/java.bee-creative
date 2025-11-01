package bee.creative.util;

import static bee.creative.util.Consumers.consumerFromSetter;
import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Producers.producerFromValue;
import static bee.creative.util.Setters.aggregatedSetter;
import static bee.creative.util.Setters.optionalizedSetter;
import static bee.creative.util.Setters.synchronizedSetter;
import static bee.creative.util.Setters.translatedSetter;

/** Diese Schnittstelle ergänzt einen {@link Setter3} insb. um eine erweiterte Anbindung an Methoden von {@link Setters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Setter3<T, V> extends Setter<T, V> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#translatedSetter(Setter, Getter) translatedSetter(this, trans)}. */
	default <V2> Setter3<T, V2> translate(Getter<? super V2, ? extends V> trans) {
		return translatedSetter(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter) this.aggregate(neutralGetter())}. */
	default Setter3<Iterable<? extends T>, V> aggregate() {
		return this.aggregate(neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#aggregatedSetter(Setter, Getter) aggregatedSetter(this, trans)}. */
	default <V2> Setter3<Iterable<? extends T>, V2> aggregate(Getter<? super V2, ? extends V> trans) {
		return aggregatedSetter(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#optionalizedSetter(Setter) optionalizedSetter(this)}. */
	default Setter3<T, V> optionalize() {
		return optionalizedSetter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Setter3<T, V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#synchronizedSetter(Setter, Object) synchronizedSetter(this)}. */
	default Setter3<T, V> synchronize(Object mutex) {
		return synchronizedSetter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asField(Getter) this.asField(emptyGetter())}. */
	default Field2<T, V> asField() {
		return this.asField(emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Getter, Setter) fieldFrom(get, this)}. */
	default Field2<T, V> asField(Getter<? super T, ? extends V> get) {
		return fieldFrom(get, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asConsumer(Object) this.asConsumer(null)}. */
	default Consumer3<V> asConsumer() {
		return this.asConsumer(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFromSetter(Setter, Producer) consumerFromSetter(this, producerFromValue(item))}. */
	default Consumer3<V> asConsumer(T item) {
		return consumerFromSetter(this, producerFromValue(item));
	}

}
