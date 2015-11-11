import java.io.*;

import semanticAnalysis.Evaluator;
import semanticAnalysis.Printer;
import syntaxAnalysis.ParseException;
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
		try {
			System.out.println("LL1:");
			InputStream in = new ByteArrayInputStream(s.getBytes());
			syntaxAnalysis.LL1.Parser parser = new syntaxAnalysis.LL1.Parser(s);
			Expression expr = parser.parse();
			expr.accept(Printer.getInstance());
			System.out.println("=" + expr.accept(Evaluator.getInstance()));
			
			System.out.println("SLR:");
			InputStream in = new ByteArrayInputStream(s.getBytes());
			syntaxAnalysis.SLR.Parser parser = new syntaxAnalysis.SLR.Parser(in);
			Expression expr =	parser.parse();
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
