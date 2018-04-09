/*
    Multiple interpreters for BeepBeep
    Copyright (C) 2017-2018 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.polyglot.ltl.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.polyglot.ltl.AtomicTrooleanLtlInterpreter;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.SinkLast;

public class AtomicTrooleanLtlInterpreterTest 
{
	@Test
	public void testG() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G (a)";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("a", "a", "b", "a").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
	
	@Test
	public void testDuplicateG() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G (a)";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		QueueSource src = new QueueSource().setEvents("a", "a", "b", "a").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
	
	@Test
	public void test4() throws InvalidGrammarException, BuildException
	{
		String formula = "G ((g) → (X (h)))";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		Pushable p = gp.getPushableInput();
		SinkLast sink = new SinkLast();
		Connector.connect(gp, sink);
		p.push("a");
	}
	
	@Test
	public void testDuplicateAtom() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "a";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		QueueSource src = new QueueSource().setEvents("a", "b", "b", "a").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
	}
	
	@Test
	public void testF() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "F (a)";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("b", "b", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
	}
	
	@Test
	public void testOr() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "(a) ∨ (b)";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("b", "c", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
	}
	
	@Test
	public void testDuplicateOr() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "(a) ∨ (b)";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		QueueSource src = new QueueSource().setEvents("b", "c", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.TRUE, b);
	}
	
	@Test
	public void testDuplicateOrPush() throws InvalidGrammarException, BuildException
	{
		String formula = "(a) ∨ (b)";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		SinkLast sink = new SinkLast();
		Connector.connect(gp, sink);
		Pushable p = gp.getPushableInput();
		p.push("a");
		assertEquals(Troolean.Value.TRUE, sink.getLast()[0]);
	}
	
	@Test
	public void testDuplicateAtomPush() throws InvalidGrammarException, BuildException
	{
		String formula = "a";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		SinkLast sink = new SinkLast();
		Connector.connect(gp, sink);
		Pushable p = gp.getPushableInput();
		p.push("a");
		assertEquals(Troolean.Value.TRUE, sink.getLast()[0]);
	}
	
	@Test
	public void testAlwaysOr() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G ((a) ∨ (b))";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().setEvents("b", "c", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
	
	@Test
	public void testDuplicateAlwaysOr() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G ((a) ∨ (b))";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		QueueSource src = new QueueSource().setEvents("b", "c", "a", "b").loop(false);
		Connector.connect(src, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.INCONCLUSIVE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
	
	@Test
	public void testDuplicateAlwaysOrPush() throws InvalidGrammarException, BuildException
	{
		String formula = "G ((a) ∨ (b))";
		AtomicTrooleanLtlInterpreter ali = new AtomicTrooleanLtlInterpreter();
		GroupProcessor gp = (GroupProcessor) ali.build(formula).duplicate();
		SinkLast sink = new SinkLast();
		Connector.connect(gp, sink);
		Pushable p = gp.getPushableInput();
		p.push("a");
		assertEquals(Troolean.Value.INCONCLUSIVE, sink.getLast()[0]);
	}
}
