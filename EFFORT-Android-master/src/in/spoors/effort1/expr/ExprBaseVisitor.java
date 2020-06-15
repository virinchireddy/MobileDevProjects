// Generated from /home/susmitha/antlrdata/Expr.g4 by ANTLR 4.2.2
package in.spoors.effort1.expr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link ExprVisitor}, which can
 * be extended to create a visitor which only needs to handle a subset of the
 * available methods.
 * 
 * @param <T>
 *            The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class ExprBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements
		ExprVisitor<T> {
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitId(@NotNull ExprParser.IdContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitFunctionCall(@NotNull ExprParser.FunctionCallContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitParenthesizedExpr(
			@NotNull ExprParser.ParenthesizedExprContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitValueRange(@NotNull ExprParser.ValueRangeContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitMulDiv(@NotNull ExprParser.MulDivContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitAddSub(@NotNull ExprParser.AddSubContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitValueList(@NotNull ExprParser.ValueListContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitIndexedVar(@NotNull ExprParser.IndexedVarContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitFloatValue(@NotNull ExprParser.FloatValueContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitIntValue(@NotNull ExprParser.IntValueContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitExpr_(@NotNull ExprParser.Expr_Context ctx) {
		return visitChildren(ctx);
	}
}