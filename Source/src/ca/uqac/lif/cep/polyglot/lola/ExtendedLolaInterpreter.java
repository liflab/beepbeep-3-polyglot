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
package ca.uqac.lif.cep.polyglot.lola;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.*;
import ca.uqac.lif.cep.tmf.*;
import ca.uqac.lif.cep.util.Numbers;

/**
 * An interpreter providing extensions to LOLA 1.0.
 * 
 * @author Sylvain Hallé
 */
public class ExtendedLolaInterpreter extends LolaInterpreter
{
  /**
   * A map associating names to GroupProcessors. This is used to store the chains
   * of processors created through {@code define} statements.
   */
  protected HashMap<String, NamedGroupProcessor> m_definedBoxes = new HashMap<String, NamedGroupProcessor>();

  /**
   * The regex pattern for {@code define} statements
   */
  protected Pattern m_definePattern = Pattern.compile("define \\$(.*?)\\((.*?)\\)");

  /**
   * The regex pattern for invoking a box defined using a {@code define} statement
   */
  protected Pattern m_boxPattern = Pattern.compile("\\$(.*?)\\((.*?)\\)");

  /**
   * Creates a new instance of the extended LOLA interpreter
   * 
   * @throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException
   */
  public ExtendedLolaInterpreter() throws ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException
  {
    super(false);
    setGrammar(super.getGrammar() + "\n" + getGrammar());
  }

  @Override
  public String getGrammar()
  {
    return ca.uqac.lif.cep.polyglot.Util
        .convertStreamToString(LolaInterpreter.class.getResourceAsStream("lola-ext.bnf"));
  }

  @Override
  public GroupProcessor build(String expression) throws BuildException
  {
    java.util.Scanner scanner = new java.util.Scanner(expression);
    while (scanner.hasNextLine())
    {
      String line = scanner.nextLine().trim();
      if (line.isEmpty())
        continue;
      if (line.startsWith("define"))
      {
        String def_line = line;
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine())
        {
          String in_line = scanner.nextLine().trim();
          if (in_line.startsWith("end define"))
            break;
          sb.append(in_line).append("\n");
        }
        createNamedBox(def_line, sb.toString());
      }
      else
      {
        buildLine(line);
      }
    }
    scanner.close();
    return endOfFileVisit();
  }

  /**
   * Processes the inside of a {@code define} statement and creates a
   * {@link NamedGroupProcessor} out of it. This processor is then associated to
   * the name occurring in the {@code define} line.
   * 
   * @param def_line
   *          The first line of the {@code define} statement (the one that
   *          contains "define")
   * @param contents
   *          The inside of the {@code define} statement (may span multiple lines,
   *          excluding the one that starts with "{@code end define}")
   * @throws ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException
   */
  public void createNamedBox(String def_line, String contents)
      throws ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException
  {
    ExtendedLolaInterpreter sub_int = null;
    try
    {
      sub_int = new ExtendedLolaInterpreter();
    }
    catch (ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException e)
    {
      // Should not happen
    }
    NamedGroupProcessor gp = (NamedGroupProcessor) sub_int.build(contents);
    Matcher mat = m_definePattern.matcher(def_line);
    if (!mat.find())
    {
      throw new ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException(
          "Incorrect define statement");
    }
    String box_name = mat.group(1);
    String[] arg_list = mat.group(2).split(",");
    gp.orderInputNumbers(arg_list);
    m_definedBoxes.put(box_name.trim(), gp);
  }

  @Builds(rule = "<window>", pop = true, clean = true)
  public Processor handleWindow(Object... args)
  {
    Window w = new Window((Processor) args[0], ((Number) args[2]).intValue());
    Connector.connect((Processor) args[1], w);
    return add(w);
  }

  @Builds(rule = "<func-name>", pop = true)
  public Processor handleFunctionName(Object... args)
  {
    String name = (String) args[0];
    if (name.startsWith("$"))
    {
      String box_name = name.substring(1);
      if (m_definedBoxes.containsKey(box_name))
      {
        return m_definedBoxes.get(box_name).duplicate();
      }
    }
    if (name.compareTo("sum") == 0)
      return add(new Cumulate(new CumulativeFunction<Number>(Numbers.addition)));
    return null;
  }

  @Builds(rule = "<box-call>", pop = true)
  public Processor handleBoxCall(Object... args)
  {
    Matcher mat = m_boxPattern.matcher((String) args[0]);
    if (mat.find())
    {
      String box_name = mat.group(1);
      String[] s_names = mat.group(2).split(",");
      NamedGroupProcessor orig_ngp = (NamedGroupProcessor) m_definedBoxes.get(box_name);
      NamedGroupProcessor ngp = (NamedGroupProcessor) orig_ngp.duplicate();
      for (int i = 0; i < s_names.length; i++)
      {
        Passthrough pt = forkInput(s_names[i].trim());
        add(pt);
        Connector.connect(pt, 0, ngp, i);
      }
      return add(ngp);
    }
    return null; // Should throw an exception
  }

  @Builds(rule = "<mutator>", pop = true, clean = true)
  public Processor handleMutator(Object... args)
  {
    TurnInto cp = new TurnInto(args[1]);
    Connector.connect((Processor) args[0], cp);
    return add(cp);
  }

  @Builds(rule = "<gen-offset>", pop = true, clean = true)
  public Processor handleGenOffset(Object... args)
  {
    Passthrough pt = forkInput((String) args[0]);
    Offset off = new Offset(((Number) args[2]).intValue(), args[3]);
    CountDecimate dec = new CountDecimate(((Number) args[1]).intValue());
    Connector.connect(add(pt), add(off), add(dec));
    return dec;
  }
}
