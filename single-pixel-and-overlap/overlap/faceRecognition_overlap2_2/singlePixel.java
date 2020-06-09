import java.util.Set;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class singlePixel
{
	private String trainingFilePath;
	private String trainingLabelPath;
	private String testingFilePath;
	private String testingLabelPath;
	private ArrayList<String> dataset;
	private ArrayList<String> testSet;
	private ArrayList<Integer> trainLabel;
	private ArrayList<Integer> testLabel;
	private ArrayList<Integer> predictLabel;
	private String digits="";
	private int rowNumber;
	private int colNumber;
	private int rowSize=2;
	private int colSize=2;
	private int quotient=0;
	private int remnant=0;


	private int indexNumber;
	private double[] rowCount;
	private double[] priorCount;
	private double[] priorProbability;
	private double[] resultMAP;
	private double[] maxMAP;
	private double[] minMAP;
	private int[][][] countFeature;
	private double[][][][][] probability;
	private double[][][][][] probabilityNothing;
	private String[] finalMAP;
	private String[] falseMAP;
	public singlePixel(String trainingFilePath,String trainingLabelPath,String testingFilePath,String testingLabelPath)
	{
		this.indexNumber=2;
		this.rowNumber=69;
		this.colNumber=60;
		this.trainingFilePath=trainingFilePath;
		this.testingFilePath=testingFilePath;
		this.trainingLabelPath=trainingLabelPath;
		this.testingLabelPath=testingLabelPath;
		this.dataset = new ArrayList();
		this.testSet = new ArrayList();
		this.trainLabel = new ArrayList();
		this.testLabel = new ArrayList();
		this.predictLabel = new ArrayList();
		this.rowCount=new double[2];
		for(int i=0;i<2;i++)
		{
			rowCount[i]=0;
		}
		this.priorCount=new double[2];
		for(int i=0;i<2;i++)
		{
			priorCount[i]=0;
		}
		this.priorProbability=new double[2];
		for(int i=0;i<2;i++)
		{
			priorProbability[i]=0;
		}
		this.resultMAP=new double[2];
		for(int i=0;i<2;i++)
		{
			resultMAP[i]=0;
		}
		this.maxMAP=new double[2];
		for(int i=0;i<2;i++)
		{
			maxMAP[i]=-1000000;
		}
		this.minMAP=new double[2];
		for(int i=0;i<2;i++)
		{
			minMAP[i]=1000000;
		}
		this.finalMAP=new String[2];
		for(int i=0;i<2;i++)
		{
			finalMAP[i]="";
		}
		this.falseMAP=new String[2];
		for(int i=0;i<2;i++)
		{
			falseMAP[i]="";
		}
		countFeature=new int[indexNumber][(rowNumber-rowSize+1)*(colNumber-colSize+1)][(int)Math.pow(2,rowSize*colSize)];
		for(int i=0;i<indexNumber;i++)
		{
			for(int j=0;j<(rowNumber-rowSize+1)*(colNumber-colSize+1);j++)
			{
				for(int k=0;k<(int)Math.pow(2,rowSize*colSize);k++)
				{
					countFeature[i][j][k]=0;
				}
			}
		}
		probability=new double[indexNumber][rowNumber-rowSize+1][colNumber-colSize+1][rowSize][colSize];
		for(int i=0;i<indexNumber;i++)
		{
			for(int j=0;j<rowNumber-rowSize+1;j++)
			{
				for(int k=0;k<colNumber-colSize+1;k++)
				{
					for(int m=0;m<rowSize;m++)
					{
						for(int n=0;n<colSize;n++)
						{
							probability[i][j][k][m][n]=0;
						}
					}
				}
			}
		}
		probabilityNothing=new double[indexNumber][rowNumber-rowSize+1][colNumber-colSize+1][rowSize][colSize];
		for(int i=0;i<indexNumber;i++)
		{
			for(int j=0;j<rowNumber-rowSize+1;j++)
			{
				for(int k=0;k<colNumber-colSize+1;k++)
				{
					for(int m=0;m<rowSize;m++)
					{
						for(int n=0;n<colSize;n++)
						{
							probabilityNothing[i][j][k][m][n]=0;
						}
					}
				}
			}
		}
	}
	public void singlePixelAlgorithm()
	{
		//train set read
		int index=0;
		double smoothingFactor=0.07;//0.07
		try
		{
			BufferedReader trainingReader=new BufferedReader(new FileReader(trainingFilePath));
			String line;
			int count=0;
			while((line=trainingReader.readLine())!=null)
            {
				count+=1;
				if(count%70==0)
				{
					dataset.add(digits);
					//priorCount[index]+=1;
					//System.out.println(digits.length());
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
		
		try
		{
			BufferedReader trainingLabelReader=new BufferedReader(new FileReader(trainingLabelPath));
			String line;
			int count=-1;
			while((line=trainingLabelReader.readLine())!=null)
            {
				count+=1;
				trainLabel.add(line.charAt(0)-'0');
				if((int)(line.charAt(0)-'0')==0)
				{
					priorCount[0]+=1;
				}
				else if((int)(line.charAt(0)-'0')==1)
				{
					priorCount[1]+=1;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		//train set read
		
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<(rowNumber-rowSize+1)*(colNumber-colSize+1);j++)
			{
				quotient=j/59;
				remnant=j%59;
				for(int m=0;m<rowSize;m++)
				{
					for(int n=0;n<colSize;n++)
					{
						//System.out.print(quotient*32+remnant+m*32+n);
						//System.out.print(" ");
						if(dataset.get(i).charAt(quotient*60+remnant+m*60+n)=='#')
						{
							if(trainLabel.get(i)==0)
							{
								countFeature[0][j][m*colSize+n]+=1;
							}
							else
							{
								countFeature[1][j][m*colSize+n]+=1;
							}
						}
					}
				}
			}
		}
		
		for(int i=0;i<2;i++)
		{
			for(int k=0;k<rowNumber-rowSize+1;k++)
			{
				for(int m=0;m<colNumber-colSize+1;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							probability[i][k][m][n][o]=countFeature[i][k*(colNumber-colSize+1)+m][n*colSize+o];							
						}
					}
				}
			}
		}

		for(int i=0;i<2;i++)
		{
			for(int k=0;k<rowNumber-rowSize+1;k++)
			{
				for(int m=0;m<colNumber-colSize+1;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							probability[i][k][m][n][o]=(double)probability[i][k][m][n][o]/(double)dataset.size();							
						}
					}
				}
			}
		}

		for(int i=0;i<2;i++)
		{
			for(int k=0;k<rowNumber-rowSize+1;k++)
			{
				for(int m=0;m<colNumber-colSize+1;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							probabilityNothing[i][k][m][n][o]=1-probability[i][k][m][n][o];							
						}
					}
				}
			}
		}

		for(int i=0;i<2;i++)
		{
			for(int k=0;k<rowNumber-rowSize+1;k++)
			{
				for(int m=0;m<colNumber-colSize+1;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							probability[i][k][m][n][o]=(probability[i][k][m][n][o]+smoothingFactor)/(dataset.size()+smoothingFactor*2);							
						}
					}
				}
			}
		}
				
		for(int i=0;i<2;i++)
		{
			priorProbability[i]=priorCount[i]/(double)dataset.size();
		}
		
		//*************************
		//test set read
		try
		{
			BufferedReader testReader=new BufferedReader(new FileReader(testingFilePath));
			String line;
			int count=0;
			
			while((line=testReader.readLine())!=null)
            {
				count+=1;
				if(count%70==0)
				{
					testSet.add(digits);
					//priorCount[index]+=1;
					//System.out.println(digits.length());
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

		try
		{
			BufferedReader testingLabelReader=new BufferedReader(new FileReader(testingLabelPath));
			String line;
			int count=-1;
			while((line=testingLabelReader.readLine())!=null)
            {
				count+=1;
				testLabel.add(line.charAt(0)-'0');
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		//test set read
		int result=0;
		for(int i=0;i<testSet.size();i++)
		{
			for(int j=0;j<2;j++)
			{
				resultMAP[j]=0;
			}
			for(int j=0;j<2;j++)
			{
				resultMAP[j]=Math.log(priorProbability[j]);
				for(int k=0;k<rowNumber-rowSize+1;k++)
				{
					for(int m=0;m<colNumber-colSize+1;m++)
					{
						quotient=(k*(colNumber-colSize+1)+m)/59;
						remnant=(k*(colNumber-colSize+1)+m)%59;
						
						for(int n=0;n<rowSize;n++)
						{
							for(int o=0;o<colSize;o++)
							{
								if(testSet.get(i).charAt(quotient*60+remnant+n*60+o)=='#')
								{
									resultMAP[j]+=Math.log(probability[j][k][m][n][o]);
								}
								else
								{
									resultMAP[j]+=Math.log(probabilityNothing[j][k][m][n][o]);
								}								
							}
						}
					}
				}
			}
			double max=-10000000;
			result=0;
			for(int n=0;n<2;n++)
			{
				if(resultMAP[n]>max)
				{
					max=resultMAP[n];
					result=n;
				}
				if(resultMAP[n]>maxMAP[n])
				{
					maxMAP[n]=resultMAP[n];
					finalMAP[n]=testSet.get(i);
				}
				if(resultMAP[n]<minMAP[n])
				{
					minMAP[n]=resultMAP[n];
					falseMAP[n]=testSet.get(i);
				}
			}
			predictLabel.add(result);
			//System.out.println(Math.log(Math.exp(1)));
			//System.out.println(testSet.get(i));
		}
		
		int countCorrect=0;
		for(int i=0;i<testSet.size();i++)
		{
			if(testLabel.get(i)==predictLabel.get(i))
			{
				countCorrect+=1;
			}
		}
		System.out.println(countCorrect);
		System.out.println(countCorrect/(double)150);
		//test
		/*
		for(int i=0;i<testLabel.size();i++)
		{
			System.out.println(testLabel.get(i));
		}
		*/
		/*
		for(int i=0;i<testLabel.size();i++)
		{
			System.out.println(testLabel.get(i));
		}

		*/
		/*
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<dataset.get(i).length();j++)
			{
				System.out.print(dataset.get(i).charAt(j));
				if(j%60==59)
				{
					System.out.println();
				}
			}
		}
		*/
		/*
		for(int i=0;i<testSet.size();i++)
		{
			for(int j=0;j<testSet.get(i).length();j++)
			{
				System.out.print(testSet.get(i).charAt(j));
				if(j%60==59)
				{
					System.out.println();
				}
			}
		}
		*/

		/*
		for(int i=0;i<rowNumber;i++)
		{
			for(int j=0;j<colNumber;j++)
			{
				if(oddFeature[i][j]>2.5)
				{
					System.out.print("+");
				}
				else
				{
					System.out.print("-");
				}
			}
			System.out.println();
		}
		*/
		/*
		System.out.println("Accuracy:"+(double)countCorrect/(double)testSet.size());//total accuracy
		System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
		for(int i=0;i<10;i++)
		{
			System.out.print(i+"\t");
			for(int j=0;j<10;j++)
			{
				System.out.print(confusionMatrix[i][j]+"\t");
				rowCount[i]+=confusionMatrix[i][j];
			}
			System.out.println();
		}

		System.out.println();
		System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
		for(int i=0;i<10;i++)
		{
			System.out.print(i+"\t");
			for(int j=0;j<10;j++)
			{
				System.out.printf("%.3f\t",confusionMatrix[i][j]/rowCount[i]);
			}
			System.out.println();
		}
		System.out.println();
		*/
		//MAP best
		/*
		System.out.println("MAP Best");
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<1024;j++)
			{
				System.out.print(finalMAP[i].charAt(j));
				if(j%32==31)
				{
					System.out.println();
				}
			}
			System.out.println();
		}
		*/
		//MAP best
		//MAP worst
		/*
		System.out.println("MAP Worst");
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<1024;j++)
			{
				System.out.print(falseMAP[i].charAt(j));
				if(j%32==31)
				{
					System.out.println();
				}
			}
			System.out.println();
		}
		*/
		//MAP worst
		/*
		for(int i=0;i<testSet.size();i++)
		{
			System.out.println(predictLabel.get(i));
		}
		*/
		//System.out.println(testLabel.size());
		/*
		for(int i=0;i<10;i++)
		{
			System.out.println(priorProbability[i]);
		}
		*/
		/*
		int result=0;		
		for(int i=0;i<10;i++)
		{
			result+=priorCount[i];
			System.out.println(result);
		}
		*/
		
		//see this
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
		//see this
		
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