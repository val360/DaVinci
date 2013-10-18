package com.prosc.msi.davinci;

import com.prosc.database.JDBCHelper;
import com.prosc.db.DatabaseWriter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: val Date: 10/23/12 Time: 3:44 PM
 */
public class DbManager {
	private static final Logger log = Logger.getLogger(DbManager.class.getName());
	private static DataSource dataSource;

	public static List<User> getUsers(boolean usersOnly) throws ClassNotFoundException, SQLException, NamingException {
		String sql;
		if( usersOnly ) {
			sql= "SELECT username, password, department, email, region, role FROM users WHERE role='user'";  //FIX!!! hardcoded
		} else {
			sql = "SELECT username, password, department, email, region, role FROM users";
		}
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<User>() {
				@Override
				public User handle(final ResultSet set) throws SQLException {
					final User user = new User();
					user.setUsername(set.getString(1));
					user.setPassword(set.getString(2));
					user.setDepartment(set.getString(3));
					user.setEmail(set.getString(4));
					user.setRegion(set.getString(5));
					user.setRole(UserRole.valueOf(set.getString(6)));
					return user;
				}
			}, sql);
		} finally {
			connection.close();
		}
	}

	public static void addUser(String username, String password, String region, String department, String email, boolean admin) throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			new JDBCHelper(connection).executeUpdate("INSERT INTO users (username, password, region, department, email, role)" + " VALUES(?, ?, ?, ?, ?, ?)", username, password, region, department, email, admin ? "admin" : "user");
		} finally {
			connection.close();
		}
	}

	public static List<Field> getDistinctFieldsByNameForDepartment(String department) throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Field>() {
				@Override
				public Field handle(final ResultSet set) throws SQLException {
					final Field field = new Field();
					field.setName(set.getString(1));
					return field;
				}
			}, "SELECT DISTINCT name FROM fields WHERE department=?", department);
		} finally {
			connection.close();
		}
	}

	public static List<Calc> getCalcs() throws SQLException, NamingException, ClassNotFoundException {
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Calc>() {
				@Override
				public Calc handle(final ResultSet set) throws SQLException {
					final Calc calc = new Calc();
					calc.setName(set.getString(1));
					calc.setDefinition(set.getString(2));
					calc.setId(set.getInt(3));
					calc.setRegion(set.getString(4));
					calc.setDepartment(set.getString(5));
					calc.setType(set.getString(6));
					calc.setScope(set.getString(7));
					return calc;
				}
			}, "SELECT name, definition, id, region, department, type, scope FROM calculations");
		} finally {
			connection.close();
		}
	}

	public static List<Field> getFields(String region, String department) throws ClassNotFoundException, SQLException, NamingException {
		//Map filters = new
		String sql = "SELECT name, type, id, region, department FROM fields";
		String where = "";
		if(region != null && department != null) {
			where = " WHERE region='" + region + "' AND department='" + department + "'";
		} else if(region != null) {
			where = " WHERE region='" + region + "'";
		} else if(department != null) {
			where = " WHERE department='" + department + "'";
		}
		sql += where;

		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Field>() {
				@Override
				public Field handle(final ResultSet set) throws SQLException {
					final Field field = new Field();
					field.setName(set.getString(1));
					field.setType(set.getString(2));
					field.setId(set.getInt(3));
					field.setRegion(set.getString(4));
					field.setDepartment(set.getString(5));
					return field;
				}
			}, sql);
		} finally {
			connection.close();
		}
	}

	public static void addField(ServletContext context, String name, String region, String department, String type) throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			new JDBCHelper(connection)
					.executeUpdate("INSERT INTO fields (name, region, department, type) VALUES(?, ?, ?, ?)", name, region, department, type);
		} finally {
			connection.close();
		}
	}

	public static void addCalc(String name, String definition, String region, String department, String type, String scope) throws SQLException, NamingException, ClassNotFoundException {
		final Connection connection = getConnection();
		try {
			new JDBCHelper(connection).executeUpdate("INSERT INTO calculations (name, definition, region, department, type, scope) VALUES(?, ?, ?, ?, ?, ?)", name, definition, region, department, type, scope);
		} finally {
			connection.close();
		}
	}

	public static List<Entry> getEntriesForFieldByID(Field field) throws SQLException, ClassNotFoundException, NamingException {
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Entry>() {
				@Override
				public Entry handle(final ResultSet set) throws SQLException {
					final Entry entry = new Entry();
					entry.setValue(set.getDouble(1));
					entry.setName(set.getString(2));
					entry.setFieldId(set.getInt(3));
					entry.setWeek(set.getShort(4));
                    entry.setType(set.getString(5));
					entry.setRegion(set.getString(6));
                    entry.setDepartment(set.getString(7));
					return entry;
				}
			}, "SELECT e.amount, e.name, e.field_id, e.week, f.type, f.region, f.department FROM entries e JOIN fields f ON e.field_id=f.id WHERE e.field_id=?", field.getId());
		} finally {
			connection.close();
		}
	}


	/**
	 * Gets entries form the database sorted by department and region
	 * @return list of Entries
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static List<Entry> getEntriesSorted() throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Entry>() {
				@Override
				public Entry handle(final ResultSet set) throws SQLException {
					final Entry entry = new Entry();
					entry.setValue(set.getDouble(1));
					entry.setName(set.getString(2));
					entry.setFieldId(set.getInt(3));
					entry.setWeek(set.getShort(4));
					entry.setDepartment(set.getString(5));
					entry.setRegion(set.getString(6));
					entry.setType(set.getString(7));
					return entry;
				}
			}, "SELECT e.amount, e.name, e.field_id, e.week, f.department, f.region, f.type FROM entries e JOIN fields f ON e.name=f.name ORDER BY f.department, f.region");
		} finally {
			connection.close();
		}
	}

	public static void addEntry(double amount, String name, String fieldId, int week, java.util.Date date) throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			new JDBCHelper(connection)
					.executeUpdate("INSERT INTO entries (amount, name, field_id, week, date) VALUES (?, ?, ?, ?, ?)", amount, name, Integer.parseInt(fieldId), week, new Date(date.getTime()));
		} finally {
			connection.close();
		}
	}

	public static List<String> getRegions(User user) throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			if(UserRole.admin.equals(user.getRole())) {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<String>() {
					@Override
					public String handle(final ResultSet set) throws SQLException {
						return set.getString(1);
					}
				}, "SELECT DISTINCT name FROM regions");
			} else {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<String>() {
					@Override
					public String handle(final ResultSet set) throws SQLException {
						return set.getString(1);
					}
				}, "SELECT DISTINCT name FROM regions WHERE name=?", user.getRegion());
			}

		} finally {
			connection.close();
		}
	}

	public static List<String> getDepartments(User user) throws ClassNotFoundException, SQLException, NamingException {
		final Connection connection = getConnection();
		try {
			if( UserRole.admin.equals(user.getRole()) ) {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<String>() {
					@Override
					public String handle(final ResultSet set) throws SQLException {
						return set.getString(1);
					}
				}, "SELECT DISTINCT name FROM departments" );
			} else {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<String>() {
					@Override
					public String handle(final ResultSet set) throws SQLException {
						return set.getString(1);
					}
				}, "SELECT DISTINCT name FROM departments WHERE name=?", user.getDepartment());
			}
		} finally {
			connection.close();
		}
	}

	private static Connection getConnection() throws ClassNotFoundException, SQLException, NamingException {
		/*Class.forName("com.mysql.jdbc.Driver");
		String username = ServletUtil.configValueForKey(context, "jdbc.username");
		String password = ServletUtil.configValueForKey(context, "jdbc.password");
		String host = ServletUtil.configValueForKey(context, "jdbc.host");
		String db = ServletUtil.configValueForKey(context, "jdbc.database");
		return DriverManager.getConnection("jdbc:mysql://" + host + "/" + db, username, password);*/
        lookupDataSource();
		return dataSource.getConnection();
	}

	public static void lookupDataSource() throws NamingException {Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		dataSource = (DataSource)envCtx.lookup("jdbc/SiteDatabase");
	}

	public static List<Entry> getEntriesForFieldDepartment(ServletContext context, String fieldName, String department) throws SQLException, ClassNotFoundException, NamingException {
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Entry>() {
				@Override
				public Entry handle(final ResultSet set) throws SQLException {
					final Entry entry = new Entry();
					entry.setValue(set.getDouble(1));
					entry.setName(set.getString(2));
					entry.setFieldId(set.getInt(3));
					entry.setWeek(set.getShort(4));
					entry.setDepartment(set.getString(5));
					entry.setRegion(set.getString(6));
					entry.setType(set.getString(7));
					return entry;
				}
			}, "SELECT e.amount, e.name, e.field_id, e.week, f.department, f.region, f.type FROM entries e JOIN fields f ON e.field_id=f.id WHERE f.name=? and f.department=?", fieldName, department);
		} finally {
			connection.close();
		}
	}

	public static void updateEntry(double amount, String fieldName, String fieldId, int week, java.util.Date date) throws SQLException, ClassNotFoundException, NamingException {
		final Connection connection = getConnection();
		try{
			final DatabaseWriter writer = new DatabaseWriter("`", DatabaseWriter.OperationType.Update, "entries")
					.setString("name", fieldName)
					.setDouble("amount", amount)
					.setDate("date", new Date(date.getTime()))
					.setInt("week", week)
					.setInt("field_id", Integer.parseInt(fieldId))
					.where("week", week)
					.where("field_id", Integer.parseInt(fieldId));
			writer.execute(connection).close();
			log.info("Updated " + writer.getUpdateCount() + " rows.");
		} finally {
			connection.close();
		}
	}

	public static List<Entry> calcQueryForDepartment(Calculable arg1, Calculable arg2, String operator, final String department) throws SQLException, NamingException, ClassNotFoundException, ScriptException {
		final Connection connection = getConnection();
		try {
			if(arg1 instanceof Metric && arg2 instanceof Metric) {
				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine engine = mgr.getEngineByName("JavaScript");
				String value = (String)engine.eval(arg1 + operator + arg2);
				List<Entry> list = new ArrayList<Entry>(52);
				for(int i=0; i < 52; i++) {
					final Entry entry = new Entry();
					entry.setWeek(Short.valueOf((short)(i + 1)));
					entry.setValue(Double.parseDouble(value));
					entry.setDepartment(department);
					list.add(entry);
				}
				return list;
			} else if(arg1 instanceof Metric) {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Entry>() {
					@Override
					public Entry handle(ResultSet set) throws SQLException {
						final Entry entry = new Entry();
						entry.setWeek(set.getShort(1));
						entry.setValue(set.getDouble(2));
						entry.setDepartment(set.getString(3));
						entry.setRegion(set.getString(4));
						return entry;
					}
				}, "SELECT tbl2.week, " + ((Metric)arg1).getValue() + operator + "tbl2.amount, tbl2.department, tbl2.region " +
						"FROM (SELECT e2.amount, e2.name, e2.field_id, e2.week, f2.department, f2.region, f2.type FROM entries e2 JOIN fields f2 ON e2.field_id = f2.id " +
						"WHERE f2.name=? and f2.department=?) tbl2", arg2.getName(), department);
			} else if(arg2 instanceof Metric) {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Entry>() {
					@Override
					public Entry handle(ResultSet set) throws SQLException {
						final Entry entry = new Entry();
						entry.setWeek(set.getShort(1));
						entry.setValue(set.getDouble(2));
						entry.setDepartment(set.getString(3));
						entry.setRegion(set.getString(4));
						return entry;
					}
				}, "SELECT tbl1.week, tbl1.amount" + operator + ((Metric)arg2).getValue() + ", tbl1.department, tbl1.region " +
						"FROM (SELECT e.amount, e.name, e.field_id, e.week, f.department, f.region, f.type FROM entries e JOIN fields f ON e.field_id = f.id " +
						"WHERE f.name=? and f.department=?) tbl1"
						,arg1.getName(), department);
			} else {
				return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Entry>() {
					@Override
					public Entry handle(ResultSet set) throws SQLException {
						final Entry entry = new Entry();
						entry.setWeek(set.getShort(1));
						entry.setValue(set.getDouble(2));
						entry.setDepartment(set.getString(3));
						entry.setRegion(set.getString(4));
						return entry;
					}
				}, "SELECT tbl1.week, tbl1.amount" + operator + "tbl2.amount, tbl1.department, tbl1.region " +
						"FROM (SELECT e.amount, e.name, e.field_id, e.week, f.department, f.region, f.type FROM entries e JOIN fields f ON e.field_id = f.id " +
						"WHERE f.name=? and f.department=?) tbl1 " +
						"INNER JOIN " +
						"(SELECT e2.amount, e2.name, e2.field_id, e2.week, f2.department, f2.region, f2.type FROM entries e2 JOIN fields f2 ON e2.field_id = f2.id " +
						"WHERE f2.name=? and f2.department=?) tbl2 ON tbl1.week = tbl2.week", arg1.getName(), department, arg2.getName(), department);
			}
		} finally {
			connection.close();
		}
	}

	public static List<Metric> getMetrics() throws SQLException, NamingException, ClassNotFoundException {
		final Connection connection = getConnection();
		try {
			return new JDBCHelper(connection).executeQuery(new JDBCHelper.RowHandler<Metric>() {
				@Override
				public Metric handle(final ResultSet set) throws SQLException {
					final Metric metric = new Metric();
					metric.setName(set.getString(1));
					metric.setValue(set.getDouble(2));
					return metric;
				}
			}, "SELECT name, value FROM metrics");
		} finally {
			connection.close();
		}
	}

	public static void addMetric(String name, String value) throws SQLException, NamingException, ClassNotFoundException {
		final Connection connection = getConnection();
		try {
			new JDBCHelper(connection).executeUpdate("INSERT INTO metrics (name, value) VALUES(?, ?)", name, value);
		} finally {
			connection.close();
		}
	}
}
