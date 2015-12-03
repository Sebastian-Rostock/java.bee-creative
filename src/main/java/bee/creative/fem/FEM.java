package bee.creative.fem;

import bee.creative.fem.Scripts.ScriptFormatter;

/**
 * FEM - Function Evaluation Model
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class FEM {

	static final char toHex(final int value) {
		final int value2 = value - 10;
		return (char)(value2 < 0 ? ('0' + value) : ('A' + value2));
	}

	public static String formatBinary(final byte[] binary) throws NullPointerException {
		return null;
	}

	public static String formatBinary(final FEMBinary source) throws NullPointerException {
		final StringBuilder result = new StringBuilder();
		FEM.formatBinary(result, source);
		return result.toString();
	}

	public static void formatBinary(final StringBuilder target, final FEMBinary source) throws NullPointerException {
		target.append("0x");
		for (int i = 0, length = source.length; i < length; i++) {
			final int value = source.get__(i);
			target.append(FEM.toHex((value >> 4) & 0xF)).append(FEM.toHex((value >> 0) & 0xF));
		}
	}

	public static String formatBoolean(final FEMBoolean femBoolean) {
		return null;
	}

	public static String formatInteger(final FEMInteger integer) {
		return null;
	}

	public static String formatDecimal(final FEMDecimal femDecimal) {
		return null;
	}

	public static String formatDatetime(final FEMDatetime source) {
		final StringBuilder target = new StringBuilder();
		FEM.formatDatetime(target, source);
		return target.toString();
	}

	public static void formatDatetime(final StringBuilder target, final FEMDatetime source) {
		final boolean hasDate = source.hasDate();
		if (hasDate) {
			target.append(String.format("%04d-%02d-%02d", source.yearValue(), source.monthValue(), source.dateValue()));
		}
		if (source.hasTime()) {
			if (hasDate) {
				target.append('T');
			}
			target.append(String.format("%02d:%02d:%02d", source.hourValue(), source.minuteValue(), source.secondValue()));
			final int millisecond = source.millisecondValue();
			if (millisecond != 0) {
				target.append(String.format(".%03d", millisecond));
			}
		}
		if (source.hasZone()) {
			final int zone = source.zoneValue();
			if (zone == 0) {
				target.append('Z');
			} else {
				final int zoneAbs = Math.abs(zone);
				target.append(zone < 0 ? '-' : '+').append(String.format("%02d:%02d", zoneAbs / 60, zoneAbs % 60));
			}
		}
	}

	public static String formatDuration(final FEMDuration source) {
		final StringBuilder target = new StringBuilder();
		FEM.formatDuration(target, source);
		return target.toString();
	}

	public static void formatDuration(final StringBuilder target, final FEMDuration source) {
		final int sing = source.signValue();
		if (sing < 0) {
			target.append('-');
		}
		if (sing != 0) {
			target.append('P');
			final int years = source.yearsValue(), months = source.monthsValue();
			if (years != 0) {
				target.append(years).append('Y');
			}
			if (months != 0) {
				target.append(months).append('M');
			}
			final int days = source.daysValue();
			if (days != 0) {
				target.append(days).append('D');
			}
			final int hours = source.hoursValue(), minutes = source.minutesValue(), seconds = source.secondsValue(), milliseconds = source.millisecondsValue();
			if ((hours | minutes | seconds | milliseconds) != 0) {
				target.append('T');
			}
			if (hours != 0) {
				target.append(hours).append('H');
			}
			if (minutes != 0) {
				target.append(minutes).append('M');
			}
			if (milliseconds != 0) {
				target.append(String.format("%d.%03dS", seconds, milliseconds));
			} else if (seconds != 0) {
				target.append(seconds).append('S');
			}
		} else {
			target.append("P0M");
		}
	}

	public static String formatVoid(final FEMVoid femVoid) {
		return null;
	}

	public static String formatArray(FEMArray source) {
		return Scripts.scriptFormatter().formatData((Object)source);
	}

	public static String formatScope(FEMScope source) {
		return  new ScriptFormatter().formatData((Object)source);
	}

}
