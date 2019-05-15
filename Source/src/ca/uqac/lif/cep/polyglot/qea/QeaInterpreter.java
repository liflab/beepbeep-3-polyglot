/*
    Multiple interpreters for BeepBeep
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
package ca.uqac.lif.cep.polyglot.qea;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.*;
import ca.uqac.lif.cep.fsm.*;
import ca.uqac.lif.cep.functions.*;
import ca.uqac.lif.cep.tmf.*;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;

public class QeaInterpreter extends ca.uqac.lif.cep.dsl.MultilineGroupProcessorBuilder
{

  /**
   * The automaton that will be built
   */
  protected MooreMachine m_machine = new MooreMachine(1, 1);

  /**
   * If the specification contains quantifiers, the innermost "slice" processor.
   * This is the one that will be passed the Moore machine as its argument once it
   * is built.
   */
  protected Slice m_innerSlice = null;

  /**
   * The outermost GroupProcessor built so far
   */
  protected GroupProcessor m_outerProcessor = null;

  public QeaInterpreter() throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException
  {
    super();
  }

  protected String getGrammar()
  {
    return ca.uqac.lif.cep.polyglot.Util
        .convertStreamToString(QeaInterpreter.class.getResourceAsStream("core.bnf"));
  }

  @Builds(rule = "<trans>")
  public void handleTrans(ArrayDeque<Object> stack)
  {
    Guard guard = (Guard) stack.pop();
    int s_to = (Integer) stack.pop();
    stack.pop(); // ->
    int s_from = (Integer) stack.pop();
    FunctionTransition trans = null;
    if (guard.m_assignment != null)
    {
      if (guard.m_condition instanceof OtherwiseFunction)
      {
        trans = new TransitionOtherwise(s_to, guard.m_assignment);
      }
      else
      {
        trans = new FunctionTransition(guard.m_condition, s_to, guard.m_assignment);
      }
    }
    else
    {
      if (guard.m_condition instanceof OtherwiseFunction)
      {
        trans = new TransitionOtherwise(s_to);
      }
      else
      {
        trans = new FunctionTransition(guard.m_condition, s_to);
      }
    }
    m_machine.addTransition(s_from, trans);
  }

  @Builds(rule = "<guard>")
  public void handleGuard(ArrayDeque<Object> stack)
  {
    stack.pop(); // ]
    Object o = stack.peek();
    ContextAssignment ca = null;
    if (o instanceof ContextAssignment)
    {
      ca = (ContextAssignment) stack.pop();
      stack.pop(); // :
    }
    Function condition = (Function) stack.pop();
    stack.pop(); // [
    Guard g = new Guard(condition, ca);
    stack.push(g);
  }

  @Builds(rule = "<symbol>")
  public void handleSymbol(ArrayDeque<Object> stack)
  {
    stack.pop(); // ]
    Function symbol = (Function) stack.pop();
    stack.pop(); // [
    int s_num = (Integer) stack.pop();
    m_machine.addSymbol(s_num, symbol);
  }

  @Builds(rule = "<state-num>", pop = true)
  public Integer handleStateNum(Object... args)
  {
    return Integer.parseInt((String) args[0]);
  }

  @Builds(rule = "<asg>", pop = true)
  public ContextAssignment handleAsg(Object... args)
  {
    return new ContextAssignment(((ContextVariable) args[0]).getName(), (Function) args[2]);
  }

  @Builds(rule = "<initial-asg>")
  public void handleAsg(ArrayDeque<Object> stack)
  {
    Constant r_value = (Constant) stack.pop();
    stack.pop(); // :=
    ContextVariable c_var = (ContextVariable) stack.pop();
    m_machine.addInitialAssignment(new ContextAssignment(c_var.getName(), r_value));
  }

  @Builds(rule = "<and>", pop = true, clean = true)
  public Function handleAnd(Object... args)
  {
    return new FunctionTree(Booleans.and, (Function) args[0], (Function) args[1]);
  }

  @Builds(rule = "<or>", pop = true, clean = true)
  public Function handleOr(Object... args)
  {
    return new FunctionTree(Booleans.or, (Function) args[0], (Function) args[1]);
  }

  @Builds(rule = "<const>", pop = true)
  public Constant handleConst(Object... args)
  {
    return new Constant(ca.uqac.lif.cep.polyglot.Util.tryPrimitive(args[0]));
  }

  @Builds(rule = "<var>", pop = true)
  public Function handleVar(Object... args)
  {
    // Remove the "$" in front
    return new ContextVariable(((String) args[0]).substring(1));
  }

  @Builds(rule = "<plus>", pop = true, clean = true)
  public Function handlePlus(Object... args)
  {
    return new FunctionTree(Numbers.addition, (Function) args[0], (Function) args[1]);
  }

  @Builds(rule = "<otherwise>", pop = true)
  public Function handleOtherwise(Object... args)
  {
    return new OtherwiseFunction();
  }

  @Builds(rule = "<sum>")
  public void handleSum(ArrayDeque<Object> stack)
  {
    slice(MapSum.instance, stack);
  }

  @Builds(rule = "<avg>")
  public void handleAverage(ArrayDeque<Object> stack)
  {
    slice(MapAverage.instance, stack);
  }

  @Builds(rule = "<forall>")
  public void handleForAll(ArrayDeque<Object> stack)
  {
    slice(MapAnd.instance, stack);
  }

  @Builds(rule = "<exists>")
  public void handleExists(ArrayDeque<Object> stack)
  {
    slice(MapOr.instance, stack);
  }

  protected void slice(Function f, ArrayDeque<Object> stack)
  {
    Function dom_fct = (Function) stack.pop();
    stack.pop(); // in
    @SuppressWarnings("unused")
    ContextVariable var = (ContextVariable) stack.pop();
    stack.pop(); // avg
    Processor to_surround = null;
    if (m_innerSlice == null)
    {
      m_innerSlice = new Slice(dom_fct, new Passthrough(1));
      m_innerSlice.explodeCollections(true);
      to_surround = m_innerSlice;
    }
    else
      to_surround = m_outerProcessor;
    GroupProcessor gp = new GroupProcessor(1, 1);
    ApplyFunction af = new ApplyFunction(f);
    Connector.connect(to_surround, af);
    gp.addProcessors(gp, af);
    gp.associateInput(0, to_surround, 0);
    gp.associateOutput(0, af, 0);
    m_outerProcessor = gp;
  }

  @Override
  public GroupProcessor endOfFileVisit()
  {
    if (m_innerSlice == null)
    {
      GroupProcessor gp = new GroupProcessor(1, 1);
      gp.addProcessor(m_machine);
      gp.associateInput(0, m_machine, 0);
      gp.associateOutput(0, m_machine, 0);
      return gp;
    }
    else
    {
      // Quantifiers surrounding the FSM
      m_innerSlice.setProcessor(m_machine);
      return m_outerProcessor;
    }
  }
}
