//Queue class
public class Queue {
	Node []array;
	int front,rear,size;
	int capacity;
	
	
	public Queue(int capacity) {
		this.capacity = capacity;
		array=new Node[capacity];
		front=this.size=0;
		rear=capacity-1;
	}
	
	//This method is used to insert elements into the queue
	public void insert(Node node) {
		if(isFull(this))
			return;
		this.rear = (this.rear + 1)%this.capacity; 
        this.array[this.rear] = node; 
        this.size = this.size + 1; 
	}
	
	//check if queue is full or not
	boolean isFull(Queue q) {
		return (q.size==q.capacity);
	}
	
	//this method is used to remove the top element of the queue
	public Node get() {
		if(isEmpty(this)) {
			System.out.println("No data");
			System.exit(0);
		}
		Node node = this.array[this.front]; 
        this.front = (this.front + 1)%this.capacity; 
        this.size = this.size - 1; 
        return node; 
		
	}
	//This method is used to get the size of the queue
	public int size(Queue queue) {
		return queue.size;
	}
	
	//Method to check if the queue is empty or not 
	boolean isEmpty(Queue q) 
    {  
		return (q.size == 0);
	} 
	
}
