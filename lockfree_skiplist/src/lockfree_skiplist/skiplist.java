package lockfree_skiplist;

import java.util.Random;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class skiplist
{
	private static final int maxheight = 20;
	final node head = new node(Integer.MIN_VALUE, maxheight);
	final node tail = new node(Integer.MAX_VALUE, maxheight);
	
	//Constructor
	public skiplist()
	{
		for(int i=0;i < head.next.length;i++)
		{
			head.next[i] = new AtomicMarkableReference<node>(tail, false);
		}
	}
	
	private int randomheight()
	{
		Random rand = new Random();
	    return(rand.nextInt(maxheight + 1));
	}
	
	public boolean find(int value,node[] pred,node[] succ)
	{
		node before = null,curr = null,after = null;
		while(true)
		{
			before = head;
			boolean done = true;
			for(int level = maxheight; level >= 0 && done; level--)
			{
				curr = before.next[level].getReference();
				while(true)
				{
					boolean[] temp = {false};
					after = curr.next[level].getReference();
					after = curr.next[level].get(temp);
					while(temp[0])
					{
						done = before.next[level].compareAndSet(curr, after, false, false);
						if(!done)
							break;
						curr = before.next[level].getReference();
						after = curr.next[level].get(temp);
					}
					if(curr.getdata() < value && done)
					{
						before = curr;
						curr = after;
					}
					else
						break;
				}
				if(done)
				{
					pred[level] = before;
					succ[level] = curr;
				}
				else
					break;
			}
			if(done)
				return curr.getdata()==value;
		}
	}
	
	public boolean contains(int value)
	{
		node before = null,curr = null,after = null;
		while(true)
		{
			before = head;
			for(int level = maxheight; level >= 0; level--)
			{
				curr = before.next[level].getReference();
				while(true)
				{
					boolean[] temp = {false};
					after = curr.next[level].get(temp);
					while(temp[0])
					{
						before = curr;
						curr = before.next[level].getReference();
						after = curr.next[level].get(temp);
					}
					if(curr.getdata() < value)
					{
						before = curr;
						curr = after;
					}
					else
						break;
				}
			}
			return curr.getdata()==value;
		}
	}
	
	public boolean add(int value)
	{
		int highestlevel = randomheight();
		node[] preds = new node[maxheight+1];
		node[] succs = new node[maxheight+1];
		//System.out.println("YOLO");
		while(true)
		{
			boolean present = find(value,preds,succs);
			if(present)
			{
				return false;
			}
			else
			{
				node curr = new node(value,highestlevel);
				for(int level = 0; level <= highestlevel; level++)
				{
					curr.next[level].set(succs[level], false);
				}
				boolean added = preds[0].next[0].compareAndSet(succs[0], curr, false, false);
				if(!added)
				{
					continue;
				}
				for(int level = 1; level <= highestlevel; level++)
				{
					while(true)
					{
						boolean temp = preds[level].next[level].compareAndSet(succs[level], curr, false, false);
						if(temp)
							break;
						find(value,preds,succs);
					}
				}
				return true;
			}
		}
	}
	
	public boolean remove(int value)
	{

		node[] preds = new node[maxheight+1];
		node[] succs = new node[maxheight+1];
		while(true)
		{
			boolean present = find(value,preds,succs);
			if(!present)
			{
				return false;
			}
			else
			{
				node curr = succs[0];
				for(int level = curr.getheight(); level >= 1; level--)
				{
					boolean[] temp = {false};
					node succ = curr.next[level].get(temp);
					while(!temp[0])
					{
						curr.next[level].attemptMark(succ, true);
						succ = curr.next[level].get(temp);
					}
				}
				boolean[] temp = {false};
				node succ = curr.next[0].get(temp);
				while(true)
				{
					boolean marked = curr.next[0].compareAndSet(succ, succ, false, true);
					if(marked)
					{
						find(value,preds,succs);
						return true;
					}
					else
					{
						succ = curr.next[0].get(temp);
						if(temp[0])
							return false;
					}
				}
			}
		}
	}
	
}
