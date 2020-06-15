// Generated from /home/susmitha/antlrdata/Expr.g4 by ANTLR 4.2.2
package in.spoors.effort1.expr;

import java.util.List;

import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({ "all", "warnings", "unchecked", "unused", "cast" })
public class ExprParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
	public static final int LP = 1, RP = 2, LSB = 3, RSB = 4, PLUS = 5,
			MINUS = 6, STAR = 7, SLASH = 8, COLON = 9, COMMA = 10, INT = 11,
			ID = 12, WS = 13, FLOAT = 14;
	public static final String[] tokenNames = { "<INVALID>", "'('", "')'",
			"'['", "']'", "'+'", "'-'", "'*'", "'/'", "':'", "','", "INT",
			"ID", "WS", "FLOAT" };
	public static final int RULE_expr_ = 0, RULE_expr = 1, RULE_valueRange = 2,
			RULE_valueList = 3;
	public static final String[] ruleNames = { "expr_", "expr", "valueRange",
			"valueList" };

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
	public ATN getATN() {
		return _ATN;
	}

	public ExprParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA,
				_sharedContextCache);
	}

	public static class Expr_Context extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class, 0);
		}

		public Expr_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_expr_;
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor).visitExpr_(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public final Expr_Context expr_() throws RecognitionException {
		Expr_Context _localctx = new Expr_Context(_ctx, getState());
		enterRule(_localctx, 0, RULE_expr_);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(8);
				expr(0);
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_expr;
		}

		public ExprContext() {
		}

		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}

	public static class IdContext extends ExprContext {
		public TerminalNode ID() {
			return getToken(ExprParser.ID, 0);
		}

		public IdContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor).visitId(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class FunctionCallContext extends ExprContext {
		public TerminalNode LP() {
			return getToken(ExprParser.LP, 0);
		}

		public TerminalNode ID() {
			return getToken(ExprParser.ID, 0);
		}

		public TerminalNode RP() {
			return getToken(ExprParser.RP, 0);
		}

		public ValueListContext valueList() {
			return getRuleContext(ValueListContext.class, 0);
		}

		public FunctionCallContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor)
						.visitFunctionCall(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class ParenthesizedExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class, 0);
		}

		public TerminalNode LP() {
			return getToken(ExprParser.LP, 0);
		}

		public TerminalNode RP() {
			return getToken(ExprParser.RP, 0);
		}

		public ParenthesizedExprContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor)
						.visitParenthesizedExpr(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class MulDivContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}

		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class, i);
		}

		public TerminalNode STAR() {
			return getToken(ExprParser.STAR, 0);
		}

		public TerminalNode SLASH() {
			return getToken(ExprParser.SLASH, 0);
		}

		public MulDivContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor).visitMulDiv(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class AddSubContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}

		public TerminalNode MINUS() {
			return getToken(ExprParser.MINUS, 0);
		}

		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class, i);
		}

		public TerminalNode PLUS() {
			return getToken(ExprParser.PLUS, 0);
		}

		public AddSubContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor).visitAddSub(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class FloatValueContext extends ExprContext {
		public TerminalNode FLOAT() {
			return getToken(ExprParser.FLOAT, 0);
		}

		public FloatValueContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor)
						.visitFloatValue(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class IndexedVarContext extends ExprContext {
		public TerminalNode RSB() {
			return getToken(ExprParser.RSB, 0);
		}

		public ExprContext expr() {
			return getRuleContext(ExprContext.class, 0);
		}

		public TerminalNode ID() {
			return getToken(ExprParser.ID, 0);
		}

		public TerminalNode LSB() {
			return getToken(ExprParser.LSB, 0);
		}

		public IndexedVarContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor)
						.visitIndexedVar(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public static class IntValueContext extends ExprContext {
		public TerminalNode INT() {
			return getToken(ExprParser.INT, 0);
		}

		public IntValueContext(ExprContext ctx) {
			copyFrom(ctx);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor).visitIntValue(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
				setState(28);
				switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
				case 1: {
					_localctx = new FunctionCallContext(_localctx);
					_ctx = _localctx;
					_prevctx = _localctx;

					setState(11);
					match(ID);
					setState(12);
					match(LP);
					setState(13);
					valueList();
					setState(14);
					match(RP);
				}
					break;

				case 2: {
					_localctx = new ParenthesizedExprContext(_localctx);
					_ctx = _localctx;
					_prevctx = _localctx;
					setState(16);
					match(LP);
					setState(17);
					expr(0);
					setState(18);
					match(RP);
				}
					break;

				case 3: {
					_localctx = new IndexedVarContext(_localctx);
					_ctx = _localctx;
					_prevctx = _localctx;
					setState(20);
					match(ID);
					setState(21);
					match(LSB);
					setState(22);
					expr(0);
					setState(23);
					match(RSB);
				}
					break;

				case 4: {
					_localctx = new IdContext(_localctx);
					_ctx = _localctx;
					_prevctx = _localctx;
					setState(25);
					match(ID);
				}
					break;

				case 5: {
					_localctx = new FloatValueContext(_localctx);
					_ctx = _localctx;
					_prevctx = _localctx;
					setState(26);
					match(FLOAT);
				}
					break;

				case 6: {
					_localctx = new IntValueContext(_localctx);
					_ctx = _localctx;
					_prevctx = _localctx;
					setState(27);
					match(INT);
				}
					break;
				}
				_ctx.stop = _input.LT(-1);
				setState(38);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
				while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
					if (_alt == 1) {
						if (_parseListeners != null)
							triggerExitRuleEvent();
						_prevctx = _localctx;
						{
							setState(36);
							switch (getInterpreter().adaptivePredict(_input, 1,
									_ctx)) {
							case 1: {
								_localctx = new MulDivContext(new ExprContext(
										_parentctx, _parentState));
								pushNewRecursionContext(_localctx, _startState,
										RULE_expr);
								setState(30);
								if (!(precpred(_ctx, 6)))
									throw new FailedPredicateException(this,
											"precpred(_ctx, 6)");
								setState(31);
								_la = _input.LA(1);
								if (!(_la == STAR || _la == SLASH)) {
									_errHandler.recoverInline(this);
								}
								consume();
								setState(32);
								expr(7);
							}
								break;

							case 2: {
								_localctx = new AddSubContext(new ExprContext(
										_parentctx, _parentState));
								pushNewRecursionContext(_localctx, _startState,
										RULE_expr);
								setState(33);
								if (!(precpred(_ctx, 5)))
									throw new FailedPredicateException(this,
											"precpred(_ctx, 5)");
								setState(34);
								_la = _input.LA(1);
								if (!(_la == PLUS || _la == MINUS)) {
									_errHandler.recoverInline(this);
								}
								consume();
								setState(35);
								expr(6);
							}
								break;
							}
						}
					}
					setState(40);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
				}
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ValueRangeContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}

		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class, i);
		}

		public TerminalNode COLON() {
			return getToken(ExprParser.COLON, 0);
		}

		public ValueRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_valueRange;
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor)
						.visitValueRange(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public final ValueRangeContext valueRange() throws RecognitionException {
		ValueRangeContext _localctx = new ValueRangeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_valueRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(41);
				expr(0);
				setState(42);
				match(COLON);
				setState(43);
				expr(0);
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}

		public List<TerminalNode> COMMA() {
			return getTokens(ExprParser.COMMA);
		}

		public ValueRangeContext valueRange(int i) {
			return getRuleContext(ValueRangeContext.class, i);
		}

		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class, i);
		}

		public List<ValueRangeContext> valueRange() {
			return getRuleContexts(ValueRangeContext.class);
		}

		public TerminalNode COMMA(int i) {
			return getToken(ExprParser.COMMA, i);
		}

		public ValueListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_valueList;
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof ExprVisitor)
				return ((ExprVisitor<? extends T>) visitor)
						.visitValueList(this);
			else
				return visitor.visitChildren(this);
		}
	}

	public final ValueListContext valueList() throws RecognitionException {
		ValueListContext _localctx = new ValueListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_valueList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(47);
				switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
				case 1: {
					setState(45);
					expr(0);
				}
					break;

				case 2: {
					setState(46);
					valueRange();
				}
					break;
				}
				setState(56);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == COMMA) {
					{
						{
							setState(49);
							match(COMMA);
							setState(52);
							switch (getInterpreter().adaptivePredict(_input, 4,
									_ctx)) {
							case 1: {
								setState(50);
								expr(0);
							}
								break;

							case 2: {
								setState(51);
								valueRange();
							}
								break;
							}
						}
					}
					setState(58);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expr_sempred((ExprContext) _localctx, predIndex);
		}
		return true;
	}

	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);

		case 1:
			return precpred(_ctx, 5);
		}
		return true;
	}

	public static final String _serializedATN = "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\20>\4\2\t\2\4\3\t"
			+ "\3\4\4\t\4\4\5\t\5\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"
			+ "\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\37\n\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3\'"
			+ "\n\3\f\3\16\3*\13\3\3\4\3\4\3\4\3\4\3\5\3\5\5\5\62\n\5\3\5\3\5\3\5\5\5"
			+ "\67\n\5\7\59\n\5\f\5\16\5<\13\5\3\5\2\3\4\6\2\4\6\b\2\4\3\2\t\n\3\2\7"
			+ "\bC\2\n\3\2\2\2\4\36\3\2\2\2\6+\3\2\2\2\b\61\3\2\2\2\n\13\5\4\3\2\13\3"
			+ "\3\2\2\2\f\r\b\3\1\2\r\16\7\16\2\2\16\17\7\3\2\2\17\20\5\b\5\2\20\21\7"
			+ "\4\2\2\21\37\3\2\2\2\22\23\7\3\2\2\23\24\5\4\3\2\24\25\7\4\2\2\25\37\3"
			+ "\2\2\2\26\27\7\16\2\2\27\30\7\5\2\2\30\31\5\4\3\2\31\32\7\6\2\2\32\37"
			+ "\3\2\2\2\33\37\7\16\2\2\34\37\7\20\2\2\35\37\7\r\2\2\36\f\3\2\2\2\36\22"
			+ "\3\2\2\2\36\26\3\2\2\2\36\33\3\2\2\2\36\34\3\2\2\2\36\35\3\2\2\2\37(\3"
			+ "\2\2\2 !\f\b\2\2!\"\t\2\2\2\"\'\5\4\3\t#$\f\7\2\2$%\t\3\2\2%\'\5\4\3\b"
			+ "& \3\2\2\2&#\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2\2)\5\3\2\2\2*(\3\2\2"
			+ "\2+,\5\4\3\2,-\7\13\2\2-.\5\4\3\2.\7\3\2\2\2/\62\5\4\3\2\60\62\5\6\4\2"
			+ "\61/\3\2\2\2\61\60\3\2\2\2\62:\3\2\2\2\63\66\7\f\2\2\64\67\5\4\3\2\65"
			+ "\67\5\6\4\2\66\64\3\2\2\2\66\65\3\2\2\2\679\3\2\2\28\63\3\2\2\29<\3\2"
			+ "\2\2:8\3\2\2\2:;\3\2\2\2;\t\3\2\2\2<:\3\2\2\2\b\36&(\61\66:";
	public static final ATN _ATN = new ATNDeserializer()
			.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}