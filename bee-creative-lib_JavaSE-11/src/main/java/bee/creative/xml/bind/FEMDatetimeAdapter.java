package bee.creative.xml.bind;

import bee.creative.fem.FEMDatetime;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMDatetime}, welcher einem Datenfeld über
 * {@code @XmlJavaTypeAdapter(FEMDatetimeAdapter.class)} zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDatetimeAdapter extends XmlAdapter<String, FEMDatetime> {

	@Override
	public String marshal(final FEMDatetime value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMDatetime unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMDatetime.from(value);
	}

}