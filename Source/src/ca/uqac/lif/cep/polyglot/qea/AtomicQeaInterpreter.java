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

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Equals;

public class AtomicQeaInterpreter extends QeaInterpreter 
{
	public AtomicQeaInterpreter() throws InvalidGrammarException
	{
		super();
		setGrammar(super.getGrammar() + "\n" + getGrammar());
	}
	
	@Override
	protected String getGrammar() {
		return ca.uqac.lif.cep.polyglot.Util.convertStreamToString(QeaInterpreter.class.getResourceAsStream("atomic.bnf"));
	}
	
	@Builds(rule="<atom>", pop=true)
	public Function handleAtom(Object ... args)
	{
		return new FunctionTree(Equals.instance, new Constant((String) args[0]), StreamVariable.X);
	}
}
