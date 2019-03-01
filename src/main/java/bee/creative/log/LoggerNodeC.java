package bee.creative.log;

@SuppressWarnings ("javadoc")
class LoggerNodeC extends LoggerNode {

	LoggerNodeC(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	boolean isClose() {
		return true;
	}

}