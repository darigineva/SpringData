package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.BorrowingRecordRootSeedDto;
import softuni.exam.models.entity.Book;
import softuni.exam.models.entity.BorrowingRecord;
import softuni.exam.models.entity.Genre;
import softuni.exam.repository.BookRepository;
import softuni.exam.repository.BorrowingRecordRepository;
import softuni.exam.repository.LibraryMemberRepository;
import softuni.exam.service.BorrowingRecordsService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class BorrowingRecordsServiceImpl implements BorrowingRecordsService {
    private static final String BORROWING_RECORDS_PATH_FILE = "src/main/resources/files/xml/borrowing-records.xml";

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final LibraryMemberRepository libraryMemberRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final XmlParser xmlParser;

    public BorrowingRecordsServiceImpl(BorrowingRecordRepository borrowingRecordRepository, LibraryMemberRepository libraryMemberRepository, BookRepository bookRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, XmlParser xmlParser) {
        this.borrowingRecordRepository = borrowingRecordRepository;
        this.libraryMemberRepository = libraryMemberRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return borrowingRecordRepository.count() > 0;
    }

    @Override
    public String readBorrowingRecordsFromFile() throws IOException {
        return Files.readString(Path.of(BORROWING_RECORDS_PATH_FILE));

    }

    @Override
    public String importBorrowingRecords() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        xmlParser
                .fromFile(BORROWING_RECORDS_PATH_FILE, BorrowingRecordRootSeedDto.class)
                .getBorrowingRecordSeedDtoList().stream().filter(borrowingRecordSeedDto -> {
                    boolean isValid = validationUtil.isValid(borrowingRecordSeedDto)
                            && doesBookExist(borrowingRecordSeedDto.getBook().getTitle())
                            && doesMemberExist(borrowingRecordSeedDto.getMember().getId());
                    sb.append(
                            isValid ? String.format(
                                    "Successfully imported borrowing record %s - %s",
                                    borrowingRecordSeedDto.getBook().getTitle(), borrowingRecordSeedDto.getBorrowDate()
                            )
                                    : "Invalid borrowing record"
                    );
                    sb.append(System.lineSeparator());
                    return isValid;
                }).map(borrowingRecordSeedDto ->
                {
                    BorrowingRecord borrowingRecord = modelMapper.map(borrowingRecordSeedDto, BorrowingRecord.class);
                    borrowingRecord.setBook(bookRepository.findByTitle(borrowingRecordSeedDto.getBook().getTitle()).orElse(null));
                    borrowingRecord.setMember(libraryMemberRepository.findById(borrowingRecordSeedDto.getMember().getId()).orElse(null));
                    return borrowingRecord;
                }).forEach(borrowingRecordRepository::save);

        return sb.toString().trim();
    }

    private boolean doesMemberExist(Long id) {
        return libraryMemberRepository.findById(id).isPresent();
    }

    private boolean doesBookExist(String title) {
        return bookRepository.findByTitle(title).isPresent();
    }

    @Override
    public String exportBorrowingRecords() {

        StringBuilder sb = new StringBuilder();
        Genre genre = Genre.SCIENCE_FICTION;
        LocalDate localDate = LocalDate.parse("2021-09-10", DateTimeFormatter
                .ofPattern("yyyy-MM-dd"));
//
        Set<BorrowingRecord> books = borrowingRecordRepository.findByGenreOrderedByBorrowDateDesc(localDate, genre);
        books.forEach(b ->
        {
//            "Book title: {bookTitle}
//            "*Book author: {bookAuthor}
//            "**Date borrowed: {dateBorrowed}
//            "***Borrowed by: {firstName} {lastName}
//
            sb.append(String.format("Book title: %s",
                    b.getBook().getTitle()));
            sb.append(System.lineSeparator());
            sb.append(String.format("*Book author: %s",
                    b.getBook().getAuthor()));
            sb.append(System.lineSeparator());
            sb.append(String.format("**Date borrowed: %s", b.getBorrowDate()));
            sb.append(System.lineSeparator());
            sb.append(String.format("***Borrowed by: %s %s",
                    b.getMember().getFirstName(), b.getMember().getLastName()));
            sb.append(System.lineSeparator());
        });
        return sb.toString().trim();
    }
}
