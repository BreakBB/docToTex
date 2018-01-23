lexer grammar JavadocLexer;

ACCESSMODS
    : 'public'
    | 'protected'
    | 'private'
    ;

CLASS
    : 'class'
    ;

STATIC
    : 'static'
    ;

NAME
	: [a-zA-Z]+
	;

NEWLINE
	: '\n' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?
	| '\r\n' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?
	| '\r' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?
	;

SPACE
	: (' '|'\t')+
	;

TEXT_CONTENT
	: ~[\n\r\t @*{}/a-zA-Z]+
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
	: '{'
	;

BRACE_CLOSE
	: '}'
	;

PARATHESES_OPEN
    : '('
    ;

PARATHESES_CLOSE
    : ')'
    ;


// Individual types are possible
// Those might need to include digits as well
RETTYPE
    : NAME
    ;
