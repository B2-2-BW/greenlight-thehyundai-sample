package com.winten.greenlight.thehyundaisample.greenlight;

import com.winten.greenlight.thehyundaisample.greenlight.dto.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GreenlightContext {
    private volatile static Map<String, Action> actionMap = new ConcurrentHashMap<>();

    private static Boolean enabled = false;

    public static void updateActions(List<Action> actions) {
        Map<String, Action> newActionMap = new ConcurrentHashMap<>();
        for (Action action: actions) {
            if (ActionType.LANDING == action.getActionType()) {
                continue;
            }
            newActionMap.put(action.getActionUrl(), action);
        }
        actionMap = newActionMap;
    }

    public static Action getActionFromUrl(String url) {
        return actionMap.get(url);
    }

    public static void clearActions() {
        actionMap.clear();
    }

    public static void setEnabled(Boolean enabled) {
        GreenlightContext.enabled = enabled;
    }

    public static boolean isEnabled() {
        return GreenlightContext.enabled;
    }
}