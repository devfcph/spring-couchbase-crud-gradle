package com.streaming.couchbase.samplecrud.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor(force = true)
public class GameDto {
    public String id;
    public int experience;
    public int hitpoints;
    public String jsonType;
    public int level;
    public boolean loggedIn;

    public String name;
    public String uuid;
}
