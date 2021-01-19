package bee.creative.bind;

import bee.creative.lang.Objects.BaseObject;

/** Diese abstrakte Klasse implementiert einen {@link Observable überwachbaren} Wert.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts.
 * @param <GMessage> Typ der Ereignisnachricht.
 * @param <GObserver> Typ der Ereignisempfänger. */
public abstract class ObservableValue<GValue, GMessage, GObserver> extends BaseObject implements Observable<GMessage, GObserver> {

}