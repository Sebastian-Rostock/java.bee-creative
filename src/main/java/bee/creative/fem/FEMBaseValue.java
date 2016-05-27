package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEM.ScriptFormatterInput;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen abstrakten Wert als {@link FEMFunction} und {@link ScriptFormatterInput}.<br>
 * Die {@link #invoke(FEMFrame)}-Methode liefert {@code this}, sodass Instanzen dieser Klassen das Einpacken in eine {@link FEMValueFunction} nicht benötigen.<br>
 * Die {@link #toString() Textdarstellung} des Werts wird über {@link #toScript(ScriptFormatter)} ermittelt. Diese Methode delegiert selbst an
 * {@link #toString()}, sodass mindestens eine dieser Methoden überschrieben werden muss.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMBaseValue implements FEMValue, FEMFunction, ScriptFormatterInput {

	/** Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextfrei konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code FEMContext.DEFAULT().dataFrom(this, type)}.
	 * 
	 * @see FEMContext#DEFAULT()
	 * @see FEMContext#dataFrom(FEMValue, FEMType)
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
	 * @param type Datentyp.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code type} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können. */
	public final <GData> GData data(final FEMType<GData> type) throws NullPointerException, IllegalArgumentException {
		return FEMContext._default_.dataFrom(this, type);
	}

	/** Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextsensitiv konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
	 * Der Rückgabewert entspricht {@code context.dataFrom(this, type)}.
	 * 
	 * @see FEMContext#dataFrom(FEMValue, FEMType)
	 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
	 * @param type Datentyp.
	 * @param context Kontext.
	 * @return Nutzdaten.
	 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können. */

	public final <GData> GData data(final FEMType<GData> type, final FEMContext context) throws NullPointerException, ClassCastException,
		IllegalArgumentException {
		return context.dataFrom(this, type);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public FEMValue result() {
		return this.result(false);
	}

	/** {@inheritDoc} */
	@Override
	public FEMValue result(final boolean recursive) {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hash(this.data());
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMValue)) return false;
		final FEMValue that = (FEMValue)object;
		return Objects.equals(this.type(), that.type()) && Objects.equals(this.data(), that.data());
	}

	/** {@inheritDoc} */
	@Override
	public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(this.toString());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return FEM.scriptFormatter().formatValue(this);
	}

}