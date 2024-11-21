package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//		app.test();
		app.launch();
	}

	private void test() {
		Film film;
		Actor actor;
		List<Actor> actors;

		try {
			film = db.findFilmById(478);
			System.out.println(film);

			actor = db.findActorById(4);
			System.out.println(actor);

			actors = db.findActorsByFilmId(2);
			System.out.println(actors);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void launch() {
		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}

	private void startUserInterface(Scanner input) {

		boolean menu = true;

		while (menu) {
			menuOption();
			int choice = input.nextInt();
			input.nextLine();

			switch (choice) {
			case 1:
				lookUpFilmById(input);
				break;
			case 2:
				lookUpFilmByKeyword(input);
				break;
			case 3:
				addNewFilm(input);
				break;
			case 4:
				deleteFilm(input);
				break;
			case 5:
				updateFilm(input);
				break;
			case 6:
				menu = false;
				System.out.println("Goodbye!");
				break;
			default:
				System.out.println("Invalid choice. Please select 1, 2, 3, 4 or 5.");
			}
		}

	}

	private void lookUpFilmById(Scanner input) {
		System.out.print("Look film by id. Enter the film ID: ");
		int filmId = input.nextInt();
		input.nextLine();

		try {
			Film film = db.findFilmById(filmId);
			if (film != null) {
				System.out.println("Film found:");
				System.out.println("Title: " + film.getTitle());
				System.out.println("Year: " + film.getReleaseYear());
				System.out.println("Rating: " + film.getRating());
				System.out.println("Description: " + film.getDescription());
				System.out.println("Language: " + film.getLanguageName());
				List<Actor> actors = db.findActorsByFilmId(filmId);

				if (!actors.isEmpty()) {
					System.out.println("List of actor(s):");
					for (Actor actor : actors) {
						System.out.println(actor.getFirstName() + " " + actor.getLastName());
					}
				} else {
					System.out.println("No actor(s) found for this film.");
				}
			} else {
				System.out.println("No film found with ID: " + filmId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void lookUpFilmByKeyword(Scanner input) {
		System.out.print("Look film by keyword. Enter a keyword to search for films: ");
		String keyword = input.nextLine();

		try {
			List<Film> films = db.findFilmsByKeyword(keyword);
			if (films.isEmpty()) {
				System.out.println("No films found with keyword " + keyword);
			} else {
				for (Film film : films) {
					System.out.println("Film found:");
					System.out.println("Title: " + film.getTitle());
					System.out.println("Year: " + film.getReleaseYear());
					System.out.println("Rating: " + film.getRating());
					System.out.println("Description: " + film.getDescription());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addNewFilm(Scanner input) {
		System.out.println("Adding a new film. Please provide the following details:");

		System.out.print("Title: ");
		String title = input.nextLine();

		System.out.print("Description: ");
		String description = input.nextLine();

		System.out.print("Release Year (e.g., 2022): ");
		int releaseYear = input.nextInt();
		input.nextLine();

		System.out.print("Rental Duration (e.g., 7): ");
		int rentalDuration = input.nextInt();
		input.nextLine();

		System.out.print("Rental Rate (e.g., 2.99): ");
		Double rentalRate = input.nextDouble();
		input.nextLine();

		System.out.print("Length (e.g., 120): ");
		int length = input.nextInt();
		input.nextLine();

		System.out.print("Replacement Cost (e.g., 19.99): ");
		Double replacementCost = input.nextDouble();
		input.nextLine();

		String rating = getRatingFromUser(input);

		String specialFeatures = getSpecialFeaturesFromUser(input);

		// Create a new Film object with the provided details
		Film newFilm = new Film();
		newFilm.setTitle(title);
		newFilm.setDescription(description);
		newFilm.setReleaseYear(releaseYear);
		newFilm.setRentalDuration(rentalDuration);
		newFilm.setRentalRate(rentalRate);
		newFilm.setLength(length);
		newFilm.setReplacementCost(replacementCost);
		newFilm.setRating(rating);
		newFilm.setSpecialFeatures(specialFeatures);

		try {

			Film createdFilm = db.createFilm(newFilm);
			if (createdFilm != null) {
				System.out.println("Film added successfully! Generated ID: " + createdFilm.getId());
			} else {
				System.out.println("Failed to add the film.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("An error occurred while adding the film.");
			e.printStackTrace();
		}
	}

	private String getRatingFromUser(Scanner input) {
		printRatingOptions();

		System.out.print("Choose the rating by selecting the letter (a, b, c, d, e): ");
		String choice = input.nextLine().toLowerCase();

		switch (choice) {
		case "a":
			return "G";
		case "b":
			return "PG";
		case "c":
			return "PG-13";
		case "d":
			return "R";
		case "e":
			return "NC-17";
		default:
			System.out.println("Invalid choice. Please try again.");
			return getRatingFromUser(input);
		}
	}

	private String getSpecialFeaturesFromUser(Scanner input) {
		printSpecialFeaturesOptions();

		System.out.print("Choose the special feature by selecting the letter (a, b, c, d): ");
		String choice = input.nextLine().toLowerCase();

		switch (choice) {
		case "a":
			return "Trailers";
		case "b":
			return "Commentaries";
		case "c":
			return "Deleted Scenes";
		case "d":
			return "Behind the Scenes";
		default:
			System.out.println("Invalid choice. Please try again.");
			return getSpecialFeaturesFromUser(input);
		}
	}

	/**
	 * Helper method to print rating options
	 */
	private void printRatingOptions() {
		System.out.println("Available Ratings:");
		System.out.println("a. G");
		System.out.println("b. PG");
		System.out.println("c. PG-13");
		System.out.println("d. R");
		System.out.println("e. NC-17");
	}

	/**
	 * Helper method to print special features options
	 */
	private void printSpecialFeaturesOptions() {
		System.out.println("Available Special Features:");
		System.out.println("a. Trailers");
		System.out.println("b. Commentaries");
		System.out.println("c. Deleted Scenes");
		System.out.println("d. Behind the Scenes");
	}

	private void menuOption() {
		System.out.println("\nPlease choose an option:");
		System.out.println("1. Look up a film by its ID");
		System.out.println("2. Look up a film by a search keyword");
		System.out.println("3. Add a new film");
		System.out.println("4. Delete an existing film");
		System.out.println("5. Update an existing film");
		System.out.println("6. Exit the application");
		System.out.print("Your choice: ");
	}

	private void deleteFilm(Scanner input) {
		System.out.print("Enter the ID of the film you want to delete: ");
		int filmId = input.nextInt();
		input.nextLine();

		try {
			// check if the film exists
			Film filmToDelete = db.findFilmById(filmId);
			if (filmToDelete == null) {
				System.out.println("Film not found with ID: " + filmId);
				return; // Exit the method if the film doesn't exist
			}

			// If the film exists, delete it
			boolean success = db.deleteFilm(filmToDelete);

			if (success) {
				System.out.println("Film with ID " + filmId + " was successfully deleted.");
			} else {
				System.out.println("Failed to delete the film with ID: " + filmId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("An error occurred while trying to delete the film.");
			e.printStackTrace();
		}

	}

	private void updateFilm(Scanner input) {
		System.out.print("Enter the ID of the film you want to update: ");
		int filmId = input.nextInt();
		input.nextLine();

		try {

			Film filmToUpdate = db.findFilmById(filmId);
			if (filmToUpdate == null) {
				System.out.println("Film not found with ID: " + filmId);
				return;
			}

			// display current details of the film
			System.out.println("Current details of the film:");
			System.out.println("Title: " + filmToUpdate.getTitle());
			System.out.println("Description: " + filmToUpdate.getDescription());

			// Prompt the user to enter new details (title, description)
			System.out.print("Enter new title (leave blank to keep current): ");
			String newTitle = input.nextLine();
			if (!newTitle.isEmpty()) { // User provided a new title -> update title
				filmToUpdate.setTitle(newTitle);
			}

			System.out.print("Enter new description (leave blank to keep current): ");
			String newDescription = input.nextLine();
			if (!newDescription.isEmpty()) { // User provided a new desc -> update desc
				filmToUpdate.setDescription(newDescription);
			}

			boolean success = db.updateFilm(filmToUpdate);

			if (success) {
				System.out.println("Film updated successfully!");
			} else {
				System.out.println("Failed to update the film.");
			}
		} catch (SQLException e) {
			System.out.println("An error occurred while trying to update the film.");
			e.printStackTrace();
		}
	}
}
