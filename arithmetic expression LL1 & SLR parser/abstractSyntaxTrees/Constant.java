package abstractSyntaxTrees;

import syntaxAnalysis.Token;

public class Constant extends Expression{
	public Token token;
	public Constant(Token curToken) {
		this.token = curToken;
		// TODO Auto-generated constructor stub
	}
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
