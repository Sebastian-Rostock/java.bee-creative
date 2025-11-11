package bee.creative.util;

/** Diese Klasse implementiert die von einem {@link UpdatePropertyListener} empfangene Nachricht zur Aktualisierung des Werts eines
 * {@link ObservableProperty}. */
public class UpdatePropertyEvent {

	/** Dieses Feld speichert den Sender des Ereignisses. */
	public final ObservableProperty<?> sender;

	/** Dieses Feld speichert den alten Wert des {@link ObservableProperty}. */
	public final Object oldValue;

	/** Dieses Feld speichert den neuen Wert des {@link ObservableProperty}. */
	public final Object newValue;

	/** Dieser Konstruktor initialisiert die Eigenschafte des Ereignisses. */
	public <V> UpdatePropertyEvent(ObservableProperty<V> sender, V oldValue, V newValue) {
		this.sender = sender;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/** Diese Methode setzt den Wert der {@link #sender Eigenschaft} auf den {@link #oldValue alten Wert}. */
	public void setOldValue() {
		this.setValue(this.oldValue);
	}

	/** Diese Methode setzt den Wert der {@link #sender Eigenschaft} auf den {@link #newValue neuen Wert}. */
	public void setNewValue() {
		this.setValue(this.newValue);
	}

	@SuppressWarnings ({"unchecked", "rawtypes"})
	private void setValue(Object value) {
		((ObservableProperty)this.sender).set(value);
	}

}