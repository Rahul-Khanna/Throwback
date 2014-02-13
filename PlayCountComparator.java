
import java.util.Comparator;


public class PlayCountComparator  
{
	
	private static final Comparator<Song> PC =
            new Comparator<Song>() 
            {

				public int compare(Song s1, Song s2)
				{
					return s1.getPlayCount().compareTo(s2.getPlayCount());
				}

            };

	public static Comparator<Song> getPC() {
		return PC;
	}
}