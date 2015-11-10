package sementicAnalysis;

import java.io.*;
import java.util.regex.Pattern;

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
		String expression = "10+(21-32)*(43/(54+65-76)*87)/98" + Character.MIN_VALUE + Character.MIN_VALUE + "+0+0";
		System.out.println(expression);
		try {
			InputStream in = new ByteArrayInputStream(expression.getBytes());
			syntaxAnalysis.LL1.Parser parser = new syntaxAnalysis.LL1.Parser(in);
			Expression expr = parser.parse();
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
			InputStream in = new ByteArrayInputStream(expression.getBytes());
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
