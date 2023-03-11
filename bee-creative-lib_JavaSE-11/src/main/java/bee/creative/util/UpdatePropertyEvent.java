package bee.creative.util;

import bee.creative.lang.Objects;
import bee.creative.util.Properties.ObservableProperty;

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
	public <GValue> UpdatePropertyEvent(final ObservableProperty<GValue> sender, final GValue oldValue, final GValue newValue) {
		this.sender = sender;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@SuppressWarnings ({"unchecked", "rawtypes"})
	private void setValue(final Object value) {
		((ObservableProperty)this.sender).set(value);
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

}