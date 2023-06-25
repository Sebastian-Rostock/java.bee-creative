package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Producer} insb. um eine Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer2<GValue> extends Producer<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Producers#buffer(Producer) Producers.buffer(this)}. */
	default Producer3<GValue> buffer() {
		return Producers.buffer(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#buffer(Producer, int, Hasher) Producers.buffer(this, mode, hasher)}. */
	default Producer3<GValue> buffer(int mode, Hasher hasher) {
		return Producers.buffer(this, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer, Field) Properties.from(this, target)}. */
	default <GValue2> Property2<GValue2> concat(Field<? super GValue, GValue2> target) {
		return Properties.from(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Producer, Setter) Consumers.from(this, target)}. */
	default <GValue2> Consumer3<GValue2> concat(Setter<? super GValue, GValue2> target) {
		return Consumers.from(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#translate(Producer, Getter) Producers.translate(this, trans)}. */
	default <GValue2> Producer3<GValue2> concat(Getter<? super GValue, GValue2> trans) {
		return Producers.translate(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronize(Producer) Producers.synchronize(this)}. */
	default Producer2<GValue> synchronize() {
		return Producers.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronize(Producer, Object) Producers.synchronize(this, mutex)}. */
	default Producer2<GValue> synchronize(Object mutex) {
		return Producers.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#from(Producer) Getter.from(this)}. */
	default Getter3<Object, GValue> toGetter() {
		return Getters.from(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer) Properties.from(this, set)}. */
	default Property2<GValue> toProperty(Consumer<? super GValue> set) {
		return Properties.from(this, set);
	}

}
