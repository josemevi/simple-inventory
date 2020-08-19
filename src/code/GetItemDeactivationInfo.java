package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/getItemDeactivationInfo")
public class GetItemDeactivationInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
	
    public GetItemDeactivationInfo() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();		
		if(con.checkSession(request.getParameter("JSESSIONID"))) { 
			String item_id = request.getParameter("item_id");
			if(con.checkString(item_id)) {
				String query = "SELECT items_deactivations.deactivation_reason, items_deactivations.deactivation_id, "
						+ "items_deactivations.user_deactivation_id, users.user_name FROM items_deactivations "
						+ "INNER JOIN users ON items_deactivations.user_deactivation_id = users.user_id "
						+ "WHERE item_id="+item_id;
				if(con.execSql(query) == 1) {
					JSONObject jsonRes = new JSONObject("{"+con.doubleQuoted("info")+":["+con.getData()+"]}");			
					response.setStatus(200);
					json.put("itemDeactivationData", jsonRes.get("info"));
				}else {			
					response.setStatus(500);
					json.put("msg", "Server Error");
				}
			}else {
				response.setStatus(400);
				json.put("msg", "Missing item id");
			}
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}
}