package bee.creative.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.util.Builder;
import bee.creative.util.Field;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Type}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Types {

	/**
	 * Diese Schnittstelle definiert einen {@link Builder} für den {@link Type} eines {@link Item}s. Der über einen {@link TypeBuilder} erstellte {@link Type}
	 * besitzt eine Liste seiner {@link Type#fields()} sowie eine Liste von {@link Type}s (Vorfahren), an erlche die {@link Type#is(Type)}-Methode delegiert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	public static interface TypeBuilder<GItem> extends Builder<Type<GItem>> {

		/**
		 * Diese Methode gibt den {@link TypeBuilderSet}-Aspekt zurück, über den {@link Type#id()} und {@link Type#label()} gesetzt werden können.
		 * 
		 * @see TypeBuilderSet
		 * @return {@link TypeBuilderSet}-Aspekt.
		 */
		public TypeBuilderSet<GItem> set();

		/**
		 * Diese Methode gibt den {@link TypeBuilderClear}-Aspekt zurück, über den die Listen der via {@link #append()} zugeordneten {@link Type}s bzw.
		 * {@link Field}s geleert werden können.
		 * 
		 * @see TypeBuilderClear
		 * @return {@link TypeBuilderClear}-Aspekt.
		 */
		public TypeBuilderClear<GItem> clear();

		/**
		 * Diese Methode gibt den {@link TypeBuilderModify}-Aspekt zurück, über den die Listen der via {@link #append()} zugeordneten {@link Type}s bzw.
		 * {@link Field}s erweitert werden können.
		 * 
		 * @return {@link TypeBuilderModify}-Aspekt zum Hinzufügen von {@link Type}s bzw. {@link Field}s.
		 */
		public TypeBuilderModify<GItem> append();

		/**
		 * Diese Methode gibt den {@link TypeBuilderModify}-Aspekt zurück, über den die Listen der via {@link #append()} zugeordneten {@link Type}s bzw.
		 * {@link Field}s reduziert werden können.
		 * 
		 * @return {@link TypeBuilderModify}-Aspekt zum Entfernen von {@link Type}s bzw. {@link Field}s.
		 */
		public TypeBuilderModify<GItem> remove();

		/**
		 * Diese Methode erzeugt den im {@link TypeBuilder} konfigurierte {@link Type} und gibt ihn zurück.
		 * 
		 * @return {@link Type}.
		 */
		@Override
		public Type<GItem> build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Builder} für den {@link Type} eines {@link Item}s. Der über einen {@link TypeBuilder} erstellte {@link Type}
	 * besitzt eine Liste seiner {@link Type#fields()} sowie eine Liste von {@link Type}s (Vorfahren), an erlche die {@link Type#is(Type)}-Methode delegiert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	public static interface TypeBuilderSet<GItem> {

		/**
		 * Diese Methode setzt die {@link Type#id()} und gibt diesen {@link TypeBuilder} zurück.
		 * 
		 * @see Type#id()
		 * @param value Identifikator.
		 * @return {@link TypeBuilder}.
		 */
		public TypeBuilder<GItem> id(int value);

		/**
		 * Diese Methode setzt die {@link Type#label()} und gibt diesen {@link TypeBuilder} zurück.
		 * 
		 * @see Type#label()
		 * @param value Beschriftung.
		 * @return {@link TypeBuilder}.
		 */
		public TypeBuilder<GItem> label(String value);

	}

	/**
	 * Diese Schnittstelle definiert den Aspekt eines {@link TypeBuilder}s, über den die Listen der zugeordneten {@link Type}s bzw. {@link Field}s geleert werden
	 * kann.
	 * 
	 * @see TypeBuilder#clear()
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	public static interface TypeBuilderClear<GItem> {

		/**
		 * Diese Methode leert die Liste der zugeordneten {@link Type}s udn gibt den {@link TypeBuilder} zurück.
		 * 
		 * @see TypeBuilder#append()
		 * @return {@link TypeBuilder}.
		 */
		public TypeBuilder<GItem> types();

		/**
		 * Diese Methode leert die Liste der zugeordneten {@link Field}s udn gibt den {@link TypeBuilder} zurück.
		 * 
		 * @see TypeBuilder#append()
		 * @return {@link TypeBuilder}.
		 */
		public TypeBuilder<GItem> fields();

	}

	/**
	 * Diese Schnittstelle definiert den Aspekt eines {@link TypeBuilder}s, über den die Listen der zugeordneten {@link Type}s bzw. {@link Field}s erweitert bzw.
	 * reduziert werden kann.
	 * 
	 * @see TypeBuilder#append()
	 * @see TypeBuilder#remove()
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	public static interface TypeBuilderModify<GItem> {

		/**
		 * Diese Methode modifiziert die Liste der zugeordneten {@link Type}s mit dem gegebenen {@link Type} und gibt anschließend den {@link TypeBuilderAnd}-Aspekt
		 * zur Fortführung derartiger Modifikationen zurück.<br>
		 * Wenn dieser {@link TypeBuilderModify}-Aspekt über {@link TypeBuilder#append()} erzeugt wurde, wird der gegebene {@link Type} zur Liste hinzugefügt;
		 * andernfalls wird er aus dieser entfert.
		 * 
		 * @param value {@link Type}.
		 * @return {@link TypeBuilderAnd}-Aspekt.
		 */
		public TypeBuilderAnd<GItem, Type<? super GItem>> type(final Type<? super GItem> value);

		/**
		 * Diese Methode modifiziert die Liste der zugeordneten {@link Field}s mit dem gegebenen {@link Field} und gibt anschließend den {@link TypeBuilderAnd}
		 * -Aspekt zur Fortführung derartiger Modifikationen zurück.<br>
		 * Wenn dieser {@link TypeBuilderModify}-Aspekt über {@link TypeBuilder#append()} erzeugt wurde, wird der gegebene {@link Field} zur Liste hinzugefügt;
		 * andernfalls wird er aus dieser entfert.
		 * 
		 * @param value {@link Type}.
		 * @return {@link TypeBuilderAnd}-Aspekt.
		 */
		public TypeBuilderAnd<GItem, Field<? super GItem, ?>> field(final Field<? super GItem, ?> value);

	}

	/**
	 * Diese Schnittstelle definiert den Aspekt eines {@link TypeBuilder}s, über den die Modifikation der Listen der zugeordneten {@link Type}s bzw. {@link Field}
	 * s fortgeführt werden kann.
	 * 
	 * @see TypeBuilderModify#type(Type)
	 * @see TypeBuilderModify#field(Field)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 * @param <GValue> Typ des Elements, dass in die Liste eingefügt bzw. aus ihr entfernt wird.
	 */
	public static interface TypeBuilderAnd<GItem, GValue> extends TypeBuilder<GItem> {

		/**
		 * Diese Methode führt die Modifikation des vorangegangenen Aufrufs von {@link TypeBuilderModify#type(Type)} bzw. {@link TypeBuilderModify#field(Field)} mit
		 * dem gegebenen Element fort und gibt anschließend den {@link TypeBuilderAnd}-Aspekt zur Fortführung derartiger Modifikationen zurück.<br>
		 * 
		 * @see TypeBuilder#append()
		 * @see TypeBuilder#remove()
		 * @see TypeBuilderModify#type(Type)
		 * @see TypeBuilderModify#field(Field)
		 * @param value {@link Type}.
		 * @return {@link TypeBuilderAnd}-Aspekt.
		 */
		public TypeBuilderAnd<GItem, GValue> and(final GValue value);

	}

	/**
	 * Diese Klasse implementiert den {@link Type}, der vom {@link TypeBuilder} erzeugt wird. Die {@link #equals(Object) Äquivalenz} dieses {@link Type}s basiert
	 * auf dem {@link #id() Identifikator}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	public static class TypeImpl<GItem> implements Type<GItem> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 */
		protected final int id;

		/**
		 * Dieses Feld speichert die Beschriftung.
		 */
		protected final String label;

		/**
		 * Dieses Feld speichert die {@link Type}s.
		 */
		protected final Type<?>[] types;

		/**
		 * Dieses Feld speichert die {@link Field}s.
		 */
		protected final Field<?, ?>[] fields;

		/**
		 * Dieser Konstruktor initialisiert Identifikator, Beschriftung, {@link Type}s und {@link Field}s.
		 * 
		 * @param id Identifikator.
		 * @param label Beschriftung.
		 * @param types {@link Type}s.
		 * @param fields {@link Field}s.
		 * @throws NullPointerException wenn eine der Eingaben {@code null} ist oder enthält.
		 */
		public TypeImpl(final int id, final String label, final Type<?>[] types, final Field<?, ?>[] fields) throws NullPointerException {
			if(label == null) throw new NullPointerException("label is null");
			if(types == null) throw new NullPointerException("types is null");
			if(fields == null) throw new NullPointerException("fields is null");
			if(Arrays.asList(types).contains(null)) throw new NullPointerException("types contains null");
			if(Arrays.asList(fields).contains(null)) throw new NullPointerException("tields contains null");
			this.id = id;
			this.label = label;
			this.types = types;
			this.fields = fields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return this.id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean is(final Type<?> type) throws NullPointerException {
			if(type == null) return false;
			if((this == type) || (this.id() == type.id())) return true;
			for(final Type<?> type2: this.types)
				if(type2.is(type)) return true;
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public Iterable<? extends Field<? super GItem, ?>> fields() {
			return (Iterable<? extends Field<? super GItem, ?>>)Iterables.unmodifiableIterable(Arrays.asList(this.fields));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String label() {
			return this.label;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Type<?>)) return false;
			final Type<?> data = (Type<?>)object;
			return this.id == data.id();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.id, this.label, this.types, this.fields);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link TypeBuilder} mit {@link TypeBuilderClear}-Aspekt.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static final class ParentBuilder<GItem> implements TypeBuilder<GItem>, TypeBuilderSet<GItem>, TypeBuilderClear<GItem> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 */
		Integer id;

		/**
		 * Dieses Feld speichert die Beschriftung.
		 */
		String label;

		/**
		 * Dieses Feld speichert die Liste der {@link Type}s.
		 */
		final List<Type<?>> types = new ArrayList<Type<?>>();

		/**
		 * Dieses Feld speichert die Liste der {@link Field}s.
		 */
		final List<Field<?, ?>> fields = new ArrayList<Field<?, ?>>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilder<GItem> id(final int value) {
			this.id = Integer.valueOf(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilder<GItem> label(final String value) {
			this.label = value;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderSet<GItem> set() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderClear<GItem> clear() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderModify<GItem> append() {
			return new AppendBuilder<GItem>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderModify<GItem> remove() {
			return new RemoveBuilder<GItem>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<GItem> build() {
			return new TypeImpl<GItem>(this.id.intValue(), this.label.intern(), this.types.toArray(new Type<?>[this.types.size()]),
				this.fields.toArray(new Field<?, ?>[this.fields.size()]));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilder<GItem> types() {
			this.types.clear();
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilder<GItem> fields() {
			this.fields.clear();
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderModify}-Aspekt eines {@link ParentBuilder}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static abstract class ModifyBuilder<GItem> implements TypeBuilderModify<GItem>, TypeBuilder<GItem> {

		/**
		 * Dieses Feld speichert den {@link ParentBuilder}.
		 */
		final ParentBuilder<GItem> parent;

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public ModifyBuilder(final ParentBuilder<GItem> parent) {
			this.parent = parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderSet<GItem> set() {
			return this.parent.set();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderClear<GItem> clear() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderModify<GItem> append() {
			return this.parent.append();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderModify<GItem> remove() {
			return this.parent.remove();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Type<GItem> build() {
			return this.parent.build();
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderModify}-Aspekt zu {@link ParentBuilder#append()}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static class AppendBuilder<GItem> extends ModifyBuilder<GItem> {

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public AppendBuilder(final ParentBuilder<GItem> parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderAnd<GItem, Type<? super GItem>> type(final Type<? super GItem> value) {
			return new AppendAndTypeBuilder<GItem>(this.parent).and(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderAnd<GItem, Field<? super GItem, ?>> field(final Field<? super GItem, ?> value) {
			return new AppendAndFieldBuilder<GItem>(this.parent).and(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderAnd}-Aspekt zu {@link ParentBuilder#append()} und {@link AppendBuilder#type(Type)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static final class AppendAndTypeBuilder<GItem> extends AppendBuilder<GItem> implements TypeBuilderAnd<GItem, Type<? super GItem>> {

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public AppendAndTypeBuilder(final ParentBuilder<GItem> parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderAnd<GItem, Type<? super GItem>> and(final Type<? super GItem> value) {
			this.parent.types.add(value);
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderAnd}-Aspekt zu {@link ParentBuilder#append()} und {@link AppendBuilder#field(Field)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static final class AppendAndFieldBuilder<GItem> extends AppendBuilder<GItem> implements TypeBuilderAnd<GItem, Field<? super GItem, ?>> {

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public AppendAndFieldBuilder(final ParentBuilder<GItem> parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderAnd<GItem, Field<? super GItem, ?>> and(final Field<? super GItem, ?> value) {
			this.parent.fields.add(value);
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderModify}-Aspekt zu {@link ParentBuilder#remove()}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static class RemoveBuilder<GItem> extends ModifyBuilder<GItem> {

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public RemoveBuilder(final ParentBuilder<GItem> parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderAnd<GItem, Type<? super GItem>> type(final Type<? super GItem> value) {
			return new RemoveAndTypeBuilder<GItem>(this.parent).and(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TypeBuilderAnd<GItem, Field<? super GItem, ?>> field(final Field<? super GItem, ?> value) {
			return new RemoveAndFieldBuilder<GItem>(this.parent).and(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderAnd}-Aspekt zu {@link ParentBuilder#remove()} und {@link RemoveBuilder#type(Type)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static final class RemoveAndTypeBuilder<GItem> extends RemoveBuilder<GItem> implements TypeBuilderAnd<GItem, Type<? super GItem>> {

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public RemoveAndTypeBuilder(final ParentBuilder<GItem> parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderAnd<GItem, Type<? super GItem>> and(final Type<? super GItem> value) {
			this.parent.types.remove(value);
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TypeBuilderAnd}-Aspekt zu {@link ParentBuilder#remove()} und {@link RemoveBuilder#field(Field)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des {@link Item}s.
	 */
	static final class RemoveAndFieldBuilder<GItem> extends RemoveBuilder<GItem> implements TypeBuilderAnd<GItem, Field<? super GItem, ?>> {

		/**
		 * Dieser Konstruktor initialisiert den {@link ParentBuilder}.
		 * 
		 * @param parent {@link ParentBuilder}.
		 */
		public RemoveAndFieldBuilder(final ParentBuilder<GItem> parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeBuilderAnd<GItem, Field<? super GItem, ?>> and(final Field<? super GItem, ?> value) {
			this.parent.fields.remove(value);
			return this;
		}

	}

	/**
	 * Diese Methode erzeugt einen neuen {@link TypeBuilder} und gibt ihn zurück.
	 * 
	 * @param <GItem> Typ des {@link Item}s.
	 * @return {@link TypeBuilder}.
	 */
	public static <GItem> TypeBuilder<GItem> builder() {
		return new ParentBuilder<GItem>();
	}

}