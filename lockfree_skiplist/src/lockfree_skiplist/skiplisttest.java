package lockfree_skiplist;

import junit.framework.TestCase;

public class skiplisttest extends TestCase
{
	private final static int THREADS = 8;
	private final static int TEST_SIZE = 128;
	private final static int PER_THREAD = TEST_SIZE / THREADS;
	skiplist instance;
	Thread[] thread = new Thread[THREADS];

	//Constructor
	public skiplisttest()
	{
		instance = new skiplist();
	}
	
	//Sequential calls
	public void testSequential()
	{
		System.out.println("sequential add, contains, and remove");
		System.out.println();
		System.out.println();

		for (int i = 0; i < TEST_SIZE; i++)
		{
			//System.out.printf("Inserting value: %d\n", i);
			instance.add(i);
			System.out.printf("Inserted value: %d\n", i);
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			System.out.printf("Searching for value: %d\n", i);
			if (!instance.contains(i))
			{
				System.out.printf("Value: %d not found\n", i);
				fail("bad contains: " + i );
			}
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			System.out.printf("Removing value: %d\n", i);
			if (!instance.remove(i))
			{
				System.out.printf("Could not remove value: %d\n", i);
				fail("bad remove: " + i );
			}
		}
		
		System.out.println();
		System.out.println();
		
	}

	//Parallel add, sequential removes
	public void testParallelAdd()  throws Exception
	{
		System.out.println("parallel add");
		System.out.println();
		System.out.println();

		
		for (int i = 0; i < THREADS; i++)
		{
			thread[i] = new AddThread(i * PER_THREAD);
		}
		for (int i = 0; i < THREADS; i ++)
		{
			thread[i].start();
		}
		for (int i = 0; i < THREADS; i ++)
		{
			thread[i].join();
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			System.out.printf("Searching for value: %d\n", i);
			if (!instance.contains(i))
			{
				System.out.printf("Value: %d not found\n", i);
				fail("bad contains: " + i );
			}
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			System.out.printf("Removing value: %d\n", i);
			if (!instance.remove(i))
			{
				System.out.printf("Could not remove value: %d\n", i);
				fail("bad remove: " + i );
			}
		}
		
		System.out.println();
		System.out.println();

	}

	//Sequential adds, parallel removes
	public void testParallelRemove()  throws Exception
	{
		System.out.println("parallel remove");
		System.out.println();
		System.out.println();

		
		for (int i = 0; i < TEST_SIZE; i++)
		{
			//System.out.printf("Inserting value: %d\n", i);
			instance.add(i);
			System.out.printf("Inserted value: %d\n", i);
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			System.out.printf("Searching for value: %d\n", i);
			if (!instance.contains(i))
			{
				System.out.printf("Value: %d not found\n", i);
				fail("bad contains: " + i );
			}
		}
		for (int i = 0; i < THREADS; i++)
		{
			thread[i] = new RemoveThread(i * PER_THREAD);
		}
		for (int i = 0; i < THREADS; i ++)
		{
			thread[i].start();
		}
		for (int i = 0; i < THREADS; i ++)
		{
			thread[i].join();
		}
		
		System.out.println();
		System.out.println();

	}

	//Parallel adds, removes
	public void testParallelBoth()  throws Exception
	{
		System.out.println("parallel both");
		System.out.println();
		System.out.println();

		Thread[] myThreads = new Thread[2 * THREADS];
		for (int i = 0; i < THREADS; i++)
		{
			myThreads[i] = new AddThread(i * PER_THREAD);
			myThreads[i + THREADS] = new RemoveThread(i * PER_THREAD);
		}
		for (int i = 0; i < 2 * THREADS; i ++)
		{
			myThreads[i].start();
		}
		for (int i = 0; i < 2 * THREADS; i ++)
		{
			myThreads[i].join();
		}
		
		System.out.println();
		System.out.println();

	}
	class AddThread extends Thread
	{
		int value;
		AddThread(int i)
		{
			value = i;
		}
		public void run() {
			for (int i = 0; i < PER_THREAD; i++)
			{
				System.out.printf("Inserting value: %d by thread: %d\n", value + i, value/PER_THREAD);
				instance.add(value + i);
				System.out.printf("Inserted value: %d by thread: %d\n", value + i, value/PER_THREAD);
			}
		}
	}
	class RemoveThread extends Thread
	{
		int value;
		RemoveThread(int i)
		{
			value = i;
		}
		public void run()
		{
			for (int i = 0; i < PER_THREAD; i++)
			{
				System.out.printf("Removing value: %d by thread: %d\n", value + i, value/PER_THREAD);
				if (!instance.remove(value + i))
				{
					System.out.printf("Could not remove value: %d\n", i);
					fail("RemoveThread: duplicate remove: " + (value + i));
				}
			}
		}
	}

}
