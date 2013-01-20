package appixia.engine;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.HashMap;
import java.util.Enumeration;

import appixia.engine.Helpers;
import appixia.engine.Mediums.Encoder;
import appixia.engine.Mediums.Container;

public class Engine
{
	public static String REQUEST_METADATA_FIELDS[] = {"X-OPERATION","X-VERSION","X-TOKEN","X-FORMAT","X-LANGUAGE","X-CURRENCY","X-FORMFACTOR"};

	// returns null on error
	public static Request handleRequest(HttpServletRequest req, HttpServletResponse res, PrintWriter out)
	{
		Request request = new Request();

		// parse the request metadata
		request.metadata = getRequestMetadata(req);

		// parse the request post data (if found)
		request.data = new Container();
		/*
		$post_data = CartAPI_Engine::getRequestPostData();
		if ($post_data !== false)
		{
			$decoder = CartAPI_Engine::getDecoder($request['metadata']['X-FORMAT']);
			if ($decoder !== false) $request['data'] = $decoder->parse($post_data);
		}
		*/

		// override with parameters passed on the URL
		parseUrlRequestData(req, request.data);

		// prepare an encoder for the response
		request.encoder = getEncoder(request.metadata.get("X-FORMAT"));
		if (request.encoder == null) return null;

		// do some sanity checking
		if (!request.metadata.containsKey("X-OPERATION")) Helpers.dieOnError(res, out, request.encoder, "IncompleteMetadata", "X-OPERATION missing from metadata");
		
		return request;
	}

	public static Encoder getEncoder(String medium)
	{
		return (Encoder)_newMediumClass(medium, "Encoder");
	}

	/*
	public static Decoder getDecoder(String medium)
	{
		return (Decoder)_newMediumClass(medium, "Decoder");
	}
	*/

	public static HashMap<String,String> getRequestMetadata(HttpServletRequest req)
	{
		HashMap<String,String> res = new HashMap<String,String>();

		// defaults
		res.put("X-FORMAT", "XML");
		res.put("X-VERSION", "1");
		res.put("X-LANGUAGE", "en");
		res.put("X-CURRENCY", "USD");

		// first look in HTTP headers
		for (String field : REQUEST_METADATA_FIELDS)
		{
			// first check in HTTP headers
			String from_header = req.getHeader(field);
			if (from_header != null) res.put(field, from_header);

			// override with URL
			String from_url = req.getParameter(field);
			if (from_url != null) res.put(field, from_url);
		}

		return res;
	}

	public static void parseUrlRequestData(HttpServletRequest req, Container request_data)
	{
		Enumeration param_names = req.getParameterNames();
		while (param_names.hasMoreElements())
		{
			String param_name = (String)param_names.nextElement();
			String param_value = req.getParameter(param_name);

			// handle containers
			String[] name_parts = param_name.split("[\\[\\]]");
			Container cur_container = request_data;
			for (int i=0; i<name_parts.length; i++)
			{
				if (i == (name_parts.length-1)) cur_container.put(name_parts[i], param_value);
				else
				{
					Container sub_container = (Container)cur_container.get(name_parts[i]);
					if (sub_container == null) 
					{
						sub_container = new Container();
						cur_container.put(name_parts[i], sub_container);
					}
					cur_container = sub_container;
				}
			}
		}
	}

	/*
	// return null if none
	public static function getRequestPostData()
	{
		if ($_SERVER["REQUEST_METHOD"] != "POST") return false;
		$post_data = file_get_contents("php://input");
		if (($post_data === false) || empty($post_data)) return false;
		return $post_data;
	}
	*/

	// return null on failure
	private static Object _newMediumClass(String medium, String class_type)
	{
		ClassLoader class_loader = Encoder.class.getClassLoader();
		try
		{
			Class class_object = class_loader.loadClass("appixia.engine.Mediums." + medium + "." + class_type);
			return class_object.newInstance();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}