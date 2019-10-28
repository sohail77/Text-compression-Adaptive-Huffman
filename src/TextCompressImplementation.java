import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TextCompressImplementation implements FileCompressor {
	
	HashMap<String,Integer> map=new HashMap<String,Integer>();
	Node node;
	Node nodeArr[];
	String outputString="";
	String decodeString="";
	Node rootNode;
	Node decodeRoot;
	HashMap<String,Integer> decodeMap=new HashMap<String,Integer>();
	HashMap<Character,String> codeBook=new HashMap<Character,String>();
	HashMap<String,String> intermediateMap=new HashMap<String,String>();

	//this method is used to encode the files using adaptive huffman encoding technique
	@Override
	public boolean encode(String input_filename, int level, boolean reset, String output_filename) {
		File file=null;
		BufferedReader reader=null;
		try {
			
			if(0<input_filename.length()) {
				file=new File(input_filename);
				
			}
			
			String line;

			try {
				//open file using buffered reader
				reader=new BufferedReader(new FileReader(file));
			}catch(FileNotFoundException e) {
				System.out.println("No such file exists, Check your filename");
				System.exit(0);
			}
			
			try {
				
				//create the starting nodes of newCharacter and End of line
				Node root =new Node("root",1);
				Node newChar=new Node("newChar",0);
				newChar.left=null;
				newChar.right=null;
				Node EOF=new Node("EOF",0);
				EOF.left=null;
				EOF.right=null;
				root.left=newChar;
				root.right=EOF;


				String[] array=new String[(int)file.length()];
				int increment=0;
				
				//check if file is empty, if empty return false;
				if(file.length()<=1) {
					System.out.println("File is empty");
					return false;
				}else {
						//add each line from text file into array of strings
						while((line=reader.readLine())!=null) {
						
						//add '\n' to end of each line
						array[increment]=line + '\n';
						increment++;
					}
					
					//Remove '\n' from the last line of the file
					for(int e=0;e<array.length-1;e++) {
						if(array[e+1]==null) {
							String string = array[e];
							String[] parts = string.split("\n");
							String part1 = parts[0];
							array[e]=part1;
							break;
						}
					}

						int currentLevel=2;
					
					char []charArr=new char[(int)file.length()-1];
					int k=0;
					
					//convert all the lines into a single character array
					for(int i=0;i<charArr.length;i++) {
						if(array[i]!=null) {
						for(int j=0;j<array[i].length();j++) {
							charArr[k]=array[i].charAt(j);
							k++;
						}
						}
					}
					
					int lvl=1;
					
					//add level to encoded string
					outputString+="" + level;
					
					//add reset value to encoded string
					if(!reset) {
						outputString+="0";
					}else {
						outputString+="1";
					}
					map.put("EOF",0);
					map.put("newChar",0);
	
					
					for(int i=0;i<charArr.length+1;i++) {
						if(rootNode!=null) {
							root=rootNode;
						}
						if(i<charArr.length) {
						
						//check current level and select number of characters based on current level
						if(i<currentLevel) {
							Node newNode=new Node("" + charArr[i],1);
							
							//if an already existing character is encountered its frequency is increased.
							if(map.containsKey("" + charArr[i])) {
								outputString+=intermediateMap.get("" + charArr[i]);
								map.put("" + charArr[i], map.get("" + charArr[i])+1);
								
								//else increment newCharacter node and add that new character to the tree
							}else {
								if(map.containsKey("newChar")) {
									map.put("newChar", map.get("newChar")+1);
									addToIntermediateMap(root,"");
	
								}
								
								//this method adds new node to the intermediate tree.
								traverse(root,newNode);
								outputString+=intermediateMap.get("newChar") + charArr[i];
								addToIntermediateMap(root,"");
								map.put("" + charArr[i], 1);
							}
							
							
						}
						//if currentLevel is reached then the tree rebuilding process starts
						else {
							
							//this calculates next level 
							if(lvl<=level) {
								currentLevel=currentLevel*2+2;
								lvl++;
							}else {
								int b=(int)Math.pow(2, level);
								currentLevel+=b;
							}
							i=i-1;
							nodeArr=new Node[map.size()];
							int l=0;
							
							//treeMap is used to sort the elements of the map 
					        Map<String, Integer> treeMap = new TreeMap<String, Integer>(map);
					        
					        //Copy treeMap data into an array of type Node class
							for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
							    String key = entry.getKey();
							    int value = entry.getValue();
							    Node tempNode=new Node();
							    tempNode.letter=key;
							    tempNode.frequency=value;
							    nodeArr[l]=tempNode;
							    l++;
							}
						
							//sort the array using insertion sort based on frequency
							sortArray(nodeArr);
							
							//Copy data from that array into a queue so first two elements can be removed easily
							Queue queue=new Queue(nodeArr.length);
							for(int q=0;q<nodeArr.length;q++) {
								queue.insert(nodeArr[q]);
							}
							
							//function call to rebuild the tree.
							rebuildTree(queue,reset,true);
						}
						}
						//this else is reached if elements are finished and level is not reached.
						else {
							nodeArr=new Node[map.size()];
							int l=0;
							if(level>0) {
								outputString+=intermediateMap.get("EOF");
							}
							//treeMap is used to sort the elements of the map 
					        Map<String, Integer> treeMap = new TreeMap<String, Integer>(map);
	
					        //Copy treeMap data into an array of type Node class
							for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
							    String key = entry.getKey();
							    int value = entry.getValue();
							    Node tempNode=new Node();
							    tempNode.letter=key;
							    tempNode.frequency=value;
							    nodeArr[l]=tempNode;
							    l++;
							}
							sortArray(nodeArr);
							Queue queue=new Queue(nodeArr.length);
							for(int q=0;q<nodeArr.length;q++) {
								queue.insert(nodeArr[q]);
							}
							rebuildTree(queue,reset,true);
						}
					}
					
					
					if(level==0) {
						outputString+=intermediateMap.get("EOF");
					}
					
					//write the output data to a text file
						BufferedWriter output=null;
						try {
				            File writeFile = new File(output_filename);
				            output = new BufferedWriter(new FileWriter(writeFile));
				            output.write(outputString);
				        } catch ( IOException e ) {
				        	System.out.println("Can't create file ");
				        	return false;
				        	} finally {
				          if ( output != null ) {
				            output.close();
				          }
				        }
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}catch(NullPointerException e) {
			System.out.println("Enter a correct file Name");
		}
		//close the reader that was reading the file
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//This method sorts the array using insertion sort technique 
	//the sorting is done based on frequency values in ascending order
	public void sortArray(Node []nodeArr) {
		for(int i=1;i<nodeArr.length;i++) {
			Node tempNode=new Node();
			tempNode.letter=nodeArr[i].letter;
			tempNode.frequency=nodeArr[i].frequency;
			
			int j=i-1;
			
			while(j>=0&&nodeArr[j].frequency>tempNode.frequency) {
				nodeArr[j+1]=nodeArr[j];
				j=j-1;
			}
			
			nodeArr[j+1]=tempNode;
		}


	}
	
	//This method is used to rebuild the tree after the level is reached.
	//it uses Huffman encoding technique
	public void rebuildTree(Queue queue,boolean reset,boolean encode) {
		
		rootNode=null;
		while(!queue.isEmpty(queue)) {
			boolean toBeInserted=true;
			//take smallest element from the queue
			Node x=new Node();
			x=queue.get();
			//take second smallest element from the queue
			Node y=new Node();
			y=queue.get();

			//join the two smallest element from the queue into a new node
			Node temp=new Node();
			temp.letter=x.letter+y.letter;
			temp.frequency=x.frequency+y.frequency;
			temp.left=x;
			temp.right=y;
			
			rootNode =temp;
			if(!queue.isEmpty(queue)) {
			Node []tempArr=new Node[queue.size(queue)+1];
			
			//copy all the elements of the queue into an array along with the newly created temp Node 
			//which combines two smallest nodes
			//an array is used so that solution could be as simple as possible
			for(int i=0;i<tempArr.length;i++) {
				Node tempNode=new Node();
				if(!queue.isEmpty(queue)) {
					tempNode=queue.get();

				
				if(tempNode.frequency<temp.frequency) {
					tempArr[i]=tempNode;
				}else if(tempNode.frequency>temp.frequency) {
					if(toBeInserted) {
						tempArr[i]=temp;
						toBeInserted=false;
						if(i!=tempArr.length-1) {
							tempArr[i+1]=tempNode;
							i=i+1;
						}
					}else {
						tempArr[i]=tempNode;
					}
				}else {
					tempArr[i]=tempNode;
				}
			}else {
				tempArr[i]=temp;
			}
			}
			//Now that sorted data is again copied into a queue and looped again to combine the two smallest nodes
			for(int j=0;j<tempArr.length;j++) {
				queue.insert(tempArr[j]);
			}
		}
		}

		if(encode) {
			addToIntermediateMap(rootNode,"");
			
			//this resets the frequency after every rebuild if the reset is true 
			if(reset) {
				for (Map.Entry<String, Integer> entry : map.entrySet()) {
					map.put(entry.getKey(), 0);   
				}
			}
		}else {
			//This resets frequency during decoding
			if(reset) {
				for (Map.Entry<String, Integer> entry : decodeMap.entrySet()) {
					decodeMap.put(entry.getKey(), 0);   
				}
		}
		}
	}
	
	
	//This Method is used to add new Node to an intermediate tree i.e before rebuilding.
	public void traverse(Node root,Node newNode) {

		if(root.right==null) {
			
			Node temp=new Node();
			temp.letter=root.letter;
			temp.frequency=root.frequency;
			root.letter=root.letter + newNode.letter;
			root.left=temp;
			root.right=newNode;	
		}
		else {
			traverse(root.right,newNode);
		}
	}
	
	//This method is used to keep track of encoded bits of all the characters at different stages. 
	public void addToIntermediateMap(Node root,String s) {
		if(root.left==null&&root.right==null) {
			if(intermediateMap.containsKey(root.letter)){
				intermediateMap.remove(root.letter);
				intermediateMap.put(root.letter,s);
				
				//Add a character of ascii 157 for newCharNode in the codeBook map
				if(root.letter=="newChar") {
					if(codeBook.containsKey((char)157)){
						codeBook.remove((char)157);
						codeBook.put((char)157, s);
					}
				}
				
				//Add a character of ascii 255 for End-of-File Node in the codeBook map
				if(root.letter=="EOF") {
					if(codeBook.containsKey((char)255)){
						codeBook.remove((char)255);
						codeBook.put((char)255, s);
					}
				}
				if(codeBook.containsKey(root.letter.charAt(0))){
					codeBook.remove(root.letter.charAt(0));
					codeBook.put(root.letter.charAt(0), s);
				}
			}else {
				intermediateMap.put(root.letter,s);
				if(root.letter=="newChar") {
					codeBook.put((char)157, s);					
				}else if(root.letter=="EOF") {
					codeBook.put((char)255, s);					
				}else {
					codeBook.put(root.letter.charAt(0), s);
				}
			}
			return;
		}
		//recursion call to traverse to all the nodes
		addToIntermediateMap(root.left,s + "0");
		addToIntermediateMap(root.right,s + "1");

	}
	

	//This method is used to decode files
	@Override
	public boolean decode(String input_filename, String output_filename) {
		File decodeFile=null;
		BufferedReader decodeReader=null;
		try {
			if(0<input_filename.length()) {
				decodeFile=new File(input_filename);
				
			}
			
			String line;

			try {
				decodeReader=new BufferedReader(new FileReader(decodeFile));
			}catch(FileNotFoundException e) {
				System.out.println("No such file exists, Check your filename");
				System.exit(0);
			}
			try {
				//Add starting node sof newChar and End-of-file
				Node root =new Node("root",1);
				Node newChar=new Node("newChar",0);
				newChar.left=null;
				newChar.right=null;
				Node EOF=new Node("EOF",0);
				EOF.left=null;
				EOF.right=null;
				root.left=newChar;
				root.right=EOF;
				rootNode=root;


				String[] array=new String[(int)decodeFile.length()];
				int increment=0;
				//check if file is empty or not
				if(decodeFile.length()<=1) {
					System.out.println("File is empty");
					return false;
				}else {
					while((line=decodeReader.readLine())!=null) {
						
						array[increment]=line + '\n';
						increment++;
					}
					
					
					for(int e=0;e<array.length-1;e++) {
						if(array[e+1]==null) {
							String string = array[e];
							String[] parts = string.split("\n");
							String part1 = parts[0];
							array[e]=part1;
							break;
						}
					}
					
					
					
					char []charArr=new char[(int)decodeFile.length()-1];
					int k=0;
					char lev=array[0].charAt(0);
					int level;
					//set the level value based on character value in the text file
					if(lev=='0')
						level=0;
					else if(lev=='1')
						level=1;
					else if(lev=='2')
						level=2;
					else if(lev=='3')
						level=3;
					else if(lev=='4')
						level=4;
					else if(lev=='5')
						level=5;
					else if(lev=='6')
						level=6;
					else if(lev=='7')
						level=7;
					else if(lev=='8')
						level=8;
					else if(lev=='9')
						level=9;
					else {
						System.out.println("corrupt encoded file");
						return false;
					}
					
//					int currentLevel;
//					if(level==0) {
//						currentLevel=1;
//					}else 
						int currentLevel=2;
					
					char rs=array[0].charAt(1);
					//Create array of characters
					for(int i=0;i<charArr.length;i++) {
						if(array[i]!=null) {
						for(int j=0;j<array[i].length();j++) {
							if(i==0&&j<2) {
								j++;
							}else {
							charArr[k]=array[i].charAt(j);
							k++;
							}
						}
						}
					}
					int lvl=1;
					
					boolean startAgain=true;
					boolean reset;
					if(rs=='0')
						reset=false;
					else if(rs=='1')
						reset=true;
					else {
						System.out.println("corrupt encoded file");
						return false;
					}
					Node node=new Node();
					node=root;
					
					int counter=0;
					decodeMap.put("EOF", 0);
					decodeMap.put("newChar",0);
					for(int i=0;i<charArr.length;i++) {
						
						if(i<charArr.length) {
						if(counter<currentLevel) {
							if(startAgain) {
								node=rootNode;
							}
							//if not a leaf node then traverse down the tree
							if(node.right!=null||node.left!=null) {
								if(charArr[i]=='0'||charArr[i]=='1') {
									node=decodeTraverse(node,charArr[i]);
									startAgain=false;
								}
								//if a leaf node and a NewChar node then add the character to decoded string
							}else if(node.letter=="newChar") {
								decodeString+=charArr[i];
								startAgain=true;
								Node newNode=new Node();
								newNode.letter="" + charArr[i];
								newNode.frequency=1;
								traverse(rootNode,newNode);
								decodeMap.put("" + charArr[i],1);
								decodeMap.put("newChar", decodeMap.get("newChar")+1);
								i=i-1;
								counter+=1;
								
							}else if(node.right==null&&node.left==null){
								
								//if an already existing node then increase the counter
								if(decodeMap.containsKey(node.letter)) {
									decodeMap.put(node.letter, decodeMap.get(node.letter)+1);
								}
								counter+=1;
								startAgain=true;
								i=i-1;
								
								//finish the decoding if End-Of-file is reached.
								if(node.letter.equals("EOF")) {
									BufferedWriter output=null;
									try {
										//write the output to the file
							            File writeFile = new File(output_filename);
							            output = new BufferedWriter(new FileWriter(writeFile));
							            output.write(decodeString);
							        } catch ( IOException e ) {
							        	System.out.println("Can't create file ");
							        	return false;
							        } finally {
							          if ( output != null ) {
							            output.close();
							          }
							        }

									return true;
								}
								decodeString+="" + node.letter;
	
							}
						}else {
								//when the level reaches the current level then rebuild the tree
								startAgain=true;
								if(lvl<=level) {
									currentLevel=currentLevel*2+2;
									lvl++;
								}else {
									int b=(int)Math.pow(2, level);
									currentLevel+=b;
								}
								i=i-1;
								nodeArr=new Node[decodeMap.size()];
								int l=0;
								//tree map is used to sort the data
						        Map<String, Integer> treeMap = new TreeMap<String, Integer>(decodeMap);
						        
						        //data is copied to an array
								for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
								    String key = entry.getKey();
								    int value = entry.getValue();
								    Node tempNode=new Node();
								    tempNode.letter=key;
								    tempNode.frequency=value;
								    nodeArr[l]=tempNode;
								    l++;
								}
								//array is sorted based on frequency
								sortArray(nodeArr);
								
								//data is copied to a queue so its easier to remove the first two elements
								Queue queue=new Queue(nodeArr.length);
								for(int q=0;q<nodeArr.length;q++) {
									queue.insert(nodeArr[q]);
								}
								//rebuild the tree
								rebuildTree(queue,reset,false);
		
								node=rootNode;
							}
						}else {
							//when data is finished but the level is not reached 
							nodeArr=new Node[decodeMap.size()];
							int l=0;
	
					        Map<String, Integer> treeMap = new TreeMap<String, Integer>(decodeMap);
	
							for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
							    String key = entry.getKey();
							    int value = entry.getValue();
							    Node tempNode=new Node();
							    tempNode.letter=key;
							    tempNode.frequency=value;
							    nodeArr[l]=tempNode;
							    l++;
							}
							
							
							sortArray(nodeArr);
							Queue queue=new Queue(nodeArr.length);
							for(int q=0;q<nodeArr.length;q++) {
								queue.insert(nodeArr[q]);
							}
							rebuildTree(queue,reset,false);
							node=rootNode;
						}
						
					}
					
					BufferedWriter output=null;
					try {
			            File writeFile = new File(output_filename);
			            output = new BufferedWriter(new FileWriter(writeFile));
			            output.write(decodeString);
			        } catch ( IOException e ) {
			            e.printStackTrace();
			        } finally {
			          if ( output != null ) {
			            output.close();
			          }
			        }

					
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}catch(NullPointerException e) {
			System.out.println("Enter a correct file Name");
		}
		//close the reader
		try {
			decodeReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//traverse down the tree while decoding
	public Node decodeTraverse(Node root,char c) {
		if(root==null)
			return null;
		if(c=='0') {
			return root.left;
		}
		return root.right;
	}
	
	//Method to return the codeBook which contains encoded value of all the charcters in the file
	@Override
	public Map<Character, String> codebook() {
			return codeBook;
	}
	
}
