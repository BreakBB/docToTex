parser grammar JavadocParser;

options { tokenVocab=JavadocLexer; }

// TODO: Anonyme innere Klassen
// TODO: HTML evtl. unterstützen
// TODO: Tags erweitern mit gültigem Content

documentation
	: EOF
	| docStart EOF
	;

docStart
    : javaPackage? (javaClassDoc | javaClassOrInterface)+
    ;

// =============== Documented ======================
javaClassDoc
    : javaDocStart classTag* JAVADOC_END javaClassOrInterface
    ;

javaFieldDoc
    : javaDocStart fieldTag* JAVADOC_END javaField
    ;

javaConstructorDoc
    : javaDocStart methodOrConstructorTag* JAVADOC_END javaConstructor
    ;

javaMethodDoc
    : javaDocStart (methodTag | methodOrConstructorTag)* JAVADOC_END javaMethod
    ;

insideClassDoc
    : javaClassDoc
    | javaFieldDoc
    | javaConstructorDoc
    | javaMethodDoc
    ;

javaDocStart
    : JAVADOC_START description?
    ;

// =================================================

// =============== Not Documented ==================
javaClassOrInterface
    : javaClassOrInterfaceDef BRACE_OPEN (insideClassDoc | javaClassOrInterface | javaField | javaMethod | javaConstructor)* BRACE_CLOSE
    ;

javaClassOrInterfaceDef
    : annotation* ACCESSMODS? modifier* (CLASS | INTERFACE) NAME polymorphy*
    ;

javaField
    : annotation* ACCESSMODS? modifier* type NAME skipCodeToSemi
    ;

javaConstructor
    : annotation* ACCESSMODS? FUNC_NAME javaParams throwing? block
    ;

// TODO: '...' Operator bei Parametern unterstützen
javaMethod
    : annotation* ACCESSMODS? modifier* type FUNC_NAME javaParams throwing? (SEMI | block)
    ;

// ================================================

// ============== Modifier and more ===============

modifier
    : STATIC
    | FINAL
    | ABSTRACT
    ;

javaParams
    : (javaParam (COMMA javaParam)*)? PARATHESES_CLOSE
    ;

javaParam
    : type NAME
    ;

throwing
    : THROWS typeName (COMMA typeName)*
    ;

polymorphy
    : IMPLEMENTS typeName (COMMA typeName)*
    | EXTENDS typeName
    ;

classType
    : CLASS
    | INTERFACE
    ;

type
    : typeName (BRACKET_OPEN BRACKET_CLOSE | (ANGLE_BRACKET_OPEN type (COMMA type)* ANGLE_BRACKET_CLOSE))*
    ;

typeName
    : TYPE_NAME
    | NAME
    ;

// ===============================================

block
    : BRACE_OPEN (skipToBrace* block)* skipToBrace* BRACE_CLOSE
    ;

annotation
    : AT (typeName | (FUNC_NAME skipCodeToParatheses) | (typeName PARATHESES_OPEN skipCodeToParatheses))
    ;

javaPackage
    : PACKAGE typeName SEMI
    ;

// ===============================================

// ======== Documentation content ================

description
    : (docText | inlineTag)+
	;

docText
    : ACCESSMODS
    | CLASS
    | INTERFACE
    | FINAL
    | STATIC
    | ABSTRACT
    | EXTENDS
    | IMPLEMENTS
    | PACKAGE
    | IMPORT
    | COMMA
    | SEMI
    | AT
    | HASHTAG
    | DOT
    | QUOTE
    | SINGLE_QUOTE
    | FUNC_NAME
    | SEE_REF
    | TYPE_NAME
    | NAME
    | STAR
    | SLASH
    | JAVADOC_START
    | BRACE_OPEN
    | BRACE_CLOSE
    | PARATHESES_OPEN
    | PARATHESES_CLOSE
    | BRACKET_OPEN
    | BRACKET_CLOSE
    | ANGLE_BRACKET_OPEN
    | ANGLE_BRACKET_CLOSE
    | TEXT_CONTENT
    ;

inlineTag
	: inlineTagName inlineTagContent? BRACE_CLOSE
	;

// ============ Inline Tag content ===============
inlineTagName
	: INLINE_CODE
	| INLINE_DOC_ROOT
	| INLINE_INHERIT_DOC
	| INLINE_LINK_PLAIN
	| INLINE_LINK
	| INLINE_VALUE
	;

inlineTagContent
	: braceContent+
	;

// ==============================================

braceExpression
	: BRACE_OPEN braceContent* BRACE_CLOSE
	;

braceContent
	: braceExpression
	| braceText+
	;

braceText
	: ~(BRACE_CLOSE | JAVADOC_END)
	;

// ================================================

// ============= Documented Tags ==================

tag
    : SEE (typeName | SEE_REF) classTagEnd*    # See
    | SINCE classTagEnd+                       # Since
    | DEPRECATED classTagEnd+                  # Deprecated
    ;

classTag
    : AUTHOR classTagEnd+
    | SERIAL classTagEnd+
    | VERSION classTagEnd+
    | tag
    ;

classTagEnd
    : ~(JAVADOC_END | SERIAL | VERSION | SEE | SINCE | DEPRECATED)
    ;

fieldTag
    : SERIAL
    | SERIAL_FIELD
    | tag
    ;

methodOrConstructorTag
    : PARAM NAME methodOrConstructorTagEnd+         # Params
    | EXCEPTION typeName methodOrConstructorTagEnd+ # Throws
    | SERIAL_DATA                                   # SerialData
    | tag                                           # TagTag
    ;

methodOrConstructorTagEnd
    : ~(JAVADOC_END | PARAM | EXCEPTION | SERIAL_DATA | SEE | SINCE | DEPRECATED)
    ;

methodTag
    :  RETURN NAME+
    ;

// =====================================================

// =========== Skip code ===============================

skipCodeToSemi
    : ~SEMI* SEMI
    ;

skipToBrace
    : ~(BRACE_CLOSE | BRACE_OPEN)
    ;

skipCodeToParatheses
    : noQuoteOrPclose* ((QUOTE skipToQuote skipCodeToParatheses) | PARATHESES_CLOSE)
    ;

noQuoteOrPclose
    : ~(PARATHESES_CLOSE | QUOTE)
    ;

skipToQuote
    : notQuote* QUOTE
    ;

notQuote
    : ~QUOTE
    ;
