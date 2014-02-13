
import java.util.Comparator;

public class ScoreComparator {

		private static final Comparator<Song> Scorer =
	            new Comparator<Song>() 
	            {

					public int compare(Song s1, Song s2)
					{
						return s1.getCompositeScore().compareTo(s2.getCompositeScore());
					}

	            };

		public static Comparator<Song> getScorer() {
			return Scorer;
		}
	
}
