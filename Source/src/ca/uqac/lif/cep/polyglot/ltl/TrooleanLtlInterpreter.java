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
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.dsl.GroupProcessorBuilder;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.*;
import ca.uqac.lif.cep.polyglot.AsyncFork;
import ca.uqac.lif.cep.tmf.BlackHole;

public abstract class TrooleanLtlInterpreter extends GroupProcessorBuilder {
	public TrooleanLtlInterpreter() throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException {
		super();
	}
	
	protected String getGrammar() {
		return ca.uqac.lif.cep.polyglot.Util.convertStreamToString(TrooleanLtlInterpreter.class.getResourceAsStream("core.bnf"));
	}
	
	@Builds(rule="<F>", pop=true, clean=true)
	public Processor handleF(Object ... procs) {
		return handleUnaryOperator(new Sometime(), (Processor) procs[0]);
	}
	
	@Builds(rule="<G>", pop=true, clean=true)
	public Processor handleG(Object ... procs) {
		return handleUnaryOperator(new Always(), (Processor) procs[0]);
	}
	
	@Builds(rule="<X>", pop=true, clean=true)
	public Processor handleX(Object ... procs) {
		return handleUnaryOperator(new After(), (Processor) procs[0]);
	}
	
	@Builds(rule="<U>", pop=true, clean=true)
	public Processor handleU(Object ... procs) {
		return null; // TODO return handleBinary(new Until(), procs);
	}
	
	@Builds(rule="<and>", pop=true, clean=true)
	public Processor handleAnd(Object ... procs) {
		return handleBinaryFunction(Troolean.AND_FUNCTION, (Processor) procs[0], (Processor) procs[1]);
	}
	
	@Builds(rule="<or>", pop=true, clean=true)
	public Processor handleOr(Object ... procs) {
		return handleBinaryFunction(Troolean.OR_FUNCTION, (Processor) procs[0], (Processor) procs[1]);
	}
	
	@Builds(rule="<implies>", pop=true, clean=true)
	public Processor handleImplies(Object ... procs) {
		return handleBinaryFunction(Troolean.IMPLIES_FUNCTION, (Processor) procs[0], (Processor) procs[1]);
	}
	
	@Builds(rule="<not>", pop=true, clean=true)
	public Processor handleNot(Object ... procs) {
		return handleUnaryFunction(Troolean.NOT_FUNCTION, (Processor) procs[0]);
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
	
	protected Processor handleUnaryOperator(UnaryOperator op, Processor phi) {
		Processor src = phi.getPullableInput(0).getProcessor();
		phi.setPullableInput(0, null);
		op.setProcessor(phi);
		remove(phi);
		Connector.connect(src, op);
		return add(op);
	}
	
	protected Processor handleBinaryFunction(Function f, Processor phi, Processor psi) {
		GroupProcessor gp = new GroupProcessor(1, 1);
		AsyncFork fork = new AsyncFork(2);
		ApplyFunction af = new ApplyFunction(f);
		Processor src_phi = phi.getPullableInput(0).getProcessor();
		Processor src_psi = psi.getPullableInput(0).getProcessor();
		phi.setPullableInput(0, null);
		psi.setPullableInput(0, null);
		Connector.connect(fork, 0, phi, 0);
		Connector.connect(fork, 1, psi, 0);
		Connector.connect(phi, 0, af, 0);
		Connector.connect(psi, 0, af, 1);
		BlackHole bh = new BlackHole();
		gp.addProcessors(fork, phi, psi, af, bh);
		gp.associateInput(0, fork, 0);
		gp.associateOutput(0, af, 0);
		Connector.connect(src_phi, 0, gp, 0);
		Connector.connect(src_psi, 0, bh, 0);
		remove(phi, psi);
		add(bh);
		return add(gp);
	}
	
	protected Processor handleUnaryFunction(Function f, Processor phi) {
		GroupProcessor gp = new GroupProcessor(1, 1);
		ApplyFunction af = new ApplyFunction(f);
		Connector.connect(phi, 0, af, 0);
		gp.addProcessors(phi, af);
		gp.associateInput(0, phi, 0);
		gp.associateOutput(0, af, 0);
		Connector.connect(phi.getPullableInput(0).getProcessor(), 0, gp, 0);
		phi.getPullableInput(0).getProcessor().setPushableOutput(0, null);
		phi.setPullableInput(0, null);
		remove(phi);
		return add(gp);
	}
	
	@Override
	protected AsyncFork newFork()
	{
		return new AsyncFork(0);
	}
}