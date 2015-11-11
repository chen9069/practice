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
import syntaxAnalysis.Parser;
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
public class SLRParser extends Parser{
	// G = {non_Terminal, Terminal, Rules, Start}
	private Set<String> non_terminals;
	private Set<String> terminals;
	public SLRParser(Lexer scanner) {
		super(scanner);
		// TODO Auto-generated constructor stub
	}
	public SLRParser(InputStream in) throws IOException {
		super(in);
	}
	public SLRParser(String s) throws IOException {
		super(s);
	}
	public Expression parse() throws TokenizeException, ParseException {
		initialize();
		Stack<String> symbols = new Stack<String>();
		Stack<Closure> statuses = new Stack<Closure>();
		Stack<Expression> expressions = new Stack<Expression>();
		statuses.push(Closure.getInstance(0));
		consume();
		String curSymbol = getSymbol(super.curToken);
		while(!(curToken.isEnd() && statuses.peek().id == 0)) {
			Closure curStat = statuses.peek();
			if (curStat.canShift(curSymbol)) {
				statuses.push(curStat.shift(curSymbol));
				symbols.push(curSymbol);
				if (curToken.isNumber())
					expressions.push(new Constant(curToken));
				if (!nonTerminal(curSymbol)) 
					consume();
				curSymbol = getSymbol(curToken);
			} else if (curStat.canReduce(curSymbol)) {
					curSymbol = reduceBy(curStat.reduce(curSymbol), statuses, symbols, expressions);
			} else {
				throw new ParseException(scanner.getCurIndex(), curToken, scanner.getCurString());
			}
		}
		return expressions.pop();
	}
	private void initialize() {
		this.terminals = new HashSet<String>();
		this.non_terminals = new HashSet<String>();
		setSymbols();
		setGrammars();
		setRules();
	}
	private void setSymbols() {
		this.non_terminals.add("S");
		this.non_terminals.add("E");
		this.non_terminals.add("T");
		this.non_terminals.add("F");
		this.terminals.add("(");
		this.terminals.add(")");
		this.terminals.add("+");
		this.terminals.add("-");
		this.terminals.add("*");
		this.terminals.add("/");
		this.terminals.add("i");
	}
	private void setGrammars() { 
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
	private void setRules() {
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
	private String reduceBy (Rule rule, Stack<Closure> statuses, Stack<String> symbols, Stack<Expression> expressions) throws ParseException {
		String check = "";
		for (int i = 0; i < rule.right.length(); i ++) {
			check = symbols.pop() + check;
			statuses.pop();
		}
		if (!check.equals(rule.right))
			throw new ParseException(scanner.getCurIndex(), rule.left + "->" + check, scanner.getCurString());
		BinaryExpr binaryExpr = new BinaryExpr();
		switch (rule.id) {
		case 1:
			binaryExpr.op = new Operator(TokenType.PLUS);
			break;
		case 2:
			binaryExpr.op = new Operator(TokenType.MINUS);
			break;
		case 4:
			binaryExpr.op = new Operator(TokenType.MULTIPLY);
			break;
		case 5:
			binaryExpr.op = new Operator(TokenType.DIVIDE);
			break;
		default:
			return rule.left;
		}
		binaryExpr.right = expressions.pop();
		binaryExpr.left = expressions.pop();
		expressions.push(binaryExpr);
		return rule.left;
	}
	private boolean isTerminal(String symbol) {
		return this.terminals.contains(symbol);
	}
	private boolean nonTerminal(String symbol) {
		return this.non_terminals.contains(symbol);
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
}
