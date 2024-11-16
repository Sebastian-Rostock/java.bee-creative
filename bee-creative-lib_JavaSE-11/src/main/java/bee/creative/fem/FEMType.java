package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert den abstrakten Datentyp eines Werts, analog zur {@link Class} eines {@link Object}. Ein solcher Datentyp besitzt Methoden zum
 * Konvertieren der Nutzdaten eines gegebenen Werts sowie zur Prüfung der Kompatibilität zu anderen Datentypen.
 *
 * @see FEMValue#type()
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ der von {@link FEMContext#dataFrom(FEMValue, FEMType)} gelieferten Nutzdaten. */
public class FEMType<GData> {

	/** Diese Methode gibt einen einfachen Datentyp mit dem gegebenen Identifikator zurück.
	 *
	 * @see #id()
	 * @param <GData> Typ des Werts.
	 * @param id Identifikator für {@link #id()}.
	 * @return {@code simple}-{@link FEMType}. */
	public static <GData> FEMType<GData> from(int id) {
		return new FEMType<>(id);
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
	 * @param that Datentyp.
	 * @return {@code true}, wenn ein {@code cast} in den gegebenen Datentyp zulässig ist. */
	public boolean is(FEMType<?> that) {
		return (this == that) || ((that != null) && (that.id == this.id));
	}

	/** {@inheritDoc}
	 *
	 * @see #id() */
	@Override
	public int hashCode() {
		return this.id;
	}

	/** {@inheritDoc}
	 *
	 * @see #id() */
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMType<?>)) return false;
		var that = (FEMType<?>)object;
		return this.id == that.id;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.id);
	}

	/** Dieses Feld speichert den Identifikator. */
	final int id;

	/** Dieser Konstruktor initialisiert den Identifikator.
	 *
	 * @param id Identifikator. */
	protected FEMType(int id) {
		this.id = id;
	}

}
