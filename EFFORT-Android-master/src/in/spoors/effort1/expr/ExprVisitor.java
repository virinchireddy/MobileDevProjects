// Generated from /home/susmitha/antlrdata/Expr.g4 by ANTLR 4.2.2
package in.spoors.effort1.expr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExprParser}.
 * 
 * @param <T>
 *            The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ExprVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExprParser#id}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull ExprParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#functionCall}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(@NotNull ExprParser.FunctionCallContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#parenthesizedExpr}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedExpr(@NotNull ExprParser.ParenthesizedExprContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#valueRange}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitValueRange(@NotNull ExprParser.ValueRangeContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#mulDiv}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitMulDiv(@NotNull ExprParser.MulDivContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#addSub}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(@NotNull ExprParser.AddSubContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#valueList}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitValueList(@NotNull ExprParser.ValueListContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#indexedVar}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitIndexedVar(@NotNull ExprParser.IndexedVarContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#floatValue}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitFloatValue(@NotNull ExprParser.FloatValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#intValue}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitIntValue(@NotNull ExprParser.IntValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExprParser#expr_}.
	 * 
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitExpr_(@NotNull ExprParser.Expr_Context ctx);
}