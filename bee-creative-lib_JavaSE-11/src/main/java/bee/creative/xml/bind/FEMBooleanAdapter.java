package bee.creative.xml.bind;

import bee.creative.fem.FEMBoolean;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMBoolean}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMBooleanAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMBooleanAdapter extends XmlAdapter<String, FEMBoolean> {

	@Override
	public String marshal(FEMBoolean value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMBoolean unmarshal(String value) throws Exception {
		if (value == null) return null;
		return FEMBoolean.from(value);
	}

}