# Git Repository Explorer

This is a Spring Boot application that uses the GitHub API to explore repositories of a given user. The endpoint returns a list of non-forked
repositories of the user along with their branches and last commit SHA.

## Technologies Used

- Java
- Spring Boot
- Gradle

## Setup and Installation

1. Clone the repository.
2. Navigate to the project directory.
3. Update the `application.properties` file with your GitHub token.
```properties
GITHUB_TOKEN=<your_token>
```
4. Build the project using the following command:
```shell
./gradlew build
```
5. Run the project using the following command:
```shell
./gradlew bootRun
```

## Usage

### Endpoint
Use any rest client to make a GET request to the following endpoint:
```
http://localhost:8080/api/explore/{username}
```
Replace `{username}` with the GitHub username you want to explore.

### Headers

Set the `Accept` header to `application/json` to receive the response, otherwise you will receive a `415 Unsupported Media Type` error.
```
Accept: application/json
```

## Testing
To run the tests, use the following command:
```shell
./gradlew test
```

## Example
### Request
```
GET /api/explore/jcembala
Accept: application/json
```

### Response
```json
[
	{
		"name": "git-repo-explorer",
		"owner": {
			"login": "JCembala"
		},
		"branches": [
			{
				"name": "master",
				"lastCommitSha": "2d0342e39c997dd38f5fe7195d17961d8fb2fbe8"
			}
		]
	}
]
```

## License
[Apache License 2.0](LICENSE)
