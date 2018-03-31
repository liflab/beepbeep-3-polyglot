package ca.uqac.lif.cep.polyglot.lola.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.polyglot.lola.LolaInterpreter;
import ca.uqac.lif.cep.polyglot.lola.NamedGroupProcessor;
import ca.uqac.lif.cep.tmf.QueueSource;

public class ExtendedLolaInterpreterTest 
{
	@Test
	public void testMutator() throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1);
		source.addEvent(1);
		source.addEvent(2);
		source.addEvent(3);
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = convert s1 into 1";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(1, (Number) o);
		o = pul.pull();
		assertEquals(1, (Number) o);
	}
	
	@Test
	public void testWindow() throws ParseException, ConnectorException, InvalidGrammarException, BuildException
	{
		Object o;
		QueueSource source = new QueueSource(1);
		source.addEvent(1);
		source.addEvent(2);
		source.addEvent(3);
		source.addEvent(4);
		LolaInterpreter my_int = new LolaInterpreter();
		String expression = "s2 = win(sum, s1, 2)";
		NamedGroupProcessor gp = (NamedGroupProcessor) my_int.build(expression);
		Connector.connect(source, 0, gp, gp.getInputIndex("s1"));
		Pullable pul = gp.getPullableOutput("s2");
		o = pul.pull();
		assertEquals(3f, (Number) o);
		o = pul.pull();
		assertEquals(5f, (Number) o);
		o = pul.pull();
		assertEquals(7f, (Number) o);
	}

}
