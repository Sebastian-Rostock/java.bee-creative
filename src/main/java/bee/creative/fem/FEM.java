package bee.creative.fem;

import javax.xml.datatype.Duration;

/**
 * FEM - Function Evaluation Model
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class FEM {


	public static String formatBinary(final byte[] binary) throws NullPointerException {
		return null;
	}

	public static String formatBinary(final FEMBinary binary) throws NullPointerException {
		return null;
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

	public static String formatDatetime(final FEMDatetime value) {
		final StringBuilder result = new StringBuilder();
		FEM.formatDatetime(result, value);
		return result.toString();
	}

	public static void formatDatetime(final StringBuilder result, final FEMDatetime value) {
		final boolean hasDate = value.hasDate();
		if (hasDate) {
			result.append(String.format("%04d-%02d-%02d", value.yearValue(), value.monthValue(), value.dateValue()));
		}
		if (value.hasTime()) {
			if (hasDate) {
				result.append('T');
			}
			result.append(String.format("%02d:%02d:%02d", value.hourValue(), value.minuteValue(), value.secondValue()));
			final int millisecond = value.millisecondValue();
			if (millisecond != 0) {
				result.append(String.format(".%03d", millisecond));
			}
		}
		if (value.hasZone()) {
			final int zone = value.zoneValue();
			if (zone == 0) {
				result.append('Z');
			} else {
				int zoneAbs = Math.abs(zone);
				result.append(zone < 0 ? '-' : '+').append(String.format("%02d:%02d", zoneAbs / 60, zoneAbs % 60));
			}
		}
	}

	public static String formatDuration(FEMDuration femDuration) {
		return null;
	}

	public static String formatVoid(FEMVoid femVoid) {
		return null;
	}

}
