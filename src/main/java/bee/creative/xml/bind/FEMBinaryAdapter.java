package bee.creative.xml.bind;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMBinary;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMBinary}, welcher einem Datenfeld via {@code @XmlJavaTypeAdapter(FEMBinaryAdapter.class)}
 * zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMBinaryAdapter extends XmlAdapter<String, FEMBinary> {

	/** {@inheritDoc} */
	@Override
	public String marshal(final FEMBinary value) throws Exception {
		if (value == null) return null;
		return DatatypeConverter.printHexBinary(value.value());
	}

	/** {@inheritDoc} */
	@Override
	public FEMBinary unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMBinary.from(DatatypeConverter.parseHexBinary(value));
	}

}