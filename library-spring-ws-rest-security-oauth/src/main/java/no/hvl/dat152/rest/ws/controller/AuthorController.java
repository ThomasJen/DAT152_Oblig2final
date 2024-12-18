/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

	// TODO authority annotation
	@Autowired
	private AuthorService authorservice;
	
	// TODO - getAllAuthor (@Mappings, URI, and method)
	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/authors")
	public ResponseEntity<Object> getAllAuthors() {

		List<Author> authors = authorservice.findAll();
		if (authors != null) {

			return new ResponseEntity<>(authors, HttpStatus.OK);
		} else
			//Dette er en stor test
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	// TODO - getAuthor (@Mappings, URI, and method)
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/authors/{id}")
	public ResponseEntity<Object> getAuthor(@PathVariable("id") long id) throws AuthorNotFoundException {

		try {
			Author author = authorservice.findById((int) id);
			
			return new ResponseEntity<>(author, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	// TODO - getBooksByAuthorId (@Mappings, URI, and method)
	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/authors/{id}/books")
	public ResponseEntity<Set<Book>> findBooksByAuthorId(@PathVariable("id") int id) {
        try {
            Set<Book> books = authorservice.findBooksByAuthorId(id);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	// TODO - createAuthor (@Mappings, URI, and method)
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/authors")
	public ResponseEntity<Object> createAuthor(@RequestBody Author author) {

		Author a1 = authorservice.saveAuthor(author);

		return new ResponseEntity<>(a1, HttpStatus.CREATED);
	}
	
	// TODO - updateAuthor (@Mappings, URI, and method)
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/authors/{id}")
	public ResponseEntity<Author> updateAuthor(@PathVariable("id") long id, @RequestBody Author author)
			throws AuthorNotFoundException {

		Author authorToUpdate = authorservice.findById((int) id);
		if (author.getFirstname() != null || author.getFirstname() != "") {
			authorToUpdate.setFirstname(author.getFirstname());
		}

		if (author.getLastname() != null || author.getLastname() != "") {
			authorToUpdate.setLastname(author.getLastname());
		}

		if (author.getBooks() != null) {
			authorToUpdate.setBooks(author.getBooks());
		}

		authorservice.saveAuthor(authorToUpdate);

		return new ResponseEntity<>(authorToUpdate, HttpStatus.OK);
	}

}
