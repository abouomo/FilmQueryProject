package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	private static String URL = "jdbc:mysql://localhost:3306/sdvid";

	public DatabaseAccessorObject() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Film findFilmById(int filmId) throws SQLException {
		Film film = null;

		String name = "student";
		String password = "student";

		Connection conn = DriverManager.getConnection(URL, name, password); // Connect to the database
		String sql = "SELECT film.id, film.title, film.description, film.release_year, film.language_id, "
				+ "film.rental_duration, film.rental_rate, film.length, film.replacement_cost, "
				+ "film.rating, film.special_features, language.name FROM film "
				+ "JOIN language ON film.language_id = language.id WHERE film.id = ?"; // Formulate the SQL statement

		PreparedStatement ps = conn.prepareStatement(sql); // Compile and optimize

		ps.setInt(1, filmId); // Bind any bind variable we may have
		ResultSet rs = ps.executeQuery(); // Execute the query to retrieve the result set of rows

		if (rs.next()) { // Retrieve result set of rows
			int id = rs.getInt("id");
			String title = rs.getString("title");
			String description = rs.getString("description");
			Integer releaseYear = rs.getInt("release_year");
			int languageId = rs.getInt("language_id");
			int rentalDuration = rs.getInt("rental_duration");
			double rentalRate = rs.getDouble("rental_rate");
			int length = rs.getInt("length");
			double replacementCost = rs.getDouble("replacement_cost");
			String rating = rs.getString("rating");
			String specialFeatures = rs.getString("special_features");
			String languageName = rs.getString("language.name");

			film = new Film(id, title, description, releaseYear, languageId, rentalDuration, rentalRate, length,
					replacementCost, rating, specialFeatures, languageName);

		}

		ps.close();
		conn.close();
		return film;
	}

	@Override
	public Actor findActorById(int actorId) throws SQLException {

		Actor actor = null;

		String name = "student";
		String password = "student";

		Connection conn = DriverManager.getConnection(URL, name, password); // Connect to the database
		String sql = "SELECT * FROM actor WHERE id = ?"; // Formulate the SQL statement

		PreparedStatement ps = conn.prepareStatement(sql); // Compile and optimize

		ps.setInt(1, actorId); // Bind any bind variable we may have
		ResultSet rs = ps.executeQuery(); // Execute the query to retrieve the result set of rows

		if (rs.next()) { // Retrieve result set of rows
			int id = rs.getInt("id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");

			actor = new Actor(id, firstName, lastName);

		}

		ps.close();
		conn.close();

		return actor;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) throws SQLException {

		Actor actor = null;
		List<Actor> actors = new ArrayList<>();

		String name = "student";
		String password = "student";

		Connection conn = DriverManager.getConnection(URL, name, password); // Connect to the database
		String sql = "SELECT actor.id, actor.first_name, actor.last_name from actor "
				+ "JOIN film_actor ON film_actor.actor_id = actor.id "
				+ "JOIN film ON film.id = film_actor.film_id WHERE film.id = ?"; // Formulate the SQL statement

		PreparedStatement ps = conn.prepareStatement(sql); // Compile and optimize

		ps.setInt(1, filmId); // Bind any bind variable we may have

		ResultSet rs = ps.executeQuery(); // Execute the query to retrieve the result set of rows

		while (rs.next()) { // Retrieve result set of rows
			int id = rs.getInt("id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");

			actor = new Actor(id, firstName, lastName);
			actors.add(actor);

		}

		ps.close();
		conn.close();

		return actors;

	}

	@Override
	public List<Film> findFilmsByKeyword(String keyword) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Film createFilm(Film aFilm) throws SQLException {
		// TODO Auto-generated method stub
		String name = "student";
		String password = "student";

		Connection conn = DriverManager.getConnection(URL, name, password);
		String sql = "INSERT INTO film (title, description, release_year, language_id, "
				+ "rental_duration, rental_rate, length, replacement_cost, rating, special_features) "
				+ "VALUES (?, ?, ?, 1, ?, ?, ?, ?, ?, ?)";

		boolean committed = false; // To track if transaction is successful
		try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			conn.setAutoCommit(false); // Begin transaction

			// Set values for the prepared statement
			ps.setString(1, aFilm.getTitle());
			ps.setString(2, aFilm.getDescription());
			ps.setInt(3, aFilm.getReleaseYear());
			ps.setInt(4, aFilm.getRentalDuration());
			ps.setDouble(5, aFilm.getRentalRate());
			ps.setInt(6, aFilm.getLength());
			ps.setDouble(7, aFilm.getReplacementCost());
			ps.setString(8, aFilm.getRating());
			ps.setString(9, aFilm.getSpecialFeatures());

			// Execute the insert statement
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				conn.rollback(); // Rollback transaction on failure
				return null; // Insert failed
			}

			// Retrieve the generated key (new ID)
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					// Set the generated ID to the Film object
					aFilm.setId(generatedKeys.getInt(1));
				} else {
					conn.rollback(); // Rollback if no key was generated
					return null; // Failed to retrieve ID
				}
			}

			conn.commit(); // Commit transaction on success
			committed = true;
		} catch (SQLException e) {
			conn.rollback(); // Rollback transaction in case of an error
			throw e;
		} finally {
			if (!committed) {
				conn.setAutoCommit(true); // Restore default auto-commit behavior
			}
		}

		return aFilm;
	}

	@Override
	public boolean deleteFilm(Film aFilm) throws SQLException {
		// TODO Auto-generated method stub
		boolean deleted;
		boolean committed = false;
		String name = "student";
		String password = "student";

		Connection conn = DriverManager.getConnection(URL, name, password);
		String deleteFilmActorSql = "DELETE FROM film_actor WHERE film_id = ?";
		String deleteFilmSql = "DELETE FROM film WHERE id = ?";

		try (PreparedStatement ps1 = conn.prepareStatement(deleteFilmActorSql);
				PreparedStatement ps2 = conn.prepareStatement(deleteFilmSql)) {
			conn.setAutoCommit(false); 

			ps1.setInt(1, aFilm.getId());
			ps1.executeUpdate();

			ps2.setInt(1, aFilm.getId());
			int rowsAffected = ps2.executeUpdate();
			deleted = rowsAffected > 0;

			conn.commit(); 
			committed = true;
		} catch (SQLException e) {
			conn.rollback(); 
			throw e;
		} finally {
			if (!committed) {
				conn.setAutoCommit(true);
			}
		}

		return deleted;
	}

	@Override
	public boolean updateFilm(Film aFilm) throws SQLException {
		// TODO Auto-generated method stub
		boolean committed = false;
		boolean updated;
		String name = "student";
		String password = "student";

		Connection conn = DriverManager.getConnection(URL, name, password);
		String sql = "UPDATE film SET title = ?, description = ?, release_year = ?, "
				+ "language_id = ?, rental_duration = ?, rental_rate = ?, length = ?, "
				+ "replacement_cost = ?, rating = ?, special_features = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			conn.setAutoCommit(false);
			ps.setString(1, aFilm.getTitle());
			ps.setString(2, aFilm.getDescription());
			ps.setInt(3, aFilm.getReleaseYear());
			ps.setInt(4, aFilm.getLanguageId());
			ps.setInt(5, aFilm.getRentalDuration());
			ps.setDouble(6, aFilm.getRentalRate());
			ps.setInt(7, aFilm.getLength());
			ps.setDouble(8, aFilm.getReplacementCost());
			ps.setString(9, aFilm.getRating());
			ps.setString(10, aFilm.getSpecialFeatures());
			ps.setInt(11, aFilm.getId());

			int rowsAffected = ps.executeUpdate();
			updated = rowsAffected > 0;

			conn.commit();
			committed = true;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			if (!committed) {
				conn.setAutoCommit(true);
			}
		}

		return updated;

	}
}
