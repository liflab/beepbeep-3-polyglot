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
import java.util.Iterator;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SynchronousProcessor;

public class Offset extends SynchronousProcessor 
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
	public OffsetPullable getPullableOutput(int index)
	{
		if (m_outputPullables[0] == null)
		{
			m_outputPullables[0] = new OffsetPullable();
		}
		return (OffsetPullable) m_outputPullables[0];
	}

	@Override
	public Offset duplicate(boolean with_state)
	{
		Offset o = new Offset(m_offset, m_defaultValue);
		if (with_state)
		{
			o.m_bufferedOutputs.addAll(m_bufferedOutputs);
			o.m_eventCount = m_eventCount;
		}
		return o;
	}

	protected class OffsetPullable implements Pullable
	{	
		@Override
		public Iterator<Object> iterator()
		{
			return this;
		}

		@Override
		public Object pullSoft()
		{
			return pull();
		}

		@Override
		public Object pull() 
		{
			if (m_offset < 0)
			{
				if (m_eventCount < -m_offset)
				{
					m_eventCount++;
					return m_defaultValue;
				}
				else if (m_eventCount == -m_offset)
				{
					m_eventCount++;
					return m_inputPullables[0].pull();
				}
				else
				{
					return m_inputPullables[0].pull();
				}
			}
			else if (m_offset > 0 && m_eventCount < m_offset)
			{
				for (int i = m_eventCount; i < m_offset; i++)
				{
					// Discard the first m_offset events
					m_inputPullables[0].pullSoft();
					m_eventCount++;
				}
				return m_inputPullables[0].pull();
			}
			else
			{
				return m_inputPullables[0].pull();
			}
		}

		@Override
		public Object next() 
		{
			return pull();
		}

		@Override
		public NextStatus hasNextSoft() 
		{
			if (m_offset < 0 && m_eventCount < -m_offset)
				return NextStatus.YES;
			if (!m_bufferedOutputs.isEmpty())
				return NextStatus.YES;
			if (m_offset > 0 && m_eventCount < m_offset)
			{
				for (int i = m_eventCount; i < m_offset; i++)
				{
					// Discard the first m_offset events
					m_inputPullables[0].pullSoft();
					m_eventCount++;
				}
			}
			return m_inputPullables[0].hasNextSoft();
		}

		@Override
		public boolean hasNext() 
		{
			if (m_offset < 0 && m_eventCount < -m_offset)
			{
				return true;
			}
			if (!m_bufferedOutputs.isEmpty())
			{
				return true;
			}
			if (m_offset > 0 && m_eventCount < m_offset)
			{
				for (int i = m_eventCount; i < m_offset; i++)
				{
					// Discard the first m_offset events
					m_inputPullables[0].pull();
					m_eventCount++;
				}
			}
			return m_inputPullables[0].hasNext();
		}

		@Override
		public Processor getProcessor()
		{
			return Offset.this;
		}

		@Override
		public int getPosition() 
		{
			return 0;
		}

		@Override
		public void start() 
		{
			Offset.this.start();
		}

		@Override
		public void stop() 
		{
			Offset.this.stop();
		}

		@Override
		public void dispose()
		{
			// Nothing to do
		}
	}
}
