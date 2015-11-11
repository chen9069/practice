import java.io.*;
import java.util.regex.Pattern;

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
		String s = "  0+ 10 +(21  -32  )*  ((((43 ))  +(1234)  )/(	54+65-  76)* 87) /98-0 ";
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
		try {
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
