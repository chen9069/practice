package abstractSyntaxTrees;

public abstract class AST {
	public abstract <R> R accept(Visitor<Void, R> visitor);
	public abstract <A, R> R accept (Visitor<A, R> visitor, A... args);
}
