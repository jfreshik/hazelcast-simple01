package com.example.cache.manual;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/manual")
public class CommandController {

    private final HazelcastInstance hazelcastInstance;

    public CommandController(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    private ConcurrentMap<String,String> retrieveMap() {
        return hazelcastInstance.getMap("map");
    }

    @PostMapping("/put")
    public CommandResponse put(@RequestParam(value = "key")String key, @RequestParam(value = "value") String value) {
        retrieveMap().put(key, value);
        return new CommandResponse(value);
    }

    @GetMapping("/get")
    public CommandResponse get(@RequestParam(value = "key") String key){
        final String value = retrieveMap().get(key);
        return new CommandResponse(value);
    }
}
