package appixia.engine;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.HashMap;

import appixia.engine.Mediums.Encoder;
import appixia.engine.Mediums.Container;
import appixia.engine.Mediums.Array;

public class Helpers
{
	public static Container createSuccessResponse(Encoder encoder)
	{
		Container root = encoder.createRoot();
		Container result = encoder.addContainer(root, "Result");
		encoder.addBoolean(result, "Success", Boolean.TRUE);
		return root;
	}

	public static Container createSuccessResponseWithPaging(Encoder encoder, Container paging_request, int total_elements)
	{
		Container root = encoder.createRoot();
		Container result = encoder.addContainer(root, "Result");
		encoder.addBoolean(result, "Success", Boolean.TRUE);
		Container paging = encoder.addContainer(root, "Paging");
		encoder.addNumber(paging, "PageNumber", new Integer(intValue(paging_request.get("PageNumber"))));
		encoder.addNumber(paging, "ElementsPerPage", new Integer(intValue(paging_request.get("ElementsPerPage"))));

		if (total_elements >= 0)
		{
			encoder.addNumber(paging, "TotalPages", new Integer((int)Math.ceil((double)total_elements / floatValue(paging_request.get("ElementsPerPage")))));
			encoder.addNumber(paging, "TotalElements", new Integer(total_elements));
		}
		
		return root;
	}

	public static void dieOnError(HttpServletResponse res, PrintWriter out, Encoder encoder, String error, String message)
	{
		Container root = encoder.createRoot();
		Container result = encoder.addContainer(root, "Result");
		encoder.addBoolean(result, "Success", Boolean.FALSE);
		Array details = encoder.addArray(result, "Detail");
		Container detail = encoder.addContainerToArray(details);
		encoder.addString(detail, "Error", error);
		encoder.addString(detail, "Message", message);
		encoder.render(res, out, root);
		throw new Error("Die requested");
	}

	public static int getZbOffsetFromPagingRequest(Container paging_request)
	{
		int page_number = intValue(paging_request.get("PageNumber"));
		int elements_per_page = intValue(paging_request.get("ElementsPerPage"));
		int first_index_zb = (page_number - 1) * elements_per_page;
		return first_index_zb;
	}

	public static void validatePagingRequest(HttpServletResponse res, PrintWriter out, Encoder encoder, Object paging_request_object)
	{
		if (!(paging_request_object instanceof Container)) Helpers.dieOnError(res, out, encoder, "InvalidRequest", "PagingRequest is invalid");
		Container paging_request = (Container)paging_request_object;
		if (!paging_request.containsKey("PageNumber")) Helpers.dieOnError(res, out, encoder, "IncompleteRequest", "PagingRequest.PageNumber missing");
		Object page_number = paging_request.get("PageNumber");
		if (!isNumeric(page_number)) Helpers.dieOnError(res, out, encoder, "InvalidRequest", "PagingRequest.PageNumber not numeric");
		if (intValue(page_number) < 1) Helpers.dieOnError(res, out, encoder, "PageOutOfBounds", "PagingRequest.PageNumber below 1");
		if (!paging_request.containsKey("ElementsPerPage")) Helpers.dieOnError(res, out, encoder, "IncompleteRequest", "PagingRequest.ElementsPerPage missing");
		Object elements_per_page = paging_request.get("ElementsPerPage");
		if (!isNumeric(elements_per_page)) Helpers.dieOnError(res, out, encoder, "InvalidRequest", "PagingRequest.ElementsPerPage not numeric");
		if (intValue(elements_per_page) < 1) Helpers.dieOnError(res, out, encoder, "PageSizeInvalid", "PagingRequest.ElementsPerPage below 1");
	}

	public static void validateFilter(HttpServletResponse res, PrintWriter out, Encoder encoder, Object filter_object, HashMap<String,String> db_field_name_map)
	{
		if (!(filter_object instanceof Container)) Helpers.dieOnError(res, out, encoder, "InvalidRequest", "Filter is invalid");
		Container filter = (Container)filter_object;
		if (!filter.containsKey("Field")) Helpers.dieOnError(res, out, encoder, "IncompleteRequest", "Filter.Field missing");
		if (!filter.containsKey("Relation")) Helpers.dieOnError(res, out, encoder, "IncompleteRequest", "Filter.Relation missing");
		if (!filter.containsKey("Value")) Helpers.dieOnError(res, out, encoder, "IncompleteRequest", "Filter.Value missing");
		if (db_field_name_map != null)
		{
			if (!db_field_name_map.containsKey(filter.get("Field").toString())) Helpers.dieOnError(res, out, encoder, "UnsupportedFilter", filter.get("Field").toString()+" filter is unsupported");
		}
	}
	
	public static boolean isNumeric(Object object)  
	{  
		try  
		{  
			double d = Double.parseDouble(object.toString());
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}
		return true;
	}

	public static int intValue(Object object)
	{
		return (int)floatValue(object);
	}

	public static double floatValue(Object object)
	{
		double d = 0;
		try  
		{  
			d = Double.parseDouble(object.toString());
		}  
		catch(NumberFormatException nfe) {}
		return d;
	}

}