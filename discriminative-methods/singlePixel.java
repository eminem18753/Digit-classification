import java.util.Set;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
public class singlePixel
{
	private int rowNumber=32;
	private int colNumber=32;
	private int kNumber=3;
	private String trainingFilePath;
	private String testingFilePath;
	private String digits="";
	private String testDigits="";
	private ArrayList<ArrayList<String>> dataset;
	private ArrayList<ArrayList<String>> testset;

	private double[][] confusionMatrix;
	private double[] number;
	private double[] accuracy;
	private double[] outputs;
	private double[] distance;
	public singlePixel(String trainingFilePath,String testingFilePath)
	{
		this.trainingFilePath=trainingFilePath;
		this.testingFilePath=testingFilePath;
		this.dataset = new ArrayList();
		this.testset = new ArrayList();
		double totalDistance=0;
		for(int i=0;i<10;i++)
		{	
			dataset.add(new ArrayList<String>());
		}
		for(int i=0;i<10;i++)
		{	
			testset.add(new ArrayList<String>());
		}
		confusionMatrix=new double[10][10];
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<10;j++)
			{
				confusionMatrix[i][j]=0;
			}
		}
		distance=new double[kNumber];
		for(int i=0;i<kNumber;i++)
		{
			distance[i]=100000000;
		}
		outputs=new double[10];
		for(int i=0;i<10;i++)
		{
			outputs[i]=0;
		}
		number=new double[10];
		for(int i=0;i<10;i++)
		{
			number[i]=0;
		}
		accuracy=new double[10];
		for(int i=0;i<10;i++)
		{
			accuracy[i]=0;
		}
	}
	public void singlePixelAlgorithm()
	{
		int correctCount=0;
		int errorCount=0;
		int index=0;
				
		try
		{
			BufferedReader trainingReader=new BufferedReader(new FileReader(trainingFilePath));
			String line;
			
			while((line=trainingReader.readLine())!=null)
            {
				if(line.charAt(0)==' ')
				{
					index=line.charAt(1)-'0';
					dataset.get(index).add(digits);
					digits="";
				}
				else
				{
					digits=digits+line;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		//while()
		//*************************
		//test set read
		
		index=0;

		try
		{
			BufferedReader testReader=new BufferedReader(new FileReader(testingFilePath));
			String line;
			
			while((line=testReader.readLine())!=null)
            {
				if(line.charAt(0)==' ')
				{
					index=line.charAt(1)-'0';
					testset.get(index).add(testDigits);
					testDigits="";
				}
				else
				{
					testDigits=testDigits+line;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		Date previousDate=new Date();
		double[] minimumDistance=new double[kNumber];
		int[] minimumDistanceIndexI=new int[kNumber];
		int[] minimumDistanceIndexJ=new int[kNumber];
		for(int i=0;i<kNumber;i++)
		{
			minimumDistance[i]=10000000;
		}
		for(int testIndex=0;testIndex<testset.size();testIndex++)
		{
			for(int testCount=0;testCount<testset.get(testIndex).size();testCount++)
			{
				int[] digitCount=new int[10];
				int finalPrediction=-1;
				//Date date=new Date();
				//System.out.print((date.getTime()-previousDate.getTime()));
				//System.out.println("ms");
				for(int digit=0;digit<10;digit++)
				{
					digitCount[digit]=0;
				}
				for(int i=0;i<kNumber;i++)
				{
					minimumDistance[i]=10000000;
				}
				for(int kth=0;kth<kNumber;kth++)
				{
					for(int i=0;i<dataset.size();i++)
					{
						for(int j=0;j<dataset.get(i).size();j++)
						{
							double totalDistance=0;
							int alreadySmallerFlag=0;
							for(int k=0;k<dataset.get(i).get(j).length();k++)
							{
								totalDistance+=((double)dataset.get(i).get(j).charAt(k)-(double)testset.get(testIndex).get(testCount).charAt(k))*((double)dataset.get(i).get(j).charAt(k)-(double)testset.get(testIndex).get(testCount).charAt(k));
							}
							//System.out.println(totalDistance);
							
							for(int loop=0;loop<kth;loop++)
							{
								if(totalDistance<minimumDistance[loop])
								{
									alreadySmallerFlag=1;
								}
							}
							if(alreadySmallerFlag==0&&totalDistance<minimumDistance[kth])
							{
								minimumDistance[kth]=totalDistance;
								minimumDistanceIndexI[kth]=i;
								minimumDistanceIndexJ[kth]=j;
							}							
						}
					}
					digitCount[minimumDistanceIndexI[kth]]+=1;
					//previousDate=date;
				}
				//test
				/*
				for(int i=0;i<10;i++)
				{
					System.out.print(digitCount[i]);
					System.out.print(" ");
				}
				System.out.println();
				*/
				//test
				int maxDigit=-1;
				for(int digit=0;digit<10;digit++)
				{
					if(digitCount[digit]>maxDigit)
					{
						maxDigit=digitCount[digit];
						finalPrediction=digit;
					}
				}
				if(finalPrediction==testIndex)
				{
					correctCount+=1;
					//System.out.println(finalPrediction);
				}
				else if(finalPrediction!=testIndex)
				{
					errorCount+=1;
				}
				confusionMatrix[testIndex][finalPrediction]+=1;
			}
		}
		System.out.println(correctCount);
		System.out.println(correctCount/(double)444);
		//test set read
		//test set accuracy

		
		System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
		for(int i=0;i<10;i++)
		{
			System.out.print(i+"\t");
			for(int j=0;j<10;j++)
			{
				System.out.print(confusionMatrix[i][j]+"\t");
			}
			System.out.println();
		}
		
		
		System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
		for(int i=0;i<10;i++)
		{
			System.out.print(i+"\t");
			for(int j=0;j<10;j++)
			{
				System.out.printf("%.3f\t",confusionMatrix[i][j]/(double)testset.get(i).size());
			}
			System.out.println();
		}

		//overall accuracy
		//************************************************************
		//separate accuracy
		// result=0;
		// for(int indexFinal=0;indexFinal<10;indexFinal++)
		// {
			// number[indexFinal]=0;
			// for(int j=0;j<dataset.get(indexFinal).size();j++)
			// {
				// double min=1000000;
				// int minIndex=0;
				// for(int i=0;i<10;i++)
				// {
					// result=0;
					// for(int k=0;k<rowNumber*colNumber;k++)
					// {
						// result+=weights[i][k]*(float)(dataset.get(indexFinal).get(j).charAt(k)-'0');
					// }
					// if(result<min)
					// {
						// min=result;
						// minIndex=i;
					// }
					// /*
					// System.out.print(i);
					// System.out.print(":");
					// System.out.println(result);	
					// */
				// }
				// if(minIndex==indexFinal)
				// {
					// number[indexFinal]+=1;
				// }
				// /*
				// System.out.println(minIndex);
				// System.out.println();
				// */
			// }
			// accuracy[indexFinal]=number[indexFinal]/dataset.get(indexFinal).size();
			// System.out.println(accuracy[indexFinal]);
		// }
		/*
		float result=0;
		
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<dataset.get(i).size();j++)
			{
				result=0;
				for(int k=0;k<rowNumber*colNumber;k++)
				{
					result+=weights[1][k]*(float)(dataset.get(i).get(j).charAt(k)-'0');
				}
				System.out.print(i);
				System.out.print(":");
				System.out.println((float)1/(float)(1+Math.exp(result+51.5)));
				//System.out.println(result);
			}
		}
		*/
		
		
		/*
		for(int i=0;i<10;i++)
		{
			System.out.println(dataset.get(i).size());
		}
		*/
		/*
		for(int i=0;i<3;i++)
		{
			for(int j=0;j<rowNumber*colNumber;j++)
			{
				System.out.print(j);
				System.out.print(":");
				System.out.println(weights[i][j]);
			}
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		}
		*/
		/*
		for(int i=0;i<10;i++)
		{
			System.out.println(lastErrorGradient[i]);
		}
		*/
		/*
		int totalSize=0;
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<dataset.get(i).size();j++)
			{
				//totalSize+=dataset.get(i).get(j).length();
				totalSize+=1;
				System.out.println(totalSize);
			}
		}
		*/
		//test
		//get the combination of weights
		
		//get the combination of weights
		
		//test
		/*
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<dataset.get(i).size();j++)
			{
				for(int k=0;k<dataset.get(i).get(j).length();k++)
				{
					System.out.print(dataset.get(i).get(j).charAt(k));
					if(k%32==31)
					{
						System.out.println();
					}
				}
				System.out.println();
			}
		}
		*/
		/*
		int totalSize=0;
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<dataset.get(i).size();j++)
			{
				//totalSize+=dataset.get(i).get(j).length();
				totalSize+=1;
				System.out.println(totalSize);
			}
		}
		*/
		/*
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<dataset.get(i).size();j++)
			{
				System.out.println(dataset.get(i).get(j));				
			}
			System.out.println(i);
		}
		*/
		//test
	}
}