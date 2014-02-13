
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;



import java.util.Collections;
/**
 * The class that does all the analysis of the songs
 * @author rahulkhanna
 *
 */
public class Analyzer {
	
	private double timeLengthWeight;
	private double playCountWeight;
	private double dateAddedWeight;
	private ArrayList<Song> library;
	private double tlMean;
	private boolean throwback; //a boolean indicating whether the playlist is actually one that can be deemed throwback
	private int oneCount; // counts the number of songs with one a play count of one/ time length of zero
	
	public Analyzer(ArrayList<Song> library,double daWeight)
	{
		this.library=library;
//		ZScoreCalculator.calculatePlayCountZScore(this.library);
//		ZScoreCalculator.calculateTLZScore(this.library);
		for(Song song:this.library)
		{
			song.setTimeLength();
		}
		dateAddedWeight=daWeight;
		oneCount=0;
		calculateWeights();
	}
	/**
	 * Calculates the weights for playcount and timelength based on the coefficient of variation of the sample for both attributes
	 */
	private void calculateWeights()
	{
		double tlVar;
		double tlSum=0;
		double pcMean;
		double pcVar;
		double pcSum=0;
		double pcSD;
		double tlSD;
		double coefficientOfVariationPC; //measure of variability of playcounts
		double coefficientOfVariationTL; //measure of variability of time length
		
		
		for(int i=0; i< library.size(); i++)
		{
			if(library.get(i).getTimeLength()>0 && library.get(i).getPlayCount()>1)
			{
				tlSum=tlSum+library.get(i).getTimeLength();
				pcSum=pcSum+library.get(i).getPlayCount();
			}
			
			else
			{
				pcSum=pcSum+0.1;
				oneCount++;
			}
		}
		double denominator= (library.size()-0.9*oneCount);
//		System.out.println(count);
//		System.out.println(tlSum);
		tlMean=tlSum/denominator;
		pcMean=pcSum/denominator;
//		System.out.println(tlMean);
//		System.out.println(pcMean);
		tlSum=0;
		pcSum=0;
		
		for(int i=0; i<library.size(); i++)
		{
			if(library.get(i).getTimeLength()>0 && library.get(i).getPlayCount()>1)
			{
				tlSum+=((library.get(i).getTimeLength()-tlMean)*(library.get(i).getTimeLength()-tlMean));
				pcSum+=((library.get(i).getPlayCount()-pcMean)*(library.get(i).getPlayCount()-pcMean));
			}
			
			else
			{
				tlSum+=tlMean*tlMean*0.1;
				pcSum+=((library.get(i).getPlayCount()-pcMean)*(library.get(i).getPlayCount()-pcMean))*0.1;
			}
		}
		
//		double divisor= library.size()-1; //not sure what is going on here, but it fixed the problem
//		divisor=divisor/(library.size());
//		denominator= denominator*divisor;
		tlVar=tlSum/denominator; 
		pcVar=pcSum/denominator;
		
		tlSD=Math.pow(tlVar, 0.5);
		pcSD=Math.pow(pcVar, 0.5);
//		System.out.println(tlSD);
//		System.out.println(pcSD);
		coefficientOfVariationTL=tlMean/tlSD;
		coefficientOfVariationPC=pcMean/pcSD;
//		System.out.println(coefficientOfVariationTL);
//		System.out.println(coefficientOfVariationPC);
		double ratio=(coefficientOfVariationTL/coefficientOfVariationPC);
//		System.out.println(ratio);
		playCountWeight=(1-dateAddedWeight)/(ratio+1);
		timeLengthWeight=playCountWeight*ratio;
		}
	/**
	 * A simple linear ranker of playcounts
	 */
	public void playCountRank() 
	{
		Collections.sort(library,PlayCountComparator.getPC());
		int rank=1;
		
		for(int i=0;i<oneCount;i++)
		{
			library.get(i).setPCRank(library.size());//if the play count is 1, then the rank is the size of the library
		}
		
		if(oneCount<library.size())
		{
			library.get(library.size()-1).setPCRank(rank);
			for(int i=library.size()-2;i>=oneCount;i--)
			{
				if(library.get(i).getPlayCount()<library.get(i+1).getPlayCount())
				{
					rank++;
					library.get(i).setPCRank(rank);
				}
				else
					library.get(i).setPCRank(rank);
			}
			
		}
		
		
	}
	/**
	 * A linear ranker after a certain timelength is achieved. Tries to adjust the the cut off if the certain conditions are not met for analysis. 
	 * @param percentage
	 * @return
	 */
	public double timeLengthRank(double percentage) 
	{
		boolean cutOffNotOkay=true;//determines if cut off is appropriate
		Collections.sort(library,TimeLengthComparator.getTL());
		double percentageOfMean=percentage*1.5;
		while(cutOffNotOkay)
		{
			int count=0;
			double cutoff= tlMean *percentageOfMean; // the cut off is a certain percentage of the mean
			for(int i=0;i<library.size();i++)
			{
				if(library.get(i).getTimeLength()<cutoff)
				{
					library.get(i).setTLRank(library.size());
					count++;
				}
					
				else
					break;
			}
			
			int rank=1;
			if(count<oneCount*1.65) // the number of songs below the cut off should be in between 1.15 and 1.65 times the number of songs that by default will be
			{
				if(count>oneCount*1.15)
				{
					library.get(count).setTLRank(rank);
					for(int i=count+1;i<library.size();i++)
					{
						if(library.get(i).getTimeLength()>library.get(i-1).getTimeLength())
						{
							rank++;
							library.get(i).setTLRank(rank);
						}
						else
							library.get(i).setTLRank(rank);	
					 }
					
					cutOffNotOkay=false;
					
				}
				
				else
					percentageOfMean=percentageOfMean+0.025; // the % was too small
						
			}
			
			else
				percentageOfMean=percentageOfMean-0.025; // the % was too big
		}
		
		return (percentageOfMean/1.5);
		
		
	}
	/**
	 * A ranker based on the theory people listen to songs in cycles. Establishes an optimal cycle and then evaluates all other songs relative to the optimal, favoring older songs over newer ones.
	 * @param optimalCycle
	 * @return
	 */
	public double dateAddedRank(double optimalCycle)
	{
		Collections.sort(library, DateAddedComparator.getDA());
		boolean repeat=true;
		long rightNow=LastFMClient.getLastUpdate();
		if(rightNow-(optimalCycle+1)*tlMean<0) // the optimal amount of cycles must have passed
		{
			throwback=false;
			for(Song song:library)
			{
				song.setDARank(library.size());
			}
		}
		
		else
		{
			Collections.sort(library,DateAddedComparator.getDA());
			while(repeat)
			{
				throwback=true;
				double firstCutOff= rightNow-(optimalCycle)*tlMean;
				double secondCutOff= rightNow- (optimalCycle+1)*tlMean;
				double thirdCutOff=rightNow-(optimalCycle+2)*tlMean;
				int firstCounter=library.size()-1;
				int secondCounter=0;
				int thirdCounter=0;
				int rank=1;
				
				for(int i=library.size()-1; i>=0; i--)
				{
					if((double)library.get(i).getDateAdded()>firstCutOff)
					{
						library.get(i).setDARank(library.size());
						firstCounter--;
					}
						
					else 
						break;
				}
				
				if(firstCounter>0) // makes sure that there are more songs left
				{
					secondCounter=firstCounter;
					
					for(int i=firstCounter;i>=0;i--)
					{
						if((double)library.get(i).getDateAdded()>secondCutOff)
						{
							secondCounter--;
						}
						
						else
							break;
					}
					
					
					if(secondCounter>0 && secondCounter<firstCounter) // makes sure that there are more songs left, and that there is some gap between the first and second counter
					{
						thirdCounter=secondCounter;
						
						for(int i=secondCounter;i>=0;i--)
						{
							if((double)library.get(i).getDateAdded()>thirdCutOff)
							{
								library.get(i).setDARank(rank);
								rank++;
								thirdCounter--;
							}
							
							else
								break;
						}
						
						if(thirdCounter<secondCounter&&thirdCounter>0) // makes sure there is space between 
						{
							library.get(thirdCounter).setDARank(rank);
							rank++;
							repeat=false;
						}
						
					}
				}
				
				if(!repeat)
				{
					for(int i=secondCounter+1; i<=firstCounter; i++)
					{
						library.get(i).setDARank(rank);
						rank++;
					}
					
					for(int i=thirdCounter-1; i>=0; i--)
					{
						library.get(i).setDARank(rank);
						rank++;
					}
				}
				
			
				optimalCycle--;  // adjustment to the optimal cycle, if the optimal cycle inputed didn't really work
			}
			optimalCycle++;
		}
		
		
		return optimalCycle;
		
	}
	/**
	 * Creates the composite score and rank for each song based on the three attributes.
	 * @return
	 */
	public boolean compositeRank() 
	{
		if(throwback)
		{
			for(Song song: library)
			{
				double score= (dateAddedWeight*song.getDARank()) + (playCountWeight*song.getPCRank())+ (timeLengthWeight*song.getTLRank());
				song.setCompositeScore(score);
			}
			
			Collections.sort(library, ScoreComparator.getScorer());
			
			for(int i=0; i<library.size(); i++)
			{
				library.get(i).setCompositeRank(i+1);
			}
			
		}
		
		else
		{
			for(Song song: library)
			{
				double score= (playCountWeight*song.getPCRank())+ (timeLengthWeight*song.getTLRank());
				song.setCompositeScore(score);
			}
			
			Collections.sort(library, ScoreComparator.getScorer());
			
			for(int i=0; i<library.size(); i++)
			{
				library.get(i).setCompositeRank(i+1);
			}
		}
		
		return throwback;
		
	}
	
	
	public ArrayList<Song> getAnalyzedLibrary()
	{
		return library;
	}
	
	public double getTLMean()
	{
		return tlMean;
	}
	
	
	
		
		
	
	


}
