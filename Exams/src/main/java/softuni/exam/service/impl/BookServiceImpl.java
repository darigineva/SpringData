package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.BookSeedDto;
import softuni.exam.models.entity.Book;
import softuni.exam.repository.BookRepository;
import softuni.exam.service.BookService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service

public class BookServiceImpl implements BookService {
    private static final String BOOK_PATH_FILE = "src/main/resources/files/json/books.json";

    private BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper, ValidationUtil validationUtil,
                           Gson gson) {
        this.bookRepository = bookRepository;

        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return bookRepository.count() > 0;
    }

    @Override
    public String readBooksFromFile() throws IOException {
        return Files.readString(Path.of(BOOK_PATH_FILE));

    }

    @Override
    public String importBooks() throws IOException {

        StringBuilder sb = new StringBuilder();

        BookSeedDto[] bookSeedDtos = gson.fromJson
                (readBooksFromFile(), BookSeedDto[].class);
        Arrays.stream(bookSeedDtos).filter(bookSeedDto -> {
                    boolean isValid = validationUtil.isValid(bookSeedDto) &&
                            isUniqueTitle(bookSeedDto.getTitle());
                    sb.append(
                            isValid ? String.format
                                    ("Successfully imported book %s - %s"
                                            , bookSeedDto.getAuthor(),
                                            bookSeedDto.getTitle())
                                    : "Invalid book"
                    );
                    sb.append(System.lineSeparator());
                    return isValid;

                }).map(bookSeedDto -> {
                    return modelMapper.map(bookSeedDto, Book.class);


                })
                .forEach(bookRepository::save);
        return sb.toString().trim();

    }

    private boolean isUniqueTitle(String title) {
        return bookRepository.findByTitle(title).isEmpty();

    }

}

