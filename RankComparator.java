
import java.util.Comparator;


public class RankComparator  
{
	
	private static final Comparator<Song> Ranker =
            new Comparator<Song>() 
            {

				public int compare(Song s1, Song s2)
				{
					return s1.getCompositeRank().compareTo(s2.getCompositeRank());
				}

            };

	public static Comparator<Song> getRanker() {
		return Ranker;
	}
}