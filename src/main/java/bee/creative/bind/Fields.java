package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import bee.creative.bind.Properties.BaseProperty;
import bee.creative.util.Filter;
import bee.creative.util.Natives;
import bee.creative.util.Objects;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Field}-Instanzen.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	/** Diese Klasse implementiert ein abstraktes {@link Field} als {@link BaseObject}. */
	public static abstract class BaseField<GItem, GValue> extends BaseObject implements Field<GItem, GValue> {
	}

	/** Diese Klasse implementiert {@link Fields#valueField(Object)}. */
	@SuppressWarnings ("javadoc")
	public static class ValueField<GValue> implements Field<Object, GValue> {

		public static final Field<?, ?> EMPTY = new ValueField<>(null);

		public final GValue value;

		public ValueField(final GValue value) {
			this.value = value;
		}

		@Override
		public GValue get(final Object item) {
			return this.value;
		}

		@Override
		public void set(final Object item, final GValue value) {
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Fields#nativeField(java.lang.reflect.Field)}. */
	@SuppressWarnings ("javadoc")
	public static class NativeField<GItem, GValue> implements Field<GItem, GValue> {

		public final java.lang.reflect.Field field;

		public NativeField(final java.lang.reflect.Field field, final boolean forceAccessible) {
			this.field = forceAccessible ? Natives.forceAccessible(field) : Objects.notNull(field);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				return (GValue)this.field.get(item);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				this.field.set(item, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#setupField(Getter, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class SetupField<GItem, GValue> implements Field<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> setup;

		public final Field<? super GItem, GValue> field;

		public SetupField(final Getter<? super GItem, ? extends GValue> setup, final Field<? super GItem, GValue> field) {
			this.setup = Objects.notNull(setup);
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get(final GItem item) {
			GValue result = this.field.get(item);
			if (result != null) return result;
			result = this.setup.get(item);
			this.field.set(item, result);
			return result;
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.field.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setup, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#defaultField(Object, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class DefaultField<GItem, GValue> implements Field<GItem, GValue> {

		public final Field<? super GItem, GValue> field;

		public final GValue value;

		public DefaultField(final Field<? super GItem, GValue> field, final GValue value) {
			this.field = Objects.notNull(field);
			this.value = value;
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.value;
			return this.field.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (item == null) return;
			this.field.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Fields#mappingField(Map)}. */
	@SuppressWarnings ("javadoc")
	public static class MappingField<GItem, GValue> implements Field<GItem, GValue> {

		public final Map<GItem, GValue> mapping;

		public MappingField(final Map<GItem, GValue> mapping) {
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

	/** Diese Klasse implementiert {@link Fields#navigatedField(Getter, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class NavigatedField<GSource, GTarget, GValue> implements Field<GSource, GValue> {

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public final Field<? super GTarget, GValue> field;

		public NavigatedField(final Getter<? super GSource, ? extends GTarget> toTarget, final Field<? super GTarget, GValue> field) {
			this.toTarget = Objects.notNull(toTarget);
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get(final GSource item) {
			return this.field.get(this.toTarget.get(item));
		}

		@Override
		public void set(final GSource item, final GValue value) {
			this.field.set(this.toTarget.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toTarget, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#compositeField(Getter, Setter)}. */
	@SuppressWarnings ("javadoc")
	public static class CompositeField<GItem, GValue> implements Field<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> getter;

		public final Setter<? super GItem, ? super GValue> setter;

		public CompositeField(final Getter<? super GItem, ? extends GValue> getter, final Setter<? super GItem, ? super GValue> setter) {
			this.getter = Objects.notNull(getter);
			this.setter = Objects.notNull(setter);
		}

		@Override
		public GValue get(final GItem item) {
			return this.getter.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.setter.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter, this.setter);
		}

	}

	/** Diese Klasse implementiert {@link Fields#translatedField(Translator, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedField<GItem, GTarget, GSource> implements Field<GItem, GTarget> {

		public final Field<? super GItem, GSource> field;

		public final Translator<GSource, GTarget> translator;

		public TranslatedField(final Field<? super GItem, GSource> field, final Translator<GSource, GTarget> translator) {
			this.field = Objects.notNull(field);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public GTarget get(final GItem item) {
			return this.translator.toTarget(this.field.get(item));
		}

		@Override
		public void set(final GItem item, final GTarget value) {
			this.field.set(item, this.translator.toSource(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Fields#conditionalField(Filter, Field, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class ConditionalField<GItem, GValue> implements Field<GItem, GValue> {

		public final Filter<? super GItem> condition;

		public final Field<? super GItem, GValue> acceptField;

		public final Field<? super GItem, GValue> rejectField;

		public ConditionalField(final Filter<? super GItem> condition, final Field<? super GItem, GValue> acceptField,
			final Field<? super GItem, GValue> rejectField) {
			this.condition = Objects.notNull(condition);
			this.acceptField = Objects.notNull(acceptField);
			this.rejectField = Objects.notNull(rejectField);
		}

		@Override
		public GValue get(final GItem item) {
			if (this.condition.accept(item)) return this.acceptField.get(item);
			return this.rejectField.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (this.condition.accept(item)) {
				this.acceptField.set(item, value);
			} else {
				this.rejectField.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.condition, this.acceptField, this.rejectField);
		}

	}

	/** Diese Klasse implementiert {@link Fields#synchronizedField(Object, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedField<GItem, GValue> implements Field<GItem, GValue> {

		public final Object mutex;

		public final Field<? super GItem, GValue> field;

		public SynchronizedField(final Object mutex, final Field<? super GItem, GValue> field) {
			this.mutex = Objects.notNull(mutex, this);
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.field.get(item);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			synchronized (this.mutex) {
				this.field.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#toProperty(Object, Field)}. */
	@SuppressWarnings ("javadoc")
	static class FieldProperty<GValue, GItem> extends BaseProperty<GValue> {

		public final GItem item;

		public final Field<? super GItem, GValue> field;

		public FieldProperty(final GItem item, final Field<? super GItem, GValue> field) {
			this.item = item;
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get() {
			return this.field.get(this.item);
		}

		@Override
		public void set(final GValue value) {
			this.field.set(this.item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.field);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#valueField(Object) Fields.valueField(null)}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Field<Object, GValue> emptyField() {
		return (Field<Object, GValue>)ValueField.EMPTY;
	}

	/** Diese Methode gibt ein {@link Field} zurück, das stets den gegebenen Wert liefert und das Schreiben ignoriert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param value Wert.
	 * @return {@code value}-{@link Field}. */
	public static <GValue> Field<Object, GValue> valueField(final GValue value) {
		if (value == null) return Fields.emptyField();
		return new ValueField<>(value);
	}

	/** Diese Methode gibt ein initialisierendes {@link Field} zurück. Das Schreiben wird direkt an das gegebene {@link Field} {@code field} delegiert. Beim Lesen
	 * wird der Wert zuerst über das gegebene {@link Field} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. üner den gegebenen
	 * {@link Getter} {@code setup} ermittelt und über das {@link Field} {@code field} geschrieben.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param setup Methode zur Initialisierung.
	 * @param field Datenfeld zur Manipulation.
	 * @return {@code setup}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} bzw. {@code setup} {@code null} ist. */
	public static <GItem, GValue> Field<GItem, GValue> setupField(final Getter<? super GItem, ? extends GValue> setup, final Field<? super GItem, GValue> field)
		throws NullPointerException {
		return new SetupField<>(setup, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(java.lang.reflect.Field, boolean) Fields.nativeField(field, true)}. */
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(field, true);
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
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeField<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(Method, Method, boolean) Fields.nativeField(getMethod, setMethod, true)}. */
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final Method getMethod, final Method setMethod)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(getMethod, setMethod, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.nativeGetter(getMethod, forceAccessible),
	 * Setters.nativeSetter(setMethod, forceAccessible))}.
	 *
	 * @see Getters#nativeGetter(Method, boolean)
	 * @see Setters#nativeSetter(Method, boolean) */
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final Method getMethod, final Method setMethod, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.compositeField(Getters.<GItem, GValue>nativeGetter(getMethod, forceAccessible),
			Setters.<GItem, GValue>nativeSetter(setMethod, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(Class, String, boolean) Fields.nativeField(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeField(java.lang.reflect.Field, boolean) Fields.nativeField(Natives.parseField(fieldOwner, fieldName),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final Class<? extends GItem> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #defaultField(Object, Field) Fields.defaultField(null, field)}. */
	public static <GItem, GValue> Field<GItem, GValue> defaultField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.defaultField(null, field);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #compositeField(Getter, Setter) Fields.compositeField(Fields.defaultGetter(value, field),
	 * Fields.defaultSetter(field))}.
	 *
	 * @see Getters#defaultGetter(Object, Getter)
	 * @see Setters#defaultSetter(Setter) */
	public static <GItem, GValue> Field<GItem, GValue> defaultField(final GValue value, final Field<? super GItem, GValue> field) throws NullPointerException {
		return new DefaultField<>(field, value);
	}

	/** Diese Methode gibt ein {@link Field} zurück, welches beim Lesen am {@link Map#get(Object)} sowie beim Schreiben an {@link Map#put(Object, Object)}
	 * delegiert.
	 *
	 * @param <GEntry> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param mapping {@link Map} zur Abbildung von einer Eingabe auf einen Wert.
	 * @return {@code mapping}-{@link Field}.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
	public static <GEntry, GValue> Field<GEntry, GValue> mappingField(final Map<GEntry, GValue> mapping) throws NullPointerException {
		return new MappingField<>(mapping);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.navigatedGetter(toTarget, field),
	 * Setters.navigatedSetter(toTarget, field))}.
	 *
	 * @see Getters#navigatedGetter(Getter, Getter)
	 * @see Setters#navigatedSetter(Getter, Setter) */
	public static <GSource, GTarget, GValue> Field<GSource, GValue> navigatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Field<? super GTarget, GValue> field) throws NullPointerException {
		return new NavigatedField<>(toTarget, field);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #translatedField(Getter, Getter, Field)
	 * Fields.translatedField(Translators.toTargetGetter(translator), Translators.toSourceGetter(translator), field)}.
	 *
	 * @see Translators#toTargetGetter(Translator)
	 * @see Translators#toSourceGetter(Translator) */
	public static <GItem, GSource, GTarget> Field<GItem, GTarget> translatedField(final Translator<GSource, GTarget> translator,
		final Field<? super GItem, GSource> field) throws NullPointerException {
		return new TranslatedField<>(field, translator);
	}

	/** Diese Methode gibt ein übersetztes {@link Field} zurück. Das erzeugte {@link Field} liefert beim Lesen den Wert, der über den gegebenen {@link Getter}
	 * {@code toTarget} aus dem über das gegebene {@link Field} ermittelten Wert berechnet wird. Beim Schreiben eines Werts wird dieser über dem gegebenen
	 * {@link Getter} {@code toSource} in einen Wert überfüght, welcher anschließend an das gegebene {@link Field} delegiert wird.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GTarget> Typ des Werts des erzeugten {@link Field}.
	 * @param <GSource> Typ des Werts des gegebenen {@link Field}.
	 * @param field {@link Field} zur Modifikation.
	 * @param toTarget {@link Getter} zum Umwandeln des Wert beim Lesen.
	 * @param toSource {@link Getter} zum Umwandeln des Wert beim Schreiben.
	 * @return {@code translated}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GItem, GSource, GTarget> Field<GItem, GTarget> translatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GTarget, ? extends GSource> toSource, final Field<? super GItem, GSource> field) throws NullPointerException {
		return Fields.compositeField(Getters.translatedGetter(toTarget, field), Setters.translatedSetter(toSource, field));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableField new ObservableField<>(field)}. */
	public static <GItem, GValue> Field<GItem, GValue> observableField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return new ObservableField<>(field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #observableField(Field) Fields.observableField(Fields.compositeField(getter, setter))}. */
	public static <GItem, GValue> Field<GItem, GValue> observableField(final Getter<? super GItem, ? extends GValue> getter,
		final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return Fields.observableField(Fields.compositeField(getter, setter));
	}

	/** Diese Methode gibt ein zusammengesetztes {@link Field} zurück, dessen Methoden an die des gegebenen {@link Getter} und {@link Setter} delegieren.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param getter {@link Getter} für {@link Field#get(Object)}.
	 * @param setter {@link Setter} für {@link Field#set(Object, Object)}.
	 * @return {@code composite}-{@link Field}.
	 * @throws NullPointerException Wenn {@code getter} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GValue> Field<GItem, GValue> compositeField(final Getter<? super GItem, ? extends GValue> getter,
		final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return new CompositeField<>(getter, setter);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedField(Getter, Getter, Field) Fields.aggregatedField(Getters.neutralGetter(),
	 * Getters.neutralGetter(), field)}. */
	public static <GItem, GValue> Field<Iterable<? extends GItem>, GValue> aggregatedField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.aggregatedField(Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter(), field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedField(Getter, Getter, Object, Object, Field) Fields.aggregatedField(Getters.neutralGetter(),
	 * Getters.neutralGetter(), emptyTarget, mixedTarget, field)}. */
	public static <GEntry, GValue> Field<Iterable<? extends GEntry>, GValue> aggregatedField(final GValue emptyTarget, final GValue mixedTarget,
		final Field<? super GEntry, GValue> field) throws NullPointerException {
		return Fields.aggregatedField(Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter(), emptyTarget, mixedTarget, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedField(Getter, Getter, Object, Object, Field) Fields.aggregatedField(toTarget, toSource, null, null,
	 * field)}. */
	public static <GEntry, GSource, GTarget> Field<Iterable<? extends GEntry>, GTarget> aggregatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GTarget, ? extends GSource> toSource, final Field<? super GEntry, GSource> field) throws NullPointerException {
		return Fields.aggregatedField(toTarget, toSource, null, null, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.aggregatedGetter(toTarget, emptyTarget,
	 * mixedTarget, field), Setters.aggregatedSetter(toSource, field))}. Mit einem aggregierten {@link Field} können die Elemente des iterierbaren Datensatzes
	 * parallel modifiziert werden.
	 *
	 * @see Getters#aggregatedGetter(Getter, Object, Object, Getter)
	 * @see Setters#aggregatedSetter(Getter, Setter) */
	public static <GEntry, GSource, GTarget> Field<Iterable<? extends GEntry>, GTarget> aggregatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GTarget, ? extends GSource> toSource, final GTarget emptyTarget, final GTarget mixedTarget, final Field<? super GEntry, GSource> field)
		throws NullPointerException {
		return Fields.compositeField(Getters.aggregatedGetter(toTarget, emptyTarget, mixedTarget, field), Setters.aggregatedSetter(toSource, field));
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.conditionalGetter(condition,
	 * acceptField, rejectField), Setters.conditionalSetter(condition, acceptField, rejectField))}.
	 *
	 * @see Getters#conditionalGetter(Filter, Getter, Getter)
	 * @see Setters#conditionalSetter(Filter, Setter, Setter) */
	public static <GItem, GValue> Field<GItem, GValue> conditionalField(final Filter<? super GItem> condition, final Field<? super GItem, GValue> acceptField,
		final Field<? super GItem, GValue> rejectField) throws NullPointerException {
		return new ConditionalField<>(condition, acceptField, rejectField);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedField(Object, Field) Fields.synchronizedField(field, field)}. */
	public static <GItem, GValue> Field<GItem, GValue> synchronizedField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.synchronizedField(field, field);
	}

	/** Diese Methode gibt einen {@link Field} zurück, welcher das gegebenen {@link Field} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird das erzeugte {@link Field} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @return {@code synchronized}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static <GItem, GValue> Field<GItem, GValue> synchronizedField(final Object mutex, final Field<? super GItem, GValue> field)
		throws NullPointerException {
		return new SynchronizedField<>(mutex, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#toProperty(Object, Field) Fields.toProperty(null, field)}. */
	public static <GItem, GValue> Property<GValue> toProperty(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.toProperty(null, field);
	}

	/** Diese Methode gibt ein {@link Property} zurück, dessen Methoden mit dem gegebenen Datensatz an das gegebene {@link Field} delegieren.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param item Datensatz.
	 * @param field {@link Field}.
	 * @return {@link Field}-{@link Property}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static <GItem, GValue> Property<GValue> toProperty(final GItem item, final Field<? super GItem, GValue> field) throws NullPointerException {
		return new FieldProperty<>(item, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#toSet(Property) Properties.toSet(Fields.toProperty(item, field))}. */
	public static <GItem, GEntry> Set<GEntry> toSet(final GItem item, final Field<? super GItem, Set<GEntry>> field) throws NullPointerException {
		return Properties.toSet(Fields.toProperty(item, field));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#toList(Property) Properties.toList(Fields.toProperty(item, field))}. */
	public static <GItem, GEntry> List<GEntry> toList(final GItem item, final Field<? super GItem, List<GEntry>> field) throws NullPointerException {
		return Properties.toList(Fields.toProperty(item, field));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#toMap(Property) Properties.toMap(Fields.toProperty(item, field))}. */
	public static <GItem, GKey, GValue> Map<GKey, GValue> toMap(final GItem item, final Field<? super GItem, Map<GKey, GValue>> field)
		throws NullPointerException {
		return Properties.toMap(Fields.toProperty(item, field));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#toCollection(Property) Properties.toCollection(Fields.toProperty(item, field))}. */
	public static <GItem, GEntry> Collection<GEntry> toCollection(final GItem item, final Field<? super GItem, Collection<GEntry>> field) throws NullPointerException {
		return Properties.toCollection(Fields.toProperty(item, field));
	}

}