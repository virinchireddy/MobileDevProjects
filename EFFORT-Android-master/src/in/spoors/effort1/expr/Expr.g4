grammar Expr;

// Examples:
// f1: Customer
// f2: Order date
// s1f1: Item
// s1f2: Qty
// s1f3: Unit Price
// s1f4: Price,  expr: s1f2 * s1f3
// f3: Discount
// f4: Total, expr: sum(s1f4) - f3

expr_ : expr;

expr : ID LP valueList RP     # functionCall
     | LP expr RP             # parenthesizedExpr
     | expr (STAR|SLASH) expr # mulDiv
     | expr (PLUS|MINUS) expr # addSub
     | ID LSB expr RSB         # indexedVar
     | ID                     # id
     | FLOAT                  # floatValue
     | INT                    # intValue
     ;

valueRange: expr COLON expr;
valueList : (expr|valueRange) (COMMA (expr|valueRange))*;

LP    : '(';
RP    : ')';
LSB   : '[';
RSB   : ']';
PLUS  : '+';
MINUS : '-';
STAR  : '*';
SLASH : '/';
COLON : ':';
COMMA : ',';
INT   : DIGIT+;
ID    : [a-zA-Z]+[a-zA-Z0-9_]*;
WS    : [ \t\r\n]+ -> skip;
FLOAT : DIGIT+ '.' DIGIT* // match 1. 39. 3.14159 etc...
      | '.' DIGIT+ // match .1 .14159
      ;
fragment
DIGIT : [0-9];
