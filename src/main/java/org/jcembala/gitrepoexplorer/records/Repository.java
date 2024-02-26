package org.jcembala.gitrepoexplorer.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Repository(String name, Owner owner, List<Branch> branches, boolean fork) {}