package bee.creative.fem;

import bee.creative.util.Comparators;
import bee.creative.util.Integers;

/** Diese Klasse implementiert eine unveränderliche Zeitspanne aus Jahren, Monaten, Tagen, Stunden, Minuten, Sekunden und Millisekunden. Intern wird die
 * Zeitspanne als ein {@code long} dargestellt.
 * <h5><a name="durationmonths">Gesamtanzahl der Monate</a></h5>
 * <p>
 * Der relative Anteil einer Zeitspanne ist durch die {@link #yearsValue() Jahre} und {@link #monthsValue() Monate} gegeben, welche zur
 * {@link #durationmonthsValue() Gesamtanzahl an Monaten} zusammengefasst werden können. Diese Gesamtanzahl liegt im Bereich {@code -119999..119999}. Wenn ein
 * Datum um diese Monatsanzahl verschoben werden soll, entscheidet erst dieses Datum über die konkrete Anzahl an Tagen, um die das Datum verschoben wird. Man
 * kann zu einer Monatsanzahl jedoch ermitteln, zu wie vielen Tagen diese {@link #minLengthOf(int) minimal} bzw. {@link #maxLengthOf(int) maximal} führen kann.
 * </p>
 * <h5><a name="durationmillis">Gesamtanzahl der Millisekunden</a></h5>
 * <p>
 * Der absolute Anteil einer Zeitspanne ist durch {@link #daysValue() Tage}, {@link #hoursValue() Stunden}, {@link #minutesValue() Minuten},
 * {@link #secondsValue() Sekunden} und {@link #millisecondsValue() Millisekunden} gegeben, welche zur {@link #durationmillisValue() Gesamtanzahl an
 * Millisekunden} zusammengefasst werden können. Diese Gesamtanzahl liegt im Bereich {@code -315569519999999..315569519999999}, d.h. plus/minus eine
 * Millisekunde weniger als 10000 Jahre.
 * </p>
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDuration extends FEMValue implements Comparable<FEMDuration> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 8;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMDuration> TYPE = FEMType.from(FEMDuration.ID);

	/** Dieses Feld speichert die leere Zeitspanne, deren Komponenten {@code 0} sind. */
	public static final FEMDuration EMPTY = new FEMDuration(0, 0);

	/** Dieses Feld speichert die größte negative Zeitspanne. */
	public static final FEMDuration MINIMUM = new FEMDuration(-1673560325, -1901318169);

	/** Dieses Feld speichert die größte positive Zeitspanne. */
	public static final FEMDuration MAXIMUM = new FEMDuration(-1673691397, -1901318169);

	/** Dieses Feld speichert die Ergebnisse von {@link #rangeOf(int)}. */
	static final byte[] ranges = {0, 18, 33, 18, 33, 18, 33, 33, 33, 48, 33, 48, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49,
		49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 16, 19, 34, 19, 34, 19, 34, 34, 34, 49, 34, 49, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50,
		50, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 17, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35,
		35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 17, 19, 34, 34, 34, 34, 34, 49, 34, 49, 34, 49, 16, 19, 34, 19, 34,
		19, 34, 49, 34, 49, 34, 49, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 17, 19, 34, 34, 34, 34, 34, 49,
		34, 49, 34, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50,
		50, 17, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35,
		35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 17, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50,
		35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49,
		34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49,
		49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17, 34,
		34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50,
		35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 50,
		50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 34,
		49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19,
		34, 34, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34,
		34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50,
		50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50,
		65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35,
		50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34,
		34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49,
		49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50,
		65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35,
		50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50,
		50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 34, 49,
		49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49,
		64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34,
		49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50,
		35, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65,
		50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49,
		49, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34,
		34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50,
		35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66,
		51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66,
		66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51,
		51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50,
		35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 35, 50, 65,
		50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66,
		66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 51,
		51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 51,
		51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50,
		50, 65, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50,
		65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35,
		50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51,
		51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66,
		66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50,
		65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35,
		50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50,
		50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66,
		66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66,
		81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 33, 51,
		66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 35, 50,
		50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65,
		65, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66,
		66, 33, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66, 51, 66, 81, 66, 81, 66, 81, 33, 51,
		51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66,
		51, 66, 81, 66, 81, 66, 81, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65,
		50, 65, 65, 65, 48, 51, 66, 51, 66, 51, 66, 81, 66, 81, 66, 81, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65,
		65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 48, 51, 66, 51, 66, 51, 66, 81, 66, 81, 66, 81, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50,
		50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 48, 51, 66, 51, 66, 66, 66, 81, 66, 81, 66, 81, 33, 51, 66, 51, 66,
		51, 66, 66, 66, 81, 66, 81, 33, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65,
		50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49,
		49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17, 35,
		35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 33, 36, 51, 36, 51, 36, 51, 51, 51, 66, 51, 66, 18, 35, 35, 35, 50,
		35, 50, 50, 50, 50, 50, 50, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 36, 51, 36, 51, 36, 51, 66,
		51, 66, 51, 66, 18, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51,
		66, 33, 36, 51, 36, 51, 36, 51, 66, 51, 66, 51, 66, 18, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36,
		51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36, 51, 36, 51, 36, 51, 66, 51, 66, 51, 66, 18, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35,
		35, 50, 50, 50, 50, 50, 50, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36, 51, 36, 51, 36, 51, 66, 51, 66, 51, 66, 18, 35, 50, 35, 50, 35, 50, 50,
		50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51,
		66, 18, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36,
		51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 18, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51,
		51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50,
		50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50,
		65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35,
		50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51,
		51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 51, 66,
		66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50,
		65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35,
		50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50,
		50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66,
		51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66,
		66, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51,
		51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50,
		35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65,
		50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66,
		66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51,
		51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66,
		51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65,
		50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65,
		65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 50,
		50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35,
		35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 50,
		50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34,
		49, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19,
		34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34,
		34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50,
		50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50,
		65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35,
		50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34,
		34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49,
		49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50,
		65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35,
		50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 35, 50,
		50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49,
		49, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49,
		64, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34,
		34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50,
		35, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65,
		50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49,
		49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34,
		34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49,
		34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65,
		50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65,
		65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 32, 50,
		50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 34, 34, 49,
		34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 64,
		49, 64, 49, 64, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50,
		65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 35,
		50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50,
		50, 65, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 49, 34, 49, 34, 49, 49,
		49, 64, 49, 64, 32, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 34, 49, 34, 49, 49, 49, 64, 49, 64, 49,
		64, 16, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 18,
		33, 18, 33, 33, 33, 48, 33, 48, 33, 48};

	/** Diese Methode gibt eine neue Zeitspanne mit dem in der gegebenen Zeichenkette kodierten Wert zurück. Das Format der Zeichenkette entspricht dem der
	 * {@link #toString() Textdarstellung}.
	 *
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @return Zeitspanne.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMDuration from(final String string) throws NullPointerException, IllegalArgumentException {
		int size, offset, symbol;

		final int length;
		final char[] buffer;
		{
			length = string.length() - 1;
			if ((length < 2) || (length > 45)) throw new IllegalArgumentException();
			buffer = string.toCharArray();
			symbol = buffer[0];
		}

		final boolean negate;
		if (symbol == '-') {
			symbol = buffer[1];
			negate = true;
			offset = 2;
		} else {
			negate = false;
			offset = 1;
		}

		{
			if (symbol != 'P') throw new IllegalArgumentException();
			size = Integers.integerSize(buffer, offset, length - offset);
			offset += size;
			symbol = buffer[offset];
			offset += 1;
		}

		final int years;
		if (symbol == 'Y') {
			if ((size < 1) || (size > 4)) throw new IllegalArgumentException();
			years = Integers.parseInt(buffer, offset - size - 1, size);
			if (offset > length) return negate //
				? FEMDuration.from(-years, 0, 0, 0, 0, 0, 0) //
				: FEMDuration.from(years, 0, 0, 0, 0, 0, 0);
			size = Integers.integerSize(buffer, offset, length - offset);
			offset += size;
			symbol = buffer[offset];
			offset += 1;
		} else {
			years = 0;
		}

		final int months;
		if (symbol == 'M') {
			if ((size < 1) || (size > 6)) throw new IllegalArgumentException();
			months = Integers.parseInt(buffer, offset - size - 1, size);
			if (offset > length) return negate //
				? FEMDuration.from(-years, -months, 0, 0, 0, 0, 0) //
				: FEMDuration.from(years, months, 0, 0, 0, 0, 0);
			size = Integers.integerSize(buffer, offset, length - offset);
			offset += size;
			symbol = buffer[offset];
			offset += 1;
		} else {
			months = 0;
		}

		final int days;
		if (symbol == 'D') {
			if ((size < 1) || (size > 7)) throw new IllegalArgumentException();
			days = Integers.parseInt(buffer, offset - size - 1, size);
			if (offset > length) return negate //
				? FEMDuration.from(-years, -months, -days, 0, 0, 0, 0) //
				: FEMDuration.from(years, months, days, 0, 0, 0, 0);
			size = 0;
			symbol = buffer[offset];
			offset += 1;
		} else {
			days = 0;
		}

		{
			if ((symbol != 'T') || (size != 0) || (offset > length)) throw new IllegalArgumentException();
			size = Integers.integerSize(buffer, offset, length - offset);
			offset += size;
			symbol = buffer[offset];
			offset += 1;
		}

		final int hours;
		if (symbol == 'H') {
			if ((size < 1) || (size > 8)) throw new IllegalArgumentException();
			hours = Integers.parseInt(buffer, offset - size - 1, size);
			if (offset > length) return negate //
				? FEMDuration.from(-years, -months, -days, -hours, 0, 0, 0) //
				: FEMDuration.from(years, months, days, hours, 0, 0, 0);
			size = Integers.integerSize(buffer, offset, length - offset);
			offset += size;
			symbol = buffer[offset];
			offset += 1;
		} else {
			hours = 0;
		}

		final long minutes;
		if (symbol == 'M') {
			if ((size < 1) || (size > 10)) throw new IllegalArgumentException();
			minutes = Integers.parseLong(buffer, offset - size - 1, size);
			if (offset > length) return negate //
				? FEMDuration.from(-years, -months, -days, -hours, -minutes, 0, 0) //
				: FEMDuration.from(years, months, days, hours, minutes, 0, 0);
			size = Integers.integerSize(buffer, offset, length - offset);
			offset += size;
			symbol = buffer[offset];
			offset += 1;
		} else {
			minutes = 0;
		}

		final long seconds;
		{
			if ((size < 1) || (size > 12)) throw new IllegalArgumentException();
			seconds = Integers.parseLong(buffer, offset - size - 1, size);
		}

		final int milliseconds;
		if (symbol == '.') {
			size = Integers.integerSize(buffer, offset, length - offset);
			if (size != 3) throw new IllegalArgumentException();
			milliseconds = Integers.parseInt(buffer, offset, 3);
			symbol = buffer[offset + 3];
			offset += 4;
		} else {
			milliseconds = 0;
		}

		{
			if ((symbol != 'S') || (offset <= length)) throw new IllegalArgumentException();
			return negate //
				? FEMDuration.from(-years, -months, -days, -hours, -minutes, -seconds, -milliseconds) //
				: FEMDuration.from(years, months, days, hours, minutes, seconds, milliseconds);
		}

	}

	/** Diese Methode gibt eine Zeitspanne mit den gegebenen Gesamtanzahlen an Monaten und Millisekunden zurück.
	 *
	 * @param durationmonths Gesamtanzahl der Monate ({@code -119999..119999}).
	 * @param durationmillis Gesamtanzahl der Millisekunden ({@code -315569519999999..315569519999999}).
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public static FEMDuration from(int durationmonths, long durationmillis) throws IllegalArgumentException {
		FEMDuration.checkMonths(+durationmonths);
		FEMDuration.checkMonths(-durationmonths);
		FEMDuration.checkMilliseconds(+durationmillis);
		FEMDuration.checkMilliseconds(-durationmillis);
		int negate = 0, years, months, days, hours, minutes, seconds, milliseconds;
		if (durationmillis < 0) {
			durationmonths = -durationmonths;
			durationmillis = -durationmillis;
			negate = 1;
		}
		// "durationmillis" auf "durationmonths" übertragen
		durationmonths += (int)(durationmillis / 12622780800000L) * 4800;
		durationmillis = durationmillis % 12622780800000L;
		// Vorzeichen prüfen
		if (durationmillis > 0) {
			if (durationmonths < 0) throw new IllegalArgumentException();
		} else if (durationmonths < 0) {
			negate ^= 1;
			durationmonths = -durationmonths;
		} else if (durationmonths == 0) return FEMDuration.EMPTY;
		days = (int)(durationmillis / 86400000);
		milliseconds = (int)(durationmillis % 86400000);
		hours = milliseconds / 3600000;
		milliseconds = milliseconds % 3600000;
		minutes = milliseconds / 60000;
		milliseconds = milliseconds % 60000;
		seconds = milliseconds / 1000;
		milliseconds = milliseconds % 1000;
		months = durationmonths % 12;
		years = durationmonths / 12;
		FEMDuration.checkYears(years);
		return new FEMDuration( //
			(years << 18) | (negate << 17) | (hours << 12) | (minutes << 6) | (seconds << 0), //
			(days << 14) | (months << 10) | (milliseconds << 0));
	}

	/** Diese Methode gibt eine Zeitspanne mit den Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @see #from(int, long)
	 * @see #durationmonthsOf(int, int)
	 * @see #durationmillisOf(int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -9999..9999}).
	 * @param months Anzahl der Monate ({@code -119999..119999}).
	 * @param days Anzahl der Tage ({@code -3652424..3652424}).
	 * @param hours Anzahl der Stunden ({@code -87658199..87658199}).
	 * @param minutes Anzahl der Minuten.
	 * @param seconds Anzahl der Sekunden.
	 * @param milliseconds Anzahl der Millisekunden.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public static FEMDuration from(final int years, final int months, final int days, final int hours, final int minutes, final int seconds,
		final int milliseconds) throws IllegalArgumentException {
		return FEMDuration.EMPTY.move(years, months, days, hours, minutes, seconds, milliseconds);
	}

	/** Diese Methode gibt eine Zeitspanne mit den Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @see #from(int, long)
	 * @see #durationmonthsOf(int, int)
	 * @see #durationmillisOf(int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -9999..9999}).
	 * @param months Anzahl der Monate ({@code -119999..119999}).
	 * @param days Anzahl der Tage ({@code -3652424..3652424}).
	 * @param hours Anzahl der Stunden ({@code -87658199..87658199}).
	 * @param minutes Anzahl der Minuten ({@code -5259491999..5259491999}).
	 * @param seconds Anzahl der Sekunden ({@code -315569519999..315569519999}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -315569519999999..315569519999999}).
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public static FEMDuration from(final int years, final int months, final int days, final int hours, final long minutes, final long seconds,
		final long milliseconds) throws IllegalArgumentException {
		return FEMDuration.EMPTY.move(years, months, days, hours, minutes, seconds, milliseconds);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMDuration.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Zeitspanne.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMDuration from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMDuration.TYPE);
	}

	@SuppressWarnings ("javadoc")
	static void checkDays(final int days) throws IllegalArgumentException {
		if (days > 3652424) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkYears(final int years) throws IllegalArgumentException {
		if (years > 9999) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkMonths(final int months) throws IllegalArgumentException {
		if (months > 119999) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkHours(final int hours) throws IllegalArgumentException {
		if (hours > 87658199) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkMinutes(final long minutes) throws IllegalArgumentException {
		if (minutes > 5259491999L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkSeconds(final long seconds) throws IllegalArgumentException {
		if (seconds > 315569519999L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkMilliseconds(final long milliseconds) throws IllegalArgumentException {
		if (milliseconds > 315569519999999L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static void checkPositive(final int value) throws IllegalArgumentException {
		if (value < 0) throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Zeitspanne zwischen den gegebenen Zeitangaben in Zeitzone {@code 00:00} zurück.
	 *
	 * @see FEMDatetime#withZone(int)
	 * @param datetime1 erste Zeitangabe.
	 * @param datetime2 zweite Zeitangabe.
	 * @return Zeitspanne von der ersten zur zweiten Zeitangabe.
	 * @throws NullPointerException Wenn {@code datetime1} bzw. {@code datetime2} {@code null} ist.
	 * @throws IllegalArgumentException Wenn nur eine der Zeitangaben ein Datum bzw eine Uhrzeit besitzt. */
	public static FEMDuration between(final FEMDatetime datetime1, final FEMDatetime datetime2) throws NullPointerException, IllegalArgumentException {
		if (datetime1.hasDate()) {
			if (!datetime2.hasDate()) throw new IllegalArgumentException();
			if (datetime1.hasTime()) {
				if (!datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, datetime2.calendardayValueImpl() - datetime1.calendardayValueImpl(), //
					0, datetime1.zoneValueImpl() - datetime2.zoneValueImpl(), 0, datetime2.daymillisValueImpl() - datetime1.daymillisValueImpl());
			} else {
				if (datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, datetime2.calendardayValueImpl() - datetime1.calendardayValueImpl(), //
					0, datetime1.zoneValueImpl() - datetime2.zoneValueImpl(), 0, 0);
			}
		} else {
			if (datetime2.hasDate()) throw new IllegalArgumentException();
			if (datetime1.hasTime()) {
				if (!datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, 0, 0, datetime1.zoneValueImpl() - datetime2.zoneValueImpl(), 0,
					datetime2.daymillisValueImpl() - datetime1.daymillisValueImpl());
			} else {
				if (datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, 0, 0, datetime1.zoneValueImpl() - datetime2.zoneValueImpl(), 0, 0);
			}
		}
	}

	/** Diese Methode gibt die minimale Anzahl an Tagen zurück, die durch die gegebene Anzahl an Monaten ausgedrückt werden kann.
	 *
	 * @see FEMDatetime#move(int, int, int, int, long, long, long)
	 * @param months Anzahl der Monate ({@code 0..119999}).
	 * @return minimale Anzahl an Tagen in den gegebenen Monaten.
	 * @throws IllegalArgumentException Wenn {@code months} ungültig ist. */
	public static int minLengthOf(final int months) throws IllegalArgumentException {
		FEMDuration.checkMonths(months);
		FEMDuration.checkPositive(months);
		return FEMDuration.lengthOf(months) - ((FEMDuration.rangeOf(months) >> 0) & 0x0F);
	}

	/** Diese Methode gibt die maximale Anzahl an Tagen zurück, die durch die gegebene Anzahl an Monaten ausgedrückt werden kann.
	 *
	 * @see FEMDatetime#move(int, int, int, int, long, long, long)
	 * @param months Anzahl der Monate ({@code 0..119999}).
	 * @return maximale Anzahl an Tagen in den gegebenen Monaten.
	 * @throws IllegalArgumentException Wenn {@code months} ungültig ist. */
	public static int maxLengthOf(final int months) throws IllegalArgumentException {
		FEMDuration.checkMonths(months);
		FEMDuration.checkPositive(months);
		return FEMDuration.lengthOf(months) + ((FEMDuration.rangeOf(months) >> 4) & 0x0F);
	}

	/** Diese Methode gibt das Paar aus Min und Max zur Ergänzung von {@link #lengthOf(int)} zu {@link #minLengthOf(int)} und {@link #maxLengthOf(int)} zurück.
	 * <p>
	 * Das Paar besteht aus {@code (MAX << 4) | (MIN << 0)}.
	 * <p>
	 * Für die Min- und maximalen Monatslängen gilt damit: {@code minLengthOf(i) = _lengthOf_(i) - MIN} sowie {@code maxLengthOf(i) = _lengthOf_(i) + MAX}.
	 *
	 * @param months Anzahl an Monaten ({@code 0..119999}).
	 * @return Min-Max-Paar. */
	static int rangeOf(final int months) {
		return FEMDuration.ranges[months % 4800];
	}

	/** Diese Methode gibt die mittlere Anzahl an Tagen zurück, die durch die gegebene Anzahl an Monaten ausgedrückt werden kann.
	 *
	 * @param months Anzahl der Monate ({@code 0..119999}).
	 * @return mittlere Anzahl an Tagen in den gegebenen Monaten. */
	static int lengthOf(final int months) {
		return (months * 146097) / 4800;
	}

	/** Diese Methode gibt die Gesamtanzahl der Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @param days Anzahl der Tage ({@code -3652424..3652424}).
	 * @param hours Anzahl der Stunden ({@code -87658199..87658199}).
	 * @param minutes Anzahl der Minuten.
	 * @param seconds Anzahl der Sekunden.
	 * @param milliseconds Anzahl der Millisekunden.
	 * @return die Gesamtanzahl der Millisekunden.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen ungültig sind oder zu einer ungültigen Gesamtanzahl führen würde. */
	public static long durationmillisOf(final int days, final int hours, final int minutes, final int seconds, final int milliseconds)
		throws IllegalArgumentException {
		FEMDuration.checkDays(+days);
		FEMDuration.checkDays(-days);
		FEMDuration.checkHours(+hours);
		FEMDuration.checkHours(-hours);
		final long result = FEMDuration.durationmillisOfImpl(days, hours, minutes, seconds, milliseconds);
		FEMDuration.checkMilliseconds(+result);
		FEMDuration.checkMilliseconds(-result);
		return result;
	}

	/** Diese Methode gibt die Gesamtanzahl der Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @param days Anzahl der Tage ({@code -3652424..3652424}).
	 * @param hours Anzahl der Stunden ({@code -87658199..87658199}).
	 * @param minutes Anzahl der Minuten ({@code -5259491999..5259491999}).
	 * @param seconds Anzahl der Sekunden ({@code -315569519999..315569519999}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -315569519999999..315569519999999}).
	 * @return die Gesamtanzahl der Millisekunden ({@code -315569519999999..315569519999999}).
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen ungültig sind oder zu einer ungültigen Gesamtanzahl führen würde. */
	public static long durationmillisOf(final int days, final int hours, final long minutes, final long seconds, final long milliseconds)
		throws IllegalArgumentException {
		FEMDuration.checkDays(+days);
		FEMDuration.checkDays(-days);
		FEMDuration.checkHours(+hours);
		FEMDuration.checkHours(-hours);
		FEMDuration.checkMinutes(+minutes);
		FEMDuration.checkMinutes(-minutes);
		FEMDuration.checkSeconds(+seconds);
		FEMDuration.checkSeconds(-seconds);
		final long result = FEMDuration.durationmillisOfImpl(days, hours, minutes, seconds, milliseconds);
		FEMDuration.checkMilliseconds(+result);
		FEMDuration.checkMilliseconds(-result);
		return result;
	}

	@SuppressWarnings ("javadoc")
	static long durationmillisOfImpl(final int days, final int hours, final long minutes, final long seconds, final long milliseconds)
		throws IllegalArgumentException {
		return (days * 86400000L) + (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L) + milliseconds;
	}

	/** Diese Methode gibt die Gesamtanzahl der Monate zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @param years Anzahl der Jahre ({@code -9999..9999}).
	 * @param months Anzahl der Monate ({@code -119999..119999}).
	 * @return Gesamtanzahl der Monate ({@code -119999..119999}).
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen ungültig sind oder zu einer ungültigen Gesamtanzahl führen würde. */
	public static int durationmonthsOf(final int years, final int months) throws IllegalArgumentException {
		FEMDuration.checkYears(+years);
		FEMDuration.checkYears(-years);
		FEMDuration.checkMonths(-months);
		FEMDuration.checkMonths(+months);
		final int result = FEMDuration.durationmonthsOfImpl(years, months);
		FEMDuration.checkMonths(+result);
		FEMDuration.checkMonths(-result);
		return result;
	}

	@SuppressWarnings ("javadoc")
	static int durationmonthsOfImpl(final int years, final int months) {
		return (years * 12) + months;
	}

	/** Dieses Feld speichert die 32 LSB der internen 64 Bit Darstellung dieser Zeitspanne.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>daysValue - 18 Bit</li>
	 * <li>monthsValue - 4 Bit</li>
	 * <li>millisecondsValue - 10 Bit</li>
	 * </ul>
	*/
	final int valueL;

	/** Dieses Feld speichert die 32 MSB der internen 64 Bit Darstellung dieser Zeitspanne.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>yearsValue - 14 Bit</li>
	 * <li>signValue - 1 Bit</li>
	 * <li>hoursValue - 5 Bit</li>
	 * <li>minutesValue - 6 Bit</li>
	 * <li>secondsValue - 6 Bit</li>
	 * </ul>
	*/
	final int valueH;

	/** Dieser Konstruktor initialisiert die interne Darstellung der Zeitspanne.
	 *
	 * @see #value()
	 * @param value interne Darstellung der Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist. */
	public FEMDuration(final long value) throws IllegalArgumentException {
		this(Integers.toIntH(value), Integers.toIntL(value));
		if (value == 0) return;
		FEMDuration.checkYears(this.yearsValue());
		if (this.monthsValue() > 11) throw new IllegalArgumentException();
		if (this.daysValue() > 146096) throw new IllegalArgumentException();
		if (this.hoursValue() > 23) throw new IllegalArgumentException();
		if (this.minutesValue() > 59) throw new IllegalArgumentException();
		if (this.secondsValue() > 59) throw new IllegalArgumentException();
		if (this.millisecondsValue() > 999) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	FEMDuration(final int valueH, final int valueL) {
		this.valueH = valueH;
		this.valueL = valueL;
	}

	/** Diese Methode gibt die interne Darstellung der Zeitspanne zurück.
	 * <p>
	 * Die 64 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>yearsValue - 14 Bit</li>
	 * <li>signValue - 1 Bit</li>
	 * <li>hoursValue - 5 Bit</li>
	 * <li>minutesValue - 6 Bit</li>
	 * <li>secondsValue - 6 Bit</li>
	 * <li>daysValue - 18 Bit</li>
	 * <li>monthsValue - 4 Bit</li>
	 * <li>millisecondsValue - 10 Bit</li>
	 * </ul>
	 *
	 * @return interne Darstellung der Zeitspanne. */
	public final long value() {
		return Integers.toLong(this.valueH, this.valueL);
	}

	/** Diese Methode gibt das Vorzeichen dieser Zeitspanne zurück. Das Vorzeichen ist {@code -1}, {@code 0} oder {@code +1}, wenn alle Komponenten der Zeitspanne
	 * kleiner als, gleich bzw. größer als {@code 0} sind.
	 *
	 * @return Vorzeichen. */
	public final int signValue() {
		if ((this.valueH | this.valueL) == 0) return 0;
		return (this.valueH & 0x020000) != 0 ? -1 : +1;
	}

	/** Diese Methode gibt die Anzahl der Jahre zurück.
	 *
	 * @return Anzahl der Jahre ({@code 0..9999}). */
	public final int yearsValue() {
		return (this.valueH >> 18) & 0x3FFF;
	}

	/** Diese Methode gibt die Anzahl der Monate zurück.
	 *
	 * @return Anzahl der Monate ({@code 0..11}). */
	public final int monthsValue() {
		return (this.valueL >> 10) & 0x0F;
	}

	/** Diese Methode gibt die Anzahl der Tage zurück.
	 *
	 * @return Anzahl der Tage ({@code 0..146096}). */
	public final int daysValue() {
		return (this.valueL >> 14) & 0x03FFFF;
	}

	/** Diese Methode gibt die Anzahl der Stunden zurück.
	 *
	 * @return Anzahl der Stunde ({@code 0..23}). */
	public final int hoursValue() {
		return (this.valueH >> 12) & 0x1F;
	}

	/** Diese Methode gibt die Anzahl der Minuten zurück.
	 *
	 * @return Anzahl der Minuten ({@code 0..59}). */
	public final int minutesValue() {
		return (this.valueH >> 6) & 0x3F;
	}

	/** Diese Methode gibt die Anzahl der Sekunden zurück.
	 *
	 * @return Anzahl der Sekunden ({@code 0..59}). */
	public final int secondsValue() {
		return (this.valueH >> 0) & 0x3F;
	}

	/** Diese Methode gibt die Anzahl der Millisekunden zurück.
	 *
	 * @return Anzahl der Millisekunden ({@code 0..999}). */
	public final int millisecondsValue() {
		return (this.valueL >> 0) & 0x03FF;
	}

	/** Diese Methode gibt die Gesamtanzahl der Millisekunden zurück. Diese fassen {@link #daysValue()}, {@link #hoursValue()}, {@link #minutesValue()},
	 * {@link #secondsValue()} und {@link #millisecondsValue()} zusammen.
	 *
	 * @return Gesamtanzahl der Millisekunden ({@code 0..315569519999999}). */
	public final long durationmillisValue() {
		return FEMDuration.durationmillisOfImpl(this.daysValue(), this.hoursValue(), this.minutesValue(), this.secondsValue(), this.millisecondsValue());
	}

	/** Diese Methode gibt die Gesamtanzahl der Monate zurück. Diese fassen {@link #yearsValue()} und {@link #monthsValue()} zusammen.
	 *
	 * @return Gesamtanzahl der Monate ({@code 0..119999}). */
	public final int durationmonthsValue() {
		return FEMDuration.durationmonthsOfImpl(this.yearsValue(), this.monthsValue());
	}

	/** Diese Methode gibt diese Zeitspanne mit umgekehrten Vorzeichen zurück.
	 *
	 * @see #signValue()
	 * @return Zeitspanne mit umgekehrten Vorzeichen. */
	public final FEMDuration negate() {
		if (this.signValue() == 0) return this;
		return new FEMDuration(this.valueH ^ 0x020000, this.valueL);
	}

	/** Diese Methode gibt diese Zeitspanne verschoben um die gegebenen Gesamtanzahlen an Monate und Millisekunden zurück.
	 *
	 * @param durationmonths Gesamtanzahl der Monate ({@code -119999..119999}).
	 * @param durationmillis Gesamtanzahl der Millisekunden.
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration move(final int durationmonths, final int durationmillis) throws IllegalArgumentException {
		FEMDuration.checkMonths(-durationmonths);
		FEMDuration.checkMonths(+durationmonths);
		return this.moveImpl(durationmonths, durationmillis);
	}

	/** Diese Methode gibt diese Zeitspanne verschoben um die gegebenen Gesamtanzahlen an Monate und Millisekunden zurück.
	 *
	 * @param durationmonths Gesamtanzahl der Monate ({@code -119999..119999}).
	 * @param durationmillis Gesamtanzahl der Millisekunden ({@code -315569519999999..315569519999999}).
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration move(final int durationmonths, final long durationmillis) throws IllegalArgumentException {
		FEMDuration.checkMonths(-durationmonths);
		FEMDuration.checkMonths(+durationmonths);
		FEMDuration.checkMilliseconds(+durationmillis);
		FEMDuration.checkMilliseconds(-durationmillis);
		return this.moveImpl(durationmonths, durationmillis);
	}

	/** Diese Methode gibt diese Zeitspanne verschoben um die Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @see #move(int, long)
	 * @see #durationmonthsOf(int, int)
	 * @see #durationmillisOf(int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -9999..9999}).
	 * @param months Anzahl der Monate ({@code -119999..119999}).
	 * @param days Anzahl der Tage ({@code -3652424..3652424}).
	 * @param hours Anzahl der Stunden ({@code -87658199..87658199}).
	 * @param minutes Anzahl der Minuten.
	 * @param seconds Anzahl der Sekunden.
	 * @param milliseconds Anzahl der Millisekunden.
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration move(final int years, final int months, final int days, final int hours, final int minutes, final int seconds,
		final int milliseconds) throws IllegalArgumentException {
		return this.moveImpl(FEMDuration.durationmonthsOf(years, months), FEMDuration.durationmillisOf(days, hours, minutes, seconds, milliseconds));
	}

	/** Diese Methode gibt diese Zeitspanne verschoben um die Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 *
	 * @see #move(int, long)
	 * @see #durationmonthsOf(int, int)
	 * @see #durationmillisOf(int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -9999..9999}).
	 * @param months Anzahl der Monate ({@code -119999..119999}).
	 * @param days Anzahl der Tage ({@code -3652424..3652424}).
	 * @param hours Anzahl der Stunden ({@code -87658199..87658199}).
	 * @param minutes Anzahl der Minuten ({@code -5259491999..5259491999}).
	 * @param seconds Anzahl der Sekunden ({@code -315569519999..315569519999}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -315569519999999..315569519999999}).
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration move(final int years, final int months, final int days, final int hours, final long minutes, final long seconds,
		final long milliseconds) throws IllegalArgumentException {
		return this.moveImpl(FEMDuration.durationmonthsOf(years, months), FEMDuration.durationmillisOf(days, hours, minutes, seconds, milliseconds));
	}

	/** Diese Methode gibt diese Zeitspanne verschoben um die Gesamtanzahlen an Monate und Millisekunden der gegebenen Zeitspanne zurück.
	 *
	 * @param duration Gesamtanzahlen an Monate und Millisekunden.
	 * @param negate {@code true}, wenn die Verschiebung in die der gegebenen Zeitspanne entgegengesetzte Richtung erfolgen soll.
	 * @return verschobene Zeitspanne.
	 * @throws NullPointerException Wenn {@code duration} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration move(final FEMDuration duration, final boolean negate) throws NullPointerException, IllegalArgumentException {
		if ((duration.signValue() > 0) == negate) return this.move(-duration.durationmonthsValue(), -duration.durationmillisValue());
		return this.move(duration.durationmonthsValue(), duration.durationmillisValue());
	}

	@SuppressWarnings ("javadoc")
	final FEMDuration moveImpl(final int durationmonths, final long durationmillis) throws IllegalArgumentException {
		if (this.signValue() < 0) return FEMDuration.from(durationmonths - this.durationmonthsValue(), durationmillis - this.durationmillisValue());
		return FEMDuration.from(durationmonths + this.durationmonthsValue(), durationmillis + this.durationmillisValue());
	}

	/** Diese Methode multipliziert die Gesamtanzahlen an Monate und Millisekunden diese Zeitspanne mit dem gegebenen Faktor und gibt die resultierenden
	 * Gesamtanzahlen als neue Zeitspanne zurück.
	 *
	 * @param factor Faktor.
	 * @return Multiplizierte Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration multiply(final int factor) throws IllegalArgumentException {
		return FEMDuration.from(this.durationmonthsValue() * factor, this.durationmillisValue() * factor);
	}

	/** Diese Methode multipliziert die Gesamtanzahlen an Monate und Millisekunden diese Zeitspanne mit dem gegebenen Zähler, dividiert sie mit dem gegebenen
	 * Nenner und gibt die resultierenden Gesamtanzahlen als neue Zeitspanne zurück.
	 *
	 * @param numerator Zähler.
	 * @param denominator Nenner.
	 * @return Multiplizierte Zeitspanne.
	 * @throws IllegalArgumentException Wenn der Nenner {@code 0} ist oder die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden. */
	public final FEMDuration multiply(final int numerator, final int denominator) throws IllegalArgumentException {
		if (denominator == 0) throw new IllegalArgumentException();
		return FEMDuration.from((this.durationmonthsValue() * numerator) / denominator, (this.durationmillisValue() * numerator) / denominator);
	}

	/** Diese Methode gibt den Streuwert zurück.
	 *
	 * @return Streuwert. */
	public final int hash() {
		return this.valueH ^ this.valueL;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitspanne effektiv gleich der gegebenen ist.
	 *
	 * @see #compare(FEMDuration, int)
	 * @param that Zeitspannen.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMDuration that) throws NullPointerException {
		return ((this.valueL == that.valueL) && (this.valueH == that.valueH)) || (this.compare(that, 1) == 0);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn diese Zeitspanne kürzer, gleich bzw. länger als die gegebene Zeitspanne ist. Wenn
	 * die Zeitspannen nicht vergleichbar sind, wird {@code undefined} geliefert.
	 *
	 * @param that Zeitspanne.
	 * @param undefined Rückgabewert für nicht vergleichbare Zeitspannen.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final int compare(final FEMDuration that, final int undefined) {
		final int thisSign = this.signValue(), thatSign = that.signValue();
		if (thatSign > 0) return thisSign > 0 ? this.compareImpl(that, undefined) : -1;
		if (thatSign < 0) return thisSign < 0 ? that.compareImpl(this, undefined) : +1;
		return thisSign;
	}

	@SuppressWarnings ("javadoc")
	final int compareImpl(final FEMDuration that, final int undefined) {
		final int thisMonths = this.durationmonthsValue(), thatMonths = that.durationmonthsValue();
		final long thisMillis = this.durationmillisValue(), thatMillis = that.durationmillisValue();
		if (thisMonths == thatMonths) return Comparators.compare(thisMillis, thatMillis);
		final int thisLength = FEMDuration.lengthOf(thisMonths), thisRange = FEMDuration.rangeOf(thisMonths);
		final int thatLength = FEMDuration.lengthOf(thatMonths), thatRange = FEMDuration.rangeOf(thatMonths);
		final int length = thisLength - thatLength;
		final long millis = thisMillis - thatMillis;
		long result;
		// 864e5 x thisMinLength + thisMillis > 864e5 x thatMaxLength + thatMillis => +1
		result = millis + ((length - ((thisRange >> 0) & 0xF) - ((thatRange >> 4) & 0xF)) * 86400000L);
		if (result > 0) return +1;
		// 864e5 x thisMaxLength + thisMillis < 864e5 x thatMinLength + thatMillis => -1
		result = millis + ((length + ((thisRange >> 4) & 0xF) + ((thatRange >> 0) & 0xF)) * 86400000L);
		if (result < 0) return -1;
		return undefined;
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMDuration data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMDuration> type() {
		return FEMDuration.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMDuration result() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMDuration result(final boolean recursive) {
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
		if (!(object instanceof FEMDuration)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMDuration)) return false;
		}
		return this.equals((FEMDuration)object);
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final FEMDuration that) {
		return this.compare(that, 0);
	}

	/** Diese Methode gibt die Textdarstellung dieser Zeitspanne zurück. Diese Textdarstellung entspricht der des Datentyps
	 * <a href="http://www.w3.org/TR/xmlschema-2/#duration-lexical-repr">xsd:duration</a> aus <a href="www.w3.org/TR/xmlschema-2">XML Schema Part 2: Datatypes
	 * Second Edition</a>, beschränkt auf genau drei optionale Nachkommastellen für die Sekunden.
	 *
	 * @return Textdarstellung. */
	@Override
	public final String toString() {
		final int sing = this.signValue();
		if (sing == 0) return "P0Y";
		final char[] buffer = new char[31];
		int offset = 0;
		if (sing < 0) {
			buffer[offset] = '-';
			offset += 1;
		}
		buffer[offset] = 'P';
		offset += 1;
		final int years = this.yearsValue();
		if (years != 0) {
			offset += Integers.stringSize(years);
			Integers.formatInt(years, buffer, offset);
			buffer[offset] = 'Y';
			offset += 1;
		}
		final int months = this.monthsValue();
		if (months != 0) {
			offset += Integers.stringSize(months);
			Integers.formatInt(months, buffer, offset);
			buffer[offset] = 'M';
			offset += 1;
		}
		final int days = this.daysValue();
		if (days != 0) {
			offset += Integers.stringSize(days);
			Integers.formatInt(days, buffer, offset);
			buffer[offset] = 'D';
			offset += 1;
		}
		final int hours = this.hoursValue(), minutes = this.minutesValue(), seconds = this.secondsValue(), milliseconds = this.millisecondsValue();
		if ((hours | minutes | seconds | milliseconds) != 0) {
			buffer[offset] = 'T';
			offset += 1;
		}
		if (hours != 0) {
			offset += Integers.stringSize(hours);
			Integers.formatInt(hours, buffer, offset);
			buffer[offset] = 'H';
			offset += 1;
		}
		if (minutes != 0) {
			offset += Integers.stringSize(minutes);
			Integers.formatInt(minutes, buffer, offset);
			buffer[offset] = 'M';
			offset += 1;
		}
		if (milliseconds != 0) {
			offset += Integers.stringSize(seconds);
			Integers.formatInt(seconds, buffer, offset);
			offset += 4;
			Integers.formatInt(milliseconds + 1000, buffer, offset);
			buffer[offset - 4] = '.';
			buffer[offset] = 'S';
			offset += 1;
		} else if (seconds != 0) {
			offset += Integers.stringSize(seconds);
			Integers.formatInt(seconds, buffer, offset);
			buffer[offset] = 'S';
			offset += 1;
		}
		return new String(buffer, 0, offset);
	}

}
