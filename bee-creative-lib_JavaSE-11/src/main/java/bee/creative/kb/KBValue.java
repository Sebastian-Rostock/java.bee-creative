package bee.creative.kb;

import bee.creative.fem.FEMString;
import bee.creative.util.AbstractEntry;

public class KBValue extends AbstractEntry<Integer, FEMString> {

	public static KBValue from(int valueRef, FEMString valueStr) {
		if ((valueRef == 0) || (valueStr == null)) return null;
		return new KBValue(valueRef, valueStr);
	}

	public int valueRef() {
		return this.valueRef;
	}

	public FEMString valueStr() {
		return this.valueStr;
	}

	@Override
	public Integer getKey() {
		return this.valueRef;
	}

	@Override
	public FEMString getValue() {
		return super.getValue();
	}

	final int valueRef;

	final FEMString valueStr;

	KBValue(int valueRef, FEMString valueStr) {
		this.valueRef = valueRef;
		this.valueStr = valueStr;
	}

}
