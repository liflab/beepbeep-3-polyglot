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

import java.util.Map;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Computes the sum of the values in a map
 */
@SuppressWarnings("rawtypes")
public class MapSum extends UnaryFunction<Map, Number>
{
  public static final transient MapSum instance = new MapSum();

  private MapSum()
  {
    super(Map.class, Number.class);
  }

  @Override
  public Number getValue(Map m)
  {
    float sum = 0;
    for (Object o : m.values())
    {
      if (o instanceof Function)
      {
        Object[] out = new Object[1];
        Function f = (Function) o;
        ((Function) o).evaluate(new Object[f.getInputArity()], out);
        sum += ((Number) out[0]).floatValue();
      }
      else
      {
        sum += ((Number) o).floatValue();
      }
    }
    return sum;
  }
}
