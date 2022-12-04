# Cafe Keeper

CafeKeeper is an inventory management system for a cafe. The system includes a database created using mysql and a graphical user interface created using Java Swing. Users are able to update and keep track of their stock of coffee, baked goods, and other items. The system will generate reports and prompt users to restock items as they are needed.

As of the current submission, the project includes methods to add items, increment/ decrement the quantities of items currently in the database, remove items from database, and display the current inventory items in the database. We have begun work on report generation, and user creation functionalities. 

Item class:

The Item class is meant to represent each individual inventory item in the database. It has attributes for the item name, its current quantity, and which unit it is measured in (eg. bags of coffee, bottles of syrup, kgs of sugar). This addition improves our ability to handle the items we retrieve from and add to the database.

User class:

The User class represents the possible user types in the system. It includes attributes for a username, password, and security level (e.g. administrator, employee).

Add item function:

The add item function allows us to add new items to the database. It uses user input to assign the name, initial quantity, and measurement unit.

Increment/Decrement item functions:

These functions allow us to change the quantity listed in the database, according to the user’s input. This is an essential functionality for our project, as the inventory management system is useless if quantities cannot be updated.

Remove item function:

This function removes items from the database. At this time it does not first check if the item is currently stored in the database, so there is a potential crash there.

Report Generation function:

CafeKeeper now tracks the user's actions while using it. This function creates a PDF report listing the date and  those user actions. This feature is useful to the project because the ability to keep track of any changes made would be useful in our cafe scenario for recordkeeping reasons. This feature uses the iTextPDF library.

Translate function:

The user is now able to view the menu in four languages, which are English, French, German, and Italian. This feature uses the Google Translate API.

GUI:

The GUI created in Java Swing, creates a new window with some interaction capability, allowing the user to do actions without having to directly use SQL code.
