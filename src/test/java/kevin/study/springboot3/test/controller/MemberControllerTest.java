package kevin.study.springboot3.test.controller;

import kevin.study.springboot3.test.entity.Member;
import kevin.study.springboot3.test.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@SpringBootApplication 이 있는 클래스 기준 빈들을 생성한다음 테스트용 애플리케이션 컨텍스트 만듬.
//spring context에 등록된 빈 들을 가져와서 테스트에서 사용하려면 이 어노테이션이 필요하다.
@AutoConfigureMockMvc
//MockMvc를 생성하고 구성해줌. MockMvc 는 컨트롤러 api를 테스트할때 사용
@Transactional
//위 어노테이션 추가 시 테스트중 repository를 통한 Db변경내역을 모두 rollback 처리한다.
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    //mockMvc를 초기화 할때 필요함.

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        //mockMvc 초기화(세팅)
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                      .build();
    }

    @AfterEach
    public void cleanUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 조회 api 검증")
    void getAllMembers() throws Exception {
        //given
        final String url = "/members";
        Member savedMember = memberRepository.save(new Member(1L, "이나경"));
        //h2 database dependencies를 추가하였기 때문에 인메모리 DB 저장 테스트 가능.

        //when
        final ResultActions resultActions =
                mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isOk())
                     .andExpect(jsonPath("$[0].id").value(savedMember.getId()))
                     .andExpect(jsonPath("$[0].name").value(savedMember.getName()));
    }
}