package appixia.engine.Mediums.JSON;

import appixia.engine.Mediums.Container;
import appixia.engine.Mediums.Array;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

// TODO: add support for on-the-fly of array encoding (at least for array parts)

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
		res.setContentType("text/javascript");
		out.print("");
	}

	private void renderFooter(HttpServletResponse res, PrintWriter out)
	{
		out.print("");
	}

	private void renderFieldHeader(HttpServletResponse res, PrintWriter out, String fieldname, String newline)
	{
		if (fieldname != null) out.print("\"" + fieldname + "\":" + newline);
	}

	private void renderFieldFooter(HttpServletResponse res, PrintWriter out, String fieldname, String newline)
	{
		if (fieldname != null) out.print(newline);
	}

	private void renderField(HttpServletResponse res, PrintWriter out, Object fieldvalue, String fieldname)
	{
		if (fieldvalue instanceof Array)
		{
			renderFieldHeader(res, out, fieldname, "");
			out.print("[");
			boolean first = true;
			for (Object fieldvalue2 : ((Array)fieldvalue))
			{
				if (first) first = false;
				else out.print(",");
				renderField(res, out, fieldvalue2, null);
			}
			out.print("]");
			renderFieldFooter(res, out, fieldname, "");
		}
		else if (fieldvalue instanceof Container)
		{
			renderFieldHeader(res, out, fieldname, "");
			out.print("{");
			boolean first = true;
			for (String fieldname2 : ((Container)fieldvalue).keySet())
			{
				if (first) first = false;
				else out.print(",");
				Object fieldvalue2 = ((Container)fieldvalue).get(fieldname2);
				renderField(res, out, fieldvalue2, fieldname2);
			}
			out.print("}");
			renderFieldFooter(res, out, fieldname, "");
		}
		else
		{
			renderFieldHeader(res, out, fieldname, "");
			renderString(res, out, fieldvalue.toString(), "");
			renderFieldFooter(res, out, fieldname, "");
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
			string = string.replaceAll("\"", "\\\"");
			out.print("\"" + string + "\"" + newline);
		}
	}

}