package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Producer2} insb. um eine erweiterte Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer3<GValue> extends Producer2<GValue> {

	@Override
	default Producer3<GValue> synchronize() {
		return Producers.synchronize(this);
	}

	@Override
	default Producer3<GValue> synchronize(final Object mutex) {
		return Producers.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer) Properties.from(this)}. */
	default Property2<GValue> toProperty() {
		return Properties.from(this);
	}

}
