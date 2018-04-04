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

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Strings;
import ca.uqac.lif.cep.xml.XPathFunction;

public class XmlQeaInterpreter extends QeaInterpreter {

	public XmlQeaInterpreter() throws InvalidGrammarException {
		super();
		setGrammar(super.getGrammar() + "\n" + getGrammar());
	}
	
	@Override
	protected String getGrammar() {
		return ca.uqac.lif.cep.polyglot.Util.convertStreamToString(QeaInterpreter.class.getResourceAsStream("xml.bnf"));
	}
	
	@Builds(rule="<x-equals>", pop=true, clean=true)
	public FunctionTree handleXEquals(Object ... args) {
		String cons = (String) args[1];
		XPathFunction xpf = (XPathFunction) args[0];
		return new FunctionTree(Equals.instance,
				new FunctionTree(Strings.toString, new FunctionTree(Bags.anyElement, new FunctionTree(xpf, StreamVariable.X))), new Constant(cons));
	}
	
	@Builds(rule="<xpathfct>")
	public void handleXPath(java.util.ArrayDeque<Object> stack) {
		XPathFunction xpf = new XPathFunction((String) stack.pop());
		stack.push(xpf);
	}

}
