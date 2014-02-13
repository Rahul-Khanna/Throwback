
import java.util.ArrayList;

/**
 * A class that was to be used, but no longer needed due to the coefficient of variation. Could prove useful later on though.
 * @author rahulkhanna
 *
 */
public class ZScoreCalculator {
	
	public ZScoreCalculator()
	{
		
	}
	
	
	public static void calculateTLZScore(ArrayList<Song> songs)
	{
		for(Song song: songs)
		{
			song.setTimeLength();
		}
		double sum=0;
		double mean;
		double var;
		double sd;
		double zScore;
		int count=0;
		for(int i=0; i<songs.size(); i++)
		{
			if(songs.get(i).getTimeLength()>0)
				sum=sum+songs.get(i).getTimeLength();
			else
				count++;
		}
		
		mean=sum/(songs.size()-(0.9*count));
		sum=0;
		
		for(int i=0;i<songs.size(); i++)
		{
			if(songs.get(i).getTimeLength()>0)
				sum=sum+((songs.get(i).getTimeLength()-mean)*(songs.get(i).getTimeLength()-mean));
			else
				sum=sum+((songs.get(i).getTimeLength()-mean)*(songs.get(i).getTimeLength()-mean))*0.1;
		}
		
		double denominator= (songs.size()-0.9*count);
//		double divisor= songs.size()-1; //not sure what is going on here, but it fixed the problem
//		divisor=divisor/(songs.size());
//		denominator= denominator*divisor;
		var=sum/denominator;
		sd=Math.pow(var, 0.5);
		for(int i=0;i<songs.size();i++)
		{
			zScore= ((songs.get(i).getTimeLength()-mean)/sd);
//			songs.get(i).setTimeLengthZScore(zScore);
		}
		
	}
	
	public static void calculatePlayCountZScore(ArrayList<Song> songs) // error exists here
	{
		double sum=0;
		double mean;
		double var;
		double sd;
		double zScore;
		int count=0;
		for(int i=0; i<songs.size(); i++)
		{
			if(songs.get(i).getPlayCount()>1)
				sum=sum+songs.get(i).getPlayCount();
			else
				count++;
		}
		
		mean=(sum+count*0.1)/(songs.size()-(0.9*count));
		sum=0;
		
		for(int i=0;i<songs.size(); i++)
		{
			if(songs.get(i).getPlayCount()>1)
				sum=sum+((songs.get(i).getPlayCount()-mean)*(songs.get(i).getPlayCount()-mean));
			else
				sum=sum+((songs.get(i).getPlayCount()-mean)*(songs.get(i).getPlayCount()-mean))*0.1;
		}
		
		double denominator= (songs.size()-0.9*count);
//		double divisor= songs.size()-1; //not sure what is going on here, but it fixed the problem
//		divisor=divisor/(songs.size());
//		denominator= denominator*divisor;
		var=sum/denominator;
		sd=Math.pow(var, 0.5);
		for(int i=0;i<songs.size();i++)
		{
			zScore= ((songs.get(i).getPlayCount()-mean)/sd);
//			songs.get(i).setPlayCountZScore(zScore);
		}
	}
		
}
