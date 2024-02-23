package org.jcembala.gitrepoexplorer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jcembala.gitrepoexplorer.serializers.Branch;
import org.jcembala.gitrepoexplorer.serializers.Repo;
import org.jcembala.gitrepoexplorer.records.RepoRecord;
import org.jcembala.gitrepoexplorer.services.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GithubController {

    private final GithubService githubService;

    @Autowired
    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping(value = "/explore/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getGithubRepos(@PathVariable String username) {
        try {
            List<Repo> repos = githubService.getRepos(username);
            List<RepoRecord> repoRecords = new ArrayList<>();

            for (Repo repo : repos) {
                List<Branch> branches = githubService.getBranchesForRepo(username, repo.getName());
                repo.setBranches(branches);

                RepoRecord repoRecord = new RepoRecord(repo.getName(), repo.getOwner(), repo.getBranches());


                repoRecords.add(repoRecord);
            }

            return ResponseEntity.ok(repoRecords);
        } catch (Exception exception) {
            if (exception instanceof HttpClientErrorException httpException) {
                return new ResponseEntity<>(httpException.getStatusText(), httpException.getStatusCode());
            } else if (exception instanceof JsonProcessingException) {
                return new ResponseEntity<>("Error processing JSON", HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}