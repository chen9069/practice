package abstractSyntaxTrees;

public class UnaryExpr extends Expression{

	public Expression expr;
	public Operator op;
	
	@Override
	public <A, R> R accept(Visitor<A, R> visitor, A... args) {
		// TODO Auto-generated method stub
		return visitor.visit(this, args);
	}

	@Override
	public <R> R accept(Visitor<Void, R> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}

}
