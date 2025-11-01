package bee.creative.util;

import bee.creative.lang.Objects;

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
	public <VALUE> UpdatePropertyEvent(ObservableProperty<VALUE> sender, VALUE oldValue, VALUE newValue) {
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

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.sender, this.oldValue, this.newValue);
	}

	@SuppressWarnings ({"unchecked", "rawtypes"})
	private void setValue(Object value) {
		((ObservableProperty)this.sender).set(value);
	}

}