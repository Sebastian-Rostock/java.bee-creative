package bee.creative.fem;

import java.util.Calendar;
import java.util.GregorianCalendar;
import bee.creative.util.Comparators;
import bee.creative.util.Integers;

/** Diese Klasse implementiert eine Zeitangabe mit Datum, Uhrzeit und/oder Zeitzone im Gregorianischen Kalender.<br>
 * Intern wird die Zeitangabe als ein {@code long} dargestellt.
 * <p>
 * Das <u>Datum</u> kann unspezifiziert sein oder aus Jahr, Monat sowie Tag bestehen und im Bereich {@code 1582-10-15..9999-12-31} liegen. Die <u>Uhrzeit</u>
 * kann unspezifiziert sein oder aus Stunde, Minute, Sekunde sowie Millisekunde bestehen und im Bereich {@code 00:00:00.000..24:00:00.000} liegen. Die
 * <u>Zeitzone</u> kann unspezifiziert sein oder aus Stunde sowie Minute bestehen und im Bereich {@code -14:00..+14:00} liegen.
 * </p>
 * <h5><a name="year">Jahr</a></h5>
 * <p>
 * Der Zahlenwert für das {@link #yearValue() Jahr} entspricht der Anzahl der Jahre seit dem Beginn des Gregorianischen Kalenders erhöht um {@code 1582}.
 * Unterstützte Zahlenwerte für das Jahr sind {@code 1582..9999}.<br>
 * Ein reguläres Jahr hat 365 Tage, ein {@link #leapOf(int) Schaltjahr} hat 366 Tage.
 * </p>
 * <h5><a name="month">Monat</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #monthValue() Monat} entspricht der Anzahl der Monate seit Beginn des Jahres erhöht um {@code 1}. Unterstützte Zahlenwerte für
 * den Monat sind {@code 1..12}.
 * <ol>
 * <li>Januar = {@link Calendar#JANUARY} + 1</li>
 * <li>Februar = {@link Calendar#FEBRUARY} + 1</li>
 * <li>März = {@link Calendar#MARCH} + 1</li>
 * <li>April = {@link Calendar#APRIL} + 1</li>
 * <li>Mai = {@link Calendar#MAY} + 1</li>
 * <li>Juni = {@link Calendar#JUNE} + 1</li>
 * <li>Juli = {@link Calendar#JULY} + 1</li>
 * <li>August = {@link Calendar#AUGUST} + 1</li>
 * <li>September = {@link Calendar#SEPTEMBER} + 1</li>
 * <li>Oktober = {@link Calendar#OCTOBER} + 1</li>
 * <li>November = {@link Calendar#NOVEMBER} + 1</li>
 * <li>Dezember = {@link Calendar#DECEMBER} + 1</li>
 * </ol>
 * </p>
 * <h5><a name="date">Tag (Tag in einem Monat)</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #dateValue() Tag} entspricht der Anzahl der Tage seit Beginn des Monats erhöht um {@code 1}. Unterstützte Zahlenwerte für den
 * Monat sind {@code 1..31}, wobei einige Monate auch abhängig von Schaltjahren geringere {@link #lengthOf(int, boolean) Obergrenzen} besitzen.
 * </p>
 * <h5><a name="yearday">Jahrestag (Tag in einem Jahr)</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #yeardayValue() Jahrestag} entspricht der Anzahl der Tage seit dem Beginn des Jahres erhöht um {@code 1}. Unterstützte
 * Zahlenwerte für den Jahrestag sind {@code 1..366}.
 * </p>
 * <h5><a name="weekday">Wochentag (Tag in einer Woche)</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #weekdayValue() Wochentag} entspricht der Anzahl der Tage seit Beginn der Woche erhöht um {@code 1}. Unterstützte Zahlenwerte
 * für den Wochentag sind {@code 1..7}.
 * <ol>
 * <li>Sonntag = {@link Calendar#SUNDAY}</li>
 * <li>Montag = {@link Calendar#MONDAY}</li>
 * <li>Dienstag = {@link Calendar#TUESDAY}</li>
 * <li>Mittwoch = {@link Calendar#WEDNESDAY}</li>
 * <li>Donnerstag = {@link Calendar#THURSDAY}</li>
 * <li>Freitag = {@link Calendar#FRIDAY}</li>
 * <li>Samstag = {@link Calendar#SATURDAY}</li>
 * </ol>
 * </p>
 * <h5><a name="calendarday">Kalendertag (Tag im Kalender)</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #calendardayValue() Kalendertag} entspricht der Anzahl der Tage seit dem Beginn des Gregorianischen Kalenders am Freitag dem
 * {@code 1582-10-15}. Unterstützte Zahlenwerte für den Kalendertag sind {@code 0..3074323}.
 * </p>
 * <h5><a name="daymillis">Tagesmillis (Millisekunden am Tag)</a></h5>
 * <p>
 * Der Zahlenwert für die {@link #daymillisValue() Tagesmillis} entspricht der Anzahl der Millisekunden seit {@code 00:00:00.000}. Unterstützte Zahlenwerte für
 * die Tagesmillis sind {@code 0..86400000}.
 * </p>
 * <h5><a name="datum">Datum</a></h5>
 * <p>
 * Das Datum einer Zeitangabe kann entweder als Kalendertag oder als Kombination von Jahr, Monat und Tag angegeben werden.
 * </p>
 * <h5><a name="uhrzeit">Uhrzeit</a></h5>
 * <p>
 * Die Uhrzeit einer Zeitangabe kann als Tagesmillis oder als Kombination von Stunden, Minuten, Sekunden und Millisekunden angegeben werden. Unterstützte
 * Zahlenwerte für Stunden, Minuten, Sekunden und Millisekunden sind «0…24», «0…59», «0…59» bzw. «0…999».
 * </p>
 * <h5><a name="zone">Zeitzone (Zeitzonenverschiebung)</a></h5>
 * <p>
 * Der Zahlenwert für die {@link #zoneValue() Zeitzone} entspricht der Zeitzonenverschiebung gegenüber UTC in Minuten. Unterstützte Zahlenwerte für die Zeitzone
 * sind {@code -840..840}.
 * </p>
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDatetime extends FEMValue implements Comparable<FEMDatetime> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 9;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMDatetime> TYPE = FEMType.from(FEMDatetime.ID);

	/** Dieses Feld speichert die leere Zeitangabe ohne Datum, ohne Uhrzeit und ohne Zeitzone. */
	public static final FEMDatetime EMPTY = new FEMDatetime(0, 1073741824);

	/** Dieses Feld speichert die früheste darstellbare Zeitangabe. */
	public static final FEMDatetime MINIMUM = new FEMDatetime(414875651, -192446464);

	/** Dieses Feld speichert die späteste darstellbare Zeitangabe. */
	public static final FEMDatetime MAXIMUM = new FEMDatetime(-1673592829, -1953505280);

	{}

	/** Diese Methode gibt den aktuellen Zeitpunkt zurück.
	 *
	 * @return aktueller Zeitpunkt.
	 * @throws IllegalArgumentException Wenn {@link #from(Calendar)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime now() throws IllegalArgumentException {
		return FEMDatetime.from(System.currentTimeMillis());
	}

	/** Diese Methode gibt einen Zeitpunkt zurück, der die gegebene Anzahl an Millisekunden nach dem Zeitpunkt {@code 1970-01-01T00:00:00Z} liegt.
	 *
	 * @see #toTime()
	 * @see Calendar#setTimeInMillis(long)
	 * @param millis Anzahl der Millisekunden.
	 * @return Zeitpunkt.
	 * @throws IllegalArgumentException Wenn {@link #from(Calendar)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime from(final long millis) throws IllegalArgumentException {
		return new FEMDatetime(516440067, -1073709056).move(0, millis);
	}

	/** Diese Methode gibt eine Zeitangabe mit den in der gegebenen Zeitangabe kodierten Komponenten zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString() Textdarstellung}.
	 *
	 * @see #withDate(int, int, int)
	 * @see #withTime(int, int, int, int)
	 * @see #withZone(int, int)
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @return Zeitangabe.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMDatetime from(final String string) throws NullPointerException, IllegalArgumentException {
		if (string.length() > 29) throw new IllegalArgumentException();
		final char[] buffer = string.toCharArray();
		switch (buffer.length) {
			case 0: //
				return FEMDatetime.EMPTY;
			case 1: // Z
				return FEMDatetime.fromZ1(FEMDatetime.EMPTY, buffer, 0);
			case 6: // +00:01
				return FEMDatetime.fromZ6(FEMDatetime.EMPTY, buffer, 0);
			case 8: // 00:00:01
				return FEMDatetime.fromT8(FEMDatetime.EMPTY, buffer, 0);
			case 9: // 00:00:01Z
				return FEMDatetime.fromZ1(FEMDatetime.fromT8(FEMDatetime.EMPTY, buffer, 0), buffer, 8);
			case 10: // 1582-10-16
				return FEMDatetime.fromD10(FEMDatetime.EMPTY, buffer, 0);
			case 11: // 1582-10-16Z
				return FEMDatetime.fromZ1(FEMDatetime.fromD10(FEMDatetime.EMPTY, buffer, 0), buffer, 10);
			case 12: // 00:00:00.001
				return FEMDatetime.fromT12(FEMDatetime.EMPTY, buffer, 0);
			case 13: // 00:00:00.001Z
				return FEMDatetime.fromZ1(FEMDatetime.fromT12(FEMDatetime.EMPTY, buffer, 0), buffer, 12);
			case 14: // 00:00:01+00:01
				return FEMDatetime.fromZ6(FEMDatetime.fromT8(FEMDatetime.EMPTY, buffer, 0), buffer, 8);
			case 16: // 1582-10-16+00:01
				return FEMDatetime.fromZ6(FEMDatetime.fromD10(FEMDatetime.EMPTY, buffer, 0), buffer, 10);
			case 18: // 00:00:00.001+00:01
				return FEMDatetime.fromZ6(FEMDatetime.fromT12(FEMDatetime.EMPTY, buffer, 0), buffer, 12);
			case 19: // 1582-10-16T00:00:01
				return FEMDatetime.fromT8(FEMDatetime.fromD11(FEMDatetime.EMPTY, buffer, 0), buffer, 11);
			case 20: // 1582-10-16T00:00:00Z
				return FEMDatetime.fromZ1(FEMDatetime.fromT8(FEMDatetime.fromD11(FEMDatetime.EMPTY, buffer, 0), buffer, 11), buffer, 19);
			case 23: // 1582-10-16T00:00:00.001
				return FEMDatetime.fromT12(FEMDatetime.fromD11(FEMDatetime.EMPTY, buffer, 0), buffer, 11);
			case 24: // 1582-10-16T00:00:00.001Z
				return FEMDatetime.fromZ1(FEMDatetime.fromT12(FEMDatetime.fromD11(FEMDatetime.EMPTY, buffer, 0), buffer, 11), buffer, 23);
			case 25: // 1582-10-16T00:00:00+00:01
				return FEMDatetime.fromZ6(FEMDatetime.fromT8(FEMDatetime.fromD11(FEMDatetime.EMPTY, buffer, 0), buffer, 11), buffer, 19);
			case 29: // 1582-10-16T00:00:00.001+00:01
				return FEMDatetime.fromZ6(FEMDatetime.fromT12(FEMDatetime.fromD11(FEMDatetime.EMPTY, buffer, 0), buffer, 11), buffer, 23);
			default:
				throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings ("javadoc")
	static int fromI2(final char[] buffer, final int offset) {
		if (Integers.integerSize(buffer, offset, 2) != 2) throw new IllegalArgumentException();
		return Integers.parseInt(buffer, offset, 2);
	}

	@SuppressWarnings ("javadoc")
	static FEMDatetime fromZ1(final FEMDatetime result, final char[] buffer, final int offset) {
		if (buffer[offset] != 'Z') throw new IllegalArgumentException();
		return result.withZone(0);
	}

	@SuppressWarnings ("javadoc")
	static FEMDatetime fromZ6(final FEMDatetime result, final char[] buffer, final int offset) {
		if (buffer[offset + 3] != ':') throw new IllegalArgumentException();
		final int sign = buffer[offset], hour = FEMDatetime.fromI2(buffer, offset + 1), minute = FEMDatetime.fromI2(buffer, offset + 4);
		if (sign == '-') return result.withZone(-hour, minute);
		if (sign == '+') return result.withZone(+hour, minute);
		throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static FEMDatetime fromT8(final FEMDatetime result, final char[] buffer, final int offset) {
		if ((buffer[offset + 2] != ':') || (buffer[offset + 5] != ':')) throw new IllegalArgumentException();
		final int hour = FEMDatetime.fromI2(buffer, offset + 0), minute = FEMDatetime.fromI2(buffer, offset + 3), second = FEMDatetime.fromI2(buffer, offset + 6);
		return result.withTime(hour, minute, second, 0);
	}

	@SuppressWarnings ("javadoc")
	static FEMDatetime fromT12(final FEMDatetime result, final char[] buffer, final int offset) {
		if ((buffer[offset + 2] != ':') || (buffer[offset + 5] != ':') || (buffer[offset + 8] != '.')) throw new IllegalArgumentException();
		if (Integers.integerSize(buffer, offset + 9, 3) != 3) throw new IllegalArgumentException();
		final int hour = FEMDatetime.fromI2(buffer, offset + 0), minute = FEMDatetime.fromI2(buffer, offset + 3), second = FEMDatetime.fromI2(buffer, offset + 6),
			millisecond = Integers.parseInt(buffer, offset + 9, 3);
		return result.withTime(hour, minute, second, millisecond);
	}

	@SuppressWarnings ("javadoc")
	static FEMDatetime fromD10(final FEMDatetime result, final char[] buffer, final int offset) {
		if ((buffer[offset + 4] != '-') || (buffer[offset + 7] != '-')) throw new IllegalArgumentException();
		if (Integers.integerSize(buffer, offset, 4) != 4) throw new IllegalArgumentException();
		final int year = Integers.parseInt(buffer, offset, 4), month = FEMDatetime.fromI2(buffer, offset + 5), date = FEMDatetime.fromI2(buffer, offset + 8);
		return result.withDate(year, month, date);
	}

	@SuppressWarnings ("javadoc")
	static FEMDatetime fromD11(final FEMDatetime result, final char[] buffer, final int offset) {
		if (buffer[offset + 10] != 'T') throw new IllegalArgumentException();
		return FEMDatetime.fromD10(result, buffer, offset);
	}

	/** Diese Methode gibt eine Zeitangabe mit dem Datum, der Uhrzeit und der Zeitzone des gegebenen {@link Calendar} zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withDate(calendar).withTime(calendar).withZone(calendar)}.
	 *
	 * @see #withDate(Calendar)
	 * @see #withTime(Calendar)
	 * @see #withZone(Calendar)
	 * @param calendar {@link Calendar}.
	 * @return Zeitangabe.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #withDate(Calendar)}, {@link #withTime(Calendar)} bzw. {@link #withZone(Calendar)} eine entsprechende Ausnahme
	 *         auslöst. */
	public static FEMDatetime from(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(calendar).withTime(calendar).withZone(calendar);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMDatetime.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Zeitangabe.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMDatetime from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMDatetime.TYPE);
	}

	/** Diese Methode gibt eine Zeitangabe mit dem Datum zum gegebenen Kalendertag zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withDate(calendarday)}.
	 *
	 * @see #withDate(int)
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn {@link #withDate(int)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime fromDate(final int calendarday) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(calendarday);
	}

	/** Diese Methode gibt eine Zeitangabe mit dem gegebenen Datum zurück und ist eine Abkürzung für {@code FEE_Datetime.EMPTY.withDate(year, month, date)}.
	 *
	 * @see #withDate(int, int, int)
	 * @param year Jahr ({@code 1582..9999}).
	 * @param month Monat ({@code 1..12}).
	 * @param date Tag ({@code 1..31}).
	 * @return Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn {@link #withDate(int, int, int)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime fromDate(final int year, final int month, final int date) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(year, month, date);
	}

	/** Diese Methode gibt eine Zeitangabe mit dem Datum der gegebenen zurück.<br>
	 * Wenn die gegebene Zeitangabe kein Datum {@link #hasDate() besitzt}, hat die gelieferte Zeitangabe auch kein Datum.
	 *
	 * @see #withDate(FEMDatetime)
	 * @param datetime Zeitangabe.
	 * @return Datum oder leere Zeitangabe.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist. */
	public static FEMDatetime fromDate(final FEMDatetime datetime) throws NullPointerException {
		return FEMDatetime.EMPTY.withDate(datetime);
	}

	/** Diese Methode gibt eine Zeitangabe mit der Uhrzeit zu den gegebenen Tagesmillis zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withTime(daymillis)}.
	 *
	 * @see #withTime(int)
	 * @see #daymillisOf(int, int, int, int)
	 * @param daymillis Tagesmillis ({@code 0..86400000}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@link #withTime(int)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime fromTime(final int daymillis) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withTime(daymillis);
	}

	/** Diese Methode gibt eine Zeitangabe mit der gegebenen Uhrzeit zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withTime(hour, minute, second, millisecond)}.
	 *
	 * @see #withTime(int, int, int, int)
	 * @param hour Stunde ({@code 0..24}).
	 * @param minute Minute ({@code 0..59}).
	 * @param second Sekunde ({@code 0..59}).
	 * @param millisecond Millisekunde ({@code 0..999}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@link #withTime(int, int, int, int)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime fromTime(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withTime(hour, minute, second, millisecond);
	}

	@SuppressWarnings ("javadoc")
	static void checkDate(final int calendarday) throws IllegalArgumentException {
		if ((calendarday < 0) || (calendarday > 3074323)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkDate(final int year, final int month, final int date) throws IllegalArgumentException {
		if (year != 1582) {
			FEMDatetime.checkYear(year);
			if ((month < 1) || (month > 12)) throw new IllegalArgumentException();
			if (date < 1) throw new IllegalArgumentException();
		} else if (month != 10) {
			if ((month < 10) || (month > 12)) throw new IllegalArgumentException();
			if (date < 1) throw new IllegalArgumentException();
		} else if (date < 15) throw new IllegalArgumentException();
		if (date > FEMDatetime.lengthOfImpl(month, year)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkTime(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		if (hour == 24) {
			if (minute != 0) throw new IllegalArgumentException();
			if (second != 0) throw new IllegalArgumentException();
			if (millisecond != 0) throw new IllegalArgumentException();
		} else {
			if ((hour < 0) || (hour > 23)) throw new IllegalArgumentException();
			if ((minute < 0) || (minute > 59)) throw new IllegalArgumentException();
			if ((second < 0) || (second > 59)) throw new IllegalArgumentException();
			if ((millisecond < 0) || (millisecond > 999)) throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings ("javadoc")
	static void checkYear(final int year) throws IllegalArgumentException {
		if ((year < 1582) || (year > 9999)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkZero(final int data, final int ignore) {
		if ((data & ~ignore) != 0) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkZone(final int zone) throws IllegalArgumentException {
		if ((zone < -840) || (zone > 840)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkDays(final int days) throws IllegalArgumentException {
		if (days > 3652424) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkYears(final int years) throws IllegalArgumentException {
		if (years > 8417) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkMonths(final int months) throws IllegalArgumentException {
		if (months > 101015) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkHours(final int hours) throws IllegalArgumentException {
		if (hours > 73783776) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void minutes(final long minutes) throws IllegalArgumentException {
		if (minutes > 4427026560L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkSeconds(final long seconds) throws IllegalArgumentException {
		if (seconds > 265621593600L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkMilliseconds(final long milliseconds) throws IllegalArgumentException {
		if (milliseconds > 265621593600000L) throw new IllegalArgumentException();
	}

	/** Diese Methode gibt eine Zeitangabe mit der Uhrzeit der gegebenen zurück.<br>
	 * Wenn die gegebene Zeitangabe keine Uhrzeit {@link #hasTime() besitzt}, hat die gelieferte Zeitangabe auch keine Uhrzeit.
	 *
	 * @see #withTime(FEMDatetime)
	 * @param datetime Zeitangabe.
	 * @return Uhrzeit oder leere Zeitangabe.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist. */
	public static FEMDatetime fromTime(final FEMDatetime datetime) throws NullPointerException {
		return FEMDatetime.EMPTY.withTime(datetime);
	}

	/** Diese Methode gibt eine Zeitangabe mit der gegebenen Zeitzone zurück und ist eine Abkürzung für {@code FEE_Datetime.EMPTY.withZone(zone)}.
	 *
	 * @see #withZone(int)
	 * @param zone Zeitzone ({@code -840..840})
	 * @return Zeitangabe mit Zeitzone.
	 * @throws IllegalArgumentException Wenn {@link #withZone(int)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime fromZone(final int zone) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withZone(zone);
	}

	/** Diese Methode gibt eine Zeitangabe mit der gegebenen Zeitzone zurück und ist eine Abkürzung für {@code FEE_Datetime.EMPTY.withZone(zoneHour, zoneMinute)}.
	 *
	 * @see #withZone(int, int)
	 * @param zoneHour Stunde der Zeitzone ({@code -14..14}).
	 * @param zoneMinute Minute der Zeitzone ({@code 0..59}).
	 * @return Zeitangabe mit Zeitzone.
	 * @throws IllegalArgumentException Wenn {@link #withZone(int, int)} eine entsprechende Ausnahme auslöst. */
	public static FEMDatetime fromZone(final int zoneHour, final int zoneMinute) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withZone(zoneHour, zoneMinute);
	}

	/** Diese Methode gibt eine Zeitangabe mit der Zeitzone der gegebenen zurück.<br>
	 * Wenn die gegebene Zeitangabe keine Zeitzone {@link #hasTime() besitzt}, hat die gelieferte Zeitangabe auch keine Zeitzone.
	 *
	 * @see #withZone(FEMDatetime)
	 * @param datetime Zeitangabe.
	 * @return Zeitzone oder leere Zeitangabe.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist. */
	public static FEMDatetime fromZone(final FEMDatetime datetime) throws NullPointerException {
		return FEMDatetime.EMPTY.withZone(datetime);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Jahr ein Schaltjahr im Gregorianischen Kalender ist.<br>
	 * Ein Schaltjahr hat 366 Tage, ein reguläres hat dagegen 365 Tage.
	 *
	 * @param year Jahr ({@code 1582..9999}).
	 * @return {@code true} bei einem Schaltjahr.
	 * @throws IllegalArgumentException Wenn {@code year} ungültig ist. */
	public static boolean leapOf(final int year) throws IllegalArgumentException {
		FEMDatetime.checkYear(year);
		return FEMDatetime.leapOfImpl(year);
	}

	@SuppressWarnings ("javadoc")
	static boolean leapOfImpl(final int year) {
		final int div = year / 100, mod = year % 100;
		return ((mod != 0 ? mod : div) & 3) == 0;
	}

	/** Diese Methode gibt Jahr, Monat und Tag zum gegebenen Kalendertag zurück.<br>
	 * Die 32 Bit des Rückgabewerts sind von MBS zum LSB:
	 * <ul>
	 * <li>EMPTY - 8 Bit</li>
	 * <li>Year - 14 Bit</li>
	 * <li>Month - 5 Bit</li>
	 * <li>Date - 5 Bit</li>
	 * </ul>
	 *
	 * @param calendarday Kalendertag.
	 * @return Jahr, Monat und Tag. */
	static int dateOf(final int calendarday) {
		final int months = (int)(((calendarday + 139824) * 400 * 12L) / 146097);
		final int div = months / 12, mod = months % 12;
		int year = div + 1200, month = mod + 1, date = (calendarday - FEMDatetime.calendardayOfImpl(year, month, 1)) + 1;
		if (date <= 0) {
			if (month == 1) {
				year--;
				month = 12;
			} else {
				month--;
			}
			date += FEMDatetime.lengthOfImpl(month, year);
		}
		return (year << 10) | (month << 5) | (date << 0);
	}

	/** Diese Methode gibt die Länge des gegebenen Monats im gegebenen Jahr zurück, d.h. die Anzahl der Tage im Monat.
	 *
	 * @see #leapOf(int)
	 * @see #lengthOf(int, boolean)
	 * @param month Monat ({@code 1..12}).
	 * @param year Jahr ({@code 1582..9999}).
	 * @return Länge des Monats ({@code 1..31}).
	 * @throws IllegalArgumentException Wenn {@code month} bzw. {@code year} ungültig ist. */
	public static int lengthOf(final int month, final int year) throws IllegalArgumentException {
		if (year != 1582) return FEMDatetime.lengthOf(month, FEMDatetime.leapOf(year));
		if ((month < 10) || (month > 12)) throw new IllegalArgumentException();
		return FEMDatetime.lengthOfImpl(month, FEMDatetime.leapOf(year));
	}

	@SuppressWarnings ("javadoc")
	static int lengthOfImpl(final int month, final int year) {
		return FEMDatetime.lengthOfImpl(month, FEMDatetime.leapOfImpl(year));
	}

	/** Diese Methode gibt die Länge des gegebenen Monats zurück, d.h. die Anzahl der Tage im Monat.
	 *
	 * @see #leapOf(int)
	 * @param month Monat ({@code 1..12}).
	 * @param leap {@code true} in einem Schaltjahr mit 366 Tagen.
	 * @return Länge des Monats ({@code 1..31}).
	 * @throws IllegalArgumentException Wenn {@code month} ungültig ist. */
	public static int lengthOf(final int month, final boolean leap) throws IllegalArgumentException {
		if ((month < 1) || (month > 12)) throw new IllegalArgumentException();
		return FEMDatetime.lengthOfImpl(month, leap);
	}

	@SuppressWarnings ("javadoc")
	static int lengthOfImpl(final int month, final boolean isLeap) {
		return 28 + (((isLeap ? 62648028 : 62648012) >> (month << 1)) & 3);
	}

	/** Diese Methode gibt den Jahrestag zum gegebenen {@link #calendardayOf(int, int, int) Kalendertag} zurück.
	 *
	 * @see #calendardayOf(int, int, int)
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return Jahrestag ({@code 1..366}).
	 * @throws IllegalArgumentException Wenn {@code calendarday} ungültig ist. */
	public static int yeardayOf(final int calendarday) throws IllegalArgumentException {
		FEMDatetime.checkDate(calendarday);
		return FEMDatetime.yeardayOfImpl(calendarday);
	}

	@SuppressWarnings ("javadoc")
	static int yeardayOfImpl(final int calendarday) {
		final int year = (((calendarday + 139810) * 400) / 146097) + 1200;
		final int result = (calendarday - FEMDatetime.calendardayOfImpl(year, 1, 1)) + 1;
		if (result == 0) return FEMDatetime.leapOfImpl(year - 1) ? 366 : 365;
		if (result == 366) return FEMDatetime.leapOfImpl(year) ? 366 : 1;
		return result;
	}

	/** Diese Methode gibt den Wochentag zum gegebenen {@link #calendardayOf(int, int, int) Kalendertag} zurück.
	 *
	 * @see #calendardayOf(int, int, int)
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return Wochentag ({@code 1..7}).
	 * @throws IllegalArgumentException Wenn {@code calendarday} ungültig ist. */
	public static int weekdayOf(final int calendarday) throws IllegalArgumentException {
		FEMDatetime.checkDate(calendarday);
		return FEMDatetime.weekdayOfImpl(calendarday);
	}

	@SuppressWarnings ("javadoc")
	static int weekdayOfImpl(final int calendarday) {
		return ((calendarday + 5) % 7) + 1;
	}

	/** Diese Methode gibt die Tagesmillis zur gegebenen Uhrzeit zurück.
	 *
	 * @param hour Stunde ({@code 0..24}).
	 * @param minute Minute ({@code 0..59}).
	 * @param second Sekunde ({@code 0..59}).
	 * @param millisecond Millisekunde ({@code 0..999}).
	 * @return Anzahl der Millisekunden zwischen {@code 00:00:00.000} und der gegebenen Uhrzeit ({@code 0..86400000}).
	 * @throws IllegalArgumentException Wenn die gegebene Uhrzeit ungültig ist. */
	public static int daymillisOf(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		FEMDatetime.checkTime(hour, minute, second, millisecond);
		return FEMDatetime.daymillisOfImpl(hour, minute, second, millisecond);
	}

	@SuppressWarnings ("javadoc")
	static int daymillisOfImpl(final int hour, final int minute, final int second, final int millisecond) {
		return (hour * 3600000) + (minute * 60000) + (second * 1000) + millisecond;
	}

	/** Diese Methode gibt den Kalendertag zum gegebenen Datum zurück.
	 *
	 * @param year Jahr des Datums ({@code 1582..9999}).
	 * @param month Monat des Jahres ({@code 1..12}).
	 * @param date Tag des Monats ({@code 1..31}).
	 * @return Anzahl der Tage zwischen dem {@code 15.10.1582} und dem gegebenen Datum ({@code 0..3074323}).
	 * @throws IllegalArgumentException Wenn das gegebene Datum ungültig ist. */
	public static int calendardayOf(final int year, final int month, final int date) throws IllegalArgumentException {
		FEMDatetime.checkDate(year, month, date);
		return FEMDatetime.calendardayOfImpl(year, month, date);
	}

	@SuppressWarnings ("javadoc")
	static int calendardayOfImpl(final int year, final int month, final int date) {
		final int year2 = (year - ((7 >> month) & 1)) >> 2, year3 = year2 / 25, month2 = month << 1;
		final int month3 = (month * 29) + ((59630432 >> month2) & 3) + ((266948608 >> month2) & 12);
		return ((((year * 365) + year2) - year3) + (year3 >> 2) + month3 + date) - 578130;
	}

	{}

	/** Dieses Feld speichert die 32 LSB der internen 64 Bit Darstellung dieser Zeitangabe.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>hasZone - 1 Bit</li>
	 * <li>zoneValue - 11 Bit</li>
	 * <li>dateValue - 5 Bit</li>
	 * <li>hourValue - 5 Bit</li>
	 * <li>millisecondValue - 10 Bit</li>
	 * </ul>
	*/
	final int valueL;

	/** Dieses Feld speichert die 32 MSB der internen 64 Bit Darstellung dieser Zeitangabe.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>yearValue - 14 Bit</li>
	 * <li>monthValue - 4 Bit</li>
	 * <li>minuteValue - 6 Bit</li>
	 * <li>secondValue - 6 Bit</li>
	 * <li>hasDate - 1 Bit</li>
	 * <li>hasTime - 1 Bit</li>
	 * </ul>
	*/
	final int valueH;

	/** Dieser Konstruktor initialisiert die interne Darstellung.
	 *
	 * @see #value()
	 * @param value interne Darstellung.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist. */
	public FEMDatetime(final long value) throws IllegalArgumentException {
		this((int)(value >> 32), (int)(value >> 0));
		if (this.hasDate()) {
			FEMDatetime.checkDate(this.yearValueImpl(), this.monthValueImpl(), this.dateValueImpl());
		} else {
			FEMDatetime.checkZero(this.valueH, 0x3FFD);
			FEMDatetime.checkZero(this.valueL, 0xFFF07FFF);
		}
		if (this.hasTime()) {
			FEMDatetime.checkTime(this.hourValueImpl(), this.minuteValueImpl(), this.secondValueImpl(), this.millisecondValueImpl());
		} else {
			FEMDatetime.checkZero(this.valueH, 0xFFFFC002);
			FEMDatetime.checkZero(this.valueL, 0xFFFF8000);
		}
		if (this.hasZone()) {
			FEMDatetime.checkZone(this.zoneValueImpl());
		} else {
			FEMDatetime.checkZero(this.zoneValueImpl(), 0);
		}
	}

	@SuppressWarnings ("javadoc")
	FEMDatetime(final int valueH, final int valueL) {
		this.valueH = valueH;
		this.valueL = valueL;
	}

	{}

	/** Diese Methode gibt die interne Darstellung der Zeitangabe zurück.
	 * <p>
	 * Die 64 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>yearValue - 14 Bit</li>
	 * <li>monthValue - 4 Bit</li>
	 * <li>minuteValue - 6 Bit</li>
	 * <li>secondValue - 6 Bit</li>
	 * <li>hasDate - 1 Bit</li>
	 * <li>hasTime - 1 Bit</li>
	 * <li>hasZone - 1 Bit</li>
	 * <li>zoneValue - 11 Bit</li>
	 * <li>dateValue - 5 Bit</li>
	 * <li>hourValue - 5 Bit</li>
	 * <li>millisecondValue - 10 Bit</li>
	 * </ul>
	 *
	 * @return interne Darstellung der Zeitangabe. */
	public final long value() {
		return (((long)this.valueH) << 32) | (((long)this.valueL) << 0);
	}

	/** Diese Methode gibt das Jahr zurück.
	 *
	 * @return Jahr ({@code 1582..9999}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int yearValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.yearValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int yearValueImpl() {
		return (this.valueH >> 18) & 0x3FFF;
	}

	/** Diese Methode gibt den Tag zurück.
	 *
	 * @return Tag ({@code 1..31}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int dateValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.dateValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int dateValueImpl() {
		return (this.valueL >> 15) & 0x1F;
	}

	/** Diese Methode gibt den Monat zurück.
	 *
	 * @return Monat ({@code 1..12}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int monthValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.monthValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int monthValueImpl() {
		return (this.valueH >> 14) & 0x0F;
	}

	/** Diese Methode gibt die Stunde zurück.
	 *
	 * @return Stunde ({@code 0..24}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt. */
	public final int hourValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.hourValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int hourValueImpl() {
		return (this.valueL >> 10) & 0x1F;
	}

	/** Diese Methode gibt die Minute zurück.
	 *
	 * @return Minute ({@code 0..59}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt. */
	public final int minuteValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.minuteValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int minuteValueImpl() {
		return (this.valueH >> 8) & 0x3F;
	}

	/** Diese Methode gibt die Sekunde zurück.
	 *
	 * @return Sekunde ({@code 0..59}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt. */
	public final int secondValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.secondValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int secondValueImpl() {
		return (this.valueH >> 2) & 0x3F;
	}

	/** Diese Methode gibt die Millisekunde zurück.
	 *
	 * @return Millisekunde ({@code 0..999}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt. */
	public final int millisecondValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.millisecondValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int millisecondValueImpl() {
		return (this.valueL >> 0) & 0x03FF;
	}

	/** Diese Methode gibt die Zeitzonenverschiebung zur UTC in Minuten zurück.
	 *
	 * @return Zeitzonenverschiebung in Minuten ({@code -840..840}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() keine Zeitzone} besitzt. */
	public final int zoneValue() throws IllegalStateException {
		if (!this.hasZone()) throw new IllegalStateException();
		return this.zoneValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int zoneValueImpl() {
		return ((this.valueL >> 20) & 0x07FF) - 1024;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe ein Datum besitzt.
	 *
	 * @return {@code true}, wenn diese Zeitangabe ein Datum besitzt. */
	public final boolean hasDate() {
		return (this.valueH & 0x02) != 0;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe eine Uhrzeit besitzt.
	 *
	 * @return {@code true}, wenn diese Zeitangabe eine Uhrzeit besitzt. */
	public final boolean hasTime() {
		return (this.valueH & 0x01) != 0;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe eine Zeitzone besitzt.
	 *
	 * @return {@code true}, wenn diese Zeitangabe eine Zeitzone besitzt. */
	public final boolean hasZone() {
		return (this.valueL & 0x80000000) != 0;
	}

	/** Diese Methode gibt den Jahrestag zurück.
	 *
	 * @see #yeardayOf(int)
	 * @return Jahrestag ({@code 1..366}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int yeardayValue() throws IllegalStateException {
		return FEMDatetime.yeardayOfImpl(this.calendardayValue());
	}

	/** Diese Methode gibt den Wochentag zurück.
	 *
	 * @see #weekdayOf(int)
	 * @return Wochentag ({@code 1..7}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int weekdayValue() throws IllegalStateException {
		return FEMDatetime.weekdayOfImpl(this.calendardayValue());
	}

	/** Diese Methode gibt die Tagesmillis zurück.
	 *
	 * @see #daymillisOf(int, int, int, int)
	 * @return Tagesmillis ({@code 0..86400000}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int daymillisValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.daymillisValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int daymillisValueImpl() {
		return FEMDatetime.daymillisOfImpl(this.hourValueImpl(), this.minuteValueImpl(), this.secondValueImpl(), this.millisecondValueImpl());
	}

	/** Diese Methode gibt den Kalendertag zurück.
	 *
	 * @see #calendardayOf(int, int, int)
	 * @return Kalendertag ({@code 0..3074323}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final int calendardayValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.calendardayValueImpl();
	}

	@SuppressWarnings ("javadoc")
	final int calendardayValueImpl() {
		return FEMDatetime.calendardayOfImpl(this.yearValueImpl(), this.monthValueImpl(), this.dateValueImpl());
	}

	/** Diese Methode gibt diese Zeitangabe mit dem Datum zum gegebenen Kalendertag zurück.
	 *
	 * @see #calendardayOf(int, int, int)
	 * @see #withoutDate()
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return diese Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn {@code calendarday} ungültig ist. */
	public final FEMDatetime withDate(final int calendarday) throws IllegalArgumentException {
		FEMDatetime.checkDate(calendarday);
		final int date = FEMDatetime.dateOf(calendarday);
		return this.withDateImpl((date >> 10) & 0x3FFF, (date >> 5) & 0x1F, (date >> 0) & 0x1F);
	}

	/** Diese Methode gibt diese Zeitangabe mit dem gegebenen Datum zurück.
	 *
	 * @see #withoutDate()
	 * @param year Jahr ({@code 1582..9999}).
	 * @param month Monat ({@code 1..12}).
	 * @param date Tag ({@code 1..31}).
	 * @return diese Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn das gegebene Datum ungültig ist. */
	public final FEMDatetime withDate(final int year, final int month, final int date) throws IllegalArgumentException {
		FEMDatetime.checkDate(year, month, date);
		return this.withDateImpl(year, month, date);
	}

	/** Diese Methode gibt diese Zeitangabe mit dem Datum des gegebenen {@link Calendar} zurück.<br>
	 * Die gelieferte Zeitangabe {@link #hasDate() besitzt} nur dann ein Datum, wenn am gegebenen {@link Calendar} die Felder {@link Calendar#YEAR},
	 * {@link Calendar#MONTH} und {@link Calendar#DATE} definiert sind. Andernfalls hat die gelieferte Zeitangabe kein Datum.
	 *
	 * @see #withoutDate()
	 * @param calendar Datum.
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Datum ungültig ist. */
	public final FEMDatetime withDate(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		if (!calendar.isSet(Calendar.YEAR) || !calendar.isSet(Calendar.MONTH) || !calendar.isSet(Calendar.DATE)) return this.withoutDate();
		return this.withDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
	}

	/** Diese Methode gibt diese Zeitangabe mit dem Datum der gegebenen Zeitangabe zurück.<br>
	 * Wenn die gegebene Zeitangabe kein Datum {@link #hasDate() besitzt}, hat die gelieferte Zeitangabe auch kein Datum.
	 *
	 * @see #withoutDate()
	 * @param datetime Datum.
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist. */
	public final FEMDatetime withDate(final FEMDatetime datetime) throws NullPointerException {
		if (!datetime.hasDate()) return this.withoutDate();
		return this.withDateImpl(datetime.yearValueImpl(), datetime.monthValueImpl(), datetime.dateValueImpl());
	}

	@SuppressWarnings ("javadoc")
	final FEMDatetime withDateImpl(final int year, final int month, final int date) {
		return new FEMDatetime( //
			(this.valueH & 0x3FFD) | (year << 18) | (month << 14) | (1 << 1), //
			(this.valueL & 0xFFF07FFF) | (date << 15));
	}

	/** Diese Methode gibt diese Zeitangabe mit der Uhrzeit zu den gegebenen Tagesmillis zurück.
	 *
	 * @see #daymillisOf(int, int, int, int)
	 * @see #withoutTime()
	 * @param daymillis Tagesmillis ({@code 0..86400000}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@code daymillis} ungültig ist. */
	public final FEMDatetime withTime(final int daymillis) throws IllegalArgumentException {
		if ((daymillis < 0) || (daymillis > 86400000)) throw new IllegalArgumentException();
		return this.withTimeImpl(daymillis);
	}

	/** Diese Methode gibt diese Zeitangabe mit der gegebenen Uhrzeit zurück.
	 *
	 * @see #withoutTime()
	 * @param hour Stunde ({@code 0..24}).
	 * @param minute Minute ({@code 0..59}).
	 * @param second Sekunde ({@code 0..59}).
	 * @param millisecond Millisekunde ({@code 0..999}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn die gegebenen Uhrzei ungültig ist. */
	public final FEMDatetime withTime(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		FEMDatetime.checkTime(hour, minute, second, millisecond);
		return this.withTimeImpl(hour, minute, second, millisecond);
	}

	/** Diese Methode gibt diese Zeitangabe mit der Uhrzeit des gegebenen {@link Calendar} zurück.<br>
	 * Die gelieferte Zeitangabe {@link #hasTime() besitzt} nur dann eine Uhrzeit, wenn am gegebenen {@link Calendar} die Felder {@link Calendar#HOUR_OF_DAY},
	 * {@link Calendar#MINUTE} und {@link Calendar#SECOND} definiert sind. Andernfalls hat die gelieferte Zeitangabe keine Uhrzeit.
	 *
	 * @see #withoutTime()
	 * @param calendar Uhrzeit.
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Uhrzeit ungültig ist. */
	public final FEMDatetime withTime(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		if (!calendar.isSet(Calendar.HOUR) || !calendar.isSet(Calendar.MINUTE) || !calendar.isSet(Calendar.SECOND)) return this.withoutTime();
		return this.withTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
	}

	/** Diese Methode gibt diese Zeitangabe mit der Uhrzeit der gegebenen Zeitangabe zurück.<br>
	 * Wenn die gegebene Zeitangabe keine Uhrzeit {@link #hasTime() besitzt}, hat die gelieferte Zeitangabe auch keine Uhrzeit.
	 *
	 * @see #withoutTime()
	 * @param datetime Uhrzeit.
	 * @return Zeitangabe mit oder ohne Uhrzeit.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist. */
	public final FEMDatetime withTime(final FEMDatetime datetime) throws NullPointerException {
		if (!datetime.hasTime()) return this.withoutTime();
		return this.withTimeImpl(datetime.hourValueImpl(), datetime.minuteValueImpl(), datetime.secondValueImpl(), datetime.millisecondValueImpl());
	}

	@SuppressWarnings ("javadoc")
	final FEMDatetime withTimeImpl(final int daymillis) {
		final int hour = daymillis / 3600000, hourmillis = daymillis % 3600000;
		final int minute = hourmillis / 60000, minutemillis = hourmillis % 60000;
		final int second = minutemillis / 1000, millisecond = minutemillis % 1000;
		return this.withTimeImpl(hour, minute, second, millisecond);
	}

	@SuppressWarnings ("javadoc")
	final FEMDatetime withTimeImpl(final int hour, final int minute, final int second, final int millisecond) {
		return new FEMDatetime( //
			(this.valueH & 0xFFFFC002) | (minute << 8) | (second << 2) | (1 << 0), //
			(this.valueL & 0xFFFF8000) | (hour << 10) | (millisecond << 0));
	}

	/** Diese Methode gibt diese Zeitangabe mit der gegebenen Zeitzone zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.
	 *
	 * @see #moveZone(int, int)
	 * @see #withoutZone()
	 * @param zone Zeitzone ({@code -840..840})
	 * @return Zeitangabe mit Zeitzone.
	 * @throws IllegalArgumentException Wenn die gegebenen Zeitzone ungültig ist. */
	public final FEMDatetime withZone(final int zone) throws IllegalArgumentException {
		FEMDatetime.checkZone(zone);
		if (!this.hasZone()) return this.withZoneImpl(zone);
		return this.moveZone(0, zone - this.zoneValueImpl());
	}

	/** Diese Methode gibt diese Zeitangabe mit der gegebenen Zeitzone zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.
	 *
	 * @see #withoutZone()
	 * @param zoneHour Stunde der Zeitzone ({@code -14..14}).
	 * @param zoneMinute Minute der Zeitzone ({@code 0..59}).
	 * @return Zeitangabe mit Zeitzone.
	 * @throws IllegalArgumentException Wenn die gegebenen Zeitzone ungültig ist. */
	public final FEMDatetime withZone(final int zoneHour, final int zoneMinute) throws IllegalArgumentException {
		if ((zoneHour == -14) || (zoneHour == 14)) {
			if (zoneMinute != 0) throw new IllegalArgumentException();
		} else {
			if ((zoneHour < -14) || (zoneHour > 14)) throw new IllegalArgumentException();
			if ((zoneMinute < 0) || (zoneMinute > 59)) throw new IllegalArgumentException();
		}
		final int zone = (zoneHour * 60) + (zoneHour < 0 ? -zoneMinute : zoneMinute);
		if (!this.hasZone()) return this.withZoneImpl(zone);
		return this.moveZoneImpl(zone - this.zoneValueImpl());
	}

	/** Diese Methode gibt diese Zeitangabe mit der Zeitzone des gegebenen {@link Calendar} zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.<br>
	 * Wenn am gegebenen {@link Calendar} das Feld {@link Calendar#ZONE_OFFSET} undefiniert ist, hat die gelieferte Zeitangabe keine Zeitzone.
	 *
	 * @see #withoutZone()
	 * @param calendar Zeitzone.
	 * @return Zeitangabe mit oder ohne Zeitzone.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeitzone ungültig ist. */
	public final FEMDatetime withZone(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		if (!calendar.isSet(Calendar.ZONE_OFFSET)) return this.withoutZone();
		final int zone = calendar.get(Calendar.ZONE_OFFSET) / 60000;
		return this.withZone(zone);
	}

	/** Diese Methode gibt diese Zeitangabe mit der Zeitzone der gegebenen Zeitangabe zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.<br>
	 * Wenn die gegebene Zeitangabe keine Zeitzone {@link #hasZone() besitzt}, hat die gelieferte Zeitangabe auch keine Zeitzone.
	 *
	 * @see #withoutZone()
	 * @param datetime Zeitzone.
	 * @return Zeitangabe mit oder ohne Zeitzone.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist. */
	public final FEMDatetime withZone(final FEMDatetime datetime) throws NullPointerException {
		if (!datetime.hasZone()) return this.withoutZone();
		final int zone = datetime.zoneValueImpl();
		if (!this.hasZone()) return this.withZoneImpl(zone);
		return this.moveZoneImpl(zone - this.zoneValueImpl());
	}

	@SuppressWarnings ("javadoc")
	final FEMDatetime withZoneImpl(final int zone) {
		return new FEMDatetime(this.valueH, (this.valueL & 0xFFFFF) | (1 << 31) | ((zone + 1024) << 20));
	}

	/** Diese Methode gibt diese Zeitangabe ohne Datum zurück.
	 *
	 * @see #withDate(int)
	 * @see #withDate(int, int, int)
	 * @see #withDate(Calendar)
	 * @see #withDate(FEMDatetime)
	 * @return Zeitangabe ohne Datum. */
	public final FEMDatetime withoutDate() {
		if (!this.hasDate()) return this;
		return new FEMDatetime(this.valueH & 0x3FFD, this.valueL & 0xFFF07FFF);
	}

	/** Diese Methode gibt diese Zeitangabe ohne Uhrzeit zurück.
	 *
	 * @see #withTime(int)
	 * @see #withTime(int, int, int, int)
	 * @see #withTime(Calendar)
	 * @see #withTime(FEMDatetime)
	 * @return Zeitangabe ohne Uhrzeit. */
	public final FEMDatetime withoutTime() {
		if (!this.hasTime()) return this;
		return new FEMDatetime(this.valueH & 0xFFFFC002, this.valueL & 0xFFFF8000);
	}

	/** Diese Methode gibt diese Zeitangabe ohne Zeitzone zurück.
	 *
	 * @see #withZone(int)
	 * @see #withZone(int, int)
	 * @see #withZone(Calendar)
	 * @see #withZone(FEMDatetime)
	 * @return Zeitangabe ohne Zeitzone. */
	public final FEMDatetime withoutZone() {
		if (!this.hasZone()) return this;
		return new FEMDatetime(this.valueH, (this.valueL & 0xFFFFF) | (1024 << 20));
	}

	/** Diese Methode gibt diese Zeitangabe verschoben um die gegebenen Gesamtanzahlen an Monate und Millisekunden zurück.
	 *
	 * @see #move(int, int, int, int, long, long, long)
	 * @param durationmonths Gesamtanzahl der Monate ({@code -101015..101015}).
	 * @param durationmillis Gesamtanzahl der Millisekunden.
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDatetime move(final int durationmonths, final int durationmillis) throws IllegalArgumentException {
		FEMDatetime.checkMonths(+durationmonths);
		FEMDatetime.checkMonths(-durationmonths);
		return this.moveImpl(durationmonths, durationmillis);
	}

	/** Diese Methode gibt diese Zeitangabe verschoben um die gegebenen Gesamtanzahlen an Monate und Millisekunden zurück.
	 *
	 * @see #move(int, int, int, int, long, long, long)
	 * @param durationmonths Gesamtanzahl der Monate ({@code -101015..101015}).
	 * @param durationmillis Gesamtanzahl der Millisekunden ({@code -265621593600000..265621593599999}).
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDatetime move(final int durationmonths, final long durationmillis) throws IllegalArgumentException {
		FEMDatetime.checkMonths(+durationmonths);
		FEMDatetime.checkMonths(-durationmonths);
		FEMDatetime.checkMilliseconds(+durationmillis);
		FEMDatetime.checkMilliseconds(-durationmillis);
		return this.moveImpl(durationmonths, durationmillis);
	}

	/** Diese Methode gibt diese Zeitangabe verschoben um die Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @see #move(int, int, int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -8417..8417}).
	 * @param months Anzahl der Monate ({@code -101015..101015}).
	 * @param days Anzahl der Tage ({@code -3074323..3074323}).
	 * @param hours Anzahl der Stunden ({@code -73783776..73783775}).
	 * @param minutes Anzahl der Minuten.
	 * @param seconds Anzahl der Sekunden.
	 * @param milliseconds Anzahl der Millisekunden.
	 * @return verschobene Zeitangabe.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde. */
	public final FEMDatetime move(final int years, final int months, final int days, final int hours, final int minutes, final int seconds,
		final int milliseconds) throws IllegalArgumentException {
		FEMDatetime.checkYear(+years);
		FEMDatetime.checkYear(-years);
		FEMDatetime.checkMonths(+months);
		FEMDatetime.checkMonths(-months);
		FEMDatetime.checkDays(+days);
		FEMDatetime.checkDays(-days);
		FEMDatetime.checkHours(+hours);
		FEMDatetime.checkHours(-hours);
		return this.moveImpl(FEMDuration.durationmonthsOfImpl(years, months), FEMDuration.durationmillisOfImpl(days, hours, minutes, seconds, milliseconds));
	}

	/** Diese Methode gibt diese Zeitangabe verschoben um die Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 * <p>
	 * Die Verschiebung erfolgt gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#adding-durations-to-dateTimes">XML Schema Part 2: §E Adding
	 * durations to dateTimes</a>:
	 * <ul>
	 * <li>{@link #yearValue() Jahr} und {@link #monthValue() Monat} werden gemäß der gegebenen Anzahl an Jahren ({@code years}) und Monaten ({@code months})
	 * verschoben.</li>
	 * <li>Der {@link #dateValue() Tag} wird gemäß dem ermittelten Jahr und Monat korrigiert, sodass der Tag nicht größer als die {@link #lengthOf(int, int)
	 * Anzahl der Tage im Monat} ist.</li>
	 * <li>Der Tag wird gemäß der gegebenen Anzahl an Tagen ({@code days}) verschoben. Dadurch können sich Jahr und Monat nochmals ändern.</li>
	 * </ul>
	 * </p>
	 *
	 * @param years Anzahl der Jahre ({@code -8417..8417}).
	 * @param months Anzahl der Monate ({@code -101015..101015}).
	 * @param days Anzahl der Tage ({@code -3074323..3074323}).
	 * @param hours Anzahl der Stunden ({@code -73783776..73783775}).
	 * @param minutes Anzahl der Minuten ({@code -4427026560..4427026559}).
	 * @param seconds Anzahl der Sekunden ({@code -265621593600..265621593599}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -265621593600000..265621593599999}).
	 * @return verschobene Zeitangabe.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde. */
	public final FEMDatetime move(final int years, final int months, final int days, final int hours, final long minutes, final long seconds,
		final long milliseconds) throws IllegalArgumentException {
		FEMDatetime.checkYear(+years);
		FEMDatetime.checkYear(-years);
		FEMDatetime.checkMonths(+months);
		FEMDatetime.checkMonths(-months);
		FEMDatetime.checkDays(+days);
		FEMDatetime.checkDays(-days);
		FEMDatetime.checkHours(+hours);
		FEMDatetime.checkHours(-hours);
		FEMDatetime.checkMilliseconds(+minutes);
		FEMDatetime.checkMilliseconds(-minutes);
		FEMDatetime.checkSeconds(+seconds);
		FEMDatetime.checkSeconds(-seconds);
		FEMDatetime.checkMilliseconds(-milliseconds);
		FEMDatetime.checkMilliseconds(+milliseconds);
		return this.moveImpl(FEMDuration.durationmonthsOfImpl(years, months), FEMDuration.durationmillisOfImpl(days, hours, minutes, seconds, milliseconds));
	}

	/** Diese Methode gibt diese Zeitangabe verschoben um die Gesamtanzahlen an Monate und Millisekunden der gegebenen Zeitspanne zurück.
	 *
	 * @see #move(int, int, int, int, long, long, long)
	 * @param duration Gesamtanzahlen an Monate und Millisekunden.
	 * @param negate {@code true}, wenn die Verschiebung in die der gegebenen Zeitspanne entgegengesetzte Richtung erfolgen soll.
	 * @return verschobene Zeitangabe.
	 * @throws NullPointerException Wenn {@code duration} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde. */
	public final FEMDatetime move(final FEMDuration duration, final boolean negate) throws NullPointerException, IllegalArgumentException {
		if ((duration.signValue() > 0) == negate) return this.moveImpl(-duration.durationmonthsValue(), -duration.durationmillisValue());
		return this.moveImpl(duration.durationmonthsValue(), duration.durationmillisValue());
	}

	@SuppressWarnings ("javadoc")
	final FEMDatetime moveImpl(final int months, final long millis) {
		int days = 0;
		FEMDatetime result = this;
		if (millis != 0) {
			final long datetimemillis = millis + this.daymillisValueImpl();
			int daymillis = (int)(datetimemillis % 86400000);
			days = (int)(datetimemillis / 86400000);
			if (daymillis < 0) {
				days--;
				daymillis += 86400000;
			}
			if (result.hasTime()) {
				result = result.withTimeImpl(daymillis);
			}
		}
		if (!this.hasDate() || ((days | months) == 0)) return result;
		final int datetimemonths = (months + this.monthValueImpl()) - 1;
		final int year = (datetimemonths / 12) + this.yearValueImpl(), month = (datetimemonths % 12) + 1;
		final int value = this.dateValueImpl(), length = FEMDatetime.lengthOf(month, year), date = value > length ? length : value;
		if (days == 0) return result.withDateImpl(year, month, date);
		return result.withDate(FEMDatetime.calendardayOfImpl(year, month, date) + days);
	}

	/** Diese Methode gibt diese Zeitangabe mit verschobener Zeitzone zurück.<br>
	 * Wenn die Zeitangabe eine Uhrzeit {@link #hasTime() besitzt}, wird diese falls nötig ebenfalls verschoben.
	 *
	 * @see #move(int, int, int, int, long, long, long)
	 * @param hours Anzahl der Stunden ({@code -28..28}).
	 * @param minutes Anzahl der Minuten ({@code -1680..1680}).
	 * @return Zeitangabe mit verschobener Zeitzone.
	 * @throws IllegalStateException Wenn diese Zeitangabe keine Zeitzone besitzt.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde. */
	public final FEMDatetime moveZone(final int hours, final int minutes) throws IllegalStateException, IllegalArgumentException {
		if (!this.hasZone()) throw new IllegalStateException();
		if ((hours == 0) && (minutes == 0)) return this;
		if ((hours < -28) || (hours > 28)) throw new IllegalArgumentException();
		if ((minutes < -1680) || (minutes > 1680)) throw new IllegalArgumentException();
		return this.moveZoneImpl((hours * 60) + minutes);
	}

	@SuppressWarnings ("javadoc")
	final FEMDatetime moveZoneImpl(final int minutes) {
		if (minutes == 0) return this;
		final int zone = minutes + this.zoneValueImpl();
		FEMDatetime.checkZone(zone);
		if (this.hasTime()) return this.moveImpl(0, minutes * 60000).withZoneImpl(zone);
		if (!this.hasDate()) return this.withZoneImpl(zone);
		final int days = (minutes - 1439) / 1440;
		if (days != 0) return this.moveImpl(0, days * 86400000L).withZoneImpl(zone);
		return this.withZoneImpl(zone);
	}

	/** Diese Methode gibt diese Zeitangabe normalisiert zurück.<br>
	 * Hierbei werden Zeitpunkte mit der Uhrzeit {@code 24:00:00.000} auf {@code 00:00:00.000} des Folgetages normalisiert, sofern dies möglich ist.
	 *
	 * @return normalisierte Zeitangabe. */
	public final FEMDatetime normalize() {
		if (!this.hasDate() || !this.hasTime()) return this;
		if (this.hourValueImpl() != 24) return this;
		if ((this.yearValueImpl() == 9999) && (this.monthValueImpl() == 12) && (this.dateValueImpl() == 31)) return this;
		return this.withTimeImpl(0, 0, 0, 0).moveImpl(0, 86400000);
	}

	/** Diese Methode gibt den Streuwert zurück.
	 *
	 * @return Streuwert. */
	public final int hash() {
		return this.valueH ^ this.valueL;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe effektiv gleich der gegebenen ist.
	 *
	 * @see #compare(FEMDatetime, int)
	 * @param that Zeitangabe.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMDatetime that) throws NullPointerException {
		return ((this.valueL == that.valueL) && (this.valueH == that.valueH)) || (this.compare(that, 1) == 0);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn diese Zeitangabe früher, gleich bzw. später als die gegebene Zeitangabe ist. Wenn
	 * die Zeitangaben nicht vergleichbar sind, wird {@code undefined} geliefert.
	 * <p>
	 * Der Vergleich erfolgt für Zeitangaben mit Datum und/oder Uhrzeit gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#dateTime-order">XML
	 * Schema Part 2: 3.2.7.3 Order relation on dateTime</a>:
	 * <ul>
	 * <li>Verschieben beider Zeitangaben auf Zeitzone {@code 00:00}. Zeitangaben mit Datum und ohne Uhrzeit werden hierbei so behandelt, als hätten sie die
	 * Uhrzeit {@code 00:00:00}. Damit sinkt der {@link #calendardayValue()} nur dann um {@code 1}, wenn der {@link #zoneValue()} größer als {@code 0} ist.</li>
	 * <li>Wenn nur eine der Zeitangaben ein Datum besitzt, wird {@code undefined} geliefert.</li>
	 * <li>Wenn beide ein Datum besitzen und die Differenz von {@link #calendardayValue()} ungleich {@code 0} ist, wird das Vorzeichen dieser Differenz
	 * geliefert.</li>
	 * <li>Wenn nur eine der Zeitangaben eine Uhrzeit besitzt, wird {@code undefined} geliefert.</li>
	 * <li>Wenn beide eine Uhrzeit besitzen, wird das Vorzeichen der Differenz von {@link #daymillisValue()} geliefert.</li>
	 * <li>Andernfalls wird {@code 0} geliefert.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Der Vergleich für Zeitangaben ohne Datum und ohne Uhrzeit erfolgt über folgende Schritte:
	 * <ul>
	 * <li>Wenn nur eine der Zeitangaben eine Zeitzone besitzt, wird {@code undefined} geliefert.</li>
	 * <li>Wenn beide eine Zeitzone besitzen, wird das Vorzeichen der Differenz von {@link #zoneValue()} geliefert.</li>
	 * <li>Andernfalls wird {@code 0} geliefert.</li>
	 * </ul>
	 * </p>
	 *
	 * @param that Zeitangabe.
	 * @param undefined Rückgabewert für nicht vergleichbare Zeitangaben.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final int compare(final FEMDatetime that, final int undefined) throws NullPointerException {
		int result;
		if (this.hasDate()) {
			if (!that.hasDate()) return undefined;
			result = this.calendardayValueImpl() - that.calendardayValueImpl();
			if ((result < -2) || (result > 2)) return Comparators.compare(result, 0);
			if (this.hasTime()) {
				if (that.hasTime()) {
					result *= 86400000;
					result += (this.daymillisValueImpl() - that.daymillisValueImpl());
					result += (this.zoneValueImpl() - that.zoneValueImpl()) * -60000;
					return Comparators.compare(result, 0);
				} else {
					if (that.zoneValueImpl() > 0) {
						result++;
					}
					result *= 86400000;
					result += this.daymillisValueImpl();
					result += this.zoneValueImpl() * -60000;
					if (result < 0) return -1;
					if (result >= 86400000) return +1;
					return undefined;
				}
			} else {
				if (that.hasTime()) {
					if (this.zoneValueImpl() > 0) {
						result--;
					}
					result *= 86400000;
					result -= that.daymillisValueImpl();
					result -= that.zoneValueImpl() * -60000;
					if (result > 0) return +1;
					if (result <= -86400000) return -1;
					return undefined;
				} else {
					if (this.zoneValueImpl() > 0) {
						result--;
					}
					if (that.zoneValueImpl() > 0) {
						result++;
					}
					return Comparators.compare(result, 0);
				}
			}
		} else {
			if (that.hasDate()) return undefined;
			if (this.hasTime()) {
				if (!that.hasTime()) return undefined;
				result = this.daymillisValueImpl() - that.daymillisValueImpl();
				result += (this.zoneValueImpl() - that.zoneValueImpl()) * -60000;
				return Comparators.compare(result, 0);
			} else {
				if (that.hasTime()) return undefined;
				if (this.hasZone()) {
					if (!that.hasZone()) return undefined;
					result = that.zoneValueImpl() - this.zoneValueImpl();
					return Comparators.compare(result, 0);
				} else {
					if (that.hasZone()) return undefined;
					return 0;
				}
			}
		}
	}

	/** Diese Methode gibt diese Zeitangabe als Anzahl an Millisekunden seit dem Zeitpunkt {@code 1970-01-01T00:00:00Z} zurück. Wenn diese Zeitangabe
	 * {@link #hasZone() keine Zeitzone} besitzt, wird {@code 0} angenommen. Wenn sie {@link #hasTime() keine Uhrzeit} besitzt, wird {@code 00:00:00.000}
	 * angenommen.
	 *
	 * @return Anzahl an Millisekunden.
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt. */
	public final long toTime() {
		return FEMDuration.durationmillisOfImpl(this.calendardayValue() - 141427, 0, this.zoneValueImpl(), 0, this.daymillisValueImpl());
	}

	/** Diese Methode gibt diese Zeitangabe als {@link Calendar} zurück.
	 *
	 * @return {@link Calendar}. */
	public final GregorianCalendar toCalendar() {
		final GregorianCalendar result = new GregorianCalendar();
		result.clear();
		if (this.hasDate()) {
			result.set(Calendar.YEAR, this.yearValueImpl());
			result.set(Calendar.MONTH, (this.monthValueImpl() - 1));
			result.set(Calendar.DATE, this.dateValueImpl());
		}
		if (this.hasTime()) {
			result.set(Calendar.HOUR_OF_DAY, this.hourValueImpl());
			result.set(Calendar.MINUTE, this.minuteValueImpl());
			result.set(Calendar.SECOND, this.secondValueImpl());
			result.set(Calendar.MILLISECOND, this.millisecondValueImpl());
		}
		if (this.hasZone()) {
			result.set(Calendar.ZONE_OFFSET, this.zoneValueImpl() * 60000);
		}
		return result;
	}

	{}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMDatetime data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMDatetime> type() {
		return FEMDatetime.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMDatetime result() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMDatetime result(final boolean recursive) {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDatetime)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMDatetime)) return false;
		}
		return this.equals((FEMDatetime)object);
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final FEMDatetime that) {
		return this.compare(that, 0);
	}

	/** Diese Methode gibt die Textdarstellung dieser Zeitangabe zurück.<br>
	 * Diese Textdarstellung entspricht der des Datentyps <a href="http://www.w3.org/TR/xmlschema-2/#dateTime-lexical-representation">xsd:dateTime</a> aus
	 * <a href="www.w3.org/TR/xmlschema-2">XML Schema Part 2: Datatypes Second Edition</a>, beschränkt auf genau drei optionale Nachkommastellen für die Sekunden.
	 *
	 * @return Textdarstellung. */
	@Override
	public final String toString() {
		final char[] buffer = new char[29];
		final boolean hasDate = this.hasDate();
		int offset = 0;
		if (hasDate) {
			Integers.formatInt(this.yearValueImpl(), buffer, offset, 4);
			offset += 4;
			buffer[offset] = '-';
			offset += 1;
			Integers.formatInt(this.monthValueImpl(), buffer, offset, 2);
			offset += 2;
			buffer[offset] = '-';
			offset += 1;
			Integers.formatInt(this.dateValueImpl(), buffer, offset, 2);
			offset += 2;
		}
		if (this.hasTime()) {
			if (hasDate) {
				buffer[offset] = 'T';
				offset += 1;
			}
			buffer[7] = '-';
			Integers.formatInt(this.hourValueImpl(), buffer, offset, 2);
			offset += 2;
			buffer[offset] = ':';
			offset += 1;
			Integers.formatInt(this.minuteValueImpl(), buffer, offset, 2);
			offset += 2;
			buffer[offset] = ':';
			offset += 1;
			Integers.formatInt(this.secondValueImpl(), buffer, offset, 2);
			offset += 2;
			final int millisecond = this.millisecondValueImpl();
			if (millisecond != 0) {
				buffer[offset] = '.';
				offset += 1;
				Integers.formatInt(millisecond, buffer, offset, 3);
				offset += 3;
			}
		}
		if (this.hasZone()) {
			final int zone = this.zoneValueImpl();
			if (zone == 0) {
				buffer[offset] = 'Z';
				offset += 1;
			} else {
				final int zoneAbs = Math.abs(zone);
				buffer[offset] = zone < 0 ? '-' : '+';
				offset += 1;
				Integers.formatInt(zoneAbs / 60, buffer, offset, 2);
				offset += 2;
				buffer[offset] = ':';
				offset += 1;
				Integers.formatInt(zoneAbs % 60, buffer, offset, 2);
				offset += 2;
			}
		}
		return new String(buffer, 0, offset);
	}

}
