package bee.creative.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.util.Assignment;
import bee.creative.util.Converter;
import bee.creative.util.Field;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsmethoden und Klassen zu {@link Item}s und {@link Pool}s.
 * 
 * @see Item
 * @see Pool
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Items {

	/**
	 * Diese Klasse implementiert das abstrakte Ereignis, dass beim Modifizieren eines {@link Item}s ausgelöst wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class ModifyItemEvent {

		/**
		 * Dieses Feld speichert den Modus, in dem normale Ereignisse ausgelöst werden.
		 */
		public static final int DEFAULT_MODE = 0;

		/**
		 * Dieses Feld speichert den Modus, in dem rückgängig machende Ereignisse ausgelöst werden.
		 */
		public static final int UNDOING_MODE = 1;

		/**
		 * Dieses Feld speichert den Modus, in dem wiederholende Ereignisse ausgelöst werden.
		 */
		public static final int REDOING_MODE = 2;

		/**
		 * Dieses Feld speichert den Modus.
		 * 
		 * @see #DEFAULT_MODE
		 * @see #UNDOING_MODE
		 * @see #REDOING_MODE
		 */
		public final int mode;

		/**
		 * Dieses Feld speichert den {@link Type} des modifizierten {@link Item}s.
		 */
		public final Type<?> type;

		/**
		 * Dieses Feld speichert das modifizierte {@link Item}.
		 */
		public final Item item;

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @param mode Modus.
		 * @param item modifiziertes {@link Item}.
		 * @throws NullPointerException Wenn das gegebene {@link Item} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemEvent(final int mode, final Item item) throws NullPointerException, IllegalArgumentException {
			if((mode < ModifyItemEvent.DEFAULT_MODE) || (mode > ModifyItemEvent.REDOING_MODE)) throw new IllegalArgumentException("mode is invalid");
			if(item == null) throw new NullPointerException("item is null");
			this.mode = mode;
			this.type = item.type();
			this.item = item;
		}

	}

	/**
	 * Diese Klasse implementiert das Ereignis, dass beim Modifizieren eienr Eigenschaft eines {@link Item}s ausgelöst wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ModifyItemFieldEvent extends ModifyItemEvent {

		/**
		 * Dieses Feld speichert das {@link Field} zur modifizierten Eigenschaft.
		 */
		public final Field<?, ?> field;

		/**
		 * Dieses Feld speichert den alten Wert der Eigenschaft.
		 */
		public final Object oldValue;

		/**
		 * Dieses Feld speichert den neuen Wert der Eigenschaft.
		 */
		public final Object newValue;

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @see #DEFAULT_MODE
		 * @param item modifiziertes {@link Item}.
		 * @param field {@link Field} zur modifizierten Eigenschaft.
		 * @param oldValue alter Wert oder {@code null}.
		 * @param newValue neuer Wert oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Item} bzw. {@link Field} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemFieldEvent(final Item item, final Field<?, ?> field, final Object oldValue, final Object newValue) throws NullPointerException,
			IllegalArgumentException {
			this(ModifyItemEvent.DEFAULT_MODE, item, field, oldValue, newValue);
		}

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @param mode Modus.
		 * @param item modifiziertes {@link Item}.
		 * @param field {@link Field} zur modifizierten Eigenschaft.
		 * @param oldValue alter Wert oder {@code null}.
		 * @param newValue neuer Wert oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Item} bzw. {@link Field} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemFieldEvent(final int mode, final Item item, final Field<?, ?> field, final Object oldValue, final Object newValue)
			throws NullPointerException, IllegalArgumentException {
			super(mode, item);
			if(field == null) throw new NullPointerException("field is null");
			this.field = field;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

	}

	/**
	 * Diese Klasse implementiert das Ereignis, dass beim Modifizieren des Status eins {@link Item}s ausgelöst wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ModifyItemStateEvent extends ModifyItemEvent {

		/**
		 * Dieses Feld speichert den alten Status des {@link Item}s.
		 */
		public final int oldState;

		/**
		 * Dieses Feld speichert den neuen Status des {@link Item}s.
		 */
		public final int newState;

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @see #DEFAULT_MODE
		 * @param item {@link Item}.
		 * @param oldState alter Status des {@link Item}s.
		 * @param newState neuer Status des {@link Item}s.
		 * @throws NullPointerException Wenn das gegebene {@link Item} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemStateEvent(final Item item, final int oldState, final int newState) throws NullPointerException, IllegalArgumentException {
			this(ModifyItemEvent.DEFAULT_MODE, item, oldState, newState);
		}

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @param mode Modus.
		 * @param item {@link Item}.
		 * @param oldState alter Status des {@link Item}s.
		 * @param newState neuer Status des {@link Item}s.
		 * @throws NullPointerException Wenn das gegebene {@link Item} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemStateEvent(final int mode, final Item item, final int oldState, final int newState) throws NullPointerException, IllegalArgumentException {
			super(mode, item);
			this.oldState = oldState;
			this.newState = newState;
		}

	}

	/**
	 * Diese Schnittstelle definiert die Methoden zur Behandlung des {@link ModifyItemStateEvent}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ModifyItemStateListener {

		/**
		 * Diese Methode wird nach der Modifikation des Status eines {@link Item}s aufgerufen.
		 * 
		 * @param event {@link ModifyItemStateEvent}.
		 */
		public void onModifyItemState(ModifyItemStateEvent event);

	}

	/**
	 * Diese Schnittstelle definiert die Methoden zur Behandlung des {@link ModifyItemFieldEvent}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ModifyItemFieldListener {

		/**
		 * Diese Methode wird nach der Modifikation einer Eigenschaft eines {@link Item}s aufgerufen.
		 * 
		 * @param event {@link ModifyItemFieldEvent}.
		 */
		public void onModifyItemField(ModifyItemFieldEvent event);

	}

	/**
	 * Diese Methode setzt den Wert des gegebenen {@link Field}s am gegebenen {@link Item} und benachrichtigt bei einer Änderung den gegebenen
	 * {@link ModifyItemFieldListener}. Eine Änderung liegt dann vor, denn der über {@link Field#get(Object)} ermittelte, aktuelle Wert nicht
	 * {@link Objects#equals(Object, Object) äquivalent} dem gegebenen Wert ist. In diesem Fall wird mit dem gegebenen {@link Converter} eine Kopie des aktuellen
	 * Werts erzeugt, der neue Wert über das {@link Field} gesetzt und ein {@link ModifyItemFieldEvent} mit dem kopierten als alten und dem gegebenen als neuen
	 * Wert ausgelöst.
	 * 
	 * @param item {@link Item}.
	 * @param field {@link Field}.
	 * @param value neuer Wert.
	 * @param copier {@link Converter} zum Kopieren des aktuellen Werts.
	 * @param listener {@link ModifyItemFieldListener}.
	 * @param <GItem> Typ des {@link Item}s.
	 * @param <GValue> Typ des Werts.
	 * @throws NullPointerException Wenn bis auf den gegebenen Wert eine der Eingaben {@code null} ist.
	 */
	public static <GItem extends Item, GValue> void modifyField(final GItem item, final Field<? super GItem, GValue> field, final GValue value,
		final Converter<? super GValue, ? extends GValue> copier, final ModifyItemFieldListener listener) throws NullPointerException {
		final GValue oldValue = field.get(item);
		if(Objects.equals(oldValue, value)) return;
		final GValue copyValue = copier.convert(oldValue);
		field.set(item, value);
		listener.onModifyItemField(new ModifyItemFieldEvent(item, field, copyValue, value));
	}

	/**
	 * Diese Methode überführt das gegebene {@link Item} in den gegebenen Status und benachrichtigt bei einer Änderung den gegebenen
	 * {@link ModifyItemStateListener}.
	 * 
	 * @see Item#append()
	 * @see Item#remove()
	 * @see Item#update()
	 * @see ModifyItemStateListener
	 * @param item {@link Item}.
	 * @param state Status ({@link Item#APPEND_STATE}, {@link Item#REMOVE_STATE}, {@link Item#UPDATE_STATE}).
	 * @param listener {@link ModifyItemStateListener}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalStateException Wenn der Übergang in den gegebenen Status unzulässig ist.
	 * @throws IllegalArgumentException Wenn der gegebene Status ungültig ist.
	 */
	public static void modifyState(final Item item, final int state, final ModifyItemStateListener listener) throws NullPointerException, IllegalStateException,
		IllegalArgumentException {
		final int oldState = item.state();
		if(oldState == state) return;
		switch(state){
			case Item.APPEND_STATE:
				item.append();
				break;
			case Item.REMOVE_STATE:
				item.remove();
				break;
			case Item.UPDATE_STATE:
				item.update();
				break;
			default:
				throw new IllegalArgumentException();
		}
		listener.onModifyItemState(new ModifyItemStateEvent(item, oldState, state));
	}

	/**
	 * Diese Methode leert den gegebenen Zielpool und fügt anschließend alle Kopien zu allen Datensätzen des gegebenen Quellpools in den Zielpool ein. Die
	 * Implementation entspricht:
	 * 
	 * <pre>
	 * target.items().clear();
	 * for(GItem item: source)assigner.set(item, target.create());
	 * for(GItem item: source)assigner.assign(item, assigner.get(item));
	 * </pre>
	 * 
	 * @see Assignment
	 * @see Pool
	 * @param <GItem> Typ der Datensätze.
	 * @param assignment {@link Assignment}.
	 * @param source Quellpool.
	 * @param target Zielpool.
	 * @return Liste der Kopien.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GItem extends Item> List<GItem> assignItems(final Assignment<? extends Item> assignment, final Pool<GItem> source, final Pool<GItem> target)
		throws NullPointerException {
		if(assignment == null) throw new NullPointerException();
		target.items().clear();
		final List<GItem> result = new ArrayList<GItem>(source.size());
		for(final GItem item: source){
			final GItem item2 = target.create();
			assignment.set(item, item2);
			result.add(item2);
		}
		for(final GItem item: source){
			assignment.assign(item, assignment.get(item));
		}
		return result;
	}

	/**
	 * Diese Methode leert die gegebenen Zielabbildung und fügt anschließend alle via {@link Assignment#get(Object)} zu den Schlüsseln und Werten der Einträge der
	 * gegebenen Quellabbildung ermittelten Schlüssel-Wert-Paare in die Zielabbildung ein. Die Implementation entspricht:
	 * 
	 * <pre>
	 * target.clear();
	 * for(Entry<GKey, GValue> entry: source.entrySet())target.put(assigner.get(entry.getKey()), assigner.get(entry.getValue()));
	 * </pre>
	 * 
	 * @see Assignment
	 * @see Map
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param assignment {@link Assignment}.
	 * @param source Quellabbildung.
	 * @param target Zielabbildung.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GKey, GValue> void assignEntries(final Assignment<?> assignment, final Map<GKey, GValue> source, final Map<GKey, GValue> target)
		throws NullPointerException {
		if(assignment == null) throw new NullPointerException();
		target.clear();
		for(final Entry<GKey, GValue> entry: source.entrySet()){
			target.put(assignment.get(entry.getKey()), assignment.get(entry.getValue()));
		}
	}

	/**
	 * Diese Methode leert die gegebenen Zielsammlung und fügt anschließend alle via {@link Assignment#get(Object)} zu den Elemente der gegebenen Quellsammlung
	 * ermittelten Zielobjekte in die Zielsammlung ein. Die Implementation entspricht:
	 * 
	 * <pre>
	 * target.clear();
	 * for(GValue value: source)target.add(assigner.get(value));
	 * </pre>
	 * 
	 * @see Assignment
	 * @see Collection
	 * @param <GValue> Typ der Elemente.
	 * @param assignment {@link Assignment}.
	 * @param source Quellsammlung.
	 * @param target Zielsammlung.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> void assignValues(final Assignment<?> assignment, final Collection<GValue> source, final Collection<GValue> target)
		throws NullPointerException {
		if(assignment == null) throw new NullPointerException();
		target.clear();
		for(final GValue value: source){
			target.add(assignment.get(value));
		}
	}

}
