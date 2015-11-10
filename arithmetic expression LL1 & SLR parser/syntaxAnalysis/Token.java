package syntaxAnalysis;

import java.util.regex.Pattern;

public class Token {
	private TokenType type;
	private String value;
	public Token(TokenType type, String value) throws TokenizeException {
		if (!Pattern.matches("^(" + type.regex + ")$", value))
			throw new TokenizeException(type, value);
		this.type = type;
		this.value = value;
	}
	public TokenType getType() {
		return this.type;
	}
	public String getValue() {
		return this.value;
	}
	public String toString() {
		return type.toString() + ":" + value;
	}
	public boolean isEnd() {
		return type == TokenType.EOT;
	}
	public boolean isNumber() {
		return type == TokenType.NUMBER;
	}
}
