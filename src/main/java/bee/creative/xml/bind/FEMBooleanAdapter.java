package bee.creative.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMBoolean;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMBoolean}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMBooleanAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMBooleanAdapter extends XmlAdapter<String, FEMBoolean> {

	/** {@inheritDoc} */
	@Override
	public String marshal(final FEMBoolean value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	/** {@inheritDoc} */
	@Override
	public FEMBoolean unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMBoolean.from(value);
	}

}