import java.util.Scanner;

public class Main {
	
	static Scanner s = new Scanner(System.in);

	public static void main(String[] args) {
		
		System.out.println("Welcome");
		
		String[] list = getSQLCredential();
		String url = list[0];
		String uid = list[1];
		String pw = list[2];
	
		SQL.initialize(url, uid, pw);
		url = url + "db";
		int loggedIn = 0;
		
		User temp = SQL.selectUser(url, uid, pw, "admin");
		if(temp.getUsername() == null) {
			System.out.println("\n-----Intial setup-----\nCreating defualt user: admin, please refer to software documentation for password.");
			User tempUser = new User("admin", "123456", 1);
			SQL.insertUser(url, uid, pw, tempUser);
		}
		
		
		
		while(true) {
			// MAIN PROGRAM LOOP
			
			if(loggedIn == 0) {
				loggedIn = logIn(url, uid, pw); // Varying user level. 0 is not logged in, 1 is admin, 2 is employee, and so on
			}
			
			// check user level here, display different menu for different level
			int userInput = menu();
			
			if(userInput == 1) {
				addItem(url, uid, pw);
			}
			else if(userInput == 2) {
				incrItem(url, uid, pw);
			}
			else if(userInput == 3){
				decrItem(url, uid, pw);
			}
			else if(userInput == 4) {
				removeItem(url, uid, pw);
			}
			//else if(userInput == 5) {
			//	changeUserInfo();
			//}
			else if(userInput == 5) {
				displayInventory(url, uid, pw);
			}
			else if(userInput == 6) {
				generateReport(url, uid, pw);
			}
			else if(userInput == 7) {
				loggedIn = 0; // Log out
				System.out.println("\nLogged out.");
				continue;
			}
			else {
				System.out.println("\nGoodbye"); 
				break; // Exit
			}
			// TO DO: expand option 5 into sub menu
			// TO DO: option to DROP table and database
		}

	}
	
	public static String[] getSQLCredential() {

		String url, uid, pw;
		
		while(true) {
			
			System.out.println("\nEnter SQL credential.");
			System.out.print("\nEnter SQL url: ");
			url = "jdbc:mysql://" + s.nextLine() + "/";
			System.out.print("Enter SQL user name: ");
			uid = s.nextLine();
			System.out.print("Enter SQL password: ");
			pw = s.nextLine();
			
			if(SQL.trySQLCredential(url, uid, pw)) {
				break;
			}
			else {
				System.out.println("Please try again.");
			}
			
		}
			
		String[] list = {url, uid, pw};
		return list;
		
	}
	
	public static int menu() {
			
		while(true) {
			
			System.out.println("\n--------------------------------------------------\n");
			System.out.println("1. Add a new item to inventory");
			System.out.println("2. Increase amount of existing item");
			System.out.println("3. Decrease amount of existing item");
			System.out.println("4. Remove item from inventory");
			System.out.println("5. Display inventory");
			//System.out.println("6. Change user information"); // To be implemented
			System.out.println("6. Generate report");
			System.out.println("7. Log out");
			System.out.println("8. Shut down");
			System.out.print("\nEnter 1 - 8: ");
			
			int input = s.nextInt();
			s.nextLine();// Capture the \n from user hitting enter
			
			if(input >= 1 & input <= 8) { // May need to do more user input verification
				return input;
			}
			else {
				System.out.println("Incorrect input. Please try again.");
			}
			
		}

	}
	
	public static int logIn(String url, String uid, String pw) {
		
		while(true) {
			
			System.out.println("\nPlease log in.");
			System.out.print("Enter user name: ");
			String username = s.nextLine();
			System.out.print("Enter password: ");
			String password = s.nextLine();
			
			User temp = SQL.selectUser(url, uid, pw, username);
			
			if(temp.getUsername() == null) {
				System.out.println(username + " does not exist. Please try again.");
				continue;
			}
			
			if(!password.equals(temp.getPassword())) {
				System.out.println("Password is incorrect. Please try again.");
				continue;
			}
			
			System.out.println("\nLogged in as " + temp.getUsername() + ".\n");
			return temp.getLevel();
			
		}
		
	}
	
	public static void addItem(String url, String uid, String pw) {
		
		System.out.println("\nEnter item info");
		System.out.print("Enter item name: "); // TO DO: check string length, check if exists
		String itemname = s.nextLine();
		System.out.print("Enter amount: ");
		double amount = s.nextDouble();
		s.nextLine();// Capture the \n from user hitting enter
		System.out.print("Enter unit: ");
		String unit = s.nextLine(); // TO DO: check string length
		
		Item item = new Item(itemname, amount, unit);
		
		SQL.insertInventory(url, uid, pw, item);
		
	}
	
	public static void incrItem(String url, String uid, String pw) {
		
		System.out.println("\nEnter item info");
		System.out.print("Enter item name: "); // TO DO: check if exists, right now possible crash if item does not exist
		String itemname = s.nextLine();
		System.out.print("Enter amount to be added: ");
		double amount = s.nextDouble();
		
		Item fromDB = SQL.selectInventory(url, uid, pw, itemname);
		double amountFromDB = fromDB.getAmount();
		amount = amount + amountFromDB;
		SQL.updateInventory(url, uid, pw, fromDB.getItemname(), "amount", amount + ""); // TO DO: better way to turn double into string, or find a way to use double
		
	}
	
	public static void decrItem(String url, String uid, String pw) {
		
		System.out.println("\nEnter item info");
		System.out.print("Enter item name: "); // TO DO: check if exists, right now possible crash if item does not exist
		String itemname = s.nextLine();
		System.out.print("Enter amount to be removed: ");
		double amount = s.nextDouble();
		
		Item fromDB = SQL.selectInventory(url, uid, pw, itemname);
		double amountFromDB = fromDB.getAmount();
		amount = amountFromDB - amount; // TO DO: check if amountFromDB is larger than amount, right now it can go negative
		SQL.updateInventory(url, uid, pw, fromDB.getItemname(), "amount", amount + ""); // TO DO: better way to turn double into string, or find a way to use double
		
	}
	
	public static void removeItem(String url, String uid, String pw) {
		
		System.out.print("Enter item name: "); // TO DO: check if exists, right now possible crash if item does not exist
		String itemname = s.nextLine();
		SQL.deleteInventory(url,  uid, pw, itemname);
		
	}
	
	public static void displayInventory(String url, String uid, String pw) {
		SQL.displayInventory(url, uid, pw);
		// TO DO: display empty if inventory is empty
	}
	
	public static void changeUserInfo() {
		System.out.println("To be implmented.");
	}
	
	public static void generateReport(String url, String uid, String pw) {
		System.out.println("Implementation in progress.");
//		Document document = new Document();
//		PdfWriter.getInstance(document, new FileOutputStream("sampleReport.pdf"));
//
//		document.open();
//		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//		Chunk chunk = new Chunk("Hello World", font);
//
//		document.add(chunk);
//		document.close();
	}
	
}
