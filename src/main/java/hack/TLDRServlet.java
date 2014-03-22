package hack;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/tldr")
public class TLDRServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1811282464442766846L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
		
		String query = req.getParameter("q");
		System.out.println(query);
		JSONObject data = null;
		try {
			data = IPoolAPI.getLatestArticles(query);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		doGet(req, resp);
	}
	
	
}
