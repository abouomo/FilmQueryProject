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
				menu = false;
				System.out.println("Goodbye!");
				break;
			default:
				System.out.println("Invalid choice. Please select 1, 2, or 3.");
			}
		}

	}

	private void lookUpFilmById(Scanner input) {
		System.out.print("Enter the film ID: ");
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
			e.printStackTrace();
		}

	}

	private void lookUpFilmByKeyword(Scanner input) {
		System.out.print("Enter a keyword to search for films: ");
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
			e.printStackTrace();
		}

	}

	private void menuOption() {
		System.out.println("\nPlease choose an option:");
		System.out.println("1. Look up a film by its ID");
		System.out.println("2. Look up a film by a search keyword");
		System.out.println("3. Exit the application");
		System.out.print("Your choice: ");
	}

}
