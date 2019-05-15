package ca.uqac.lif.cep.polyglot.qea;

import ca.uqac.lif.cep.functions.ContextAssignment;
import ca.uqac.lif.cep.functions.Function;

public class Guard
{
  public Guard(Function condition, ContextAssignment ca)
  {
    super();
    m_condition = condition;
    m_assignment = ca;
  }

  Function m_condition;

  ContextAssignment m_assignment;

}
