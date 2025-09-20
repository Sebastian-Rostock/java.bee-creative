package bee.creative.util;

import java.util.Map;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Field}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	/** Diese Methode ist eine Abkürzung für {@link CompositeField new CompositeField<>(get, set)}. */
	public static <GItem, VALUE> Field2<GItem, VALUE> fieldFrom(final Getter<? super GItem, ? extends VALUE> get, final Setter<? super GItem, ? super VALUE> set)
		throws NullPointerException {
		return new CompositeField<>(get, set);
	}

	/** Diese Methode liefert das {@link EmptyField}. */
	@SuppressWarnings ("unchecked")
	public static <GItem, VALUE> Field2<GItem, VALUE> emptyField() {
		return (Field2<GItem, VALUE>)EmptyField.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.concat(trans, that), Setters.translate(trans, that))}.
	 *
	 * @see Getters#concatGetter(Getter, Getter)
	 * @see Setters#translatedSetter(Setter, Getter) */
	public static <GSource, GTarget, VALUE> Field2<GSource, VALUE> concatField(final Getter<? super GSource, ? extends GTarget> trans,
		final Field<? super GTarget, VALUE> that) throws NullPointerException {
		return Fields.fieldFrom(Getters.concatGetter(trans, that), Setters.concatSetter(that, trans));
	}

	/** Diese Methode liefert einen {@link Field2} zu {@link Property#get()} und {@link Property#set(Object)} des gegebenen {@link Property}. */
	public static <VALUE> Field2<Object, VALUE> from(final Property<VALUE> target) throws NullPointerException {
		return new PropertyField<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Fields.empty(), that)}.
	 *
	 * @see #emptyField() */
	public static <GItem, VALUE> Field2<GItem, VALUE> fieldFrom(final Setter<? super GItem, ? super VALUE> that) throws NullPointerException {
		return Fields.fieldFrom(Fields.<GItem, VALUE>emptyField(), that);
	}

	/** Diese Methode liefert ein {@link Field} zu {@link Map#get(Object)} und {@link Map#put(Object, Object)} der gegebenen {@link Map}. */
	public static <GItem, VALUE> Field2<GItem, VALUE> fromMap(final Map<GItem, VALUE> that) throws NullPointerException {
		return new MapField<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SetupField new SetupField<>(that, setup)}. */
	public static <GItem, VALUE> Field2<GItem, VALUE> setupField(final Field<? super GItem, VALUE> that, final Getter<? super GItem, ? extends VALUE> setup)
		throws NullPointerException {
		return new SetupField<>(that, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableField new ObservableField<>(target)}. */
	public static <GItem, VALUE> ObservableField<GItem, VALUE> observeField(final Field<? super GItem, VALUE> that) throws NullPointerException {
		return new ObservableField<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.concat(that, getTrans), Setters.translate(that, setTrans))}.
	 *
	 * @see Getters#concatGetter(Getter, Getter)
	 * @see Setters#translatedSetter(Setter, Getter) */
	public static <GItem, GSource, GTarget> Field2<GItem, GTarget> translateField(final Field<? super GItem, GSource> that,
		final Getter<? super GSource, ? extends GTarget> getTrans, final Getter<? super GTarget, ? extends GSource> setTrans) throws NullPointerException {
		return Fields.fieldFrom(Getters.concatGetter(that, getTrans), Setters.translatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #translateField(Field, Getter, Getter) translateField(that, trans::toTarget, trans::toSource)}.
	 *
	 * @see Translator#toTarget(Object)
	 * @see Translator#toSource(Object) */
	public static <GItem, GSource, GTarget> Field2<GItem, GTarget> translateField(final Field<? super GItem, GSource> that,
		final Translator<GSource, GTarget> trans) throws NullPointerException {
		return translateField(that, trans::toTarget, trans::toSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.aggregate(that, getTrans), Setters.aggregate(that, setTrans))}.
	 *
	 * @see Getters#aggregatedGetter(Getter, Getter)
	 * @see Setters#aggregatedSetter(Setter, Getter) */
	public static <GEntry, GSource, GTarget> Field2<Iterable<? extends GEntry>, GTarget> aggregateField(final Field<? super GEntry, GSource> that,
		final Getter<? super GSource, ? extends GTarget> getTrans, final Getter<? super GTarget, ? extends GSource> setTrans) throws NullPointerException {
		return Fields.<Iterable<? extends GEntry>, GTarget>fieldFrom(Getters.aggregatedGetter(that, getTrans), Setters.aggregatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.aggregate(that, getTrans, empty, mixed),
	 * Setters.aggregate(that, setTrans))}.
	 *
	 * @see Getters#aggregatedGetter(Getter, Getter, Getter, Getter)
	 * @see Setters#aggregatedSetter(Setter, Getter) */
	public static <GItem extends Iterable<? extends GItem2>, VALUE, GItem2, VALUE2> Field2<GItem, VALUE> aggregatedField(final Field<? super GItem2, VALUE2> that,
		final Getter<? super VALUE2, ? extends VALUE> getTrans, final Getter<? super VALUE, ? extends VALUE2> setTrans,
		final Getter<? super GItem, ? extends VALUE> empty, final Getter<? super GItem, ? extends VALUE> mixed) throws NullPointerException {
		return Fields.fieldFrom(Getters.<GItem, VALUE, GItem2, VALUE2>aggregatedGetter(that, getTrans, empty, mixed), Setters.aggregatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.optionalize(that, value), Setters.optionalize(that))}.
	 *
	 * @see Getters#optionalizedGetter(Getter, Object)
	 * @see Setters#optionalizedSetter(Setter) */
	public static <GItem, VALUE> Field2<GItem, VALUE> optionalizedField(final Field<? super GItem, VALUE> that, final VALUE value) throws NullPointerException {
		return Fields.fieldFrom(Getters.optionalizedGetter(that, value), Setters.optionalizedSetter(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedField new SynchronizedField<>(that, mutex)}. */
	public static <GItem, VALUE> Field2<GItem, VALUE> synchronizedField(final Field<? super GItem, VALUE> that, final Object mutex) throws NullPointerException {
		return new SynchronizedField<>(that, mutex);
	}

	/** Diese Klasse implementiert ein {@link Field2}, das beim {@link #get(Object) Lesen} stets {@code null} liefert und das {@link #set(Object, Object)
	 * Schreiben} ignoriert. */
	public static class EmptyField extends AbstractField<Object, Object> {

		public static final Field2<?, ?> INSTANCE = new EmptyField();

	}

	/** Diese Klasse implementiert ein initialisierendes {@link Field2}, das das {@link #set(Object, Object) Schreiben} an ein gegebenes {@link Field} delegiert
	 * und beim {@link #get(Object) Lesen} den Wert des gegebenen {@link Field} nur dann liefert, wenn dieser nicht {@code null} ist. Andernfalls wird der über
	 * einen gegebenen {@link Getter} ermittelte Wert geliefert und zuvor über das gegebene {@link Field} geschrieben.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts. */

	public static class SetupField<GItem, VALUE> extends AbstractField<GItem, VALUE> {

		public final Field<? super GItem, VALUE> that;

		public final Getter<? super GItem, ? extends VALUE> setup;

		public SetupField(final Field<? super GItem, VALUE> that, final Getter<? super GItem, ? extends VALUE> setup) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public VALUE get(final GItem item) {
			VALUE result = this.that.get(item);
			if (result != null) return result;
			result = this.setup.get(item);
			this.that.set(item, result);
			return result;
		}

		@Override
		public void set(final GItem item, final VALUE value) {
			this.that.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.setup);
		}

	}

	/** Diese Klasse implementiert ein zusammengesetztes {@link Field2}, das das {@link #get(Object) Lesen} an einen gegebenen {@link Getter} und das
	 * {@link #set(Object, Object) Schreiben} an einen gegebenen {@link Setter} delegiert.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */

	public static class CompositeField<GItem, VALUE> extends AbstractField<GItem, VALUE> {

		public final Getter<? super GItem, ? extends VALUE> get;

		public final Setter<? super GItem, ? super VALUE> set;

		public CompositeField(final Getter<? super GItem, ? extends VALUE> get, final Setter<? super GItem, ? super VALUE> set) {
			this.get = Objects.notNull(get);
			this.set = Objects.notNull(set);
		}

		@Override
		public VALUE get(final GItem item) {
			return this.get.get(item);
		}

		@Override
		public void set(final GItem item, final VALUE value) {
			this.set.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get, this.set);
		}

	}

	/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class ObservableField<GItem, VALUE> extends AbstractField<GItem, VALUE> implements Observable<UpdateFieldEvent, UpdateFieldListener> {

		/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
		public final Field<? super GItem, VALUE> that;

		/** Dieser Konstruktor initialisiert das überwachte Datenfeld. */
		public ObservableField(final Field<? super GItem, VALUE> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public VALUE get(final GItem input) {
			return this.that.get(input);
		}

		@Override
		public void set(final GItem item, final VALUE newValue) {
			VALUE oldValue = this.that.get(item);
			if (this.customEquals(oldValue, newValue)) return;
			oldValue = this.customClone(oldValue);
			this.that.set(item, newValue);
			this.fire(new UpdateFieldEvent(this, item, oldValue, newValue));
		}

		@Override
		public UpdateFieldListener put(final UpdateFieldListener listener) throws IllegalArgumentException {
			return UpdateFieldObservables.INSTANCE.put(this, listener);
		}

		@Override
		public UpdateFieldListener putWeak(final UpdateFieldListener listener) throws IllegalArgumentException {
			return UpdateFieldObservables.INSTANCE.putWeak(this, listener);
		}

		@Override
		public void pop(final UpdateFieldListener listener) throws IllegalArgumentException {
			UpdateFieldObservables.INSTANCE.pop(this, listener);
		}

		@Override
		public UpdateFieldEvent fire(final UpdateFieldEvent event) throws NullPointerException {
			return UpdateFieldObservables.INSTANCE.fire(this, event);
		}

		@Override
		public String toString() {
			return this.that.toString();
		}

		/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese
		 * Methode eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann
		 * nötig, wenn der alte Wert sich durch das Schreiben des neuen ändert.
		 *
		 * @param value alter Wert.
		 * @return gegebener oder kopierter Wert. */
		protected VALUE customClone(final VALUE value) {
			return value;
		}

		/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
		 * Wertänderung eingesetzt.
		 *
		 * @param value1 alter Wert.
		 * @param value2 neuer Wert.
		 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
		protected boolean customEquals(final VALUE value1, final VALUE value2) {
			return Objects.deepEquals(value1, value2);
		}

	}

	/** Diese Klasse implementiert ein {@link Field2}, das einen gegebenes {@link Field} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class SynchronizedField<GItem, VALUE> extends AbstractField<GItem, VALUE> {

		public final Field<? super GItem, VALUE> that;

		public final Object mutex;

		public SynchronizedField(final Field<? super GItem, VALUE> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public VALUE get(final GItem item) {
			synchronized (this.mutex) {
				return this.that.get(item);
			}
		}

		@Override
		public void set(final GItem item, final VALUE value) {
			synchronized (this.mutex) {
				this.that.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	static class MapField<GItem, VALUE> extends AbstractField<GItem, VALUE> {

		public final Map<GItem, VALUE> that;

		public MapField(final Map<GItem, VALUE> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public VALUE get(final GItem item) {
			return this.that.get(item);
		}

		@Override
		public void set(final GItem item, final VALUE value) {
			this.that.put(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode gibt ein {@link Field} zurück, das seinen Datensatz ignoriert und den Wert des gegebenen {@link Property} manipuliert. */

	static class PropertyField<VALUE> extends AbstractField<Object, VALUE> {

		public final Property<VALUE> that;

		public PropertyField(final Property<VALUE> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public VALUE get(final Object input) {
			return this.that.get();
		}

		@Override
		public void set(final Object input, final VALUE value) {
			this.that.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

}