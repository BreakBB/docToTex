parser grammar JavadocParser;

options { tokenVocab=JavadocLexer; }


// TODO: package und imports

documentation
	: EOF
	| skipWhitespace* (javaClassDoc | javaClass) skipCode* ((insideClassDoc | javaClass | javaMethod) skipCode*)* skipCode* EOF
	;

// TODO: skipWhitespace verringern

javaClassDoc
    : javaDoc javaClass
    ;

insideClassDoc
    : javaClassDoc
    | javaDoc javaMethod
    ;

javaDoc
    : JAVADOC_START skipWhitespace* documentationContent JAVADOC_END skipWhitespace*
    ;

// TODO: tagSection aufteilen in Class und Methoden für Token
documentationContent
	: description skipWhitespace*
	| skipWhitespace* tagSection
	| description NEWLINE+ skipWhitespace* tagSection
	;

skipWhitespace
	: SPACE
	| NEWLINE
	;

description
	: descriptionLine (descriptionNewline+ descriptionLine)*
	;

descriptionLine
	: descriptionLineStart descriptionLineElement*
	| inlineTag descriptionLineElement*
	;

descriptionLineStart
	: SPACE? descriptionLineNoSpaceNoAt+ (descriptionLineNoSpaceNoAt | SPACE | AT)*
	;

descriptionLineNoSpaceNoAt
	: TEXT_CONTENT
	| NAME
	| STAR
	| SLASH
	| BRACE_OPEN
	| BRACE_CLOSE
	;

descriptionLineElement
	: inlineTag
	| descriptionLineText
	;

descriptionLineText
	: (descriptionLineNoSpaceNoAt | SPACE | AT)+
	;

descriptionNewline
	: NEWLINE
	;

tagSection
	: blockTag+
	;

blockTag
	: SPACE? AT blockTagName SPACE? blockTagContent*
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
	| SPACE
	| STAR
	| SLASH
	| BRACE_OPEN
	| BRACE_CLOSE
	;

inlineTag
	: INLINE_TAG_START inlineTagName SPACE* inlineTagContent? BRACE_CLOSE
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
    : ACCESSMODS? skipWhitespace* STATIC? skipWhitespace* CLASS skipWhitespace* NAME skipWhitespace* polymorphy?
    ;

// TODO: '...' Operator bei Parametern unterstützen
javaMethod
    : ACCESSMODS? skipWhitespace* staticFinal? skipWhitespace* NAME skipWhitespace* NAME skipWhitespace* PARATHESES_OPEN javaParams? PARATHESES_CLOSE
    ;

staticFinal
    : STATIC
    | FINAL
    | FINAL skipWhitespace* STATIC
    | STATIC skipWhitespace* FINAL
    ;

javaParams
    : skipWhitespace* NAME skipWhitespace* NAME (COMMA skipWhitespace* NAME skipWhitespace* NAME)*
    ;

polymorphy
    : javaExtends
    | javaImplements
    | javaExtends skipWhitespace* javaImplements
    ;

javaExtends
    : EXTENDS skipWhitespace* NAME
    ;

javaImplements
    : IMPLEMENTS skipWhitespace* NAME (skipWhitespace* COMMA skipWhitespace* NAME)*
    ;

// TODO: Alles außer javaMethod und javaClass und JAVADOC_START überspringen
skipCode
    : skipWhitespace
    ;