package bee.creative.fem;

import bee.creative.util.Comparators;
import de.fhg.ivi.fee.core.FEE;
import de.fhg.ivi.fee.core.FEE_Values;

/**
 * Diese Klasse implementiert eine Zeitspanne aus Jahre, Monate, Tage, Stunden, Minuten und Sekunden zur Verschiebung von Daten und Zeitpunkten.
 * 
 * @author Sebastian Rostock 2014.
 */
public final class FEMDuration {

	/**
	 * Diese Methode erzeugt eine neue Zeitspanne der gegebenen Anzahl an Sekunden zurück.
	 * 
	 * @param seconds Sekunden ({@code -7549743600..7549743600}).
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@code seconds} ungültig ist.
	 */
	public static FEMDuration valueOf(final long seconds) throws IllegalArgumentException {
		if (seconds == 0) return new FEMDuration(0);
		if (seconds < 0) return FEMDuration.valueOf(-1, 0, 0, 0, 0, 0, -seconds);
		return FEMDuration.valueOf(+1, 0, 0, 0, 0, 0, +seconds);
	}

	/**
	 * Diese Methode erzeugt eine neue Zeitspanne mit den gegebenen Komponenten und gibt diese zurück.
	 * 
	 * @param sign Vorzeichen.
	 * @param years Jahre ({@code 0..255}).
	 * @param months Monate ({@code 0..3060}).
	 * @param days Tage ({@code 0..131071}).
	 * @param hours Stunden ({@code 0..4194303}).
	 * @param minutes Minuten ({@code 0..251658180}).
	 * @param seconds Sekunden ({@code 0..7549743600}).
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn einer der Parameter ungültig ist.
	 */
	public static FEMDuration valueOf(final int sign, final int years, final int months, final int days, final int hours, final int minutes, final long seconds,
		final long millis) throws IllegalArgumentException {
		if ((years < 0) || (years > 255)) throw FEMDatetime.illegalYear(years);
		if ((months < 0) || (months > 3060)) throw FEMDatetime.illegalMonth(months);
		if ((days < 0) || (days > 131071)) throw FEMDatetime.illegalDate(days);
		if ((hours < 0) || (hours > 4194303)) throw FEMDatetime.illegalHour(hours);
		if ((minutes < 0) || (minutes > 251658180)) throw FEMDatetime.illegalMinute(minutes);
		if ((seconds < 0) || (seconds > 7549743600L)) throw FEMDatetime.illegalSecond(seconds);
		final long totalMonths = months + (years * 12);
		if (totalMonths > 3060) throw FEMDuration.illegalTotalMonth(totalMonths);
		final long totalSeconds = seconds + ((long)minutes * 60) + ((long)hours * 3600);
		if (totalSeconds > 7549743600L) throw FEMDuration.illegalTotalSecond(totalSeconds);
		if ((days == 0) && (totalMonths == 0) && (totalSeconds == 0)) return new FEMDuration(0);
		final long totalSign = sign < 0 ? 4611686018427387904L : 0L;
		// TODO nicht normalisieren, wenn nur eine komponente nicht 0 ist
		final FEMDuration result = new FEMDuration(totalSign | ((long)days << 33) | //
			((totalMonths / 12) << 54) | ((totalMonths % 12) << 50) | //
			((totalSeconds / 3600) << 12) | (((totalSeconds / 60) % 60) << 6) | ((totalSeconds % 60) << 0));
		return result;
	}

	{}

	@SuppressWarnings ("javadoc")
	static IllegalArgumentException illegalTotalMonth(final long totalMonth) {
		return FEE_Values.illegal(null, "Die Gesamtanzahl der Monate '%s' ist ungültig.", totalMonth);
	}

	@SuppressWarnings ("javadoc")
	static IllegalArgumentException illegalTotalSecond(final long totalSecond) {
		return FEE_Values.illegal(null, "Die Gesamtanzahl der Sekunden '%s' ist ungültig.", totalSecond);
	}

	{}

	/**
	 * Dieses Feld speichert interne Darstellung der Zeitspanne.
	 * 
	 * @see #value()
	 */
	final int valueL;

	final int valueH;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung der Zeitspanne.
	 * 
	 * @see #value()
	 * @param value interne Darstellung der Zeitspanne.
	 */
	public FEMDuration(final long value) {
		this.value = value;
	}

	{}

	/**
	 * Diese Methode gibt die Gesamtanzahl der Monate zurück.
	 * 
	 * @return Zusammenfassung von {@link #yearsValue()} und {@link #monthsValue()}.
	 */
	final int totalMonths() {
		return this.monthsValue() + (this.yearsValue() * 12);
	}

	/**
	 * Diese Methode gibt die Gesamtanzahl der Millisekunden zurück.
	 * 
	 * @return Zusammenfassung von {@link #daysValue()}, {@link #hoursValue()}, {@link #minutesValue()}, {@link #secondsValue()} und {@link #millisValue()}.
	 */
	long _totalSeconds() {
		long result = this.daysValue();
		result = (result * 24) + this.hoursValue();
		result = (result * 60) + this.minutesValue();
		result = (result * 60) + this.secondsValue();
		result = (result * 1000) + this.millisValue();
		return result;
	}

	{}

	// Y[14]z[10]M[4]h[5]N[1]
	// D[18]m[6]s[6]
	// jahre bei modulo 400 jahre übertragen

	/**
	 * Diese Methode gibt die interne Darstellung der Zeitspanne zurück.<br>
	 * Diese ist ein {@code long} im Format {@code 0[1]:N[1]:Y[8]:M[4]:D[17]:h[21]:m[6]:s[6]} mit<br>
	 * <code>N = {@link #signValue()}</code>, <br>
	 * <code>Y = {@link #yearsValue()}</code>, <br>
	 * <code>M = {@link #monthsValue()}</code>, <br>
	 * <code>D = {@link #daysValue()}</code>, <br>
	 * <code>h = {@link #hoursValue()}</code>, <br>
	 * <code>m = {@link #minutesValue()}</code> und<br>
	 * <code>s = {@link #secondsValue()}</code>.
	 * 
	 * @return interne Darstellung der Zeitspanne.
	 */
	public final long value() {
		return ((long)this.valueH << 32) | ((long)valueL << 0);
	}

	/**
	 * Diese Methode gibt das Vorzeichen der Zeitspanne zurück.<br>
	 * Dieses ist {@code -1}, {@code 0} oder {@code 1}, wenn alle Komponenten der Zeitspanne kleiner, gleich bzw. größer {@code 0} sind.
	 * 
	 * @return Vorzeichen.
	 */
	public final int signValue() {
		final long value = this.value;
		return (value & (1L << 62)) != 0 ? -1 : value != 0 ? +1 : 0;
	}

	/**
	 * Diese Methode gibt die Anzahl der Jahre zurück.
	 * 
	 * @return Anzahl der Jahre (0..255).
	 */
	public final int yearsValue() {
		return (int)(this.value >> 54) & 255;
	}

	/**
	 * Diese Methode gibt die Anzahl der Monate zurück.
	 * 
	 * @return Anzahl der Monate (0..11).
	 */
	public final int monthsValue() {
		return (int)(this.value >> 50) & 15;
	}

	/**
	 * Diese Methode gibt die Anzahl der Tage zurück.
	 * 
	 * @return Anzahl der Tage ({@code 0..131071}).
	 */
	public final int daysValue() {
		return (int)(this.value >> 33) & 131071;
	}

	/**
	 * Diese Methode gibt die Anzahl der Stunden zurück.
	 * 
	 * @return Anzahl der Stunde ({@code 0..2097151}).
	 */
	public final int hoursValue() {
		return (int)(this.value >> 12) & 2097151;
	}

	/**
	 * Diese Methode gibt die Anzahl der Minuten zurück.
	 * 
	 * @return Anzahl der Minuten ({@code 0..59}).
	 */
	public final int minutesValue() {
		return (int)(this.value >> 6) & 63;
	}

	/**
	 * Diese Methode gibt die Anzahl der Sekunden zurück.
	 * 
	 * @return Anzahl der Sekunden ({@code 0..59}).
	 */
	public final int secondsValue() {
		return (int)(this.value >> 0) & 63;
	}

	public final int millisValue() {
		return (int)(this.value >> 0) & 63;
	}

	/**
	 * Diese Methode gibt diese Zeitspanne in entgegen gesetzter Richtung zurück.
	 * 
	 * @see #signValue()
	 * @return Zeitspanne in entgegen gesetzter Richtung.
	 */
	public FEMDuration negate() {
		if (this.value == 0) return this;
		return new FEMDuration(this.value ^ (1L << 62));
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitspanne gleich der gegebenen ist.
	 * 
	 * @param value Zeitspanne.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public boolean equals(final FEMDuration value) {
		return (this.signValue() == value.signValue()) && (this.totalMonths() == value.totalMonths()) && (this.totalSeconds() == value.totalSeconds());
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn diese Zeitspanne kürzer, gleich bzw. länger als die gegebene Zeitspanne
	 * ist. Wenn die Zeitspannen nicht vergleichbar sind, wird {@code undefined} geliefert.
	 * 
	 * @param value Zeitspanne.
	 * @param undefined Rückgabewert für nicht vergleichbare Zeitspannen.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public int compare(final FEMDuration value, final int undefined) {
		final int result = this.signValue() - value.signValue();
		if (result != 0) return result;
		// 28 Tage = 2419200 Sekunden
		final int totalMonths1 = this.totalMonths(), totalMonths2 = value.totalMonths();
		final long totalSeconds1 = this.totalSeconds(), totalSeconds2 = value.totalSeconds();
		if (totalMonths1 == totalMonths2) return Comparators.compare(totalSeconds1, totalSeconds2);
		if ((totalSeconds1 < 2419200L) && (totalSeconds2 < 2419200L)) //
			return Comparators.compare((totalMonths1 * 2419200L) + totalSeconds1, (totalMonths2 * 2419200L) + totalSeconds2);
		return undefined;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final long value = this.value;
		return (int)((value >> 32) ^ value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDuration)) return false;
		return this.equals((FEMDuration)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEE.formatDuration(this);
	}

}
