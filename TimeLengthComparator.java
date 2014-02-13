
import java.util.Comparator;


public class TimeLengthComparator  
{
	
	static final Comparator<Song> TL =
            new Comparator<Song>() 
            {

				public int compare(Song s1, Song s2)
				{
					return s1.getTimeLength().compareTo(s2.getTimeLength());
				}

            };
            
            public static Comparator<Song> getTL() {
        		return TL;
        	}
}
