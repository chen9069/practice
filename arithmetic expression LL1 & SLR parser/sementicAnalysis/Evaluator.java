package sementicAnalysis;

import abstractSyntaxTrees.AST;
import abstractSyntaxTrees.BinaryExpr;
import abstractSyntaxTrees.Constant;
import abstractSyntaxTrees.Expression;
import abstractSyntaxTrees.UnaryExpr;
import abstractSyntaxTrees.Visitor;

public class Evaluator implements Visitor<Void, Integer> {
	private static Evaluator instance = new Evaluator();
	private Evaluator(){}
	public static Evaluator getInstance() {
		return instance;
	}
	@Override
	public Integer visit(AST ast) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(BinaryExpr expr) {
		// TODO Auto-generated method stub
		int l = expr.left.accept(this);
		int r = expr.right.accept(this);
		switch (expr.op.type) {
		case PLUS:
			return l + r;
		case MINUS:
			return l - r;
		case MULTIPLY:
			return l * r;
		case DIVIDE:
			return l / r;
		default:
			return null;
		}
	}

	@Override
	public Integer visit(UnaryExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Constant expr) {
		// TODO Auto-generated method stub
		return Integer.parseInt(expr.token.getValue());
	}

	@Override
	public Integer visit(AST ast, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Expression expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(BinaryExpr expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(UnaryExpr expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Constant expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}
}
