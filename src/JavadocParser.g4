parser grammar JavadocParser;

options { tokenVocab=JavadocLexer; }


// TODO: package und imports skippen
// TODO: Anonyme innere Klassen

documentation
	: EOF
	| (javaClassDoc | javaClassOrInterface) EOF
	;

javaClassDoc
    : JAVADOC_START description? classTag* JAVADOC_END javaClassOrInterface
    ;

javaMethodDoc
    : JAVADOC_START description? (methodTag | methodOrConstructorTag)* JAVADOC_END javaMethod
    ;

javaFieldDoc
    : JAVADOC_START description? fieldTag* JAVADOC_END javaMethod
    ;

javaConstrutorDoc
    : JAVADOC_START description? methodOrConstructorTag* JAVADOC_END javaConstrutor
    ;

insideClassDoc
    : javaClassDoc
    | javaFieldDoc
    | javaMethodDoc
    | javaConstrutorDoc
    ;

// TODO: siehe TODO bei descriptionNewLine ausserdem sollten dann die einzelnen Elemente gekuerzt werden
description
//	: descriptionLine (descriptionNewline+ descriptionLine)*
    : (NAME | inlineTag)+
	;

blockTagTextElement
	: TEXT_CONTENT
	| NAME
	| STAR
	| SLASH
/*	| BRACE_OPEN
	| BRACE_CLOSE
*/	;

inlineTag
	: INLINE_TAG_START inlineTagName inlineTagContent? BRACE_CLOSE
	;

inlineTagName
	: NAME
	;

inlineTagContent
	: braceContent+
	;

braceExpression
	: BRACE_OPEN braceContent* BRACE_CLOSE
	;

braceContent
	: braceExpression
	| braceText (NEWLINE* braceText)*
	;

braceText
	: TEXT_CONTENT
	| NAME
	| SPACE
	| STAR
	| SLASH
	| NEWLINE
	;

classTag
    : AUTHOR NAME+ (COMMA NAME+)*
    | DATE
    | DEPRECATED
    | SEE
    | SERIAL
    | SINCE
    | VERSION
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

fieldTag
    : SEE
    | SINCE
    | DEPRECATED
    | SERIAL
    | SERIAL_FIELD
    ;

javaClassOrInterface
    : ACCESSMODS? modifier? (CLASS | INTERFACE) NAME polymorphy? BRACE_OPEN (insideClassDoc | javaClassOrInterface | javaField | javaMethod | javaConstrutor)* BRACE_CLOSE
    ;

// TODO: '...' Operator bei Parametern unterstützen
javaMethod
    : ACCESSMODS? modifier? NAME FUNC_NAME javaParams (SEMI | block)
    ;

block
    : BRACE_OPEN (skipToBrace* block)* skipToBrace* BRACE_CLOSE
    ;

javaConstrutor
    : ACCESSMODS? FUNC_NAME javaParams block
    ;

javaField
    : ACCESSMODS? modifier? NAME NAME skipCodeToSemi
    ;

modifier
    : STATIC
    | FINAL
    | FINAL STATIC
    | STATIC FINAL
    ;

javaParams
    : (NAME NAME (COMMA NAME NAME)*)? PARATHESES_CLOSE
    ;

polymorphy
    : javaExtends javaImplements
    | javaImplements
    | javaExtends
    ;

javaExtends
    : EXTENDS NAME
    ;

javaImplements
    : IMPLEMENTS NAME (COMMA NAME)*
    ;

skipCodeToSemi
    : ~SEMI* SEMI
    ;

skipToBrace
    : ~(BRACE_CLOSE | BRACE_OPEN)
    ;

//TODO: skipCode fuer falsche Token,die zu Fehler fuehren
skipCode
    : .
    ;
