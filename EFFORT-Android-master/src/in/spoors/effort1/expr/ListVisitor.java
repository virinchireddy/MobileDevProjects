package in.spoors.effort1.expr;

import in.spoors.effort1.expr.ExprParser.AddSubContext;
import in.spoors.effort1.expr.ExprParser.ExprContext;
import in.spoors.effort1.expr.ExprParser.FloatValueContext;
import in.spoors.effort1.expr.ExprParser.FunctionCallContext;
import in.spoors.effort1.expr.ExprParser.IdContext;
import in.spoors.effort1.expr.ExprParser.IndexedVarContext;
import in.spoors.effort1.expr.ExprParser.IntValueContext;
import in.spoors.effort1.expr.ExprParser.MulDivContext;
import in.spoors.effort1.expr.ExprParser.ParenthesizedExprContext;
import in.spoors.effort1.expr.ExprParser.ValueListContext;
import in.spoors.effort1.expr.ExprParser.ValueRangeContext;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTree;

public class ListVisitor extends ExprBaseVisitor<Set<String>> {

	public boolean isNumber(String str) {
		return Pattern.matches("[0-9]+", str);
	}

	@Override
	public Set<String> visitId(IdContext ctx) {
		String id = ctx.getText();

		Set<String> fields = new HashSet<String>();
		fields.add(id);

		return fields;
	}

	@Override
	public Set<String> visitFunctionCall(FunctionCallContext ctx) {
		ValueListContext values = ctx.valueList();

		Set<String> fields = new HashSet<String>();

		for (int i = 0; i < values.getChildCount(); ++i) {
			ParseTree tree = values.getChild(i);

			if (tree instanceof IndexedVarContext) {
				Set<String> set = visit(tree);
				if (set != null) {
					fields.addAll(set);
				}
			} else if (tree instanceof ExprContext) {
				Set<String> set = visit(tree);
				if (set != null) {
					fields.addAll(set);
				}
				// fields.add(tree.getText());
			} else if (tree instanceof ValueRangeContext) {
				ValueRangeContext range = (ValueRangeContext) tree;

				ExprContext begin = range.expr(0);
				ExprContext end = range.expr(1);

				if (begin instanceof IndexedVarContext
						&& end instanceof IndexedVarContext) {

					IndexedVarContext beginIndexed = (IndexedVarContext) begin;
					IndexedVarContext endIndexed = (IndexedVarContext) end;

					fields.add(beginIndexed.ID().getText());
					fields.add(endIndexed.ID().getText());
				} else {
					if (isNumber(begin.getText()) && isNumber(end.getText())) {

					} else if (!isNumber(begin.getText())
							&& !isNumber(end.getText())) {

						fields.add(begin.getText());
						fields.add(end.getText());
					}
				}
			}
		}

		return fields;
	}

	@Override
	public Set<String> visitParenthesizedExpr(ParenthesizedExprContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public Set<String> visitMulDiv(MulDivContext ctx) {
		Set<String> value1 = visit(ctx.expr(0));
		Set<String> value2 = visit(ctx.expr(1));

		Set<String> fields = new HashSet<String>();
		if (value1 != null) {
			fields.addAll(value1);
		}
		if (value2 != null) {
			fields.addAll(value2);
		}

		return fields;
	}

	@Override
	public Set<String> visitAddSub(AddSubContext ctx) {
		Set<String> value1 = visit(ctx.expr(0));
		Set<String> value2 = visit(ctx.expr(1));

		Set<String> fields = new HashSet<String>();
		if (value1 != null) {
			fields.addAll(value1);
		}
		if (value2 != null) {
			fields.addAll(value2);
		}

		return fields;
	}

	@Override
	public Set<String> visitFloatValue(FloatValueContext ctx) {
		return null;
	}

	@Override
	public Set<String> visitIndexedVar(IndexedVarContext ctx) {
		// Set<String> ctxs = visit(ctx.expr());
		String field = ctx.ID().getText();

		Set<String> fields = new HashSet<String>();
		fields.add(field);

		// if(ctxs != null){
		// fields.addAll(ctxs);
		// }

		return fields;
	}

	@Override
	public Set<String> visitIntValue(IntValueContext ctx) {
		return null;
	}

}
