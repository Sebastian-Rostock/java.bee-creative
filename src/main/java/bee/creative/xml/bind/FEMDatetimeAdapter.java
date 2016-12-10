package bee.creative.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMDatetime;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMDatetime}, welcher einem Datenfeld via {@code @XmlJavaTypeAdapter(FEMDatetimeAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDatetimeAdapter extends XmlAdapter<String, FEMDatetime> {

	/** {@inheritDoc} */
	@Override
	public String marshal(final FEMDatetime value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	/** {@inheritDoc} */
	@Override
	public FEMDatetime unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMDatetime.from(value);
	}

}