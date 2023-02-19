
declare var JAVADOC: JavadocInfo

interface JavadocInfo {
	packages?: PackageInfo[]
}

interface PackageInfo {
	href: string
	name: string
	docs?: DocInfo[]
	tags?: TagInfo[]
	classes?: ClassInfo[]
}

type DocInfo = DocInfo_CODE | DocInfo_TEXT | DocInfo_LINK | DocInfo_TYPE

interface DocInfo_CODE { code: string }

interface DocInfo_TEXT { text: string }

interface DocInfo_HTML { html: string }

interface DocInfo_LINK extends DocInfo_HTML { href: string }

interface DocInfo_TYPE extends DocInfo_HTML { type: string }

interface TagInfo {
	type: string
	href?: string
	html?: string
	docs?: DocInfo[]
}

interface ClassInfo {
	href: string
	name: string
	parent?: string
	isFinal?: true
	isStatic?: true
	isPublic?: true
	isPrivate?: true
	isAbstract?: true
	isProtected?: true
	isError?: true
	isException?: true
	isInterface?: true
	isSerializable?: true
	isExternalizable?: true
	vars?: TemplateInfo[]
	superclass?: TypeInfo
	interfaces?: TypeInfo[]
	docs?: DocInfo[]
	tags?: TagInfo[]
	fields?: FieldInfo[]
	methods?: MethodInfo[]
	constructors?: ConstructorInfo[]
}

interface TypeInfo {
	href?: string
	name: string
	args?: TypeInfo2[]
	dims?: string // "[]", "[][]", ...
}

type TypeInfo2 = TypeInfo | { super: TypeInfo[] } | { extends: TypeInfo[] }

interface FieldInfo {
	href: string
	name: string
	isFinal?: true
	isStatic?: true
	isPublic?: true
	isPrivate?: true
	isProtected?: true
	isVolatile?: true
	isSynthetic?: true
	isTransient?: true
	type: TypeInfo
	value?: string
	docs?: DocInfo[]
	tags?: TagInfo[]
}

interface MethodInfo {
	href: string
	name: string
	isFinal?: true
	isStatic?: true
	isPublic?: true
	isPrivate?: true
	isAbstract?: true
	isProtected?: true
	isNative?: true
	isSynthetic?: true
	isSynchronized?: true
	isVarargs?: true
	vars?: TemplateInfo[]
	params?: ParameterInfo[]
	throws?: ReturnInfo[]
	returns: ReturnInfo
	docs?: DocInfo[]
	tags?: TagInfo[]
}

interface ConstructorInfo {
	href: string
	isFinal?: true
	isStatic?: true
	isPublic?: true
	isPrivate?: true
	isProtected?: true
	isNative?: true
	isSynthetic?: true
	isSynchronized?: true
	isVarargs?: true
	vars?: TemplateInfo[]
	params?: ParameterInfo[]
	throws?: ReturnInfo[]
	docs?: DocInfo[]
	tags?: TagInfo[]
}

interface ReturnInfo {
	type: TypeInfo
	docs?: DocInfo[]
}

/** Dieser Datentyp beschreibt die Merkmale einer Typvariable. */
interface TemplateInfo {
	docs?: DocInfo[]
	extends?: TypeInfo[]
}

/** Dieser Datentyp beschreibt die Merkmale eines Parameters. */
interface ParameterInfo {
	docs?: DocInfo[]
	type: TypeInfo2
}




