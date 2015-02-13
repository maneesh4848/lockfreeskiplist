package lockfree_skiplist;

public class test
{
	public static void main(String[] args)
	{
		skiplisttest test = new skiplisttest();
		test.testSequential();
		try
		{
			test.testParallelBoth();
		}
		catch (Exception e)
		{
		}
		try
		{
			test.testParallelAdd();
		}
		catch (Exception e)
		{
		}
		try
		{
			test.testParallelRemove();
		}
		catch (Exception e)
		{
		}
	}
}
