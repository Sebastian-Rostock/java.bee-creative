package bee.creative.fem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.util.Natives;

/** Diese Klasse stellt {@link FEMFunction Funktionen} zum Lesen und Schreiben von {@link Field nativen Datenfeldern} sowie zum Aufrufen von {@link Method
 * nativen Methoden} und {@link Constructor nativen Konstruktoren} bereit.
 * 
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMNativeFunction extends FEMBaseFunction {

	@SuppressWarnings ("javadoc")
	static abstract class FromField extends FEMNativeFunction {

		@Override
		public abstract Field member();

		@Override
		public boolean isField() {
			return true;
		}

		@Override
		public String toString() {
			return Natives.formatField(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	static abstract class FromMethod extends FEMNativeFunction {

		@Override
		public abstract Method member();

		@Override
		public boolean isMethod() {
			return true;
		}

		@Override
		public String toString() {
			return Natives.formatMethod(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	static abstract class FromConstructor extends FEMNativeFunction {

		@Override
		public abstract Constructor<?> member();

		@Override
		public boolean isStatic() {
			return true;
		}

		@Override
		public boolean isConstructor() {
			return true;
		}

		@Override
		public String toString() {
			return Natives.formatConstructor(this.member());
		}

	}

	{}

	/** Diese Methode gibt die native Funktion zur gegebenen Eingabe zurück.<br>
	 * Die Eingabe kann hierbei eine Funktion kodieren, die eine Klasse liefert, an eine Methode bzw. einen Konstruktor delegiert oder ein Datenfeld liest bzw.
	 * schreibt.
	 * <p>
	 * <h4>{@code "CLASS_PATH.class"}</h4> Dieser Pfad ergibt {@link FEMNative#from(Object) FEMNative.from(CLASS_PATH.class)}.
	 * <p>
	 * <h4>{@code "CLASS_PATH.FIELD_NAME"}</h4> Dieser Pfad ergibt {@link #from(Field) fromField(CLASS_PATH.class.getDeclaredField("FIELD_NAME"))}.
	 * <p>
	 * <h4>{@code "CLASS_PATH.new(TYPE_1,...,TYPE_N)"}</h4> Dieser Pfad ergibt {@link #from(Constructor)
	 * fromConstructor(CLASS_PATH.class.getDeclaredConstructor(TYPE_1.class, ..., TYPE_N.class))}.
	 * <p>
	 * <h4>{@code "CLASS_PATH.METHOD_NAME(TYPE1_1,...,TYPE_N)"}</h4> Dieser Pfad ergibt {@link #from(Method)
	 * fromMethod(CLASS_PATH.class.getDeclaredMethod("METHOD_NAME", TYPE_1.class, ..., TYPE_N.class))}.
	 * <p>
	 * 
	 * @see #from(Field)
	 * @see #from(Method)
	 * @see #from(Constructor)
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktord oder eines Datenfelds.
	 * @return {@link FEMNativeFunction}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Natives#parseMethod(String)} oder {@link Natives#parseConstructor(String)} eine entsprechende Ausnahme
	 *         auslöst.
	 * @throws ReflectiveOperationException Wenn {@link Natives#parseField(String)}, {@link Natives#parseMethod(String)}, {@link Natives#parseConstructor(String)}
	 *         oder {@link Natives#parseClass(String)} eine entsprechende Ausnahme auslöst. */
	public static FEMFunction from(final String memberPath) throws NullPointerException, IllegalArgumentException, ReflectiveOperationException {
		if (memberPath.endsWith(".class")) return FEMNative.from(Natives.parseClass(memberPath.substring(0, memberPath.length() - 6)));
		if (memberPath.contains(".new(")) return FEMNativeFunction.from(Natives.parseConstructor(memberPath));
		if (memberPath.contains("(")) return FEMNativeFunction.from(Natives.parseMethod(memberPath));
		return FEMNativeFunction.from(Natives.parseField(memberPath));
	}

	/** Diese Methode gibt eine Funktion zurück, mit welcher der Wert des gegebenen Datenfelds gelesen sowie geschrieben werden kann.<br>
	 * Wenn das gegebene Datenfeld {@code static} ist, muss die gelieferte Funktion es zum Lesen ohne Parameter und zum Schreiben mit dem Wert als Parameter
	 * aufgerufen werden. Andernfalls muss die gelieferte Funktion es zum Lesen mit dem Objekt und zum Schreiben mit dem Objekt und dem Wert als Parameter
	 * aufgerufen werden. Die gelieferte Funktion liefert beim Schreiben stets {@link FEMNative#NULL}.
	 * 
	 * @param field Datenfeld.
	 * @return Funktion zum gegebenen Datenfeld.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static FEMNativeFunction from(final Field field) throws NullPointerException {
		return Modifier.isStatic(field.getModifiers()) ? //
			new FromField() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						switch (frame.size()) {
							case 0:
								final Object getValue = field.get(null);
								return FEMNative.from(getValue);
							case 1:
								final Object setValue = frame.get(0).data();
								field.set(null, setValue);
								return FEMNative.NULL;
						}
						throw new IllegalArgumentException();
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Field member() {
					return field;
				}

				@Override
				public boolean isStatic() {
					return true;
				}

			} : //
			new FromField() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						switch (frame.size()) {
							case 1:
								final Object getInput = frame.get(0).data();
								final Object getValue = field.get(getInput);
								return FEMNative.from(getValue);
							case 2:
								final Object setInput = frame.get(0).data();
								final Object setValue = frame.get(1).data();
								field.set(setInput, setValue);
								return FEMNative.NULL;
						}
						throw new IllegalArgumentException();
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Field member() {
					return field;
				}

				@Override
				public boolean isStatic() {
					return false;
				}

			};
	}

	/** Diese Methode gibt eine Funktion zurück, die an die gegebene Methode delegiert.
	 * 
	 * @param method Methode.
	 * @return Funktion zur gegebenen Methode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static FEMNativeFunction from(final Method method) throws NullPointerException {
		return Modifier.isStatic(method.getModifiers()) ? //
			new FromMethod() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						final Object[] params = FEMNativeFunction._params_(frame, false);
						final Object result = method.invoke(null, params);
						return FEMNative.from(result);
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Method member() {
					return method;
				}

				@Override
				public boolean isStatic() {
					return true;
				}

			} : //
			new FromMethod() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						final Object[] params = FEMNativeFunction._params_(frame, true);
						final Object input = frame.get(0).data();
						final Object result = method.invoke(input, params);
						return FEMNative.from(result);
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Method member() {
					return method;
				}

				@Override
				public boolean isStatic() {
					return false;
				}

			};
	}

	/** Diese Methode gibt eine Funktion zurück, die an den gegebenen Konstruktor delegiert.
	 * 
	 * @param constructor Konstruktor.
	 * @return Funktion zum gegebenen Konstruktor.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static FEMNativeFunction from(final Constructor<?> constructor) throws NullPointerException {
		if (constructor == null) throw new NullPointerException("constructor = null");
		return new FromConstructor() {

			@Override
			public FEMValue invoke(final FEMFrame frame) {
				try {
					final Object[] params = FEMNativeFunction._params_(frame, false);
					final Object result = constructor.newInstance(params);
					return FEMNative.from(result);
				} catch (final Exception cause) {
					throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
				}
			}

			@Override
			public Constructor<?> member() {
				return constructor;
			}

			@Override
			public boolean isStatic() {
				return true;
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static Object[] _params_(final FEMFrame frame, final boolean skipFirst) {
		final int offset = skipFirst ? 1 : 0, length = frame.size() - offset;
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = frame.get(i + offset).data();
		}
		return result;
	}

	{}

	/** Diese Methode gibt den {@link Member} zurück, auf den sich die Methode {@link #invoke(FEMFrame)} bezieht.
	 * 
	 * @return {@link Member}. */
	public abstract Member member();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} {@code static} ist.
	 * 
	 * @return Bezugskennzeichnung. */
	public abstract boolean isStatic();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} ein {@link Field} ist.
	 * 
	 * @return Feldkennzeichnung. */
	public boolean isField() {
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} eine {@link Method} ist.
	 * 
	 * @return Methodenkennzeichung. */
	public boolean isMethod() {
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} ein {@link Constructor} ist.
	 * 
	 * @return Konstructorkennzeichung. */
	public boolean isConstructor() {
		return false;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(this.toString());
	}

}