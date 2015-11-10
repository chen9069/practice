package syntaxAnalysis.SLR;

import java.util.HashMap;
import java.util.Map;

public class Closure {
	public int id;
	private static Map<Integer, Closure> closures = new HashMap<Integer, Closure>();
	private Map<String, Closure> shift = new HashMap<String, Closure>();
	private Map<String, Rule> reduce = new HashMap<String, Rule>();
	private Closure(int id) {
		this.id = id;
	}
	public static Closure getInstance(int id) {
		if (!closures.containsKey(id))
			closures.put(id, new Closure(id));
		return closures.get(id);
	}
	public Closure setShift(String symbol, int next) {
		shift.put(symbol, Closure.getInstance(next));
		return this;
	}public Closure setReduce(String symbol, int rule) {
		reduce.put(symbol, Rule.getInstance(rule));
		return this;
	}
	public Closure shift(String symbol) {
		return this.shift.get(symbol);
	}
	public Rule reduce(String symbol) {
		return this.reduce.get(symbol);
	}
	public boolean canShift(String symbol) {
		return shift.containsKey(symbol);
	}
	public boolean canReduce(String symbol) {
		return reduce.containsKey(symbol);
	}
	public String toString() {
		return String.valueOf(this.id);
	}
}
