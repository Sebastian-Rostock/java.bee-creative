package bee.creative.fem;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Diese Klasse implementiert eine Zeitangabe mit Datum, Uhrzeit und/oder Zeitzonenverschiebung im Gregorianischen Kalender.<br>
 * Intern wird die Zeitangabe als ein 64 Bit Zahlenwert dargestellt.
 * <p>
 * Das <u>Datum</u> kann unspezifiziert sein oder aus Jahr, Monat sowie Tag bestehen und im Bereich {@code 15.10.1582..31.12.9999} liegen. Die <u>Uhrzeit</u>
 * kann unspezifiziert sein oder aus Stunde, Minute, Sekunde sowie Millisekunde bestehen und im Bereich {@code 00:00:00.000..24:00:00.000} liegen. Die
 * <u>Zeitzone</u> kann unspezifiziert sein oder aus Stunde sowie Minute bestehen und im Bereich {@code -14:00..+14:00} liegen.
 * </p>
 * <h5><a name="year">Jahr</a></h5>
 * <p>
 * Der Zahlenwert für das Jahr entspricht der Anzahl der Jahre seit dem beginn des Gregorianischen Kalenders erhöht um {@code 1582}. Unterstützte Zahlenwerte
 * für das Jahr sind {@code 1582..9999}.<br>
 * Ein reguläres Jahr hat 365 Tage, ein {@link #leapOf(int) Schaltjahr} hat 366 Tage.
 * </p>
 * <h5><a name="month">Monat</a></h5>
 * <p>
 * Der Zahlenwert für den Monat entspricht der Anzahl der Monate seit beginn des Jahres erhöht um {@code 1}. Unterstützte Zahlenwerte für den Monat sind
 * {@code 1..12}.
 * <ol>
 * <li>Januar = {@link Calendar#JANUARY} + 1</li>
 * <li>Februar = {@link Calendar#FEBRUARY} + 1</li>
 * <li>März = {@link Calendar#MARCH} + 1</li>
 * <li>April = {@link Calendar#APRIL} + 1</li>
 * <li>Mai = {@link Calendar#MAY} + 1</li>
 * <li>Juni = {@link Calendar#JUNE} + 1</li>
 * <li>Juli = {@link Calendar#JULY} + 1</li>
 * <li>August = {@link Calendar#AUGUST} + 1</li>
 * <li>Sptember = {@link Calendar#SEPTEMBER} + 1</li>
 * <li>Oktober = {@link Calendar#OCTOBER} + 1</li>
 * <li>November = {@link Calendar#NOVEMBER} + 1</li>
 * <li>Dezember = {@link Calendar#DECEMBER} + 1</li>
 * </ol>
 * </p>
 * <h5><a name="date">Tag (Tag in einem Monat)</a></h5>
 * <p>
 * Der Zahlenwert für den Tag entspricht der Anzahl der Tage seit beginn des Monats erhöht um {@code 1}. Unterstützte Zahlenwerte für den Monat sind
 * {@code 1..31}, wobei einige Monate auch abhängig von Schaltjahren geringere {@link #lengthOf(int, boolean) Obergrenzen} besitzen.
 * </p>
 * <h5><a name="yearday">Jahrestag (Tag in einem Jahr)</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #yeardayOf(int) Jahrestag} entspricht der Anzahl der Tages seit dem Beginn des Jahres erhöht um {@code 1}. Unterstützte
 * Zahlenwerte für den Jahrestag sind {@code 1..366}.
 * </p>
 * <h5><a name="weekday">Wochentag (Tag in einer Woche)</a></h5>
 * <p>
 * Der Zahlenwert für den {@link #weekdayOf(int) Wochentag} entspricht der Anzahl der Tage seit beginn der Woche erhöht um {@code 1}. Unterstützte Zahlenwerte
 * für den Wochentag sind {@code 1..12}.
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
 * Der Zahlenwert für den Kalendertag entspricht der Anzahl der Tages seit dem Beginn des Gregorianischen Kalenders am Freitag dem {@code 15.10.1582}.
 * Unterstützte Zahlenwerte für den Kalendertag sind {@code 0..3074323}.
 * </p>
 * <h5><a name="daymillis">Tagesmillis (Millisekunden am Tag)</a></h5>
 * <p>
 * Der Zahlenwert für die {@link #daymillisOf(int, int, int, int) Tagesmillis} entspricht der Anzahl der Millisekunden seit {@code 00:00:00.000}. Unterstützte
 * Zahlenwerte für die Tagesmillis sind {@code 0..86400000}.
 * </p>
 * <h5><a name="zone">Zeitzone (Zeitzonenverschiebung)</a></h5>
 * <p>
 * Der Zahlenwert für die {@link #zoneValue() Zeitzone} entspricht der Zeitzonenverschiebung in Minuten. Unterstützte Zahlenwerte für die Zeitzone sind
 * {@code -840..840}.
 * </p>
 * 
 * @author Sebastian Rostock 2011.
 */
public final class FEMDatetime {

	/**
	 * Diese Methode gibt eine Zeitangabe mit dem Datum, der Uhrzeit und der Zeitzone des gegebenen {@link Calendar} zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withDate(calendar).withTime(calendar).withZone(calendar)}.
	 * 
	 * @see #withDate(Calendar)
	 * @see #withTime(Calendar)
	 * @see #withZone(Calendar)
	 * @param calendar {@link Calendar}.
	 * @return Zeitangabe.
	 * @throws IllegalArgumentException Wenn {@link #withDate(Calendar)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime from(final Calendar calendar) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(calendar).withTime(calendar).withZone(calendar);
	}

	/**
	 * Diese Methode gibt eine Zeitangabe mit dem Datum zum gegebenen Kalendertag zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withDate(calendarday)}.
	 * 
	 * @see #withDate(int)
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn {@link #withDate(int)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime fromDate(final int calendarday) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(calendarday);
	}

	/**
	 * Diese Methode gibt eine Zeitangabe mit dem gegebenen Datum zurück und ist eine Abkürzung für {@code FEE_Datetime.EMPTY.withDate(year, month, date)}.
	 * 
	 * @see #withDate(int, int, int)
	 * @param year Jahr ({@code 1582..9999}).
	 * @param month Monat ({@code 1..12}).
	 * @param date Tag ({@code 1..31}).
	 * @return Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn {@link #withDate(int, int, int)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime fromDate(final int year, final int month, final int date) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(year, month, date);
	}

	/**
	 * Diese Methode gibt eine Zeitangabe mit dem Datum des gegebenen {@link Calendar} zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withDate(calendar)}.
	 * 
	 * @see #withDate(Calendar)
	 * @see #calendardayOf(int, int, int)
	 * @param calendar Kalendertag ({@code 0..3074323}).
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws IllegalArgumentException Wenn {@link #withDate(Calendar)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime fromDate(final Calendar calendar) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withDate(calendar);
	}

	/**
	 * Diese Methode gibt eine Zeitangabe mit der Uhrzeit zu den gegebenen Tagesmillis zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withTime(daymillis)}.
	 * 
	 * @see #withTime(int)
	 * @see #daymillisOf(int, int, int, int)
	 * @param daymillis Tagesmillis ({@code 0..86400000}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@link #withTime(int)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime fromTime(final int daymillis) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withTime(daymillis);
	}

	/**
	 * Diese Methode gibt eine Zeitangabe mit der gegebenen Uhrzeit zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withTime(hour, minute, second, millisecond)}.
	 * 
	 * @see #withTime(int, int, int, int)
	 * @param hour Stunde ({@code 0..24}).
	 * @param minute Minute ({@code 0..59}).
	 * @param second Sekunde ({@code 0..59}).
	 * @param millisecond Millisekunde ({@code 0..999}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@link #withTime(int, int, int, int)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime fromTime(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withTime(hour, minute, second, millisecond);
	}

	/**
	 * Diese Methode gibt eine Zeitangabe mit der Uhrzeit des gegebenen {@link Calendar} zurück und ist eine Abkürzung für
	 * {@code FEE_Datetime.EMPTY.withTime(calendar)}.
	 * 
	 * @see #withTime(Calendar)
	 * @param calendar {@link Calendar}.
	 * @return Zeitangabe mit oder ohne Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@link #withTime(Calendar)} eine entsprechende Ausnahme auslöst.
	 */
	public static final FEMDatetime fromTime(final Calendar calendar) throws IllegalArgumentException {
		return FEMDatetime.EMPTY.withTime(calendar);
	}

	@SuppressWarnings ("javadoc")
	private static final void checkDate(final int calendarday) throws IllegalArgumentException {
		if ((calendarday < 0) || (calendarday > 3074323)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	private static final void checkDate(final int year, final int month, final int date) throws IllegalArgumentException {
		if (year != 1582) {
			FEMDatetime.checkYear(year);
			if ((month < 1) || (month > 12)) throw new IllegalArgumentException();
			if (date < 1) throw new IllegalArgumentException();
		} else if (month != 10) {
			if ((month < 10) || (month > 12)) throw new IllegalArgumentException();
			if (date < 1) throw new IllegalArgumentException();
		} else if (date < 15) throw new IllegalArgumentException();
		if (date > FEMDatetime.lengthOf__(month, year)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	private static final void checkTime(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		if (hour == 24) {
			if (minute != 0) throw new IllegalArgumentException();
			if (second != 0) throw new IllegalArgumentException();
			if (millisecond != 0) throw new IllegalArgumentException();
		} else {
			if ((hour < 0) || (hour > 23)) throw new IllegalArgumentException();
			if ((minute < 0) || (minute > 59)) throw new IllegalArgumentException();
			if ((second < 0) || (second >= 59)) throw new IllegalArgumentException();
			if ((millisecond < 0) || (millisecond > 999)) throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings ("javadoc")
	private static final void checkYear(final int year) throws IllegalArgumentException {
		if ((year < 1582) || (year > 9999)) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	private static final void checkZero(final int data, final int valid) {
		if ((data & ~valid) != 0) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	private static final void checkZone(final int zone) throws IllegalArgumentException {
		if ((zone < -840) || (zone > 840)) throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Jahr ein Schaltjahr im Gregorianischen Kalender ist.<br>
	 * Ein Schaltjahr hat 366 Tage, ein reguläres hat dagegen 365 Tage.
	 * 
	 * @param year Jahr ({@code 1582..9999}).
	 * @return {@code true} bei einem Schaltjahr.
	 * @throws IllegalArgumentException Wenn {@code year} ungültig ist.
	 */
	public static final boolean leapOf(final int year) throws IllegalArgumentException {
		FEMDatetime.checkYear(year);
		return FEMDatetime.leapOf__(year);
	}

	@SuppressWarnings ("javadoc")
	private static final boolean leapOf__(final int year) {
		final int div = year / 100, mod = year % 100;
		return ((mod != 0 ? mod : div) & 3) == 0;
	}

	/**
	 * Diese Methode gibt Jahr, Monat und Tag zum gegebenen Kalendertag zurück.<br>
	 * Die 32 Bit des Rückgabewerts sind von MBS zum LSB:
	 * <ul>
	 * <li>EMPTY - 8 Bit</li>
	 * <li>Year - 14 Bit</li>
	 * <li>Month - 5 Bit</li>
	 * <li>Date - 5 Bit</li>
	 * </ul>
	 * 
	 * @param calendarday Kalendertag.
	 * @return Jahr, Monat und Tag.
	 */
	private static final int dateOf__(final int calendarday) {
		final int months = (int)(((calendarday + 139824) * 400 * 12L) / 146097);
		final int div = months / 12, mod = months % 12;
		int year = div + 1200, month = mod + 1, date = (calendarday - FEMDatetime.calendardayOf__(year, month, 1)) + 1;
		if (date <= 0) {
			if (month == 1) {
				year--;
				month = 12;
			} else {
				month--;
			}
			date += FEMDatetime.lengthOf__(month, year);
		}
		return (year << 10) | (month << 5) | (date << 0);
	}

	/**
	 * Diese Methode gibt die Länge des gegebenen Monats im gegebenen Jahr zurück, d.h. die Anzahl der Tage im Monat.
	 * 
	 * @see #leapOf(int)
	 * @see #lengthOf(int, boolean)
	 * @param month Monat ({@code 1..12}).
	 * @param year Jahr ({@code 1582..9999}).
	 * @return Länge des Monats ({@code 1..31}).
	 * @throws IllegalArgumentException Wenn {@code month} bzw. {@code year} ungültig ist.
	 */
	public static final int lengthOf(final int month, final int year) throws IllegalArgumentException {
		if (year != 1582) return FEMDatetime.lengthOf(month, FEMDatetime.leapOf(year));
		if ((month < 10) || (month > 12)) throw new IllegalArgumentException();
		return FEMDatetime.lengthOf__(month, FEMDatetime.leapOf(year));
	}

	@SuppressWarnings ("javadoc")
	private static final int lengthOf__(final int month, final int year) {
		return FEMDatetime.lengthOf__(month, FEMDatetime.leapOf__(year));
	}

	/**
	 * Diese Methode gibt die Länge des gegebenen Monats zurück, d.h. die Anzahl der Tage im Monat.
	 * 
	 * @see #leapOf(int)
	 * @param month Monat ({@code 1..12}).
	 * @param leap {@code true} in einem Schaltjahr mit 366 Tagen.
	 * @return Länge des Monats ({@code 1..31}).
	 * @throws IllegalArgumentException Wenn {@code month} ungültig ist.
	 */
	public static final int lengthOf(final int month, final boolean leap) throws IllegalArgumentException {
		if ((month < 1) || (month > 12)) throw new IllegalArgumentException();
		return FEMDatetime.lengthOf__(month, leap);
	}

	@SuppressWarnings ("javadoc")
	private static final int lengthOf__(final int month, final boolean isLeap) {
		return 28 + (((isLeap ? 62648028 : 62648012) >> (month << 1)) & 3);
	}

	/**
	 * Diese Methode gibt den Jahrestag zum gegebenen {@link #calendardayOf(int, int, int) Kalendertag} zurück.
	 * 
	 * @see #calendardayOf(int, int, int)
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return Jahrestag ({@code 1..366}).
	 * @throws IllegalArgumentException Wenn {@code calendarday} ungültig ist.
	 */
	public static final int yeardayOf(final int calendarday) throws IllegalArgumentException {
		FEMDatetime.checkDate(calendarday);
		return FEMDatetime.yeardayOf__(calendarday);
	}

	@SuppressWarnings ("javadoc")
	private static final int yeardayOf__(final int calendarday) {
		final int year = (((calendarday + 139810) * 400) / 146097) + 1200;
		final int result = (calendarday - FEMDatetime.calendardayOf__(year, 1, 1)) + 1;
		if (result == 0) return FEMDatetime.leapOf__(year - 1) ? 366 : 365;
		if (result == 366) return FEMDatetime.leapOf__(year) ? 366 : 1;
		return result;
	}

	/**
	 * Diese Methode gibt den Wochentag zum gegebenen {@link #calendardayOf(int, int, int) Kalendertag} zurück.
	 * 
	 * @see #calendardayOf(int, int, int)
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return Wochentag ({@code 1..7}).
	 * @throws IllegalArgumentException Wenn {@code calendarday} ungültig ist.
	 */
	public static final int weekdayOf(final int calendarday) throws IllegalArgumentException {
		FEMDatetime.checkDate(calendarday);
		return FEMDatetime.weekdayOf__(calendarday);
	}

	@SuppressWarnings ("javadoc")
	private static final int weekdayOf__(final int calendarday) {
		return ((calendarday + 5) % 7) + 1;
	}

	/**
	 * Diese Methode gibt die Tagesmillis zur gegebenen Uhrzeit zurück.
	 * 
	 * @param hour Stunde ({@code 0..24}).
	 * @param minute Minute ({@code 0..59}).
	 * @param second Sekunde ({@code 0..59}).
	 * @param millisecond Millisekunde ({@code 0..999}).
	 * @return Anzahl der Millisekunden zwischen {@code 00:00:00.000} und der gegebenen Uhrzeit ({@code 0..86400000}).
	 * @throws IllegalArgumentException Wenn die gegebene Uhrzeit ungültig ist.
	 */
	public static final int daymillisOf(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		FEMDatetime.checkTime(hour, minute, second, millisecond);
		return FEMDatetime.daymillisOf__(hour, minute, second, millisecond);
	}

	@SuppressWarnings ("javadoc")
	private static final int daymillisOf__(final int hour, final int minute, final int second, final int millisecond) {
		return (hour * 3600000) + (minute * 60000) + (second * 1000) + millisecond;
	}

	/**
	 * Diese Methode gibt den Kalendertag zum gegebenen Datum zurück.
	 * 
	 * @param year Jahr des Datums ({@code 1582..9999}).
	 * @param month Monat des Jahres ({@code 1..12}).
	 * @param date Tag des Monats ({@code 1..31}).
	 * @return Anzahl der Tage zwischen dem {@code 15.10.1582} und dem gegebenen Datum ({@code 0..3074323}).
	 * @throws IllegalArgumentException Wenn das gegebene Datum ungültig ist.
	 */
	public static final int calendardayOf(final int year, final int month, final int date) throws IllegalArgumentException {
		FEMDatetime.checkDate(year, month, date);
		return FEMDatetime.calendardayOf__(year, month, date);
	}

	@SuppressWarnings ("javadoc")
	private static final int calendardayOf__(final int year, final int month, final int date) {
		final int year2 = (year - ((7 >> month) & 1)) >> 2, year3 = year2 / 25, month2 = month << 1;
		final int month3 = (month * 29) + ((59630432 >> month2) & 3) + ((266948608 >> month2) & 12);
		return ((((year * 365) + year2) - year3) + (year3 >> 2) + month3 + date) - 578130;
	}

	public static void main(final String[] args) throws Exception {

		final FEMDatetime MIN = FEMDatetime.EMPTY.withDate(0).withTime(0).withZone(+840);
		System.out.println(MIN);
		final FEMDatetime MAX = FEMDatetime.EMPTY.withDate(3074323).withTime(86400000).withZone(-840);
		System.out.println(MAX);

		final Calendar s = new GregorianCalendar();
		System.out.println(FEMDatetime.from(FEMDatetime.from(s).withZone(2, 0).toCalendar()));
		System.out.println(s.get(Calendar.DATE));
		System.out.println(s.get(Calendar.MONTH));
		System.out.println(s.get(Calendar.YEAR));

		// System.out.println(FEMDatetime.calendardayOf__(1600, 1, 1));
		//
		// System.out.println("dad");
		// for (int i = 0; i < 3074324; i++) {
		// // System.out.println(Integer.toHexString(
		// final int x = FEMDatetime.dateOf__(i)
		// // ))
		//
		// ;
		// final int y = FEMDatetime.yeardayOf__(i);
		// if ((y < 3) || (y > 363)) {
		// System.out.println(String.format("%d,%d,%d  %d", x & 31, (x >> 5) & 31, x >> 10, y));
		// }
		// }
		// System.out.println(FEMDatetime.calendardayOf__(1200, 1, 1));
	}

	{}

	/**
	 * Dieses Feld speichert die leere Zeitangabe ohne Datum, ohne Uhrzeit und ohne Zeitzone.
	 */
	public static final FEMDatetime EMPTY = new FEMDatetime(0, 0);

	{}

	/**
	 * Dieses Feld speichert die 32 LSB der internen 64 Bit Darstellung dieser Zeitangabe.
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

	/**
	 * Dieses Feld speichert die 32 MSB der internen 64 Bit Darstellung dieser Zeitangabe.
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

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung.
	 * 
	 * @see #value()
	 * @param value interne Darstellung.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist.
	 */
	public FEMDatetime(final long value) throws IllegalArgumentException {
		this((int)(value >> 32), (int)(value >> 0));
		if (this.hasDate()) {
			FEMDatetime.checkDate(this.yearValue__(), this.monthValue__(), this.dateValue__());
		} else {
			FEMDatetime.checkZero(this.valueH, 0x3FFD);
			FEMDatetime.checkZero(this.valueL, 0xFFF07FFF);
		}
		if (this.hasTime()) {
			FEMDatetime.checkTime(this.hourValue__(), this.minuteValue__(), this.secondValue__(), this.millisecondValue__());
		} else {
			FEMDatetime.checkZero(this.valueH, 0xFFFFC002);
			FEMDatetime.checkZero(this.valueL, 0xFFFF8000);
		}
		if (this.hasZone()) {
			FEMDatetime.checkZone(this.zoneValue__());
		} else {
			FEMDatetime.checkZero(this.valueL, 0xFFFFF);
		}
	}

	/**
	 * Dieser Konstruktor initialisiert die internen Darstellungen.
	 * 
	 * @see #value()
	 * @param valueH 32 MSB der interne Darstellung.
	 * @param valueL 32 LBB der interne Darstellung.
	 */
	FEMDatetime(final int valueH, final int valueL) {
		this.valueH = valueH;
		this.valueL = valueL;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung der Zeitangabe zurück.
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
	 * @return interne Darstellung der Zeitangabe.
	 */
	public final long value() {
		return (((long)this.valueH) << 32) | (((long)this.valueL) << 0);
	}

	/**
	 * Diese Methode gibt das Jahr zurück.
	 * 
	 * @return Jahr ({@code 1582..9999}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int yearValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.yearValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int yearValue__() {
		return (this.valueH >> 18) & 0x3FFF;
	}

	/**
	 * Diese Methode gibt den Tag zurück.
	 * 
	 * @return Tag ({@code 1..31}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int dateValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.dateValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int dateValue__() {
		return (this.valueL >> 15) & 0x1F;
	}

	/**
	 * Diese Methode gibt den Monat zurück.
	 * 
	 * @return Monat ({@code 1..12}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int monthValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.monthValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int monthValue__() {
		return (this.valueH >> 14) & 0x0F;
	}

	/**
	 * Diese Methode gibt die Stunde zurück.
	 * 
	 * @return Stunde ({@code 0..24}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt.
	 */
	public final int hourValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.hourValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int hourValue__() {
		return (this.valueL >> 10) & 0x1F;
	}

	/**
	 * Diese Methode gibt die Minute zurück.
	 * 
	 * @return Minute ({@code 0..59}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt.
	 */
	public final int minuteValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.minuteValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int minuteValue__() {
		return (this.valueH >> 8) & 0x3F;
	}

	/**
	 * Diese Methode gibt die Sekunde zurück.
	 * 
	 * @return Sekunde ({@code 0..59}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt.
	 */
	public final int secondValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.secondValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int secondValue__() {
		return (this.valueH >> 2) & 0x3F;
	}

	/**
	 * Diese Methode gibt die Millisekunde zurück.
	 * 
	 * @return Millisekunde ({@code 0..999}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasTime() keine Uhrzeit} besitzt.
	 */
	public final int millisecondValue() throws IllegalStateException {
		if (!this.hasTime()) throw new IllegalStateException();
		return this.millisecondValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int millisecondValue__() {
		return (this.valueL >> 0) & 0x03FF;
	}

	/**
	 * Diese Methode gibt die Anzahl an Sekunden zurück, um die der Bis-Zeitpunkt später als der Von-Zeitpunkt ist. Wenn bei genau einem der Zeitpunkte eine
	 * Zeitzonenverschiebung angegeben ist, wird {@code null} geliefert.
	 * 
	 * @param this Von-Zeitpunkt.
	 * @param datetime Bis-Zeitpunkt.
	 * @return Anzahl an Sekunden mit Vorzeichen oder {@code null}.
	 */
	public long millisecondsUntil(final FEMDatetime datetime) throws IllegalArgumentException {

		final int daymillis = 0;
		if (this.hasTime()) {
			if (!datetime.hasZone()) throw new IllegalArgumentException();
		}

		if (this.hasZone() != datetime.hasZone()) return null;

		return new Long((datetime.toDate().totalSeconds() - this.toDate().totalSeconds()) + (datetime.toTime().totalSeconds() - this.toTime().totalSeconds()));
	}

	/**
	 * Diese Methode gibt die Zeitzonenverschiebung in Minuten zurück.
	 * 
	 * @return Zeitzonenverschiebung in Minuten ({@code -840..840}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() keine Zeitzone} besitzt.
	 */
	public final int zoneValue() throws IllegalStateException {
		if (!this.hasZone()) throw new IllegalStateException();
		return this.zoneValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int zoneValue__() {
		return ((this.valueL >> 20) & 0x07FF) - 1024;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe ein Datum besitzt.
	 * 
	 * @return {@code true}, wenn diese Zeitangabe ein Datum besitzt.
	 */
	public final boolean hasDate() {
		return ((this.valueH >> 1) & 0x01) != 0;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe eine Uhrzeit besitzt.
	 * 
	 * @return {@code true}, wenn diese Zeitangabe eine Uhrzeit besitzt.
	 */
	public final boolean hasTime() {
		return ((this.valueH >> 0) & 0x01) != 0;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitangabe eine Zeitzone besitzt.
	 * 
	 * @return {@code true}, wenn diese Zeitangabe eine Zeitzone besitzt.
	 */
	public final boolean hasZone() {
		return ((this.valueL >> 31) & 0x01) != 0;
	}

	/**
	 * Diese Methode gibt den Jahrestag zurück.
	 * 
	 * @see #yeardayOf(int)
	 * @return Jahrestag ({@code 1..366}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int yeardayValue() throws IllegalStateException {
		return FEMDatetime.yeardayOf__(this.calendardayValue());
	}

	/**
	 * Diese Methode gibt den Wochentag zurück.
	 * 
	 * @see #weekdayOf(int)
	 * @return Wochentag ({@code 1..7}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int weekdayValue() throws IllegalStateException {
		return FEMDatetime.weekdayOf__(this.calendardayValue());
	}

	/**
	 * Diese Methode gibt die Tagesmillis zurück.
	 * 
	 * @see #daymillisOf(int, int, int, int)
	 * @return Tagesmillis ({@code 0..86400000}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int daymillisValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.daymillisValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int daymillisValue__() {
		return FEMDatetime.daymillisOf__(this.hourValue__(), this.minuteValue__(), this.secondValue__(), this.millisecondValue__());
	}

	/**
	 * Diese Methode gibt den Kalendertag zurück.
	 * 
	 * @see #calendardayOf(int, int, int)
	 * @return Kalendertag ({@code 0..3074323}).
	 * @throws IllegalStateException Wenn diese Zeitangabe {@link #hasDate() kein Datum} besitzt.
	 */
	public final int calendardayValue() throws IllegalStateException {
		if (!this.hasDate()) throw new IllegalStateException();
		return this.calendardayValue__();
	}

	@SuppressWarnings ("javadoc")
	private final int calendardayValue__() {
		return FEMDatetime.calendardayOf__(this.yearValue__(), this.monthValue__(), this.dateValue__());
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit dem Datum zum gegebenen Kalendertag zurück.
	 * 
	 * @see #calendardayOf(int, int, int)
	 * @see #withoutDate()
	 * @param calendarday Kalendertag ({@code 0..3074323}).
	 * @return diese Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn {@code calendarday} ungültig ist.
	 */
	public final FEMDatetime withDate(final int calendarday) throws IllegalArgumentException {
		FEMDatetime.checkDate(calendarday);
		final int date = FEMDatetime.dateOf__(calendarday);
		return this.withDate__((date >> 10) & 0x3FFF, (date >> 5) & 0x1F, (date >> 0) & 0x1F);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit dem gegebenen Datum zurück.
	 * 
	 * @see #withoutDate()
	 * @param year Jahr ({@code 1582..9999}).
	 * @param month Monat ({@code 1..12}).
	 * @param date Tag ({@code 1..31}).
	 * @return diese Zeitangabe mit Datum.
	 * @throws IllegalArgumentException Wenn das gegebene Datum ungültig ist.
	 */
	public final FEMDatetime withDate(final int year, final int month, final int date) throws IllegalArgumentException {
		FEMDatetime.checkDate(year, month, date);
		return this.withDate__(year, month, date);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit dem Datum des gegebenen {@link Calendar} zurück.<br>
	 * Die gelieferte Zeitangabe {@link #hasDate() besitzt} nur dann ein Datum, wenn am gegebenen {@link Calendar} die Felder {@link Calendar#YEAR},
	 * {@link Calendar#MONTH} und {@link Calendar#DATE} definiert sind. Andernfalls hat die gelieferte Zeitangabe kein Datum.
	 * 
	 * @see #withoutDate()
	 * @param calendar Datum.
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Datum ungültig ist.
	 */
	public final FEMDatetime withDate(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		if (calendar == null) throw new NullPointerException("calendar = null");
		if (!calendar.isSet(Calendar.YEAR) || !calendar.isSet(Calendar.MONTH) || !calendar.isSet(Calendar.DATE)) return this.withoutDate();
		return this.withDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit dem Datum der gegebnene Zeitangabe zurück.<br>
	 * Wenn die gegebene Zeitangabe kein Datum {@link #hasDate() besitzt}, hat die gelieferte Zeitangabe auch kein Datum.
	 * 
	 * @see #withoutDate()
	 * @param datetime Datum.
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist.
	 */
	public final FEMDatetime withDate(final FEMDatetime datetime) throws NullPointerException {
		if (datetime == null) throw new NullPointerException("datetime = null");
		if (!datetime.hasDate()) return this.withoutDate();
		return this.withDate__(this.yearValue__(), this.monthValue__(), this.dateValue__());
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime withDate__(final int year, final int month, final int date) {
		return new FEMDatetime((this.valueH & 0x3FFD) | (year << 18) | (month << 14) | (1 << 1), (this.valueL & 0xFFF07FFF) | (date << 15));
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der Uhrzeit zu den gegebenen Tagesmillis zurück.
	 * 
	 * @see #daymillisOf(int, int, int, int)
	 * @see #withoutTime()
	 * @param daymillis Tagesmillis ({@code 0..86400000}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn {@code daymillis} ungültig ist.
	 */
	public final FEMDatetime withTime(final int daymillis) throws IllegalArgumentException {
		if ((daymillis < 0) || (daymillis > 86400000)) throw new IllegalArgumentException();
		return this.withTime__(daymillis);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der gegebenen Uhrzeit zurück.
	 * 
	 * @see #withoutTime()
	 * @param hour Stunde ({@code 0..24}).
	 * @param minute Minute ({@code 0..59}).
	 * @param second Sekunde ({@code 0..59}).
	 * @param millisecond Millisekunde ({@code 0..999}).
	 * @return Zeitangabe mit Uhrzeit.
	 * @throws IllegalArgumentException Wenn die gegebenen Uhrzei ungültig ist.
	 */
	public final FEMDatetime withTime(final int hour, final int minute, final int second, final int millisecond) throws IllegalArgumentException {
		FEMDatetime.checkTime(hour, minute, second, millisecond);
		return this.withTime__(hour, minute, second, millisecond);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der Uhrzeit des gegebenen {@link Calendar} zurück.<br>
	 * Die gelieferte Zeitangabe {@link #hasTime() besitzt} nur dann eine Uhrzeit, wenn am gegebenen {@link Calendar} die Felder {@link Calendar#HOUR_OF_DAY},
	 * {@link Calendar#MINUTE} und {@link Calendar#SECOND} definiert sind. Andernfalls hat die gelieferte Zeitangabe keine Uhrzeit.
	 * 
	 * @see #withoutTime()
	 * @param calendar Uhrzeit.
	 * @return Zeitangabe mit oder ohne Datum.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Uhrzeit ungültig ist.
	 */
	public final FEMDatetime withTime(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		if (calendar == null) throw new NullPointerException("calendar = null");
		if (!calendar.isSet(Calendar.HOUR) || !calendar.isSet(Calendar.MINUTE) || !calendar.isSet(Calendar.SECOND)) return this.withoutTime();
		return this.withTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der Uhrzeit der gegebnene Zeitangabe zurück.<br>
	 * Wenn die gegebene Zeitangabe keine Uhrzeit {@link #hasTime() besitzt}, hat die gelieferte Zeitangabe auch keine Uhrzeit.
	 * 
	 * @see #withoutTime()
	 * @param datetime Uhrzeit.
	 * @return Zeitangabe mit oder ohne Uhrzeit.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist.
	 */
	public final FEMDatetime withTime(final FEMDatetime datetime) throws NullPointerException {
		if (datetime == null) throw new NullPointerException("datetime = null");
		if (!datetime.hasTime()) return this.withoutTime();
		return this.withTime__(datetime.hourValue__(), datetime.minuteValue__(), datetime.secondValue__(), datetime.millisecondValue__());
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime withTime__(final int daymillis) {
		final int hour = daymillis / 3600000, hourmillis = daymillis % 3600000;
		final int minute = hourmillis / 60000, minutemillis = hourmillis % 60000;
		final int second = minutemillis / 1000, millisecond = minutemillis % 1000;
		return this.withTime__(hour, minute, second, millisecond);
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime withTime__(final int hour, final int minute, final int second, final int millisecond) {
		return new FEMDatetime((this.valueH & 0xFFFFC002) | (minute << 8) | (second << 2) | (1 << 0), //
			(this.valueL & 0xFFFF8000) | (hour << 10) | (millisecond << 0));
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der gegebenen Zeitzone zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.
	 * 
	 * @see #moveZone(int, int)
	 * @see #withoutZone()
	 * @param zone Zeitzone ({@code -840..840})
	 * @return Zeitangabe mit Zeitzone.
	 * @throws IllegalArgumentException Wenn die gegebenen Zeitzone ungültig ist.
	 */
	public final FEMDatetime withZone(final int zone) throws IllegalArgumentException {
		FEMDatetime.checkZone(zone);
		if (!this.hasZone()) return this.withZone__(zone);
		return this.moveZone(0, zone - this.zoneValue__());
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der gegebenen Zeitzone zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.
	 * 
	 * @see #withoutZone()
	 * @param zoneHour Stunde der Zeitzone ({@code -14..14}).
	 * @param zoneMinute Minute der Zeitzone ({@code 0..59}).
	 * @return Zeitangabe mit Zeitzone.
	 * @throws IllegalArgumentException Wenn die gegebenen Zeitzone ungültig ist.
	 */
	public final FEMDatetime withZone(final int zoneHour, final int zoneMinute) throws IllegalArgumentException {
		if ((zoneHour == -14) || (zoneHour == 14)) {
			if (zoneMinute != 0) throw new IllegalArgumentException();
		} else {
			if ((zoneHour < -14) || (zoneHour > 14)) throw new IllegalArgumentException();
			if ((zoneMinute < 0) || (zoneMinute > 59)) throw new IllegalArgumentException();
		}
		final int zone = (zoneHour * 60) + (zoneHour < 0 ? -zoneMinute : zoneMinute);
		if (!this.hasZone()) return this.withZone__(zone);
		return this.moveZone__(zone - this.zoneValue__());
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der Zeitzone des gegebenen {@link Calendar} zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.<br>
	 * Wenn am gegebenen {@link Calendar} das Feld {@link Calendar#ZONE_OFFSET} undefiniert ist, hat die gelieferte Zeitangabe keine Zeitzone.
	 * 
	 * @see #withoutZone()
	 * @param calendar Zeitzone.
	 * @return Zeitangabe mit oder ohne Zeitzone.
	 * @throws NullPointerException Wenn {@code calendar} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeitzone ungültig ist.
	 */
	public final FEMDatetime withZone(final Calendar calendar) throws NullPointerException, IllegalArgumentException {
		if (calendar == null) throw new NullPointerException("calendar = null");
		if (!calendar.isSet(Calendar.ZONE_OFFSET)) return this.withoutZone();
		final int zone = calendar.get(Calendar.ZONE_OFFSET) / 60000;
		if (!this.hasZone()) return this.withZone__(zone);
		return this.moveZone__(zone - this.zoneValue__());
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit der Zeitzone der gegebenen Zeitangabe zurück.<br>
	 * Wenn diese Zeitangabe bereits eine Zeitzone besitzt, werden Uhrzeit und Datum sofern vorhanden entsprechend angepasst.<br>
	 * Wenn die gegebene Zeitangabe keine Zeitzone {@link #hasZone() besitzt}, hat die gelieferte Zeitangabe auch keine Zeitzone.
	 * 
	 * @see #withoutZone()
	 * @param datetime Zeitzone.
	 * @return Zeitangabe mit oder ohne Zeitzone.
	 * @throws NullPointerException Wenn {@code datetime} {@code null} ist.
	 */
	public final FEMDatetime withZone(final FEMDatetime datetime) throws NullPointerException {
		if (datetime == null) throw new NullPointerException("datetime = null");
		if (!datetime.hasZone()) return this.withoutZone();
		final int zone = datetime.zoneValue__();
		if (!this.hasZone()) return this.withZone__(zone);
		return this.moveZone__(zone - this.zoneValue__());
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime withZone__(final int zone) {
		return new FEMDatetime(this.valueH, (this.valueL & 0xFFFFF) | (1 << 31) | ((zone + 1024) << 20));
	}

	/**
	 * Diese Methode gibt diese Zeitangabe ohne Datum zurück.
	 * 
	 * @see #withDate(int)
	 * @see #withDate(int, int, int)
	 * @see #withDate(Calendar)
	 * @see #withDate(FEMDatetime)
	 * @return Zeitangabe ohne Datum.
	 */
	public final FEMDatetime withoutDate() {
		if (!this.hasDate()) return this;
		return new FEMDatetime(this.valueH & 0x3FFD, this.valueL & 0xFFF07FFF);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe ohne Uhrzeit zurück.
	 * 
	 * @see #withTime(int)
	 * @see #withTime(int, int, int, int)
	 * @see #withTime(Calendar)
	 * @see #withTime(FEMDatetime)
	 * @return Zeitangabe ohne Uhrzeit.
	 */
	public final FEMDatetime withoutTime() {
		if (!this.hasTime()) return this;
		return new FEMDatetime(this.valueH & 0xFFFFC002, this.valueL & 0xFFFF8000);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe ohne Zeitzone zurück.
	 * 
	 * @see #withZone(int)
	 * @see #withZone(int, int)
	 * @see #withZone(Calendar)
	 * @see #withZone(FEMDatetime)
	 * @return Zeitangabe ohne Zeitzone.
	 */
	public final FEMDatetime withoutZone() {
		if (!this.hasZone()) return this;
		return new FEMDatetime(this.valueH, this.valueL & 0xFFFFF);
	}

	/**
	 * Diese Methode gibt einen Zeitpunkt zurück, der diesem um die gegebene Zeitspanne verschoben entspricht.
	 * 
	 * @param value Zeitspanne.
	 * @return verschobener Zeitpunkt.
	 */
	public FEMDatetime move(final FEMDuration value) {
		final int sign = value.signValue();
		if (sign == 0) return this;
		final FEMDatetime v = this.hasDate() ? this.moveDate(value.yearsValue() * sign, value.monthsValue() * sign, value.daysValue() * sign) : this;
		return this.hasTime() ? this.moveTime(value.hoursValue() * sign, value.minutesValue() * sign, value.secondsValue() * sign, value.millisValue() * sign)
			: this;

	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit verschobenem Datum zurück.<br>
	 * Die Verschiebung erfolgt gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#adding-durations-to-dateTimes">XML Schema Part 2: §E Adding
	 * durations to dateTimes</a>. Die Schritte des Algorithmus sind:
	 * <ol>
	 * <li>{@link #yearValue() Jahr} und {@link #monthValue() Monat} gemäß der gegebenen Anzahl an Jahren ({@code years}) und Monaten ({@code months})
	 * verschieben.</li>
	 * <li>{@link #dateValue() Tags} gemäß dem ermittelten Jahr und Monat korrigieren, sodass der Tag nicht größer als die {@link #lengthOf(int, int) Anzahl der
	 * Tage im Monat} ist.</li>
	 * <li>{@link #dateValue() Tag} gemäß der gegebenen Anzahl an Tagen ({@code days}) verschieben.</li>
	 * </ol>
	 * 
	 * @param years Anzahl der Jahre ({@code -8417..8417}).
	 * @param months Anzahl der Monate ({@code -101015..101015}).
	 * @param days Anzahl der Tage ({@code -3074323..3074323}).
	 * @return Zeitangabe mit verschobenem Datum.
	 * @throws IllegalStateException Wenn diese Zeitangabe kein Datum besitzt.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde.
	 */
	public final FEMDatetime moveDate(final int years, final int months, final int days) throws IllegalStateException, IllegalArgumentException {
		if (!this.hasDate()) throw new IllegalStateException();
		if ((years == 0) && (months == 0) && (days == 0)) return this;
		if ((years < -8417) || (years > 8417)) throw new IllegalArgumentException();
		if ((months < -101015) || (months > 101015)) throw new IllegalArgumentException();
		if ((days < -3074323) || (days > 3074323)) throw new IllegalArgumentException();
		return this.moveDate__((years * 12) + months, days);
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime moveDate__(final int monthsAdd, final int daysAdd) throws IllegalArgumentException {
		int value = ((12 * this.yearValue__()) + this.monthValue__() + monthsAdd) - 1;
		final int year = value / 12, month = (value % 12) + 1;
		FEMDatetime.checkYear(year);
		value = this.dateValue__();
		final int length = FEMDatetime.lengthOf(month, year), date = value > length ? length : value;
		value = FEMDatetime.calendardayOf__(year, month, date) + daysAdd;
		return this.withDate(value);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit verschobener Uhrzeit zurück.<br>
	 * Wenn die Zeitangabe ein Datum {@link #hasDate() besitzt}, wird dieses falls nötig ebenfalls verschoben.
	 * 
	 * @see #moveDate(int, int, int)
	 * @param hours Anzahl der Stunden ({@code -73783776..73783775}).
	 * @param minutes Anzahl der Minuten.
	 * @param seconds Anzahl der Sekunden.
	 * @param milliseconds Anzahl der Millisekunden.
	 * @return Zeitangabe mit verschobener Uhrzeit.
	 * @throws IllegalStateException Wenn diese Zeitangabe keine Uhrzeit besitzt.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde.
	 */
	public final FEMDatetime moveTime(final int hours, final int minutes, final int seconds, final int milliseconds) throws IllegalStateException,
		IllegalArgumentException {
		if (!this.hasTime()) throw new IllegalStateException();
		if ((hours == 0) && (minutes == 0) && (seconds == 0) && (milliseconds == 0)) return this;
		if ((hours < -73783776) || (hours > 73783775)) throw new IllegalArgumentException();
		return this.moveTime__((hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L) + milliseconds);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit verschobener Uhrzeit zurück.<br>
	 * Wenn die Zeitangabe ein Datum {@link #hasDate() besitzt}, wird dieses falls nötig ebenfalls verschoben.
	 * 
	 * @see #moveDate(int, int, int)
	 * @param hours Anzahl der Stunden ({@code -73783776..73783775}).
	 * @param minutes Anzahl der Minuten ({@code -4427026560..4427026559}).
	 * @param seconds Anzahl der Sekunden ({@code -265621593600..265621593599}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -265621593600000..265621593599999}).
	 * @return Zeitangabe mit verschobener Uhrzeit.
	 * @throws IllegalStateException Wenn diese Zeitangabe keine Uhrzeit besitzt.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde.
	 */
	public final FEMDatetime moveTime(final int hours, final long minutes, final long seconds, final long milliseconds) throws IllegalStateException,
		IllegalArgumentException {
		if (!this.hasTime()) throw new IllegalStateException();
		if ((hours == 0) && (minutes == 0) && (seconds == 0) && (milliseconds == 0)) return this;
		if ((hours < -73783776) || (hours > 73783775)) throw new IllegalArgumentException();
		if ((minutes < -4427026560L) || (minutes > 4427026559L)) throw new IllegalArgumentException();
		if ((seconds < -265621593600L) || (seconds > 265621593599L)) throw new IllegalArgumentException();
		if ((milliseconds < -265621593600000L) || (milliseconds > 265621593599999L)) throw new IllegalArgumentException();
		return this.moveTime__((hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L) + milliseconds);
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime moveTime__(final long millisecondsAdd) {
		final long value = this.daymillisValue__() + millisecondsAdd;
		final int daysAdd = (int)(value / 86400000), daymillis = (int)(value % 86400000);
		return ((daysAdd != 0) && this.hasDate() ? this.moveDate__(0, daysAdd) : this).withTime__(daymillis);
	}

	/**
	 * Diese Methode gibt diese Zeitangabe mit verschobener Zeitzone zurück.<br>
	 * Wenn die Zeitangabe eine Uhrzeit {@link #hasTime() besitzt}, wird diese falls nötig ebenfalls verschoben.
	 * 
	 * @see #moveTime(int, int, int, int)
	 * @param hours Anzahl der Stunden ({@code -28..28}).
	 * @param minutes Anzahl der Minuten ({@code -1680..1680}).
	 * @return Zeitangabe mit verschobener Zeitzone.
	 * @throws IllegalStateException Wenn diese Zeitangabe keine Zeitzone besitzt.
	 * @throws IllegalArgumentException Wenn die Verschiebung zu einer ungültigen Zeitangabe führen würde.
	 */
	public final FEMDatetime moveZone(final int hours, final int minutes) throws IllegalStateException, IllegalArgumentException {
		if (!this.hasZone()) throw new IllegalStateException();
		if ((hours == 0) && (minutes == 0)) return this;
		if ((hours < -28) || (hours > 28)) throw new IllegalArgumentException();
		if ((minutes < -1680) || (minutes > 1680)) throw new IllegalArgumentException();
		return this.moveZone__((hours * 60) + minutes);
	}

	@SuppressWarnings ("javadoc")
	private final FEMDatetime moveZone__(final int zoneAdd) {
		final int zoneOld = this.zoneValue__(), zoneNew = zoneAdd + zoneOld;
		FEMDatetime.checkZone(zoneNew);
		return ((zoneAdd != 0) && this.hasTime() ? this.moveTime__(zoneAdd * -60000) : this).withZone__(zoneNew);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieser Zeitpunkt gleich dem gegebenen ist.
	 * 
	 * @param value Zeitpunkt.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public boolean equals(final FEMDatetime value) throws NullPointerException {
		return this.compare(value, Integer.MAX_VALUE) == 0;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn dieser Zeitpunkt früger, gleich bzw. später als der gegebene Zeitpunkt
	 * ist. Wenn die Zeitpunkte nicht vergleichbar sind, wird {@code undefined} geliefert.
	 * <p>
	 * Algorithmus gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#dateTime-order">XML Schema Part 2: 3.2.7.3 Order relation on dateTime</a>.
	 * 
	 * @param value Zeitpunkt.
	 * @param undefined Rückgabewert für nicht vergleichbare Zeitpunkte.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public int compare(FEMDatetime value, final int undefined) throws NullPointerException {
		if (this.hasZone() == value.hasZone()) {
			value = value.move(0, 0, 0, 0, this.zoneValue__() - value.zoneValue__(), 0);
			final int result = this.dateValue - value.dateValue;
			if (result != 0) return result;
			return (this.timeValue >> 12) - (value.timeValue >> 12);
		}
		// genäherte Differenz in achtel Tagen
		int result = this.dateValue - value.dateValue;
		if ((result < -50) || (result > 50)) return result;
		// exakte Differenz in Sekunden
		result = (this.secondValue() - value.secondValue()) //
			+ (60 * ((this.minuteValue() + value.zoneValue()) - this.zoneValue() - value.minuteValue())) //
			+ (3600 * (this.hourValue() - value.hourValue())) + (86400 * (FEMDatetime.calendardayOf(this.yearValue__(), this.monthValue(), this.dateValue()) //
			- FEMDatetime.calendardayOf(value.yearValue__(), value.monthValue(), value.dateValue())));
		// 50400 Sekunden = 14 Stunden
		if (this.hasZone()) {
			if (value.hasZone()) return result;
			if (result < -50400) return -1;
			if (result > +50400) return +1;
		} else {
			if (!value.hasZone()) return result;
			if (result < +50400) return -1;
			if (result > -50400) return +1;
		}
		return undefined;
	}

	/**
	 * Diese Methode gibt diese Zeitangabe als {@link Calendar} zurück.
	 * 
	 * @return {@link Calendar}.
	 */
	public GregorianCalendar toCalendar() {
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.clear();
		if (this.hasDate()) {
			calendar.set(Calendar.YEAR, this.yearValue__());
			calendar.set(Calendar.MONTH, (this.monthValue__() - 1));
			calendar.set(Calendar.DATE, this.dateValue__());
		}
		if (this.hasTime()) {
			calendar.set(Calendar.HOUR_OF_DAY, this.hourValue__());
			calendar.set(Calendar.MINUTE, this.minuteValue__());
			calendar.set(Calendar.SECOND, this.secondValue__());
			calendar.set(Calendar.MILLISECOND, this.millisecondValue__());
		}
		if (this.hasZone()) {
			calendar.set(Calendar.ZONE_OFFSET, this.zoneValue__() * 60000);
		}
		return calendar;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.valueH ^ this.valueL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDatetime)) return false;
		return this.equals((FEMDatetime)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatDatetime(this);
	}

}
