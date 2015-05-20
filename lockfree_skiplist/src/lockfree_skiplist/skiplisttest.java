package lockfree_skiplist;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestCase;

public class skiplisttest extends TestCase
{
	private final static int THREADS = 8;
	private final static int TEST_SIZE = 128;
	private final static int PER_THREAD = TEST_SIZE / THREADS;
	skiplist instance;
	Thread[] thread = new Thread[THREADS];
	AddThread[] addthreads;
	RemoveThread[] removethreads;
	mythread[] parallel;
	ArrayList<Integer> addset;
	ArrayList<Integer> removeset;
	ArrayList<Integer> containsset;
	int numseconds;
	private boolean printbit;
	boolean done;

	//Constructor
	public skiplisttest()
	{
		numseconds = 0;
		instance = new skiplist();
		printbit = false;
		done = false;
		Random rand = new Random();
		addset = new ArrayList<>(512);
		while(addset.size() < 512)
		{
			int temp = rand.nextInt(2048) - 1024;
			if(!addset.contains(temp))
			{
				addset.add(temp);
			}
		}
		removeset = new ArrayList<>(512);
		while(removeset.size() < 512)
		{
			int temp = rand.nextInt(2048) - 1024;
			if(!removeset.contains(temp))
			{
				removeset.add(temp);
			}
		}
		containsset = new ArrayList<>(1024);
		while(containsset.size() < 1024)
		{
			int temp = rand.nextInt(2048) - 1024;
			if(!containsset.contains(temp))
			{
				containsset.add(temp);
			}
		}
		starttimer();
	}
	
	//Sequential calls
	public void testSequential()
	{
		System.out.println("sequential add, contains, and remove");
		System.out.println();
		System.out.println();

		for (int i = 0; i < TEST_SIZE; i++)
		{
			if(!instance.add(i));
			{
				//System.out.printf("%d already present in list\n", i);
				//fail("bad insert: " + i);
			}
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			if (!instance.contains(i))
			{
				//System.out.printf("Value: %d not found\n", i);
				//fail("bad contains: " + i );
			}
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			if (!instance.remove(i))
			{
				//System.out.printf("Could not remove value: %d\n", i);
				//fail("bad remove: " + i );
			}
		}
		
		System.out.println("working");
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
		for (int i = 0; i < THREADS; i++)
		{
			thread[i].start();
		}
		for (int i = 0; i < THREADS; i++)
		{
			thread[i].join();
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			if (!instance.contains(i))
			{
				System.out.printf("Value: %d not found\n", i);
				//fail("bad contains: " + i );
			}


		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			if (!instance.remove(i))
			{
				System.out.printf("Could not remove value: %d\n", i);
				//fail("bad remove: " + i );
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
			if(!instance.add(i));
			{
				System.out.printf("%d already present in list\n", i);
				//fail("bad insert: " + i);
			}
		}
		for (int i = 0; i < TEST_SIZE; i++)
		{
			//System.out.printf("Searching for value: %d\n", i);
			if (!instance.contains(i))
			{
				System.out.printf("Value: %d not found\n", i);
				//fail("bad contains: " + i );
			}
		}
		for (int i = 0; i < THREADS; i++)
		{
			thread[i] = new RemoveThread(i * PER_THREAD);
		}
		for (int i = 0; i < THREADS; i++)
		{
			thread[i].start();
		}
		for (int i = 0; i < THREADS; i++)
		{
			thread[i].join();
		}
		
		System.out.println();
		System.out.println();

	}

	//Parallel adds, removes
	public void testParallelBoth()  throws Exception
	{
		printbit = true;
		System.out.println("parallel both");
		System.out.println();
		System.out.println();
		
		
		System.out.print("Time\t\t\t\t");
		for(int i = 0; i < THREADS; i++)
		{
			System.out.print("Thread " + Integer.toString(i) + "\t");
		}
		/*for(int i = 0; i < THREADS; i++)
		{
			System.out.print("RemoveThread " + Integer.toString(i) + "\t");
		}*/
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------------------------");

		parallel = new mythread[THREADS];
		for(int i = 0; i < THREADS; i++)
		{
			parallel[i] = new mythread();
		}
		for(int i = 0; i < THREADS; i++)
		{
			parallel[i].start();
		}
		for(int i = 0; i < THREADS; i++)
		{
			parallel[i].join();
		}
		
		System.out.println("working");
		System.out.println();
		System.out.println();
		printbit = false;
	}
	
	class AddThread extends Thread
	{
		int value;
		public String threadstat;
		public int threadvalue;
		public AddThread(int i)
		{
			value = i;
			threadstat = "sleeping";
			threadvalue = -1;
		}
		public void run() {
			for (int i = 0; i < PER_THREAD; i++)
			{
				threadstat = "inserting";
				threadvalue = value + i;
				if(!instance.add(value + i))
				{
					//System.out.printf("Value %d already present in list\n", value+i);
				}
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
				threadstat = "sleeping";
				threadvalue = -1;
			}
		}
	}
	class RemoveThread extends Thread
	{
		int value;
		public String threadstat;
		public int threadvalue;
		public RemoveThread(int i)
		{
			value = i;
			threadstat = "sleeping";
			threadvalue = -1;
		}
		public void run()
		{
			for (int i = 0; i < PER_THREAD; i++)
			{
				threadstat = "removing";
				threadvalue = value + i;
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
				if (!instance.remove(value + i))
				{
					//System.out.printf("Could not remove value: %d\n", value + i);
					//fail("RemoveThread: duplicate remove: " + (value + i));
				}
				
				threadstat = "sleeping";
				threadvalue = -1;
			}
		}
	}
	
	class mythread extends Thread
	{
		public String threadstat;
		public int threadvalue;
		public mythread()
		{
			threadstat = "sleeping";
			threadvalue = Integer.MIN_VALUE;
		}
		public void run()
		{
			while(!done)
			{
				Random rand = new Random();
				int op = rand.nextInt(4);
				if(addset.size() == 0 && removeset.size() == 0 && containsset.size() == 0)
					done = true;
				else if(op < 2)
				{
					if(containsset.size() > 0)
					{
						int index = rand.nextInt(containsset.size());
						threadstat = "searching";
						if(containsset.size() > index)
						{
							threadvalue = containsset.get(index);
							containsset.remove(index);
						}
						try
						{
							Thread.sleep(10);
						}
						catch (InterruptedException e)
						{
						}
						if(!instance.contains(threadvalue))
						{
							//System.out.printf("%d not in list\n", threadvalue);
						}
					}
					threadstat = "sleeping";
					threadvalue = Integer.MIN_VALUE;
				}
				else if(op < 3)
				{
					if(addset.size() > 0)
					{
						int index = rand.nextInt(addset.size());
						threadstat = "inserting";
						if(addset.size() > index)
						{
							threadvalue = addset.get(index);
							addset.remove(index);
						}
						try
						{
							Thread.sleep(10);
						}
						catch (InterruptedException e)
						{
						}
						if(!instance.add(threadvalue))
						{
							//System.out.printf("Value %d already present in list\n", threadvalue);
						}
					}
					threadstat = "sleeping";
					threadvalue = Integer.MIN_VALUE;
				}
				else
				{
					if(removeset.size() > 0)
					{
						int index = rand.nextInt(removeset.size());
						threadstat = "deleting";
						if(removeset.size() > index)
						{
							threadvalue = removeset.get(index);
							removeset.remove(index);
						}
						try
						{
							Thread.sleep(10);
						}
						catch (InterruptedException e)
						{
						}
						if(!instance.remove(threadvalue))
						{
							//System.out.printf("Could not remove value: %d\n", threadvalue);
						}
					}
					threadstat = "sleeping";
					threadvalue = Integer.MIN_VALUE;
				}
			}
		}
	}
	
	public void printstatus()
	{
		if(printbit)
		{
			for(int i = 0; i < THREADS; i++)
			{
				if(parallel[i].threadstat == "sleeping")
					System.out.print(parallel[i].threadstat + "\t");
				else
				{
					System.out.print(parallel[i].threadstat + " " + Integer.toString(parallel[i].threadvalue) + "\t");
				}
			}
		}
	}
	
	public void starttimer()
	{
		numseconds = 0;
		Timer timer = new Timer();
		TimerTask newtask = new TimerTask()
		{
			@Override
			public void run()
			{
				if(printbit)
				{
					System.out.print("t = " + Integer.toString(numseconds) + "ms:\t\t\t");
					printstatus();
					System.out.println();
				}
				numseconds += 10;
				/*if(numseconds % 250 == 0)
				{
					instance.print();
				}*/
			}
		};
		timer.scheduleAtFixedRate(newtask, new Date(), 10);
	}
}
