package bee.creative.fem;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.fhg.ivi.fee.core.FEE;
import de.fhg.ivi.fee.core.FEE_Values;

/**
 * Diese Klasse implementiert eine Uhrzeit mit Stunden, Minuten und Sekunden sowie Zeitzone.
 * 
 * @author Sebastian Rostock 2011.
 */
public class FEE_Time {




	/**
	 * Diese Methode gibt diese Uhrzeit um die gegebenen Zeitspannen verschoben zurück.
	 * 
	 * @see #move(int, int, int)
	 * @param value Zeitspanne.
	 * @return verschobene Uhrzeit.
	 */
	public FEE_Time move(final FEMDuration value) {
		final int signValue = value.signValue();
		if (signValue == 0) return this;
		return this.move(value.hoursValue() * signValue, value.minutesValue() * signValue, value.secondsValue() * signValue);
	}

	/**
	 * Diese Methode gibt diese Uhrzeit um die gegebenen Zeitspannen verschoben zurück.
	 * <p>
	 * Algorithmus gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#adding-durations-to-dateTimes">XML Schema Part 2: §E
	 * Adding durations to dateTimes</a>.
	 * 
	 * @see #move(FEMDuration)
	 * @param hours Verschiebung der Stunden.
	 * @param minutes Verschiebung der Minuten.
	 * @param seconds Verschiebung der Sekunden.
	 * @return verschobene Uhrzeit.
	 */
	public FEE_Time move(final int hours, final int minutes, final int seconds) {
		if ((hours == 0) && (minutes == 0) && (seconds == 0)) return this;
		// Uhrzeit verschieben und normalisieren
		int value = this.secondValue() + seconds + (60 * (this.minuteValue() + minutes)) + (3600 * (this.hourValue() + hours));
		if (value < 0) {
			// übertragen von 24855 Tage in Sekunden als größter positiver Integer
			value += 2147472000;
		}
		final int secondValue = value % 60;
		value = value / 60;
		final int minuteValue = value % 60;
		value = value / 60;
		final int hourValue = value % 24;
		return new FEE_Time(this.hasTimezone() ? FEMDatetime.compileTime(hourValue, minuteValue, secondValue) : FEMDatetime.compileTime(hourValue,
			minuteValue, secondValue, this.timezoneValue()));
	}

	/**
	 * Diese Methode gibt diese Uhrzeit mit der gegebenen Zeitzonenverschiebung zurück. Wenn {@link #hasTimezone()} angibt, dass keine
	 * Zeitzonenverschiebung vorhanden ist, wird die gegebene Zeitzonenverschiebung einfach übernommen. Andernfalls wird die Uhrzeit
	 * entsprechend der Differenz von {@code timezoneValue} und {@link #timezoneValue()} verschoben.
	 * 
	 * @see #localize(FEE_Time)
	 * @see #delocalize()
	 * @param timezone Zeitzonenverschiebung.
	 * @return Uhrzeit mit Zeitzonenverschiebung.
	 */
	public FEE_Time localize(final int timezone) {
		if ((timezone < -840) || (timezone > 840)) throw FEMDatetime.illegalTimezone(timezone);
		final int value = this.hasTimezone() ? this.move(0, timezone - this.timezoneValue(), 0).value : this.value;
		return new FEE_Time((value & ~4095) | 2048 | (timezone + 1024));
	}

	/**
	 * Diese Methode gibt diese Uhrzeit mit der Zeitzonenverschiebung der gegebenen Uhrzeit zurück. Wenn die gegebene Uhrzeit eine
	 * Zeitzonenverschiebung besitzt, wird diese an {@link #localize(int)} delegiert und damit der Rückgabewert berechnet. Andernfalls wird
	 * der Rückgabewert über {@link #delocalize()} ermittelt.
	 * 
	 * @see #localize(int)
	 * @see #delocalize()
	 * @param value Vorgabe für die Zeitzonenverschiebung.
	 * @return Uhrzeit mit entsprechender Zeitzonenverschiebung.
	 */
	public FEE_Time localize(final FEE_Time value) {
		return value.hasTimezone() ? this.localize(value.timezoneValue()) : this.delocalize();
	}

	/**
	 * Diese Methode gibt diese Uhrzeit mit der Zeitzonenverschiebung des gegebenen Zeitpunkts zurück. Wenn der gegebene Zeitpunkt eine
	 * Zeitzonenverschiebung besitzt, wird diese an {@link #localize(int)} delegiert und damit der Rückgabewert berechnet. Andernfalls wird
	 * der Rückgabewert über {@link #delocalize()} ermittelt.
	 * 
	 * @see #localize(int)
	 * @see #delocalize()
	 * @param value Vorgabe für die Zeitzonenverschiebung.
	 * @return Uhrzeit mit entsprechender Zeitzonenverschiebung.
	 */
	public FEE_Time localize(final FEMDatetime value) {
		return value.hasTimezone() ? this.localize(value.timezoneValue()) : this.delocalize();
	}

	/**
	 * Diese Methode gibt diese Uhrzeit ohne Zeitzonenverschiebung zurück.
	 * 
	 * @see #localize(int)
	 * @see #localize(FEE_Time)
	 * @return Uhrzeit ohne Zeitzonenverschiebung.
	 */
	public FEE_Time delocalize() {
		if ((this.value & 2048) == 0) return this;
		return new FEE_Time((this.value & ~4095) | 1024);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Uhrzeit gleich der gegebenen ist.
	 * 
	 * @param value Uhrzeit.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final boolean equals(final FEE_Time value) throws NullPointerException {
		return this.compare(value, Integer.MAX_VALUE) == 0;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn diese Uhrzeit früger, gleich bzw. später als die
	 * gegebene Uhrzeit ist. Wenn die Uhrzeiten nicht vergleichbar sind, wird {@code undefined} geliefert.
	 * <p>
	 * Algorithmus gemäß <a href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#dateTime-order">XML Schema Part 2: 3.2.7.3 Order
	 * relation on dateTime</a>.
	 * 
	 * @param value Uhrzeit.
	 * @param undefined Rückgabewert für nicht vergleichbare Uhrzeiten.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEE_Time value, final int undefined) throws NullPointerException {
		// 50400 Sekunden = 14 Stunden
		final int result = this.totalMillis() - value.totalMillis();
		if (this.hasTimezone()) {
			if (value.hasTimezone()) return result;
			if (result < -50400) return -1;
			if (result > +50400) return +1;
		} else {
			if (!value.hasTimezone()) return result;
			if (result < +50400) return -1;
			if (result > -50400) return +1;
		}
		return undefined;
	}

	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEE_Time)) return false;
		final FEE_Time data = (FEE_Time) object;
		return this.compare(data, Integer.MAX_VALUE) == 0;
	}


	/**
	 * Diese Methode gibt die Uhrzeit als {@link Calendar} zurück.
	 * 
	 * @return {@link Calendar}.
	 */
	public GregorianCalendar toCalendar() {
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.clear();
		if (this.hasTimezone()) {
			calendar.set(Calendar.ZONE_OFFSET, this.timezoneValue() * 60000);
		} else {
			calendar.clear(Calendar.ZONE_OFFSET);
		}
		calendar.set(Calendar.HOUR_OF_DAY, this.hourValue());
		calendar.set(Calendar.MINUTE, this.minuteValue());
		calendar.set(Calendar.SECOND, this.secondValue());
		return calendar;
	}

}