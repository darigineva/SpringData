package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.LibraryMemberSeedDto;
import softuni.exam.models.entity.LibraryMember;
import softuni.exam.repository.LibraryMemberRepository;
import softuni.exam.service.LibraryMemberService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class LibraryMemberServiceImpl implements LibraryMemberService {
    private static final String LIBRARY_MEMBER_PATH_FILE = "src/main/resources/files/json/library-members.json";
    private LibraryMemberRepository libraryMemberRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final XmlParser xmlParser;


    public LibraryMemberServiceImpl(LibraryMemberRepository libraryMemberRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, XmlParser xmlParser) {
        this.libraryMemberRepository = libraryMemberRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return libraryMemberRepository.count() > 0;
    }

    @Override
    public String readLibraryMembersFileContent() throws IOException {
        return Files.readString(Path.of(LIBRARY_MEMBER_PATH_FILE));
    }

    @Override
    public String importLibraryMembers() throws IOException {
        StringBuilder sb = new StringBuilder();

        LibraryMemberSeedDto[] libraryMemberSeedDtos = gson.fromJson
                (readLibraryMembersFileContent(), LibraryMemberSeedDto[].class);
        Arrays.stream(libraryMemberSeedDtos).filter(libraryMemberSeedDto -> {
                    boolean isValid = validationUtil.isValid(libraryMemberSeedDto) &&
                            isUniquePhone(libraryMemberSeedDto.getPhoneNumber());
                    sb.append(
                            isValid ? String.format
                                    ("Successfully imported library member %s - %s"
                                            , libraryMemberSeedDto.getFirstName(),
                                            libraryMemberSeedDto.getLastName())
                                    : "Invalid library member"
                    );
                    sb.append(System.lineSeparator());
                    return isValid;

                }).map(libraryMemberSeedDto -> {
                    return modelMapper.map(libraryMemberSeedDto, LibraryMember.class);


                })
                .forEach(libraryMemberRepository::save);
        return sb.toString().trim();
    }

    private boolean isUniquePhone(String phoneNumber) {
        return libraryMemberRepository.findByPhone(phoneNumber).isEmpty();
    }
}
