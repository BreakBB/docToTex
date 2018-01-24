lexer grammar JavadocLexer;

ACCESSMODS
    : 'public'
    | 'protected'
    | 'private'
    ;

CLASS
    : 'class'
    ;

INTERFACE
    : 'interface'
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

SEMI
    : ';'
    ;

FUNC_NAME
    :[a-zA-Z]+ (SPACE | NEWLINE)* PARATHESES_OPEN
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


AUTHOR
    : AT 'author'
    ;

DATE
    : AT 'date'
    ;

DEPRECATED
    : AT 'deprecated'
    ;

SEE
    : AT 'see'
    ;

SERIAL_DATA
    : AT 'serialData'
    ;

SERIAL_FIELD
    : AT 'serialField'
    ;

SERIAL
    : AT 'serial'
    ;

SINCE
    : AT 'since'
    ;

VERSION
    : AT 'version'
    ;

PARAM
    : AT 'param'
    ;

RETURN
    : AT 'return'
    ;

THROWS
    : AT 'throws'
    ;

EXCEPTION
    : AT 'exception'
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
	: SPACE* STAR* '*/'
	;

COMMENT
    : '//'+ ~[\n\r]* NEWLINE ->skip
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
