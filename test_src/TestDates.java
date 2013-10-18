import org.json.simple.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA. User: val Date: 11/8/12 Time: 12:48 PM
 */
public class TestDates {
	public static void main(String[] args) {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int year = Integer.parseInt(sdf.format(today));
		//set the calendar 1 month in the past
		cal.add(Calendar.MONTH, -1);
		JSONArray mondays = new JSONArray();
		for(int i = 0, inc = 1; i < 366 && cal.get(Calendar.YEAR) == year; i += inc) {
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				final Date monday = cal.getTime();
				if(monday.after(today)) break;
				mondays.add(DateFormat.getDateInstance(DateFormat.SHORT).format(monday));
				System.out.println(DateFormat.getDateInstance(DateFormat.SHORT).format(monday));
				cal.add(Calendar.DAY_OF_MONTH, 7);
				// currently no future dates are needed
				inc = 7;
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		//return mondays;
	}
}
