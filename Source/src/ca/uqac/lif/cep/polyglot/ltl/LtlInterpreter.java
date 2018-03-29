package ca.uqac.lif.cep.polyglot.ltl;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.dsl.GroupProcessorBuilder;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.ltl.Eventually;
import ca.uqac.lif.cep.ltl.Globally;
import ca.uqac.lif.cep.ltl.Next;
import ca.uqac.lif.cep.ltl.Until;
import ca.uqac.lif.cep.polyglot.Util;
import ca.uqac.lif.cep.util.Booleans;

public abstract class LtlInterpreter extends GroupProcessorBuilder
{
	public LtlInterpreter() throws InvalidGrammarException
	{
		super();
	}
	
	protected String getGrammar()
	{
		return Util.convertStreamToString(LtlInterpreter.class.getResourceAsStream("core.bnf"));
	}
	
	@Builds(rule="<F>", pop=true, clean=true)
	public Processor handleF(Object ... procs)
	{
		return handleUnary(new Eventually(), procs);
	}
	
	@Builds(rule="<G>", pop=true, clean=true)
	public Processor handleG(Object ... procs)
	{
		return handleUnary(new Globally(), procs);
	}
	
	@Builds(rule="<X>", pop=true, clean=true)
	public Processor handleX(Object ... procs)
	{
		return handleUnary(new Next(), procs);
	}
	
	@Builds(rule="<U>", pop=true, clean=true)
	public Processor handleU(Object ... procs)
	{
		return handleBinary(new Until(), procs);
	}
	
	@Builds(rule="<and>", pop=true, clean=true)
	public Processor handleAnd(Object ... procs)
	{
		return handleBinary(new ApplyFunction(Booleans.and), procs);
	}
	
	@Builds(rule="<or>", pop=true, clean=true)
	public Processor handleOr(Object ... procs)
	{
		return handleBinary(new ApplyFunction(Booleans.or), procs);
	}
	
	@Builds(rule="<implies>", pop=true, clean=true)
	public Processor handleImplies(Object ... procs)
	{
		return handleBinary(new ApplyFunction(Booleans.implies), procs);
	}
	
	@Builds(rule="<not>", pop=true, clean=true)
	public Processor handleNot(Object ... procs)
	{
		return handleUnary(new ApplyFunction(Booleans.not), procs);
	}
	
	@Builds(rule="<num>")
	public void handleNum(ArrayDeque<Object> stack)
	{
		stack.push(Integer.parseInt((String) stack.pop()));
	}
	
	@Builds(rule="<C>", pop=true, clean=true)
	public Processor handleC(Object ... procs)
	{
		int num = (Integer) procs[0];
		CountTrue ct = new CountTrue(num);
		Connector.connect((Processor) procs[1], ct);
		return add(ct);
	}
	
	protected Processor handleUnary(Processor p, Object ... procs)
	{
		Connector.connect((Processor) procs[0], p);
		return add(p);
	}
	
	protected Processor handleBinary(Processor p, Object ... procs)
	{
		Connector.connect((Processor) procs[0], 0, p, 0);
		Connector.connect((Processor) procs[1], 0, p, 1);
		return add(p);
	}
}
