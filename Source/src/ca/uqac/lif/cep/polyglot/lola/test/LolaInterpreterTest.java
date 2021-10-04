/*
    Multiple interpreters for BeepBeep
    Copyright (C) 2017-2021 Sylvain Hallé

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
package ca.uqac.lif.cep.polyglot.lola.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.polyglot.lola.LolaInterpreter;
import ca.uqac.lif.cep.polyglot.lola.NamedGroupProcessor;
import ca.uqac.lif.cep.polyglot.lola.Trigger;
import ca.uqac.lif.cep.polyglot.lola.Trigger.Triggerable;
import ca.uqac.lif.cep.tmf.QueueSource;

public class LolaInterpreterTest
{
	@Test
	public void testNames1()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents("A", "B");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = s1";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals("A", (String) o);
		o = pul.pull();
		assertEquals("B", (String) o);
	}
	
	@Test
	public void testNames2()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents("A", "B");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = s1";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals("A", (String) o);
		o = pul.pull();
		assertEquals("B", (String) o);
		QueueSource source_dup = new QueueSource(1).setEvents("B", "C");
		NamedGroupProcessor gp_dup = gp.duplicate(true);
		Connector.connect(source_dup, 0, gp_dup, gp_dup.getInputIndex("s1"));
		Pullable pul_dup = gp_dup.getPullableOutput("s2");
		o = pul_dup.pull();
		assertEquals("B", (String) o);
		o = pul_dup.pull();
		assertEquals("C", (String) o);
	}

	@Test
	public void testOffset1()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents("A", "B");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = s1[-1,Z]";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals("Z", (String) o);
		o = pul.pull();
		assertEquals("A", (String) o);
		o = pul.pull();
		assertEquals("B", (String) o);
	}

	@Test
	public void testOffset2()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents("A", "B", "C");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = s1[1,Z]";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals("B", (String) o);
		o = pul.pull();
		assertEquals("C", (String) o);
	}

	@Test
	public void testOffset3()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents("A", "B", "C");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = s1[1,Z]";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals("B", (String) o);
		o = pul.pull();
		assertEquals("C", (String) o);
	}

	@Test
	public void testOffset4()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents("A", "B", "C");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = s1[1,Z]";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		QueueSource source_dup = new QueueSource(1).setEvents("B", "C");
		NamedGroupProcessor gp_dup = gp.duplicate(true);
		Connector.connect(source_dup, 0, gp_dup, gp_dup.getInputIndex("s1"));
		Pullable pul_dup = gp_dup.getPullableOutput("s2");
		o = pul_dup.pull();
		assertEquals("B", (String) o);
		o = pul_dup.pull();
		assertEquals("C", (String) o);
	}

	@Test
	public void testBoolean1()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = true";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(true, (Boolean) o);
		o = pul.pull();
		assertEquals(true, (Boolean) o);
	}

	@Test
	public void testBoolean2()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = false";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(false, (Boolean) o);
		o = pul.pull();
		assertEquals(false, (Boolean) o);
	}

	@Test
	public void testOutput1()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents(true, true);
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = ite(s1, s2[-1,0], 1)\noutput s2";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		assertEquals(1, gp.getOutputArity());
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(0, o);
		o = pul.pull();
		assertEquals(0, o);
	}

	@Test
	public void testOutput2()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1).setEvents(true, true, false);
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = ite(s1, (s2[-1,0]) + (1), 0)\noutput s2";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		assertEquals(1, gp.getOutputArity());
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(1.0f, o);
		o = pul.pull();
		assertEquals(2.0f, o);
		o = pul.pull();
		assertEquals(0, o);
	}

	@Test
	public void testTrigger1()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		@SuppressWarnings("unused")
		Object o;
		QueueSource source = new QueueSource(1);
		source.addEvent(1);
		source.addEvent(2);
		source.addEvent(3);
		source.addEvent(4);
		LolaInterpreter my_int = new LolaInterpreter();
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build("s2 = s1\ntrigger tr1 (s2) > (2)");
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		TestTriggerable tt = new TestTriggerable();
		Trigger t = my_int.getTrigger("tr1");
		my_int.setTriggerable("tr1", tt);
		t.pull();
		assertEquals(0, tt.m_notifCount);
		t.pull();
		assertEquals(0, tt.m_notifCount);
		t.pull();
		assertEquals(1, tt.m_notifCount);
		t.pull();
		assertEquals(1, tt.m_notifCount);
	}

	@Test
	public void testAdd()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1);
		source.addEvent(1);
		source.addEvent(2);
		source.addEvent(3);
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = (s1) + (3)";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(4.0f, (Number) o);
		o = pul.pull();
		assertEquals(5.0f, (Number) o);
	}

	@Test
	public void testIfThenElse1()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source1 = new QueueSource(1).setEvents(false, true, false, true);
		QueueSource source2 = new QueueSource(1).setEvents("A", "B", "C", "D");
		QueueSource source3 = new QueueSource(1).setEvents("a", "b", "c", "d");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s = ite(s1,s2,s3)";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source1, 0, gp, gp.getInputIndex("s1"));
		Connector.connect(source2, 0, gp, gp.getInputIndex("s2"));
		Connector.connect(source3, 0, gp, gp.getInputIndex("s3"));
		Pullable pul = gp.getPullableOutput("s");
		o = pul.pull();
		assertEquals("a", (String) o);
		o = pul.pull();
		assertEquals("B", (String) o);
		o = pul.pull();
		assertEquals("c", (String) o);
		o = pul.pull();
		assertEquals("D", (String) o);
	}

	@Test
	public void testIfThenElse2()
			throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source1 = new QueueSource(1).setEvents(0, 3, 1, 4);
		QueueSource source2 = new QueueSource(1).setEvents("A", "B", "C", "D");
		QueueSource source3 = new QueueSource(1).setEvents("a", "b", "c", "d");
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s = ite((s1) > (2), s2, s3)";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source1, 0, gp, gp.getInputIndex("s1"));
		Connector.connect(source2, 0, gp, gp.getInputIndex("s2"));
		Connector.connect(source3, 0, gp, gp.getInputIndex("s3"));
		Pullable pul = gp.getPullableOutput("s");
		o = pul.pull();
		assertEquals("a", (String) o);
		o = pul.pull();
		assertEquals("B", (String) o);
		o = pul.pull();
		assertEquals("c", (String) o);
		o = pul.pull();
		assertEquals("D", (String) o);
	}

	@Test
	public void testIfThenElse3Pull() throws InvalidGrammarException, BuildException
	{
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "t1  = ite(e , 15 , t2)\nt2 = t1[-1,10]\noutput t1";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		QueueSource e = new QueueSource().setEvents(false);
		Connector.connect(e, 0, gp, gp.getInputIndex("e"));
		assertEquals(1, gp.getInputArity());
		Pullable out = gp.getPullableOutput();
		Object o = out.pull();
		assertEquals(10, o);
	}

	public static class TestTriggerable implements Triggerable
	{
		public int m_notifCount = 0;

		@Override
		public void getNotified()
		{
			m_notifCount++;
		}

	}
}
