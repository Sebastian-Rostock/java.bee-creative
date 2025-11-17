package bee.creative.xml.bind;

import bee.creative.fem.FEMBinary;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMBinary}, welcher einem Datenfeld über {@code @XmlJavaTypeAdapter(FEMBinaryAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMBinaryAdapter extends XmlAdapter<String, FEMBinary> {

	@Override
	public String marshal(FEMBinary value) throws Exception {
		if (value == null) return null;
		return value.toString(false);
	}

	@Override
	public FEMBinary unmarshal(String value) throws Exception {
		if (value == null) return null;
		return FEMBinary.femBinaryFrom(false, value);
	}

}