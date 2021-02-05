package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Producer2} insb. um eine erweiterte Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer3<GValue> extends Producer2<GValue> {

	@Override
	public Producer3<GValue> synchronize();

	@Override
	public Producer3<GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Properties#from(Producer) Properties.from(this)}. */
	public Property2<GValue> toProperty();

}
