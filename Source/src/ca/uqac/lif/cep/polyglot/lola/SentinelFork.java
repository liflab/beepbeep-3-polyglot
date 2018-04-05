package ca.uqac.lif.cep.polyglot.lola;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.cep.tmf.Fork;

public class SentinelFork extends Fork 
{
	protected Set<Notifiable> m_notifiables;
	
	public SentinelFork(int out_arity)
	{
		super(out_arity);
		m_notifiables = new HashSet<Notifiable>();
	}
	
	@Override
	public SentinelFork duplicate()
	{
		return new SentinelFork(getOutputArity());
	}
	
	public SentinelFork registerNotifiable(Notifiable not)
	{
		m_notifiables.add(not);
		return this;
	}
	
	@Override
	public boolean compute(Object[] inputs, Object[] outputs)
	{
		boolean b = super.compute(inputs, outputs);
		for (Notifiable not : m_notifiables)
		{
			not.newEvent(outputs[0]);
		}
		return b;
	}
}
