package code;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/getItems")
public class GetItems extends HttpServlet {
	private static final long serialVersionUID = 1L;
    DBConnection con = new DBConnection();
    
    public GetItems() {
        super();
        
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject json = new JSONObject();	
		if(session != null) { 
			if(con.execSql("SELECT items.item_id, items.item_code, items.item_description, items.item_price, "
					+ "items.state_id ,items_states.state_name, items.item_creation_date, items.user_creator_id, "
					+ "users.user_name FROM items INNER JOIN items_states ON "
					+ "items.state_id = items_states.state_id INNER JOIN users ON "
					+ "items.user_creator_id = users.user_id") == 1) {
				JSONObject jsonRes = new JSONObject("{"+con.doubleQuoted("items")+":["+con.getData()+"]}");			
				response.setStatus(200);
				json.put("itemsData", jsonRes.get("items"));
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