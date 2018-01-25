parser grammar JavadocParser;

options { tokenVocab=JavadocLexer; }


// TODO: package und imports zu Beginn skippen
// TODO: Anonyme innere Klassen
// TODO: HTML evtl. unterstützen

documentation
	: EOF
	| javaPackage? imports* (javaClassDoc | javaClassOrInterface) EOF
	;

// =============== Documented ======================
javaClassDoc
    : JAVADOC_START description? classTag* JAVADOC_END javaClassOrInterface
    ;

javaFieldDoc
    : JAVADOC_START description? fieldTag* JAVADOC_END javaMethod
    ;

javaConstrutorDoc
    : JAVADOC_START description? methodOrConstructorTag* JAVADOC_END javaConstrutor
    ;

javaMethodDoc
    : JAVADOC_START description? (methodTag | methodOrConstructorTag)* JAVADOC_END javaMethod
    ;

insideClassDoc
    : javaClassDoc
    | javaFieldDoc
    | javaConstrutorDoc
    | javaMethodDoc
    ;

// =================================================

// =============== Not Documented ==================
javaClassOrInterface
    : annotation* ACCESSMODS? modifier? (CLASS | INTERFACE) type polymorphy? BRACE_OPEN (insideClassDoc | javaClassOrInterface | javaField | javaMethod | javaConstrutor)* BRACE_CLOSE
    ;

javaField
    : annotation* ACCESSMODS? modifier? type NAME skipCodeToSemi
    ;

javaConstrutor
    : annotation* ACCESSMODS? FUNC_NAME javaParams block
    ;

// TODO: '...' Operator bei Parametern unterstützen
javaMethod
    : annotation* ACCESSMODS? modifier? type FUNC_NAME javaParams (SEMI | block)
    ;

// ================================================

// ============== Modifier and more ===============
modifier
    : STATIC
    | FINAL
    | ABSTRACT
    | FINAL STATIC
    | STATIC FINAL
    | STATIC ABSTRACT
    | ABSTRACT STATIC
    ;

// TODO: Include Array, Genertics

javaParams
    : (type NAME (COMMA type NAME)*)? PARATHESES_CLOSE
    ;

polymorphy
    : javaExtends javaImplements
    | javaImplements
    | javaExtends
    ;

type
    : typeName (BRACKETS | (ANGLE_BRACKET_OPEN type (COMMA type)* ANGLE_BRACKET_CLOSE))*
    ;

typeName
    : TYPE_NAME
    | NAME
    ;

javaExtends
    : EXTENDS typeName
    ;

javaImplements
    : IMPLEMENTS typeName (COMMA typeName)*
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

imports
    : IMPORT typeName SEMI
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
    | DOT
    | QUOTE
    | SINGLE_QUOTE
    | FUNC_NAME
    | TYPE_NAME
    | NAME
    | STAR
    | SLASH
    | JAVADOC_START
    | BRACE_OPEN
    | BRACE_CLOSE
    | PARATHESES_OPEN
    | PARATHESES_CLOSE
    | BRACKETS
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

classTag
    : AUTHOR ~(JAVADOC_END | COMMA)+ (COMMA ~JAVADOC_END+)*
    | DATE
    | DEPRECATED
    | SEE
    | SERIAL
    | SINCE
    | VERSION
    ;

fieldTag
    : SEE
    | SINCE
    | DEPRECATED
    | SERIAL
    | SERIAL_FIELD
    ;

methodOrConstructorTag
    : SEE
    | SINCE
    | DEPRECATED
    | PARAM NAME NAME+
    | THROWS
    | EXCEPTION
    | SERIAL_DATA
    ;

methodTag
    :  RETURN
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
    : ~(PARATHESES_CLOSE | QUOTE)* ((QUOTE skipToQuote skipCodeToParatheses) | PARATHESES_CLOSE)
    ;

skipToQuote
    : ~QUOTE* QUOTE
    ;


//TODO: skipCode fuer falsche Token,die zu Fehler fuehren
skipCode
    : .
    ;
