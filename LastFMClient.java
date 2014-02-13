

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * Static class for web interaction with Last.FM
 * @author Rahul
 *
 */


public class LastFMClient {
	private static long lastTime;

	/**
	 * the first download of a user's listening history
	 * @return The list of songs
	 */
	
	public static ArrayList<Song> initialDownload(String userName)
	{
		ArrayList<Song>	allSongs=new ArrayList<Song>();
		ArrayList<String> dates= new ArrayList<String>();
		ArrayList<String> names= new ArrayList<String>();
		ArrayList<String> artists = new ArrayList<String>();
		Elements dateList;
		Elements nameList;
		Elements artistList;
		Element name;
		Element artist;
		Element date;
		String lastDate;
		String user= userName;
		TimeCalculator calc= new TimeCalculator();
		System.out.println("Going to try and download your listening history now.");
		try {
			Document doc= Jsoup.connect("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&limit=200&user="+user+"&api_key=2f8ae0c603413f99dd8e7d92a5bb430c").get();
			while (doc.hasText()) {
				nameList = doc.getElementsByTag("name");
				artistList = doc.getElementsByTag("artist");
				dateList = doc.getElementsByTag("date");
				for (int i = 0; i < dateList.size(); i++) {  // 0=current song, size-1=oldest song
					name = nameList.get(i);
					artist = artistList.get(i);
					date=dateList.get(i);
					names.add(name.ownText());
					artists.add(artist.ownText());
					dates.add(date.ownText());
				}
				if(dates.size()>0)
				{
					lastDate=dates.get(dates.size()-1);
					System.out.println(lastDate);
					lastTime=calc.calculte(lastDate);
					String url="http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&limit=200&user="+user+"&to="+lastTime+"&api_key=2f8ae0c603413f99dd8e7d92a5bb430c";
					doc= Jsoup.connect(url).get();
				}
				
				else
				{
					System.out.println("Error with downloading dates.");
					break;
				}
				
				
			}
			System.out.println("Finnished downloading your listening history.");
			System.out.println("Now preparing your library.");
			getSongLibrary(allSongs, dates, names, artists);
		} catch(Exception e){
			System.out.println("Last.fm connection error");
			System.out.println("Please restart the program.");
			File test = new File("library.txt");
			test.delete();
			System.exit(0);
		}
		if(dates.size()>0)
			lastTime= calc.calculte(dates.get(0));
		return allSongs;
		
		
	}
		
	/**
	 * Downloads the user's recent listening history, adding the songs to the user's library
	 * @param time
	 * @param userName
	 * @param songs
	 * @return songs
	 */
	public static ArrayList<Song> download(long time, String userName, ArrayList<Song> songs)
	{
		ArrayList<Song>	allSongs=songs;
		ArrayList<String> dates= new ArrayList<String>();
		ArrayList<String> names= new ArrayList<String>();
		ArrayList<String> artists = new ArrayList<String>();
		Elements dateList;
		Elements nameList;
		Elements artistList;
		String lastDate;
		String user= userName;
		long cutOff=time;
//		String user="rck24";
//		String apiKey="2f8ae0c603413f99dd8e7d92a5bb430c";
		TimeCalculator calc= new TimeCalculator();
		System.out.println("Going to try and download your listening history now.");
		try{
			Document doc= Jsoup.connect("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&limit=200&user="+user+"&from="+cutOff+"&api_key=2f8ae0c603413f99dd8e7d92a5bb430c").get();
			while(doc.hasText()){
				dateList=doc.getElementsByTag("date");
				nameList=doc.getElementsByTag("name");
				artistList=doc.getElementsByTag("artist");
				
				for(int i=0; i<dateList.size(); i++){
					Element date= dateList.get(i);
					Element name= nameList.get(i);
					Element artist= artistList.get(i);
					
					Node dateUnWrapped= date.unwrap();
					Node nameUnWrapped=name.unwrap();
					Node artistUnWrapped= artist.unwrap();
					
					dates.add(dateUnWrapped.toString());
					names.add(nameUnWrapped.toString());
					artists.add(artistUnWrapped.toString());	
				}
				if(dates.size()>0)
				{
					lastDate=dates.get(dates.size()-1);
					System.out.println(lastDate);
					lastTime=calc.calculte(lastDate);
					String url="http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&limit=200&user="+user+"&from="+cutOff+"&to="+lastTime+"&api_key=2f8ae0c603413f99dd8e7d92a5bb430c";
					doc= Jsoup.connect(url).get();
				}
				
				else
				{
					System.out.println("Error with downloading dates.");
					break;
				}
				
			}
			System.out.println("Finnished downloading your listening history.");
			System.out.println("Now preparing your library.");
			getSongLibrary(allSongs, dates, names, artists);
		} catch(Exception e){
			System.out.println("Last.fm connection error");
			lastTime=time;
		}
		if(dates.size()==0)
		{
			lastTime= time;
		}
			
		return allSongs;
		
		
	}
	
	/**
	 * Go through the user's listening history and breaks down the history into the various songs the person has listend to
	 * @param songs
	 * @param dates
	 * @param names
	 * @param artists
	 */
	private static void getSongLibrary(ArrayList<Song> songs, ArrayList<String> dates, ArrayList<String> names, ArrayList<String> artists)
	{
		TimeCalculator calc= new TimeCalculator();
		for(int i=(names.size()-1); i>=0; i--){
			boolean contains=false;
			for(Song song: songs){
				if (song.getTitle().toLowerCase()
						.contains(names.get(i).toLowerCase())
						|| names.get(i).toLowerCase()
								.contains(song.getTitle().toLowerCase())) {
					if (song.getArtist().toLowerCase()
							.contains(artists.get(i).toLowerCase())
							|| artists.get(i).toLowerCase()
									.contains(song.getArtist().toLowerCase())) {
					contains=true;
					song.updateSong(calc.calculte(dates.get(i)));
					break;
				}
			}
			}
			if(!contains){
				songs.add(new Song(names.get(i),artists.get(i),calc.calculte(dates.get(i))));
			}
		}
	}
	
	/**
	 * checks if the last.fm user name passed in is a valid one.
	 * @param user
	 * @return
	 */
	public static boolean checkUser(String user)
	{
		String userName=user;
		Document doc =null;
		try {
			
			 doc= Jsoup.connect("http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user="+userName+"&api_key=2f8ae0c603413f99dd8e7d92a5bb430c").get();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
		if(doc==null)
			return false;
		else
			return true;
		
	}
	
	/**
	 * Downloads the user's top 20 songs of the week
	 * @param user
	 * @return
	 */
	public static ArrayList<Song> getTop20(String user)
	{
		String userName=user;
		ArrayList<Song> songs=new ArrayList<Song>();
		Song song;
		Elements nameList;
		Element name;
		Element artist;
		Document doc;
		try {
			doc = Jsoup.connect("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user="+userName+"&period=7day&limit=20&&api_key=2f8ae0c603413f99dd8e7d92a5bb430c").get();
			nameList = doc.getElementsByTag("name");
			int size = nameList.size();
			if (size % 2 == 0&&size>0) {
				for (int j = 0; j < size; j++) {
					name = nameList.get(j);
					j++;
					artist = nameList.get(j);
					song = new Song(name.ownText(), artist.ownText());
					songs.add(song);
					
				}
			} else
				System.out.println("Reading Error");

		} catch (Exception e) {
			System.out.println("Last.fm connection error");
		}
		
		
		return songs;	
	}
	
	/**
	 * A method to return the last time a user played any song
	 * @return
	 */
	public static long getLastUpdate()
	{
		return lastTime;
	}

}
