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
	public static <GData> FEMType<GData> from(final int id) {
		return new FEMType<>(id);
	}

	/** Dieses Feld speichert den Identifikator. */
	final int id;

	/** Dieser Konstruktor initialisiert den Identifikator.
	 *
	 * @param id Identifikator. */
	protected FEMType(final int id) {
		this.id = id;
	}

	/** Diese Methode gibt den Identifikator dieses Datentyps zurück. Dieser sollte über eine statische Konstante definiert werden, um Fallunterscheidungen mit
	 * einem {@code switch}-Statement umsetzen zu können. Derzeit bekannt sind:
	 * <table>
	 * <tr>
	 * <th>{@link #id()}</th>
	 * <th>Datentyp</th>
	 * </tr>
	 * <tr>
	 * <td>{@code 0}</td>
	 * <td>{@link FEMVoid}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 1}</td>
	 * <td>{@link FEMArray}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 2}</td>
	 * <td>{@link FEMHandler}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 3}</td>
	 * <td>{@link FEMBoolean}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 4}</td>
	 * <td>{@link FEMString}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 5}</td>
	 * <td>{@link FEMBinary}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 6}</td>
	 * <td>{@link FEMInteger}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 7}</td>
	 * <td>{@link FEMDecimal}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 8}</td>
	 * <td>{@link FEMDuration}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 9}</td>
	 * <td>{@link FEMDatetime}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code 10}</td>
	 * <td>{@link FEMObject}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code -1}</td>
	 * <td>{@link FEMNative}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code -2}</td>
	 * <td>{@link FEMVariable}</td>
	 * </tr>
	 * </table>
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
	public boolean is(final FEMType<?> that) {
		return (this == that) || ((that != null) && (that.id == this.id));
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
		final FEMType<?> that = (FEMType<?>)object;
		return this.id == that.id;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.id);
	}

}
