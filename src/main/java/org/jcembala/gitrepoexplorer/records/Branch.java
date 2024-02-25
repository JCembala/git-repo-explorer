package org.jcembala.gitrepoexplorer.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Branch(String name, String lastCommitSha) {}