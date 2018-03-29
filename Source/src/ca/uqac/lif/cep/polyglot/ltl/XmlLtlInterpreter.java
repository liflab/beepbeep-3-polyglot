package ca.uqac.lif.cep.polyglot.ltl;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.polyglot.Util;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Strings;
import ca.uqac.lif.cep.xml.XPathFunction;

public class XmlLtlInterpreter extends LtlInterpreter
{
	public XmlLtlInterpreter() throws InvalidGrammarException
	{
		super();
		setGrammar(super.getGrammar() + "\n" + getGrammar());
	}
	
	@Override
	protected String getGrammar()
	{
		return Util.convertStreamToString(LtlInterpreter.class.getResourceAsStream("xml.bnf"));
	}
	
	@Builds(rule="<equals>", pop=true, clean=true)
	public Processor handleEquals(Object ... args)
	{
		String cons = (String) args[1];
		XPathFunction xpf = (XPathFunction) args[0];
		ApplyFunction af = new ApplyFunction(new FunctionTree(Equals.instance,
				new FunctionTree(Strings.toString, new FunctionTree(Bags.anyElement, new FunctionTree(xpf, StreamVariable.X))), new Constant(cons)));
		Passthrough pt = forkInput(0);
		Connector.connect(pt, af);
		return add(af);
	}
	
	@Builds(rule="<xpathfct>")
	public void handleXPath(ArrayDeque<Object> stack)
	{
		XPathFunction xpf = new XPathFunction((String) stack.pop());
		stack.push(xpf);
	}
}
