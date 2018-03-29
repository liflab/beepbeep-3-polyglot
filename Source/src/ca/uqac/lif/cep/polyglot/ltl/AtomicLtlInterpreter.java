package ca.uqac.lif.cep.polyglot.ltl;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.polyglot.Util;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.util.Equals;

public class AtomicLtlInterpreter extends LtlInterpreter
{
	public AtomicLtlInterpreter() throws InvalidGrammarException
	{
		super();
		setGrammar(super.getGrammar() + "\n" + getGrammar());
	}
	
	@Override
	protected String getGrammar()
	{
		return Util.convertStreamToString(LtlInterpreter.class.getResourceAsStream("atomic.bnf"));
	}
	
	@Builds(rule="<atom>")
	public void handleAtom(ArrayDeque<Object> stack)
	{
		String s = (String) stack.pop();
		ApplyFunction af = new ApplyFunction(new FunctionTree(Equals.instance,
				new Constant(s), StreamVariable.X));
		Passthrough pt = forkInput(0);
		Connector.connect(pt, af);
		stack.push(add(af));
	}
}
