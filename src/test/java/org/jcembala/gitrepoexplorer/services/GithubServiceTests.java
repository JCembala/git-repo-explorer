package org.jcembala.gitrepoexplorer.services;

import org.jcembala.gitrepoexplorer.serializers.Branch;
import org.jcembala.gitrepoexplorer.serializers.Repo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GithubServiceTests {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private GithubService githubService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        githubService = new GithubService(restTemplateBuilder);
    }

    @Test
    public void getReposReturnsListOfRepos() {
        when(restTemplateBuilder.build().exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("[{\"name\":\"repo1\",\"fork\":false},{\"name\":\"repo2\",\"fork\":true}]"));

        assertDoesNotThrow(() -> {
            List<Repo> repos = githubService.getRepos("testUser");
            assertEquals(1, repos.size());
            assertEquals("repo1", repos.getFirst().getName());
        });
    }

    @Test
    public void getReposThrowsExceptionWhenUserNotFound() {
        when(restTemplateBuilder.build().exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class, () -> githubService.getRepos("nonexistentUser"));
    }

    @Test
    public void getBranchesForRepoReturnsListOfBranches() {
        when(restTemplateBuilder.build().exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("[{\"name\":\"branch1\"},{\"name\":\"branch2\"}]"));

        when(restTemplateBuilder.build().exchange(contains("/branches/"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"commit\":{\"sha\":\"testSha\"}}"));

        assertDoesNotThrow(() -> {
            List<Branch> branches = githubService.getBranchesForRepo("testUser", "testRepo");
            assertEquals(2, branches.size());
            assertEquals("branch1", branches.get(0).getName());
            assertEquals("branch2", branches.get(1).getName());
            assertEquals("testSha", branches.get(0).getLastCommitSha());
            assertEquals("testSha", branches.get(1).getLastCommitSha());
        });
    }

    @Test
    public void getBranchesForRepoThrowsExceptionWhenRepoNotFound() {
        when(restTemplateBuilder.build().exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class, () -> githubService.getBranchesForRepo("testUser", "nonexistentRepo"));
    }
}