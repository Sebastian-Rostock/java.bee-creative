package bee.creative.util;

import bee.creative.util.Tester.Test;

public class Tests {

	public static interface A1Test<GArg1> {

		public void run(GArg1 arg1) throws Throwable;

	}

	public static <GData> Tester runA1Test(final A1Test<? super GData> test, final GData data) {
		return new Tester(new Test() {

			@Override
			public void run() throws Throwable {
				test.run(data);
			}

		});
	}

	public static void printA1Title() {
		System.out.println("Method\tClass\tTime(ms)\tMemory(MB)");
	}

	public static <GData> void printA1Test(final A1Test<? super GData> test, final GData data) {
		Tests.printA1Result(runA1Test(test, data), test, data);
	}

	public static void printA1Result(final Tester tester, final A1Test<?> test, final Object data) {
		final String text =
			String.format("%s\t%s\t%3.1f\t%2.3f", test.toString(), data.getClass().getSimpleName(),
				tester.usedTime / 1000000f, tester.usedMemory / 1024f / 1024f);
		System.out.println(text);

	}

}
