/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2018 Alvaro Monge <alvaro.monge@csulb.edu>
 *
 */

package csulb.cecs323.app;

// Import all of the entity classes that we have written for this application.

import csulb.cecs323.model.*;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JPA Project implementing entity relationships with books and authors.
 */
public class JPABooksProject {
	/**
	 * You will likely need the entityManager in a great many functions throughout your application.
	 * Rather than make this a global variable, we will make it an instance variable within the CustomerOrders
	 * class, and create an instance of CustomerOrders in the main.
	 */
	private final EntityManager entityManager;

	/**
	 * The Logger can easily be configured to log to a file, rather than, or in addition to, the console.
	 * We use it because it is easy to control how much or how little logging gets done without having to
	 * go through the application and comment out/uncomment code and run the risk of introducing a bug.
	 * Here also, we want to make sure that the one Logger instance is readily available throughout the
	 * application, without resorting to creating a global variable.
	 */
	private static final Logger LOGGER = Logger.getLogger(JPABooksProject.class.getName());


	// A static reference to the JPA project instance so that entities can be persisted from static methods
	private static JPABooksProject jpa;

	/**
	 * The constructor for the CustomerOrders class.  All that it does is stash the provided EntityManager
	 * for use later in the application.
	 * @param manager    The EntityManager that we will use.
	 */
	public JPABooksProject(EntityManager manager) {
		this.entityManager = manager;
	}

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("JPABooksProject");
		EntityManager manager = factory.createEntityManager();
		// Create an instance of CustomerOrders and store our new EntityManager as an instance variable.
		jpa = new JPABooksProject(manager);

		// Any changes to the database need to be done within a transaction.
		// See: https://en.wikibooks.org/wiki/Java_Persistence/Transactions
		EntityTransaction tx = manager.getTransaction();

		Scanner scanner = new Scanner(System.in);

		boolean quit = false;

		while (!quit) {

			try {

				// begin a new transaction
				tx.begin();

				// prompt for choice
				int choice = promptForMainMenuChoice(scanner);

				// condition for whether any changes made should be committed
				boolean validTransaction;

				// perform an operation based on the choice
				switch (choice) {
					case -1 -> {
						quit = true;
						validTransaction = false;
					}
					case 1 -> validTransaction = performAddOperation(scanner);
					case 2 -> validTransaction = performInfoOperation(scanner);
					case 3 -> validTransaction = performDeleteOperation(scanner);
					case 4 -> validTransaction = performUpdateOperation(scanner);
					case 5 -> validTransaction = performPrimaryKeyOperation(scanner);
					default -> {
						System.out.println("\nPlease select a valid option.\n");
						validTransaction = false;
					}
				}

				// If the user chose to quit, do that.
				if (quit) {
					System.out.println("\nExiting application.\n");
					tx.rollback();
				}
				// If the transaction is valid, commit it; else rollback
				else if (validTransaction) {
					tx.commit();
					System.out.println("\nSuccessful transaction, committing to database.\n");
					// ^ print this AFTER calling commit so that if an error occurs during the commit it does not print
				}
				else {
					System.out.println("\nTransaction failed (or cancelled). Rolling back changes.\n");
					tx.rollback();
				}

			} catch (Exception e) {
				String message = e.getMessage();
				if (message.contains("DerbySQLIntegrityConstraintViolationException")) {
					if (message.contains("INSERT INTO PUBLISHERS")) {
						System.out.println("\nError: a publisher already exists with the given information.");
					} else if (message.contains("INSERT INTO AUTHORING_ENTITIES")) {
						System.out.println("\nError: an authoring entity already exists with the given information.");
					} else if (message.contains("INSERT INTO BOOKS")) {
						System.out.println("\nError: a book already exists with the given information.");
					}
				}
			}
		}

		scanner.close();
	} // End of the main method

	/**
	 * Display the main menu and prompt the user to make a choice.
	 * @param scanner the scanner to use for getting input
	 * @return an {@code int} representing the user's choice, with -1 representing
	 * 			the choice to quit the application
	 */
	private static int promptForMainMenuChoice(Scanner scanner) {
		boolean success = false;
		int result = 0;

		while (!success) {
			try {
				displayMainMenu();
				String response = promptForString(scanner, "Choose an option (#): ");
				if (response.equalsIgnoreCase("q")) result = -1;
				else result = Integer.parseInt(response);

				success = true;
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}

		return result;
	}

	/**
	 * Display the main menu.
	 */
	private static void displayMainMenu() {
		System.out.println("\n******** MAIN MENU ********");
		System.out.println("1. Add a new object");
		System.out.println("2. List object information");
		System.out.println("3. Delete a book");
		System.out.println("4. Update a book");
		System.out.println("5. List primary keys");
		System.out.println("\nOr enter Q to quit.\n");
	}

	/**
	 * Guide thr user through the process of adding an entity
	 * to the database. This method will display a menu and prompt
	 * the user for a choice of what to add, then delegate the
	 * rest of the process to the appropriate method for that object type.
	 * @param scanner the scanner to use for input
	 * @return {@code true} if the user successfully completes the add operation,
	 * 			or {@code false} if the operation fails or the user chooses to cancel
	 */
	private static boolean performAddOperation(Scanner scanner) {
		boolean success = false;

		while (!success) {
			try {

				displayAddMenu();

				String response = promptForString(scanner, "\nChoose an option (#), or Q to cancel: ");

				// if the user chooses to cancel, do so immediately
				if (response.equalsIgnoreCase("q")) return false;

				int choice = Integer.parseInt(response);

				switch (choice) {
					case 1 -> success = addAuthoringEntity(scanner);
					case 2 -> success = addPublisher(scanner);
					case 3 -> success = addBook(scanner);
					default -> throw new IllegalArgumentException("Please select a valid option (1-3).");
				}

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}

		return true;
	}

	/**
	 * Display the menu of object types to add.
	 */
	private static void displayAddMenu() {
		System.out.println("\n******** ADD MENU ********");
		System.out.println("1. Add new Authoring Entity");
		System.out.println("2. Add new Publisher");
		System.out.println("3. Add new Book");
	}

	/**
	 * Guide the user into the process of adding an authoring entity.
	 * Displays a menu for different authoring entity types and
	 * prompts the user for a choice, then delegates the rest of the
	 * process to the appropriate method.
	 * @param scanner the scanner to use for getting input
	 * @return {@code true} if the user completes the process successfully,
	 * 			or {@code false} if the process fails or the user chooses to cancel
	 */
	private static boolean addAuthoringEntity(Scanner scanner) {
		while (true) {
			try {

				displayAuthorTypesMenu();

				String response = promptForString(scanner, "Choose an author type (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return false;

				int choice = Integer.parseInt(response);
				if (choice <= 0 || choice > 3) throw new IllegalArgumentException("Please select a number 1-3.");

				switch (choice) {
					case 1 -> { return addWritingGroup(scanner); }
					case 2 -> { return addIndividualAuthor(scanner); }
					case 3 -> { return addAdHocTeam(scanner); }
				}

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	/**
	 * Display the menu of authoring entity types.
	 */
	private static void displayAuthorTypesMenu() {
		System.out.println("\n******** AUTHORING ENTITY TYPES ********");
		System.out.println("1. Writing Group");
		System.out.println("2. Individual Author");
		System.out.println("3. Ad Hoc Team");
	}


	/**
	 * Guide the user through the process of adding a writing group.
	 * Prompts the user for the group's name, email, head writer, and
	 * year formed. If the user enters valid values for all of these,
	 * the writing group is created and persisted in the database. If
	 * any invalid values are entered, the user will be prompted to
	 * try again; the user can also choose to cancel the operation.
	 * @param scanner the scanner to use for getting input
	 * @return {@code true} if the user successfully completes the operation,
	 * 			or {@code false} if the user chooses to cancel.
	 */
	private static boolean addWritingGroup(Scanner scanner) {
		while (true) {
			try {
				String name = promptForString(scanner, "Enter the Writing Group name, or Q to cancel: ");
				if (name.trim().equalsIgnoreCase("q")) return false;
				if (name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
				else if (name.length() > 80) throw new IllegalArgumentException("Name cannot exceed 80 characters long.");

				String email = promptForString(scanner, "Enter the Writing Group email, or Q to cancel: ");
				if (email.trim().equalsIgnoreCase("q")) return false;
				if (email.trim().isEmpty()) throw new IllegalArgumentException("Email cannot be empty.");
				else if (email.length() > 30) throw new IllegalArgumentException("Email cannot exceed 30 characters long.");

				String headWriter = promptForString(scanner, "Enter the Head Writer name, or Q to cancel: ");
				if (headWriter.trim().equalsIgnoreCase("q")) return false;
				if (headWriter.trim().isEmpty()) throw new IllegalArgumentException("Head Writer name cannot be empty.");
				else if (headWriter.length() > 80) throw new IllegalArgumentException("Head Writer name cannot exceed 80 characters long.");

				String yearFormedStr = promptForString(scanner, "Enter the year formed, or Q to cancel: ");
				if (yearFormedStr.trim().equalsIgnoreCase("q")) return false;
				if (yearFormedStr.trim().isEmpty()) throw new IllegalArgumentException("Year formed cannot be empty.");
				int yearFormed = Integer.parseInt(yearFormedStr);

				Writing_Groups writingGroup = new Writing_Groups();
				writingGroup.setName(name);
				writingGroup.setEmail(email);
				writingGroup.setHeadWriter(headWriter);
				writingGroup.setYearFormed(yearFormed);

				jpa.entityManager.persist(writingGroup);

				return true;

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	private static boolean addIndividualAuthor(Scanner scanner) {
		// TODO
		return false;
	}

	private static boolean addAdHocTeam(Scanner scanner) {
		// TODO
		return false;
	}

	/**
	 * Guide the user through the process of adding a publisher. Prompts
	 * the user for the publisher's name, email, and phone. If the user enters
	 * valid values for all of these, the publisher is created and persisted
	 * in the database. If any invalid values are entered, the user will be
	 * prompted to try again; the user can also choose to cancel the operation.
	 * @param scanner the scanner to use for getting input
	 * @return {@code true} if the user successfully completes the operation,
	 * 			or {@code false} if the user chooses to cancel.
	 */
	private static boolean addPublisher(Scanner scanner) {
		while (true) {
			try {

				System.out.println("\n******** ADDING PUBLISHER ********");

				// Prompt for publisher name
				String name = promptForString(scanner, "Enter the Publisher name, or Q to cancel: ");
				if (name.trim().equalsIgnoreCase("q")) return false;
				if (name.trim().isEmpty()) throw new IllegalArgumentException("Publisher name cannot be empty.");

				String email = promptForString(scanner, "Enter the Publisher email, or Q to cancel: ");
				if (email.trim().equalsIgnoreCase("q")) return false;
				if (email.trim().isEmpty()) throw new IllegalArgumentException("Publisher email cannot be empty.");

				String phone = promptForString(scanner, "Enter the Publisher phone, or Q to cancel: ");
				if (phone.trim().equalsIgnoreCase("q")) return false;
				if (phone.trim().isEmpty()) throw new IllegalArgumentException("Publisher phone cannot be empty.");

				Publishers publisher = new Publishers();
				publisher.setName(name);
				publisher.setEmail(email);
				publisher.setPhone(phone);

				jpa.entityManager.persist(publisher);

				return true;

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	/**
	 * Guide the user through the process of adding a book. Prompts
	 * the user for the book's publisher, author, ISBN, publication year,
	 * and title. If the user enters valid values for all of these, the
	 * book is created and persisted in the database. If any invalid
	 * values are entered, the user will be prompted to try again;
	 * the user can also choose to cancel the operation.
	 * @param scanner the scanner to use for getting input
	 * @return {@code true} if the user successfully completes the operation,
	 *  		or {@code false} if the user chooses to cancel.
	 */
	private static boolean addBook(Scanner scanner) {
		// Cannot add book if there are no publishers or authors
		if (getAuthors().isEmpty() || getPublishers().isEmpty()) {
			System.out.println("Error: missing required database information to add a book.");
			System.out.println("Please ensure at least one publisher and one author exist before attempting to add a book.\n");
			return false;
		}

		while (true) {
			try {

				System.out.println("\n******** ADDING BOOK ********");

				// Prompt for a publisher choice
				Publishers publisher = promptForPublisherChoice(scanner);
				if (publisher == null) return false;

				// Prompt for an author choice
				Authoring_Entities author = promptForAuthorChoice(scanner);
				if (author == null) return false;

				// Prompt for an ISBN
				String isbn = promptForString(scanner, "Enter the book's ISBN, or Q to cancel: ");
				if (isbn.trim().equalsIgnoreCase("q")) return false;
				else if (isbn.trim().isEmpty()) throw new IllegalArgumentException("ISBN cannot be empty.");
				else if (isbn.length() > 17) throw new IllegalArgumentException("ISBN cannot exceed 17 characters long.");

				// Prompt for a publication year
				String yearStr = promptForString(scanner, "Enter the book's publication year, or Q to cancel: ");
				if (yearStr.trim().equalsIgnoreCase("q")) return false;
				int year = Integer.parseInt(yearStr);

				// Prompt for a title
				String title = promptForString(scanner, "Enter the book's title, or Q to cancel: ");
				if (title.trim().equalsIgnoreCase("q")) return false;
				else if (title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty.");
				else if (title.length() > 80) throw new IllegalArgumentException("Title cannot exceed 80 characters long.");

				Books book = new Books();
				book.setAuthor(author);
				book.setPublisher(publisher);
				book.setISBN(isbn);
				book.setYearPublished(year);
				book.setTitle(title);

				jpa.entityManager.persist(book);

				return true;

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	/**
	 * Guide the user through the process of retrieving information
	 * about an entity from the database. Displays a menu of available
	 * object types to retrieve information about, and prompts the
	 * user for a choice. The rest of the process will be delegated to
	 * the appropriate method(s).
	 * @param scanner the scanner to use for getting input
	 * @return {@code true} if the user successfully completes the operation,
	 *   		or {@code false} if the user chooses to cancel.
	 */
	private static boolean performInfoOperation(Scanner scanner) {
		while (true) {
			try {

				displayInfoMenu();

				String response = promptForString(scanner, "Choose an option (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return false;

				int choice = Integer.parseInt(response);
				if (choice <= 0 || choice > 3) throw new IllegalArgumentException("Please enter a number 1-3.");

				switch (choice) {
					case 1 -> { // publisher info
						Publishers publisher = promptForPublisherChoice(scanner);
						if (publisher == null) return false;
						displayPublisherInfo(publisher);
						return true;
					}
					case 2 -> { // book info
						Books book = promptForBookChoice(scanner);
						if (book == null) return false;
						displayBookInfo(book);
						return true;
					}
					case 3 -> { // writing group info
						Writing_Groups writingGroup = promptForWritingGroupChoice(scanner);
						if (writingGroup == null) return false;
						displayWritingGroupInfo(writingGroup);
						return true;
					}
				}

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	/**
	 * Display the menu of entity types to retrieve information for.
	 */
	private static void displayInfoMenu() {
		System.out.println("\n******** INFO MENU ********");
		System.out.println("1. Get Publisher Info");
		System.out.println("2. Get Book Info");
		System.out.println("3. Get Writing Group Info");
	}

	/**
	 * Display the name, email, and phone of a publisher.
	 * @param publisher the publisher retrieved from the database
	 */
	private static void displayPublisherInfo(Publishers publisher) {
		System.out.println("\n******** PUBLISHER INFO ********");
		System.out.println("Publisher Name : " + publisher.getName());
		System.out.println("Publisher Email: " + publisher.getEmail());
		System.out.println("Publisher Phone: " + publisher.getPhone());
		System.out.println();
	}

	/**
	 * Display the title, author, year published, publisher, and ISBN of a book.
	 * @param book the book retrieved from the database
	 */
	private static void displayBookInfo(Books book) {
		System.out.println("\n******** BOOK INFO ********");
		System.out.println("Book Title:     " + book.getTitle());
		System.out.println("Book Author:    " + book.getAuthor().getName());
		System.out.println("Book Year:      " + book.getYearPublished());
		System.out.println("Book Publisher: " + book.getPublisher().getName());
		System.out.println("Book ISBN:      " + book.getISBN());
		System.out.println();
	}

	/**
	 * Display the name, email, head writer, and year formed of a writing group.
	 * @param writingGroup the writing group retrieved from the database
	 */
	private static void displayWritingGroupInfo(Writing_Groups writingGroup) {
		System.out.println("\n******** WRITING GROUP INFO ********");
		System.out.println("Writing Group Name:        " + writingGroup.getName());
		System.out.println("Writing Group Email:       " + writingGroup.getEmail());
		System.out.println("Writing Group Head Writer: " + writingGroup.getHeadWriter());
		System.out.println("Writing Group Year Formed: " + writingGroup.getYearFormed());
		System.out.println();
	}

	/**
	 * Display a list of books retrieved from the database, and
	 * prompt the user for a choice.
	 * @param scanner the scanner to use for getting input
	 * @return the {@code Books} object corresponding to the user's choice,
	 * 			or {@code null} if the user chooses to cancel or there are
	 * 			no existing books in the database
	 */
	private static Books promptForBookChoice(Scanner scanner) {
		List<Books> books = getBooks();
		if (books.isEmpty()) {
			System.out.println("\nError: missing required database information.");
			System.out.println("Please ensure at least one book entry exists before requesting book info.");
			return null;
		}

		while (true) {
			try {

				displayAvailableBooks(books);

				String response = promptForString(scanner, "Choose a book (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return null;

				int choice = Integer.parseInt(response);
				if (choice > books.size() || choice <= 0) throw new IllegalArgumentException("Invalid selection. Please enter a number 1-" + books.size());

				// if the choice is valid, return that publisher
				return books.get(choice - 1);

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	/**
	 * Display a list of books as a numbered list.
	 * @param books the list of books to display
	 */
	private static void displayAvailableBooks(List<Books> books) {
		System.out.println("\n******** AVAILABLE BOOKS ********");
		// print all options
		for (int i = 0; i < books.size(); i++) {
			Books book = books.get(i);

			String stringBuilder = (i + 1) + ". " +
					"ISBN: " + book.getISBN();

			System.out.println(stringBuilder);
		}
	}

	/**
	 * Display a list of writing groups retrieved from the database,
	 * and prompt the user for a choice.
	 * @param scanner the scanner to use for getting input
	 * @return the {@code Writing_Groups} object corresponding to the user's
	 * 			choice, or {@code null} if the user chooses to cancel or there
	 * 			are no existing writing groups in the database
	 */
	private static Writing_Groups promptForWritingGroupChoice(Scanner scanner) {
		List<Writing_Groups> writingGroups = getWritingGroups();
		if (writingGroups.isEmpty()) {
			System.out.println("\nError: missing required database information.");
			System.out.println("Please ensure at least one writing group entry exists before requesting writing group info.");
			return null;
		}

		while (true) {
			try {

				displayAvailableWritingGroups(writingGroups);

				String response = promptForString(scanner, "Choose a writing group (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return null;

				int choice = Integer.parseInt(response);
				if (choice > writingGroups.size() || choice <= 0) throw new IllegalArgumentException("Invalid selection. Please enter a number 1-" + writingGroups.size());

				// if the choice is valid, return that publisher
				return writingGroups.get(choice - 1);

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}

	}

	/**
	 * Display a list of writing groups as a numbered list.
	 * @param writingGroups the list of writing groups to display
	 */
	private static void displayAvailableWritingGroups(List<Writing_Groups> writingGroups) {
		System.out.println("\n******** AVAILABLE WRITING GROUPS ********");
		// print all options
		for (int i = 0; i < writingGroups.size(); i++) {
			Writing_Groups writingGroup = writingGroups.get(i);

			String stringBuilder = (i + 1) + ". " +
					"Name: " + writingGroup.getName();

			System.out.println(stringBuilder);
		}
	}

	private static boolean performUpdateOperation(Scanner scanner) {
		// TODO
		return false;
	}

	private static boolean editPublisher(Scanner scanner, Publishers publisher) {
		// TODO
		return false;
	}

	private static boolean editBook(Scanner scanner, Books books) {
		// TODO
		return false;
	}

	private static boolean editWritingGroup(Scanner scanner, Writing_Groups group) {
		// TODO
		return false;
	}

	private static boolean performDeleteOperation(Scanner scanner) {
		// TODO
		return false;
	}

	private static boolean performPrimaryKeyOperation(Scanner scanner) {

		while (true) {
			try {

				displayPrimaryKeyMenu();

				String response = promptForString(scanner, "Choose an option (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return false;

				int choice = Integer.parseInt(response);
				if (choice <= 0 || choice > 3) throw new IllegalArgumentException("Please enter a number 1-3.");

				switch (choice) {
					case 1 -> listPublisherPrimaryKeys();
					case 2 -> listBookPrimaryKeys();
					case 3 -> listAuthoringEntityPrimaryKeys();
				}

				return true;

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}

	}

	private static void displayPrimaryKeyMenu() {
		System.out.println("******** ENTITY TYPES ********");
		System.out.println("1. Publishers");
		System.out.println("2. Books");
		System.out.println("3. Authoring Entities");
		System.out.println();
	}

	private static void listPublisherPrimaryKeys() {
		displayAvailablePublishers(getPublishers());
	}

	private static void listBookPrimaryKeys() {
		displayAvailableBooks(getBooks());
	}

	private static void listAuthoringEntityPrimaryKeys() {
		displayAvailableAuthors(getAuthors());
	}

	/**
	 * Display a list of publishers retrieved from the database,
	 * and prompt the user for a choice.
	 * @param scanner the scanner to use for getting input
	 * @return the {@code Publishers} object corresponding to the user's
	 * 			choice, or {@code null} if the user chooses to cancel or
	 * 			there are no existing publishers in the database
	 */
	private static Publishers promptForPublisherChoice(Scanner scanner) {
		List<Publishers> publishers = getPublishers();
		if (publishers.isEmpty()) {
			System.out.println("\nError: missing required database information.");
			System.out.println("Please ensure at least one publisher entry exists before requesting publisher info.");
			return null;
		}

		while (true) { // loop until a return statement occurs
			try {
				displayAvailablePublishers(publishers);

				String response = promptForString(scanner, "Choose a publisher (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return null;

				int choice = Integer.parseInt(response);
				if (choice > publishers.size() || choice <= 0) throw new IllegalArgumentException("Invalid selection. Please enter a number 1-" + publishers.size());

				// if the choice is valid, return that publisher
				return publishers.get(choice - 1);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}
	}

	/**
	 * Display a list of publishers as a numbered list.
	 * @param publishers the list of publishers to display
	 */
	private static void displayAvailablePublishers(List<Publishers> publishers) {
		System.out.println("\n******** AVAILABLE PUBLISHERS ********");
		// print all options
		for (int i = 0; i < publishers.size(); i++) {
			Publishers publisher = publishers.get(i);

			String stringBuilder = (i + 1) + ". " +
					"Name: " + publisher.getName();

			System.out.println(stringBuilder);
		}
	}

	/**
	 * Display a list of publishers retrieved from the database,
	 * and prompt the user for a choice.
	 * @param scanner the scanner to use for getting input
	 * @return the {@code Authoring_Entities} object corresponding to the
	 * 			user's choice, or {@code null} if the user chooses to
	 * 			cancel or there are no existing authors in the database
	 */
	private static Authoring_Entities promptForAuthorChoice(Scanner scanner) {
		List<Authoring_Entities> authors = getAuthors();
		if (authors.isEmpty()) {
			System.out.println("\nError: missing required database information.");
			System.out.println("Please ensure at least one author entry exists before requesting author info.");
			return null;
		}

		while (true) {
			try {
				displayAvailableAuthors(authors);

				String response = promptForString(scanner, "Choose an author (#), or Q to cancel: ");
				if (response.trim().equalsIgnoreCase("q")) return null;

				int choice = Integer.parseInt(response);
				if (choice > authors.size() || choice <= 0) throw new IllegalArgumentException("Invalid selection. Please enter a number 1-" + authors.size());

				// if the choice is valid, return that publisher
				return authors.get(choice);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + "; Please try again.");
			}
		}

	}

	/**
	 * Display a list of authors as a numbered list.
	 * @param authors the list of authors to display
	 */
	private static void displayAvailableAuthors(List<Authoring_Entities> authors) {
		System.out.println("\n******** AVAILABLE AUTHORS ********");
		// print all options
		for (int i = 0; i < authors.size(); i++) {
			Authoring_Entities author = authors.get(i);

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(i + 1).append(". ");
			stringBuilder.append("Email: ").append(author.getEmail());
			while (stringBuilder.length() < 30) stringBuilder.append(' ');

			// If using Java 17 preview features, can do it this way:
//			switch (author) {
//				case Writing_Groups w -> stringBuilder.append(" (Writing Group)");
//				case IndividualAuthor ind -> stringBuilder.append(" (Individual Author)");
//				case AdHocTeam a -> stringBuilder.append(" (Ad Hoc Team)");
//				default -> stringBuilder.append(" (Authoring Entity)");
//			}

			// Otherwise, do it this way:
			if (author instanceof Writing_Groups) stringBuilder.append(" (Writing Group)");
			else if (author instanceof IndividualAuthor) stringBuilder.append(" (Individual Author)");
			else if (author instanceof AdHocTeam) stringBuilder.append(" (Ad Hoc Team)");
			else stringBuilder.append(" (Authoring Entity)");

			System.out.println(stringBuilder);
		}
	}

	/**
	 * Prompt the user for a line of input. Prints the given prompt
	 * String and then uses the given Scanner to get a user response.
	 * @param scanner the scanner to use for getting input
	 * @param prompt a String to prompt the user for what to enter
	 * @return the user's input as a String
	 */
	private static String promptForString(Scanner scanner, String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}

	/**
	 * Get all publishers from the database.
	 * @return a List of publishers retrieved
	 */
	public static List<Publishers> getPublishers() {
		return jpa.entityManager.createNamedQuery("ReturnAllPublishers", Publishers.class).getResultList();
	}

	/**
	 * Get all authors from the database.
	 * @return a List of all authoring entities retrieved
	 */
	public static List<Authoring_Entities> getAuthors() {
		return jpa.entityManager.createNamedQuery("ReturnAllAuthors", Authoring_Entities.class).getResultList();
	}

	/**
	 * Get all writing groups from the database.
	 * @return a List of all writing groups retrieved
	 */
	public static List<Writing_Groups> getWritingGroups() {
		return jpa.entityManager.createNamedQuery("ReturnAllWritingGroups", Writing_Groups.class).getResultList();
	}

	/**
	 * Get all individual authors from the database.
	 * @return a List of all individual authors retrieved
	 */
	public static List<IndividualAuthor> getIndividualAuthors() {
		return jpa.entityManager.createNamedQuery("ReturnAllIndividualAuthors", IndividualAuthor.class).getResultList();
	}

	/**
	 * Get all ad hoc teams from the database.
	 * @return a List of all ad hoc teams retrieved
	 */
	public static List<AdHocTeam> getAdHocTeams() {
		return jpa.entityManager.createNamedQuery("ReturnAllAdHocTeams", AdHocTeam.class).getResultList();
	}

	/**
	 * Get all books from the database.
	 * @return a List of all books retrieved
	 */
	public static List<Books> getBooks() {
		return jpa.entityManager.createNamedQuery("ReturnAllBooks", Books.class).getResultList();
	}

	/**
	 * Create and persist a list of objects to the database.
	 * @param entities   The list of entities to persist.  These can be any object that has been
	 *                   properly annotated in JPA and marked as "persistable."  I specifically
	 *                   used a Java generic so that I did not have to write this over and over.
	 */
	public <E> void createEntity(List <E> entities) {
		for (E next : entities) {
			LOGGER.info("Persisting: " + next);
			// Use the CustomerOrders entityManager instance variable to get our EntityManager.
			this.entityManager.persist(next);
		}

		// The auto generated ID (if present) is not passed in to the constructor since JPA will
		// generate a value.  So the previous for loop will not show a value for the ID.  But
		// now that the Entity has been persisted, JPA has generated the ID and filled that in.
		for (E next : entities) {
			LOGGER.info("Persisted object after flush (non-null id): " + next);
		}
	} // End of createEntity member method

	/**
	 * Think of this as a simple map from a String to an instance of Publisher that has the
	 * same name, as the string that you pass in.
	 * @param name        The name of the publisher that you are looking for.
	 * @return           The Publisher instance corresponding to that name.
	 */
	public Publishers getPublisher(String name) {
		// Run the native query that we defined in the Publisher entity to find the right style.
		List<Publishers> publishers = this.entityManager.createNamedQuery("ReturnPublisher",
				Publishers.class).setParameter(1, name).getResultList();
		if (publishers.size() == 0) {
			// Invalid style name passed in.
			return null;
		} else {
			// Return the style object that they asked for.
			return publishers.get(0);
		}
	}// End of the getStyle method
} // End of CustomerOrders class
