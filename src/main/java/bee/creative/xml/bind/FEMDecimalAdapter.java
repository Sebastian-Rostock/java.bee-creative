package bee.creative.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMDecimal;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMDecimal}, welcher einem Datenfeld via {@code @XmlJavaTypeAdapter(FEMDecimalAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDecimalAdapter extends XmlAdapter<String, FEMDecimal> {

	/** {@inheritDoc} */
	@Override
	public String marshal(final FEMDecimal value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	/** {@inheritDoc} */
	@Override
	public FEMDecimal unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMDecimal.from(value);
	}

}