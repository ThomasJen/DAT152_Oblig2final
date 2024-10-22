/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateBookFailedException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	
	public Book saveBook(Book book) {
		
		return bookRepository.save(book);
		
	}
	
	public List<Book> findAll(){
		
		return (List<Book>) bookRepository.findAll();
		
	}
	
	
	public Book findByISBN(String isbn) throws BookNotFoundException {
		
		Book book = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
		
		return book;
	}
	
	public Book updateBook(Book book, String isbn) throws BookNotFoundException {
		
		Book bookToupdate = findByISBN(isbn);

		bookToupdate.setTitle(book.getTitle());
		bookToupdate.setIsbn(book.getIsbn());
		bookToupdate.setAuthors(book.getAuthors());

		return bookRepository.save(bookToupdate);
		
	}
	
	public List<Book> findAllPaginate(Pageable page) {
		
		Page<Book> books = bookRepository.findAll(page);

		return books.getContent();
		
	}
	
	public Set<Author> findAuthorsOfBookByISBN(String isbn) {
		
		Book book = bookRepository.findBookByISBN(isbn);

		return book.getAuthors();
		
	}
	
	public void deleteById(long id) throws BookNotFoundException {
		
		Book deleteBook = findById(id);
		bookRepository.delete(deleteBook);
		
	}
	
	public Book findById(long id) throws BookNotFoundException {
	
		Book book = findBookById(id);
		
		return book;
	}

	private Book findBookById(long id) throws BookNotFoundException {
		
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("Book with id = " + id + " not found!"));

		return book;
	}
		

	public void deleteByISBN(String isbn) throws BookNotFoundException {
		
		Book bookToDelete = findByISBN(isbn);
		bookRepository.delete(bookToDelete);
		
	}
	
}
