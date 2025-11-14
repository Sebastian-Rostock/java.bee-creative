package bee.creative.xml.bind;

import bee.creative.fem.FEMString;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMString}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMStringAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMStringAdapter extends XmlAdapter<String, FEMString> {

	@Override
	public String marshal(FEMString value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMString unmarshal(String value) throws Exception {
		if (value == null) return null;
		return FEMString.from(value);
	}

}