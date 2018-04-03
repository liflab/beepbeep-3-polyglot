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
package ca.uqac.lif.cep.polyglot.qea;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.dsl.MultilineGroupProcessorBuilder;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.functions.ContextAssignment;
import ca.uqac.lif.cep.functions.Function;

public class QeaInterpreter extends MultilineGroupProcessorBuilder {
	
	/**
	 * The automaton that will be built
	 */
	protected MooreMachine m_machine = new MooreMachine(1, 1);

	public QeaInterpreter() throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException {
		super();
	}
	
	protected String getGrammar() {
		return ca.uqac.lif.cep.polyglot.Util.convertStreamToString(QeaInterpreter.class.getResourceAsStream("core.bnf"));
	}
	
	@Builds(rule="<trans>")
	public void handleTrans(ArrayDeque<Object> stack)
	{
		Guard guard = (Guard) stack.pop();
		int s_to = (Integer) stack.pop();
		stack.pop(); // ->
		int s_from = (Integer) stack.pop();
		FunctionTransition trans = null;
		if (guard.m_assignment != null)
		{
			trans = new FunctionTransition(guard.m_condition, s_to, guard.m_assignment);			
		}
		else
		{
			trans = new FunctionTransition(guard.m_condition, s_to);
		}
		m_machine.addTransition(s_from, trans);
	}
	
	@Builds(rule="<guard>")
	public void handleGuard(ArrayDeque<Object> stack)
	{
		stack.pop(); // ]
		Object o = stack.peek();
		ContextAssignment ca = null;
		if (o instanceof ContextAssignment)
		{
			ca = (ContextAssignment) stack.pop();
			stack.pop(); // :
		}
		Function condition = (Function) stack.pop();
		stack.pop(); // [
		Guard g = new Guard(condition, ca);
		stack.push(g);
	}
	
	@Builds(rule="<symbol>")
	public void handleSymbol(ArrayDeque<Object> stack)
	{
		stack.pop(); // ]
		String symbol = (String) stack.pop();
		stack.pop(); // [
		int s_num = (Integer) stack.pop();
		m_machine.addSymbol(s_num, symbol);
	}
	
	@Builds(rule="<state-num>", pop=true)
	public Integer handleStateNum(Object ... args)
	{
		return Integer.parseInt((String) args[0]);
	}

	@Override
	public GroupProcessor endOfFileVisit() 
	{
		GroupProcessor gp = new GroupProcessor(1, 1);
		gp.addProcessor(m_machine);
		gp.associateInput(0,  m_machine, 0);
		gp.associateOutput(0,  m_machine, 0);
		return gp;
	}
	
}
