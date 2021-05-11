package com.learn.urlshortening;


import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/url/shortener")
public class UrlShortenerController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("{id}")
    public String get(@PathVariable String id) {

        String url = stringRedisTemplate.opsForValue().get(id);
        if(url == null) {
            throw new RuntimeException("Invalid URL");
        }
        return url;
    }

    @PostMapping
    public String create(@RequestBody String url) {

        try {
            UrlValidator urlValidator = new UrlValidator(
                    new String[]{"http", "https"}
            );

            if (urlValidator.isValid(url)) {

                String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
                System.out.println("Generated Id for URL: " + id);
                stringRedisTemplate.opsForValue().set(id, url);
                return id;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Invalid URL");
    }
}
