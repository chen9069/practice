package abstractSyntaxTrees;

public interface Visitor<ArgTyp, RstTyp> {
	public RstTyp visit(AST ast);
	public RstTyp visit(Expression expr);
	public RstTyp visit(BinaryExpr expr);
	public RstTyp visit(UnaryExpr expr);
	public RstTyp visit(Constant expr);
	public RstTyp visit(AST ast, ArgTyp... args);
	public RstTyp visit(Expression expr, ArgTyp... args);
	public RstTyp visit(BinaryExpr expr, ArgTyp... args);
	public RstTyp visit(UnaryExpr expr, ArgTyp... args);
	public RstTyp visit(Constant expr, ArgTyp... args);
}
