package com.streaming.couchbase.samplecrud.controller;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import com.streaming.couchbase.samplecrud.config.DBProperties;
import com.streaming.couchbase.samplecrud.model.GameDto;
import com.streaming.couchbase.samplecrud.model.GameRequestDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.streaming.couchbase.samplecrud.config.CollectionNames.PROFILE;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    private Cluster cluster;
    private Collection profileCol;
    private DBProperties dbProperties;
    private Bucket bucket;

    public GameController(Cluster cluster, Bucket bucket, DBProperties dbProperties) {
      System.out.println("Initializing profile controller, cluster: " + cluster + "; bucket: " + bucket);
        this.cluster = cluster;
        this.bucket = bucket;
        this.profileCol = bucket.defaultCollection();
        this.dbProperties = dbProperties;
    }


    @CrossOrigin(value="*")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea un item de juegos")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = GameDto.class),
            @ApiResponse(code = 400, message = "Bad request", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public ResponseEntity<GameDto> save(@RequestBody final GameRequestDto gameRequestDto) {
        //generates an id and save the user
        GameDto gameDto = gameRequestDto.getGame();

        try {
            profileCol.insert(gameDto.id, gameDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(gameDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CrossOrigin(value="*")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtiene un juego mediante el ID", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 500, message = "Error occurred in getting user profiles", response = Error.class)
            })
    public ResponseEntity<GameDto> getGame(@PathVariable("id") UUID id) {
        GameDto gameDto = profileCol.get(id.toString()).contentAs(GameDto.class);
        return ResponseEntity.status(HttpStatus.OK).body(gameDto);
    }

    @CrossOrigin(value="*")
    @PutMapping(path = "/{id}")
    @ApiOperation(value = "Actualiza la información del juego", response = GameDto.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "¡Actualización correcta!", response = GameDto.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public ResponseEntity<GameDto> update(@PathVariable("id") UUID id, @RequestBody GameDto gameDto) {
        try {
            profileCol.upsert(id.toString(), gameDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(gameDto);
        } catch (DocumentNotFoundException dnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CrossOrigin(value="*")
    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Elimina un juego")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public ResponseEntity delete(@PathVariable UUID id){

        try {
            profileCol.remove(id.toString());
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (DocumentNotFoundException dnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CrossOrigin(value="*")
    @GetMapping(path = "/games/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Lista todos los juegos", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the list of user profiles"),
                    @ApiResponse(code = 500, message = "Error occurred in getting user profiles", response = Error.class)
            })
    public ResponseEntity<List<GameDto>> getGames(
            @RequestParam(required=false, defaultValue = "10") int limit,
            @RequestParam(required=false, defaultValue = "0") int skip,
            @RequestParam String search) {

        String qryString = "SELECT p.* FROM `"+dbProperties.getBucketName()+"`.`_default`.`_default` p "+
                            "WHERE lower(p.name) LIKE '%"+search.toLowerCase()
                            +"%' OR lower(p.name) LIKE '%"+search.toLowerCase()+"%'  LIMIT "+limit+" OFFSET "+skip;
        System.out.println("Query="+qryString);
        //TBD with params: final List<Profile> profiles = cluster.query("SELECT p.* FROM `$bucketName`.`_default`.`$collectionName` p WHERE lower(p.firstName) LIKE '$search' OR lower(p.lastName) LIKE '$search' LIMIT $limit OFFSET $skip",
        final List<GameDto> gameDtos =
                cluster.query(qryString,
                    QueryOptions.queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS))
                .rowsAs(GameDto.class);
        return ResponseEntity.status(HttpStatus.OK).body(gameDtos);
    }

}
