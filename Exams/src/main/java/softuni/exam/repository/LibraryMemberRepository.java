package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.LibraryMember;

import java.util.Optional;

@Repository
public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Long> {
    @Query("SELECT lm FROM LibraryMember lm WHERE lm.phoneNumber = :phone")
    Optional<LibraryMember> findByPhone(@Param("phone")String phoneNumber);


}
