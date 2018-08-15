package bee.creative.util;

import bee.creative.util.ObservableField.ChangeFieldEvent;
import bee.creative.util.ObservableField.ChangeFieldListener;

/** Diese Klasse implementiert ein überwachbares {@link Field Datenfeld}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public class ObservableField<GInput, GValue> implements Field<GInput, GValue>, Listenable<ChangeFieldEvent, ChangeFieldListener> {

	public static class ChangeFieldEvent {

		public final ObservableField<?, ?> sender;

		public final Field<?, ?> field;

		public final Object input;

		public final Object oldValue;

		public final Object newValue;

		public ChangeFieldEvent(final ObservableField<?, ?> sender, final Field<?, ?> field, final Object input, final Object oldValue, final Object newValue) {
			this.sender = sender;
			this.field = field;
			this.input = input;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

	}

	public static interface ChangeFieldListener {

		public void onChangeField(ChangeFieldEvent event);

	}

	public static final Listenables<ChangeFieldEvent, ChangeFieldListener> OnChangeField = new Listenables<ChangeFieldEvent, ChangeFieldListener>() {

		@Override
		protected void customFire(final Object sender, final ChangeFieldEvent event, final ChangeFieldListener listener) {
			listener.onChangeField(event);
		}

	};

	/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
	public final Field<? super GInput, GValue> field;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
	 *
	 * @param field überwachtes Datenfeld. */
	public ObservableField(final Field<? super GInput, GValue> field) {
		this.field = Objects.assertNotNull(field);
	}

	protected GValue customClone(final GValue value) {
		return value;
	}

	protected boolean customEquals(final GValue value1, final GValue value2) {
		return Objects.equals(value1, value2);
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final GInput input) {
		return this.field.get(input);
	}

	/** {@inheritDoc}
	 *
	 * @throws IllegalStateException Wenn der Wert während */
	@Override
	public void set(final GInput input, final GValue newValue) throws IllegalStateException {
		GValue oldValue = this.field.get(input);
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.field.set(input, newValue);
		this.fire(new ChangeFieldEvent(this, this.field, input, oldValue, newValue));
	}

	/** {@inheritDoc} */
	@Override
	public ChangeFieldListener put(final ChangeFieldListener listener) throws IllegalArgumentException {
		return ObservableField.OnChangeField.put(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public ChangeFieldListener putWeak(final ChangeFieldListener listener) throws IllegalArgumentException {
		return ObservableField.OnChangeField.putWeak(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final ChangeFieldListener listener) throws IllegalArgumentException {
		ObservableField.OnChangeField.pop(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public ChangeFieldEvent fire(final ChangeFieldEvent event) throws NullPointerException {
		return ObservableField.OnChangeField.fire(this, event);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.field.toString();
	}

}