package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/getItemInfo")
public class GetItemInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    DBConnection con = new DBConnection();
       
  
    public GetItemInfo() {
        super();        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();		
		response.setCharacterEncoding("UTF-8");
		if(con.checkSession(request.getParameter("JSESSIONID"))) { 
			String item_id = request.getParameter("item_id");
			if(con.checkString(item_id)) {
				String query = "SELECT items.item_code, items.item_description, items.item_price, "
						+ "items.state_id, items_states.state_name, items.item_creation_date, items.user_creator_id, "
						+ "items.item_img_url, items.reduction_id, users.user_name, items.supplier_id, suppliers.supplier_name, suppliers.supplier_country FROM items"
						+" INNER JOIN items_states ON items.state_id = items_states.state_id "
						+ "INNER JOIN users ON items.user_creator_id = users.user_id "
						+ "LEFT JOIN suppliers ON items.supplier_id = suppliers.supplier_id "
						+ "WHERE items.item_id="+item_id;
				if(con.execSql(query) == 1) {
					JSONObject jsonRes = new JSONObject("{"+con.doubleQuoted("items")+":["+con.getData()+"]}");			
					response.setStatus(200);
					json.put("itemsData", jsonRes.get("items"));
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