package syntaxAnalysis;


public class TokenizeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6107021231122317933L;
	public int position;
	public String value;
	public TokenType type;
	public TokenizeException(int position, String value) {
		super("Cant recognized token near index " + position);
	}
	public TokenizeException(TokenType type, String value) {
		super("Token value " + value + " doesn't match " + type.regex);
	}
}
