package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/addPriceRedux")
public class AddPriceRedux extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
 
    public AddPriceRedux() {
        super();      
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		JSONObject json = new JSONObject();		
		if(con.checkSession(request.getParameter("JSESSIONID"))) {
			JSONObject requestJson = con.retrieveJson(request);
			Float reduction_price = requestJson.getFloat("reduction_price");
			String reduction_start_date = requestJson.getString("reduction_start_date");
			String reduction_end_date = requestJson.getString("reduction_end_date");
			if(reduction_price != null && con.checkString(reduction_start_date) && con.checkString(reduction_end_date)) {
				String query = "INSERT INTO prices_reductions VALUES (DEFAULT, "+reduction_price+", "+con.simpleQuoted(reduction_start_date)+", "
						+con.simpleQuoted(reduction_end_date)+")";
				if(con.execSql(query) == 1) {
					response.setStatus(201);
					json.put("msg", "Price Reduction Added");	
				}else {
					response.setStatus(500);
					json.put("msg", "Server Error");
				}
			}else {
				response.setStatus(400);
				json.put("msg", "Invalid values in important fields detected, please check all the fields");
			}
			
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}
}