package syntaxAnalysis;

public class ParseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2814278164237673281L;
	
	public int index;
	public Token token;
	public String value;
	public String rule;
	
	public ParseException(int index, Token token, String value) {
		super ("Unexpected token " + token + " near index " + index);
		this.index = index;
		this.token = token;
		this.value = value;
	}
	public ParseException(int index, String rule, String value) {
		super("Unrecognized rule: " + rule + " near index " + index);
		this.index = index;
		this.rule = rule;
	}
	public String getMessage() {
		String message = super.getMessage();
		message = String.format("%3$s\n%2$s\n%1$" + index + "s", "^", value, message);
		return message;
	}
}
