package kevin.study.springboot3.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kevin.study.springboot3.config.jwt.JwtFactory;
import kevin.study.springboot3.user.config.jwt.JwtProperties;
import kevin.study.springboot3.user.domain.RefreshToken;
import kevin.study.springboot3.user.domain.User;
import kevin.study.springboot3.user.dto.CreateAccessTokenRequest;
import kevin.study.springboot3.user.repository.RefreshTokenRepository;
import kevin.study.springboot3.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TokenApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                      .build();
    }

    @Test
    @DisplayName("createNewAccessToken() : 새로운 액세스토큰을 발급한다.")
    void createNewAccessToken() throws Exception {
        //given
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                                                .id(1L)
                                                .email("test@naver.com")
                                                .password("test")
                                                .build());

        String refreshToken = JwtFactory.builder()
                                        .claims(Map.of("id", testUser.getId()))
                                        .build()
                                        .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest(refreshToken);


        //when
        //then
        mockMvc.perform(post(url)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }


}