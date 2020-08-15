package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/*
 * Makes use of the items_prices_reductions table to take all the values that have been assigned to a item
 */
@WebServlet("/getItemPriceReduxInfo")
public class GetItemPriceReduxInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();

    public GetItemPriceReduxInfo() {
        super();
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject json = new JSONObject();		
		if(session != null) { 
			String item_id = request.getParameter("item_id");
			if(con.checkString(item_id)) {
				String query = "SELECT prices_reductions.reduction_price, prices_reductions.reduction_start_date, "
						+ "prices_reductions.reduction_end_date FROM items_prices_reductions "
						+ "INNER JOIN prices_reductions ON items_prices_reductions.price_reduction_id = prices_reductions.reduction_id "
						+ " WHERE items_prices_reductions.item_id ="+item_id;
				if(con.execSql(query) == 1) {
					JSONObject jsonRes = new JSONObject("{"+con.doubleQuoted("info")+":["+con.getData()+"]}");			
					response.setStatus(200);
					json.put("itemPriceReduxData", jsonRes.get("info"));
				}else if(!con.checkString(con.getData())) {			
					response.setStatus(200);
					json.put("msg", "No results");
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