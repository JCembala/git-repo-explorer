package org.jcembala.gitrepoexplorer.controllers;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jcembala.gitrepoexplorer.config.TestConfig;
import org.jcembala.gitrepoexplorer.services.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class GithubControllerTests {

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Mock
    private GithubService githubService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockMvc = MockMvcBuilders.standaloneSetup(new GithubController(githubService)).build();
    }

    @Test
    void whenUsernameIsProvided_shouldReturnListOfRepos() throws Exception {
        String githubApiResponse = "[]";

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/user/repos"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Authorization", "token mock_token"))
                .andRespond(MockRestResponseCreators.withSuccess(githubApiResponse, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/explore/user")
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserHasNoRepos_shouldReturnEmptyList() throws Exception {
        String githubApiResponse = "[]";

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/user/repos"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Authorization", "token mock_token"))
                .andRespond(MockRestResponseCreators.withSuccess(githubApiResponse, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/explore/user")
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAcceptHeaderIsNotCorrect_shouldReturnStatusNotAcceptable() throws Exception {
        mockMvc.perform(get("/api/explore/user")
                .header("Accept", "application/xml"))
                .andExpect(status().isNotAcceptable());
    }
}