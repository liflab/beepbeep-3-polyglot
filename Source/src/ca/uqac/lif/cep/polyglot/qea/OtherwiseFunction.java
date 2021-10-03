package ca.uqac.lif.cep.polyglot.qea;

import java.util.Set;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.EventTracker;

public class OtherwiseFunction extends Function
{

  @Override
  public Function duplicate(boolean with_state)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void evaluate(Object[] inputs, Object[] outputs, Context context, EventTracker tracker)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public int getInputArity()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getOutputArity()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int index)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public Class<?> getOutputTypeFor(int index)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
