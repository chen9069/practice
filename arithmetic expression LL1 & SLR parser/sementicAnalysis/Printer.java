package sementicAnalysis;

import abstractSyntaxTrees.AST;
import abstractSyntaxTrees.BinaryExpr;
import abstractSyntaxTrees.Constant;
import abstractSyntaxTrees.Expression;
import abstractSyntaxTrees.UnaryExpr;
import abstractSyntaxTrees.Visitor;

public class Printer implements Visitor<Void, Void> {
	private static Printer instance = new Printer();
	private Printer(){}
	public static Printer getInstance() {
		return instance;
	}
	public Void visit(AST ast) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(BinaryExpr expr) {
		// TODO Auto-generated method stub
		System.out.print("(");
		expr.left.accept(this);
		System.out.print(" " + expr.op + " ");
		expr.right.accept(this);
		System.out.print(")");
		return null;
	}

	@Override
	public Void visit(UnaryExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(Constant expr) {
		// TODO Auto-generated method stub
		System.out.print(expr.token.getValue());
		return null;
	}

	@Override
	public Void visit(AST ast, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(Expression expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(BinaryExpr expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(UnaryExpr expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(Constant expr, Void... args) {
		// TODO Auto-generated method stub
		return null;
	}
}
