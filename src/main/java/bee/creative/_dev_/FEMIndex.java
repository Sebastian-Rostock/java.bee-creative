package bee.creative._dev_;

import bee.creative.bex.BEXFile;
import bee.creative.fem.FEMArray;
import bee.creative.fem.FEMBinary;
import bee.creative.fem.FEMBoolean;
import bee.creative.fem.FEMDatetime;
import bee.creative.fem.FEMDecimal;
import bee.creative.fem.FEMDuration;
import bee.creative.fem.FEMFunction;
import bee.creative.fem.FEMInteger;
import bee.creative.fem.FEMString;
import bee.creative.fem.FEMValue;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.util.Producer;

// TODO de-/coder für ausgewählte fem-datentypen in iam-format und json-format
// json-format aus string und object[] derselben, de-/coder ebenfalls in javascript
public abstract class FEMIndex {

	public static class FEM2IAM {

		IAMIndexBuilder indexBuilder;

	}

	/** Diese Klasse implementiert .
	 * <p>
	 * {@link #get()} liefert object[] aus pool und rootiRef
	 * <p>
	 * pool ist object[] aus string und object[] object[] kann string oder number enthalten */
	public static class FEM_JSON implements Producer<Object> {

		// hashmap mit int value
		
		
		
		// werte als string kodieren und häufigkeit der referenzen zählen
		// häufigste an den listenbeginn sortieren
		// 1-malige inline kodieren, übrige per referenz auf häufigen eintrag in pool
		//

		public int put(FEMValue value) {
// todo switch
			return 0;
		}
		
		protected int customPutVoid() {
			return 0;
		}

		protected int customPutArray(FEMArray value) {
			return 0;
		}
		
		protected int customPutString(FEMString value) {
			return 0;
		}
		
		protected int customPutBinary(FEMBinary value) {
			return 0;
		}

		protected int customPutInteger(FEMInteger value) {
			return 0;
		}

		protected int customPutDecimal(FEMDecimal value) {
			return 0;
		}

		protected int customPutBoolean(FEMBoolean value) {
			return 0;
		}

		protected int customPutDuration(FEMDuration value) {
			return 0;
		}

		protected int customPutDatetime(FEMDatetime value) {
			return 0;
		}
		
		/** Diese Methode leert den konfigurator */
		public void cleaar() {

		}

		@Override
		public Object get() {
			return null;
		}

	}

	
	public abstract FEMValue value(int ref);
	
	public abstract FEMFunction function(int ref);
	
	
}
