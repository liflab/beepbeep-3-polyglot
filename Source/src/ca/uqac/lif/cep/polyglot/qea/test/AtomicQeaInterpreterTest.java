package ca.uqac.lif.cep.polyglot.qea.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.polyglot.qea.AtomicQeaInterpreter;
import ca.uqac.lif.cep.tmf.QueueSource;

public class AtomicQeaInterpreterTest 
{
	@SuppressWarnings("unused")
	@Test
	public void testTrans1() throws InvalidGrammarException, BuildException
	{
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = "0 -> 1 [a]";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		MooreMachine mm = (MooreMachine) gp.getAssociatedInput(0);
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testSymbol1() throws InvalidGrammarException, BuildException
	{
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = "0 [a]";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		MooreMachine mm = (MooreMachine) gp.getAssociatedInput(0);
	}
	
	@Test
	public void testSimple1() throws InvalidGrammarException, BuildException
	{
		Object o;
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = "1 [foo]\n0 -> 1 [a]";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("a");
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals("foo", o);
	}
}
