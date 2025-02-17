package com.example.demo.controller;


import com.example.demo.exception.NoSuchBookException;
import com.example.demo.model.Book;
import com.example.demo.model.BookModel;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookServiceImpl;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;


@AutoConfigureEmbeddedDatabase(
        provider = ZONKY,
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookModel bookModel;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Title");
        book.setDescription("Test Description");


        bookModel = BookModel.from(book);
    }

    @Test
    @DisplayName("Create Book")
    void createBook_ShouldReturnSavedBook() {
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        BookModel result = bookService.createBook(book);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookModel.getTitle(), result.getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    @DisplayName("Get Book")
    void getBook_ShouldReturnBook_WhenExists() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookModel result = bookService.getBook(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookModel.getTitle(), result.getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Book not found")
    void getBook_ShouldThrowException_WhenNotFound() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchBookException.class, () -> bookService.getBook(1L));

        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Update Book")
    void updateBook_ShouldUpdateAndReturnBook() {
        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Title");
        updatedBook.setDescription("Updated Description");


        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(updatedBook);

        BookModel result = bookService.updateBook(1L, updatedBook);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Title", result.getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    @DisplayName("Not found updatable book")
    void updateBook_ShouldThrowException_WhenNotFound() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchBookException.class, () -> bookService.updateBook(1L, book));

        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Delete Book")
    void deleteBook_ShouldDelete_WhenExists() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Not found deletable book")
    void deleteBook_ShouldThrowException_WhenNotFound() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchBookException.class, () -> bookService.deleteBook(1L));

        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }


}
