package bee.creative.xml.bind;

import bee.creative.fem.FEMDecimal;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMDecimal}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMDecimalAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDecimalAdapter extends XmlAdapter<String, FEMDecimal> {

	@Override
	public String marshal(final FEMDecimal value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMDecimal unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMDecimal.from(value);
	}

}