package bee.creative.xml.bind;

import bee.creative.fem.FEMDuration;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMDuration}, welcher einem Datenfeld über
 * {@code @XmlJavaTypeAdapter(FEMDurationAdapter.class)} zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDurationAdapter extends XmlAdapter<String, FEMDuration> {

	@Override
	public String marshal(final FEMDuration value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMDuration unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMDuration.from(value);
	}

}