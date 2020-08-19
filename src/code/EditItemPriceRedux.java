package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Edits the field "price_reduction" from items table, i decided to implement a flag called
 * "remove" to use this EP to remove the current price reduction id assigned as well 
 * 
 * This also inserts into "items_prices_reductions" a many to many table relation between items and prices_reductions
 * the corresponded values in order to keep track of all the assigned prices reductions to a item
 */
@WebServlet("/editItemPriceRedux")
public class EditItemPriceRedux extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
       
    public EditItemPriceRedux() {
        super();
    }

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		JSONObject json = new JSONObject();		
		if(con.checkSession(request.getParameter("JSESSIONID"))) {
			JSONObject requestJson = con.retrieveJson(request);
			Integer item_id = requestJson.getInt("item_id");
			String reduction_id = requestJson.getString("reduction_id");
			Boolean remove = requestJson.getBoolean("remove");			
			String query = "UPDATE items SET reduction_id="+reduction_id+" WHERE item_id="+item_id;
			String query2 = "INSERT INTO items_prices_reductions VALUES(DEFAULT,"+item_id+", "+reduction_id+")";
			if(!remove) {					
				if(con.checkString(reduction_id) && con.execSql(query) == 1 && con.execSql(query2) == 1) {
					response.setStatus(200);
					json.put("msg", "Price Reduction Edited");	
				}else {
					response.setStatus(500);
					json.put("msg", "Server Error, check price reduction id");
				}
			}else {
				query = "UPDATE items SET reduction_id=null WHERE item_id="+item_id;
				if(con.execSql(query) == 1) {
					response.setStatus(200);
					json.put("msg", "Price Reduction Removed");	
				}else {
					response.setStatus(500);
					json.put("msg", "Server Error");
				}
			}				
		}else {
			response.setStatus(403);
			json.put("msg", "Must Sign In first");
		}
		response.getWriter().print(json.toString());
	}
}