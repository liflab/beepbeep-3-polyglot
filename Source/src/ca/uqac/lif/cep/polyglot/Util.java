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
package ca.uqac.lif.cep.polyglot;

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.util.Numbers;

public class Util 
{
	public static String convertStreamToString(java.io.InputStream is) 
	{
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    String out = "";
	    if (s.hasNext())
	    	out = s.next();
	    s.close();
	    return out;
	}
	
	public static Object tryPrimitive(Object o)
	{
		if (o instanceof String)
		{
			String s = (String) o;
			if (s.equalsIgnoreCase("true"))
				return true;
			if (s.equalsIgnoreCase("false"))
				return false;
		}
		try
		{
			return Numbers.NumberCast.getNumber(o);
		}
		catch (FunctionException e)
		{
			return o;
		}
	}
}
