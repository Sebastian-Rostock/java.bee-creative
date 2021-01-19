package bee.creative.bind;

/** Diese Schnittstelle ergänzt einen {@link Consumer2} um eine erweiterte Anbindung an Methoden von {@link Consumers} und {@link Properties}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Consumer3<GValue> extends Consumer2<GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Properties#from(Consumer) Properties.from(this)}. */
	public Property2<GValue> toProperty();

	@Override
	public Consumer3<GValue> toSynchronized();

	@Override
	public Consumer3<GValue> toSynchronized(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Consumers#toTranslated(Consumer, Getter) Consumers.toTranslated(this, trans)}. */
	public <GValue2> Consumer3<GValue2> toTranslated(final Getter<? super GValue2, ? extends GValue> trans);

}
