package code;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * This servlet creates users with the minimun necessary information. Also validates if the user email is already inside the "users" table
 */
@WebServlet("/signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBConnection con = new DBConnection();
	
       
 
    public Signup() {
        super();
       
    }
    
   public boolean checkUsername (String username) {
		if(con.execSql("SELECT user_name FROM users WHERE user_name= "+con.simpleQuoted(username)) == 1) {
			return true;
		}else {
			return false;
		}			 
   }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject requestJson = con.retrieveJson(request);
		String username = requestJson.getString("username");
		String password = requestJson.getString("password");
		String rol = requestJson.getString("rol_id");
		if(!con.checkString(rol)) {
			rol = "1";
		}
		JSONObject json = new JSONObject();		
		if(session == null) {
			if(con.checkString(username) && con.checkString(password)) {
				if(checkUsername(username)) {
					response.setStatus(400);
					json.put("msg", "Username Already In Use");
				}else {					
					if(con.execSql("INSERT INTO users values (DEFAULT, "+rol+", "+ con.simpleQuoted(username)+", "+con.simpleQuoted(password)+")") == 1){
						response.setStatus(201);
						json.put("msg", "Created");
				    }else{
				    	response.setStatus(500);
						json.put("msg", "Server error");
				    }		
				}
					
			}else {
				response.setStatus(400);
				json.put("msg", "Invalid values in important fields detected, please check all the fields");
			}
		}else {
			response.setStatus(403);
			json.put("msg", "Already logged");
		}
		
		response.getWriter().print(json.toString());
	}

}
