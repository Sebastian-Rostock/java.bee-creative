package bee.creative.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMObject;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMObject}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMObjectAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMObjectAdapter extends XmlAdapter<String, FEMObject> {

	@Override
	public String marshal(final FEMObject value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMObject unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMObject.from(value);
	}

}