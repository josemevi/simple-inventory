package code;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/*
 * The reason for using the "JSESSIONID" instead of the session object for authentication is because when a external request 
 * comes by a site without any domain name associated (e.g localhost with different port) by default tomcat server
 * nulls all the cookies related to that session, that's why i re-send and re-assigned every time /login is called  
 * 
 * Testing from "Postman" or a static web site without any package manager (and of course inside the WebContent folder) will work
 * with the session object without problem.
 */

@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
    
    public Login() {
        super();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();		
		JSONObject json = new JSONObject();		
		if(!con.checkSession(request.getParameter("JSESSIONID"))) {
			JSONObject requestJson = con.retrieveJson(request);
			String username = requestJson.getString("username");
			String password = requestJson.getString("password");
			String query = "SELECT * FROM users WHERE user_name= "+con.simpleQuoted(username);
			if(con.execSql(query) == 1) {
				JSONObject result = new JSONObject(con.getData());
				if(password.equals(result.get("user_password"))) {
					session = request.getSession();
					String sessionId = session.getId();
				    Cookie userCookie = new Cookie("JSESSIONID", sessionId);
				    response.addCookie(userCookie);
					session.setAttribute("userId", result.get("user_id"));
					session.setAttribute("rolId", result.get("user_rol_id"));
					json.put("jsessionId",sessionId);
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
			String sessionId = session.getId();
		    Cookie userCookie = new Cookie("JSESSIONID", sessionId);
		    response.addCookie(userCookie);
			response.setStatus(403);
			json.put("msg", "Already Logged");
		}
		response.getWriter().print(json.toString());
	}
}