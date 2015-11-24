package bee.creative.fem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Diese Klasse implementiert ein Datum im Gregorianischen Kalender (ab {@code 1582-10-15}) mit Jahr, Monat, Tag und Wochentag.
 * 
 * @author Sebastian Rostock 2011.
 */
public class FEE_Date {

	/**
	 * Diese Methode erzeugt aus dem gegebenen {@link Date} ein neues Datum und gibt dieses zurück.
	 * 
	 * @param date {@link Date}.
	 * @return Datum.
	 * @throws NullPointerException Wenn {@code date} {@code null} ist.
	 */
	public static FEE_Date valueOf(final Date date) throws NullPointerException {
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return FEE_Date.valueOf(calendar);
	}

	/**
	 * Diese Methode erzeugt aus dem gegebenen {@link Calendar} ein neues Datum und gibt dieses zurück.
	 * 
	 * @param calendar {@link Calendar}.
	 * @return Datum.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 */
	public static FEE_Date valueOf(final Calendar calendar) throws NullPointerException {
		return new FEE_Date(FEMDatetime.compileDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE)));
	}

	/**
	 * Diese Methode erzeugt ein neues Datum mit den gegebenen Komponenten und gibt dieses zurück.
	 * 
	 * @param year Jahr ({@code 1582..524287}).
	 * @param month Monat ({@code 1..12}).
	 * @param date Tag ({@code 1..31}).
	 * @return Datum.
	 * @throws IllegalArgumentException Wenn einer der Parameter ungültig ist.
	 */
	public static FEE_Date valueOf(final int year, final int month, final int date) throws IllegalArgumentException {
		return new FEE_Date(FEMDatetime.compileDate(year, month, date));
	}

	/**
	 * Diese Methode gibt die Anzahl an Sekunden zurück, um die das Bis-Datum später als das Von-Datum ist.
	 * 
	 * @param from Von-Datum.
	 * @param to Bis-Datum.
	 * @return Anzahl an Sekunden mit Vorzeichen.
	 */
	public static long secondsBetween(final FEE_Date from, final FEE_Date to) {
		return to.totalSeconds() - from.totalSeconds();
	}

	{
	}

	{
	}

 

	/**
	 * Diese Methode gibt die Anzahl an Sekunden seit dem Beginn des Gregorianischen Kalenders zurück.
	 * 
	 * @return Anzahl der Sekunden seit {@code 15.10.1582 00:00:00}.
	 */
	final long totalSeconds() {
		return FEMDatetime.daysInCalendar(this.yearValue(), this.monthValue(), this.dateValue()) * 86400L;
	}

	{
	}
 
	/**
	 * Diese Methode gibt dieses Datum um die gegebenen Zeitspannen verschoben zurück.
	 * 
	 * @see FEMDatetime#move(FEMDuration)
	 * @param value Zeitspanne.
	 * @return verschobenes Datum.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public FEE_Date move(final FEMDuration value) throws NullPointerException {
		if (value.signValue() == 0) return this;
		return new FEMDatetime(this.value, 1024).move(value).datePart();
	}

	/**
	 * Diese Methode gibt dieses Datum um die gegebenen Zeitspannen verschoben zurück.
	 * <p>
	 * Algorithmus gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#adding-durations-to-dateTimes">XML Schema Part 2: §E
	 * Adding durations to dateTimes</a>.
	 * 
	 * @see FEMDatetime#move(int, int, int, int, int, int)
	 * @param years Verschiebung der Jahre.
	 * @param months Verschiebung der Monate.
	 * @param days Verschiebung der Tage.
	 * @return verschobenes Datum.
	 */
	public FEE_Date move(final int years, final int months, final int days) {
		if ((years == 0) && (months == 0) && (days == 0)) return this;
		return new FEMDatetime(this.value, 1024).move(years, months, days, 0, 0, 0).datePart();
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieses Datum gleich dem gegebenen ist.
	 * 
	 * @param value Datum.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final boolean equals(final FEE_Date value) throws NullPointerException {
		return this.value == value.value;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn dieses Datum früher, gleich oder später als
	 * das gegebene Datum ist.
	 * 
	 * @param value Datum.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEE_Date value) throws NullPointerException {
		return this.value - value.value;
	}

	{
	}
  
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEE.formatDate(this);
	}

	/**
	 * Diese Methode gibt das Datum als {@link Calendar} zurück.
	 * 
	 * @return {@link Calendar}.
	 */
	public GregorianCalendar toCalendar() {
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.clear();
		calendar.set(Calendar.YEAR, this.yearValue());
		calendar.set(Calendar.MONTH, (this.monthValue() - 1));
		calendar.set(Calendar.DATE, this.dateValue());
		return calendar;
	}

}