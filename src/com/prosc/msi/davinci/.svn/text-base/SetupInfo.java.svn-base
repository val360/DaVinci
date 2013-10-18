package com.prosc.msi.davinci;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: val Date: 10/23/12 Time: 6:52 PM
 */
public class SetupInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SetupInfo.class.getName());

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			DbManager.lookupDataSource();
		} catch(NamingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info(req.getMethod());
		resp.setContentType("application/json");
		final BufferedWriter out = new BufferedWriter(resp.getWriter());
		final ServletContext context = getServletContext();
		try {
			JSONObject obj = new JSONObject();
			final HttpSession session = req.getSession(false);
			final User sessionUser = (User)session.getAttribute("user");
			if(sessionUser != null) {
				JSONObject currentUser = new JSONObject();
				currentUser.put("username", sessionUser.getUsername());
				currentUser.put("region", sessionUser.getRegion());
				currentUser.put("department", sessionUser.getDepartment());
				currentUser.put("role", sessionUser.getRole().toString());
				obj.put("currentUser", currentUser);
			}
			final List<String> departments = DbManager.getDepartments(sessionUser);
			final List<String> regions = DbManager.getRegions(sessionUser);
			final List<User> users = DbManager.getUsers(false);
			final List<Field> fields = DbManager.getFields(null, null);
			final List<Calc> calcs = DbManager.getCalcs();
			final List<Metric> metrics = DbManager.getMetrics();
			JSONArray usersJson = new JSONArray();
			for(User u : users) {
				JSONObject user = new JSONObject();
				user.put("username", u.getUsername());
				user.put("email", u.getEmail());
				user.put("department", u.getDepartment());
				user.put("region", u.getRegion());
				user.put("admin", "admin".equals(u.getRole())?"on":"");
				usersJson.add(user);
			}
			obj.put("users", usersJson);
			JSONArray fieldsJson = new JSONArray();
			for(Field f : fields) {
				JSONObject field = new JSONObject();
				field.put("name", f.getName());
				field.put("type", f.getType());
				field.put("department", f.getDepartment());
				field.put("region", f.getRegion());
				field.put("id", f.getId());
				fieldsJson.add(field);
			}
			obj.put("fields", fieldsJson);
			JSONArray departmentsJson = new JSONArray();
			for(String d : departments) {
				JSONObject department = new JSONObject();
				department.put("name", d);
				departmentsJson.add(department);
			}
			obj.put("departments", departmentsJson);
			JSONArray regionsJson = new JSONArray();
			for(String r : regions) {
				JSONObject region = new JSONObject();
				region.put("name", r);
				regionsJson.add(region);
			}
			obj.put("regions", regionsJson);
			JSONArray calcsJson = new JSONArray();
			for(Calc c : calcs) {
				JSONObject calc = new JSONObject();
				calc.put("name", c.getName());
				calc.put("definition", c.getDefinition());
				calc.put("type", c.getType());
				calc.put("id", c.getId());
				calc.put("region", c.getRegion());
				calc.put("department", c.getDepartment());
				calc.put("scope", c.getScope());
				calcsJson.add(calc);
			}
			obj.put("calculations", calcsJson);
			JSONArray metricsJson = new JSONArray();
			for(Metric m : metrics) {
				JSONObject metric = new JSONObject();
				metric.put("name", m.getName());
				metric.put("value", m.getValue());
				metricsJson.add(metric);
			}
			obj.put("metrics", metricsJson);

			obj.put("success", true);
			obj.writeJSONString(out);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		} catch(NamingException e) {
			throw new RuntimeException(e);
		} finally {
			out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("POST");
		final BufferedWriter out = new BufferedWriter(resp.getWriter());
		final BufferedReader in = req.getReader();
		try {
			final Map<String,String> parsed = (Map<String,String>)JSONValue.parseWithException(in);
			final String queryString = req.getQueryString();
			if( queryString.contains("adduser") ) {
				final boolean admin = "on".equals(parsed.get("admin"));
				DbManager.addUser(parsed.get("username"), parsed.get("password"), parsed.get("region"), parsed.get("department"), parsed.get("email"), admin);
			} else if ( queryString.contains("addfield") ) {
				DbManager.addField(getServletContext(), parsed.get("name"), parsed.get("region"), parsed.get("department"), parsed.get("type"));
			} else if(queryString.contains("addcalc")) {
				DbManager.addCalc(parsed.get("name"), parsed.get("definition"), parsed.get("region"), parsed.get("department"), parsed.get("type"), parsed.get("scope"));
			} else if(queryString.contains("addmetric")) {
				DbManager.addMetric(parsed.get("name"), parsed.get("value"));
			}
			resp.setContentType("application/json");
			out.write("{ success: true }");
		} catch(Throwable e) {
			log.log(Level.SEVERE, "", e);
			throw new RuntimeException(e);
		} finally {
			in.close();
			out.close();
		}
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doHead(req, resp);
		log.info("HEAD");
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPut(req, resp);
		log.info("PUT");
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doDelete(req, resp);
		log.info("DELETE");
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doOptions(req, resp);
		log.info("OPTIONS");
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doTrace(req, resp);
		log.info("TRACE");
	}
}