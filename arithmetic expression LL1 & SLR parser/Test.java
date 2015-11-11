import java.io.*;

import semanticAnalysis.Evaluator;
import semanticAnalysis.Printer;
import syntaxAnalysis.ParseException;
import syntaxAnalysis.Parser;
import syntaxAnalysis.TokenizeException;

import abstractSyntaxTrees.*;

public class Test {

	/**
	 * @param args
	 * @throws TokenizeException
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "10 +(21  -32	)*	((((4  3 ))    )/(	5  4+6	5-  76)* 8   7) /9 8";
		System.out.println("Expression: " + s);
		InputStream in;
		Expression expr;
		try {
			System.out.println("LL1:");
			in = new ByteArrayInputStream(s.getBytes());
			Parser ll1Parser = new syntaxAnalysis.LL1.LL1Parser(s);
			expr = ll1Parser.parse();
			expr.accept(Printer.getInstance());
			System.out.println("=" + expr.accept(Evaluator.getInstance()));

			System.out.println("SLR:");
			in = new ByteArrayInputStream(s.getBytes());
			Parser slrParser = new syntaxAnalysis.SLR.SLRParser(in);
			expr = slrParser.parse();
			expr.accept(Printer.getInstance());
			System.out.println("=" + expr.accept(Evaluator.getInstance()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TokenizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
