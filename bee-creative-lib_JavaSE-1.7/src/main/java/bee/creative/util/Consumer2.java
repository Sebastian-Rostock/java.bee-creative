package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Consumer} insb. um eine Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Consumer2<GValue> extends Consumer<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#synchronize(Consumer) Consumers.synchronize(this)}. */
	public Consumer2<GValue> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Consumers#synchronize(Consumer, Object) Consumers.synchronize(this, mutex)}. */
	public Consumer2<GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Setters#from(Consumer) Setter.from(this)}. */
	public Setter3<Object, GValue> toSetter();

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer) Properties.from(get, this)}. */
	public Property2<GValue> toProperty(Producer<? extends GValue> get);

}
