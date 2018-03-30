/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.polyglot.ltl;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.xml.ParseXml;

public class XmlLtlInterpreterTest 
{
	@Test
	public void test1() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G (e/id/text() = 0)";
		XmlLtlInterpreter ali = new XmlLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().loop(false);
		src.setEvents("<e><id>0</id></e>", "<e><id>1</id></e>");
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertEquals(Troolean.Value.FALSE, b);
	}
	
	@Test
	public void test2() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G ((e/id/text() = 0) ∨ (e/id/text() = 2))";
		XmlLtlInterpreter ali = new XmlLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().loop(false);
		src.setEvents("<e><id>0</id></e>", "<e><id>1</id></e>");
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertNull(b);
	}
	
	@Test
	public void test3() throws InvalidGrammarException, BuildException
	{
		Troolean.Value b;
		String formula = "G (∀ $x ∈ /e/id/text() : ( $x = 0 ))";
		XmlLtlInterpreter ali = new XmlLtlInterpreter();
		GroupProcessor gp = ali.build(formula);
		QueueSource src = new QueueSource().loop(false);
		src.setEvents("<e><id>0</id></e>", "<e><id>1</id></e>");
		ApplyFunction xml_parse = new ApplyFunction(ParseXml.instance);
		Connector.connect(src, xml_parse, gp);
		Pullable p = gp.getPullableOutput();
		b = (Troolean.Value) p.pull();
		assertNull(b);
	}
}
