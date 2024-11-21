package com.skilldistillery.filmquery.database;

import java.sql.SQLException;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public interface DatabaseAccessor {
	
  public Film findFilmById(int filmId) throws SQLException;
  public Actor findActorById(int actorId) throws SQLException; 
  public List<Actor> findActorsByFilmId(int filmId) throws SQLException;
  public List<Film> findFilmsByKeyword(String keyword) throws SQLException;
  
  /**
   * Takes a newly created Film object and inserts it into the database
   * @param aFilm
   * @return the persisted Film object on success, or null if the insert fails
   */
  public Film createFilm(Film aFilm) throws SQLException;
  
  /**
   * 
   * @param aFilm
   * @return true upon successful deletion or false if the delete fails
   */
 public boolean deleteFilm(Film aFilm) throws SQLException;
  
  /**
   * 
   * @param aFilm
   * @return true upon successful update or false if the update fails.
   */
  public boolean updateFilm(Film aFilm) throws SQLException;

}
