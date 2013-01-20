package appixia.engine.Mediums.XML;

import appixia.engine.Mediums.Container;
import appixia.engine.Mediums.Array;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;


public class Encoder extends appixia.engine.Mediums.Encoder
{
	public void render(HttpServletResponse res, PrintWriter out, Container root)
	{
		renderHeader(res, out);
		renderField(res, out, root, null);
		renderFooter(res, out);
	}

	private void renderHeader(HttpServletResponse res, PrintWriter out)
	{
		res.setContentType("text/xml; charset=utf-8");
		out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<api>\n");
	}

	private void renderFooter(HttpServletResponse res, PrintWriter out)
	{
		out.print("</api>");
	}

	private void renderFieldHeader(HttpServletResponse res, PrintWriter out, String fieldname, String newline)
	{
		if (fieldname != null) out.print("<" + fieldname + ">" + newline);
	}

	private void renderFieldFooter(HttpServletResponse res, PrintWriter out, String fieldname, String newline)
	{
		if (fieldname != null) out.print("</" + fieldname + ">" + newline);
	}

	private void renderField(HttpServletResponse res, PrintWriter out, Object fieldvalue, String fieldname)
	{
		if (fieldvalue instanceof Array)
		{
			for (Object fieldvalue2 : ((Array)fieldvalue))
			{
				renderField(res, out, fieldvalue2, fieldname);
			}
		}
		else if (fieldvalue instanceof Container)
		{
			renderFieldHeader(res, out, fieldname, "\n");
			for (String fieldname2 : ((Container)fieldvalue).keySet())
			{
				Object fieldvalue2 = ((Container)fieldvalue).get(fieldname2);
				renderField(res, out, fieldvalue2, fieldname2);
			}
			renderFieldFooter(res, out, fieldname, "\n");
		}
		else
		{
			renderFieldHeader(res, out, fieldname, "");
			renderString(res, out, fieldvalue.toString(), "");
			renderFieldFooter(res, out, fieldname, "\n");
		}
	}

	private void renderString(HttpServletResponse res, PrintWriter out, Object object, String newline)
	{
		if (object instanceof Boolean)
		{
			out.print(object.toString() + newline);
		}
		else if (object instanceof Number)
		{
			out.print(object.toString() + newline);
		}
		else
		{
			String string = object.toString();
			string = string.replaceAll("&", "&amp;");
			string = string.replaceAll("<", "&lt;");
			string = string.replaceAll(">", "&gt;");
			string = string.replaceAll("\"", "&quot;");
			string = string.replaceAll("'", "&apos;");
			out.print(string + newline);
		}
	}

}