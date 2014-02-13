
import java.util.Comparator;


public class DateAddedComparator  
{
	
	static final Comparator<Song> DA =
            new Comparator<Song>() 
            {

				public int compare(Song s1, Song s2)
				{
					return s1.getDateAdded().compareTo(s2.getDateAdded());
				}

            };
            
            public static Comparator<Song> getDA() {
        		return DA;
        	}
}
