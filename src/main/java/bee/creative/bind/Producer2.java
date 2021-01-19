package bee.creative.bind;

/** Diese Schnittstelle ergänzt einen {@link Producer} um eine Anbindung an Methoden von {@link Producers} und {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer2<GValue> extends Producer<GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Producers#toBuffered(Producer) Producers.toBuffered(this)}. */
	public Producer3<GValue> toBuffered();

	/** Diese Methode ist eine Abkürtung für {@link Producers#toBuffered(Producer, int) Producers.toBuffered(this, mode)}. */
	public Producer3<GValue> toBuffered(int mode);

	/** Diese Methode ist eine Abkürtung für {@link Getters#from(Producer) Getter.from(this)}. */
	public Getter3<Object, GValue> toGetter();

	/** Diese Methode ist eine Abkürtung für {@link Properties#from(Producer) Properties.from(this, set)}. */
	public Property2<GValue> toProperty(Consumer<? super GValue> set);

	/** Diese Methode ist eine Abkürtung für {@link Producers#toSynchronized(Producer) Producers.toSynchronized(this)}. */
	public Producer2<GValue> toSynchronized();

	/** Diese Methode ist eine Abkürtung für {@link Producers#toSynchronized(Producer, Object) Producers.toSynchronized(this, mutex)}. */
	public Producer2<GValue> toSynchronized(final Object mutex);

}
