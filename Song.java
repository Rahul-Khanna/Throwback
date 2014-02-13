import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * The main song class that holds all the information about a song.
 * Certain attributes like likes and numberOfPlaylists haven't been used yet, but will be
 * @author rahulkhanna
 *
 */

public class Song implements Comparable<Song>, Serializable {

	private static final long serialVersionUID = 1L;
	private String title; // name of the song
	private String artist; // name of the artist
	private int playCount; // number of times played
	public ArrayList<Long> timeStamps = new ArrayList<Long>(); // array of times played in seconds from the epoch
	private long dateAdded; // the first time played in second since the epoch
	//private double sd; // the standard deviation of the time played 
	private long timeLength; // the time length of the middle 80% of the song being played
//	private int likes; // number of times the song has been liked
//	private int numberOfPlaylists; // number of times the song has been in a playlist
//	private double zScorePlayCount; // the z-score of the song's play count
//	private double zScoreTL; // the z-score of the song's standard deviation
	private int tlRank;
	private int pcRank;
	private int daRank;
	private int compositeRank;
	private double compositeScore;
	private boolean selected;

	
	public Song(String title, String artist, long dateAdded)
	{
		this.title=title;
		this.artist=artist;
		this.dateAdded=dateAdded;
		timeStamps.add(dateAdded);
		playCount=1;
		//sd=0;
//		likes=0;
		timeLength=0;
//		numberOfPlaylists=0;
		selected=false;
	}
	
	public Song(String title, String artist, long dateAdded, int playCount)
	{
		this.title=title;
		this.artist=artist;
		this.dateAdded=dateAdded;
		this.playCount=playCount;
		//sd=0;
//		likes=0;
//		numberOfPlaylists=0;
	}
	
	public Song(String title, String artist)
	{
		this.title=title;
		this.artist=artist;
//		likes=0;
//		numberOfPlaylists=0;
	}
	/**
	 * updates the information of an exsiting song class
	 * @param time
	 */
	public void updateSong(long time)
	{
		playCount++;
		timeStamps.add(time);
	}
	/**
	 * whether a song has already been selected by the current playlist being made
	 */
	public void selected()
	{
		selected=true;
	}
	/**
	 * resets the selected boolean that might still be true due to the last playlist
	 */
	public void resetSelect()
	{
		selected=false;
	}
	
//	//public void setSD() // an old method, that might become useful in the future
//	//{
//		//if(playCount==1)
//			//sd=0;
//		else{
//		double mean;
//		double var;
//		double sum=0;
//		for(int i=0; i< timeStamps.size(); i++)
//		{
//			sum+=timeStamps.get(i);
//		}
//		
//		mean=sum/timeStamps.size();
//		sum=0;
//		
//		for(int i=0; i<timeStamps.size(); i++)
//		{
//			sum+=((timeStamps.get(i)-mean)*(timeStamps.get(i)-mean));
//		}
//		
//		var=sum/(timeStamps.size()-1); // correction for bias
//		
//		sd=Math.pow(var, 0.5);
//		}
//		
//	}
	/**
	 * Calculates the middle 80% time length of a song
	 */
	public void setTimeLength()
	{
		Collections.sort(timeStamps);
		int bottom= (int) (Math.round(timeStamps.size()*0.1));
		int top= (int) (Math.round(timeStamps.size()*0.9)-1);
		bottom=Math.max(bottom, 0);
		top= Math.min(top,timeStamps.size()-1);
		long low=timeStamps.get(bottom);
		long high=timeStamps.get(top);
		timeLength=high-low;	
	}
	
//	public void updateLikes()
//	{
//		likes++;
//	}
//	
//	public void updatePlaylistCount()
//	{
//		numberOfPlaylists++;
//	}
	
	/**
	 * allows the song's timestamps to be updates when read in by the file
	 * @param timeStamps
	 */
	public void setTimeStamps(ArrayList<Long> timeStamps)
	{
		for(Long time: timeStamps)
		{
			this.timeStamps.add(time);
		}
	}
	
//	public void setTimeLengthZScore(double tlZScore)
//	{
//		zScoreTL= tlZScore;
//	}
//	
//	public void setPlayCountZScore(double playCountZScore)
//	{
//		zScorePlayCount= playCountZScore;
//	}
	
	public void setTLRank(int rank)
	{
		tlRank=rank;
	}
	
	public void setPCRank(int rank)
	{
		pcRank=rank;
	}
	
	public void setDARank(int rank)
	{
		daRank=rank;
	}
	
	public void setCompositeRank(int rank)
	{
		compositeRank=rank;
	}
	
	public void setCompositeScore(double score)
	{
		compositeScore=score;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getArtist()
	{
		return artist;
	}
	
	public Integer getPlayCount()
	{
		return playCount;
	}
	
	public Long getDateAdded()
	{
		return dateAdded;
	}
	
//	public Double getSD()
//	{
//		return sd;
//	}
	
	public Long getTimeLength()
	{
		return timeLength;
	}
	
	public ArrayList<Long> getTimeArray()
	{
		return timeStamps;
	}
	
//	public Integer likes()
//	{
//		return likes;
//	}
//	
//	public Integer getPlayListCount()
//	{
//		return numberOfPlaylists;
//	}
	
//	public Double getTLZScore()
//	{
//		return zScoreTL;
//	}
//	
//	public Double getPlayCountZScore()
//	{
//		return zScorePlayCount;
//	}
	
	public Integer getTLRank()
	{
		return tlRank;
	}
	
	public Integer getPCRank()
	{
		return pcRank;
	}
	
	public Integer getDARank()
	{
		return daRank;
	}
	
	public Integer getCompositeRank()
	{
		return compositeRank;
	}
	
	public Double getCompositeScore()
	{
		return compositeScore;
	}
	
	public boolean getSelected()
	{
		return selected;
	}
	
	@Override
	public int compareTo(Song song2) //lexicographically
	{
		if(title.compareTo(song2.getTitle())<0)
			return -1;
		if(title.compareTo(song2.getTitle())>0)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		String eol= System.getProperty("line.separator");
		String rep= title+" : "+artist+ " : " +dateAdded+" : "+playCount+ eol +timeStamps;
		return rep;
				
	}

}
