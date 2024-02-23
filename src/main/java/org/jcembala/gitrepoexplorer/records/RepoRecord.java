package org.jcembala.gitrepoexplorer.records;

import org.jcembala.gitrepoexplorer.serializers.Branch;
import org.jcembala.gitrepoexplorer.serializers.Owner;

import java.util.List;

public record RepoRecord(String name, Owner owner, List<Branch> branches) {}