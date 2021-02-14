package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Producer} insb. um eine Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer2<GValue> extends Producer<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Producers#buffer(Producer) Producers.buffer(this)}. */
	public Producer3<GValue> buffer();

	/** Diese Methode ist eine Abkürzung für {@link Producers#buffer(Producer, int) Producers.buffer(this, mode)}. */
	public Producer3<GValue> buffer(int mode);

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer, Field) Properties.from(this, target)}. */
	public <GValue2> Property2<GValue2> concat(final Field<? super GValue, GValue2> target);

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Producer, Setter) Consumers.from(this, target)}. */
	public <GValue2> Consumer3<GValue2> concat(final Setter<? super GValue, GValue2> target);

	/** Diese Methode ist eine Abkürzung für {@link Producers#translate(Producer, Getter) Producers.translate(this, trans)}. */
	public <GValue2> Producer3<GValue2> concat(final Getter<? super GValue, GValue2> trans);

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronize(Producer) Producers.synchronize(this)}. */
	public Producer2<GValue> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronize(Producer, Object) Producers.synchronize(this, mutex)}. */
	public Producer2<GValue> synchronize(final Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Getters#from(Producer) Getter.from(this)}. */
	public Getter3<Object, GValue> toGetter();

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer) Properties.from(this, set)}. */
	public Property2<GValue> toProperty(Consumer<? super GValue> set);

}
