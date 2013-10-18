package com.prosc.msi.davinci;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: val Date: 11/3/12 Time: 5:29 PM
 */
public class EntryInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(EntryInfo.class.getName());

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
		log.info("GET");
		resp.setContentType("application/json");
		final BufferedWriter out = new BufferedWriter(resp.getWriter());
		try {
			final HttpSession session = req.getSession(false);
			User user = null;
			if(session != null) {
				user = (User)session.getAttribute("user");
			}
			//final List<Entry> entries = DbManager.getEntries(getServletContext());
			JSONObject obj = new JSONObject();
			JSONArray fieldsJson = new JSONArray();
			final List<Field> fields = DbManager.getFields(user!=null? user.getRegion() : null, user!=null? user.getDepartment() : null);
			for(Field f:fields) {
				final JSONObject field = new JSONObject();
				field.put("name", f.getName());
				field.put("fieldId", f.getId());
				fieldsJson.add(field);
				//fieldsJson.add(f.getName());
			}
			obj.put("inputs", fieldsJson);
			obj.put("weeks", getWeeksJson());
			obj.put("success", true);
			obj.writeJSONString(out);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Could not load MySQL JDBC driver"); //FIX! don't hardcode
		} catch(Throwable t) {
			log.log(Level.SEVERE, "", t);
			throw new RuntimeException(t);
		} finally {
			out.close();
		}
	}

	private JSONArray getWeeksJson() {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int year = Integer.parseInt(sdf.format(today));
		//start entry from begging of current year
		Calendar cal = new GregorianCalendar(year, 0, 1);
		//set the calendar 1 month in the past
		//cal.setTime(today);
		//cal.add(Calendar.MONTH, -1);
		JSONArray mondays = new JSONArray();
		for(int i = 0, inc = 1; i < 366 && cal.get(Calendar.YEAR) == year; i += inc) {
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				final Date monday = cal.getTime();
				// currently no future dates are needed
				if(monday.after(today)) break;
				JSONObject obj = new JSONObject();
				final String dateString = DateFormat.getDateInstance(DateFormat.SHORT).format(monday);
				obj.put("date", dateString);
				final int number = cal.get(Calendar.WEEK_OF_YEAR);
				obj.put("number", number);
				obj.put("label", number + " - " + dateString);
				mondays.add(obj);
				cal.add(Calendar.DAY_OF_MONTH, 7);
				inc = 7;
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		return mondays;
	}
}
