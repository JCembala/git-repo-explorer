package org.jcembala.gitrepoexplorer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jcembala.gitrepoexplorer.records.Repository;
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
    public ResponseEntity<List<Repository>> getGithubRepos(@PathVariable String username) {
        try {
            List<Repository> repos = githubService.repositories(username);

            return new ResponseEntity<>(repos, HttpStatus.OK);
        } catch (HttpClientErrorException.NotFound e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}