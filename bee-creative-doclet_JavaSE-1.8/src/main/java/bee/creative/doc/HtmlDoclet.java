package bee.creative.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/** Diese Klasse implementiert ein {@link Doclet}, welches die Inhalte einer {@link RootDoc} in eine {@code json}-Datei exportiert.<br>
 * Der Name dieser Datei wird über die Option {@value #TARGETPATH} angegeben.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HtmlDoclet extends Doclet {

	/** Dieses Feld speichert den Namen der {@link RootDoc#options() Option}, die den Dateinamen der Ausgabedatei angibt. */
	public static final String TARGETPATH = "-targetpath";

	public static void main(final String[] args) throws Exception {
		com.sun.tools.javadoc.Main.execute("javadoc-json-gen", HtmlDoclet.class.getCanonicalName(), new String[]{ //
			"-private", //
			HtmlDoclet.TARGETPATH, "D:\\projects\\java.bee-creative\\bee-creative-doclet_JavaSE-1.8\\src\\main\\web", //
			"-sourcepath", "D:\\projects\\java.bee-creative\\bee-creative-lib_JavaSE-1.7\\src\\main\\java", //
			"bee.creative.array", //
			"bee.creative.bex", //
			"bee.creative.csv", //
			"bee.creative.emu", //
			"bee.creative.fem", //
			"bee.creative.iam", //
			"bee.creative.ini", //
			"bee.creative.io", //
			"bee.creative.lang", //
			"bee.creative.log", //
			"bee.creative.mmi", //
			"bee.creative.qs", //
			"bee.creative.qs.h2", //
			"bee.creative.ref", //
			"bee.creative.util", //
			"bee.creative.xml", //
			"bee.creative.xml.bind", //
		});

	}

	public static boolean start(final RootDoc rootDoc) {
		try {
			final HtmlDoclet gen = new HtmlDoclet();

			gen.run(rootDoc);

			return true;
		} catch (final Throwable cause) {
			cause.printStackTrace();
			return false;
		}
	}

	public static int optionLength(final String option) {
		if (HtmlDoclet.TARGETPATH.equals(option)) return 2;
		return 0;
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	HashMap<Object, OBJ> objectMap = new HashMap<>();

	void runObj(final OBJ res, final Runnable task) {
		final HashMap<Object, OBJ> objectMap = this.objectMap;
		this.objectMap = new HashMap<>(objectMap);
		try {
			this.objectMap.put(null, res);
			task.run();
		} finally {
			this.objectMap = objectMap;
		}
	}

	private RootDoc root;

	void run(final RootDoc src) throws IOException {
		File targetpath = new File(".").getAbsoluteFile();

		for (final String[] option: src.options()) {
			if ((option.length == 2) && HtmlDoclet.TARGETPATH.equals(option[0])) {
				targetpath = new File(option[1]);
			}
		}
		final File file = new File(targetpath, "javadoc.info");

		final OBJ res = this.newObj(src, this::putRootInfo);
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.append("var JAVADOC = ");
			mapper.writeValue(writer, res);
		}

	}

	<SRC> SRC[] sort(final SRC[] src, final Function<? super SRC, String> pr) {
		Arrays.sort(src, (a, b) -> pr.apply(a).compareTo(pr.apply(b)));
		return src;
	}

	<SRC> ARR newArr(final SRC[] src, final PUT<? super OBJ, ? super SRC> put) {
		final ARR res = new ARR();
		for (final SRC src2: src) {
			final OBJ item = this.newObj(src2, put);
			if (!item.isEmpty()) {
				res.add(item);
			}
		}
		return res;
	}

	<SRC> OBJ newObj(final SRC src, final PUT<? super OBJ, ? super SRC> put) {
		final OBJ res = new OBJ();
		put.put(res, src);
		return res;
	}

	void putStr(final OBJ res, final String key, final String src) {
		if ((src == null) || src.isEmpty()) return;
		res.put(key, src);
	}

	void putArr(final OBJ res, final String key, final List<?> src) {
		if ((src == null) || src.isEmpty()) return;
		res.put(key, src);
	}

	<SRC> void putArr(final OBJ res, final String key, final SRC[] src, final PUT<? super OBJ, ? super SRC> put) {
		this.putArr(res, key, this.newArr(src, put));
	}

	void putObj(final OBJ res, final String key, final OBJ val) {
		if ((val == null) || val.isEmpty()) return;
		res.put(key, val);
	}

	<SRC> void putObj(final OBJ res, final String key, final SRC src, final PUT<? super OBJ, ? super SRC> val) {
		this.putObj(res, key, this.newObj(src, val));
	}

	void putBol(final OBJ res, final String key, final boolean src) {
		if (!src) return;
		res.put(key, Boolean.TRUE);
	}

	void putRootInfo(final OBJ res, final RootDoc src) {
		this.root = src;
		this.putArr(res, "packages", this.sort(src.specifiedPackages(), PackageDoc::name), this::putPackageInfo);
	}

	void putPackageInfo(final OBJ res, final PackageDoc src) {
		this.putStr(res, "href", this.calcHref(src));
		this.putStr(res, "name", this.calcName(src));
		this.putArr(res, "docs", src.inlineTags(), this::putDocInfo);
		this.putArr(res, "tags", src.tags(), this::putTag_);
		this.putArr(res, "classes", src.allClasses(), this::putClassInfo);
	}

	void putVarInfo(final OBJ res, final TypeVariable src) {
		this.objectMap.put("V-" + src.typeName(), res);
		this.putStr(res, "href", this.calcHref_VarInfo(src));
		this.putStr(res, "name", this.calcName_VarInfo(src));
		this.putArr(res, "extends", src.bounds(), this::putTypeInfo);
	}

	private String calcName_VarInfo(final TypeVariable src) {
		return src.typeName();
	}

	private String calcHref_VarInfo(final TypeVariable src) {
		return this.calcHref(src.owner()) + "-V-" + src.typeName();
	}

	void putFieldInfo(final OBJ res, final FieldDoc src) {
		this.putStr(res, "href", this.calcHref(src));
		this.putStr(res, "name", src.name());
		this.putBol(res, "isFinal", src.isFinal());
		this.putBol(res, "isStatic", src.isStatic());
		this.putBol(res, "isPublic", src.isPublic());
		this.putBol(res, "isPrivate", src.isPrivate());
		this.putBol(res, "isProtected", src.isProtected());
		this.putBol(res, "isVolatile", src.isVolatile());
		this.putBol(res, "isSynthetic", src.isSynthetic());
		this.putBol(res, "isTransient", src.isTransient());
		this.putObj(res, "type", src.type(), this::putTypeInfo);
		this.putStr(res, "value", src.constantValueExpression());
		this.putArr(res, "docs", src.inlineTags(), this::putDocInfo);
		this.putArr(res, "tags", src.tags(), this::putTag_);
	}

	void putClassInfo(final OBJ res, final ClassDoc src) {
		this.runObj(res, () -> {
			final ClassDoc c = src.containingClass();
			final String key = this.calcHref(src);
			this.putStr(res, "href", key);
			this.putStr(res, "name", this.calcName(src));
			this.putStr(res, "parent", c != null ? this.calcHref(c) : null);
			this.putBol(res, "isFinal", src.isFinal());
			this.putBol(res, "isStatic", src.isStatic());
			this.putBol(res, "isPublic", src.isPublic());
			this.putBol(res, "isPrivate", src.isPrivate());
			this.putBol(res, "isAbstract", src.isAbstract());
			this.putBol(res, "isProtected", src.isProtected());
			this.putBol(res, "isError", src.isError());
			this.putBol(res, "isException", src.isException());
			this.putBol(res, "isInterface", src.isInterface());
			this.putBol(res, "isSerializable", src.isSerializable());
			this.putBol(res, "isExternalizable", src.isExternalizable());

			this.putArr(res, "vars", src.typeParameters(), this::putVarInfo);
			this.putObj(res, "superclass", src.superclassType(), this::putSuper);
			this.putArr(res, "interfaces", src.interfaceTypes(), this::putTypeInfo);
			this.putArr(res, "docs", src.inlineTags(), this::putDocInfo);
			this.putArr(res, "tags", src.tags(), this::putTag_);
			this.putArr(res, "fields", src.fields(), this::putFieldInfo);
			this.putArr(res, "methods", src.methods(), this::putMethodInfo);
			this.putArr(res, "constructors", src.constructors(), this::putConstructorInfo);
		});
	}

	void putMethodInfo(final OBJ res, final MethodDoc src) {
		this.runObj(res, () -> {
			final String key = this.calcHref(src);
			this.putStr(res, "href", key);
			this.putStr(res, "name", src.name());
			this.putBol(res, "isFinal", src.isFinal());
			this.putBol(res, "isStatic", src.isStatic());
			this.putBol(res, "isPublic", src.isPublic());
			this.putBol(res, "isPrivate", src.isPrivate());
			this.putBol(res, "isAbstract", src.isAbstract());
			this.putBol(res, "isProtected", src.isProtected());
			this.putBol(res, "isNative", src.isNative());
			this.putBol(res, "isSynthetic", src.isSynthetic());
			this.putBol(res, "isSynchronized", src.isSynchronized());
			this.putBol(res, "isVarargs", src.isVarArgs());
			this.putArr(res, "vars", src.typeParameters(), this::putVarInfo);
			this.putArr(res, "params", src.parameters(), this::putParameter_);
			this.putArr(res, "throws", src.thrownExceptionTypes(), this::putThrows_);
			this.putObj(res, "returns", src.returnType(), this::putReturnsInfo);
			this.putArr(res, "docs", src.inlineTags(), this::putDocInfo);
			this.putArr(res, "tags", src.tags(), this::putTag_);
		});
	}

	void putConstructorInfo(final OBJ res, final ConstructorDoc src) {
		this.runObj(res, () -> {
			this.putStr(res, "href", this.calcHref(src));
			this.putStr(res, "name", src.name());
			this.putBol(res, "isFinal", src.isFinal());
			this.putBol(res, "isStatic", src.isStatic());
			this.putBol(res, "isPublic", src.isPublic());
			this.putBol(res, "isPrivate", src.isPrivate());
			this.putBol(res, "isProtected", src.isProtected());
			this.putBol(res, "isNative", src.isNative());
			this.putBol(res, "isSynthetic", src.isSynthetic());
			this.putBol(res, "isSynchronized", src.isSynchronized());
			this.putBol(res, "isVarargs", src.isVarArgs());
			this.putArr(res, "vars", src.typeParameters(), this::putVarInfo);
			this.putArr(res, "params", src.parameters(), this::putParameter_);
			this.putArr(res, "throws", src.thrownExceptionTypes(), this::putThrows_);
			this.putArr(res, "docs", src.inlineTags(), this::putDocInfo);
			this.putArr(res, "tags", src.tags(), this::putTag_);
		});
	}

	void putThrows_(final OBJ res, final Type src) {
		this.putObj(res, "type", src, this::putTypeInfo);
		this.objectMap.put("T-" + res.get("href"), res);
	}

	void putReturnsInfo(final OBJ res, final Type src) {
		this.putObj(res, "type", src, this::putTypeInfo);
		this.objectMap.put("R", res);
	}

	void putSuper(final OBJ res, final Type src) {
		if ((src == null) || "java.lang.Object".equals(src.qualifiedTypeName())) return;
		this.putTypeInfo(res, src);
	}

	void putTypeInfo(final OBJ res, final Type src) {
		if (src == null) return;
		if (src.asParameterizedType() != null) {
			final ParameterizedType src2 = src.asParameterizedType();
			this.putStr(res, "href", src2.qualifiedTypeName());
			this.putStr(res, "name", src2.typeName());
			this.putArr(res, "args", src2.typeArguments(), this::putTypeInfo);
		} else if (src.asTypeVariable() != null) {
			final TypeVariable src2 = src.asTypeVariable();
			this.putStr(res, "href", this.calcHref_VarInfo(src2));
			this.putStr(res, "name", this.calcName_VarInfo(src2));
		} else if (src.asWildcardType() != null) {
			final WildcardType src2 = src.asWildcardType();
			this.putArr(res, "super", src2.superBounds(), this::putTypeInfo);
			this.putArr(res, "extends", src2.extendsBounds(), this::putTypeInfo);
		} else if (src.asClassDoc() != null) {
			final ClassDoc src2 = src.asClassDoc();
			this.putStr(res, "href", this.calcHref(src2));
			this.putStr(res, "name", this.calcName(src2));
		} else {
			this.putStr(res, "name", src.simpleTypeName());
		}
		this.putStr(res, "dims", src.dimension());
	}

	void putParameter_(final OBJ res, final Parameter src) {
		final String name = src.name();
		final String href = this.objectMap.get(null).get("href") + "-P-" + name;
		this.putStr(res, "href", href);
		this.putStr(res, "name", name);
		this.putObj(res, "type", src.type(), this::putTypeInfo);
		this.objectMap.put("P-" + name, res);
	}

	void putDocInfo(final OBJ res, final Tag src) {
		if (src instanceof SeeTag) {
			final SeeTag src2 = (SeeTag)src;
			final String text = src2.label();
			this.putStr(res, "href", this.calcHref(src2));
			this.putStr(res, "html", (text != null) && !text.isEmpty() ? text : this.calcName(src2));
		} else if ("@code".equals(src.name())) {
			this.putStr(res, "code", src.text());
		} else if ("@literal".equals(src.name())) {
			this.putStr(res, "text", src.text());
		} else {
			if (!"Text".equals(src.name())) {
				this.putStr(res, "type", src.name());
			}
			this.putStr(res, "html", src.text());
		}
	}

	void putTag_(final OBJ res, final Tag src) {
		if (src instanceof ParamTag) {
			// In putVarInfo vorbereitetes OBJ ergänzen
			final ParamTag src2 = (ParamTag)src;
			final OBJ res2 = this.objectMap.get((src2.isTypeParameter() ? "V-" : "P-") + src2.parameterName());
			if (res2 == null) {
				this.root.printWarning(src2.position(), "@param " + src2.parameterName() + " invalid.");
			} else {
				this.putArr(res2, "docs", src2.inlineTags(), this::putDocInfo);
			}
		} else if (src instanceof ThrowsTag) {

			final ThrowsTag src2 = (ThrowsTag)src;
			final Type x = src2.exceptionType();
			final OBJ res2 = this.objectMap.get("T-" + (x != null ? x.qualifiedTypeName() : src2.exceptionName()));
			if (res2 == null) {
				this.root.printWarning(src2.position(), "@throws " + src2.exceptionName() + " invalid.");
			} else {
				this.putArr(res2, "docs", src2.inlineTags(), this::putDocInfo);
			}
		} else if ("@return".equals(src.name())) {
			final OBJ res2 = this.objectMap.get("R");
			if (res2 == null) {
				this.root.printWarning(src.position(), "@return invalid.");
			} else {
				this.putArr(res2, "docs", src.inlineTags(), this::putDocInfo);
			}
		} else {
			this.putStr(res, "type", src.name());
			// TODO docs bei see ohne erstem text
			if (src instanceof SeeTag) {
				final SeeTag src2 = (SeeTag)src;
				final String text = src2.label();
				this.putStr(res, "href", this.calcHref(src2));
				this.putStr(res, "html", (text != null) && !text.isEmpty() ? text : this.calcSignature(this.calcName(src2)));
			}
			this.putArr(res, "docs", src.inlineTags(), this::putDocInfo);
		}
	}

	{}

	/** Diese Methode gibt die gegebene Zeichenkette ohne generische Parameter zurück.
	 *
	 * @param src Zeichenkette.
	 * @return Zeichenkette ohne {@code '<'}, {@code '>'} und die Texte dazwischen. */
	String calcSignature(final String src) {
		if (src == null) return null;
		final int count = src.length();
		final StringBuilder res = new StringBuilder(count);
		for (int i = 0, level = 0; i < count; i++) {
			final char token = src.charAt(i);
			if (token == '<') {
				level++;
			} else if (token == '>') {
				level--;
			} else if ((level == 0) && (token != ' ')) {
				res.append(token);
			}
		}
		return res.toString();
	}

	String calcSignature(final MethodDoc src) {
		return this.calcSignature(src.flatSignature()) + ':' + src.returnType().typeName() + src.returnType().dimension();
	}

	String calcSignature(final ConstructorDoc src) {
		return this.calcSignature(src.flatSignature());
	}

	{}

	String calcHref(final Doc src) {
		if (src.isField()) return this.calcHref((FieldDoc)src);
		if (src.isMethod()) return this.calcHref((MethodDoc)src);
		if (src.isConstructor()) return this.calcHref((ConstructorDoc)src);
		if (src instanceof PackageDoc) return this.calcHref((PackageDoc)src);
		if (src instanceof ClassDoc) return this.calcHref((ClassDoc)src);
		return null;
	}

	String calcHref(final SeeTag src) {
		final MemberDoc src2 = src.referencedMember();
		if (src2 != null) return this.calcHref(src2);
		final ClassDoc src3 = src.referencedClass();
		if (src3 != null) return this.calcHref(src3);
		final PackageDoc src4 = src.referencedPackage();
		if (src4 != null) return this.calcHref(src4);
		return null;
	}

	String calcHref(final ClassDoc src) {
		return src.qualifiedName();
	}

	String calcHref(final PackageDoc src) {
		return src.name();
	}

	String calcHref(final FieldDoc src) {
		return src.qualifiedName() + ':' + src.type().typeName() + src.type().dimension();
	}

	String calcHref(final MethodDoc src) {
		return src.qualifiedName() + this.calcSignature(src);
	}

	String calcHref(final ConstructorDoc src) {
		return src.qualifiedName() + this.calcSignature(src);
	}

	String calcHref(final ParamTag src) {
		final String href = this.calcHref(src.holder());
		if (href == null) return null;
		return href + "-" + src.parameterName();
	}

	{}

	String calcName(final SeeTag src) {
		final String res = src.label();
		if ((res != null) && !res.isEmpty()) return res;
		final MemberDoc src2 = src.referencedMember();
		if (src2 != null) return this.calcName(src2);
		final ClassDoc src3 = src.referencedClass();
		if (src3 != null) return this.calcName(src3);
		final PackageDoc src4 = src.referencedPackage();
		if (src4 != null) return this.calcName(src4);
		return null;
	}

	String calcName(final ClassDoc src) {
		return src.simpleTypeName();
	}

	String calcName(final PackageDoc src) {
		return src.name();
	}

	String calcName(final MemberDoc src) {
		if (src.isField() || src.isEnumConstant()) return this.calcName((FieldDoc)src);
		if (src.isMethod()) return this.calcName((MethodDoc)src);
		if (src.isConstructor()) return this.calcName((ConstructorDoc)src);
		return "???";
	}

	String calcName(final FieldDoc src) {
		return this.calcName(src.containingClass()) + "." + src.name();
	}

	String calcName(final MethodDoc src) {
		return this.calcName(src.containingClass()) + "." + src.name() + src.flatSignature();
	}

	String calcName(final ConstructorDoc src) {
		return this.calcName(src.containingClass()) + "." + src.name() + src.flatSignature();
	}

	static class ARR extends ArrayList<Object> {

		private static final long serialVersionUID = 4688485079740562562L;

	}

	static class OBJ extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = 958817899797592534L;

	}

	static interface PUT<GRes, GSrc> {

		void put(GRes res, GSrc src);

	}

}
