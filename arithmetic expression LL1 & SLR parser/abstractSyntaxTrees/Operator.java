package abstractSyntaxTrees;

import syntaxAnalysis.TokenType;

public class Operator{
	public TokenType type;
	public Operator(TokenType type) {
		this.type = type;
	}
	public String toString() {
		return type.toString();
	}
}
