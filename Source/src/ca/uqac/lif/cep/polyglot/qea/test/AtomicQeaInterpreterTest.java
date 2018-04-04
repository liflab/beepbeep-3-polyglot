package ca.uqac.lif.cep.polyglot.qea.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.polyglot.Util;
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
	public void testInitAsg() throws InvalidGrammarException, BuildException
	{
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = "$x := 0";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		MooreMachine mm = (MooreMachine) gp.getAssociatedInput(0);
		assertEquals(0, mm.getContext("x"));
	}
	
	@Test
	public void testSimple2() throws InvalidGrammarException, BuildException
	{
		Object o;
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = "0 [foo]\n0 -> 0 [(a) | (b)]";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("a", "b");
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals("foo", ((Constant) o).getValue());
		o = p.pull();
		assertEquals("foo", ((Constant) o).getValue());
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
		assertEquals("foo", ((Constant) o).getValue());
	}
	
	@Test
	public void testAssignment1() throws InvalidGrammarException, BuildException
	{
		Object o;
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = "1 [foo]\n0 -> 1 [a / $x := 0]";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		MooreMachine mm = (MooreMachine) gp.getAssociatedInput(0);
		QueueSource src = new QueueSource().setEvents("a");
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals("foo", ((Constant) o).getValue());
		assertEquals(0, mm.getContext("x"));
	}
	
	@Test
	public void testAssignment2() throws InvalidGrammarException, BuildException
	{
		Object o;
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = Util.convertStreamToString(AtomicQeaInterpreterTest.class.getResourceAsStream("test1.qea"));
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		MooreMachine mm = (MooreMachine) gp.getAssociatedInput(0);
		QueueSource src = new QueueSource().setEvents("a", "a", "b", "a");
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals("bar", ((Constant) o).getValue());
		assertEquals(0, mm.getContext("x"));
		o = p.pull();
		assertEquals("foo", ((Constant) o).getValue());
		assertEquals(1f, mm.getContext("x"));
		o = p.pull();
		assertEquals("foo", ((Constant) o).getValue());
		assertEquals(2f, mm.getContext("x"));
		o = p.pull();
		assertEquals("bar", ((Constant) o).getValue());
		assertEquals(0, mm.getContext("x"));
	}
	
	@Test
	public void testSum1() throws InvalidGrammarException, BuildException
	{
		Object o;
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = Util.convertStreamToString(AtomicQeaInterpreterTest.class.getResourceAsStream("test2.qea"));
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("a", "a", "b", "a", "c");
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals(1f, o);
		o = p.pull();
		assertEquals(1f, o);
		o = p.pull();
		assertEquals(2f, o);
		o = p.pull();
		assertEquals(2f, o);
		o = p.pull();
		assertEquals(3f, o);
	}
	
	@Test
	public void testAverage1() throws InvalidGrammarException, BuildException
	{
		Object o;
		AtomicQeaInterpreter q_int = new AtomicQeaInterpreter();
		String expression = Util.convertStreamToString(AtomicQeaInterpreterTest.class.getResourceAsStream("test3.qea"));
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("a", "a", "b", "a", "c");
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals(1f, o);
		o = p.pull();
		assertEquals(2f, o);
		o = p.pull();
		assertEquals(1.5f, o);
		o = p.pull();
		assertEquals(2f, o);
		o = p.pull();
		assertEquals(1f, o);
	}
}
