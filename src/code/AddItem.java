package code;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/addItem")
public class AddItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
       
    public AddItem() {
        super();
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		JSONObject json = new JSONObject();		
		request.setCharacterEncoding("UTF-8");
		if(con.checkSession(request.getParameter("JSESSIONID"))) { 			
			JSONObject requestJson = con.retrieveJson(request);
			Integer user_id = requestJson.getInt("user_id");
			Integer item_code = requestJson.getInt("item_code");
			String item_description = requestJson.getString("item_description");
			Float item_price = requestJson.getFloat("item_price");
			Integer supplier_id = requestJson.getInt("supplier_id");
			String item_img_url = requestJson.getString("item_img_url");
			if(supplier_id < 0) {
				supplier_id = null;
			}
			if(item_price < 0) {
				item_price = null;
			}
			if(con.checkString(item_description)) {						
				if(!con.checkValue("items", "item_code", item_code.toString())) {
					String query = "INSERT INTO items (item_id, item_code, item_description, item_price, state_id, supplier_id, item_creation_date, "
							+ "user_creator_id, item_img_url) VALUES (DEFAULT, "+item_code+", "+con.simpleQuoted(item_description)+", "+item_price+", "
							+ "DEFAULT, "+supplier_id+", "+con.simpleQuoted(Instant.now().toString())+", "+user_id+", " 
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