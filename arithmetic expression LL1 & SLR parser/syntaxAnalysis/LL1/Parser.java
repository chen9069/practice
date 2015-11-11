package syntaxAnalysis.LL1;

import java.io.IOException;
import java.io.InputStream;

import abstractSyntaxTrees.*;

import syntaxAnalysis.ParseException;
import syntaxAnalysis.Token;
import syntaxAnalysis.TokenType;
import syntaxAnalysis.TokenizeException;
import syntaxAnalysis.Lexer;

/**
 * LL1 parser
 * @author chenji
 *
 * grammar
 * E -> TE'   										select = { first(T) = [(, int] } 
 * E' -> +TE' | -TE' | epsilon  	select = { + | - | follow(E') = [), #] }
 * T -> FT'  											select = { first(F) = [(, int] }
 * T' -> *FT' | /FT' | epsilon    select = { * | / | follow(T') = [+, -, ), #] }
 * F -> (E) | int  								select = { ( | int }
 */
public class Parser {
	private Lexer scanner;
	private Token curToken = null;
	public Parser(InputStream in) throws IOException {
		scanner = new Lexer(in);
	}
	public Parser(Lexer scanner) {
		this.scanner = scanner;
	}
	public Parser(String s) throws IOException {
		this.scanner = new Lexer(s);
	}
	private boolean consume() throws TokenizeException {
		if (scanner.hasNext()) {
			curToken = scanner.nextToken();
			return true;
		}
		return false;
	}
	public Expression parse() throws TokenizeException, ParseException {
		Expression expr = parseExpression();
		if (!curToken.isEnd())
			throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
		return expr;
	}
	/**
	 * Parse the E non-terminal
	 * E -> TE'   									  select = { first(T) = [(, int] } 
	 * @return AST root node of Expression
	 * @throws TokenizeException 
	 * @throws ParseException 
	 */
	public Expression parseExpression() throws TokenizeException, ParseException {
		consume();
		if (curToken.getType() != TokenType.LPAREN && curToken.getType() != TokenType.NUMBER)
			throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
		Expression term = parseTerm();
		return parseExprOp(term);
	}
	/**
	 * Parse the E' non-terminal
	 * E' -> +TE' | -TE' | epsilon  	select = { + | - | follow(E') = [), #] } 
	 * @return AST right recursion node of Expression
	 * @throws TokenizeException 
	 * @throws ParseException 
	 */
	public Expression parseExprOp(Expression expr) throws TokenizeException, ParseException {
		switch (curToken.getType()) {
		case PLUS:
			consume();
			Expression term = parseTerm();
			BinaryExpr binaryExpr = new BinaryExpr();
			binaryExpr.op = new Operator(TokenType.PLUS);
			binaryExpr.left = expr;
			binaryExpr.right = term;
			return parseExprOp(binaryExpr);
		case MINUS:
			consume();
			term = parseTerm();
			binaryExpr = new BinaryExpr();
			binaryExpr.op = new Operator(TokenType.MINUS);
			binaryExpr.left = expr;
			binaryExpr.right = term;
			return parseExprOp(binaryExpr);
		case RPAREN:
		case EOT:
			return expr;
		default:
			throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
		}
	}

	/**
	 * Parse the T non-terminal
	 * T -> FT'  											select = { first(F) = [(, int] }
	 * @return AST root node of Term
	 * @throws TokenizeException 
	 * @throws ParseException 
	 */
	public Expression parseTerm() throws TokenizeException, ParseException {
		if (curToken.getType() != TokenType.LPAREN && curToken.getType() != TokenType.NUMBER)
			throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
		Expression factor = parseFactor();
		return parseTermOp(factor);
	}

	/**
	 * Parse the T' non-terminal
	 * T' -> *FT' | /FT' | epsilon    select = { * | / | follow(T') = [+, -, ), #] }
	 * @return AST right recursion node of Term
	 * @throws TokenizeException 
	 * @throws ParseException 
	 */
	public Expression parseTermOp(Expression expr) throws TokenizeException, ParseException {
		switch (curToken.getType()) {
		case MULTIPLY:
			consume();
			Expression factor = parseFactor();
			BinaryExpr binaryExpr = new BinaryExpr();
			binaryExpr.op = new Operator(TokenType.MULTIPLY);
			binaryExpr.left = expr;
			binaryExpr.right = factor;
			return parseTermOp(binaryExpr);
		case DIVIDE:
			consume();
			factor = parseFactor();
			binaryExpr = new BinaryExpr();
			binaryExpr.op = new Operator(TokenType.DIVIDE);
			binaryExpr.left = expr;
			binaryExpr.right = factor;
			return parseTermOp(binaryExpr);
		case PLUS:
		case MINUS:
		case RPAREN:
		case EOT:
			return expr;
		default:
			throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
		}
	}
	/**
	 * Parse the F non-terminal
	 * F -> (E) | int  								select = { ( | int }
	 * @return AST root node of Factor
	 * @throws TokenizeException 
	 * @throws ParseException 
	 */
	public Expression parseFactor() throws TokenizeException, ParseException {
		switch (curToken.getType()) {
		case LPAREN:
			Expression expr = parseExpression();
			if (curToken.getType() != TokenType.RPAREN)
				throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
			consume();
			return expr;
		case NUMBER:
			Constant cons = new Constant(curToken);
			consume();
			return cons;
		default:
			throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
		}
	}
}
