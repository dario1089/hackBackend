package hack;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


public class FrontServlet extends HttpServlet {
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
		
		JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			array.put(IPoolAPI.getMostTrending("persons"));
			array.put(IPoolAPI.getMostTrending("orgs"));
			array.put(IPoolAPI.getMostTrending("geos"));
			array.put(IPoolAPI.getMostTrending("events"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		data.put("data", array);
		
		res.setContentType("application/json");
		// Get the printwriter object from response to write the required json object to the output stream      
		PrintWriter out = res.getWriter();
		// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
		out.print(data);
		out.flush();
		
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}
}
