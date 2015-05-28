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
		int height = 0;
		while(rand.nextInt(2) == 1)
		{
			height++;
		}
		return height;
	}
	
	public boolean find(int value,node[] pred,node[] succ)
	{
		node before = null,curr = null,after = null;
		while(true)
		{
			before = head;
			boolean done = true;
			
			//Traversing the list
			for(int level = maxheight; level >= 0 && done; level--)
			{
				curr = before.next[level].getReference();
				while(true)
				{
					boolean[] temp = {false};
					//after = curr.next[level].getReference();
					after = curr.next[level].get(temp);
					
					//If temp[0] is true, node is marked
					while(temp[0])
					{
						//Physically deleting the node
						done = before.next[level].compareAndSet(curr, after, false, false);
						
						//If deleting was unsuccessful,
						//restarting the process
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
				
				//Recording predecessor
				//and successor nodes
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
			
			//Traversing the list
			for(int level = maxheight; level >= 0; level--)
			{
				curr = before.next[level].getReference();
				while(true)
				{
					boolean[] temp = {false};
					after = curr.next[level].get(temp);
					
					//If temp[0] is true, node is marked
					//Skipping over the marked nodes
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
	
	public void print()
	{
		for(int level = maxheight; level >= 0; level--)
		{
			System.out.print(level);
			boolean[] temp = {false};
			node curr = head;
			node after = curr.next[level].get(temp);
			while(after != null)
			{
				System.out.print(Integer.toString(curr.getdata()) + ' ');
				int flag = 0;
				while(temp[0] && after != null)
				{
					flag = 1;
					curr = after;
					after = after.next[level].get(temp);
				}
				if(flag == 0)
				{
					after = after.next[level].get(temp);
				}
			}
			System.out.println();
		
		}
	}
	
	
	public boolean add(int value)
	{
		//highestlevel: highest level until which the node is inserted
		int highestlevel = randomheight();
		node[] preds = new node[maxheight+1];
		node[] succs = new node[maxheight+1];
		while(true)
		{
			//Getting the predecessor and successor nodes
			boolean present = find(value,preds,succs);
			
			//If the node is already present, we return false
			if(present)
			{
				return false;
			}
			else
			{
				//Initializing the node 
				//and making it point to the successor nodes at all levels 
				node curr = new node(value,highestlevel);
				for(int level = 0; level <= highestlevel; level++)
				{
					curr.next[level].set(succs[level], false);
				}
				
				//Using compareandset to add the node to the bottom level
				boolean added = preds[0].next[0].compareAndSet(succs[0], curr, false, false);
				
				//If the operation does not succeed
				//The predecessors or successors might be modified, retry
				if(!added)
				{
					continue;
				}
				
				//Adding node at all levels
				for(int level = 1; level <= highestlevel; level++)
				{
					//retry until added
					while(true)
					{
						boolean temp = preds[level].next[level].compareAndSet(succs[level], curr, false, false);
						if(temp)
							break;
						//Using find because predecessors and successors are modified
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
			//Getting predecessor and successor nodes
			boolean present = find(value,preds,succs);
			
			//Checking for presence of node in skip list
			if(!present)
			{
				return false;
			}
			
			else
			{
				node curr = succs[0];
				
				//Marking all levels except bottom level
				for(int level = curr.getheight(); level >= 1; level--)
				{
					//temp: contains the logical 'mark' value
					boolean[] temp = {false};
					node succ = curr.next[level].get(temp);
					
					//marking the nodes while they are not marked
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
					//Marking the bottom level
					boolean marked = curr.next[0].compareAndSet(succ, succ, false, true);
					
					//calling find method to clear out marked nodes
					//can be skipped
					if(marked)
					{
						find(value,preds,succs);
						return true;
					}
					else
					{
						succ = curr.next[0].get(temp);
						
						//Node is already marked, returning false
						if(temp[0])
							return false;
					}
				}
			}
		}
	}
}
