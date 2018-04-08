package ca.uqac.lif.cep.polyglot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.dsl.GroupProcessorBuilder;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.io.ReadStringStream;
import ca.uqac.lif.cep.polyglot.lola.ExtendedLolaInterpreter;
import ca.uqac.lif.cep.polyglot.ltl.AtomicTrooleanLtlInterpreter;
import ca.uqac.lif.cep.polyglot.ltl.XmlLtlInterpreter;
import ca.uqac.lif.cep.polyglot.qea.AtomicQeaInterpreter;
import ca.uqac.lif.cep.polyglot.qea.XmlQeaInterpreter;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.util.FindPattern;
import ca.uqac.lif.cep.xml.ParseXml;
import ca.uqac.lif.util.CliParser;
import ca.uqac.lif.util.CliParser.Argument;
import ca.uqac.lif.util.CliParser.ArgumentMap;

public class Polyglot 
{
	public static final int ERR_ARGS = 1;
	public static final int ERR_NOT_FOUND = 2;
	public static final int ERR_GRAMMAR = 3;
	public static final int ERR_SYNTAX = 4;

	public static void main(String[] args)
	{
		CliParser parser = setupCli();
		ArgumentMap arg_map = parser.parse(args);
		List<String> filenames = arg_map.getOthers();
		if (!arg_map.hasOption("quiet"))
		{
			printBanner();
		}
		if (!arg_map.hasOption("in-format"))
		{
			System.err.println("ERROR: input format must be specified with option --in-format");
			System.exit(ERR_ARGS);
		}
		String in_format = arg_map.getOptionValue("in-format");
		ReadStringStream reader = null;
		if (arg_map.hasOption("stdin"))
		{
			if (filenames.size() < 1)
			{
				System.err.println("ERROR: at least one specification file must be given");
				System.exit(ERR_ARGS);
			}
			reader = new ReadStringStream(System.in);
			reader.setIsFile(false);
		}
		else
		{
			if (filenames.size() < 2)
			{
				System.err.println("ERROR: at least one input file and one specification file must be given");
				System.exit(ERR_ARGS);
			}
			String trace_filename = filenames.get(0);
			try 
			{
				reader = new ReadStringStream(new BufferedInputStream(new FileInputStream(new File(trace_filename))));
				reader.setIsFile(true);
			} 
			catch (FileNotFoundException e)
			{
				System.err.println("ERROR: file " + trace_filename + " not found");
				System.exit(ERR_NOT_FOUND);
			}
		}
		Pump pump = new Pump(0);
		Thread pump_thread = new Thread(pump);
		Connector.connect(reader, pump);
		Processor source = null;
		if (in_format.equalsIgnoreCase("xml"))
		{
			FindPattern feeder = new FindPattern("(<event>.*?</event>)");
			ApplyFunction parse_xml = new ApplyFunction(ParseXml.instance);
			Connector.connect(pump, feeder, parse_xml);
			source = parse_xml;
		}
		if (in_format.equalsIgnoreCase("atomic"))
		{
			FindPattern feeder = new FindPattern("(.*?),");
			Connector.connect(pump, feeder);
			source = feeder;
		}
		for (int i = 1; i < filenames.size(); i++)
		{
			String spec_filename = filenames.get(i);
			Processor p = null;
			try
			{
				p = getProcessor(spec_filename, in_format);
			} 
			catch (InvalidGrammarException e) 
			{
				System.err.println("ERROR: invalid grammar");
				System.exit(ERR_GRAMMAR);
			} 
			catch (FileNotFoundException e)
			{
				System.err.println("ERROR: file " + spec_filename + " not found");
				System.exit(ERR_NOT_FOUND);
			}
			catch (BuildException e) 
			{
				System.err.println("ERROR: incorrect syntax in " + spec_filename);
				System.exit(ERR_SYNTAX);
			}
			if (p == null)
			{
				System.err.println("ERROR: incorrect syntax in " + spec_filename);
				System.exit(ERR_SYNTAX);
			}
			Connector.connect(source, p);
			source = p;
		}
		Print print = new Print();
		Connector.connect(source, print);
		pump_thread.start();
	}
	
	public static Processor getProcessor(String filename, String in_format) throws InvalidGrammarException, FileNotFoundException, BuildException
	{
		String[] file_parts = filename.split("\\.");
		String extension = file_parts[file_parts.length - 1];
		String spec = Util.convertStreamToString(new FileInputStream(filename));
		GroupProcessorBuilder interpreter = null;
		if (extension.equalsIgnoreCase("qea"))
		{
			if (in_format.equalsIgnoreCase("atomic"))
			{
				interpreter = new AtomicQeaInterpreter(); 
			}
			else if (in_format.equalsIgnoreCase("xml"))
			{
				interpreter = new XmlQeaInterpreter();
			}
		}
		else if (extension.equalsIgnoreCase("ltlfo"))
		{
			if (in_format.equalsIgnoreCase("atomic"))
			{
				interpreter = new AtomicTrooleanLtlInterpreter(); 
			}
			else if (in_format.equalsIgnoreCase("xml"))
			{
				interpreter = new XmlLtlInterpreter();
			}
		}
		else if (extension.equalsIgnoreCase("lola"))
		{
			if (in_format.equalsIgnoreCase("atomic"))
			{
				interpreter = new ExtendedLolaInterpreter(); 
			}
			else if (in_format.equalsIgnoreCase("xml"))
			{
				return null;
			}
		}
		return interpreter.build(spec);
	}

	protected static CliParser setupCli()
	{
		CliParser parser = new CliParser();
		parser.addArgument(new Argument().withLongName("in-format").withArgument("f").withDescription("Sets input format to f"));
		parser.addArgument(new Argument().withLongName("stdin").withDescription("Read input from stdin"));
		parser.addArgument(new Argument().withLongName("quiet").withShortName("q").withDescription("Don't print start banner"));
		return parser;
	}
	
	protected static void printBanner()
	{
		System.out.println("Polyglot: a multi-interpreter for BeepBeep 3\n(C) 2018 Laboratoire d'informatique formelle\nUniversité du Québec à Chicoutimi, Canada\n");
	}

}
