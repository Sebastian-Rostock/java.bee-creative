package bee.creative.util;

import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Properties.observableProperty;
import static bee.creative.util.Properties.setupProperty;
import static bee.creative.util.Properties.synchronizedProperty;
import static bee.creative.util.Properties.translatedProperty;
import bee.creative.util.Properties.ObservableProperty;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Property3<V> extends Property<V> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#setupProperty(Property, Producer) setupProperty(this, setup)}. */
	default Property3<V> setup(Producer<? extends V> setup) {
		return setupProperty(this, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#observableProperty(Property) observableProperty(this)}. */
	default ObservableProperty<V> observe() {
		return observableProperty(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translatedProperty(Property, Getter, Getter) translatedProperty(this, transGet, transSet)}. */
	default <V2> Property3<V2> translate(Getter<? super V, ? extends V2> transGet, Getter<? super V2, ? extends V> transSet) {
		return translatedProperty(this, transGet, transSet);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translatedProperty(Property, Translator) translatedProperty(this, trans)}. */
	default <V2> Property3<V2> translate(Translator<V, V2> trans) {
		return translatedProperty(this, trans);
	}

	default Property3<V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronizedProperty(Property, Object) synchronizedProperty(this, mutex)}. */
	default Property3<V> synchronize(Object mutex) {
		return synchronizedProperty(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Property) fieldFrom(this)}. */
	default Field2<Object, V> asField() {
		return fieldFrom(this);
	}

}
