package ca.uqac.lif.cep.polyglot;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.Fork;

public class AsyncFork extends Fork
{

  public AsyncFork(int out_arity)
  {
    super(out_arity);
  }

  @Override
  public Pullable getPullableOutput(int index)
  {
    if (m_outputPullables[index] == null)
    {
      m_outputPullables[index] = new AsyncPullable(index);
    }
    return m_outputPullables[index];
  }

  @Override
  public AsyncFork duplicate(boolean with_state)
  {
    return new AsyncFork(getOutputArity());
  }

  protected class AsyncPullable implements Pullable
  {
    protected int m_index;

    public AsyncPullable(int index)
    {
      super();
      m_index = index;
    }

    @Override
    public Iterator<Object> iterator()
    {
      return this;
    }

    @Override
    public Object pullSoft()
    {
      if (!m_outputQueues[m_index].isEmpty())
      {
        return m_outputQueues[m_index].remove();
      }
      Object o = m_inputPullables[0].pullSoft();
      if (o != null)
      {
        for (int i = 0; i < m_outputQueues.length; i++)
        {
          m_outputQueues[i].add(o);
        }
      }
      if (!m_outputQueues[m_index].isEmpty())
      {
        return m_outputQueues[m_index].remove();
      }
      return null;
    }

    @Override
    public Object pull()
    {
      if (!m_outputQueues[m_index].isEmpty())
      {
        return m_outputQueues[m_index].remove();
      }
      Object o = m_inputPullables[0].pull();
      if (o != null)
      {
        for (int i = 0; i < m_outputQueues.length; i++)
        {
          m_outputQueues[i].add(o);
        }
      }
      if (!m_outputQueues[m_index].isEmpty())
      {
        return m_outputQueues[m_index].remove();
      }
      throw new NoSuchElementException();
    }

    @Override
    public Object next()
    {
      return pull();
    }

    @Override
    public NextStatus hasNextSoft()
    {
      if (!m_outputQueues[m_index].isEmpty())
      {
        return NextStatus.YES;
      }
      return m_inputPullables[0].hasNextSoft();
    }

    @Override
    public boolean hasNext()
    {
      if (!m_outputQueues[m_index].isEmpty())
      {
        return true;
      }
      return m_inputPullables[0].hasNext();
    }

    @Override
    public Processor getProcessor()
    {
      return AsyncFork.this;
    }

    @Override
    public int getPosition()
    {
      return m_index;
    }

    @Override
    public void start()
    {
      AsyncFork.this.start();
    }

    @Override
    public void stop()
    {
      AsyncFork.this.stop();
    }

    @Override
    public void dispose()
    {
      // Nothing to do
    }
  }
}
