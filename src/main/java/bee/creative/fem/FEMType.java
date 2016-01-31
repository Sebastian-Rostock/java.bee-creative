package bee.creative.fem;

import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert den abstrakten Datentyp eines Werts, analog zur {@link Class} eines {@link Object}.<br>
 * Ein solcher Datentyp besitzt Methoden zum Konvertieren der Nutzdaten eines gegebenen Werts sowie zur Prüfung der Kompatibilität zu anderen Datentypen.
 * 
 * @see FEMValue#type()
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ der von {@link #dataOf(FEMValue)} bzw. {@link #dataOf(FEMValue, FEMContext)} gelieferten Nutzdaten.
 */
public abstract class FEMType<GData> {

	/**
	 * Diese Methode gibt einen einfachen Datentyp mit dem gegebenen Identifikator zurück.
	 * 
	 * @see #id()
	 * @param <GValue> Typ des Werts.
	 * @param id Identifikator für {@link #id()}.
	 * @return {@code simple}-{@link FEMType}.
	 */
	public static final <GValue> FEMType<GValue> from(final int id) {
		return FEMType.from(id, Objects.toInvokeString("simpleType", id));
	}

	/**
	 * Diese Methode gibt einen einfachen Datentyp mit dem gegebenen Identifikator und der gegebenen Textdarstellung zurück.
	 * 
	 * @see #id()
	 * @param <GValue> Typ des Werts.
	 * @param id Identifikator für {@link #id()}.
	 * @param toString Textdarstellung für {@link #toString()}.
	 * @return {@code simple}-{@link FEMType}.
	 * @throws NullPointerException Wenn {@code toString} {@code null} ist.
	 */
	public static final <GValue> FEMType<GValue> from(final int id, final String toString) throws NullPointerException {
		if (toString == null) throw new NullPointerException("toString = null");
		return new FEMType<GValue>() {

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
	public boolean is(final FEMType<?> type) {
		return (type == this) || ((type != null) && (type.id() == this.id()));
	}

	/**
	 * Diese Methode gibt die in diesen Datentyp ({@code GData}) kontextfreie konvertierten Nutzdaten des gegebenen Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code Context.DEFAULT().dataOf(value, this)}.
	 * 
	 * @see FEMContext#DEFAULT()
	 * @see FEMContext#dataOf(FEMValue, FEMType)
	 * @param value gegebener Wert.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können.
	 */
	public final GData dataOf(final FEMValue value) throws NullPointerException, ClassCastException, IllegalArgumentException {
		return FEMContext._default_.dataOf(value, this);
	}

	/**
	 * Diese Methode gibt die in diesen Datentyp ({@code GData}) kontextsensitiv konvertierten Nutzdaten des gegebenen Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code context.dataOf(value, this)}.
	 * 
	 * @see FEMContext#dataOf(FEMValue, FEMType)
	 * @param value gegebener Wert.
	 * @param context Kontextobjekt.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können.
	 */
	public final GData dataOf(final FEMValue value, final FEMContext context) throws NullPointerException, ClassCastException, IllegalArgumentException {
		return context.dataOf(value, this);
	}

	{}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #id()
	 */
	@Override
	public final int hashCode() {
		return this.id();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #id()
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMType<?>)) return false;
		final FEMType<?> data = (FEMType<?>)object;
		return this.id() == data.id();
	}

}
