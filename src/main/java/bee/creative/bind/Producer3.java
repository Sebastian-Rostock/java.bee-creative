package bee.creative.bind;

/** Diese Schnittstelle ergänzt einen {@link Producer2} um eine erweiterte Anbindung an Methoden von {@link Producers} und {@link Properties}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer3<GValue> extends Producer2<GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Properties#from(Producer) Properties.from(this)}. */
	public Property2<GValue> toProperty();

	@Override
	public Producer3<GValue> toSynchronized();

	@Override
	public Producer3<GValue> toSynchronized(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Producers#toTranslated(Producer, Getter) Producers.toTranslated(this, trans)}. */
	public <GValue2> Producer3<GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> trans);

}
