package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;
    public static Question originalQuestion = new Question("title", "contents");
    public static Question updatedQuestion = new Question("updatedTitle", "updatedContents");

    public static User owner = new User(1, "javajigi", "password", "name", "javajigi@slipp.net");
    public static User other = new User(2, "sanjigi", "password", "name", "sanjigi@slipp.net");

    @Before
    public void setUp() throws Exception {
        originalQuestion.setId(1);
        originalQuestion.writeBy(owner);
    }

    @Test
    public void 질문수정_로그인X() throws Exception {
//        Question question = new Question("title", "contents");
//        when(questionRepository.findByWriter(question.getWriter())).thenReturn(Optional.of(question));
//        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
//        User loginUser = userService.login(user.getUserId(), user.getPassword());
//        softly.assertThat(loginUser).isEqualTo(user);
    }

    @Test
    public void 질문수정_로그인O() throws Exception {

    }

    @Test
    public void 질문수정_다른유저() throws Exception {

    }

    @Test
    public void 질문삭제_로그인X() throws Exception {

    }

    @Test
    public void 질문삭제_로그인O() throws Exception {

    }

    @Test
    public void 질문삭제_다른유저() throws Exception {
    }
}
