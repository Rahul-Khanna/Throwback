
import java.io.*;
import java.util.*;
/**
 * A static class that deals with the reading and writing to the library file
 * @author rahulkhanna
 *
 */


public class IOController {
	private static File songs= new File("library.txt");
	private static FileReader reader;
	private static BufferedReader bReader;
	private static FileWriter writer;
	private static BufferedWriter bWriter;
	private static PrintWriter pWriter;
	private static Long lastTime;
	private static String userName;
	private static double percentage;
	private static double cycle;
	IOController()
	{
		
	}
	
	/**
	 * writes the relevant information to the library file
	 * @param Songlist
	 * @param time
	 * @param userName
	 * @param percentage
	 * @param cycle
	 * @throws IOException
	 */
	public static void write(ArrayList<Song> Songlist, long time,String userName,double percentage, double cycle) throws IOException //lexographically sorted Songlist
	{
		while(!songs.exists())
		{
			if(!songs.createNewFile())
				System.out.println("Writing Error");
		}
			
		try {
			
			pWriter= new PrintWriter(bWriter=new BufferedWriter(writer=new FileWriter(songs)));
			for(Song song: Songlist)
			{
				pWriter.println(song);
			}
			pWriter.println("****");
			pWriter.println(userName+":"+percentage+":"+cycle+":"+time);
			pWriter.println("****");
			pWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the relevant information from the file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Song> read() throws IOException
	{
		ArrayList<Song> songList= new ArrayList<Song>();
		bReader= new BufferedReader(reader=new FileReader(songs));
		try {
			String songInfo= bReader.readLine();
			String pieces[];
			Song song;
			ArrayList<Long> timeStamps= new ArrayList<Long>();
			while(!(songInfo.equalsIgnoreCase("****")))
			{
				pieces=songInfo.split(" : ");
				song= new Song(pieces[0],pieces[1],Long.parseLong(pieces[2]),Integer.parseInt(pieces[3]));
				
				songInfo=bReader.readLine();
				songInfo=songInfo.substring(1, songInfo.length()-1);
				pieces=songInfo.split(", ");
				for(String number:pieces)
				{
					timeStamps.add(Long.parseLong(number));
				}
				song.setTimeStamps(timeStamps);
				
				songList.add(song);
				
				timeStamps.clear();
				songInfo=bReader.readLine();
			}
			String userInfo=bReader.readLine();
			pieces=userInfo.split(":");
			if(pieces.length==4)
			{
				userName=pieces[0];
				percentage=Double.parseDouble(pieces[1]);
				cycle=Double.parseDouble(pieces[2]);
				lastTime=Long.parseLong(pieces[3]);
			}
			else
				System.out.println("Error reading in information.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bReader.close();
		return songList;
	}
	
	public static long getLastTime()
	{
		return lastTime;
	}
	
	public static double getPercentage()
	{
		return percentage;
	}
	
	public static double getCycle()
	{
		return cycle;
	}
	
	public static String getUserName()
	{
		return userName;
	}
	
	

}
