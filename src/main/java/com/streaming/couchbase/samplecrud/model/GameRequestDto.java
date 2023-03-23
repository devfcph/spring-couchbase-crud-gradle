package com.streaming.couchbase.samplecrud.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor(force = true)
public class GameRequestDto {
    public int experience;
    public int hitpoints;
    public String jsonType;
    public int level;
    public boolean loggedIn;
    public String name;
    public String uuid;

    public GameDto getGame() {
        return GameDto.builder()
                .id(UUID.randomUUID().toString())
                .experience(experience)
                .hitpoints(hitpoints)
                .jsonType(jsonType)
                .level(level)
                .loggedIn(loggedIn)
                .name(name)
                .uuid(uuid)
                .build();
    }


}
