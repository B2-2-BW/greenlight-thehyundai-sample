//package com.winten.greenlight.thehyundaisample.service;
//
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//@Service
//public class CachedActionService {
//    private final ConcurrentMap<String, Integer> urlToActionIdMap = new ConcurrentHashMap<>();
//
//    public void initialize() {
//        urlToActionIdMap.clear();
//
//        // ActionType이 Landing이 아닌 DIRECT인 경우만 캐싱
//        Map<String, Object> actions = new HashMap<>(); //redisService.getAllActions();
//
//        for (var action : actions.values()) {
//            action = (Map) action;
//            urlToActionIdMap.put((String) action.get("actionUrl"), (Integer) action.get("id"));
//        }
//    }
//
//    public void update() {
//        initialize();
//    }
//
//    public Integer getActionByUrl(String url) {
//        return urlToActionIdMap.get(url);
//    }
//}