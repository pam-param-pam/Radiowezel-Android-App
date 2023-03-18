package dev.pamparampam.myapplication.radiowezel.network;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

interface IEventListener {
    void receive(String message) throws JsonProcessingException, JSONException;
}
