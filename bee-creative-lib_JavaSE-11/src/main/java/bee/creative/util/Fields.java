package bee.creative.util;

import java.lang.reflect.Method;
import java.util.Map;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Field}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	/** Diese Klasse implementiert ein {@link Field2}, das beim {@link #get(Object) Lesen} stets {@code null} liefert und das {@link #set(Object, Object)
	 * Schreiben} ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyField extends AbstractField<Object, Object> {

		public static final Field2<?, ?> INSTANCE = new EmptyField();

	}

	/** Diese Klasse implementiert {@link Field2}, das das {@link #get(Object) Lesen} und {@link #set(Object, Object) Schreiben} an ein gegebenes
	 * {@link java.lang.reflect.Field natives Datenfeld} delegiert. Bei einem statischen nativen Datenfeld wird der Datensatz ignoriert.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class NativeField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final java.lang.reflect.Field that;

		public NativeField(final java.lang.reflect.Field target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.that = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				return (GValue)this.that.get(item);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				this.that.set(item, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.that.isAccessible());
		}

	}

	/** Diese Klasse implementiert ein initialisierendes {@link Field2}, das das {@link #set(Object, Object) Schreiben} an ein gegebenes {@link Field} delegiert
	 * und beim {@link #get(Object) Lesen} den Wert des gegebenen {@link Field} nur dann liefert, wenn dieser nicht {@code null} ist. Andernfalls wird der über
	 * einen gegebenen {@link Getter} ermittelte Wert geliefert und zuvor über das gegebene {@link Field} geschrieben.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class SetupField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Field<? super GItem, GValue> that;

		public final Getter<? super GItem, ? extends GValue> setup;

		public SetupField(final Field<? super GItem, GValue> that, final Getter<? super GItem, ? extends GValue> setup) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public GValue get(final GItem item) {
			GValue result = this.that.get(item);
			if (result != null) return result;
			result = this.setup.get(item);
			this.that.set(item, result);
			return result;
		}

		@Override
		public void set(final GItem item, final GValue value) {
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
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class CompositeField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> get;

		public final Setter<? super GItem, ? super GValue> set;

		public CompositeField(final Getter<? super GItem, ? extends GValue> get, final Setter<? super GItem, ? super GValue> set) {
			this.get = Objects.notNull(get);
			this.set = Objects.notNull(set);
		}

		@Override
		public GValue get(final GItem item) {
			return this.get.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
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
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class ObservableField<GItem, GValue> extends AbstractField<GItem, GValue> implements Observable<UpdateFieldEvent, UpdateFieldListener> {

		/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
		public final Field<? super GItem, GValue> that;

		/** Dieser Konstruktor initialisiert das überwachte Datenfeld. */
		public ObservableField(final Field<? super GItem, GValue> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public GValue get(final GItem input) {
			return this.that.get(input);
		}

		@Override
		public void set(final GItem item, final GValue newValue) {
			GValue oldValue = this.that.get(item);
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
		protected GValue customClone(final GValue value) {
			return value;
		}

		/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
		 * Wertänderung eingesetzt.
		 *
		 * @param value1 alter Wert.
		 * @param value2 neuer Wert.
		 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
		protected boolean customEquals(final GValue value1, final GValue value2) {
			return Objects.deepEquals(value1, value2);
		}

	}

	/** Diese Klasse implementiert ein {@link Field2}, das einen gegebenes {@link Field} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Field<? super GItem, GValue> that;

		public final Object mutex;

		public SynchronizedField(final Field<? super GItem, GValue> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.that.get(item);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			synchronized (this.mutex) {
				this.that.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	static class MapField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Map<GItem, GValue> that;

		public MapField(final Map<GItem, GValue> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public GValue get(final GItem item) {
			return this.that.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.that.put(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode gibt ein {@link Field} zurück, das seinen Datensatz ignoriert und den Wert des gegebenen {@link Property} manipuliert. */

	static class PropertyField<GValue> extends AbstractField<Object, GValue> {

		public final Property<GValue> that;

		public PropertyField(final Property<GValue> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public GValue get(final Object input) {
			return this.that.get();
		}

		@Override
		public void set(final Object input, final GValue value) {
			this.that.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode liefert das {@link EmptyField}. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Field2<GItem, GValue> empty() {
		return (Field2<GItem, GValue>)EmptyField.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.concat(trans, that), Setters.translate(trans, that))}.
	 *
	 * @see Getters#concat(Getter, Getter)
	 * @see Setters#translate(Getter, Setter) */
	public static <GSource, GTarget, GValue> Field2<GSource, GValue> translate(final Getter<? super GSource, ? extends GTarget> trans,
		final Field<? super GTarget, GValue> that) throws NullPointerException {
		return Fields.from(Getters.concat(trans, that), Setters.translate(trans, that));
	}

	/** Diese Methode liefert einen {@link Field2} zu {@link Property#get()} und {@link Property#set(Object)} des gegebenen {@link Property}. */
	public static <GValue> Field2<Object, GValue> from(final Property<GValue> target) throws NullPointerException {
		return new PropertyField<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Fields.empty(), that)}.
	 *
	 * @see #empty() */
	public static <GItem, GValue> Field2<GItem, GValue> from(final Setter<? super GItem, ? super GValue> that) throws NullPointerException {
		return Fields.from(Fields.<GItem, GValue>empty(), that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(that, Fields.empty())}.
	 *
	 * @see #empty() */
	public static <GItem, GValue> Field2<GItem, GValue> from(final Getter<? super GItem, ? extends GValue> that) throws NullPointerException {
		return Fields.from(that, Fields.<GItem, GValue>empty());
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeField new CompositeField<>(get, set)}. */
	public static <GItem, GValue> Field2<GItem, GValue> from(final Getter<? super GItem, ? extends GValue> get, final Setter<? super GItem, ? super GValue> set)
		throws NullPointerException {
		return new CompositeField<>(get, set);
	}

	/** Diese Methode liefert ein {@link Field} zu {@link Map#get(Object)} und {@link Map#put(Object, Object)} der gegebenen {@link Map}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromMap(final Map<GItem, GValue> that) throws NullPointerException {
		return new MapField<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(java.lang.reflect.Field, boolean) Fields.fromNative(that, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link NativeField new NativeField<>(that, forceAccessible)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final java.lang.reflect.Field that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeField<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Method, Method, boolean) Fields.fromNative(getMethod, setMethod, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Method get, final Method set) throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(get, set, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.fromNative(get, forceAccessible), Setters.fromNative(set,
	 * forceAccessible))}.
	 *
	 * @see Getters#fromNative(Method, boolean)
	 * @see Setters#fromNative(Method, boolean) */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Method get, final Method set, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.from(Getters.<GItem, GValue>fromNative(get, forceAccessible), Setters.<GItem, GValue>fromNative(set, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Class, String, boolean) Fields.fromNative(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Fields.fromNative(Natives.parseField(fieldOwner, fieldName),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link SetupField new SetupField<>(that, setup)}. */
	public static <GItem, GValue> Field2<GItem, GValue> setup(final Field<? super GItem, GValue> that, final Getter<? super GItem, ? extends GValue> setup)
		throws NullPointerException {
		return new SetupField<>(that, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableField new ObservableField<>(target)}. */
	public static <GItem, GValue> ObservableField<GItem, GValue> observe(final Field<? super GItem, GValue> that) throws NullPointerException {
		return new ObservableField<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.concat(that, getTrans), Setters.translate(that, setTrans))}.
	 *
	 * @see Getters#concat(Getter, Getter)
	 * @see Setters#translate(Setter, Getter) */
	public static <GItem, GSource, GTarget> Field2<GItem, GTarget> translate(final Field<? super GItem, GSource> that,
		final Getter<? super GSource, ? extends GTarget> getTrans, final Getter<? super GTarget, ? extends GSource> setTrans) throws NullPointerException {
		return Fields.from(Getters.concat(that, getTrans), Setters.translate(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #translate(Field, Getter, Getter) Fields.translate(that, Getters.fromTarget(trans),
	 * Getters.fromSource(trans))}.
	 *
	 * @see Getters#fromTarget(Translator)
	 * @see Getters#fromSource(Translator) */
	public static <GItem, GSource, GTarget> Field2<GItem, GTarget> translate(final Field<? super GItem, GSource> that, final Translator<GSource, GTarget> trans)
		throws NullPointerException {
		return Fields.translate(that, Getters.fromTarget(trans), Getters.fromSource(trans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.aggregate(that), Setters.aggregate(that))}.
	 *
	 * @see Getters#aggregate(Getter)
	 * @see Setters#aggregate(Setter) */
	public static <GItem, GValue> Field2<Iterable<? extends GItem>, GValue> aggregate(final Field<? super GItem, GValue> that) throws NullPointerException {
		return Fields.<Iterable<? extends GItem>, GValue>from(Getters.aggregate(that), Setters.aggregate(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.aggregate(that, getTrans), Setters.aggregate(that, setTrans))}.
	 *
	 * @see Getters#aggregate(Getter, Getter)
	 * @see Setters#aggregate(Setter, Getter) */
	public static <GEntry, GSource, GTarget> Field2<Iterable<? extends GEntry>, GTarget> aggregate(final Field<? super GEntry, GSource> that,
		final Getter<? super GSource, ? extends GTarget> getTrans, final Getter<? super GTarget, ? extends GSource> setTrans) throws NullPointerException {
		return Fields.<Iterable<? extends GEntry>, GTarget>from(Getters.aggregate(that, getTrans), Setters.aggregate(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.aggregate(that, getTrans, empty, mixed), Setters.aggregate(that,
	 * setTrans))}.
	 *
	 * @see Getters#aggregate(Getter, Getter, Getter, Getter)
	 * @see Setters#aggregate(Setter, Getter) */
	public static <GItem extends Iterable<? extends GItem2>, GValue, GItem2, GValue2> Field2<GItem, GValue> aggregate(final Field<? super GItem2, GValue2> that,
		final Getter<? super GValue2, ? extends GValue> getTrans, final Getter<? super GValue, ? extends GValue2> setTrans,
		final Getter<? super GItem, ? extends GValue> empty, final Getter<? super GItem, ? extends GValue> mixed) throws NullPointerException {
		return Fields.from(Getters.<GItem, GValue, GItem2, GValue2>aggregate(that, getTrans, empty, mixed), Setters.aggregate(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.optionalize(that), Setters.optionalize(that))}.
	 *
	 * @see Getters#optionalize(Getter)
	 * @see Setters#optionalize(Setter) */
	public static <GItem, GValue> Field2<GItem, GValue> optionalize(final Field<? super GItem, GValue> that) throws NullPointerException {
		return Fields.from(Getters.optionalize(that), Setters.optionalize(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Getters.optionalize(that, value), Setters.optionalize(that))}.
	 *
	 * @see Getters#optionalize(Getter, Object)
	 * @see Setters#optionalize(Setter) */
	public static <GItem, GValue> Field2<GItem, GValue> optionalize(final Field<? super GItem, GValue> that, final GValue value) throws NullPointerException {
		return Fields.from(Getters.optionalize(that, value), Setters.optionalize(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Field, Object) Fields.synchronize(that, that)}. */
	public static <GItem, GValue> Field2<GItem, GValue> synchronize(final Field<? super GItem, GValue> that) throws NullPointerException {
		return Fields.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedField new SynchronizedField<>(that, mutex)}. */
	public static <GItem, GValue> Field2<GItem, GValue> synchronize(final Field<? super GItem, GValue> that, final Object mutex) throws NullPointerException {
		return new SynchronizedField<>(that, mutex);
	}

}