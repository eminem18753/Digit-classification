import java.util.Set;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class singlePixel
{
	private int rowNumber=32;
	private int colNumber=32;
	private String trainingFilePath;
	private String testingFilePath;
	private String digits="";
	private String testDigits="";
	private ArrayList<ArrayList<String>> dataset;
	private ArrayList<ArrayList<String>> testset;

	private double[][] weights;
	private double[][] confusionMatrix;
	private double[] number;
	private double[] accuracy;
	private double[] outputs;
	private double bias=0;
	private double learningRate=0.01;
	public singlePixel(String trainingFilePath,String testingFilePath)
	{
		this.trainingFilePath=trainingFilePath;
		this.testingFilePath=testingFilePath;
		this.dataset = new ArrayList();
		this.testset = new ArrayList();

		for(int i=0;i<10;i++)
		{	
			dataset.add(new ArrayList<String>());
		}
		for(int i=0;i<10;i++)
		{	
			testset.add(new ArrayList<String>());
		}
		weights=new double[10][rowNumber*colNumber];
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<rowNumber*colNumber;j++)
			{
				weights[i][j]=0;
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
		int index=0;
		
		int countEpoch=0;
		int epoches=10;
		double errorSum=0;
		double tempSingle=0;
		double[] errorGradient;
		double[] lastErrorGradient;
		double[] tempDotProduct;
		double[] outcome;
		tempDotProduct=new double[2436];
		errorGradient=new double[2436];
		lastErrorGradient=new double[2436];
		outcome=new double[10];
		for(int i=0;i<10;i++)
		{
			outcome[i]=0;
		}
		for(int i=0;i<2436;i++)
		{
			errorGradient[i]=0;
		}
		
		for(int i=0;i<2436;i++)
		{
			lastErrorGradient[i]=0;
		}
		
		for(int i=0;i<2436;i++)
		{
			tempDotProduct[i]=0;
		}

		double[] errorTemp;
		errorTemp=new double[2436];
		for(int i=0;i<2436;i++)
		{
			errorTemp[i]=0;
		}
		
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

		while(countEpoch<=50)
		{			
			for(int count=0;count<10;count++)
			{
				for(int i=0;i<10;i++)
				{
					for(int j=0;j<dataset.get(count).size();j++)
					{
						outcome[count]=0;
						for(int k=0;k<rowNumber*colNumber;k++)
						{
							if(i==count||count==(i+1)%10)
							{
								outcome[count]+=weights[count][k]*(float)(dataset.get(count).get(j).charAt(k)-'0');
							}
						}
						outcome[count]=(float)1/(float)(1+Math.exp(-outcome[count]));
						errorGradient[count]=outcome[count]-(i==count?1:0);
					}
					for(int k=0;k<rowNumber*colNumber;k++)
					{
						lastErrorGradient[count]=0;
						for(int j=0;j<dataset.get(count).size();j++)
						{
							lastErrorGradient[count]+=(float)(dataset.get(count).get(j).charAt(k)-'0')*errorGradient[count];
						}
						lastErrorGradient[count]=lastErrorGradient[count]*learningRate/dataset.get(count).size();
						weights[count][k]-=lastErrorGradient[count];
					}
				}
			}
			//*************************************************************
			//training set accuracy
			float result=0;
			double overall=0;
			for(int indexFinal=0;indexFinal<10;indexFinal++)
			{
				number[indexFinal]=0;
				for(int j=0;j<dataset.get(indexFinal).size();j++)
				{
					double min=1000000;
					int minIndex=0;
					for(int i=0;i<10;i++)
					{
						result=0;
						for(int k=0;k<rowNumber*colNumber;k++)
						{
							result+=weights[i][k]*(float)(dataset.get(indexFinal).get(j).charAt(k)-'0');
						}
						if(result<min)
						{
							min=result;
							minIndex=i;
						}
						/*
						System.out.print(i);
						System.out.print(":");
						System.out.println(result);	
						*/
					}
					if(minIndex==indexFinal)
					{
						number[indexFinal]+=1;
					}
					/*
					System.out.println(minIndex);
					System.out.println();
					*/
				}
				//accuracy[indexFinal]=number[indexFinal]/dataset.get(indexFinal).size();
				overall+=number[indexFinal];
			}
			overall/=2436;
			System.out.println(overall);
			//training set accuracy
			//*************************************************************
			countEpoch+=1;
		}
		
		//*************************
		//test set read
		System.out.println();
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
		//test set read
		//test set accuracy
		float result=0;
		double overall=0;
		for(int indexFinal=0;indexFinal<10;indexFinal++)
		{
			number[indexFinal]=0;
			for(int j=0;j<testset.get(indexFinal).size();j++)
			{
				double min=1000000;
				int minIndex=0;
				for(int i=0;i<10;i++)
				{
					result=0;
					for(int k=0;k<rowNumber*colNumber;k++)
					{
						result+=weights[i][k]*(float)(testset.get(indexFinal).get(j).charAt(k)-'0');
					}
					if(result<min)
					{
						min=result;
						minIndex=i;
					}
					/*
					System.out.print(i);
					System.out.print(":");
					System.out.println(result);	
					*/
				}
				if(minIndex==indexFinal)
				{
					number[indexFinal]+=1;
				}
				/*
				System.out.println(minIndex);
				System.out.println();
				*/
			}
			//accuracy[indexFinal]=number[indexFinal]/dataset.get(indexFinal).size();
			overall+=number[indexFinal];
		}
		overall/=444;
		System.out.println(overall);
		System.out.println();
		//test set accuracy
		//confusion matrix
		result=0;
		overall=0;
		for(int indexFinal=0;indexFinal<10;indexFinal++)
		{
			number[indexFinal]=0;
			for(int j=0;j<testset.get(indexFinal).size();j++)
			{
				double min=1000000;
				int minIndex=0;
				for(int i=0;i<10;i++)
				{
					result=0;
					for(int k=0;k<rowNumber*colNumber;k++)
					{
						result+=weights[i][k]*(float)(testset.get(indexFinal).get(j).charAt(k)-'0');
					}
					if(result<min)
					{
						min=result;
						minIndex=i;
					}
					/*
					System.out.print(i);
					System.out.print(":");
					System.out.println(result);	
					*/
				}
				confusionMatrix[indexFinal][minIndex]+=1;
				/*
				System.out.println(minIndex);
				System.out.println();
				*/
			}
			//accuracy[indexFinal]=number[indexFinal]/dataset.get(indexFinal).size();
		}
		//confusion matrix
		
		//test
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
				System.out.printf("%.3f\t",confusionMatrix[i][j]/(float)testset.get(i).size());
			}
			System.out.println();
		}

		/*
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
		*/
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