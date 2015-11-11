package syntaxAnalysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class Lexer {
	private InputStream in;
	private StringBuilder curString = new StringBuilder();
	private transient int cursor;
	private Character curChar = null;
	private char[] input;
	private boolean isReadFromString = false;

	public Lexer(InputStream in) throws IOException {
		this.in = in;
		cursor = -1;
		readNext();
	}
	public Lexer(String s) throws IOException {
		this.isReadFromString = true;
		//s = s.replaceAll("\\s+", "");
		this.input = s.toCharArray();
		this.cursor = -1;
		readNext();
	}
	public int getCurIndex() {
		return cursor;
	}
	public String getCurString() {
		return curString.toString();
	}
	public boolean hasNext() {
		return curChar != null;
	}
	public Token nextToken() throws TokenizeException {
		try {
			skipWhitespaces();
			if (curChar == Character.MIN_VALUE) {
				curChar = null;
				cursor ++;
				return new Token(TokenType.EOT, "");
			}
			else {
				switch (curChar) {
				case '+':
					readNext();
					return new Token(TokenType.PLUS, "+");
				case '-':
					readNext();
					return new Token(TokenType.MINUS, "-");
				case '*':
					readNext();
					return new Token(TokenType.MULTIPLY, "*");
				case '/':
					readNext();
					return new Token(TokenType.DIVIDE, "/");
				case '(':
					readNext();
					return new Token(TokenType.LPAREN, "(");
				case ')':
					readNext();
					return new Token(TokenType.RPAREN, ")");
				default:
					StringBuilder number = new StringBuilder();
					while (Character.isLetterOrDigit(curChar)) {
						number.append(curChar);
						readNext();
					}
					if (Pattern.matches("^(" + TokenType.NUMBER.regex + ")$", number.toString()))
						return new Token(TokenType.NUMBER, number.toString());
					else if (Pattern.matches("^(" + TokenType.SYMBOL.regex + ")$", number.toString()))
						return new Token(TokenType.SYMBOL, number.toString());
					else
						throw new TokenizeException(cursor, curString.toString());
				}
			}

		} catch (IOException e) {
			curChar = null;
			return new Token(TokenType.EOT, "$");
		}
	}
	private void skipWhitespaces() throws IOException {
		while (Character.isWhitespace(curChar) && readNext()) {}
	}
	private boolean readNext() throws IOException {
		int c = -1;
		cursor ++;
		if (!isReadFromString)
			c = in.read();
		else if (cursor < input.length)
			c = input[cursor];
		if (c == -1) {
			curChar = Character.MIN_VALUE;
			curString.append(curChar);
			return false;
		} else {
			curChar = (char) c;
			curString.append(curChar);
		}
		return true;
	}
}
