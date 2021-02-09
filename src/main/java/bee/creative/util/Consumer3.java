package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Consumer2} insb. um eine erweiterte Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Consumer3<GValue> extends Consumer2<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Consumer) Properties.from(this)}. */
	public Property2<GValue> toProperty();

	@Override
	public Consumer3<GValue> synchronize();

	@Override
	public Consumer3<GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Consumers#translate(Consumer, Getter) Consumers.translate(this, trans)}. */
	public <GValue2> Consumer3<GValue2> translate(final Getter<? super GValue2, ? extends GValue> trans);

}
