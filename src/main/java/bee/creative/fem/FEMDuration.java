package bee.creative.fem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEM.ScriptFormatterInput;
import bee.creative.util.Comparators;
import bee.creative.util.Strings;

/**
 * Diese Klasse implementiert eine Zeitspanne aus Jahren, Monaten, Tagen, Stunden, Minuten, Sekunden und Millisekunden.<br>
 * Intern wird die Zeitspanne als ein {@code long} dargestellt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMDuration implements Comparable<FEMDuration>, ScriptFormatterInput {

	/** Dieses Feld speichert die leere Zeitspanne, deren Komponenten {@code 0} sind. */
	public static final FEMDuration EMPTY = new FEMDuration(0, 0);

	/** Dieses Feld speichert die Ergebnisse von {@link #__rangeOf(int)}. */
	static final byte[] __ranges = {0, 18, 33, 18, 33, 18, 33, 33, 33, 48, 33, 48, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34,
		49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 16, 19, 34, 19, 34, 19, 34, 34, 34, 49, 34, 49, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50,
		50, 50, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 17, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17,
		35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 17, 19, 34, 34, 34, 34, 34, 49, 34, 49, 34, 49, 16, 19, 34, 19,
		34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 17, 19, 34, 34, 34, 34, 34,
		49, 34, 49, 34, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50,
		50, 50, 17, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17,
		35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 17, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 19, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35,
		50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34,
		49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 34, 49,
		49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17,
		34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35,
		50, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50,
		50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49,
		34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16,
		19, 34, 34, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34,
		34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50,
		50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65,
		50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32,
		35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34,
		34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49,
		49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65,
		50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32,
		35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 50,
		50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 34,
		49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49,
		49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17,
		34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35,
		50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50,
		65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49,
		49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16,
		34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35,
		50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51,
		66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66,
		66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33,
		51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35,
		50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 35, 50,
		65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66,
		66, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33,
		51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51,
		51, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50,
		50, 50, 65, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65,
		50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33,
		35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51,
		51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66,
		66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65,
		50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32,
		35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50,
		50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66,
		66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 81,
		66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 33,
		51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 35,
		50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 50, 50, 50, 50, 50, 50,
		65, 65, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66,
		66, 66, 33, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51, 66, 51, 66, 81, 66, 81, 66, 81, 33,
		51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 48, 51, 66, 51,
		66, 51, 66, 81, 66, 81, 66, 81, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50,
		65, 50, 65, 65, 65, 48, 51, 66, 51, 66, 51, 66, 81, 66, 81, 66, 81, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 50, 50, 50, 50, 50, 65, 65, 65, 65,
		65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 48, 51, 66, 51, 66, 51, 66, 81, 66, 81, 66, 81, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33,
		50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 48, 51, 66, 51, 66, 66, 66, 81, 66, 81, 66, 81, 33, 51, 66, 51,
		66, 51, 66, 66, 66, 81, 66, 81, 33, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50,
		65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49,
		49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17,
		35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 33, 36, 51, 36, 51, 36, 51, 51, 51, 66, 51, 66, 18, 35, 35, 35,
		50, 35, 50, 50, 50, 50, 50, 50, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33, 36, 51, 36, 51, 36, 51,
		66, 51, 66, 51, 66, 18, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66,
		51, 66, 33, 36, 51, 36, 51, 36, 51, 66, 51, 66, 51, 66, 18, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 33,
		36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36, 51, 36, 51, 36, 51, 66, 51, 66, 51, 66, 18, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 35, 35, 35,
		35, 35, 50, 50, 50, 50, 50, 50, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36, 51, 36, 51, 36, 51, 66, 51, 66, 51, 66, 18, 35, 50, 35, 50, 35, 50,
		50, 50, 50, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66,
		51, 66, 18, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33,
		36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 18, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 35, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51,
		51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50,
		50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 36, 51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 50, 50, 65,
		50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 51, 66, 33,
		35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 36, 51, 51,
		51, 51, 51, 66, 51, 66, 51, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 51,
		66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50,
		50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17,
		35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 36, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35,
		50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51,
		66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66,
		66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 51, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 33,
		51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35,
		50, 35, 50, 50, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 66, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50,
		65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 51, 66, 66, 66,
		66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 51, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33,
		51, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51,
		66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50,
		65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 66, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33, 35, 50, 50, 50, 50, 50, 65, 50, 65,
		65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 33, 51, 66, 51, 66, 51, 66, 66, 66, 81, 66, 81, 33, 51, 51, 51, 51, 51, 66, 66, 66, 66, 66, 66, 33,
		50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35,
		35, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50,
		50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 19, 34, 34, 34, 49, 34, 49,
		34, 49, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 50, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16,
		19, 34, 19, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34,
		34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 34, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 35, 35, 50, 35, 50,
		50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65,
		50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32,
		35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 50, 50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 19, 34, 34,
		34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 34, 34, 49,
		49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 35, 50, 35, 50, 35, 50, 50, 50, 65,
		50, 65, 17, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32,
		35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 35,
		50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 50, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34,
		49, 49, 49, 49, 49, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 34, 34, 49, 34, 49, 49, 49, 49,
		49, 64, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17,
		34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 35, 50, 35,
		50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 35, 50, 50, 50, 50, 50,
		65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 35, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49,
		49, 49, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16,
		34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 17, 34, 49, 34,
		49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50,
		65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65,
		65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 49, 32,
		50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 35, 50, 50, 50, 65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 34, 34,
		49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 50, 65, 32, 34, 49, 34, 49, 34, 49,
		64, 49, 64, 49, 64, 16, 34, 34, 34, 49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65,
		50, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32,
		35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 49, 34, 49, 34, 49, 49, 49, 49, 49, 64, 32, 50, 50, 50,
		50, 50, 65, 65, 65, 65, 65, 65, 32, 35, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 34, 49, 34, 49, 34, 49, 64, 49, 64, 49, 64, 16, 34, 49, 34, 49, 34, 49,
		49, 49, 64, 49, 64, 32, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 32, 50, 50, 50, 50, 50, 50, 65, 50, 65, 65, 65, 32, 34, 49, 34, 49, 49, 49, 64, 49, 64,
		49, 64, 16, 34, 49, 34, 49, 34, 49, 49, 49, 64, 49, 64, 16, 34, 34, 34, 34, 34, 49, 49, 49, 49, 49, 49, 16, 34, 34, 34, 34, 34, 34, 49, 34, 49, 49, 49, 16,
		18, 33, 18, 33, 33, 33, 48, 33, 48, 33, 48};

	@SuppressWarnings ("javadoc")
	static final Pattern __pattern = Pattern.compile("^(\\-)?P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)D)?(T)?(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)(?:\\.(\\d{0,3}))S)?$");

	{}

	/**
	 * Diese Methode gibt eine neue Zeitapanne mit dem in der gegebenen Zeichenkette kodierten Wert zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString() Textdarstellung}.
	 * 
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @return Zeitapanne.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist.
	 */
	public static final FEMDuration from(final String string) throws NullPointerException, IllegalArgumentException {
		System.out.println(Strings.matchAll(FEMDuration.__pattern, string));
		final Matcher matcher = FEMDuration.__pattern.matcher(string);
		if (!matcher.find()) throw new IllegalArgumentException();
		if (((matcher.start(6) > 0) || (matcher.start(8) > 0)) && (matcher.start(5) < 0)) throw new IllegalArgumentException();
		FEMDuration result = FEMDuration.EMPTY;

		final int years = matcher.start(2) > 0 ? Integer.parseInt(matcher.group(2)) : 0;
		final int months = matcher.start(3) > 0 ? Integer.parseInt(matcher.group(3)) : 0;
		final int days = matcher.start(4) > 0 ? Integer.parseInt(matcher.group(4)) : 0;
		final int hours = matcher.start(6) > 0 ? Integer.parseInt(matcher.group(6)) : 0;
		final long minutes = matcher.start(7) > 0 ? Long.parseLong(matcher.group(7)) : 0;
		final long seconds = matcher.start(8) > 0 ? Long.parseLong(matcher.group(8)) : 0;
		final long milliseconds = matcher.start(9) > 0 ? Long.parseLong(matcher.group(9)) : 0;
		result = result.move(years, months, days, hours, minutes, seconds, milliseconds);
		if (matcher.start(1) < 0) return result;
		result = result.negate();
		return result;
	}

	/**
	 * Diese Methode gibt eine Zeitapanne mit den gegebenen Gesamtanzahlen an Monaten und Millisekunden zurück.
	 * 
	 * @param durationmonths Gesamtanzahl der Monate ({@code -101006..101006}).
	 * @param durationmillis Gesamtanzahl der Millisekunden ({@code -265621593600000..265621593600000}).
	 * @return Zeitapanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden.
	 */
	public static final FEMDuration from(int durationmonths, long durationmillis) throws IllegalArgumentException {
		FEMDuration.__checkMonths(+durationmonths);
		FEMDuration.__checkMonths(-durationmonths);
		FEMDuration.__checkMilliseconds(+durationmillis);
		FEMDuration.__checkMilliseconds(-durationmillis);
		int negate, years, months, days, hours, minutes, seconds, milliseconds;
		if ((durationmonths < 0) || (durationmillis < 0)) {
			if ((durationmonths > 0) || (durationmillis > 0)) throw new IllegalArgumentException();
			negate = 1;
			durationmonths = -durationmonths;
			durationmillis = -durationmillis;
		} else {
			negate = 0;
		}
		days = (int)(durationmillis / 86400000);
		milliseconds = (int)(durationmillis % 86400000);
		hours = milliseconds / 3600000;
		milliseconds = milliseconds % 3600000;
		minutes = milliseconds / 60000;
		milliseconds = milliseconds % 60000;
		seconds = milliseconds / 1000;
		milliseconds = milliseconds % 1000;
		months = durationmonths % 12;
		years = (durationmonths / 12) + ((days / 146097) * 400);
		days = days % 146097;
		FEMDuration.__checkYears(years);
		return new FEMDuration( //
			(years << 18) | (negate << 17) | (hours << 12) | (minutes << 6) | (seconds << 0), //
			(days << 14) | (months << 10) | (milliseconds << 0));
	}

	/**
	 * Diese Methode gibt eine Zeitapanne mit den Gesamtanzahlen an Monaten und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 * 
	 * @see #from(int, long)
	 * @see #durationmonthsOf(int, int)
	 * @see #durationmillisOf(int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -8417..8417}).
	 * @param months Anzahl der Monate ({@code -101006..101006}).
	 * @param days Anzahl der Tage ({@code -3074324..3074324}).
	 * @param hours Anzahl der Stunden ({@code -73783776..73783776}).
	 * @param minutes Anzahl der Minuten ({@code -4427026560..4427026560}).
	 * @param seconds Anzahl der Sekunden ({@code -265621593600..265621593600}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -265621593600000..265621593600000}).
	 * @return Zeitapanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden.
	 */
	public static final FEMDuration from(final int years, final int months, final int days, final int hours, final long minutes, final long seconds,
		final long milliseconds) throws IllegalArgumentException {
		return FEMDuration.EMPTY.move(years, months, days, hours, minutes, seconds, milliseconds);
	}

	@SuppressWarnings ("javadoc")
	static final void __checkDays(final int days) throws IllegalArgumentException {
		if (days > 3074324) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkYears(final int years) throws IllegalArgumentException {
		if (years > 8417) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkMonths(final int months) throws IllegalArgumentException {
		if (months > 101006) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkHours(final int hours) throws IllegalArgumentException {
		if (hours > 73783776) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkMinutes(final long minutes) throws IllegalArgumentException {
		if (minutes > 4427026560L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkSeconds(final long seconds) throws IllegalArgumentException {
		if (seconds > 265621593600L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkMilliseconds(final long milliseconds) throws IllegalArgumentException {
		if (milliseconds > 265621593600000L) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final void __checkPositive(final int value) throws IllegalArgumentException {
		if (value < 0) throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode gibt die Zeitspanne zwischen den gegebenen Zeitangaben in Zeitzone {@code 00:00} zurück.
	 * 
	 * @see FEMDatetime#withZone(int)
	 * @param datetime1 erste Zeitangabe.
	 * @param datetime2 zweite Zeitangabe.
	 * @return Zeitspanne von der ersten zur zweiten Zeitangabe.
	 * @throws NullPointerException Wenn {@code datetime1} bzw. {@code datetime2} {@code null} ist.
	 * @throws IllegalArgumentException Wenn nur eine der Zeitangaben ein Datum bzw eine Uhrzeit besitzt.
	 */
	public static final FEMDuration between(final FEMDatetime datetime1, final FEMDatetime datetime2) throws NullPointerException, IllegalArgumentException {
		if (datetime1.hasDate()) {
			if (!datetime2.hasDate()) throw new IllegalArgumentException();
			if (datetime1.hasTime()) {
				if (!datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, datetime1.__calendardayValue() - datetime2.__calendardayValue(), //
					0, datetime2.__zoneValue() - datetime1.__zoneValue(), 0, datetime1.__daymillisValue() - datetime2.__daymillisValue());
			} else {
				if (datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, datetime1.__calendardayValue() - datetime2.__calendardayValue(), //
					0, datetime2.__zoneValue() - datetime1.__zoneValue(), 0, 0);
			}
		} else {
			if (datetime2.hasDate()) throw new IllegalArgumentException();
			if (datetime1.hasTime()) {
				if (!datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, 0, 0, datetime2.__zoneValue() - datetime1.__zoneValue(), 0, datetime1.__daymillisValue() - datetime2.__daymillisValue());
			} else {
				if (datetime2.hasTime()) throw new IllegalArgumentException();
				return FEMDuration.from(0, 0, 0, 0, datetime2.__zoneValue() - datetime1.__zoneValue(), 0, 0);
			}
		}
	}

	/**
	 * Diese Methode gibt die minnimale Anzahl an Tagen zurück, die durch die gegebene Anzahl an Monaten ausgedrückt werden kann.
	 * 
	 * @see FEMDatetime#moveDate(int, int, int)
	 * @param months Anzahl der Monate ({@code 0..101006}).
	 * @return minimale Anzahl an Tagen in den gegebenen Monaten.
	 * @throws IllegalArgumentException Wenn {@code months} ungültig ist.
	 */
	public static final int minLengthOf(final int months) throws IllegalArgumentException {
		FEMDuration.__checkMonths(months);
		FEMDuration.__checkPositive(months);
		return FEMDuration.__lengthOf(months) - ((FEMDuration.__rangeOf(months) >> 0) & 0x0F);
	}

	/**
	 * Diese Methode gibt die maxnimale Anzahl an Tagen zurück, die durch die gegebene Anzahl an Monaten ausgedrückt werden kann.
	 * 
	 * @see FEMDatetime#moveDate(int, int, int)
	 * @param months Anzahl der Monate ({@code 0..101006}).
	 * @return maximale Anzahl an Tagen in den gegebenen Monaten.
	 * @throws IllegalArgumentException Wenn {@code months} ungültig ist.
	 */
	public static final int maxLengthOf(final int months) throws IllegalArgumentException {
		FEMDuration.__checkMonths(months);
		FEMDuration.__checkPositive(months);
		return FEMDuration.__lengthOf(months) + ((FEMDuration.__rangeOf(months) >> 4) & 0x0F);
	}

	/**
	 * Diese Methode gibt das Paar aus Min und Max zur Ergänzung von {@link #__lengthOf(int)} zu {@link #minLengthOf(int)} und {@link #maxLengthOf(int)} zurück.<br>
	 * {@link #minLengthOf(int)} = {@link #__lengthOf(int)} - MIN.<br>
	 * {@link #maxLengthOf(int)} = {@link #__lengthOf(int)} + MAX.
	 * 
	 * @param months Anzahl an Monaten ({@code 0..101015}).
	 * @return Min-Max-Paar (MIN << 0 | MAX << 4).
	 */
	static final int __rangeOf(final int months) {
		return FEMDuration.__ranges[months % 4800];
	}

	/**
	 * Diese Methode gibt die mittlere Anzahl an Tagen zurück, die durch die gegebene Anzahl an Monaten ausgedrückt werden kann.
	 * 
	 * @param months Anzahl der Monate ({@code 0..101006}).
	 * @return mittlere Anzahl an Tagen in den gegebenen Monaten.
	 */
	static final int __lengthOf(final int months) {
		return (months * 146097) / 4800;
	}

	/**
	 * Diese Methode gibt die Gesamtanzahl der Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 * 
	 * @param days Anzahl der Tage ({@code -3074324..3074324}).
	 * @param hours Anzahl der Stunden ({@code -73783776..73783776}).
	 * @param minutes Anzahl der Minuten ({@code -4427026560..4427026560}).
	 * @param seconds Anzahl der Sekunden ({@code -265621593600..265621593600}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -265621593600000..265621593600000}).
	 * @return die Gesamtanzahl der Millisekunden ({@code -265621593600000..265621593600000}).
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen ungültig sind oder zu einer ungültigen Gesamtanzahl führen würde.
	 */
	public static final long durationmillisOf(final int days, final int hours, final long minutes, final long seconds, final long milliseconds)
		throws IllegalArgumentException {
		FEMDuration.__checkDays(-days);
		FEMDuration.__checkDays(+days);
		FEMDuration.__checkHours(+hours);
		FEMDuration.__checkHours(-hours);
		FEMDuration.__checkMinutes(+minutes);
		FEMDuration.__checkMinutes(-minutes);
		FEMDuration.__checkSeconds(+seconds);
		FEMDuration.__checkSeconds(-seconds);
		final long result = FEMDuration.__durationmillisOf(days, hours, minutes, seconds, milliseconds);
		FEMDuration.__checkMilliseconds(+result);
		FEMDuration.__checkMilliseconds(-result);
		return result;
	}

	@SuppressWarnings ("javadoc")
	static final long __durationmillisOf(final int days, final int hours, final long minutes, final long seconds, final long milliseconds)
		throws IllegalArgumentException {
		return (days * 86400000L) + (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L) + milliseconds;
	}

	/**
	 * Diese Methode gibt die Gesamtanzahl der Monate zurück, die sich aus den gegebenen Anzahlen ergeben.
	 * 
	 * @param years Anzahl der Jahre ({@code -8417..8417}).
	 * @param months Anzahl der Monate ({@code -101006..101006}).
	 * @return Gesamtanzahl der Monate ({@code -101006..101006}).
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen ungültig sind oder zu einer ungültigen Gesamtanzahl führen würde.
	 */
	public static final int durationmonthsOf(final int years, final int months) throws IllegalArgumentException {
		FEMDuration.__checkYears(+years);
		FEMDuration.__checkYears(-years);
		FEMDuration.__checkMonths(-months);
		FEMDuration.__checkMonths(+months);
		final int result = FEMDuration.__durationmonthsOf(years, months);
		FEMDuration.__checkMonths(+result);
		FEMDuration.__checkMonths(-result);
		return result;
	}

	@SuppressWarnings ("javadoc")
	static final int __durationmonthsOf(final int years, final int months) {
		return (years * 12) + months;
	}

	{}

	/**
	 * Dieses Feld speichert die 32 LSB der internen 64 Bit Darstellung dieser Zeitspanne.
	 * <p>
	 * Die 32 Bit von MBS zum LSB sind:
	 * <ul>
	 * <li>daysValue - 18 Bit</li>
	 * <li>monthsValue - 4 Bit</li>
	 * <li>millisecondsValue - 10 Bit</li>
	 * </ul>
	 */
	final int __valueL;

	/**
	 * Dieses Feld speichert die 32 MSB der internen 64 Bit Darstellung dieser Zeitspanne.
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
	final int __valueH;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung der Zeitspanne.
	 * 
	 * @see #value()
	 * @param value interne Darstellung der Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@code value} ungültig ist.
	 */
	public FEMDuration(final long value) throws IllegalArgumentException {
		this((int)(value >> 32), (int)(value >> 0));
		if (value == 0) return;
		FEMDuration.__checkYears(this.yearsValue());
		if (this.monthsValue() > 11) throw new IllegalArgumentException();
		if (this.daysValue() > 146096) throw new IllegalArgumentException();
		if (this.hoursValue() > 23) throw new IllegalArgumentException();
		if (this.minutesValue() > 59) throw new IllegalArgumentException();
		if (this.secondsValue() > 59) throw new IllegalArgumentException();
		if (this.millisecondsValue() > 999) throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	FEMDuration(final int valueH, final int valueL) {
		this.__valueH = valueH;
		this.__valueL = valueL;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung der Zeitspanne zurück.
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
	 * @return interne Darstellung der Zeitspanne.
	 */
	public final long value() {
		return (((long)this.__valueH) << 32) | (((long)this.__valueL) << 0);
	}

	/**
	 * Diese Methode gibt das Vorzeichen dieser Zeitspanne zurück.<br>
	 * Das Vorzeichen ist {@code -1}, {@code 0} oder {@code +1}, wenn alle Komponenten der Zeitspanne kleiner als, gleich bzw. größer als {@code 0} sind.
	 * 
	 * @return Vorzeichen.
	 */
	public final int signValue() {
		if ((this.__valueH | this.__valueL) == 0) return 0;
		return (this.__valueH & 0x020000) != 0 ? -1 : +1;
	}

	/**
	 * Diese Methode gibt die Anzahl der Jahre zurück.
	 * 
	 * @return Anzahl der Jahre ({@code 0..8417}).
	 */
	public final int yearsValue() {
		return (this.__valueH >> 18) & 0x3FFF;
	}

	/**
	 * Diese Methode gibt die Anzahl der Monate zurück.
	 * 
	 * @return Anzahl der Monate ({@code 0..11}).
	 */
	public final int monthsValue() {
		return (this.__valueL >> 10) & 0x0F;
	}

	/**
	 * Diese Methode gibt die Anzahl der Tage zurück.
	 * 
	 * @return Anzahl der Tage ({@code 0..146096}).
	 */
	public final int daysValue() {
		return (this.__valueL >> 14) & 0x03FFFF;
	}

	/**
	 * Diese Methode gibt die Anzahl der Stunden zurück.
	 * 
	 * @return Anzahl der Stunde ({@code 0..23}).
	 */
	public final int hoursValue() {
		return (this.__valueH >> 12) & 0x1F;
	}

	/**
	 * Diese Methode gibt die Anzahl der Minuten zurück.
	 * 
	 * @return Anzahl der Minuten ({@code 0..59}).
	 */
	public final int minutesValue() {
		return (this.__valueH >> 6) & 0x3F;
	}

	/**
	 * Diese Methode gibt die Anzahl der Sekunden zurück.
	 * 
	 * @return Anzahl der Sekunden ({@code 0..59}).
	 */
	public final int secondsValue() {
		return (this.__valueH >> 0) & 0x3F;
	}

	/**
	 * Diese Methode gibt die Anzahl der Millisekunden zurück.
	 * 
	 * @return Anzahl der Millisekunden ({@code 0..999}).
	 */
	public final int millisecondsValue() {
		return (this.__valueL >> 0) & 0x03FF;
	}

	/**
	 * Diese Methode gibt die Gesamtanzahl der Millisekunden zurück.<br>
	 * Diese fassen {@link #daysValue()}, {@link #hoursValue()}, {@link #minutesValue()}, {@link #secondsValue()} und {@link #millisecondsValue()} zusammen.
	 * 
	 * @return Gesamtanzahl der Millisekunden ({@code 0..265621593600000}).
	 */
	public final long durationmillisValue() {
		return FEMDuration.__durationmillisOf(this.daysValue(), this.hoursValue(), this.minutesValue(), this.secondsValue(), this.millisecondsValue());
	}

	/**
	 * Diese Methode gibt die Gesamtanzahl der Monate zurück. Diese fassen {@link #yearsValue()} und {@link #monthsValue()} zusammen.
	 * 
	 * @return Gesamtanzahl der Monate ({@code 0..101006}).
	 */
	public final int durationmonthsValue() {
		return FEMDuration.__durationmonthsOf(this.yearsValue(), this.monthsValue());
	}

	/**
	 * Diese Methode gibt diese Zeitspanne mit umgekehrten Vorzeichen zurück.
	 * 
	 * @see #signValue()
	 * @return Zeitspanne mit umgekehrten Vorzeichen.
	 */
	public final FEMDuration negate() {
		if (this.signValue() == 0) return this;
		return new FEMDuration(this.__valueH ^ 0x020000, this.__valueL);
	}

	/**
	 * Diese Methode gibt diese Zeitspanne verschoben um die gegebenen Gesamtanzahlen an Monate und Millisekunden zurück.
	 * 
	 * @param durationmonths Gesamtanzahl der Monate ({@code -101006..101006}).
	 * @param durationmillis Gesamtanzahl der Millisekunden ({@code -265621593600000..265621593600000}).
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden.
	 */
	public final FEMDuration move(final int durationmonths, final long durationmillis) throws IllegalArgumentException {
		FEMDuration.__checkMonths(-durationmonths);
		FEMDuration.__checkMonths(+durationmonths);
		FEMDuration.__checkMilliseconds(+durationmillis);
		FEMDuration.__checkMilliseconds(-durationmillis);
		return this.__move(durationmonths, durationmillis);
	}

	/**
	 * Diese Methode gibt diese Zeitspanne verschoben um die Gesamtanzahlen an Monate und Millisekunden zurück, die sich aus den gegebenen Anzahlen ergeben.
	 * 
	 * @see #move(int, long)
	 * @see #durationmonthsOf(int, int)
	 * @see #durationmillisOf(int, int, long, long, long)
	 * @param years Anzahl der Jahre ({@code -8417..8417}).
	 * @param months Anzahl der Monate ({@code -101006..101006}).
	 * @param days Anzahl der Tage ({@code -3074324..3074324}).
	 * @param hours Anzahl der Stunden ({@code -73783776..73783776}).
	 * @param minutes Anzahl der Minuten ({@code -4427026560..4427026560}).
	 * @param seconds Anzahl der Sekunden ({@code -265621593600..265621593600}).
	 * @param milliseconds Anzahl der Millisekunden ({@code -265621593600000..265621593600000}).
	 * @return verschobene Zeitspanne.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden.
	 */
	public final FEMDuration move(final int years, final int months, final int days, final int hours, final long minutes, final long seconds,
		final long milliseconds) throws IllegalArgumentException {
		return this.__move(FEMDuration.durationmonthsOf(years, months), FEMDuration.durationmillisOf(days, hours, minutes, seconds, milliseconds));
	}

	/**
	 * Diese Methode gibt diese Zeitspanne verschoben um die Gesamtanzahlen an Monate und Millisekunden der gegebenen Zeitspanne zurück.
	 * 
	 * @param duration Gesamtanzahlen an Monate und Millisekunden.
	 * @return verschobene Zeitspanne.
	 * @throws NullPointerException Wenn {@code duration} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebenen Anzahlen zu einer ungültigen Zeitspanne führen würden.
	 */
	public final FEMDuration move(final FEMDuration duration) throws NullPointerException, IllegalArgumentException {
		if (duration.signValue() < 0) return this.move(-duration.durationmonthsValue(), -duration.durationmillisValue());
		return this.move(duration.durationmonthsValue(), duration.durationmillisValue());
	}

	@SuppressWarnings ("javadoc")
	final FEMDuration __move(final int durationmonths, final long durationmillis) throws IllegalArgumentException {
		if (this.signValue() < 0) return FEMDuration.from(durationmonths - this.durationmonthsValue(), durationmillis - this.durationmillisValue());
		return FEMDuration.from(durationmonths + this.durationmonthsValue(), durationmillis + this.durationmillisValue());
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeitspannen effektiv gleich der gegebenen ist.
	 * 
	 * @see #compare(FEMDuration, int)
	 * @param that Zeitspannen.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMDuration that) throws NullPointerException {
		return this.compare(that, 1) == 0;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn diese Zeitspanne kürzer, gleich bzw. länger als die gegebene Zeitspanne
	 * ist. Wenn die Zeitspannen nicht vergleichbar sind, wird {@code undefined} geliefert.
	 * 
	 * @param that Zeitspanne.
	 * @param undefined Rückgabewert für nicht vergleichbare Zeitspannen.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEMDuration that, final int undefined) {
		final int sign = this.signValue(), result = sign - that.signValue();
		if (result != 0) return result;
		return sign < 0 ? that.__compare(this, undefined) : this.__compare(that, undefined);
	}

	@SuppressWarnings ("javadoc")
	final int __compare(final FEMDuration that, final int undefined) {
		final int thisMonths = this.durationmonthsValue(), thatMonths = that.durationmonthsValue();
		final long thisMillis = this.durationmillisValue(), thatMillis = that.durationmillisValue();
		if (thisMonths == thatMonths) return Comparators.compare(thisMillis, thatMillis);
		final int thisLength = FEMDuration.__lengthOf(thisMonths), thisRange = FEMDuration.__rangeOf(thisMonths);
		final int thatLength = FEMDuration.__lengthOf(thatMonths), thatRange = FEMDuration.__rangeOf(thatMonths);
		final int length = thisLength - thatLength;
		final long millis = thisMillis - thatMillis;
		long result;
		result = millis + ((length + ((thisRange >> 4) & 0xF) + ((thatRange >> 4) & 0xF)) * 86400000L);
		if (result < 0) return -1;
		result = millis + (((length - ((thisRange >> 0) & 0xF)) + ((thatRange >> 0) & 0xF)) * 86400000L);
		if (result > 0) return +1;
		return undefined;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.__valueH ^ this.__valueL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDuration)) return false;
		final FEMDuration that = (FEMDuration)object;
		return (this.__valueL == that.__valueL) && (this.__valueH == that.__valueH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final FEMDuration that) {
		return this.compare(that, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(FEM.formatValue(this.toString()));
	}

	/**
	 * Diese Methode gibt die Textdarstellung dieser Zeitspanne zurück.<br>
	 * Diese Textdarstellung entspricht der des Datentyps <a href="http://www.w3.org/TR/xmlschema-2/#duration-lexical-repr">xsd:duration</a> aus <a
	 * href="www.w3.org/TR/xmlschema-2">XML Schema Part 2: Datatypes Second Edition</a>, beschränkt auf maximal drei Nachkommastellen für die Sekunden.
	 * 
	 * @return Textdarstellung.
	 */
	@Override
	public final String toString() {
		final int sing = this.signValue();
		if (sing == 0) return "P0Y";
		final StringBuilder result = new StringBuilder();
		result.append(sing < 0 ? "-P" : "P");
		final int years = this.yearsValue(), months = this.monthsValue();
		if (years != 0) {
			result.append(years).append('Y');
		}
		if (months != 0) {
			result.append(months).append('M');
		}
		final int days = this.daysValue();
		if (days != 0) {
			result.append(days).append('D');
		}
		final int hours = this.hoursValue(), minutes = this.minutesValue(), seconds = this.secondsValue(), milliseconds = this.millisecondsValue();
		if ((hours | minutes | seconds | milliseconds) != 0) {
			result.append('T');
		}
		if (hours != 0) {
			result.append(hours).append('H');
		}
		if (minutes != 0) {
			result.append(minutes).append('M');
		}
		if (milliseconds != 0) {
			result.append(String.format("%d.%03dS", seconds, milliseconds));
		} else if (seconds != 0) {
			result.append(seconds).append('S');
		}
		return result.toString();
	}

}
