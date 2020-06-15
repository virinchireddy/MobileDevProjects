// Generated from /home/susmitha/antlrdata/Expr.g4 by ANTLR 4.2.2
package in.spoors.effort1.expr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({ "all", "warnings", "unchecked", "unused", "cast" })
public class ExprLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
	public static final int LP = 1, RP = 2, LSB = 3, RSB = 4, PLUS = 5,
			MINUS = 6, STAR = 7, SLASH = 8, COLON = 9, COMMA = 10, INT = 11,
			ID = 12, WS = 13, FLOAT = 14;
	public static String[] modeNames = { "DEFAULT_MODE" };

	public static final String[] tokenNames = { "<INVALID>", "'('", "')'",
			"'['", "']'", "'+'", "'-'", "'*'", "'/'", "':'", "','", "INT",
			"ID", "WS", "FLOAT" };
	public static final String[] ruleNames = { "LP", "RP", "LSB", "RSB",
			"PLUS", "MINUS", "STAR", "SLASH", "COLON", "COMMA", "INT", "ID",
			"WS", "FLOAT", "DIGIT" };

	public ExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA,
				_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() {
		return "Expr.g4";
	}

	@Override
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override
	public String[] getRuleNames() {
		return ruleNames;
	}

	@Override
	public String getSerializedATN() {
		return _serializedATN;
	}

	@Override
	public String[] getModeNames() {
		return modeNames;
	}

	@Override
	public ATN getATN() {
		return _ATN;
	}

	public static final String _serializedATN = "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\20b\b\1\4\2\t\2\4"
			+ "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"
			+ "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\3\3\3\3\4"
			+ "\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\6\f"
			+ "\67\n\f\r\f\16\f8\3\r\6\r<\n\r\r\r\16\r=\3\r\7\rA\n\r\f\r\16\rD\13\r\3"
			+ "\16\6\16G\n\16\r\16\16\16H\3\16\3\16\3\17\6\17N\n\17\r\17\16\17O\3\17"
			+ "\3\17\7\17T\n\17\f\17\16\17W\13\17\3\17\3\17\6\17[\n\17\r\17\16\17\\\5"
			+ "\17_\n\17\3\20\3\20\2\2\21\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"
			+ "\f\27\r\31\16\33\17\35\20\37\2\3\2\6\4\2C\\c|\6\2\62;C\\aac|\5\2\13\f"
			+ "\17\17\"\"\3\2\62;h\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"
			+ "\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"
			+ "\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3!\3\2\2\2"
			+ "\5#\3\2\2\2\7%\3\2\2\2\t\'\3\2\2\2\13)\3\2\2\2\r+\3\2\2\2\17-\3\2\2\2"
			+ "\21/\3\2\2\2\23\61\3\2\2\2\25\63\3\2\2\2\27\66\3\2\2\2\31;\3\2\2\2\33"
			+ "F\3\2\2\2\35^\3\2\2\2\37`\3\2\2\2!\"\7*\2\2\"\4\3\2\2\2#$\7+\2\2$\6\3"
			+ "\2\2\2%&\7]\2\2&\b\3\2\2\2\'(\7_\2\2(\n\3\2\2\2)*\7-\2\2*\f\3\2\2\2+,"
			+ "\7/\2\2,\16\3\2\2\2-.\7,\2\2.\20\3\2\2\2/\60\7\61\2\2\60\22\3\2\2\2\61"
			+ "\62\7<\2\2\62\24\3\2\2\2\63\64\7.\2\2\64\26\3\2\2\2\65\67\5\37\20\2\66"
			+ "\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29\30\3\2\2\2:<\t\2\2\2;:"
			+ "\3\2\2\2<=\3\2\2\2=;\3\2\2\2=>\3\2\2\2>B\3\2\2\2?A\t\3\2\2@?\3\2\2\2A"
			+ "D\3\2\2\2B@\3\2\2\2BC\3\2\2\2C\32\3\2\2\2DB\3\2\2\2EG\t\4\2\2FE\3\2\2"
			+ "\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\b\16\2\2K\34\3\2\2\2LN\5"
			+ "\37\20\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2PQ\3\2\2\2QU\7\60\2\2"
			+ "RT\5\37\20\2SR\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2V_\3\2\2\2WU\3\2\2"
			+ "\2XZ\7\60\2\2Y[\5\37\20\2ZY\3\2\2\2[\\\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]"
			+ "_\3\2\2\2^M\3\2\2\2^X\3\2\2\2_\36\3\2\2\2`a\t\5\2\2a \3\2\2\2\13\28=B"
			+ "HOU\\^\3\b\2\2";
	public static final ATN _ATN = new ATNDeserializer()
			.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}