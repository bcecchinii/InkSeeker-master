package com.onlyapps.inkseeker

import android.os.StrictMode
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object requests {

    fun POST(url:String, params: Array<Array<String>>): String {

        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var reqParam = URLEncoder.encode("","UTF-8")

        for (param in params) {
            reqParam += URLEncoder.encode(param[0], "UTF-8") + "=" + URLEncoder.encode(param[1], "UTF-8") + "&"
        }
        reqParam.dropLast(1)

        val mURL = URL(url)

        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(reqParam);
            wr.flush();

            //println("URL : $url")
            //println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLines = it.readLines()
                it.close()

                for (inputLine in inputLines) {
                    response.append(inputLine)
                }
                return "$response"
            }
        }
    }

    fun GET(url:String): String {

        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val mURL = URL(url)

        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            //println("URL : $url")
            //println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLines = it.readLines()
                it.close()

                for (inputLine in inputLines) {
                    response.append(inputLine)
                }
                return "$response"
            }
        }
    }
}