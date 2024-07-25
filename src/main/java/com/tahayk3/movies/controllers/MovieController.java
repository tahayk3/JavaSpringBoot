package com.tahayk3.movies.controllers;


import com.tahayk3.movies.models.Movie;
import com.tahayk3.movies.repositories.MovieRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Esto es un controlador que trabaja con las solicitudes y respone al usuario
@RestController
//Un controlador se activa en base a una anotacion(es como la ruta en otros frameworks)
@RequestMapping("/api/v1/movies")
public class MovieController {
    //Uso del repositorio
    //Autowired envita la generacion de nuevos objetos cada vez que se utiliza el repositorio, este trabajo es delegado a Spring
    @Autowired 
    private MovieRepository movieRepository;
    
    //ACCIONES CRUD movieRepository contiene metodos que son de JPA
    //Listar peliculas, con GetMapping se indica esta llegando una peticion de tipo get a la anotacion indicada en RequestMapping
    //@CrossOrigin, permite que otras aplicaciones puedan realizar solicitudes
    @CrossOrigin
    //Para especificar mas, RequestMapping, puede ir vacio o no: RequestMapping, RequestMapping("/all")
    @GetMapping
    public List<Movie> getAllMovies (){
        return movieRepository.findAll();
    }
    
    //Listar una pelicula(detalle de pelicula)
    /*ResponseEntity entrega una respuesta que entrega un estado: elemento encontrado o no
    de ser encontrado el elemento, retorna la Movie, al usar ResponseEntity, no se generan excepciones 
    en el caso de que el elemento a buscar, no exista
    */
    //PathVariable extra datos de la url
    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getOneMovie (@PathVariable Long id){
        //Con Optional, indicamos que puede que se reciba algo o no
        Optional<Movie> movie =  movieRepository.findById(id);
        //Se devuelve una respuesta de exito o de error; por medio de una funcion lambda(arrow function)
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    //agregar pelicula, con metodo Post
    @CrossOrigin
    @PostMapping
    /*En los parametros de createMovie, se realiza una anotacion para saber que Movie viene del lado del usuario
    Al ser un post, no se extrae la informacion de paramtros de la url, esta esta encriptada en el cuerpo de la peticion
    */
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie){
        //Almacenamos en una variable el elemento creado
        Movie savedMovie = movieRepository.save(movie);
        //Retornamos un 200 ok y retornamos el elemento creado 
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }
    
    //Borrar pelicula
    @CrossOrigin 
    @DeleteMapping("/{id}")
    //ResponseEntity<Void> no devuelve nada
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id){
       //Por medio de condiciones, eliminamos el elemento si exite
       if (movieRepository.existsById(id))
       {
            movieRepository.deleteById(id);
            return ResponseEntity.noContent().build();
       }
       else
       {
           return ResponseEntity.notFound().build();
       }
    }
    
    //Actualizar pelicula
    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie updateMovie){
        if(movieRepository.existsById(id)){
            //se utiliza el setId del modelo 
            updateMovie.setId(id);
            //Guardamos cambios, save funciona de tal manera que si existe un elemento con id en la db, lo actualiza
            Movie updatedMovie = movieRepository.save(updateMovie);
            return ResponseEntity.ok(updatedMovie);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    //Votar por una pelicula
    @CrossOrigin
    @GetMapping("/vote/{id}/{rating}")
    public ResponseEntity<Movie> voteMovie(@PathVariable Long id, @PathVariable double rating){
        if(movieRepository.existsById(id)){
            //obtener pelicula
            Optional<Movie> optionMovie = movieRepository.findById(id);
            //obtener valor
            Movie movie = optionMovie.get();
            
            double newRating = ((movie.getVotes() * movie.getRating()) + rating)/(movie.getVotes()+1);
            
            movie.setVotes(movie.getVotes()+1);
            movie.setRating(newRating);
            
            Movie savedMovie = movieRepository.save(movie);
            return ResponseEntity.ok(savedMovie);
        }
        else
        {
             return ResponseEntity.notFound().build();
        }
    } 
}
