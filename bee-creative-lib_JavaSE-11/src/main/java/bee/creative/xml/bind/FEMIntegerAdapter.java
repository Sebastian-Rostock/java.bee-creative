package bee.creative.xml.bind;

import bee.creative.fem.FEMInteger;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMInteger}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMIntegerAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMIntegerAdapter extends XmlAdapter<String, FEMInteger> {

	@Override
	public String marshal(FEMInteger value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMInteger unmarshal(String value) throws Exception {
		if (value == null) return null;
		return FEMInteger.femIntegerFrom(value);
	}

}