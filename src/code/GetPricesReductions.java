package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/getPricesReductions")
public class GetPricesReductions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
	
    public GetPricesReductions() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject json = new JSONObject();
		if(session != null) { 
			String query = "SELECT * FROM prices_reductions";
			if(con.execSql(query) == 1) {
				JSONObject jsonRes = new JSONObject("{"+con.doubleQuoted("prices")+":["+con.getData()+"]}");			
				response.setStatus(200);
				json.put("reduxData", jsonRes.get("prices"));
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