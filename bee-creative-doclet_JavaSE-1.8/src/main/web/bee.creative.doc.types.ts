
interface RootInfo {
	packages?: PackageInfo[]
}

interface PackageInfo {
	href: string
	name: string
	docs?: DocInfo[]
	tags?: TagInfo[]
	clsses?: ClassInfo[]
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
	vars?: VarInfo[]
	superclass?: TypeInfo
	interfaces?: TypeInfo[]
	docs?: DocInfo[]
	tags?: TagInfo[]
	fields?: FieldInfo[]
	methods?: MethodInfo[]
	constructors?: ConstructorInfo[]
}

interface VarInfo {
	href: string
	name: string
	docs?: DocInfo[]
	extends?: TypeInfo[]
}

type TypeInfo = TypeInfo_CL | TypeInfo_SU | TypeInfo_EX

interface TypeInfo_CL {
	href?: string
	name: string
	args?: TypeInfo[]
	dims?: string // "[]", "[][]", ...
}

interface TypeInfo_SU { super: TypeInfo[] }

interface TypeInfo_EX { extends: TypeInfo[] }

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
	vars?: VarInfo[]
	params?: ParamInfo[]
	result: ReturnInfo
	throws?: ReturnInfo[]
	docs?: DocInfo[]
	tags?: TagInfo[]
}

interface ConstructorInfo {
	href: string
	name: string
	isFinal?: true
	isStatic?: true
	isPublic?: true
	isPrivate?: true
	isProtected?: true
	isNative?: true
	isSynthetic?: true
	isSynchronized?: true
	isVarargs?: true
	vars?: VarInfo[]
	params?: ParamInfo[]
	throws?: ReturnInfo[]
	docs?: DocInfo[]
	tags?: TagInfo[]
}

interface ParamInfo {
	href: string
	name: string
	type: TypeInfo
	docs?: DocInfo[]
}

interface ReturnInfo extends TypeInfo_CL {
	docs?: DocInfo[]
}

