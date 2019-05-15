package ca.uqac.lif.cep.polyglot.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.ltl.TrooleanGroundTerm;

public class AtomicGroundTerm extends TrooleanGroundTerm
{
  protected String m_name;

  public AtomicGroundTerm(String name)
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
    AtomicGroundTerm ae = new AtomicGroundTerm(m_name);
    super.cloneInto(ae, with_state);
    return ae;
  }

  @Override
  public String toString()
  {
    return m_name;
  }
}
