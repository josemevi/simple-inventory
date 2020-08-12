package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class GetItemInfo
 */
@WebServlet("/getItemInfo")
public class GetItemInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    DBConnection con = new DBConnection();
	JSONObject jsonRes = new JSONObject();
       
  
    public GetItemInfo() {
        super();        
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		String item_id = request.getParameter("item_id");
		if(con.checkString(item_id)) {
			if(con.execSql("SELECT items.item_code, items.item_description, items.item_price, "
					+ "items.state_id, items_states.state_name, items.item_creation_date, items.user_creator_id, "
					+ "users.user_name, items.supplier_id, suppliers.supplier_name, suppliers.supplier_country FROM items"
					+" INNER JOIN items_states ON items.state_id = items_states.state_id "
					+ "INNER JOIN users ON items.user_creator_id = users.user_id "
					+ "INNER JOIN suppliers ON items.supplier_id = suppliers.supplier_id "
					+ "WHERE items.item_id="+item_id) == 1) {
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
		response.getWriter().print(json.toString());		
	
	}

}
