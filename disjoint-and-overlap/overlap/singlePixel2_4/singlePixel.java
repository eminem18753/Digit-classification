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
	private String testFilePath;
	private ArrayList<ArrayList<String>> dataset;
	private ArrayList<String> testSet;
	private ArrayList<Integer> testLabel;
	private ArrayList<Integer> predictLabel;
	private String digits="";
	private int rowNumber;
	private int colNumber;
	private int indexNumber;
	private int rowSize=2;
	private int colSize=4;
	private int flagOverlap=0;
	private int quotient=0;
	private int remnant=0;
	private int[][][] countFeature;
	private double[] rowCount;
	private double[] priorCount;
	private double[] priorProbability;
	private double[] resultMAP;
	private double[] maxMAP;
	private double[] minMAP;
	private double[][] confusionMatrix;
	private double[][] oddFeature;
	private double[][][][][] probability;
	private double[][][][][] probabilityNothing;
	private String[] finalMAP;
	private String[] falseMAP;
	public singlePixel(String trainingFilePath,String testFilePath)
	{
		this.indexNumber=10;
		this.rowNumber=32;
		this.colNumber=32;
		this.trainingFilePath=trainingFilePath;
		this.testFilePath=testFilePath;
		this.dataset = new ArrayList();
		this.testSet = new ArrayList();
		this.testLabel = new ArrayList();
		this.predictLabel = new ArrayList();
		this.rowCount=new double[10];
		for(int i=0;i<10;i++)
		{
			rowCount[i]=0;
		}
		this.priorCount=new double[10];
		for(int i=0;i<10;i++)
		{
			priorCount[i]=0;
		}
		this.priorProbability=new double[10];
		for(int i=0;i<10;i++)
		{
			priorProbability[i]=0;
		}
		this.resultMAP=new double[10];
		for(int i=0;i<10;i++)
		{
			resultMAP[i]=0;
		}
		this.maxMAP=new double[10];
		for(int i=0;i<10;i++)
		{
			maxMAP[i]=-1000000;
		}
		this.minMAP=new double[10];
		for(int i=0;i<10;i++)
		{
			minMAP[i]=1000000;
		}
		this.finalMAP=new String[10];
		for(int i=0;i<10;i++)
		{
			finalMAP[i]="";
		}
		this.falseMAP=new String[10];
		for(int i=0;i<10;i++)
		{
			falseMAP[i]="";
		}
		for(int i=0;i<10;i++)
		{	
			dataset.add(new ArrayList<String>());
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
		confusionMatrix=new double[10][10];
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<10;j++)
			{
				confusionMatrix[i][j]=0;
			}
		}
		oddFeature=new double[rowNumber][colNumber];
		for(int i=0;i<rowNumber;i++)
		{
			for(int j=0;j<colNumber;j++)
			{
				oddFeature[i][j]=0;
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
		double smoothingFactor=0.1;
		try
		{
			BufferedReader trainingReader=new BufferedReader(new FileReader(trainingFilePath));
			String line;
			
			while((line=trainingReader.readLine())!=null)
            {
				if(line.charAt(0)==' ')
				{
					index=line.charAt(1)-'0';
					priorCount[index]+=1;
					dataset.get(index).add(digits);
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
		//train set read
		for(int i=0;i<dataset.size();i++)
		{
			for(int j=0;j<dataset.get(i).size();j++)
			{
				for(int k=0;k<(rowNumber-rowSize+1)*(colNumber-colSize+1);k++)
				{
					for(int m=0;m<rowSize;m++)
					{
						quotient=k/29;
						remnant=k%29;
						for(int n=0;n<colSize;n++)
						{
							//System.out.print(quotient*32+remnant+m*32+n);
							//System.out.print(" ");
							if(dataset.get(i).get(j).charAt(quotient*32+remnant+m*32+n)=='1')
							{
								countFeature[i][k][m*colSize+n]+=1;
							}
						}
					}
					//System.out.println();
				}
			}
		}
		for(int i=0;i<dataset.size();i++)
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
							//System.out.println(probability[i][k][m][n][o]);
						}
					}
				}
			}
		}
		
		for(int i=0;i<dataset.size();i++)
		{
			for(int k=0;k<rowNumber-rowSize+1;k++)
			{
				for(int m=0;m<colNumber-colSize+1;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							probability[i][k][m][n][o]=(double)probability[i][k][m][n][o]/(double)dataset.get(i).size();
							//System.out.println(probability[i][k][m][n][o]);
						}
					}
				}
			}
		}
		
		for(int i=0;i<dataset.size();i++)
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
							//System.out.println(probability[i][k][m][n][o]);
						}
					}
				}
			}
		}

		for(int i=0;i<dataset.size();i++)
		{
			for(int k=0;k<rowNumber-rowSize+1;k++)
			{
				for(int m=0;m<colNumber-colSize+1;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							probability[i][k][m][n][o]=(probability[i][k][m][n][o]+smoothingFactor)/(dataset.get(i).size()+smoothingFactor*2);
							//System.out.println(probability[i][k][m][n][o]);
						}
					}
				}
			}
		}
				
		for(int i=0;i<10;i++)
		{
			priorProbability[i]=priorCount[i]/(double)2436;
		}
		
		//*************************
		//test set read
		int indexTest=0;
		try
		{
			BufferedReader testReader=new BufferedReader(new FileReader(testFilePath));
			String line;
			
			while((line=testReader.readLine())!=null)
            {
				if(line.charAt(0)==' ')
				{
					testLabel.add(line.charAt(1)-'0');
					testSet.add(digits);
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
		//test set read
		int result=0;
		for(int i=0;i<testSet.size();i++)
		{
			for(int j=0;j<10;j++)
			{
				resultMAP[j]=0;
			}
			for(int j=0;j<10;j++)
			{
				resultMAP[j]=Math.log(priorProbability[j]);
				for(int k=0;k<rowNumber-rowSize+1;k++)
				{
					for(int m=0;m<colNumber-colSize+1;m++)
					{
						quotient=(k*(colNumber-colSize+1)+m)/29;
						remnant=(k*(colNumber-colSize+1)+m)%29;
						for(int n=0;n<rowSize;n++)
						{
							for(int o=0;o<colSize;o++)
							{
								if(testSet.get(i).charAt(quotient*32+remnant+n*32+o)=='1')
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
			for(int n=0;n<10;n++)
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
			//System.out.println(predictLabel.get(i));
			if(testLabel.get(i)==predictLabel.get(i))
			{
				countCorrect+=1;
			}
		}
		System.out.println(countCorrect);
		System.out.println(countCorrect/(double)444);
		//confusion matrix
		/*
		for(int i=0;i<testLabel.size();i++)
		{
			for(int j=0;j<10;j++)
			{
				for(int k=0;k<10;k++)
				{
					if(testLabel.get(i)==j&&predictLabel.get(i)==k)
					{
						confusionMatrix[j][k]+=1;
					}
				}
			}
		}
		*/
		//odd ratio
		/*
		int x=2;
		int y=8;
		for(int k=0;k<dataset.get(x).get(0).length();k++)
		{
			System.out.print(dataset.get(x).get(0).charAt(k));
			if(k%32==31)
			{
				System.out.println();
			}
		}
		System.out.println();
		for(int k=0;k<dataset.get(y).get(0).length();k++)
		{
			System.out.print(dataset.get(y).get(0).charAt(k));
			if(k%32==31)
			{
				System.out.println();
			}
		}
		for(int i=0;i<rowNumber;i++)
		{
			for(int j=0;j<colNumber;j++)
			{
				if(probability[7][i][j]>probability[9][i][j])
				{
					oddFeature[i][j]=probability[7][i][j]/probability[9][i][j];
				}
				else
				{
					oddFeature[i][j]=probability[9][i][j]/probability[7][i][j];				
				}
			}
		}
		*/
		//odd ratio
		//confusion matrix
		//test
		int resultCount=0;
		for(int i=0;i<dataset.size();i++)
		{
			for(int k=0;k<rowNumber*colNumber/rowSize/colSize;k++)
			{
				quotient=k/16;
				remnant=k%16;
				for(int m=0;m<rowSize;m++)
				{
					for(int n=0;n<colSize;n++)
					{
						//resultCount+=1;
						//System.out.print(quotient*64+remnant*2+m+n*32);
						//System.out.print(" ");
						//System.out.println(countFeature[i][k][m*colSize+n]);
					}
				}
				//System.out.println();
				//System.out.println();
			}
		}
		/*
		double test1=0;
		for(int i=0;i<dataset.size();i++)
		{
			for(int k=0;k<rowNumber*colNumber/rowSize/colSize;k++)
			{
				for(int m=0;m<rowSize;m++)
				{
					for(int n=0;n<colSize;n++)
					{
						test1+=countFeature[i][k][m*colSize+n];
						//System.out.println(countFeature[i][k][m*colSize+n]);
					}
				}
				//System.out.println();
			}
		}
		System.out.println(test1);
		double test2=0;
		for(int i=0;i<dataset.size();i++)
		{
			for(int k=0;k<rowNumber/rowSize;k++)
			{
				for(int m=0;m<colNumber/colSize;m++)
				{
					for(int n=0;n<rowSize;n++)
					{
						for(int o=0;o<colSize;o++)
						{
							test2+=probability[i][k][m][n][o];
							//System.out.println(probability[i][k][m][n][o]);
						}
					}
				}
			}
		}
		System.out.println(test2);
		*/
		//System.out.println(resultCount);
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