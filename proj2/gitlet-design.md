# Gitlet Design Document

**Name**: Xiuyuan Qiu

## Classes and Data Structures

### Main
This is the entry point to gitlet. It takes in arguments from the command line 
and calls the appropriate command from the Repository class.

### Repository
Manages the creation of objects and files in the gitlet repository. Serialized as ".gitlet/repository"
#### Fields
* public static final File CWD = new File(System.getProperty("user.dir")); - the current working directory
* public static final File GITLET_DIR = join(CWD, ".gitlet"); - the gitlet directory
* private static  final File REPO = join(GITLET_DIR, "repository"); - the file storing the repo
* private static final File STAGING_DIR = join(GITLET_DIR, "stage_add"); - the directory storing staged files for addition
* private HashMap commits; - the HashMap of commit objects with the key-value pair of (commitHash, Commit).
* private HashMap stagingArea; - The Map of all files staged for commits. Key hash, Value file
  
### Commit
This class stores the metadata and references to the files in a commit. 
The initial commit uses a constructor that takes in no parent. 
#### Fields
* Message - contains the message of the commit.
* Parent - the parent commit of the commit object.
* Timestamp - time when the commit was created. Assigned by the constructor.
* Hash - the SHA1 hash of the Commit, calculated using the private method hash, which uses Utils.sha1();

### Blob
This class stores files from the CWD. Checks for file content equality, copies fies from the
CWD into .gitlet
#### Fields

1. Field 1
2. Field 2


## Algorithms

## Persistence

