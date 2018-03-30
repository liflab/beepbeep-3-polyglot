/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;

public class CountTrue extends UniformProcessor
{
	/**
	 * The number of time value "True" has been seen
	 */
	protected int m_count = 0;
	
	/**
	 * The number of times "True" must be seen to return true
	 */
	protected int m_refCount = 0;
	
	public CountTrue(int num)
	{
		super(1, 1);
		m_refCount = num;
	}

	@Override
	public CountTrue duplicate()
	{
		return new CountTrue(m_refCount);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) 
	{
		if (m_count >= 0 && (Boolean) inputs[0])
		{
			m_count++;
		}
		if (m_count == m_refCount)
		{
			outputs[0] = Troolean.Value.TRUE;
			m_count = -1;
		}
		else
		{
			outputs[0] = Troolean.Value.FALSE;
		}
		return true;
	}
	
	@Override
	public void reset()
	{
		m_count = 0;
	}
}
