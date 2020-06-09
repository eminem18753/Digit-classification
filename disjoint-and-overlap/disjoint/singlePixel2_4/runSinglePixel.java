public class runSinglePixel
{
	public runSinglePixel()
	{
		
	}
	public static void main(String[] args)
	{
		singlePixel singlePixelTest=new singlePixel("optdigits-orig_train.txt","optdigits-orig_test.txt");
		singlePixelTest.singlePixelAlgorithm();
	}
}