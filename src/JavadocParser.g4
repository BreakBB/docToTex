parser grammar JavadocParser;

options { tokenVocab=JavadocLexer; }


documentation
	: EOF
	| JAVADOC_START skipWhitespace* documentationContent JAVADOC_END skipWhitespace* javaClass EOF
	| skipWhitespace* documentationContent EOF
	;

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
    : ACCESSMODS? skipWhitespace* STATIC? skipWhitespace* CLASS skipWhitespace* NAME skipCode* documentation?
    ;

javaMethod
    : ACCESSMODS? skipWhitespace* STATIC? skipWhitespace* RETTYPE skipWhitespace* NAME skipWhitespace* PARATHESES_OPEN javaParams PARATHESES_CLOSE skipCode+ documentation?
    ;

javaParams
    : skipWhitespace* NAME skipWhitespace* NAME
    ;

skipCode
    : skipWhitespace
    ;