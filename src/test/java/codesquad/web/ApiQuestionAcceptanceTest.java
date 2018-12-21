package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.newUser;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문내용_작성_로그인O() throws Exception {
        Question question = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 질문내용_작성_로그인X() throws Exception {
        Question question = new Question("title", "contents");
        ResponseEntity<Void> response = template().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문수정_로그인O() throws Exception {   //로그인 된 경우, 수정버튼 클릭
        Question originalQuestion = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", originalQuestion, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Question updatedQuestion = new Question("updatedTitle", "updatedContents");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
//    public void 질문수정_로그인O() throws Exception {   //로그인 된 경우, 수정버튼 클릭
//        Question originalQuestion = new Question("title", "contents");
//        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
//                .addParameter("title", "질문있어요")
//                .addParameter("contents", "질문이 뭔지 까먹었어요")
//                .build();
//        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", request, Void.class);
//        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    public void 질문수정_로그인X() throws Exception {    //로그인 안된 경우, 수정버튼 클릭
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", new Question("title", "contents"), Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Question updateQuestion = new Question("updatedTitle", "updatedContents");

        log.debug("location------------------------ : {} ", location);

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문수정_다른유저() throws Exception {   //다른 로그인 유저일 시, 수정버튼 클릭
        User other = newUser("testUser");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", new Question("title", "contents"), Void.class);
        String location = response.getHeaders().getLocation().getPath();
        log.debug("location : {}", location);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question updateQuestion = new Question("updatedTitle", "updatedContents");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(other).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        log.debug("responseEntity : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문삭제_로그인O() {
        Question originalQuestion = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", originalQuestion, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        log.debug("location : {}", location);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(originalQuestion), Question.class);
        log.debug("responseEntity_body : {} ", responseEntity.getBody());
        log.debug("responseEntity_headers : {} ", responseEntity.getHeaders());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void 질문삭제_로그인X() {
        Question originalQuestion = new Question("title","contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", originalQuestion, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.DELETE, createHttpEntity(originalQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(responseEntity.getBody().isDeleted()).isFalse();
    }

    @Test
    public void 질문삭제_다른유저() {
        User other = newUser("testUser");
        Question originalQuestion = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/",originalQuestion,Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(other).exchange(location,HttpMethod.DELETE,createHttpEntity(originalQuestion),Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(responseEntity.getBody().isDeleted()).isFalse();
    }

    //얘 역할이???
    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
