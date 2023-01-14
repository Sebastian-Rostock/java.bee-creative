package bee.creative.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import bee.creative.fem.FEMFunction;
import bee.creative.fem.FEMReflection;

/** Diese Klasse implementiert den {@link XmlAdapter} für {@link FEMReflection#from(String)}, welcher einem Datenfeld via
 * {@code @XmlJavaTypeAdapter(FEMReflectionAdapter.class)} zugeordnet werden kann.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMReflectionAdapter extends XmlAdapter<String, FEMFunction> {

	@Override
	public String marshal(final FEMFunction value) throws Exception {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public FEMFunction unmarshal(final String value) throws Exception {
		if (value == null) return null;
		return FEMReflection.from(value);
	}

}