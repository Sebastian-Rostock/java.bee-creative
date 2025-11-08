package bee.creative.util;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert die von einem {@link UpdateFieldListener} empfangene Nachricht zur Aktualisierung des Werts eines {@link ObservableField}. */
public class UpdateFieldEvent {

	/** Dieses Feld speichert den Sender des Ereignisses. */
	public final ObservableField<?, ?> sender;

	/** Dieses Feld speichert die Eingabe des {@link ObservableField}. */
	public final Object item;

	/** Dieses Feld speichert den alten Wert des {@link ObservableField}. */
	public final Object oldValue;

	/** Dieses Feld speichert den neuen Wert des {@link ObservableField}. */
	public final Object newValue;

	/** Dieser Konstruktor initialisiert die Eigenschafte des Ereignisses. */
	public <ITEM, VALUE> UpdateFieldEvent(ObservableField<? super ITEM, VALUE> sender, ITEM item, VALUE oldValue, VALUE newValue) {
		this.sender = sender;
		this.item = item;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/** Diese Methode setzt den Wert des {@link #sender Datenfeldes} der {@link #item Einagbe} auf den {@link #oldValue alten Wert}. */
	public void setOldValue() {
		this.setValue(this.oldValue);
	}

	/** Diese Methode setzt den Wert des {@link #sender Datenfeldes} der {@link #item Einagbe} auf den {@link #newValue neuen Wert}. */
	public void setNewValue() {
		this.setValue(this.newValue);
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.sender, this.item, this.oldValue, this.newValue);
	}

	@SuppressWarnings ({"unchecked", "rawtypes"})
	private void setValue(Object value) {
		((ObservableField)this.sender).set(this.item, value);
	}

}