package com.example.projectList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubRepository {
    @JsonProperty("name")
    private String name;

    // 생성자, getter, setter 등 필요한 메서드를 추가할 수 있음

    public String getName() {
        return name;
    }
}
