package codesquad.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);
    //    Iterable<Question> findByWriter(User loginUser);
    Optional<Question> findByWriter(User loginUser);
    Optional<Question> findByTitle(String title);
}
