package com.example.demo.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/graphql")
public class GraphController {

    @Autowired
    RestTemplate restTemplate;


    String URL = "https://api.github.com/graphql";
    List<String> result = new ArrayList<>();


    @GetMapping("/api")
    public void apiCall(){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","bearer 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");


        int count = 0;
        String checkQuery = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repositories{ totalCount } } }\"}";
        JSONObject body = getBody(checkQuery,httpHeaders);
        count = body.getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");;

        System.out.println("Total count of repositories : "+count);
        System.out.println("\n\n");




        if(count>0) {
            String query = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repositories(first: "+count+") { edges { repository:node { name } } } } }\"}";

            JSONObject body2 = getBody(query,httpHeaders);
            JSONArray repo = body2.getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getJSONArray("edges");



            for (int i = 0; i < repo.length(); i++) {
                JSONObject repoObj = repo.getJSONObject(i);
                result.add(repoObj.getJSONObject("repository").getString("name"));
//            JSONObject finalResult = repoObj.getJSONObject("repository");
//            System.out.println(finalResult.getString("name"));

            }

            System.out.println("Total of Repo names saved : "+result.size());
            System.out.println("Repo names : "+result);

        }
    }

    @GetMapping("/api/prs")
    public void getPRs(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","bearer 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");

//        String e = "Signal-Android";
//        int count = 0;
//        String checkQuery = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repository(name : \\\""+e+"\\\"){ pullRequests{ totalCount } } } }\"}";
//        JSONObject body = getBody(checkQuery,httpHeaders);
//        count = body.getJSONObject("data").getJSONObject("organization").getJSONObject("repository").getJSONObject("pullRequests").getInt("totalCount");;
//
//        System.out.println("PRS in "+e+" repository : "+count);
//
        AtomicInteger totalCount = new AtomicInteger();


//            String checkQuery = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repository(name : \\\""+e+"\\\"){ pullRequests{ totalCount } } } }\"}";
        String checkQuery = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repositories(first : 95){ nodes{ pullRequests{ totalCount } } } } }\"}";

        JSONObject body = getBody(checkQuery,httpHeaders);
        JSONArray repo = body.getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getJSONArray("nodes");

        for (int i = 0; i < repo.length(); i++) {
            int count = 0;
            JSONObject repoObj = repo.getJSONObject(i);
            count = repoObj.getJSONObject("pullRequests").getInt("totalCount");
            System.out.println("PRS in  repository "+i+" : "+count);
            totalCount.addAndGet(count);

        }

        System.out.println("Total Count Of Pull Requests Across All Repositories : "+totalCount);
    }


    JSONObject getBody(String query, HttpHeaders httpHeaders){
        ResponseEntity<String> response = restTemplate.postForEntity(URL, new HttpEntity<>(query, httpHeaders), String.class);
        JSONObject obj = new JSONObject(response);
        JSONObject body = new JSONObject(obj.getString("body"));
        return body;
    }
}
