package bee.creative.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMString;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMString}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMStringAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMStringAdapter extends XmlAdapter<String, FEMString> {

	/** {@inheritDoc} */
	@Override
	public String marshal(final FEMString value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	/** {@inheritDoc} */
	@Override
	public FEMString unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMString.from(value);
	}

}