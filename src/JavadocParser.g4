parser grammar JavadocParser;

options { tokenVocab=JavadocLexer; }


// TODO: package und imports

documentation
	: EOF
	| (javaClassDoc | javaClass) (insideClassDoc | javaClass | javaMethod)* EOF
	;

javaClassDoc
    : javaDoc javaClass
    ;

insideClassDoc
    : javaClassDoc
    | javaDoc javaMethod
    ;

javaDoc
    : JAVADOC_START documentationContent JAVADOC_END
    ;

// TODO: tagSection aufteilen in Class und Methoden für Token
documentationContent
	: description
	| tagSection
	| description tagSection
	;

// TODO: siehe TODO bei descriptionNewLine ausserdem sollten dann die einzelnen Elemente gekuerzt werden
description
//	: descriptionLine (descriptionNewline+ descriptionLine)*
    : descriptionLine+
	;

descriptionLine
	: descriptionLineStart descriptionLineElement*
	| inlineTag descriptionLineElement*
	;

//TODO: Ist das AT richtig
descriptionLineStart
	: descriptionLineNoAt+ (descriptionLineNoAt | AT)*
	;

descriptionLineNoAt
	: TEXT_CONTENT
	| NAME
	| STAR
	| SLASH
/*	| BRACE_OPEN
	| BRACE_CLOSE
*/	;

descriptionLineElement
	: inlineTag
	| descriptionLineText
	;

descriptionLineText
	: (descriptionLineNoAt | AT)+
	;
// TODO fuer Ausgabe irrelevant wie viele Zeilen. Es wird in Tex runtergekuerzt. Oder soll jeder Zeilenumbruch als solcher gewertet werden
//descriptionNewline
//	: NEWLINE
//	;

tagSection
	: blockTag+
	;

blockTag
	: AT blockTagName blockTagContent*
	;

blockTagName
	: NAME
	;

blockTagContent
	: blockTagText
	| inlineTag
	| NEWLINE
	;

blockTagText
	: blockTagTextElement+
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

javaClass
    : ACCESSMODS? STATIC? CLASS NAME polymorphy?
    ;

// TODO: '...' Operator bei Parametern unterstützen
javaMethod
    : ACCESSMODS? staticFinal? NAME NAME PARATHESES_OPEN javaParams? PARATHESES_CLOSE
    ;

staticFinal
    : STATIC
    | FINAL
    | FINAL STATIC
    | STATIC FINAL
    ;

javaParams
    : NAME NAME (COMMA NAME NAME)*
    ;

polymorphy
    : javaExtends
    | javaImplements
    | javaExtends javaImplements
    ;

javaExtends
    : EXTENDS NAME
    ;

javaImplements
    : IMPLEMENTS NAME (COMMA NAME)*
    ;
//TODO: skipCode fuer falsche Token,die zu Fehler fuehren