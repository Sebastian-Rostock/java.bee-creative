package bee.creative.util;

import static bee.creative.util.Observers.observersFrom;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link Observable} {@link Property3}, das das {@link #get() Lesen} und {@link #set(Object) Schreiben} an ein gegebenes
 * {@link Property} delegiert und beim ändernden Schreiben ein {@link UpdatePropertyListener Änderungsereignis} auslöst.
 *
 * @param <V> Typ des Werts der Eigenschaft. */
public class ObservableProperty<V> implements Property3<V>, Observable2<UpdatePropertyEvent, UpdatePropertyListener> {

	/** Dieser Konstruktor initialisiert die überwachte Eigenschaft. */
	public ObservableProperty(Property<V> that) {
		this.that = Objects.notNull(that);
	}

	@Override
	public V get() {
		return this.that.get();
	}

	@Override
	public void set(V newValue) {
		var oldValue = this.that.get();
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.that.set(newValue);
		this.fire(new UpdatePropertyEvent(this, oldValue, newValue));
	}

	@Override
	public Observers<UpdatePropertyEvent, UpdatePropertyListener> observers() {
		return observers;
	}

	@Override
	public String toString() {
		return this.that.toString();
	}

	/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese Methode
	 * eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann nötig, wenn
	 * der alte Wert sich durch das Schreiben des neuen ändert.
	 *
	 * @param value alter Wert.
	 * @return gegebener oder kopierter Wert. */
	protected V customClone(V value) {
		return value;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
	 * Wertänderung eingesetzt.
	 *
	 * @param value1 alter Wert.
	 * @param value2 neuer Wert.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	protected boolean customEquals(V value1, V value2) {
		return Objects.deepEquals(value1, value2);
	}

	private static final Observers<UpdatePropertyEvent, UpdatePropertyListener> observers = observersFrom(UpdatePropertyListener::onUpdateProperty);

	private final Property<V> that;

}