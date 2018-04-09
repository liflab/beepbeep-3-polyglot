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
package ca.uqac.lif.cep.polyglot.ltl;

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.dsl.GroupProcessorBuilder;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.*;
import ca.uqac.lif.cep.polyglot.AsyncFork;
import ca.uqac.lif.cep.util.Booleans;

public abstract class LtlInterpreter extends GroupProcessorBuilder {
	public LtlInterpreter() throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException {
		super();
	}
	
	protected String getGrammar() {
		return ca.uqac.lif.cep.polyglot.Util.convertStreamToString(LtlInterpreter.class.getResourceAsStream("core.bnf"));
	}
	
	@Builds(rule="<F>", pop=true, clean=true)
	public Processor handleF(Object ... procs) {
		return handleUnary(new Eventually(), procs);
	}
	
	@Builds(rule="<G>", pop=true, clean=true)
	public Processor handleG(Object ... procs) {
		return handleUnary(new Globally(), procs);
	}
	
	@Builds(rule="<X>", pop=true, clean=true)
	public Processor handleX(Object ... procs) {
		return handleUnary(new Next(), procs);
	}
	
	@Builds(rule="<U>", pop=true, clean=true)
	public Processor handleU(Object ... procs) {
		return handleBinary(new Until(), procs);
	}
	
	@Builds(rule="<and>", pop=true, clean=true)
	public Processor handleAnd(Object ... procs) {
		return handleBinary(new ApplyFunction(Booleans.and), procs);
	}
	
	@Builds(rule="<or>", pop=true, clean=true)
	public Processor handleOr(Object ... procs) {
		return handleBinary(new ApplyFunction(Booleans.or), procs);
	}
	
	@Builds(rule="<implies>", pop=true, clean=true)
	public Processor handleImplies(Object ... procs) {
		return handleBinary(new ApplyFunction(Booleans.implies), procs);
	}
	
	@Builds(rule="<not>", pop=true, clean=true)
	public Processor handleNot(Object ... procs) {
		return handleUnary(new ApplyFunction(Booleans.not), procs);
	}
	
	@Builds(rule="<num>")
	public void handleNum(java.util.ArrayDeque<Object> stack) {
		stack.push(Integer.parseInt((String) stack.pop()));
	}
	
	@Builds(rule="<C>", pop=true, clean=true)
	public Processor handleC(Object ... procs) {
		CountTrue ct = new CountTrue((Integer) procs[0]);
		Connector.connect((Processor) procs[1], ct);
		return add(ct);
	}
	
	@Builds(rule="<forall>", pop=true)
	public Processor handleForAll(Object ... args) {
		Processor phi = (Processor) args[6];
		ForAll q = new ForAll((String) args[1], (Function) args[3], phi);
		// The argument of the quantifier was connected to the source;
		// we must redirect it to the quantifier instead
		Processor src = phi.getPullableInput(0).getProcessor();
		phi.setPullableInput(0, null);
		Connector.connect(src, q);
		return add(q);
	}
	
	@Builds(rule="<exists>", pop=true)
	public Processor handleExists(Object ... args) {
		Processor phi = (Processor) args[6];
		Exists q = new Exists((String) args[1], (Function) args[3], phi);
		// The argument of the quantifier was connected to the source;
		// we must redirect it to the quantifier instead
		Processor src = phi.getPullableInput(0).getProcessor();
		phi.setPullableInput(0, null);
		Connector.connect(src, q);
		return add(q);
	}
	
	protected Processor handleUnary(Processor p, Object ... procs) {
		Connector.connect((Processor) procs[0], p);
		return add(p);
	}
	
	protected Processor handleBinary(Processor p, Object ... procs) {
		Connector.connect((Processor) procs[0], 0, p, 0);
		Connector.connect((Processor) procs[1], 0, p, 1);
		return add(p);
	}
	
	@Override
	protected AsyncFork newFork()
	{
		return new AsyncFork(0);
	}
}