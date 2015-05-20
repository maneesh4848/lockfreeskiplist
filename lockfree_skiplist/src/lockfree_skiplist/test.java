package lockfree_skiplist;


public class test
{
	public static void main(String[] args)
	{
		long starttime = System.nanoTime();
		
		skiplisttest listtest = new skiplisttest();
		listtest.testSequential();
		/*try
		{
			listtest.testParallelAdd();
		}
		catch (Exception e)
		{
		}
		try
		{
			listtest.testParallelRemove();
		}
		catch (Exception e1)
		{
		}*/
		try
		{
			listtest.testParallelBoth();
		}
		catch (Exception e)
		{
		}
		
		long endtime = System.nanoTime();
		System.out.println(endtime-starttime);
		System.exit(0);
	}
}
