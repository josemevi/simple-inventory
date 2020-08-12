package code;

import java.io.IOException;
import java.time.Instant;

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
@WebServlet("/addItem")
public class AddItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
       
    public AddItem() {
        super();
    }
    
    public boolean checkItemCode(String code) {
    	if(con.execSql("SELECT item_code FROM items WHERE item_code="+code) == 1) {
    		return true;
    	}else {
    		return false;
    	}
    }

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject requestJson = con.retrieveJson(request);
		JSONObject json = new JSONObject();
		String item_code = requestJson.getString("item_code");
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
		if(session != null) { 
			if(con.checkString(item_code) && con.checkString(item_description)) {						
				if(!checkItemCode(item_code)) {
					String query = "INSERT INTO items (item_id, item_code, item_description, item_price, state_id, supplier_id, item_creation_date, "
							+ "user_creator_id, item_img_url) VALUES (DEFAULT, "+item_code+", "+con.simpleQuoted(item_description)+", "+item_price+", "
							+ "DEFAULT, "+con.simpleQuoted(supplier_id)+", "+con.simpleQuoted(Instant.now().toString())+", "+session.getAttribute("userId")+", " 
							+con.simpleQuoted(item_img_url)+")";
					if(con.execSql(query) == 1) {
						response.setStatus(201);
						json.put("msg", "Item Created");
					}else {
						response.setStatus(500);
						json.put("msg", "Server Error");
					}
				}else {
					response.setStatus(400);
					json.put("msg", "Item code already exist");
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
