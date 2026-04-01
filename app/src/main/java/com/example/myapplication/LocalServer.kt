package com.example.myapplication

import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject

class LocalServer(port: Int) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession?): Response? {
        return when {
            session?.uri == "/info" && session.method == Method.GET -> {
                val response = JSONObject().apply {
                    put("app", "Kotlin HTTPServer")
                    put("version", "1.0")
                    put("port", listeningPort)
                }.toString()
                newFixedLengthResponse(Response.Status.OK, "application/json", response)
            }
            else -> {
                val error = JSONObject().put("error", "Not Found").toString()
                newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", error)
            }
        }
    }
}