package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/*
 * At the end i decided no implement this EP, instead i used a simple button to deactivated items so no information related to states is required
 * I keep this here because if in the future is required to add some new states this will become handy 
 */
@WebServlet("/getItemsStates")
public class GetItemsStates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
	
    public GetItemsStates() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		if(con.checkSession(request.getParameter("JSESSIONID"))) { 
			String query = "SELECT * FROM items_states";
			if(con.execSql(query) == 1) {
				JSONObject jsonRes = new JSONObject("{"+con.doubleQuoted("states")+":["+con.getData()+"]}");			
				response.setStatus(200);
				json.put("statesData", jsonRes.get("states"));
			}else {
				response.setStatus(500);
				json.put("msg", "Server Error");
			}
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}
}