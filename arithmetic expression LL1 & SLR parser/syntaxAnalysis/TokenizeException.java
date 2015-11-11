package syntaxAnalysis;


public class TokenizeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6107021231122317933L;
	public int index;
	public String value;
	public TokenType type;
	public TokenizeException(int index, String value) {
		super("Cant recognized token near index " + index);
		this.index = index;
		this.value = value;
	}
	public TokenizeException(TokenType type, String value) {
		super("Token value " + value + " doesn't match " + type.regex);
	}
	public String getMessage() {
		String message = super.getMessage();
		message = String.format("%3$s\n%2$s\n%1$" + index + "s", "^", value, message);
		return message;
	}
}
