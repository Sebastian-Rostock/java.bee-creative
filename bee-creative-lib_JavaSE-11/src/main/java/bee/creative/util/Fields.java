package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.concatGetter;
import static bee.creative.util.Setters.concatSetter;
import static bee.creative.util.Setters.setterFromConsumer;
import java.util.Map;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Field}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	/** Diese Methode liefert das gegebene {@link Field} als {@link Field3}. */
	public static <T, V> Field3<T, V> fieldFrom(Field<T, V> that) {
		notNull(that);
		if (that instanceof Field3<?, ?>) return (Field3<T, V>)that;
		return fieldFrom(item -> that.get(item), (item, value) -> that.set(item, value));
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeField new CompositeField<>(get, set)}. */
	public static <ITEM, VALUE> Field3<ITEM, VALUE> fieldFrom(Getter<? super ITEM, ? extends VALUE> get, Setter<? super ITEM, ? super VALUE> set)
		throws NullPointerException {
		return new CompositeField<>(get, set);
	}

	/** Diese Methode liefert einen {@link Field3} zu {@link Property#get()} und {@link Property#set(Object)} und ist eine Abkürzung für
	 * {@link #fieldFrom(Getter, Setter) fieldFrom(getterFrom(that), setterFrom(that))}.
	 *
	 * @see Getters#getterFromProducer(Producer)
	 * @see Setters#setterFromConsumer(Consumer) */
	public static <VALUE> Field3<Object, VALUE> fieldFromProperty(Property<VALUE> that) {
		return fieldFrom(Getters.getterFromProducer(that), setterFromConsumer(that));
	}

	/** Diese Methode liefert ein {@link Field3} zu {@link Map#get(Object)} und {@link Map#put(Object, Object)} und ist eine Abkürzung für
	 * {@link #fieldFrom(Getter, Setter) fieldFrom(that::get, that::put)}. */
	public static <ITEM, VALUE> Field3<ITEM, VALUE> fieldFromMap(Map<ITEM, VALUE> that) throws NullPointerException {
		return fieldFrom(that::get, that::put);
	}

	/** Diese Methode liefert das {@link EmptyField}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Field3<ITEM, VALUE> emptyField() {
		return (Field3<ITEM, VALUE>)EmptyField;
	}

	/** Diese Methode ist eine Abkürzung für {@link SetupField new SetupField<>(that, setup)}. */
	public static <ITEM, VALUE> Field3<ITEM, VALUE> setupField(final Field<? super ITEM, VALUE> that, final Getter<? super ITEM, ? extends VALUE> setup)
		throws NullPointerException {
		return new SetupField<>(that, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) fieldFrom(concatGetter(trans, that), concatSetter(trans, that))}.
	 *
	 * @see Getters#concatGetter(Getter, Getter)
	 * @see Setters#concatSetter(Getter, Setter) */
	public static <ITEM, ITEM2, VALUE> Field3<ITEM, VALUE> concatField(Getter<? super ITEM, ? extends ITEM2> trans, Field<? super ITEM2, VALUE> that)
		throws NullPointerException {
		return fieldFrom(concatGetter(trans, that), concatSetter(trans, that));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableField new ObservableField<>(target)}. */
	public static <ITEM, VALUE> ObservableField<ITEM, VALUE> observeField(final Field<? super ITEM, VALUE> that) throws NullPointerException {
		return new ObservableField<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.concat(that, getTrans), Setters.translate(that, setTrans))}.
	 *
	 * @see Getters#concatGetter(Getter, Getter)
	 * @see Setters#translatedSetter(Setter, Getter) */
	public static <ITEM, GSource, GTarget> Field3<ITEM, GTarget> translateField(final Field<? super ITEM, GSource> that,
		final Getter<? super GSource, ? extends GTarget> getTrans, final Getter<? super GTarget, ? extends GSource> setTrans) throws NullPointerException {
		return Fields.fieldFrom(concatGetter(that, getTrans), Setters.translatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #translateField(Field, Getter, Getter) translateField(that, trans::toTarget, trans::toSource)}.
	 *
	 * @see Translator#toTarget(Object)
	 * @see Translator#toSource(Object) */
	public static <ITEM, GSource, GTarget> Field3<ITEM, GTarget> translateField(final Field<? super ITEM, GSource> that, final Translator<GSource, GTarget> trans)
		throws NullPointerException {
		return translateField(that, trans::toTarget, trans::toSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.aggregate(that, getTrans, empty, mixed),
	 * Setters.aggregate(that, setTrans))}.
	 *
	 * @see Getters#aggregatedGetter(Getter, Getter, Getter, Getter)
	 * @see Setters#aggregatedSetter(Setter, Getter) */
	public static <ITEM extends Iterable<? extends ITEM2>, VALUE, ITEM2, VALUE2> Field3<ITEM, VALUE> aggregatedField(final Field<? super ITEM2, VALUE2> that,
		final Getter<? super VALUE2, ? extends VALUE> getTrans, final Getter<? super VALUE, ? extends VALUE2> setTrans,
		final Getter<? super ITEM, ? extends VALUE> empty, final Getter<? super ITEM, ? extends VALUE> mixed) throws NullPointerException {
		return Fields.fieldFrom(Getters.<ITEM, VALUE, ITEM2, VALUE2>aggregatedGetter(that, getTrans, empty, mixed), Setters.aggregatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.optionalize(that, value), Setters.optionalize(that))}.
	 *
	 * @see Getters#optionalizedGetter(Getter, Object)
	 * @see Setters#optionalizedSetter(Setter) */
	public static <ITEM, VALUE> Field3<ITEM, VALUE> optionalizedField(final Field<? super ITEM, VALUE> that, final VALUE value) throws NullPointerException {
		return Fields.fieldFrom(Getters.optionalizedGetter(that, value), Setters.optionalizedSetter(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedField new SynchronizedField<>(that, mutex)}. */
	public static <ITEM, VALUE> Field3<ITEM, VALUE> synchronizedField(final Field<? super ITEM, VALUE> that, final Object mutex) throws NullPointerException {
		return new SynchronizedField<>(that, mutex);
	}

	public static final Field3<?, ?> EmptyField = new EmptyField();

	/** Diese Klasse implementiert ein {@link Field3}, das beim {@link #get(Object) Lesen} stets {@code null} liefert und das {@link #set(Object, Object)
	 * Schreiben} ignoriert. */
	public static class EmptyField extends AbstractField<Object, Object> {

	}

	/** Diese Klasse implementiert ein initialisierendes {@link Field3}, das das {@link #set(Object, Object) Schreiben} an ein gegebenes {@link Field} delegiert
	 * und beim {@link #get(Object) Lesen} den Wert des gegebenen {@link Field} nur dann liefert, wenn dieser nicht {@code null} ist. Andernfalls wird der über
	 * einen gegebenen {@link Getter} ermittelte Wert geliefert und zuvor über das gegebene {@link Field} geschrieben.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts. */

	public static class SetupField<ITEM, VALUE> extends AbstractField<ITEM, VALUE> {

		public final Field<? super ITEM, VALUE> that;

		public final Getter<? super ITEM, ? extends VALUE> setup;

		public SetupField(final Field<? super ITEM, VALUE> that, final Getter<? super ITEM, ? extends VALUE> setup) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public VALUE get(final ITEM item) {
			var result = this.that.get(item);
			if (result != null) return result;
			result = this.setup.get(item);
			this.that.set(item, result);
			return result;
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			this.that.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.setup);
		}

	}

	/** Diese Klasse implementiert ein zusammengesetztes {@link Field3}, das das {@link #get(Object) Lesen} an einen gegebenen {@link Getter} und das
	 * {@link #set(Object, Object) Schreiben} an einen gegebenen {@link Setter} delegiert.
	 *
	 * @param <ITEM> Typ der Eingabe.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */

	public static class CompositeField<ITEM, VALUE> extends AbstractField<ITEM, VALUE> {

		public final Getter<? super ITEM, ? extends VALUE> get;

		public final Setter<? super ITEM, ? super VALUE> set;

		public CompositeField(final Getter<? super ITEM, ? extends VALUE> get, final Setter<? super ITEM, ? super VALUE> set) {
			this.get = Objects.notNull(get);
			this.set = Objects.notNull(set);
		}

		@Override
		public VALUE get(final ITEM item) {
			return this.get.get(item);
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			this.set.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get, this.set);
		}

	}

	/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
	 *
	 * @param <ITEM> Typ der Eingabe.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class ObservableField<ITEM, VALUE> extends AbstractField<ITEM, VALUE> implements Observable<UpdateFieldEvent, UpdateFieldListener> {

		/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
		public final Field<? super ITEM, VALUE> that;

		/** Dieser Konstruktor initialisiert das überwachte Datenfeld. */
		public ObservableField(final Field<? super ITEM, VALUE> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public VALUE get(final ITEM input) {
			return this.that.get(input);
		}

		@Override
		public void set(final ITEM item, final VALUE newValue) {
			var oldValue = this.that.get(item);
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

	/** Diese Klasse implementiert ein {@link Field3}, das einen gegebenes {@link Field} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class SynchronizedField<ITEM, VALUE> extends AbstractField<ITEM, VALUE> {

		public final Field<? super ITEM, VALUE> that;

		public final Object mutex;

		public SynchronizedField(final Field<? super ITEM, VALUE> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public VALUE get(final ITEM item) {
			synchronized (this.mutex) {
				return this.that.get(item);
			}
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			synchronized (this.mutex) {
				this.that.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

}