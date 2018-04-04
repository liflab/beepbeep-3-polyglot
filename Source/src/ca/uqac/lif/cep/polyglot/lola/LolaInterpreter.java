/*
    A LOLA interpreter for BeepBeep
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
package ca.uqac.lif.cep.polyglot.lola;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.*;
import ca.uqac.lif.cep.tmf.*;
import ca.uqac.lif.cep.util.Numbers;

public class LolaInterpreter extends ca.uqac.lif.cep.dsl.MultilineGroupProcessorBuilder
{
	/**
	 * The set of named triggers registered with this interpreter
	 */
	protected HashMap<String,Trigger> m_triggers = new HashMap<String,Trigger>();
	
	/**
	 * A set of streams that are explicitly declared as outputs
	 */
	protected HashSet<String> m_declaredOutputs = new HashSet<String>();

	public LolaInterpreter() throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException {
		super();
		setGrammar(getGrammar());
	}
	
	protected LolaInterpreter(boolean b) throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException {
		super();
	}
	
	public String getGrammar() {
		return ca.uqac.lif.cep.polyglot.Util.convertStreamToString(LolaInterpreter.class.getResourceAsStream("lola-core.bnf"));
	}

	@Builds(rule="<constant-stream>")
	public void handleConstant(ArrayDeque<Object> stack) {
		Object value = stack.pop();
		QueueSource qs = new QueueSource(1);
		qs.addEvent(value);
		stack.push(add(qs));
	}

	@Builds(rule="<gt>", pop=true, clean=true)
	public Processor handleGt(Object ... args) {
		ApplyFunction fp = new ApplyFunction(Numbers.isGreaterThan);
		Connector.connect((Processor) args[0], 0, fp, 0);
		Connector.connect((Processor) args[1], 0, fp, 1);
		return add(fp);
	}

	@Builds(rule="<lt>", pop=true, clean=true)
	public Processor handleLt(Object ... args) {
		ApplyFunction fp = new ApplyFunction(Numbers.isLessThan);
		Connector.connect((Processor) args[0], 0, fp, 0);
		Connector.connect((Processor) args[1], 0, fp, 1);
		return add(fp);
	}

	@Builds(rule="<if-then-else>", pop=true, clean=true)
	public Processor handleIte(Object ... args) {
		ApplyFunction fp = new ApplyFunction(IfThenElse.instance);
		Connector.connect((Processor) args[0], 0, fp, 0);
		Connector.connect((Processor) args[1], 0, fp, 1);
		Connector.connect((Processor) args[2], 0, fp, 2);
		return add(fp);
	}
	
	@Builds(rule="<number>", pop=true)
	public Number handleNumber(Object ... args) {
		return Integer.parseInt((String) args[0]);
	}
	
	@Builds(rule="<boolean>", pop=true)
	public boolean handleBoolean(Object ... args) {
		return "true".equalsIgnoreCase((String) args[0]);
	}
	
	@Builds(rule="<output>") 
	public void handleOutput(ArrayDeque<Object> stack) {
		String name = (String) stack.pop();
		stack.pop(); // output
		m_declaredOutputs.add(name);
	}

	@Builds(rule="<name>", pop=true)
	public Processor handleName(Object ... args) {
		return add(forkInput((String) args[0]));
	}

	@Builds(rule="<offset>", pop=true, clean=true)
	public Processor handleOffset(Object ... args) {
		Offset off = new Offset(((Number) args[1]).intValue(), args[2]);
		Passthrough pt = forkInput((String) args[0]);
		Connector.connect(pt, off);
		return add(off);
	}

	@Builds(rule="<plus>", pop=true, clean=true)
	public Processor handlePlus(Object ... args) {
		ApplyFunction fp = new ApplyFunction(Numbers.addition);
		Connector.connect((Processor) args[0], 0, fp, 0);
		Connector.connect((Processor) args[1], 0, fp, 1);
		return add(fp);
	}

	@Builds(rule="<stream-def>")
	public void handleStreamDefinition(ArrayDeque<Object> stack) {
		Processor p = (Processor) stack.pop();
		stack.pop(); // =
		String name = (String) stack.pop();
		Connector.connect(p, add(getFork(name)));
	}
	
	@Builds(rule="<trigger>")
	public void handleTrigger(ArrayDeque<Object> stack) {
		Processor p = (Processor) stack.pop();
		String name = (String) stack.pop();
		Trigger t = new Trigger(new Trigger.PrintTriggerable());
		addTrigger(name, t);
		Connector.connect(p, t);
		stack.push(t);
	}
	
	/**
	 * Adds a trigger to the interpreter
	 * @param name The name of the trigger
	 * @param t The trigger
	 */
	public void addTrigger(String name, Trigger t) {
		m_triggers.put(name, t);
	}

	/**
	 * Gets the trigger of given name
	 * @param name The name of the trigger
	 * @return The trigger, or {@code null} if no such name exists
	 */
	public Trigger getTrigger(String name) {
		return m_triggers.get(name);
	}

	/**
	 * Associates a callback to a predefined trigger
	 * @param name The name of the LOLA trigger
	 * @param t The {@link Triggerable} object that will be called
	 *   when the trigger turns true
	 */
	public void setTriggerable(String name, Trigger.Triggerable t) {
		Trigger trig = m_triggers.get(name);
		trig.setTriggerable(t);
	}
	
	@Override
	public synchronized GroupProcessor endOfFileVisit() {
		HashMap<String,Fork> m_ins = new HashMap<String,Fork>();
		HashMap<String,Fork> m_outs = new HashMap<String,Fork>();
		for (Map.Entry<Object,Fork> entry : m_inputForks.entrySet()) {
			Fork f = entry.getValue();
			if (f.getPullableInput(0) == null) {
				// Fork's input connected to nothing
				m_ins.put((String) entry.getKey(), f);
			}
			if (f.getOutputArity() == 0 || m_declaredOutputs.contains(entry.getKey()) ) {
				// Fork's output connected to nothing
				m_outs.put((String) entry.getKey(), f);
			}
		}
		NamedGroupProcessor gp = new NamedGroupProcessor(m_ins.size(), m_outs.size());
		gp.notifySources(true);
		for (Processor in_p : m_processors)
			gp.addProcessor(in_p);
		int i = 0;
		for (Map.Entry<String,Fork> entry : m_ins.entrySet()) {
			gp.associateInput(i, entry.getValue(), 0);
			gp.setInputName(i, entry.getKey());
			gp.addProcessor(entry.getValue());
			i++;
		}
		i = 0;
		for (Map.Entry<String,Fork> entry : m_outs.entrySet()) {
			Fork f = entry.getValue();
			f.extendOutputArity(1);
			Passthrough pt = new Passthrough();
			Connector.connect(f, pt);
			gp.addProcessors(f, pt);
			gp.associateOutput(i, pt, 0);
			gp.setOutputName(i, entry.getKey());
			i++;
		}
		return gp;
	}
}
