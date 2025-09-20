package bee.creative.util;

import static bee.creative.util.Fields.aggregatedField;
import static bee.creative.util.Fields.optionalizedField;
import static bee.creative.util.Fields.setupField;
import static bee.creative.util.Fields.synchronizedField;
import static bee.creative.util.Fields.translateField;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Properties.propertyFrom;

/** Diese Schnittstelle ergänzt ein {@link Field} insb. um eine Anbindung an Methoden von {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field2<GItem, GValue> extends Field<GItem, GValue>, Getter2<GItem, GValue>, Setter2<GItem, GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Fields#setupField(Field, Getter) setupField(this, setup)}. */
	default Field2<GItem, GValue> setup(Getter<? super GItem, ? extends GValue> setup) {
		return setupField(this, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#translateField(Field, Getter, Getter) translateField(this, getTrans, setTrans)}. */
	default <GValue2> Field2<GItem, GValue2> translate(Getter<? super GValue, ? extends GValue2> getTrans, Getter<? super GValue2, ? extends GValue> setTrans) {
		return translateField(this, getTrans, setTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#translateField(Field, Translator) translateField(this, trans)}. */
	default <GValue2> Field2<GItem, GValue2> translate(Translator<GValue, GValue2> trans) {
		return translateField(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregatedField(Field, Getter, Getter, Getter, Getter) aggregatedField(this, neutralGetter(),
	 * neutralGetter(), emptyGetter(), emptyGetter())}. */
	@Override
	default Field2<Iterable<? extends GItem>, GValue> aggregate() {
		return aggregatedField(this, neutralGetter(), neutralGetter(), emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregatedField(Field, Getter, Getter, Getter, Getter) aggregatedField(this, getTrans, setTrans,
	 * emptyGetter(), emptyGetter())}. */
	default <GValue2> Field2<Iterable<? extends GItem>, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> getTrans,
		Getter<? super GValue2, ? extends GValue> setTrans) {
		return aggregatedField(this, getTrans, setTrans, emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#aggregatedField(Field, Getter, Getter, Getter, Getter) aggregatedField(this, getTrans, setTrans, empty,
	 * mixed)}. */
	default <GItem2 extends Iterable<? extends GItem>, GValue2> Field2<GItem2, GValue2> aggregate(Getter<? super GValue, ? extends GValue2> getTrans,
		Getter<? super GValue2, ? extends GValue> setTrans, Getter<? super GItem2, ? extends GValue2> empty, Getter<? super GItem2, ? extends GValue2> mixed) {
		return aggregatedField(this, getTrans, setTrans, empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalize(Object) this.optionalize(null)}. */
	@Override
	default Field2<GItem, GValue> optionalize() {
		return this.optionalize(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#optionalizedField(Field, Object) optionalizedField(this, value)}. */
	@Override
	default Field2<GItem, GValue> optionalize(GValue value) {
		return optionalizedField(this, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	@Override
	default Field2<GItem, GValue> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#synchronizedField(Field, Object) synchronizedField(this, mutex)}. */
	@Override
	default Field2<GItem, GValue> synchronize(Object mutex) {
		return synchronizedField(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toProperty(Object) this.toProperty(null)}. */
	default Property2<GValue> toProperty() {
		return this.toProperty(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(() -> this.get(item), (value) -> this.set(item,
	 * value))}. */
	default Property2<GValue> toProperty(GItem item) {
		return Properties.propertyFromField(this);
	}

}
