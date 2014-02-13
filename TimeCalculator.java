
import java.util.*;
/**
 * A class that converts a date to an epoch timestamp
 * @author rahulkhanna
 *
 */

public class TimeCalculator {
	private String [] months= {"Jan", "Feb", "Mar", "Apr","May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	private String inputDate;
	private long outputDate;
	
	public TimeCalculator()
	{
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
	}
	
	public long calculte(String dateString)
	{
		inputDate=dateString;
		StringTokenizer token=new StringTokenizer(inputDate," ");
		int date = Integer.parseInt(token.nextToken());
		int month= Arrays.asList(months).indexOf(token.nextToken());
		int year= Integer.parseInt(token.nextToken().substring(0, 4));// to exclude the comma
		StringTokenizer timeToken=new StringTokenizer(token.nextToken(),":");
		int hour= Integer.parseInt(timeToken.nextToken());
		int minute= Integer.parseInt(timeToken.nextToken());
		cal.set(year, month, date, hour, minute);
		outputDate=cal.getTimeInMillis();	
		outputDate =outputDate/1000;
		return outputDate;
	}
	
	
	
	
}
