package com.example.projectList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;

    @Autowired
    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getRepositoryNames(String owner) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);

        URI uri = UriComponentsBuilder.fromUriString(githubApiUrl)
                .path("/users/"+ owner + "/repos")
                .buildAndExpand(owner)
                .toUri();

        RequestCallback requestCallback = request -> request.getHeaders().addAll(headers);

        ResponseExtractor<ResponseEntity<List>> responseExtractor = restTemplate.responseEntityExtractor(List.class);

        ResponseEntity<List> responseEntity = restTemplate.execute(uri, HttpMethod.GET, requestCallback, responseExtractor);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody().toString();
            List<String> repositoryNames = extractRepositoryNamesFromJson(responseBody);
            return repositoryNames;
        } else {
            // Handle error response
            return Collections.emptyList();
        }
    }

    private List<String> extractRepositoryNamesFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<GithubRepository> repositories = objectMapper.readValue(json, new TypeReference<List<GithubRepository>>() {});
            return repositories.stream()
                    .map(GithubRepository::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Handle JSON parsing exception
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}