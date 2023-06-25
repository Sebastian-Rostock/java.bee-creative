package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Consumer} insb. um eine Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Consumer2<GValue> extends Consumer<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#synchronize(Consumer) Consumers.synchronize(this)}. */
	default Consumer2<GValue> synchronize() {
		return Consumers.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#synchronize(Consumer, Object) Consumers.synchronize(this, mutex)}. */
	default Consumer2<GValue> synchronize(Object mutex) {
		return Consumers.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#from(Consumer) Setter.from(this)}. */
	default Setter3<Object, GValue> toSetter() {
		return Setters.from(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer, Consumer) Properties.from(get, this)}. */
	default Property2<GValue> toProperty(Producer<? extends GValue> get) {
		return Properties.from(get, this);
	}

}
