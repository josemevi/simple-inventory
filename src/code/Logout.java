package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();	
 
    public Logout() {
        super();
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		HttpSession session = request.getSession();
		JSONObject json = new JSONObject();
		if(con.checkSession(request.getParameter("JSESSIONID"))) {
			session.invalidate();
			response.setStatus(200);
		}else {
			response.setStatus(400);
			json.put("msg", "Must Sign In First");
		}
		response.getWriter().println(json.toString());
	}
}