package syntaxAnalysis.SLR;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import abstractSyntaxTrees.BinaryExpr;
import abstractSyntaxTrees.Constant;
import abstractSyntaxTrees.Expression;
import abstractSyntaxTrees.Operator;

import syntaxAnalysis.ParseException;
import syntaxAnalysis.Token;
import syntaxAnalysis.TokenType;
import syntaxAnalysis.TokenizeException;
import syntaxAnalysis.Lexer;
/**
 * SLR parser
 * @author chenji
 *
 * grammar
 * S -> E 							follow(S) = {#}
 * E -> E+T | E-T | T		follow(E) = {+, -, ), #}
 * T -> T*F | T/F | F		follow(T) = {+, -, ), *, /, #}
 * F -> (E) | int				follow(F) = {+, -, ), *, /, #}
 * 
 * closures
 * I0 = {S->.E | E->.E+T | E->.E-T | E->.T | T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}		GOTO = {E:=I1, T:=I2, F:=I3, (:=I4, int:=I5}
 * I1 = {S->E. | E->E.+T | E->E.-T}																														GOTO = {+:=I6, -:=I7}		reduce: follow(S):=r0
 * I2 = {E->T. | T->T.*F | T->T./F}																														GOTO = {*:=I8, /:=I9} 	reduce: follow(E):=r3
 * I3 = {T->F.}																																																				reduce: follow(T):=r6
 * I4 = {F->(.E) | E->.E+T | E->.E-T | E->.T | T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}	GOTO = {E:=I10, T:=I2, F:=I3, (:=I4, int:=I5}
 * I5 = {F->int.}																																																			reduce: follow(F):=r8 
 * I6 = {E->E+.T | T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}															GOTO = {T:=I11, F:=I3, (:=I4, int:=I5}
 * I7 = {E->E-.T | T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}															GOTO = {T:=I12, F:=I3, (:=I4, int:=I5}
 * I8 = {T->T*.F | F->.(E) | F->.int}																													GOTO = {F:=I13, (:=I4, int:=I5}
 * I9 = {T->T/.F | F->.(E) | F->.int}																													GOTO = {F:=I14, (:=I4, int:=I5}
 * I10 = {F->(E.) | E->E.+T | E->E.-T}																												GOTO = {):=I15, +:=I6, -:=I7}
 * I11 = {E->E+T. | T->T.*F | T->T./F}																												GOTO = {*:=I8, /:=I9}		reduce: follow(E):=r1
 * I12 = {E->E-T. | T->T.*F | T->T./F}																												GOTO = {*:=I8, /:=I9}		reduce: follow(E):=r2
 * I13 = {T->T*F.}																																																		reduce: follow(T):=r4
 * I14 = {T->T/F.}																																																		reduce: follow(T):=r5
 * I15 = {F->(E).}																																																		reduce: follow(F):=r7
 */
public class Parser {
	// G = {non_Terminal, Terminal, Rules, Start}
	private Set<String> non_terminals;
	private Set<String> terminals;
	// scanner use
	private Lexer tokenizer;
	private Token curToken = null;
	// parser use
	private Token curNum;
	private String curSymbol;
	private Stack<String> symbols;
	private Stack<Closure> statuses;
	private Stack<Expression> expressions;
	public Parser(InputStream in) throws IOException {
		tokenizer = new Lexer(in);
	}
	public Parser(Lexer tokenizer) {
		this.tokenizer = tokenizer;
	}
	private boolean consume() throws TokenizeException {
		if (tokenizer.hasNext()) {
			curToken = tokenizer.nextToken();
			curSymbol = getSymbol(curToken);
			return true;
		}
		return false;
	}
	public void initialize() {
		this.symbols = new Stack<String>();
		this.statuses = new Stack<Closure>();
		this.expressions = new Stack<Expression>();
		this.non_terminals = new HashSet<String>();
		setSymbols();
		setGrammars();
		setRules();
	}
	public void setSymbols() {
		this.non_terminals.add("S");
		this.non_terminals.add("E");
		this.non_terminals.add("T");
		this.non_terminals.add("F");
	}
	public void setGrammars() { 
		//I0 = {S->.E | E->.E+T | E->.E-T | E->.T | T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}		GOTO = {E:=I1, T:=I2, F:=I3, (:=I4, int:=I5}
		Closure.getInstance(0).setShift("E", 1).setShift("T", 2).setShift("F", 3).setShift("(", 4).setShift("i", 5);
		//I1 = {S->E. | E->E.+T | E->E.-T}		GOTO = {+:=I6, -:=I7} 	reduce: follow(S):=r0
		Closure.getInstance(1).setShift("+", 6).setShift("-", 7)
													.setReduce("#", 0);
		//I2 = {E->T. | T->T.*F | T->T./F}		GOTO = {*:=I8, /:=I9} 	reduce: follow(E):=r3
		Closure.getInstance(2).setShift("*", 8).setShift("/", 9)
													.setReduce("+", 3).setReduce("-", 3).setReduce(")", 3).setReduce("#", 3);
		//I3 = {T->F.}		reduce: follow(T):=r6
		Closure.getInstance(3).setReduce("+", 6).setReduce("-", 6).setReduce("*", 6).setReduce("/", 6).setReduce(")", 6).setReduce("#", 6);
		//I4 = {F->(.E) [.E]| E->.E+T | E->.E-T | E->.T | T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}		GOTO = {E:=I10, T:=I2, F:=I3, (:=I4, int:=I5}
		Closure.getInstance(4).setShift("E", 	10).setShift("T", 2).setShift("F", 3).setShift("(", 4).setShift("i", 5);
		//I5 = {F->int.}		reduce: follow(F):=r8 
		Closure.getInstance(5).setReduce("+", 8).setReduce("-", 8).setReduce("*", 8).setReduce("/", 8).setReduce(")", 8).setReduce("#", 8);
		//I6 = {E->E+.T [.T]| T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}		GOTO = {T:=I11, F:=I3, (:=I4, int:=I5}
		Closure.getInstance(6).setShift("T", 11).setShift("F", 3).setShift("(", 4).setShift("i", 5);
		//I7 = {E->E-.T [.T]| T->.T*F | T->.T/F | T->.F | F->.(E) | F->.int}		GOTO = {T:=I12, F:=I3, (:=I4, int:=I5}
		Closure.getInstance(7).setShift("T", 12).setShift("F", 3).setShift("(", 4).setShift("i", 5);
		//I8 = {T->T*.F [.F]| F->.(E) | F->.int}		GOTO = {F:=I13, (:=I4, int:=I5}
		Closure.getInstance(8).setShift("F", 13).setShift("(", 4).setShift("i", 5);
		//I9 = {T->T/.F [.F]| F->.(E) | F->.int}		GOTO = {F:=I14, (:=I4, int:=I5}
		Closure.getInstance(9).setShift("F", 14).setShift("(", 4).setShift("i", 5);
		//I10 = {F->(E.) | E->E.+T | E->E.-T}		GOTO = {):=I15, +:=I6, -:=I7}
		Closure.getInstance(10).setShift(")", 15).setShift("+", 6).setShift("-", 7);
		//I11 = {E->E+T. | T->T.*F | T->T./F}		GOTO = {*:=I8, /:=I9}		reduce: follow(E):=r1
		Closure.getInstance(11).setShift("*", 8).setShift("/", 9)
													 .setReduce("+", 1).setReduce("-", 1).setReduce(")", 1).setReduce("#", 1);
		//I12 = {E->E-T. | T->T.*F | T->T./F}		GOTO = {*:=I8, /:=I9}		reduce: follow(E):=r2
		Closure.getInstance(12).setShift("*", 8).setShift("/", 9)
													.setReduce("+", 2).setReduce("-", 2).setReduce(")", 2).setReduce("#", 2);
		
		//I13 = {T->T*F.}		reduce: follow(T):=r4		
		Closure.getInstance(13).setReduce("+", 4).setReduce("-", 4).setReduce("*", 4).setReduce("/", 4).setReduce(")", 4).setReduce("#", 4);
		//I14 = {T->T/F.}		reduce: follow(T):=r5
		Closure.getInstance(14).setReduce("+", 5).setReduce("-", 5).setReduce("*", 5).setReduce("/", 5).setReduce(")", 5).setReduce("#", 5);
		//I15 = {F->(E).}		reduce: follow(F):=r7
		Closure.getInstance(15).setReduce("+", 7).setReduce("-", 7).setReduce("*", 7).setReduce("/", 7).setReduce(")", 7).setReduce("#", 7);
	}
	public void setRules() {
		Rule.getInstance(0).setRule("S", "E");
		Rule.getInstance(1).setRule("E", "E+T");
		Rule.getInstance(2).setRule("E", "E-T");
		Rule.getInstance(3).setRule("E", "T");
		Rule.getInstance(4).setRule("T", "T*F");
		Rule.getInstance(5).setRule("T", "T/F");
		Rule.getInstance(6).setRule("T", "F");
		Rule.getInstance(7).setRule("F", "(E)");
		Rule.getInstance(8).setRule("F", "i");
	}
	private void reduceBy (Rule rule) throws ParseException {
		String check = "";
		for (int i = 0; i < rule.right.length(); i ++) {
			check = symbols.pop() + check;
			statuses.pop();
		}
		if (!check.equals(rule.right))
			throw new ParseException(tokenizer.getCurIndex(), rule.left + "->" + check, tokenizer.getCurString());
		switch (rule.id) {
		case 1:
			BinaryExpr binaryExpr = new BinaryExpr();
			binaryExpr.right = expressions.pop();
			binaryExpr.left = expressions.pop();
			binaryExpr.op = new Operator(TokenType.PLUS);
			expressions.push(binaryExpr);
			break;
		case 2:
			binaryExpr = new BinaryExpr();
			binaryExpr.right = expressions.pop();
			binaryExpr.left = expressions.pop();
			binaryExpr.op = new Operator(TokenType.MINUS);
			expressions.push(binaryExpr);
			break;
		case 4:
			binaryExpr = new BinaryExpr();
			binaryExpr.right = expressions.pop();
			binaryExpr.left = expressions.pop();
			binaryExpr.op = new Operator(TokenType.MULTIPLY);
			expressions.push(binaryExpr);
			break;
		case 5:
			binaryExpr = new BinaryExpr();
			binaryExpr.right = expressions.pop();
			binaryExpr.left = expressions.pop();
			binaryExpr.op = new Operator(TokenType.DIVIDE);
			expressions.push(binaryExpr);
			break;
		case 8:
			Constant constant = new Constant(curNum);
			expressions.push(constant);
			break;
		default:
			break;
		}
		curSymbol = rule.left;
	}
	private boolean isTerminal(String symbol) {
		return !this.non_terminals.contains(symbol);
	}
	private String getSymbol(Token token) {
		switch(token.getType()) {
		case NUMBER:
			return "i";
		case EOT:
			return "#";
		default:
			return token.getValue();
		}
	}
	public Expression parse() throws TokenizeException, ParseException {
		initialize();
		statuses.push(Closure.getInstance(0));
		consume();
		while(!(curToken.isEnd() && statuses.peek().id == 0)) {
			Closure curStat = statuses.peek();
			if (curStat.canShift(curSymbol)) {
				//System.out.println("shift: " + curSymbol);
				statuses.push(curStat.shift(curSymbol));
				symbols.push(curSymbol);
				if (curToken.isNumber())
					curNum = curToken;
				if (!isTerminal(curSymbol))
					curSymbol = getSymbol(curToken);
				else
					consume();
			} else if (curStat.canReduce(curSymbol)) {
				while (curStat.canReduce(curSymbol)) {
					//System.out.println("reduce: r" + curStat.reduce(curSymbol).id);
					reduceBy(curStat.reduce(curSymbol));
				}
			} else {
				throw new ParseException(tokenizer.getCurIndex(), curToken, tokenizer.getCurString());
			}
		}
		return expressions.pop();
	}
}
