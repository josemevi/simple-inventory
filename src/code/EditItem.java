package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/editItem")
public class EditItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
        
    public EditItem() {
        super();
    }

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);		
		JSONObject json = new JSONObject();		
		if(session != null) { 
			JSONObject requestJson = con.retrieveJson(request);
			String item_id = requestJson.getString("item_id");
			String item_description = requestJson.getString("item_description");
			String item_price = requestJson.getString("item_price");
			String supplier_id = requestJson.getString("supplier_id");
			String item_img_url = requestJson.getString("item_img_url");
			if(!con.checkString(item_price)) {
				item_price = null;
			}
			if(!con.checkString(supplier_id)) {
				supplier_id = null;
			}				
			if(con.checkString(item_description) && con.checkString(item_id)) {
				String query = "UPDATE items SET item_description ="+con.simpleQuoted(item_description)+", item_price="+item_price+", supplier_id="+supplier_id
						+", item_img_url="+con.simpleQuoted(item_img_url)+"WHERE item_id="+item_id;
				if(con.execSql(query) == 1) {
					response.setStatus(200);
					json.put("msg", "Item Updated");
				}else {
					response.setStatus(500);
					json.put("msg", "Server Error");
				}
				
			}else {
				response.setStatus(400);
				json.put("msg", "Item description can't be empty or invalid item id");
			}		
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}
}