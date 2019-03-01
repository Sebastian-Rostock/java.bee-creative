package bee.creative.log;

@SuppressWarnings ("javadoc")
class LoggerNodeO extends LoggerNode {

	LoggerNodeO(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	boolean isOpen() {
		return true;
	}

}