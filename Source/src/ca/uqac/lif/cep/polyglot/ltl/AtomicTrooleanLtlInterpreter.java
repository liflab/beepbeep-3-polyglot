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
import ca.uqac.lif.cep.tmf.Passthrough;

public class AtomicTrooleanLtlInterpreter extends TrooleanLtlInterpreter
{

  public AtomicTrooleanLtlInterpreter()
      throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException
  {
    super();
    setGrammar(super.getGrammar() + "\n" + getGrammar());
  }

  @Override
  protected String getGrammar()
  {
    return ca.uqac.lif.cep.polyglot.Util
        .convertStreamToString(LtlInterpreter.class.getResourceAsStream("atomic.bnf"));
  }

  @Builds(rule = "<atom>")
  public void handleAtom(java.util.ArrayDeque<Object> stack)
  {
    String s = (String) stack.pop();
    AtomicGroundTerm ae = new AtomicGroundTerm(s);
    Passthrough pt = forkInput(0);
    Connector.connect(pt, ae);
    add(pt);
    stack.push(add(ae));
  }
}
