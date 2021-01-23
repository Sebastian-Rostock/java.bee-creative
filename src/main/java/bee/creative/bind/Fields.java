package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Field}-Instanzen.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	public static class EmptyField extends AbstractField<Object, Object> {

		public static final Field2<?, ?> INSTANCE = new EmptyField();

	}

	public static class NativeField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final java.lang.reflect.Field target;

		public NativeField(final java.lang.reflect.Field target, final boolean forceAccessible) {
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				return (GValue)this.target.get(item);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				this.target.set(item, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, target.isAccessible());
		}

	}

	public static class SetupField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> setup;

		public final Field<? super GItem, GValue> target;

		public SetupField(final Field<? super GItem, GValue> target, final Getter<? super GItem, ? extends GValue> setup) {
			this.target = Objects.notNull(target);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public GValue get(final GItem item) {
			GValue result = this.target.get(item);
			if (result != null) return result;
			result = this.setup.get(item);
			this.target.set(item, result);
			return result;
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.target.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.setup);
		}

	}

	public static class DefaultField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Field<? super GItem, GValue> target;

		public final GValue value;

		public DefaultField(final Field<? super GItem, GValue> target, final GValue value) {
			this.target = Objects.notNull(target);
			this.value = value;
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.value;
			return this.target.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (item == null) return;
			this.target.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.value);
		}

	}

	/** Diese Klasse implementiert ein zusammengesetztes {@link Field2}, welches das {@link #get(Object) Lesen} an einen gegebenen {@link Getter} und das
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

	public static class ConcatField<GItem, GItem2, GValue> extends AbstractField<GItem, GValue> {

		public final Getter<? super GItem, ? extends GItem2> source;

		public final Field<? super GItem2, GValue> target;

		public ConcatField(final Getter<? super GItem, ? extends GItem2> source, final Field<? super GItem2, GValue> target) {
			this.source = Objects.notNull(source);
			this.target = Objects.notNull(target);
		}

		@Override
		public GValue get(final GItem item) {
			return this.target.get(this.source.get(item));
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.target.set(this.source.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.source, this.target);
		}

	}

	/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class ObservableField<GItem, GValue> extends AbstractField<GItem, GValue> implements Observable<UpdateFieldMessage, UpdateFieldObserver> {

		/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
		public final Field<? super GItem, GValue> target;

		/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
		 *
		 * @param target überwachtes Datenfeld. */
		public ObservableField(final Field<? super GItem, GValue> target) {
			this.target = Objects.notNull(target);
		}

		@Override
		public GValue get(final GItem input) {
			return this.target.get(input);
		}

		@Override
		public void set(final GItem item, final GValue newValue) {
			GValue oldValue = this.target.get(item);
			if (this.customEquals(oldValue, newValue)) return;
			oldValue = this.customClone(oldValue);
			this.target.set(item, newValue);
			this.fire(new UpdateFieldMessage(this, item, oldValue, newValue));
		}

		@Override
		public UpdateFieldObserver put(final UpdateFieldObserver listener) throws IllegalArgumentException {
			return UpdateFieldEvent.INSTANCE.put(this, listener);
		}

		@Override
		public UpdateFieldObserver putWeak(final UpdateFieldObserver listener) throws IllegalArgumentException {
			return UpdateFieldEvent.INSTANCE.putWeak(this, listener);
		}

		@Override
		public void pop(final UpdateFieldObserver listener) throws IllegalArgumentException {
			UpdateFieldEvent.INSTANCE.pop(this, listener);
		}

		@Override
		public UpdateFieldMessage fire(final UpdateFieldMessage event) throws NullPointerException {
			return UpdateFieldEvent.INSTANCE.fire(this, event);
		}

		@Override
		public String toString() {
			return this.target.toString();
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

	public static class SynchronizedField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Object mutex;

		public final Field<? super GItem, GValue> target;

		public SynchronizedField(final Field<? super GItem, GValue> target, final Object mutex) {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.target.get(item);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			synchronized (this.mutex) {
				this.target.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
		}

	}

	static class MapField<GItem, GValue> extends AbstractField<GItem, GValue> {

		public final Map<GItem, GValue> mapping;

		public MapField(final Map<GItem, GValue> mapping) {
			this.mapping = Objects.notNull(mapping);
		}

		@Override
		public GValue get(final GItem item) {
			return this.mapping.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.mapping.put(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.mapping);
		}

	}

	static class PropertyField<GValue> extends AbstractField<Object, GValue> {

		public final Property<GValue> property;

		public PropertyField(final Property<GValue> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		public GValue get(final Object input) {
			return this.property.get();
		}

		@Override
		public void set(final Object input, final GValue value) {
			this.property.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.property);
		}

	}

	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Field2<GItem, GValue> empty() {
		return (Field2<GItem, GValue>)EmptyField.INSTANCE;
	}

	/** Diese Methode gibt ein {@link Field} zurück, das seinen Datensatz ignoriert und den Wert des gegebenen {@link Property} manipuliert. */
	public static <GValue> Field2<Object, GValue> from(final Property<GValue> target) throws NullPointerException {
		return new PropertyField<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(Fields.empty(), target)}. */
	public static <GItem, GValue> Field2<GItem, GValue> from(final Setter<? super GItem, ? super GValue> target) throws NullPointerException {
		return from(Fields.<GItem, GValue>empty(), target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.from(target, Fields.empty())}.
	 *
	 * @see Setters#empty() */
	public static <GItem, GValue> Field2<GItem, GValue> from(final Getter<? super GItem, ? extends GValue> target) throws NullPointerException {
		return Fields.from(target, Fields.<GItem, GValue>empty());
	}

	/** Diese Methode gibt ein zusammengesetztes {@link Field} zurück, dessen Methoden an die des gegebenen {@link Getter} und {@link Setter} delegieren.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param get {@link Getter} für {@link Field#get(Object)}.
	 * @param set {@link Setter} für {@link Field#set(Object, Object)}.
	 * @return {@code composite}-{@link Field}.
	 * @throws NullPointerException Wenn {@code getter} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GValue> Field2<GItem, GValue> from(final Getter<? super GItem, ? extends GValue> get, final Setter<? super GItem, ? super GValue> set)
		throws NullPointerException {
		return new CompositeField<>(get, set);
	}

	/** Diese Methode gibt ein {@link Field} zurück, welches beim Lesen am {@link Map#get(Object)} sowie beim Schreiben an {@link Map#put(Object, Object)}
	 * delegiert.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param target {@link Map} zur Abbildung von einer Eingabe auf einen Wert.
	 * @return {@code mapping}-{@link Field}.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
	public static <GItem, GValue> Field2<GItem, GValue> fromMap(final Map<GItem, GValue> target) throws NullPointerException {
		return new MapField<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(java.lang.reflect.Field, boolean) Fields.nativeField(field, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(field, true);
	}

	/** Diese Methode gibt ein {@link Field} zum gegebenen {@link java.lang.reflect.Field nativen Datenfeld} zurück. Für eine Eingabe {@code item} erfolgt das
	 * Lesen des gelieferten {@link Field} über {@code field.get(item)}. Das Schreiben eines Werts {@code value} erfolgt hierbei über
	 * {@code field.set(item, value)}. Bei Klassenfeldern wird die Eingabe ignoriert.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field natives Datenfeld.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Datenfeld nicht zugrifbar ist. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeField<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Method, Method, boolean) Fields.nativeField(getMethod, setMethod, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Method getMethod, final Method setMethod)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(getMethod, setMethod, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.compositeField(Getters.nativeGetter(getMethod, forceAccessible),
	 * Setters.nativeSetter(setMethod, forceAccessible))}.
	 *
	 * @see Getters#fromNative(Method, boolean)
	 * @see Setters#fromNative(Method, boolean) */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Method getMethod, final Method setMethod, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.from(Getters.<GItem, GValue>fromNative(getMethod, forceAccessible), Setters.<GItem, GValue>fromNative(setMethod, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Class, String, boolean) Fields.nativeField(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Fields.nativeField(Natives.parseField(fieldOwner, fieldName),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GItem, GValue> Field2<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #from(Getter, Setter) Fields.from(Getters.concat(source, target), Setters.concat(source,
	 * target))}.
	 *
	 * @see Getters#concat(Getter, Getter)
	 * @see Setters#concat(Getter, Setter) */
	public static <GSource, GTarget, GValue> Field2<GSource, GValue> concat(final Getter<? super GSource, ? extends GTarget> source,
		final Field<? super GTarget, GValue> target) throws NullPointerException {
		return new ConcatField<>(source, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toDefault(Field, Object) Fields.defaultField(null, field)}. */
	public static <GItem, GValue> Field2<GItem, GValue> toDefault(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.toDefault(field, null);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #from(Getter, Setter) Fields.compositeField(Fields.defaultGetter(value, field),
	 * Fields.defaultSetter(field))}.
	 *
	 * @see Getters#toDefault(Getter, Object)
	 * @see Setters#toDefault(Setter) */
	public static <GItem, GValue> Field2<GItem, GValue> toDefault(final Field<? super GItem, GValue> target, final GValue value) throws NullPointerException {
		return new DefaultField<>(target, value);
	}

	/** Diese Methode gibt ein initialisierendes {@link Field} zurück. Das Schreiben wird direkt an das gegebene {@link Field} {@code field} delegiert. Beim Lesen
	 * wird der Wert zuerst über das gegebene {@link Field} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. üner den gegebenen
	 * {@link Getter} {@code setup} ermittelt und über das {@link Field} {@code field} geschrieben.
	 *
	 * @param target Datenfeld zur Manipulation.
	 * @param setup Methode zur Initialisierung.
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @return {@code setup}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} bzw. {@code setup} {@code null} ist. */
	public static <GItem, GValue> Field2<GItem, GValue> toSetup(final Field<? super GItem, GValue> target, final Getter<? super GItem, ? extends GValue> setup)
		throws NullPointerException {
		return new SetupField<>(target, setup);
	}

	/** Diese Methode gibt ein übersetztes {@link Field} zurück. Das erzeugte {@link Field} liefert beim Lesen den Wert, der über den gegebenen {@link Getter}
	 * {@code toTarget} aus dem über das gegebene {@link Field} ermittelten Wert berechnet wird. Beim Schreiben eines Werts wird dieser über dem gegebenen
	 * {@link Getter} {@code toSource} in einen Wert überfüght, welcher anschließend an das gegebene {@link Field} delegiert wird.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GTarget> Typ des Werts des erzeugten {@link Field}.
	 * @param <GSource> Typ des Werts des gegebenen {@link Field}.
	 * @param target {@link Field} zur Modifikation.
	 * @param transGet {@link Getter} zum Umwandeln des Wert beim Lesen.
	 * @param transSet {@link Getter} zum Umwandeln des Wert beim Schreiben.
	 * @return {@code translated}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GItem, GSource, GTarget> Field2<GItem, GTarget> toTranslated(final Field<? super GItem, GSource> target,
		final Getter<? super GSource, ? extends GTarget> transGet, final Getter<? super GTarget, ? extends GSource> transSet) throws NullPointerException {
		return Fields.from(Getters.concat(target, transGet), Setters.toTranslated(target, transSet));
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #toTranslated(Field, Getter, Getter)
	 * Fields.translatedField(Translators.toTargetGetter(translator), Translators.toSourceGetter(translator), field)}.
	 *
	 * @see Getters#fromTarget(Translator)
	 * @see Getters#fromSource(Translator) */
	public static <GItem, GSource, GTarget> Field2<GItem, GTarget> toTranslated(final Field<? super GItem, GSource> target,
		final Translator<GSource, GTarget> trans) throws NullPointerException {
		return Fields.toTranslated(target, Getters.fromTarget(trans), Getters.fromSource(trans));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableField new ObservableField<>(target)}. */
	public static <GItem, GValue> ObservableField<GItem, GValue> toObservable(final Field<? super GItem, GValue> target) throws NullPointerException {
		return new ObservableField<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toAggregated(Field, Getter, Getter) Fields.aggregatedField(Getters.neutralGetter(), Getters.neutralGetter(),
	 * field)}. */
	public static <GItem, GValue> Field2<Iterable<? extends GItem>, GValue> toAggregated(final Field<? super GItem, GValue> target) throws NullPointerException {
		return Fields.toAggregated(target, Getters.<GValue>neutral(), Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #toAggregated(Field, Getter, Getter, Object, Object) Fields.aggregatedField(Getters.neutralGetter(),
	 * Getters.neutralGetter(), emptyTarget, mixedTarget, field)}. */
	public static <GEntry, GValue> Field2<Iterable<? extends GEntry>, GValue> toAggregated(final Field<? super GEntry, GValue> target, final GValue empty,
		final GValue mixed) throws NullPointerException {
		return Fields.toAggregated(target, Getters.<GValue>neutral(), Getters.<GValue>neutral(), empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toAggregated(Field, Getter, Getter, Object, Object) Fields.aggregatedField(toTarget, toSource, null, null,
	 * field)}. */
	public static <GEntry, GSource, GTarget> Field2<Iterable<? extends GEntry>, GTarget> toAggregated(final Field<? super GEntry, GSource> target,
		final Getter<? super GSource, ? extends GTarget> transGet, final Getter<? super GTarget, ? extends GSource> transSet) throws NullPointerException {
		return Fields.toAggregated(target, transGet, transSet, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Setter) Fields.compositeField(Getters.aggregatedGetter(toTarget, emptyTarget, mixedTarget,
	 * field), Setters.aggregatedSetter(toSource, field))}. Mit einem aggregierten {@link Field} können die Elemente des iterierbaren Datensatzes parallel
	 * modifiziert werden.
	 *
	 * @see Getters#toAggregated(Getter, Getter, Object, Object)
	 * @see Setters#toAggregated(Setter, Getter) */
	public static <GEntry, GSource, GTarget> Field2<Iterable<? extends GEntry>, GTarget> toAggregated(final Field<? super GEntry, GSource> target,
		final Getter<? super GSource, ? extends GTarget> transGet, final Getter<? super GTarget, ? extends GSource> transSet, final GTarget empty,
		final GTarget mixed) throws NullPointerException {
		return Fields.from(Getters.toAggregated(target, transGet, empty, mixed), Setters.toAggregated(target, transSet));
	}

	/** Diese Methode ist eine Abkürzung für {@link #toSynchronized(Field, Object) Fields.toSynchronized(target, target)}. */
	public static <GItem, GValue> Field2<GItem, GValue> toSynchronized(final Field<? super GItem, GValue> target) throws NullPointerException {
		return Fields.toSynchronized(target, target);
	}

	public static <GItem, GValue> Field2<GItem, GValue> toSynchronized(final Field<? super GItem, GValue> target, final Object mutex)
		throws NullPointerException {
		return new SynchronizedField<>(target, mutex);
	}

}