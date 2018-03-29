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
