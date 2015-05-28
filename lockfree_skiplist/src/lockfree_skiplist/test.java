package lockfree_skiplist;


public class test
{
	public static void main(String[] args)
	{		
		skiplisttest listtest = new skiplisttest();
		//listtest.testSequential();
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
		
		System.exit(0);
	}
}
