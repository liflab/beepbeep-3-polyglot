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
package ca.uqac.lif.cep.polyglot.qea.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.polyglot.Util;
import ca.uqac.lif.cep.polyglot.qea.XmlQeaInterpreter;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.xml.ParseXml;

public class XmlQeaInterpreterTest 
{
	@Test
	public void testXPathGuard1() throws InvalidGrammarException, BuildException
	{
		// Just check that the parsing does not throw an exception
		XmlQeaInterpreter q_int = new XmlQeaInterpreter();
		String expression = "0 -> 0 [/e/action = bar]";
		assertNotNull(q_int.build(expression));
	}
	
	@Test
	public void testXPathGuard2() throws InvalidGrammarException, BuildException
	{
		Object o;
		XmlQeaInterpreter q_int = new XmlQeaInterpreter();
		String expression = "0 [foo]\n1 [bar]\n0 -> 1 [e/action/text() = foo]\n1 -> 0 [e/action/text() = bar]";
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("<e><action>foo</action></e>", "<e><action>bar</action></e>");
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals("bar", o);
		o = p.pull();
		assertEquals("foo", o);
	}
	
	@Test
	public void testXPathQuantifier1() throws InvalidGrammarException, BuildException
	{
		Object o;
		XmlQeaInterpreter q_int = new XmlQeaInterpreter();
		String expression = Util.convertStreamToString(AtomicQeaInterpreterTest.class.getResourceAsStream("test5.qea"));
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("<e><id>foo</id></e>", "<e><id>bar</id></e>", "<e><id>foo</id></e>", "<e><id>foo</id></e>").loop(false);
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals(true, o);
		o = p.pull();
		assertEquals(true, o);
		o = p.pull();
		assertEquals(true, o);
		o = p.pull();
		assertEquals(false, o);
	}
	
	@Test
	public void testXPathQuantifier2() throws InvalidGrammarException, BuildException
	{
		Object o;
		XmlQeaInterpreter q_int = new XmlQeaInterpreter();
		String expression = Util.convertStreamToString(AtomicQeaInterpreterTest.class.getResourceAsStream("test5.qea"));
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents("<e><id>foo</id></e>", "<e><id>bar</id><id>foo</id></e>", "<e><id>foo</id></e>", "<e><id>foo</id></e>").loop(false);
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals(true, o);
		o = p.pull();
		assertEquals(true, o);
		o = p.pull();
		assertEquals(false, o);
		o = p.pull();
		assertEquals(false, o);
	}
	
	@Test
	public void testXPathQuantifier3() throws InvalidGrammarException, BuildException
	{
		Object o;
		XmlQeaInterpreter q_int = new XmlQeaInterpreter();
		String expression = Util.convertStreamToString(AtomicQeaInterpreterTest.class.getResourceAsStream("test6.qea"));
		GroupProcessor gp = (GroupProcessor) q_int.build(expression);
		QueueSource src = new QueueSource().setEvents(
				"<e><id>foo</id><action>a</action><p>0</p></e>",
				"<e><id>bar</id><action>a</action><p>0</p></e>",
				"<e><id>foo</id><action>b</action><p>1</p></e>",
				"<e><id>bar</id><action>b</action><p>1</p></e>").loop(false);
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		o = p.pull();
		assertEquals(false, o);
		o = p.pull();
		assertEquals(false, o);
		o = p.pull();
		assertEquals(false, o);
		o = p.pull();
		assertEquals(true, o);
	}
}
