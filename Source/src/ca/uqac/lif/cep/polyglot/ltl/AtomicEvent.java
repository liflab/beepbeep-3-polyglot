package ca.uqac.lif.cep.polyglot.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.ltl.TrooleanGroundTerm;

public class AtomicEvent extends TrooleanGroundTerm
{
	protected String m_name;
	
	public AtomicEvent(String name)
	{
		super();
		m_name = name;
	}

	@Override
	public Value computeValue(Object o)
	{
		if (((String) o).compareTo(m_name) == 0)
			return Value.TRUE;
		return Value.FALSE;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		AtomicEvent ae = new AtomicEvent(m_name);
		super.cloneInto(ae, with_state);
		return ae;
	}	
}
