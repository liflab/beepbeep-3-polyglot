package ca.uqac.lif.cep.polyglot.ltl;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.tmf.QueueSource;

public class AtomicLtlInterpreterTest 
{
	@Test
	public void test1() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G (a)";
		AtomicLtlInterpreter ali = new AtomicLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("a", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
	
	@Test
	public void test2() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G (a)";
		AtomicLtlInterpreter ali = new AtomicLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("a", "a", "a").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertNull(b);
	}
	
	@Test
	public void test3() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "C 2 (a)";
		AtomicLtlInterpreter ali = new AtomicLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("a", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
}
