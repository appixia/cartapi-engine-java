package appixia.engine.Mediums;

import appixia.engine.Mediums.Container;
import appixia.engine.Mediums.Array;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

public class Encoder
{
	public Container createRoot()
	{
		return new Container();
	}

	public void render(HttpServletResponse res, PrintWriter out, Container root)
	{
		// abstract
	}

	public void addString(Container container, String fieldname, String string)
	{
		addFieldAutoArray(container, fieldname, string);
	}

	public void addNumber(Container container, String fieldname, Number number)
	{
		addFieldAutoArray(container, fieldname, number);
	}

	public void addBoolean(Container container, String fieldname, Boolean bool)
	{
		addFieldAutoArray(container, fieldname, bool);
	}

	public Container addContainer(Container container, String fieldname)
	{
		Container created = new Container();
		addFieldAutoArray(container, fieldname, created);
		return created;
	}

	public Array addArray(Container container, String fieldname)
	{
		Array created = new Array();
		container.put(fieldname, created);
		return created;
	}

	public void addStringToArray(Array array, String string)
	{
		array.add(string);
	}

	public void addNumberToArray(Array array, Number number)
	{
		array.add(number);
	}

	public void addBooleanToArray(Array array, Boolean bool)
	{
		array.add(bool);
	}

	public Container addContainerToArray(Array array)
	{
		Container created = new Container();
		array.add(created);
		return created;
	}

	public void addFieldAutoArray(Container container, String fieldname, Object fieldvalue)
	{
		if (!container.containsKey(fieldname))
		{
			container.put(fieldname, fieldvalue);
		}
		else
		{
			Object existing = container.get(fieldname);
			if (existing instanceof Array)
			{
				((Array)existing).add(fieldvalue);
			}
			else
			{
				Array array = new Array();
				array.add(existing);
				array.add(fieldvalue);
				container.put(fieldname, array);
			}
		}
	}

}