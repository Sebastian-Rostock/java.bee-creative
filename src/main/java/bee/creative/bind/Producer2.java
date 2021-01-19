package bee.creative.bind;

/** Diese Schnittstelle ergänzt einen {@link Producer} um eine Anbindung an Methoden von {@link Producers} und {@link Getters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Producer2<GValue> extends Producer<GValue> {

	Producer2<GValue> toBuffered();

	Producer2<GValue> toBuffered(int mode);

	Getter3<Object, GValue> toGetter();

	Property2<GValue> toProperty(Consumer<? super GValue> set);

	Producer2<GValue> toSynchronized();

	Producer2<GValue> toSynchronized(final Object mutex);

}
