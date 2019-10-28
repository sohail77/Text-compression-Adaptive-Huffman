import java.util.Scanner;

public class TextCompress {
	public static void main(String []args) {
		TextCompressImplementation implementation=new TextCompressImplementation();
		//Scanner Takes filename as input from the user  
				System.out.println("filename?");
				Scanner sc=new Scanner(System.in);
				String filename=sc.next();
				
				System.out.println("Enter number");
				Scanner sc1=new Scanner(System.in);
				int num=sc.nextInt();
				if(num==1) {
		implementation.encode(filename,8,false,"hello.txt");
				}else if(num==2) {
					implementation.decode(filename, "hello.txt");
				}
	}
}
