package com.prosc.msi.davinci;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prosc.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.naming.NamingException;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: val Date: 10/18/12 Time: 4:28 PM
 */
public class EntryData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(EntryData.class.getName());

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("DELETE");
		super.doDelete(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("OPTIONS");
		super.doOptions(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("PUT");
		super.doPut(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("HEAD");
		super.doHead(req, resp);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("TRACE");
		super.doTrace(req, resp);
	}

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
			JSONObject obj = new JSONObject();
			final HttpSession session = req.getSession(false);
			if(session != null) {
				final User user = (User)session.getAttribute("user");
				if(user != null) {
					obj.put("entries", writeJsonEntries(user));
					JSONObject currentUser = new JSONObject();
					currentUser.put("username", user.getUsername());
					currentUser.put("region", user.getRegion());
					currentUser.put("department", user.getDepartment());
					currentUser.put("role", user.getRole().toString());
					obj.put("currentUser", currentUser);
					obj.put("success", true);
				}
			}
			//log.info(obj.toJSONString());
			obj.writeJSONString(out);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Could not load MySQL JDBC driver"); //FIX! don't hardcode
		} catch(Throwable e) {
			log.log(Level.SEVERE, "", e);
			throw new RuntimeException(e);
		} finally {
			out.close();
		}
	}

	private JSONArray writeJsonEntries(User user) throws SQLException, ClassNotFoundException, NamingException, ScriptException {
		final List<String> departments = DbManager.getDepartments(user);
		final List<String> regions = DbManager.getRegions(user);
		final List<Calc> calcs = DbManager.getCalcs();
		JSONArray entriesJson = new JSONArray();
		for(String d : departments) {
			JSONObject deptHeader = new JSONObject();
			deptHeader.put("entry", d); //entryJson name
			deptHeader.put("header", "department");
			deptHeader.put("department", d);
			entriesJson.add(deptHeader);
			for(String r : regions) {
				JSONObject regHeader = new JSONObject();
				regHeader.put("entry", r); //entryJson name
				regHeader.put("header", "region");
				regHeader.put("department", r);
				entriesJson.add(regHeader);
				final List<Field> fields = DbManager.getFields(r, d);
				for(Field f : fields) {
					final List<Entry> entries = DbManager.getEntriesForFieldByID(f);
					if(entries.size() > 0) {
						JSONObject entryJson = new JSONObject();
						final Entry firstEntry = entries.get(0);
						entryJson.put("entry", firstEntry.getName());
						entryJson.put("type", firstEntry.getType());
						entryJson.put("region", firstEntry.getRegion());
						entryJson.put("department", firstEntry.getDepartment());
						entryJson.put("editable", true);
						entryJson.put("fieldId", firstEntry.getFieldId());
						for(Entry e : entries) {
							//e.getType().contains("dollar") ? e.getValueCurrencyFormatted() : e.getValue()
							entryJson.put("week" + e.getWeek(), e.getValue());
						}
						entriesJson.add(entryJson);
					}
				}
				//region footer
				// region calcs
			}
			final List<Field> fields = DbManager.getDistinctFieldsByNameForDepartment(d);
			final List<Metric> metrics = DbManager.getMetrics();
			List<Calculable> fieldsAndMetrics = new ArrayList<Calculable>();
			fieldsAndMetrics.addAll(fields);
			fieldsAndMetrics.addAll(metrics);

			//department footer

			// department calcs
			boolean addDeptCalcHeader = true;
			for(Calc c : calcs) {
				String definition = c.getDefinition().toLowerCase();
				//parse def, right now only handles two arguments
				LinkedList<Calculable> argumentList = new LinkedList<Calculable>();
				argumentList.add(0, null);
				argumentList.add(1, null);
				boolean doBreak = false;
				for(Calculable o : fieldsAndMetrics) {
					final int i = definition.indexOf(o.getName().toLowerCase());
					if(i != -1) {
						if( i == 0 ) {
							argumentList.set(0, o);
						} else {
							argumentList.set(1, o);
						}
						definition = definition.replace(o.getName().toLowerCase(), "arg" + i);
						//exiting early if already have two arguments
						if(!argumentList.contains(null)) {
							break;
						}
					}
				}
				if(argumentList.contains(null)) continue;
				String operator = null; //only a single operator is supported
				// only +,-,*,/ are supported
				if( definition.contains("+") ) {
					operator = "+";
				} else if( definition.contains("-") ) {
					operator = "-";
				} else if(definition.contains("*")) {
					operator = "*";
				} else if(definition.contains("/")) {
					operator = "/";
				}
				if(operator == null) continue;
				final List<Entry> calcEntries = DbManager.calcQueryForDepartment(argumentList.get(0), argumentList.get(1), operator, d);
				if(calcEntries.size() > 0) {
					if(addDeptCalcHeader) {
						JSONObject deptCalcHeader = new JSONObject();
						deptCalcHeader.put("entry", d + " Calculations"); //entryJson name
						deptCalcHeader.put("header", "department");
						deptCalcHeader.put("department", d);
						entriesJson.add(deptCalcHeader);
						addDeptCalcHeader = false;
					}
					JSONObject entryJson = new JSONObject();
					entryJson.put("entry", c.getName());
					entryJson.put("type", c.getType());
					entryJson.put("region", c.getRegion());
					entryJson.put("department", c.getDepartment());
					for(Entry e : calcEntries) {
						final String weekString = "week" + e.getWeek();
						if(entryJson.containsKey(weekString)) {
							entryJson.put(weekString, (Double)entryJson.get(weekString) + e.getValue());
						} else {
							//e.getType().contains("dollar") ? e.getValueCurrencyFormatted() : e.getValue()
							entryJson.put(weekString, e.getValue());
						}
					}
					entriesJson.add(entryJson);
				}
			}

			// totals
			//final List<Field> fields = DbManager.getDistinctFieldsByNameForDepartment(d);
			boolean addDeptTotalsHeader = true;
			for(Field f : fields) {
				final List<Entry> departmentEntries = DbManager.getEntriesForFieldDepartment(getServletContext(), f.getName(), d);
				if(departmentEntries.size()> 0) {
					if(addDeptTotalsHeader) {
						JSONObject deptTotalsHeader = new JSONObject();
						deptTotalsHeader.put("entry", d + " Totals"); //entryJson name
						deptTotalsHeader.put("header", "department");
						deptTotalsHeader.put("department", d);
						entriesJson.add(deptTotalsHeader);
						addDeptTotalsHeader = false;
					}
					final Entry firstEntry = departmentEntries.get(0);
					JSONObject entryJson = new JSONObject();
					entryJson.put("entry", firstEntry.getName() + " Total");
					entryJson.put("type", firstEntry.getType());
					entryJson.put("region", firstEntry.getRegion());
					entryJson.put("department", firstEntry.getDepartment());
					for(Entry e : departmentEntries) {
						final String weekString = "week" + e.getWeek();
						if(entryJson.containsKey(weekString)) {
							entryJson.put(weekString, (Double)entryJson.get(weekString) + e.getValue());
						} else {
							//e.getType().contains("dollar") ? e.getValueCurrencyFormatted() : e.getValue()
							entryJson.put(weekString, e.getValue());
						}
					}
					entriesJson.add(entryJson);
				}
			}
		}
		return entriesJson;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if( req.getParameterMap().containsKey("download") ) {
			//final BufferedWriter out = new BufferedWriter(resp.getWriter());
			final ServletOutputStream stream = resp.getOutputStream();
			try {
				final List<Entry> list = DbManager.getEntriesSorted();
				final File file = writeXSLX(list);
				//headers necessary for file download
				resp.setHeader("Cache-control", "cache, must-revalidate");
				resp.setHeader("Pragma", "public");
				//resp.setHeader("Content-Type", "application/vnd.ms-excel");
				resp.setHeader("Content-Type", "application/octet-stream");
				resp.setHeader("Content-Disposition", "attachment; filename=\"data.xlsx\"");
				resp.setHeader("Content-Transfer-Encoding", "binary");
				resp.setHeader("Content-Length", file.length() + "");
				IOUtils.writeInputToOutput(new FileInputStream(file), stream, IOUtils.CHUNK_SIZE);
			} catch(ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch(SQLException e) {
				throw new RuntimeException(e);
			} catch(NamingException e) {
				throw new RuntimeException(e);
			} finally {
				stream.close();
			}
		} else if( req.getParameterMap().containsKey("action") ) {
			resp.setContentType("application/json");
			final BufferedWriter writer = new BufferedWriter(resp.getWriter());
			try {
				final String action = req.getParameter("action");
				final String dataString = req.getParameter("data");
				final JsonObject data = new JsonParser().parse(dataString).getAsJsonObject();
				final String fieldName = data.get("entry").getAsString();
				final String region = data.get("region").getAsString();
				final String department = data.get("department").getAsString();
				final String fieldId = data.get("fieldId").getAsString();
				final String value = req.getParameter("value");
				final String originalValue = req.getParameter("originalValue");
				final String weekString = req.getParameter("field");
				final Short week = Short.parseShort(weekString.substring("week".length()));
				try{
					if("insert".equals(action)) {
						DbManager.addEntry(Double.parseDouble(value), fieldName, fieldId, week, new Date());
					} else if("update".equals(action)) {
						DbManager.updateEntry(Double.parseDouble(value), fieldName, fieldId, week, new Date());
					}
				} catch(ClassNotFoundException e) {
					throw new RuntimeException(e);
				} catch(SQLException e) {
					throw new RuntimeException(e);
				}
				writer.write("{ success: true, msg: 'Entry added.' }");
			} catch(Throwable t) {
				log.log(Level.SEVERE, "", t);
				throw new RuntimeException(t);
			} finally {
				writer.close();
			}
		} else {
			resp.setContentType("application/json");
			final BufferedWriter out = new BufferedWriter(resp.getWriter());
			try {
				final String weekParam = req.getParameter("week");
				final String[] split = weekParam.split(" - ");
				final String weekNum = split[0];
				final String weekDateString = split[1];
				final Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse(weekDateString);
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				final int week = calendar.get(Calendar.WEEK_OF_MONTH);
				final Enumeration<String> names = req.getParameterNames();
				while( names.hasMoreElements() ) {
					final String param = names.nextElement();
					if( !"week".equals(param) ) {
						try {
							final String[] strings = param.split(" --- ");
							final String fieldName = strings[0];
							final String fieldId= strings[1];
							DbManager.addEntry(Double.parseDouble(req.getParameter(param)), fieldName, fieldId, Integer.parseInt(weekNum), date);
						} catch(ClassNotFoundException e) {
							throw new RuntimeException(e);
						} catch(SQLException e) {
							throw new RuntimeException(e);
						}
					}
				}
				out.write("{ success: true, msg: 'Entry added.' }");
			} catch(Throwable t) {
				log.log(Level.SEVERE, "", t);
				throw new RuntimeException(t);
			} finally {
				out.close();
			}
		}
	}

	private File writeXSLX(List<Entry> list) throws IOException {
		final File file = File.createTempFile("data", ".xlsx");
		FileOutputStream out = new FileOutputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook();
		Sheet s = wb.createSheet();
		Row r = null;
		Cell c = null;
		//CellStyle cs = wb.createCellStyle();
		//CellStyle cs2 = wb.createCellStyle();
		//CellStyle cs3 = wb.createCellStyle();
		//DataFormat df = wb.createDataFormat();
		//Font arial12bold = wb.createFont();
		//arial12bold.setFontHeightInPoints((short)12);
		//make it blue
		//arial12bold.setColor((short)0xc);
		//arial is the default font
		//arial12bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

		//set font 2 to 10 point type
		//Font arial10bold = wb.createFont();
		//arial10bold.setFontHeightInPoints((short)10);
		//make it red
		//arial10bold.setColor((short)Font.COLOR_RED);
		//make it bold
		//arial10bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		//arial10bold.setStrikeout(true);

		//set cell stlye
		//cs.setFont(arial12bold);
		//set the cell format
		//cs.setDataFormat(df.getFormat("#,##0.0"));

		//set a thin border
		//cs2.setBorderBottom(cs2.BORDER_THIN);
		//fill w fg fill color
		//cs2.setFillPattern((short)CellStyle.SOLID_FOREGROUND);
		//set the cell format to text see DataFormat for a full list
		//cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

		// set the font
		//cs2.setFont(arial10bold);

		// set the sheet name in Unicode
		//wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F " + "\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430");
		// in case of plain ascii
		wb.setSheetName(0, "data");
		r = s.createRow(0);
		for(int column = 0; column < 53; column++) {
			c = r.createCell(column);
			c.setCellValue(column == 0 ? "Entry" : "Week " + column);
			c.setCellType(Cell.CELL_TYPE_STRING);
		}

		int rownum;
		final int count = list.size() + 1;
		for(rownum = 1; rownum < count; rownum++) {
			r = s.createRow(rownum);
			//for(int column = 0; column < 53; column++) {
			final Entry entry = list.get(rownum - 1);
			c = r.createCell(0);
			c.setCellValue(entry.getName());
			c.setCellType(Cell.CELL_TYPE_STRING);
			c = r.createCell(entry.getWeek());
			c.setCellValue(entry.getValue());
			c.setCellType(Cell.CELL_TYPE_NUMERIC);
/*
				if( column == 0 ) {
				} else if( amount == null ) {
					//c.setCellValue("");
					c.setCellType(Cell.CELL_TYPE_NUMERIC);
				} else {
					c.setCellValue(amount);
					c.setCellType(Cell.CELL_TYPE_NUMERIC);
				}
*/
			//}
		}
		wb.write(out);
		return file;
	}
}
