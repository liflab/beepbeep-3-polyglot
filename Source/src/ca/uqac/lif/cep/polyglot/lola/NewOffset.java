package ca.uqac.lif.cep.polyglot.lola;

import java.util.ArrayDeque;
import java.util.Queue;

import ca.uqac.lif.cep.tmf.Source;

public class NewOffset extends Source implements Notifiable
{
	/**
	 * The name of the stream this is an offset of
	 */
	protected String m_sourceName;
	
	/**
	 * The offset to apply
	 */
	protected int m_offset;
	
	/**
	 * The default value to return when stream position is out of range
	 */
	protected Object m_defaultValue;
	
	/**
	 * The queue of incoming objects from the source stream
	 */
	protected Queue<Object> m_inputQueue;
	
	/**
	 * A counter for events received
	 */
	protected int m_eventCount = 0;
	
	public NewOffset(String source_name, int offset, Object default_value)
	{
		super(1);
		m_sourceName = source_name;
		m_offset = offset;
		m_defaultValue = default_value;
		m_inputQueue = new ArrayDeque<Object>();
		if (m_offset < 0)
		{
			for (int i = 0; i < -m_offset; i++)
			{
				m_inputQueue.add(m_defaultValue);
			}
		}
	}
	
	@Override
	public NewOffset duplicate()
	{
		return new NewOffset(m_sourceName, m_offset, m_defaultValue);
	}
	
	/**
	 * Notifies the processor of a new event produced by the source stream
	 * @param o The event
	 */
	@Override
	public void newEvent(Object o)
	{
		if (m_offset < 0 || (m_offset > 0 && m_eventCount > m_offset))
		{
			m_inputQueue.add(o);
		}
		else
		{
			m_eventCount++;
		}
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (m_offset < 0)
		{
			outputs.add(new Object[]{m_inputQueue.remove()});
		}
		return true;
	}
	
	
}
