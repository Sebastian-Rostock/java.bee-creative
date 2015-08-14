package bee.creative.function;

import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert den abstrakten Datentyp eines Werts, analog zur {@link Class} eines {@link Object}.<br>
 * Ein solcher Datentyp besitzt Methoden zum Konvertieren eines gegebenen Werts sowie zur Prüfung der Kompatibilität zu anderen Datentypen.
 * 
 * @see Value
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts, in welchen ein gegebener Wert via {@link #valueOf(Value)} oder {@link #valueOf(Value, Context)} konvertiert werden kann.
 */
public abstract class Type<GValue> {

	/**
	 * Diese Methode gibt einen einfachen Datentyp mit dem gegebenen Identifikator zurück.
	 * 
	 * @see #id()
	 * @param <GValue> Typ des Werts.
	 * @param id Identifikator für {@link #id()}.
	 * @return {@code simple}-{@link Type}.
	 */
	public static <GValue> Type<GValue> simpleType(final int id) {
		return Type.simpleType(id, Objects.toInvokeString("simpleType", id));
	}

	/**
	 * Diese Methode gibt einen einfachen Datentyp mit dem gegebenen Identifikator und der gegebenen Textdarstellung zurück.
	 * 
	 * @see #id()
	 * @param <GValue> Typ des Werts.
	 * @param id Identifikator für {@link #id()}.
	 * @param toString Textdarstellung für {@link #toString()}.
	 * @return {@code simple}-{@link Type}.
	 */
	public static <GValue> Type<GValue> simpleType(final int id, final String toString) {
		if (toString == null) throw new NullPointerException("toString = null");
		return new Type<GValue>() {

			@Override
			public int id() {
				return id;
			}

			@Override
			public String toString() {
				return toString;
			}

		};
	}

	{}

	/**
	 * Diese Methode gibt den Identifikator dieses Datentyps zurück. Dieser sollte über eine statische Konstante definiert werden, um Fallunterscheidungen mit
	 * einem {@code switch}-Statement umsetzen zu können.
	 * 
	 * @return Identifikator dieses Datentyps.
	 */
	public abstract int id();

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist. Dies kann der Fall sein, wenn der gegebene
	 * Datentyp gleich zu diesem oder ein Vorfahre dieses Datentyps ist. Wenn der gegebene Datentyp {@code null} ist, wird {@code false} zurück gegeben.
	 * 
	 * @see Class#isAssignableFrom(Class)
	 * @param type Datentyp.
	 * @return {@code true}, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist.
	 */
	public boolean is(final Type<?> type) {
		return (type == this) || ((type != null) && (type.id() == this.id()));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Wert kontextfrei in einen Wert dieses Datentyps und gibt ihn zurück.<br>
	 * Der Rückgabewert entspricht {@code Context.DEFAULT.cast(value, this)}.
	 * 
	 * @see Type#id()
	 * @see Value#type()
	 * @see Context#DEFAULT
	 * @see #valueOf(Value, Context)
	 * @param value gegebener Wert.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code Context.DEFAUL} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
	 */
	public final GValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
		return Context.DEFAULT.cast(value, this);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Wert kontextsensitiv in einen Wert dieses Datentyps und gibt ihn zurück.<br>
	 * Der Rückgabewert entspricht {@code context.cast(value, this)}.
	 * 
	 * @see Type#id()
	 * @see Value#type()
	 * @see Context#cast(Value, Type)
	 * @param value gegebener Wert.
	 * @param context Kontextobjekt.
	 * @return konvertierter Wert.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
	 */
	public final GValue valueOf(final Value value, final Context context) throws NullPointerException, ClassCastException, IllegalArgumentException {
		return context.cast(value, this);
	}

	{}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #id()
	 */
	@Override
	public int hashCode() {
		return this.id();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #id()
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Type<?>)) return false;
		final Type<?> data = (Type<?>)object;
		return this.id() == data.id();
	}

}
