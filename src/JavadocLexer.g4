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

ABSTRACT
    : 'abstract'
    ;

EXTENDS
    : 'extends'
    ;

IMPLEMENTS
    : 'implements'
    ;

THROWS
    : 'throws'
    ;

PACKAGE
    : 'package'
    ;

IMPORT
    : 'import' SPACE+ NAME (DOT NAME)* SEMI ->skip
    ;

COMMA
    : ','
    ;

SEMI
    : ';'
    ;

AT
	: '@'
	;

HASHTAG
    : '#'
    ;

VARARGS
    : '...'
    ;

DOT
    : '.'
    ;

QUOTE
    : '"'
    ;

SINGLE_QUOTE
    : '\''
    ;

FUNC_NAME
    : NAME (SPACE | NEWLINE)* PARATHESES_OPEN
    ;

SEE_REF
    : TYPE_NAME HASHTAG NAME
    ;

TYPE_NAME
    : NAME DOT (NAME | DOT)+
    ;

NAME
	: [a-zA-Z0-9]+
	;

NEWLINE
	: (('\n' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?)
	| ('\r\n' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?)
	| ('\r' (SPACE? (STAR {_input.LA(1) != '/'}?)+)?)) -> skip
	;

SPACE
	: (' '|'\t')+ -> skip
	;

AUTHOR
    : NEWLINE SPACE* AT 'author'
    ;

DATE
    : NEWLINE SPACE* AT 'date'
    ;

DEPRECATED
    : NEWLINE SPACE* AT 'deprecated'
    ;

SEE
    : NEWLINE SPACE* AT 'see'
    ;

SERIAL_DATA
    : NEWLINE SPACE* AT 'serialData'
    ;

SERIAL_FIELD
    : NEWLINE SPACE* AT 'serialField'
    ;

SERIAL
    : NEWLINE SPACE* AT 'serial'
    ;

SINCE
    : NEWLINE SPACE* AT 'since'
    ;

VERSION
    : NEWLINE SPACE* AT 'version'
    ;

PARAM
    : NEWLINE SPACE* AT 'param'
    ;

RETURN
    : NEWLINE SPACE* AT 'return'
    ;

EXCEPTION
    : NEWLINE SPACE* AT ('throws' | 'exception')
    ;

INLINE_CODE
    : BRACE_OPEN AT 'code'
    ;

INLINE_DOC_ROOT
    : BRACE_OPEN AT 'docRoot'
    ;

INLINE_INHERIT_DOC
    : BRACE_OPEN AT 'inheritDoc'
    ;

INLINE_LINK_PLAIN
    : BRACE_OPEN AT 'linkplain'
    ;

INLINE_LINK
    : BRACE_OPEN AT 'link'
    ;

INLINE_VALUE
    : BRACE_OPEN AT 'value'
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

BLOCK_COMMENT
    : '/*' {_input.LA(1) != '*'}? .*? '*/' ->skip
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

BRACKET_OPEN
    : '['
    ;

BRACKET_CLOSE
    : ']'
    ;

ANGLE_BRACKET_OPEN
    : '<'
    ;

ANGLE_BRACKET_CLOSE
    : '>'
    ;

TEXT_CONTENT
	: ~[\n\r\t @*{}[\]()<>",/a-zA-Z]+
	;