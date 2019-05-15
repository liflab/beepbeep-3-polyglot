package ca.uqac.lif.cep.polyglot.lola;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;

/**
 * A {@link GroupProcessor} whose inputs and outputs can have names instead of
 * numbers.
 * 
 * @author Sylvain Hallé
 */
public class NamedGroupProcessor extends GroupProcessor
{
  /**
   * Names associated to input pipes
   */
  Map<String, Integer> m_inNames;

  /**
   * Names associated to output pipes
   */
  Map<String, Integer> m_outNames;

  public NamedGroupProcessor(int in_arity, int out_arity)
  {
    super(in_arity, out_arity);
    m_inNames = new HashMap<String, Integer>();
    m_outNames = new HashMap<String, Integer>();
  }

  @Override
  public NamedGroupProcessor duplicate(boolean with_state)
  {
    NamedGroupProcessor ngp = new NamedGroupProcessor(getInputArity(), getOutputArity());
    cloneInto(ngp, with_state);
    ngp.m_inNames.putAll(m_inNames);
    ngp.m_outNames.putAll(m_outNames);
    return ngp;
  }

  public NamedGroupProcessor setInputName(int index, String name)
  {
    m_inNames.put(name, index);
    return this;
  }

  public NamedGroupProcessor setOutputName(int index, String name)
  {
    m_outNames.put(name, index);
    return this;
  }

  /**
   * Gets a pullable object for the output pipe of given name
   * 
   * @param name
   *          The name of the output pipe
   * @return The {@link Pullable} object
   */
  public Pullable getPullableOutput(String name)
  {
    return getPullableOutput(m_outNames.get(name));
  }

  /**
   * Gets a pullable object for the output pipe of given name
   * 
   * @param name
   *          The name of the output pipe
   * @return The {@link Pullable} object
   */
  public Pushable getPushableInput(String name)
  {
    return getPushableInput(m_inNames.get(name));
  }

  public int getInputIndex(String name)
  {
    return m_inNames.get(name);
  }

  public int getOutputIndex(String name)
  {
    return m_outNames.get(name);
  }

  public void connectInputTo(Processor p, int i, String name)
  {
    Connector.connect(p, i, this, m_inNames.get(name));
  }

  public void orderInputNumbers(String... names)
  {
    HashMap<String, Integer> new_map = new HashMap<String, Integer>();
    for (Map.Entry<String, Integer> entry : m_inNames.entrySet())
    {
      new_map.put(names[entry.getValue()], entry.getValue());
    }
    m_inNames = new_map;
  }
}
