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

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.util.Numbers;

public class ExtendedLolaInterpreter extends LolaInterpreter
{

	public ExtendedLolaInterpreter() throws InvalidGrammarException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* TODO: arranger window
	@Builds(rule="<window>", pop=true, clean=true)
	public void handleWindow(Object ... args) {
		stack.pop(); // )
		int width = ((Number) stack.pop()).intValue();
		stack.pop(); // ,
		Processor p = (Processor) stack.pop();
		stack.pop(); // ,
		Processor func_p = (Processor) stack.pop();
		stack.pop(); // (
		stack.pop(); // win
		Window w = new Window(func_p, width);
		Connector.connect(p, w);
		stack.push(w);
	}
	*/
	
	@Builds(rule="<func-name>", pop=true)
	public Processor handleFunctionName(Object ... args) {
		if (((String) args[0]).compareTo("sum") == 0)
			return add(new Cumulate(new CumulativeFunction<Number>(Numbers.addition)));
		return null;
	}
	
	/*
	@Builds(rule="<mutator>", pop=true, clean=true)
	public Processor handleMutator(Object ... args) {
		TurnInto cp = new TurnInto(args[1]);
		Connector.connect((Processor) args[0], cp);
		return add(cp);
	}
	*/


}
