package org.jcembala.gitrepoexplorer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcembala.gitrepoexplorer.records.Repository;
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

    public List<Repository> repositories(String username) throws JsonProcessingException, RestClientException {
        String url = "https://api.github.com/users/" + username + "/repos";
        HttpHeaders headers = createHeaders();

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        List<Repository> repos = Arrays.asList(mapper.readValue(response.getBody(), Repository[].class));
        return repos.stream()
                .filter(repo -> !repo.fork())
                .collect(Collectors.toList());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.github+json")));
        headers.setBearerAuth(githubToken);
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }
}