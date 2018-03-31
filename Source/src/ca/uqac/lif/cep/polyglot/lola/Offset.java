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
package ca.uqac.lif.cep.polyglot.lola;

import java.util.ArrayDeque;
import java.util.Queue;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.SingleProcessor;

public class Offset extends SingleProcessor 
{
	protected int m_offset;

	protected Object m_defaultValue;

	protected int m_eventCount;

	protected Queue<Object[]> m_bufferedOutputs;

	public Offset(int offset, Object default_value)
	{
		super(1, 1);
		m_offset = offset;
		m_defaultValue = default_value;
		m_eventCount = 0;
		m_bufferedOutputs = new ArrayDeque<Object[]>();
	}

	@Override
	public void reset()
	{
		m_eventCount = 0;
		m_bufferedOutputs.clear();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		if (m_offset < 0)
		{
			if (m_eventCount < -m_offset)
			{
				m_eventCount++;
				m_bufferedOutputs.add(inputs);
				outputs.add(new Object[]{m_defaultValue});
			}
			else if (m_eventCount == -m_offset)
			{
				m_eventCount++;
				outputs.addAll(m_bufferedOutputs);
				outputs.add(new Object[]{inputs[0]});
			}
			else
			{
				outputs.add(new Object[]{inputs[0]});
			}
		}
		else if (m_offset > 0 && m_eventCount < m_offset)
		{
			m_eventCount++;
		}
		else
		{
			outputs.add(new Object[]{inputs[0]});
		}
		return true;
	}

	@Override
	public Offset duplicate()
	{
		return new Offset(m_offset, m_defaultValue);
	}
}
