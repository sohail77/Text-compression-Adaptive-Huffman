import java.util.Comparator;

public class CustomComparator implements Comparator<Node> {

	@Override
	public int compare(Node a, Node b) {
		return a.frequency-b.frequency;
	}
	
//	public void countFrequencies(String line) {
//	for(int i=0;i<line.length();i++) {
//		if(contains(line.charAt(i))) {
//			for(int j=0;j<map.size();j++) {
//				if(map.get(j).c==line.charAt(i)) {
//					CustomMap customMapItem=new CustomMap(map.get(j).c,map.get(j).frequency+1);
//					map.set(j, customMapItem);
//				}
//			}
//		}else {
//			CustomMap item=new CustomMap(line.charAt(i),1);
//			map.add(item);
//		}
//	}
//}
//
//public boolean contains(char ch) {
//	for(int i=0;i<map.size();i++) {
//		if(map.get(i).c==ch) {
//			return true;
//		}
//	}
//	return false;
//}
//
//public void addToQueue() {
//	queue=new PriorityQueue<Node>(map.size(),new CustomComparator());
//	for(int i=0;i<map.size();i++) {
//		node =new Node();
//		node.letter=map.get(i).c;
//		node.frequency=map.get(i).frequency;
//		queue.add(node);
//		
//	}
//}
}
