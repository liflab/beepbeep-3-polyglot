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
}
