package bee.creative.util;

import static bee.creative.util.Fields.aggregatedField;
import static bee.creative.util.Fields.optionalizedField;
import static bee.creative.util.Fields.setupField;
import static bee.creative.util.Fields.synchronizedField;
import static bee.creative.util.Fields.translateField;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.getterFromProducer;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Properties.propertyFromField;

/** Diese Schnittstelle ergänzt ein {@link Field} insb. um eine Anbindung an Methoden von {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Field3<T, V> extends Field<T, V> {

	/** Diese Methode ist eine Abkürzung für {@link Fields#setupField(Field, Getter) setupField(this, setup)}. */
	default Field3<T, V> setup(Getter<? super T, ? extends V> setup) {
		return setupField(this, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setup(Getter) this.setup(getterFromProducer(setup))}. */
	default Field3<T, V> setup(Producer<? extends V> setup) {
		return this.setup(getterFromProducer(setup));
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#translateField(Field, Getter, Getter) translateField(this, getTrans, setTrans)}. */
	default <V2> Field3<T, V2> translate(Getter<? super V, ? extends V2> getTrans, Getter<? super V2, ? extends V> setTrans) {
		return translateField(this, getTrans, setTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#translateField(Field, Translator) translateField(this, trans)}. */
	default <V2> Field3<T, V2> translate(Translator<V, V2> trans) {
		return translateField(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter, Getter) this.}. */
	default Field3<Iterable<? extends T>, V> aggregate() {
		return this.aggregate(neutralGetter(), neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter, Getter, Getter, Getter) this.aggregate(getTrans, setTrans, emptyGetter(),
	 * emptyGetter())}. */
	default <T2> Field3<Iterable<? extends T>, T2> aggregate(Getter<? super V, ? extends T2> getTrans, Getter<? super T2, ? extends V> setTrans) {
		return this.aggregate(getTrans, setTrans, emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregatedField(Field, Getter, Getter, Getter, Getter) aggregatedField(this, getTrans, setTrans, empty,
	 * mixed)}. */
	default <T2 extends Iterable<? extends T>, GValue2> Field3<T2, GValue2> aggregate(Getter<? super V, ? extends GValue2> getTrans,
		Getter<? super GValue2, ? extends V> setTrans, Getter<? super T2, ? extends GValue2> empty, Getter<? super T2, ? extends GValue2> mixed) {
		return aggregatedField(this, getTrans, setTrans, empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalize(Object) this.optionalize(null)}. */
	default Field3<T, V> optionalize() {
		return this.optionalize(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#optionalizedField(Field, Object) optionalizedField(this, value)}. */
	default Field3<T, V> optionalize(V value) {
		return optionalizedField(this, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Field3<T, V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#synchronizedField(Field, Object) synchronizedField(this, mutex)}. */
	default Field3<T, V> synchronize(Object mutex) {
		return synchronizedField(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asProperty(Object) this.asProperty(null)}. */
	default Property3<V> asProperty() {
		return this.asProperty(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFromField(Field) propertyFromField(this)}. */
	default Property3<V> asProperty(T item) {
		return propertyFromField(this);
	}

}
