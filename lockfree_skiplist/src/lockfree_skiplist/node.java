package lockfree_skiplist;

import java.util.concurrent.atomic.AtomicMarkableReference;

public final class node
{
	private final int data;
	private int height;
	final AtomicMarkableReference<node>[] next;
	
	//Constructor
	@SuppressWarnings("unchecked")
	public node(int data,int height)
	{
		this.data = data;
		this.height = height;
		this.next = new AtomicMarkableReference[height + 1];
		for(int i = 0; i <= height; i++)
		{
			next[i] = new AtomicMarkableReference<node>(null, false);
		}
	}
	
	public int getdata()
	{
		return this.data;
	}
	public int getheight()
	{
		return this.height;
	}
}