package syntaxAnalysis;

public enum TokenType {
	COMMA("[,]"),
	LPAREN("[(]"),
	RPAREN("[)]"),
	LSQUARE("[\\[]"),
	RSQUARE("[]]"),
	LCURLY("[{]"),
	RCURLY("[}]"),
	PLUS("[+]"),
	MINUS("[-]"),
	DIVIDE("[/]"),
	MULTIPLY("[*]"),
	NUMBER("0|([1-9][0-9]*)"),
	SYMBOL("[a-zA-Z]+[0-9]*"),
	EOT("$");
	public final String regex;
	TokenType(String regex) {
		this.regex = regex;
	}
}
