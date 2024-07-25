package com.tahayk3.movies.repositories;

import com.tahayk3.movies.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

/*Jpa solicita dos datos: Entidad con la que trabaja y el id,
de esta manera, se crea un controlador, en base a una interfaz y jpa
*/
public interface MovieRepository extends JpaRepository<Movie, Long>{
    //Aqui se pueden construir metodos adicionales que no proporcione jpa
}
