package bee.creative._dev_;

import bee.creative.fem.FEMArray;
import bee.creative.fem.FEMBinary;
import bee.creative.fem.FEMBoolean;
import bee.creative.fem.FEMDatetime;
import bee.creative.fem.FEMDecimal;
import bee.creative.fem.FEMDuration;
import bee.creative.fem.FEMException;
import bee.creative.fem.FEMFunction;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.fem.FEMHandler;
import bee.creative.fem.FEMInteger;
import bee.creative.fem.FEMNative;
import bee.creative.fem.FEMObject;
import bee.creative.fem.FEMParam;
import bee.creative.fem.FEMProxy;
import bee.creative.fem.FEMString;
import bee.creative.fem.FEMTable;
import bee.creative.fem.FEMValue;
import bee.creative.fem.FEMVoid;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.iam.IAMIndex;
import bee.creative.util.Producer;
import bee.creative.util.Property;

// TODO de-/coder für ausgewählte fem-datentypen in iam-format (und json-format)
// json-format aus string und object[] derselben, de-/coder ebenfalls in javascript
public class FEMIndex implements Producer<FEMValue> {

	public static abstract class IndexBuilder extends FEMIndex implements Property<FEMValue> {

		IAMListingBuilder arrayPool = new IAMListingBuilder();
		
		IAMListingBuilder handlerPool = new IAMListingBuilder();
		
		IAMListingBuilder stringPool = new IAMListingBuilder();
		
		IAMListingBuilder binaryPool = new IAMListingBuilder();
		
		IAMListingBuilder integerPool = new IAMListingBuilder();
		
		IAMListingBuilder decimalPool = new IAMListingBuilder();
		
		IAMListingBuilder durationPool = new IAMListingBuilder();
		
		IAMListingBuilder datetimePool = new IAMListingBuilder();
		
		IAMListingBuilder objectPool = new IAMListingBuilder();
		
		IAMListingBuilder tablePool = new IAMListingBuilder();
		
		protected FEMValue property;

		public int putValue(final FEMValue source) throws IllegalArgumentException {
			switch (source.type().id()) {
				case FEMNative.ID:
					return this.customPutNative((FEMNative)source.result());
				case FEMVoid.ID:
					return this.customPutVoid();
				case FEMArray.ID:
					return this.customPutArray((FEMArray)source.data());
				case FEMHandler.ID:
					return this.customPutHandler((FEMHandler)source.data());
				case FEMBoolean.ID:
					return this.customPutBoolean((FEMBoolean)source.data());
				case FEMString.ID:
					return this.customPutString((FEMString)source.data());
				case FEMBinary.ID:
					return this.customPutBinary((FEMBinary)source.data());
				case FEMInteger.ID:
					return this.customPutInteger((FEMInteger)source.data());
				case FEMDecimal.ID:
					return this.customPutDecimal((FEMDecimal)source.data());
				case FEMDuration.ID:
					return this.customPutDuration((FEMDuration)source.data());
				case FEMDatetime.ID:
					return this.customPutDatetime((FEMDatetime)source.data());
				case FEMObject.ID:
					return this.customPutObject((FEMObject)source.data());
				case FEMTable.ID:
					return this.customPutTable((FEMTable)source.data());
			}
			throw new IllegalArgumentException();
		}

		public int[] putValues(final FEMValue... source) throws IllegalArgumentException {
			final int length = source.length;
			final int[] result = new int[length];
			for (int i = 0; i < length; i++) {
				result[i] = this.putValue(source[i]);
			}
			return result;
		}

		public int putFunction(final FEMFunction source) throws FEMException {
			if (source instanceof FEMValue) return this.putValue((FEMValue)source);
			if (source instanceof FEMProxy) return this.customPutProxy((FEMProxy)source);
			if (source instanceof FEMParam) return this.customPutParam((FEMParam)source);
			if (source instanceof FEMParam) return this.customPutParam((FEMParam)source);
			if (source instanceof ConcatFunction) return this.customPutConcat((ConcatFunction)source);
			if (source instanceof ClosureFunction) return this.customPutClosure((ClosureFunction)source);
			if (source instanceof CompositeFunction) return this.customPutComposite((CompositeFunction)source);
			throw new IllegalArgumentException();
		}

		public int[] putFunctions(final FEMFunction... source) throws NullPointerException, IllegalArgumentException {
			final int length = source.length;
			final int[] result = new int[length];
			for (int i = 0; i < length; i++) {
				result[i] = this.putFunction(source[i]);
			}
			return result;
		}

		protected int customPutProxy(final FEMProxy source) {
			throw new IllegalArgumentException();
		}

		protected int customPutParam(final FEMParam source) {
			throw new IllegalArgumentException();
		}

		protected int customPutConcat(final ConcatFunction source) {
			return this.customPutConcat(this.putFunction(source.function()), this.putFunctions(source.params()));
		}

		protected int customPutConcat(final int functionRef, final int[] paramRefs) {
			throw new IllegalArgumentException();
		}

		protected int customPutClosure(final ClosureFunction source) {
			return this.customPutClosure(this.putFunction(source.function()));
		}

		protected int customPutClosure(final int functionRef) {
			throw new IllegalArgumentException();
		}

		protected int customPutComposite(final CompositeFunction source) {
			return this.customPutComposite(this.putFunction(source.function()), this.putFunctions(source.params()));
		}

		protected int customPutComposite(final int functionRef, final int[] paramRefs) {
			throw new IllegalArgumentException();
		}

		protected int customPutNative(final FEMNative source) throws NullPointerException, IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutVoid() throws NullPointerException {
			throw new IllegalArgumentException();
		}

		protected int customPutArray(final FEMArray source) throws NullPointerException, IllegalArgumentException {
			return this.customPutArray(this.putValues(source.value()));
		}

		protected int customPutArray(final int[] valueRefs) {
			throw new IllegalArgumentException();
		}

		protected int customPutHandler(final FEMHandler source) throws IllegalArgumentException {
			return this.customPutHandler(this.putFunction(source.value()));
		}

		protected int customPutHandler(final int functionRef) {
			throw new IllegalArgumentException();
		}

		protected int customPutBoolean(final FEMBoolean source) throws NullPointerException {
			throw new IllegalArgumentException();
		}

		protected int customPutString(final FEMString source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutBinary(final FEMBinary source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutInteger(final FEMInteger source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutDecimal(final FEMDecimal source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutDuration(final FEMDuration source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutDatetime(final FEMDatetime source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutObject(final FEMObject source) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}

		protected int customPutTable(FEMTable data) {
			throw new IllegalArgumentException();
		}

		@Override
		public FEMValue get() {
			return super.get();
		}

		@Override
		public void set(FEMValue value) {

		}

	}

	public static abstract class IndexLoader extends FEMIndex {

	}

	@Override
	public FEMValue get() {
		return FEMVoid.INSTANCE;
	}

	public FEMValue value(int ref) {
		return FEMVoid.INSTANCE;
	}

	public FEMFunction function(int ref) {
		return FEMVoid.INSTANCE;
	}

}
