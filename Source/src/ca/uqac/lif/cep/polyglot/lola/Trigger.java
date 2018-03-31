/*
    A LOLA interpreter for BeepBeep
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

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Sink;

/**
 * A trigger is a sink that is instructed to notify another object
 * whenever it receives an input event. This is done by passing to it
 * an instance of some object that implements the {@link Triggerable}
 * interface.
 * <p>
 * As its name implies, this processor mimics the <tt>trigger</tt>
 * construct in the Lola language.
 * 
 * @author Sylvain Hallé
 */
public class Trigger extends Sink
{
	/**
	 * Object that gets notified by the trigger when it receives an input
	 */
	protected Triggerable m_toNotify;
	
	/**
	 * The current accumulated Boolean value of the events received so far
	 */
	protected boolean m_currentValue; 
	
	/**
	 * Creates a new trigger
	 * @param t The object that gets notified by the trigger when 
	 * it receives an input
	 */
	public Trigger(Triggerable t)
	{
		super(1);
		m_toNotify = t;
	}
	
	@Override
	public void reset()
	{
		m_currentValue = false;
	}
	
	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		boolean b = (Boolean) inputs[0];
		if (!m_currentValue && b)
		{
			m_toNotify.getNotified();
			m_currentValue = true;
		}
		return false;
	}
	
	@Override
	public void getInputTypesFor(/*@NotNull*/ Set<Class<?>> classes, int index)
	{
		classes.add(Boolean.class);
	}

	@Override
	public Trigger duplicate() 
	{
		return new Trigger(m_toNotify);
	}
	
	/**
	 * Object that gets notified by the trigger when it receives an input
	 * @author Sylvain Hallé
	 */
	public interface Triggerable 
	{
		/**
		 * Method called when the trigger wants to send a notification
		 */
		public void getNotified();
	}
	
	/**
	 * Simple triggerable that simply prints a message
	 */
	public static class PrintTriggerable implements Triggerable
	{
		/**
		 * A global ID counter
		 */
		private static int s_counter = 0;
		
		/**
		 * A lock for accessing the global counter
		 */
		private static final Lock s_counterLock = new ReentrantLock();
		
		/**
		 * A unique ID for this trigger
		 */
		protected int m_id;
		
		public PrintTriggerable()
		{
			super();
			s_counterLock.lock();
			m_id = s_counter++;
			s_counterLock.unlock();
		}

		@Override
		public void getNotified() 
		{
			System.out.println("Trigger #" + m_id);
		}
		
	}

	/**
	 * Sets the object to be notified by this trigger
	 * @param t The triggerable
	 */
	public void setTriggerable(Triggerable t) 
	{
		m_toNotify = t;
	}
}
