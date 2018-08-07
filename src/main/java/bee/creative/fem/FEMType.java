package bee.creative.fem;

import bee.creative.util.Objects;

/** Diese Klasse implementiert den abstrakten Datentyp eines Werts, analog zur {@link Class} eines {@link Object}.<br>
 * Ein solcher Datentyp besitzt Methoden zum Konvertieren der Nutzdaten eines gegebenen Werts sowie zur Prüfung der Kompatibilität zu anderen Datentypen.
 *
 * @see FEMValue#type()
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ der von {@link #dataFrom(FEMValue, FEMContext)} gelieferten Nutzdaten. */
public class FEMType<GData> {

	/** Diese Methode gibt einen einfachen Datentyp mit dem gegebenen Identifikator zurück.
	 *
	 * @see #id()
	 * @param <GData> Typ des Werts.
	 * @param id Identifikator für {@link #id()}.
	 * @return {@code simple}-{@link FEMType}. */
	public static <GData> FEMType<GData> from(final int id) {
		return new FEMType<>(id);
	}

	/** Dieses Feld speichert den Identifikator. */
	final int id;

	/** Dieser Konstruktor initialisiert den Identifikator.
	 *
	 * @param id Identifikator. */
	FEMType(final int id) {
		this.id = id;
	}

	/** Diese Methode gibt den Identifikator dieses Datentyps zurück. Dieser sollte über eine statische Konstante definiert werden, um Fallunterscheidungen mit
	 * einem {@code switch}-Statement umsetzen zu können.
	 *
	 * @return Identifikator dieses Datentyps. */
	public final int id() {
		return this.id;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist. Dies kann der Fall sein, wenn der gegebene
	 * Datentyp gleich zu diesem oder ein Vorfahre dieses Datentyps ist. Wenn der gegebene Datentyp {@code null} ist, wird {@code false} geliefert.
	 *
	 * @see Class#isAssignableFrom(Class)
	 * @param type Datentyp.
	 * @return {@code true}, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist. */
	public boolean is(final FEMType<?> type) {
		return (type == this) || ((type != null) && (type.id == this.id));
	}

	/** Diese Methode gibt die in den Datentyp {@code GData} kontextsensitiv konvertierten Nutzdaten des gegebenen Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code context.dataFrom(value, this)}.
	 *
	 * @see FEMContext#dataFrom(FEMValue, FEMType)
	 * @param value gegebener Wert.
	 * @param context Kontextobjekt.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten des Werts nicht konvertiert werden können. */
	public final GData dataFrom(final FEMValue value, final FEMContext context) throws NullPointerException, ClassCastException, IllegalArgumentException {
		return context.dataFrom(value, this);
	}

	/** {@inheritDoc}
	 *
	 * @see #id() */
	@Override
	public final int hashCode() {
		return this.id;
	}

	/** {@inheritDoc}
	 *
	 * @see #id() */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMType<?>)) return false;
		final FEMType<?> data = (FEMType<?>)object;
		return this.id == data.id;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.id);
	}

}
