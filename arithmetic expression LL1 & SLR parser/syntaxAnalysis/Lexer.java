package syntaxAnalysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class Lexer {
	private InputStream in;
	private StringBuilder curString = new StringBuilder();
	private int index;
	private Character curChar = null;
	private char[] input;
	private boolean isReadFromString = false;

	public Lexer(InputStream in) throws IOException {
		this.in = in;
		readNext();
		index = 0;
	}
	public Lexer(String s) throws IOException {
		this.isReadFromString = true;
		this.input = s.replaceAll("\\s+", "").toCharArray();
		this.index = -1;
		readNext();
	}
	public int getCurIndex() {
		return index;
	}
	public String getCurString() {
		return curString.toString();
	}
	public boolean hasNext() {
		return curChar != null;
	}
	public Token nextToken() throws TokenizeException {
		try {
			if (curChar == Character.MIN_VALUE) {
				curChar = null;
				index ++;
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
						throw new TokenizeException(index, curString.toString());
				}
			}

		} catch (IOException e) {
			curChar = null;
			return new Token(TokenType.EOT, "$");
		}
	}

	private boolean readNext() throws IOException {
		int c = -1;
		if (!isReadFromString)
			c = in.read();
		else if (index+1 < input.length)
			c = input[index+1];
		else 
			c = -1;
		index ++;
		if (c == -1) {
			curChar = Character.MIN_VALUE;
			return false;
		} else {
			curChar = (char) c;
			if (Character.isWhitespace(curChar)) {
				index --;
				return readNext();
			}
			curString.append(curChar);
		}
		return true;
	}
}
