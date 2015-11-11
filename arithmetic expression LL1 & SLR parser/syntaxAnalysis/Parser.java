package syntaxAnalysis;

import java.io.IOException;
import java.io.InputStream;

import abstractSyntaxTrees.Expression;

public abstract class Parser {
	protected Lexer scanner;
	protected Token curToken = null;

	public Parser(InputStream in) throws IOException {
		scanner = new Lexer(in);
	}

	public Parser(Lexer scanner) {
		this.scanner = scanner;
	}

	public Parser(String s) throws IOException {
		this.scanner = new Lexer(s);
	}

	protected boolean consume() throws TokenizeException {
		if (scanner.hasNext()) {
			curToken = scanner.nextToken();
			return true;
		}
		return false;
	}

	public abstract Expression parse() throws TokenizeException, ParseException;
}
