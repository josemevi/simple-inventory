package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * 
 */
@WebServlet("/editItemState")
public class EditItemState extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();

       
    
    public EditItemState() {
        super();
      
    }

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject requestJson = con.retrieveJson(request);
		JSONObject json = new JSONObject();
		String item_id = requestJson.getString("item_id");
		String state_id = requestJson.getString("state_id");
		String deactivation_reason = requestJson.getString("deactivation_reason");
		if(session != null) {
			if(con.checkString(state_id) && con.checkString(item_id)) {
				String query = "UPDATE items SET state_id="+state_id+" WHERE item_id="+item_id;
				String query2 = "INSERT INTO items_deactivations VALUES (DEFAULT, "+con.simpleQuoted(deactivation_reason)+", "
						+session.getAttribute("userId")+", "+item_id+")";
				switch (state_id) {				
					case "1":
						if(con.checkString(deactivation_reason)) {
							if(con.execSql(query) == 1 && con.execSql(query2) == 1) {
								response.setStatus(200);
								json.put("msg", "Item deactivated");					
							}else {
								response.setStatus(500);
								json.put("msg", "Server Error");
							}
						}else {
							response.setStatus(500);
							json.put("msg", "Deactivation reason can't be empty");
						}
					break;
					default: 
						if(con.execSql(query) == 1) {
							response.setStatus(200);
							json.put("msg", "State changed");					
						}else {
							response.setStatus(500);
							json.put("msg", "Server Error");
						}
					break;
				}		
			}else {
				response.setStatus(400);
				json.put("msg", "Missing state id or item id");
			}			
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}

}
