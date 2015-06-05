package lockfree_skiplist;

public class test
{
	public static void main(String[] args)
	{
		long [][] small,big;
		small = new long[6][10];
		big = new long[6][5];

		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				System.out.println(i);
				System.out.println(j);
				skiplisttest listtest = new skiplisttest(i,0);
				try
				{
					small[i][j] = listtest.testParallelBoth();
				}
				catch (Exception e)
				{
				}
			}
		}
		/*for(int i = 0; i < 10; i++)
		{
			skiplisttest listtest = new skiplisttest(6,0);
			try
			{
				small[6][i] = listtest.testContains();
			}
			catch (Exception e)
			{
			}
		}*/

		long avg[] = new long[6];
		for(int i = 0; i < 5; i++)
		{
			//System.out.println(Arrays.asList(small[i]));
			for(int j = 0; j < 10; j++)
			{
				System.out.println(Integer.toString(i) + " " + Integer.toString(j) + " " + Long.toString(small[i][j]));
				avg[i] += small[i][j];
			}
			avg[i] /= 10;
			System.out.println(Integer.toString(i) + " " + Long.toString(avg[i]));
		}
		//System.out.println(Arrays.asList(avg));
		
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 5; j++)
			{
				skiplisttest listtest = new skiplisttest(i,1);
				try
				{
					big[i][j] = listtest.testParallelBoth();
				}
				catch (Exception e)
				{
				}
			}
		}
		
		long avg2[] = new long[6];
		for(int i = 0; i < 5; i++)
		{
			//System.out.println(Arrays.asList(big[i]));
			for(int j = 0; j < 5; j++)
			{
				System.out.println(Integer.toString(i) + " " + Integer.toString(j) + " " + Long.toString(big[i][j]));
				avg2[i] += big[i][j];
			}
			avg2[i] /= 10;
			System.out.println(Integer.toString(i) + " " + Long.toString(avg2[i]));
		}
		//System.out.println(Arrays.asList(avg2));

		System.exit(0);
	}
}
