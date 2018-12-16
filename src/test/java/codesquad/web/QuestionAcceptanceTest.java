package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createQuestionForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)  //로그인 체크
                .getForEntity(String.format("/questions/form"), String.class);    //getForEntity get 매핑, PostForEntity post 매핑
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createQuestionForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/form"), //template 뭐임??
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "질문있어요")
                .addParameter("contents", "질문이 뭔지 까먹었어요")
                .build();
//        ResponseEntity<String> response = template().postForEntity("/questions/%d", request,  String.class);    //질문하기 하면 내 질문화면 보여주기 어떻게 id값을 가져와야 하는 가?
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);     //request에 데이터를 담으면 postForEntity로 얘가 판단해주네

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
//        softly.assertThat(questionRepository.findByWriter(loginUser).isPresent()).isTrue(); //이 경우, IncorrectResultSizeDataAccessException 에러 뜸 optional은 유니크(1개) 결과값만 값을 받는다?
        softly.assertThat(questionRepository.findByWriter(loginUser)).isNotEmpty();
    }

    @Test
    public void updateQuestionForm_login() throws Exception {   //로그인 된 경우, 수정버튼 클릭
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);   //정상처리 : 200, redirect는 status code == 300대
//        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/%d/form",1));  //얘 왜 NullPointerException 뜨냐 대체 아 빡쳐
    }

    @Test
    public void updateQuestionForm_no_login() throws Exception {    //로그인 안된 경우, 수정버튼 클릭
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); //httpStatus는 어떻게 설정함?? 컨트롤러에서 예외 발생시킬때 말임
    }

    @Test
    public void updateQuestionForm_otherUser() throws Exception {   //다른 로그인 유저일 시, 수정버튼 클릭
//        User loginUser = defaultUser();
//        ResponseEntity<String> response = basicAuthTemplate(loginUser)    //순수 로그인 했는지만 체크하는거임?
//                .getForEntity(String.format("/questions/%d/form", 2), String.class);  //이 방식으로는 안됨?
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);   //redirect는 status code == 300대
        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/%d", 1));    //결과 url
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "질문 수정입니다.")
                .addParameter("contents", "질문 또 까먹었습니다.")
                .build();
        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }
}