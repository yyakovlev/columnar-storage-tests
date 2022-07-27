package com.example.carbondatapoc.controller;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.carbondatapoc.model.MultiTypePojo;

@RestController
public class TestController {
    @GetMapping("/test")
    List<MultiTypePojo> test() {
      return Arrays.asList(MultiTypePojo.builder().stringField("test-value").build());
    }    
}
