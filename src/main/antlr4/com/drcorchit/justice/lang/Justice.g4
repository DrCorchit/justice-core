grammar Justice;

fragment DIGIT : [0-9];
fragment ID_INITIAL : [a-z];
fragment TYPE_INITIAL : [A-Z];
fragment ID_CHAR : [a-zA-Z0-9_];

//Primitive types
NULL : 'null';
BOOL : 'true' | 'false';
INT : DIGIT+;
REAL : DIGIT*'.'DIGIT+ | 'pi' | 'e';
STR : '"'('\\"'|~'"')*'"';

//Keywords
AND : 'and' | '&&';
OR : 'or' | '||';
XOR : 'xor';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
RETURN : 'return';
THROW : 'throw' | 'throws';
VAR : 'val' | 'var';
FUN : 'fun';

//Immutable means that the mechanic never adds or removes elements over its lifespan.
//Grid means that the mechanic's elements are managed via a grid instead of a map.
//Realtime means that the mechanic's data changes constantly in response to streamed data, and not discrete events. It is incompatible with the 'grid' and 'immutable' modifier.
//Ephemeral means that the mechanic's elements are not named, and can be created on the fly. It is incompatible with 'grid' and 'realtime' modifiers.
MECHANIC_TYPE : 'grid' | 'realtime' | 'ephemeral' ;
MECHANIC_MODIFIER : 'immutable';
//Mutable modifies a field, to indicate that the field can be written as well as read
//Derived means that the field is not backed by underlying data.
//Cached means that the member is a derived field whose value should be cached.
MEMBER_MODIFIER : 'mutable' | 'derived' | 'cached' | 'static';

ID : ID_INITIAL(ID_CHAR)*;
TYPE : TYPE_INITIAL(ID_CHAR)*;

//Ignored
WS : [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT : '/*'.*?'*/' -> channel(HIDDEN);
LINE_COMMENT : '//' ~[\r\n]* -> channel(HIDDEN);

mechanic : MECHANIC_TYPE MECHANIC_MODIFIER* 'mechanic' ID '{' member* '}';
//Field means that the member accepts no arguments, and calling it does not mutate the underlying object.
//Func means that the member may accept arguments or mutate the underlying object. It is never backed by data.
member : field | func;
field : MEMBER_MODIFIER* 'field' ID ':' typeExpr ('=' expression)?;
func : MEMBER_MODIFIER* 'func' ID '(' args ')' (':' typeExpr) '{' statement '}';

//Statement
statement : (stmt)+;
stmt : declare | assign | ifBranch | forLoop | whileLoop | error | returnStmt | expressionStmt;
declare : VAR ID '=' expression ';';
assign : lhv '=' expression ';';
lhv : ID #LocalAssign | expression '.' ID #InstanceAssign | expression '[' expression ']' #IndexAssign;
ifBranch : IF '(' expression ')' '{' statement? '}' elseBranch?;
forLoop : FOR '(' ID ':' expression ')' '{' statement? '}';
whileLoop : WHILE '(' expression ')' '{' statement? '}';
error : expression THROW expression ';' | THROW expression ';';
returnStmt : RETURN expression? ';';
expressionStmt : expression ';';
elseBranch : ELSE ifBranch | ELSE '{' statement? '}';

//Expression
expression : expression '^' expression #powerExpr
    | op = ('-' | '!') expression #unaryExpr
    | expression op = ('*' | '/' | '%') expression #multExpr
    | expression op = ('+' | '-') expression #addExpr
    | expression op = ('>' | '>=' | '<' | '<=') expression #compareExpr
    | expression op = ('==' | '!=') expression #equalExpr
    | expression '&&' expression #andExpr
    | expression '||' expression #orExpr
    | constant #constExpr
    | expression '[' expression ']' #indexExpr
    | expression '.' ID tuple? #lookup
    | ID tuple? #lookupEnv
    | TYPE '[' (expression (',' expression)*)? ']' #arrayExpr
    | tuple #tupleExpr
    | args (':' typeExpr)? '->' lambdaBody #lambdaExpr
    | '(' expression ')' #parenExpr;

//Miscellaneous
constant : NULL #nullConst | BOOL #boolConst | INT #intConst | REAL #realConst | STR #strConst;
tuple : '(' (expression (',' expression)*)? ')';
typeExpr : TYPE #baseTypeExpr | typeExpr '[]' #arrayTypeExpr;
//tupleType : '<' typeExpr (',' typeExpr)* '>';
//unionType : '<' typeExpr ('|' typeExpr)+ '>';
//constructor : CONS args? '{' statement? '}';
args : '(' (arg (',' arg)*)? ')';
arg : ID ':' typeExpr;
//genericsExpr : '<' typeExpr (',' typeExpr)* '>';
lambdaBody : expression #expressionLambdaBody | '{' statement? '}' #statementLambdaBody;