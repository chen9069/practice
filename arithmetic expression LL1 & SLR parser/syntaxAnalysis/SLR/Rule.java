package syntaxAnalysis.SLR;

import java.util.HashMap;
import java.util.Map;

import syntaxAnalysis.Token;

public class Rule {
	public int id;
	public String left;
	public String right;
	private static Map<Integer, Rule> rules = new HashMap<Integer, Rule>();
	private Rule(int id) {
		this.id = id;
	}
	public static Rule getInstance(int id) {
		if (!rules.containsKey(id))
			rules.put(id, new Rule(id));
		return rules.get(id);
	}
	public Rule setRule(String left, String right) {
		this.left = left;
		this.right = right;
		return this;
	}
}
