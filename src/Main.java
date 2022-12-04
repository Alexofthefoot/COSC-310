import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	private static String currentLanguage = "en";
	static Scanner s = new Scanner(System.in);
	private static String[] menuOptions = new String[] {"\n--------------------------------------------------\n",
			"1. Add a new item to inventory",
			"2. Increase amount of existing item",
			"3. Decrease amount of existing item",
			"4. Remove item from inventory",
			"5. Display inventory",
			"6. Generate report",
			"7. Change menu language",
			"8. Log out",
			"9. Shut down",
			"Enter 1 - 9: "
	};
	private static ArrayList<String> userActions = new ArrayList<>();


	public static void main(String[] args) throws IOException {
		
		System.out.println("Welcome");
		
		String[] list = getSQLCredential();
		String url = list[0];
		String uid = list[1];
		String pw = list[2];
	
		SQL.initialize(url, uid, pw);
		url = url + "db";
		int loggedIn = 0;
		
		User temp = SQL.selectUser(url, uid, pw, "admin");
		String userInfo = temp.getUsername();

		if(temp.getUsername() == null) {
			System.out.println("\n-----Intial setup-----\nCreating default user: admin, please refer to software documentation for password.");
			User tempUser = new User("admin", "123456", 1);
			SQL.insertUser(url, uid, pw, tempUser);
			//add login info to userActions
			userInfo = tempUser.getUsername();
		}
		//Add user's info to top of report
		userActions.add("Report of actions taken by: " + userInfo + "\n--------------------------------------------");
		
		
		
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
			else if (userInput == 7) {
				changeLanguage();
			}
			else if(userInput == 8) {
				loggedIn = 0; // Log out
				System.out.println("\nLogged out.");
				continue;
			}
			else {
				System.out.println("\nGoodbye");
				break; // Exit
			}
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
	
	public static int menu() throws IOException {
			
		while(true) {
			//Print out menuOptions
			for (int i = 0; i < menuOptions.length; i++){
				if (currentLanguage.compareTo("en") == 0) {
					if (i == menuOptions.length-1) {
						System.out.print(menuOptions[i]);
					}
					else {
						System.out.println(menuOptions[i]);
					}
				}
				else {
					Translator.translatedPrint(menuOptions[i], currentLanguage);
				}
			}

			int input = s.nextInt();
			s.nextLine();// Capture the \n from user hitting enter
			
			if(input >= 1 & input <= 9) { // May need to do more user input verification
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

		//Save user action for report functionality
		String userAction = "Added: " + amount + " " + unit + " of " + itemname + " to database";
		userActions.add(userAction);

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
		Double newAmount = amount + amountFromDB;
		SQL.updateInventory(url, uid, pw, fromDB.getItemname(), "amount", newAmount + ""); // TO DO: better way to turn double into string, or find a way to use double

		//Save user action for report functionality
		String userAction = "Increased: " + itemname + " by " + amount + ". Updated amount: " + newAmount;
		userActions.add(userAction);
	}

	public static void decrItem(String url, String uid, String pw) {
		
		System.out.println("\nEnter item info");
		System.out.print("Enter item name: "); // TO DO: check if exists, right now possible crash if item does not exist
		String itemname = s.nextLine();
		System.out.print("Enter amount to be removed: ");
		double amount = s.nextDouble();
		
		Item fromDB = SQL.selectInventory(url, uid, pw, itemname);
		double amountFromDB = fromDB.getAmount();
		if (amountFromDB < amount) {
			System.out.println("Cannot have negative inventory. Item quantity has not been updated");
		}
		else {
			Double newAmount = amountFromDB - amount;
			SQL.updateInventory(url, uid, pw, fromDB.getItemname(), "amount", newAmount + ""); // TO DO: better way to turn double into string, or find a way to use double

			//Save user action for report functionality
			String userAction = "Decreased: " + itemname + " by " + amount + ". Updated amount: " + newAmount;
			userActions.add(userAction);
		}
	}

	public static void removeItem(String url, String uid, String pw) {
		
		System.out.print("Enter item name: "); // TO DO: check if exists, right now possible crash if item does not exist
		String itemname = s.nextLine();
		SQL.deleteInventory(url,  uid, pw, itemname);

		String userAction = "Removed: " + itemname + " from database.";
	}
	
	public static void displayInventory(String url, String uid, String pw) {
		SQL.displayInventory(url, uid, pw);
		// TO DO: display empty if inventory is empty
	}
	
	public static void changeUserInfo() {
		System.out.println("To be implmented.");
	}
	
	public static void generateReport(String url, String uid, String pw) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MMM/dd");
		LocalDateTime now = LocalDateTime.now();
		String date = dateChangeFormat(dtf.format(now));//remove slashes so that date can be used in filename


		System.out.println("Implementation in progress.");
		try {
			//filename includes current date
			String filename = "Report" + date + ".pdf";
			System.out.println("creating file called: " + filename);

			Document doc = new Document();
			PdfWriter.getInstance(doc, new FileOutputStream(filename));
			doc.open();
			Paragraph p = new Paragraph("Today's date: " + date);
			doc.add(p);
			//iterate through userActions, printing each on a new line.
			for (int i = 0; i < userActions.size(); i++) {
				p = new Paragraph(userActions.get(i));
				doc.add(p);
			}

			doc.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static String dateChangeFormat(String str) {
		//remove slashes so that the filename can be saved as Report_Jan03.pdf format"
		String[] arr = str.split("/");
		arr[1] = arr[1].substring(0,3);
		return arr[1] + arr[2];
	}

	public static void changeLanguage() throws IOException {

		while (true) {
			System.out.println(menuOptions[0]);
			System.out.println("'en': English");
			System.out.print("'fr': ");
			Translator.translatedPrint("French", "fr");
			System.out.print("'it': ");
			Translator.translatedPrint("Italian", "it");
			System.out.print("'es': ");
			Translator.translatedPrint("Spanish", "es");
			System.out.print("'de': ");
			Translator.translatedPrint("German", "de");

			if (currentLanguage.compareTo("en") == 0) {
				System.out.print("\nYour choice: ");
			}
			else {
				System.out.print(Translator.translate("en", currentLanguage, "Your choice: "));
			}
			String input = s.next();
			s.nextLine();// Capture the \n from user hitting enter

			if(input.compareTo("en") == 0 || input.compareTo("it") == 0|| input.compareTo("fr") == 0 ||
					input.compareTo("de") == 0 || input.compareTo("es") == 0)
			{
				currentLanguage = input;
				return;
			}
			else {
				System.out.println("Incorrect input. Please try again.");
			}

		}
	}
	
}
