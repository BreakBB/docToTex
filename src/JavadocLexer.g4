lexer grammar JavadocLexer;

ACCESSMODS
    : 'public'
    | 'protected'
    | 'private'
    ;

CLASS
    : 'class'
    ;

FINAL
    : 'final'
    ;

STATIC
    : 'static'
    ;

EXTENDS
    : 'extends'
    ;

IMPLEMENTS
    : 'implements'
    ;

COMMA
    : ','
    ;

NAME
	: [a-zA-Z]+
	;

NEWLINE
	: (('\n' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?)
	| ('\r\n' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?)
	| ('\r' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?)) -> skip
	;

SPACE
	: (' '|'\t')+ -> skip
	;

TEXT_CONTENT
	: ~[\n\r\t @*{}()/a-zA-Z]+
	;

AT
	: '@'
	;

STAR
	: '*'
	;

SLASH
	: '/'
	;

JAVADOC_START
	: '/**' STAR*
	;

JAVADOC_END
	: SPACE? STAR* '*/'
	;

INLINE_TAG_START
	: '{@'
	;

BRACE_OPEN
	: '{' ->skip
	;

BRACE_CLOSE
	: '}' ->skip
	;

PARATHESES_OPEN
    : '('
    ;

PARATHESES_CLOSE
    : ')'
    ;
