
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;


/**
 * The user interface class, that allows a user to interact with the program.
 * @author rahulkhanna
 *
 */

public class Controller {
	ArrayList<Song> songs= new ArrayList<Song>();
	Scanner in;
	String userInput;
	double tlPercentage;
	double cycle;
	long lastTime;
	long sum=0;
	int count;
	double tlMean;
	final long SECONDSINADAY=86400;
	final double DAWEIGHT=0.3; //fixed weight for date added weight
	Analyzer a=null;
	boolean throwback;
	boolean recent;
	Song[] playlist= new Song[10];
	String user;
	
	public Controller()
	{
		in= new Scanner(System.in);
	}
	
	/**
	 * The method that actually starts the program.
	 * @throws IOException
	 */
	public void run() throws IOException
	{
		boolean on=true;
		System.out.println("***********************************************************************************************************************");
		System.out.println("Hello and welcome to Throwback, an application for last.fm users that aims to find hidden gems in the users' libraries.");
		System.out.println("Hopefully I can be of help and uncover some great old songs of yours!");
		System.out.println("");
		File data=new File("library.txt");
		String userInput;
		if(data.createNewFile()) // determines whether the user has used the program before.
		{
			System.out.println("Okay so this is your first time using me. Please enter your last.fm username below: ");
			userInput=in.nextLine();
			while(!(LastFMClient.checkUser(userInput)))
			{
				System.out.println("");
				System.out.println("Sorry that doesn't seem to be a Last.fm username. Please enter your last.fm username below:");
				userInput=in.nextLine();
			}
			user=userInput;
			songs=LastFMClient.initialDownload(user);
//			songs=IOController.read();
			lastTime=LastFMClient.getLastUpdate();
			if(songs.size()>50)
			{
				a=new Analyzer(songs,DAWEIGHT);
				System.out.println("");
				System.out.println("Great now we are going to play a game. The point of the game is to figure out what songs are perfect for you!");
				game();
			}
			
			else{
				System.out.println("Sorry you have a library consisting of less than 50 songs, that is not enough songs for me to find some throwback music for you.");
				System.out.println("Try again when you have listened to at least 50 songs.");
				System.out.println("Good Bye.");
				System.out.println("***********************************************************************************************************************");
				System.exit(0);
			}
			
		}
		
		else //a returning user, updates all relevant information
		{
			songs=IOController.read();
			user=IOController.getUserName();
			cycle=IOController.getCycle();
			tlPercentage=IOController.getPercentage();
			lastTime=IOController.getLastTime();
			System.out.println("Welcome back "+ user+"!");
			songs=LastFMClient.download(lastTime, user, songs);
			lastTime=LastFMClient.getLastUpdate();
			a=new Analyzer(songs,DAWEIGHT);
			a.playCountRank();
			tlPercentage=a.timeLengthRank(tlPercentage);
			cycle=a.dateAddedRank(cycle);
			throwback=a.compositeRank();
			songs=a.getAnalyzedLibrary();
			
		}
		
		while(on) // the start of the program
		{
			System.out.println("");
			System.out.println("");
			System.out.println("If you would like a new playlist, enter 1 below.");
			System.out.println("If you would like to see your top throwbacks, enter 2 below.");
			System.out.println("If you would like to play the game again, enter 3 below.");
			System.out.println("If you would like to quit, enter 4 below");
			int response=in.nextInt();
			if(response!=1&&response!=2&&response!=3&&response!=4)
			{
				System.out.println("");
				System.out.println("Sorry that was not a valid answer.");
				System.out.println("If you would like a new playlist, enter 1 below.");
				System.out.println("If you would like to see your top throwbacks, enter 2 below.");
				System.out.println("If you would like to play the game again, enter 3 below.");
				System.out.println("If you would like to quit, enter 4 below");
				response=in.nextInt();
			}
			
			if(response==1) //depending on the user's listening history 3 different playlists can be displayed
			{
				generatePlaylist();
				System.out.println("");
				if(throwback)
				{
					if(recent)
					{
						System.out.println("The first 6 songs are throwbacks, while the last 4 are ones that you are currently listning to.");
						System.out.println("Hope you enjoy it!");
						for(int i=0; i<playlist.length;i++)
						{
							int position=i+1;
							System.out.println(position+". "+ playlist[i].getTitle()+ " by, "+ playlist[i].getArtist());
						}
					}
					
					else
					{
						System.out.println("You haven't listened to enough music in the past month, so all songs below are throwback.");
						System.out.println("Blessing in Disguise?");
						for(int i=0; i<playlist.length;i++)
						{
							int position=i+1;
							System.out.println(position+". "+ playlist[i].getTitle()+ " by, "+ playlist[i].getArtist());
						}
					}
						
				}
				else
				{
					System.out.println("You don't have an extensive enough music history, however I tried to find some songs that you like.");
					System.out.println("Hope I did a decent job.");
					for(int i=0; i<playlist.length;i++)
					{
						int position=i+1;
						System.out.println(position+". "+ playlist[i].getTitle()+ " by, "+ playlist[i].getArtist());
					}
				}
			}
			
			if(response==2) //allows the user to see his/her top throwbacks if he/she has a long enough listening history
			{
				if(throwback)
				{
					Collections.sort(songs, RankComparator.getRanker());
					System.out.println("How many songs would you like to see?");
					response=in.nextInt();
					while(response>songs.size())
					{
						System.out.println("Oops, you actaully only have" + songs.size() + "songs. Try a smaller number: ");
						response=in.nextInt();
					}
					
					for(int i=0; i<response; i++)
					{
						Song song=songs.get(i);
						long firstPlay= song.getDateAdded();
						Date date = new Date(firstPlay*1000L);
						int position=i+1;
						System.out.println(position+". "+ song.getTitle()+ " by, "+ song.getArtist() + ". Play Count: " + song.getPlayCount() + ". Date Added: " +date+".");
					}
				}
				
				else
					System.out.println("Sorry you don't have an extensive enough listening history for me to determine throwback music for you.");
				
			}
			if(response==3) // allows the user to replay the game in order to determine what songs are indeed old
				game();
			
			if(response==4) //quite the program and writes all the information to the library file
			{
				on=false;
				IOController.write(songs, lastTime, user, tlPercentage, cycle);
				System.out.println("");
				System.out.println("Good Bye");
				System.out.println("***********************************************************************************************************************");
			}
			
		}
		
		
	}
	
	/**
	 * The method that lets the user inform the program as to how long a song must be listened to in order for it be significant. It also allows a user to tell the program what an old song means to the user.  
	 */
	private void game()
	{
		DecimalFormat df = new DecimalFormat("#.##");
		tlMean=a.getTLMean();
		boolean tlNotComplete=true;
		boolean cycleNotComplete=true;
		ArrayList<Song> cutOffSongs= new ArrayList<Song>();
		Collections.sort(songs, PlayCountComparator.getPC());
		for(int i=(songs.size()-1);i>=0;i--)
		{
			if(songs.get(i).getPlayCount()>1)
				cutOffSongs.add(songs.get(i));
		}
		System.out.println("So I will start asking you some questions about songs you have listened to, please answer as well as you can; the better your answer is, the better I can help you.");
		
		Collections.sort(cutOffSongs,TimeLengthComparator.getTL());
//		int position=cutOffSongs.size()/2;
		long time=(cutOffSongs.get(cutOffSongs.size()-1).getTimeLength())/2;
		long timeIncrement=time/2;
		long temp;
		int response;
		System.out.println("These questions will help determine if you feel you have listened to a song enough.");
		while(tlNotComplete)
		{
//			Song song=cutOffSongs.get(position);
			System.out.println("");
//			System.out.println("Do you think you have listened to this song enough?");
//			System.out.println(song.getTitle()+ " by, "+ song.getArtist()+ ".");
//			double numberOfWeeks= (double) song.getTimeLength()/SECONDSINADAY;
//			System.out.println("You listened to this song for " +numberOfWeeks+ " days.");
			double numberOfDays= (double) time/SECONDSINADAY;
			String number=df.format(numberOfDays);
			System.out.println("If you listened to a song for " + number + " days, would that be enough for you to get familiar with it.");
			numberOfDays= tlMean/SECONDSINADAY;
			number=df.format(numberOfDays);
			System.out.println("You on average listen to a song for "+number+ " days.");
			System.out.println("");
			System.out.println("Enter 1 for yes and 0 for no.");
			System.out.println("Enter 2 for an example of a song you listened to this for this long.");
			response=in.nextInt();
			while(response!=1&&response!=0&&response!=2)
			{
				System.out.println("Sorry that isn't a valid response.");
				System.out.println("Enter 1 for yes and 0 for no");
				System.out.println("Enter 2 for an example of a song you listened to this for this long.");
				response=in.nextInt();
			}
			
			if(response==2)
			{
				int position=0;
				for(int i=0;i<cutOffSongs.size();i++)
				{
					if(cutOffSongs.get(i).getTimeLength()>time)
					{
						position=i;
						break;
					}
				}
				
				boolean notHelpful=true;
				while(notHelpful)
				{
					Song song=cutOffSongs.get(position);
					numberOfDays= (double) song.getTimeLength()/SECONDSINADAY;
					number=df.format(numberOfDays);
					System.out.println("");
					System.out.println("You listened to " + song.getTitle() +" by, " +song.getArtist()+" for " + number+ " days.");
					System.out.println("Was that helpful or would you like another example?");
					System.out.println("Enter 1 to continue, and 0 for another example.");
					response=in.nextInt();
					while(response!=1&&response!=0)
					{
						System.out.println("Sorry that isn't a valid response.");
						System.out.println("Enter 0 for another example, and 1 to continue");
						System.out.println("");
						response=in.nextInt();
					}
					
					if(response==0)
					{
						position--;
						if(position<0)
						{
							position=position+4;
						}
					}
						
					else
						notHelpful=false;		
				}
				
				
			}
			
			else if(response==1)
			{
				temp=time-timeIncrement;
				if(temp<0||Math.abs(temp-time)<20000)
				{
					tlNotComplete=false;
				}
				else
				{
					time=time-timeIncrement;
					timeIncrement=timeIncrement/2;
				}
			}
			
			else
			{
				temp=time+timeIncrement;
				if(temp>cutOffSongs.get(cutOffSongs.size()-1).getTimeLength()||Math.abs(temp-time)<20000)
				{
					tlNotComplete=false;
				}
				
				else
				{
					time=time+timeIncrement;
					timeIncrement=timeIncrement/2;
				}
			}
		}
		
		
//		tlPercentage=cutOffSongs.get(position).getTimeLength()/tlMean;
		tlPercentage=time/tlMean;
		System.out.println("");
		System.out.println("This is your percentage of the mean: "+tlPercentage);
		
		Collections.sort(cutOffSongs,DateAddedComparator.getDA());
		int position=cutOffSongs.size()/2;
		int increment=position/2;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
		System.out.println("Great, you are almost done!");
		System.out.println("I am now going to ask you questions about how old is truely old for you.");
		System.out.println("Again remember the better your answers, the better I can help you!");
		System.out.println("For best results pay more attention to the dates attached to each song, rather than the song!!!!");
		while(cycleNotComplete)
		{
			System.out.println("");
			System.out.println("Is this song old for you?");
			Song song=cutOffSongs.get(position);
			System.out.println(song.getTitle()+ " by, "+ song.getArtist()+ ".");
			long firstPlay= song.getDateAdded();
			Date date = new Date(firstPlay*1000L);
			System.out.println("You first listened to this song on "+date );
			System.out.println("");
			System.out.println("Enter 1 for yes and 0 for no");
			response=in.nextInt();
			while(response!=1&&response!=0)
			{
				System.out.println("Sorry that isn't a valid response.");
				System.out.println("Enter 1 for yes and 0 for no");
				response=in.nextInt();
			}
			
			if(response==0)
			{
				temp=position-increment;
				if(temp<0||Math.abs(temp-position)<1)
				{
					cycleNotComplete=false;
				}
				else
				{
					position=position-increment;
					increment=increment/2;
				}
			}
			
			else
			{
				temp=position+increment;
				if(temp>cutOffSongs.size()-1||Math.abs(temp-position)<1)
				{
					cycleNotComplete=false;
				}
				
				else
				{
					position=position+increment;
					increment=increment/2;
				}
			}
		}
		
		cycle= lastTime- (cutOffSongs.get(position).getDateAdded());
		cycle=cycle/tlMean;
		a.playCountRank();
		tlPercentage=a.timeLengthRank(tlPercentage);
		cycle=a.dateAddedRank(cycle);
		throwback=a.compositeRank();
		songs=a.getAnalyzedLibrary();
		System.out.println("");
		System.out.println("This is your optimal cycle: "+cycle);
		System.out.println("Thanks for playing!");
	}
	
	/**
	 * the method that actually generates a playlist for a user
	 */
	private void generatePlaylist()
	{
		for(Song song: songs)
		{
			song.resetSelect();
		}
		Random rand = new Random();
		int i=1;
		int position;
		int min=(i-1)*10;
		int max=i*10;
		position= rand.nextInt((max - min)) + min;
		playlist[0]=songs.get(position);
		songs.get(position).selected();
		while(i<6){
			min=(i-1)*10;
			max=i*10;
			position= rand.nextInt((max - min)) + min;
			if(!(songs.get(position).getSelected()))
			{
				playlist[i]=songs.get(position);
				songs.get(position).selected();
				i++;
			}
		}
		ArrayList<Song> top40 =LastFMClient.getTop20(user); // think about month being a short enough time for a user
		if(top40.size()>4)
		{
			int increment = (int) top40.size()/4;
			for(i=0;i<4;i++)
			{
				min=i*increment;
				max=(i+1)*increment;
				position= rand.nextInt((max - min)) + min;
				playlist[i+6]=top40.get(position);
			}
			
			recent=true;
		}
		
		else
		{
			i=1;
			while(i<5)
			{
				min=(i-1)*10;
				max=i*10;
				position= rand.nextInt((max - min)) + min;
				if(!(songs.get(position).getSelected()))
				{
					playlist[i+5]=songs.get(position);
					songs.get(position).selected();
					i++;
				}
			}
			
			recent=false;
		}
		
	}
}
