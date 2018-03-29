package ca.uqac.lif.cep.polyglot;

public class Util 
{
	public static String convertStreamToString(java.io.InputStream is) {
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    String out = "";
	    if (s.hasNext())
	    	out = s.next();
	    s.close();
	    return out;
	}
}
