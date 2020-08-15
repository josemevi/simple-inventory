package code;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
    
    public Login() {
        super();
   
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);		
		JSONObject json = new JSONObject();		
		if(session == null) {
			JSONObject requestJson = con.retrieveJson(request);
			String username = requestJson.getString("username");
			String password = requestJson.getString("password");
			String query = "SELECT user_id, user_name, user_password, user_rol_id FROM users WHERE user_name= "+con.simpleQuoted(username);
			if(con.execSql(query) == 1) {
				JSONObject result = new JSONObject(con.getData());
				if(password.equals(result.get("user_password"))) {
					response.setStatus(200);					
					session = request.getSession();
					session.setAttribute("userId", result.get("user_id"));
					session.setAttribute("rolId", result.get("user_rol_id"));
					json.put("user_id", result.get("user_id"));
					json.put("rol_id", result.get("user_rol_id"));				
				}else {
					response.setStatus(401);
					json.put("msg", "invalid credentials");
				}
			} else {
				response.setStatus(401);
				json.put("msg", "invalid credentials");
			}			
		}else {
			response.setStatus(403);
			json.put("msg", "Already Logged");
		}
		response.getWriter().print(json.toString());
	}
}