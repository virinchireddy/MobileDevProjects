package in.spoors.effort1.expr;

import in.spoors.effort1.BuildConfig;
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

import java.util.Map;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import android.util.Log;

public class EvalVisitor extends ExprBaseVisitor<Double> {

	private static final String TAG = "EvalVisitor";
	private Map<String, Double> values;
	private int instanceId;
	private int largestInstanceId;

	public EvalVisitor(Map<String, Double> values, int instanceId) {
		this.values = values;
		this.instanceId = instanceId;

		largestInstanceId = 0;

		if (values == null) {
			return;
		}

		for (String key : values.keySet()) {
			if (key == null) {
				continue;
			}

			int start = key.indexOf('[');
			int end = key.indexOf(']');

			if (start != -1 && end != -1) {
				int index = Integer.parseInt(key.substring(start + 1, end));

				if (index > largestInstanceId) {
					largestInstanceId = index;
				}
			}
		}
	}

	@Override
	public Double visitId(IdContext ctx) {
		String id = ctx.getText();

		if (values.containsKey(id)) {
			return values.get(id);
		} else if (values.containsKey(id + "[" + instanceId + "]")) {
			return values.get(id + "[" + instanceId + "]");
		} else {
			return null;
		}
	}

	public boolean isNumber(String str) {
		return Pattern.matches("[0-9]+", str);
	}

	public Double sumOfSectionFields(String sectionField) {
		double sum = 0;

		for (int i = 0; i <= largestInstanceId; ++i) {
			Double value = this.values.get(sectionField + "[" + i + "]");
			sum += (value != null) ? value : 0d;
		}

		return sum;
	}

	public Double productOfSectionFields(String sectionField) {
		double product = 1;

		for (int i = 0; i < largestInstanceId; ++i) {
			Double value = this.values.get(sectionField + "[" + i + "]");
			product *= (value != null) ? value : 1d;
		}

		return product;
	}

	@Override
	public Double visitFunctionCall(FunctionCallContext ctx) {
		String fname = ctx.ID().getText();
		// System.out.println("function: " + fname);

		ValueListContext values = ctx.valueList();

		double result;

		if ("sum".equalsIgnoreCase(fname)) {
			result = 0;
		} else {
			result = 1;
		}

		for (int i = 0; i < values.getChildCount(); ++i) {
			ParseTree tree = values.getChild(i);
			// System.out.println(i + ":" + tree);

			if (tree instanceof IndexedVarContext) {
				Double value = visit(tree);
				if (value != null) {
					if ("sum".equalsIgnoreCase(fname)) {
						result += value;
					} else {
						result *= value;
					}
				}
			} else if (tree instanceof ExprContext) {
				boolean isSectionId = false;
				for (int j = 0; j < tree.getChildCount(); j++) {
					ParseTree childTree = tree.getChild(j);
					if (childTree instanceof IdContext
							|| childTree instanceof TerminalNode) {
						if (childTree.getText().startsWith("S")) {
							isSectionId = true;
							break;
						}
					}
				}

				if (isSectionId) {
					for (int j = 0; j <= largestInstanceId; j++) {
						EvalVisitor evalVisitor = new EvalVisitor(this.values,
								j);
						Double value = evalVisitor.visit(tree);
						if (value != null) {
							if ("sum".equalsIgnoreCase(fname)) {
								result += value;
							} else {
								result *= value;
							}
						}
					}
				} else {
					Double value = visit(tree);
					if (value != null) {
						if ("sum".equalsIgnoreCase(fname)) {
							result += value;
						} else {
							result *= value;
						}
					}
				}
			} else if (tree instanceof ValueRangeContext) {
				ValueRangeContext range = (ValueRangeContext) tree;

				ExprContext begin = range.expr(0);
				ExprContext end = range.expr(1);

				if (begin instanceof IndexedVarContext
						&& end instanceof IndexedVarContext) {
					IndexedVarContext beginIndexed = (IndexedVarContext) begin;
					int beginIndex = visit(beginIndexed.expr()).intValue();

					IndexedVarContext endIndexed = (IndexedVarContext) end;
					int endIndex = visit(endIndexed.expr()).intValue();

					for (int index = beginIndex; index <= endIndex; ++index) {
						Double value = this.values.get(beginIndexed.ID()
								.getText() + "[" + index + "]");

						if ("sum".equalsIgnoreCase(fname)) {
							result += value != null ? value : 0d;
						} else {
							result *= value != null ? value : 1d;
						}
					}
				} else {
					if (isNumber(begin.getText()) && isNumber(end.getText())) {
						int beginValue = Integer.parseInt(begin.getText());
						int endValue = Integer.parseInt(end.getText());

						for (int value = beginValue; value <= endValue; ++value) {
							if ("sum".equalsIgnoreCase(fname)) {
								result += value;
							} else {
								result *= value;
							}
						}
					} else if (!isNumber(begin.getText())
							&& !isNumber(end.getText())) {
						String[] begins = begin.getText().split("F");
						String[] ends = end.getText().split("F");

						int beginIndex = Integer.parseInt(begins[1]);
						int endIndex = Integer.parseInt(ends[1]);

						for (int index = beginIndex; index <= endIndex; ++index) {
							if (begins[0].length() > 0) {
								if ("sum".equalsIgnoreCase(fname)) {
									result += sumOfSectionFields(begins[0]
											+ "F" + index);
								} else {
									result *= sumOfSectionFields(begins[0]
											+ "F" + index);
								}
							} else {
								if ("sum".equalsIgnoreCase(fname)) {
									result += this.values.get("F" + index);
								} else {
									result *= this.values.get("F" + index);
								}
							}
						}
					}
				}
			}
		}

		return result;
	}

	@Override
	public Double visitParenthesizedExpr(ParenthesizedExprContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public Double visitMulDiv(MulDivContext ctx) {
		Double value1 = visit(ctx.expr(0));
		Double value2 = visit(ctx.expr(1));

		if (value1 == null || value2 == null) {
			return null;
		}

		if (ctx.STAR() != null) {
			return value1 * value2;
		} else {
			if (value2 == 0) {
				return null;
			} else {
				return value1 / value2;
			}
		}
	}

	@Override
	public Double visitAddSub(AddSubContext ctx) {
		Double value1 = visit(ctx.expr(0));
		Double value2 = visit(ctx.expr(1));

		if (ctx.PLUS() != null) {
			return ((value1 != null) ? value1 : 0d)
					+ ((value2 != null) ? value2 : 0d);
		} else {
			return ((value1 != null) ? value1 : 0d)
					- ((value2 != null) ? value2 : 0d);
		}
	}

	@Override
	public Double visitFloatValue(FloatValueContext ctx) {
		Double value = null;

		try {
			value = Double.parseDouble(ctx.FLOAT().getText());
		} catch (NumberFormatException e) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Failed to parse " + ctx.FLOAT().getText()
						+ " as a double value.");
			}
		}

		return value;
	}

	@Override
	public Double visitIndexedVar(IndexedVarContext ctx) {
		Double index = visit(ctx.expr());
		String field = ctx.ID().getText() + "[" + index.intValue() + "]";
		// System.out.println("field: " + field);
		return values.get(field);
	}

	@Override
	public Double visitIntValue(IntValueContext ctx) {
		Double value = null;

		try {
			value = Double.parseDouble(ctx.INT().getText());
		} catch (NumberFormatException e) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Failed to parse " + ctx.INT().getText()
						+ " as a double value.");
			}
		}

		return value;
	}

}
