package com.prosc.msi.davinci;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: val Date: 10/11/12 Time: 4:45 PM
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Login.class.getName());
	private static final String LOGIN_FAIL_JSON = "{ success: false, errors: { reason: 'Login failed. Try again.' }}";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("get request");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		final BufferedWriter out = new BufferedWriter(resp.getWriter());
		final String username = req.getParameter("loginUsername");
		final String password = req.getParameter("loginPassword");

		if(username == null || password == null) {
			out.write(LOGIN_FAIL_JSON);
		} else {
			try {
				final List<User> userList = DbManager.getUsers(false);
				User currentUser = null;
				for ( User u : userList ) {
					if( username.equals(u.getUsername()) ) {
						currentUser = u;
					}
				}
				if(currentUser != null && password.equals(currentUser.getPassword())) {
					/* Success! */
					final HttpSession session = req.getSession();
					session.setAttribute("user", currentUser);
					out.write("{ success: true, role: '" + currentUser.getRole().toString() + "' }");
				} else {
					/* Fail! */
					out.write(LOGIN_FAIL_JSON);
				}
			} catch(ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch(SQLException e) {
				throw new RuntimeException(e);
			} catch(NamingException e) {
				throw new RuntimeException(e);
			}
		}
		out.close();
	}

	private void doLogout(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if(session != null) {
			session.invalidate();
		}
	}
}