package org.jcembala.gitrepoexplorer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcembala.gitrepoexplorer.serializers.Branch;
import org.jcembala.gitrepoexplorer.serializers.Commit;
import org.jcembala.gitrepoexplorer.serializers.Repo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final RestTemplate restTemplate;

    @Value("${GITHUB_TOKEN}")
    private String githubToken;

    public GithubService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Repo> getRepos(String username) throws JsonProcessingException, RestClientException {
        String url = "https://api.github.com/users/" + username + "/repos";
        HttpHeaders headers = createHeaders();

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        List<Repo> repos = Arrays.asList(mapper.readValue(response.getBody(), Repo[].class));
        return repos.stream()
                .filter(repo -> !repo.isFork())
                .collect(Collectors.toList());
    }

    public List<Branch> getBranchesForRepo(String username, String repoName) throws HttpClientErrorException, JsonProcessingException{
        String url = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";

        HttpHeaders headers = createHeaders();

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        List<Branch> branches = mapper.readValue(responseBody, new TypeReference<>(){});

        for (Branch branch : branches) {
            String branchUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/branches/" + branch.getName();
            ResponseEntity<String> branchResponse = restTemplate.exchange(branchUrl, HttpMethod.GET, entity, String.class);
            String branchResponseBody = branchResponse.getBody();

            Map<String, Object> branchData = mapper.readValue(branchResponseBody, new TypeReference<>(){});
            Map<String, Object> commitMap = (Map<String, Object>) branchData.get("commit");
            Commit commit = mapper.convertValue(commitMap, Commit.class);

            branch.setLastCommitSha(commit.getSha());
        }

        return branches;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.github+json")));
        headers.setBearerAuth(githubToken);
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }
}