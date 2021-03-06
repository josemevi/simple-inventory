package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Edits the field "item_state" from the items table, i decided to implement a switch making sure that the
 * id of "Discontinued" state is always 1, this is because if in the future we desire to add extra state we could simple use
 * the same EP for all of them also adding extra cases to the switch statement if more actions are required.
 * 
 * Actually this EP can be use to assign any of the id in "items_states". The discontinued "1" also inserts into 
 * "items_deactivations" the extra data required when a item is deactivated
 */
@WebServlet("/editItemState")
public class EditItemState extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();  
    
    public EditItemState() {
        super();
      
    }

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		if(con.checkSession(request.getParameter("JSESSIONID"))) {
			JSONObject requestJson = con.retrieveJson(request);
			Integer user_id = requestJson.getInt("user_id");
			String item_id = requestJson.getString("item_id");
			String state_id = requestJson.getString("state_id");
			String deactivation_reason = requestJson.getString("deactivation_reason");
			if(con.checkString(state_id) && con.checkString(item_id)) {
				String query = "UPDATE items SET state_id="+state_id+" WHERE item_id="+item_id;
				String query2 = "INSERT INTO items_deactivations VALUES (DEFAULT, "+con.simpleQuoted(deactivation_reason)+", "
						+user_id+", "+item_id+")";
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
				json.put("msg", "Missing state id or item id or user id");
			}			
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}
}