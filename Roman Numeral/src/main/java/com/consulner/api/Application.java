package com.consulner.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpServer;

class Application {

    public static void main(String[] args) throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/romannumeral", (exchange -> {

            if ("GET".equals(exchange.getRequestMethod())) {
                //parse input parameters
                Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
                OutputStream output = exchange.getResponseBody();
                if(isSecondExtension(params))//min and max case
                {
                    String min = params.get("min").stream().findFirst().orElse("");//number in query case
                    String max = params.get("max").stream().findFirst().orElse("");//number in query case
                    var resultList = new ArrayList<String>();
                    var numbersList = new ArrayList<String>();
                    for(Integer i=RomanNumeralConvertor.toInt(min);i<=RomanNumeralConvertor.toInt(max);++i)
                    {
                        resultList.add(RomanNumeralConvertor.convert(i.toString()));
                        numbersList.add(i.toString());
                    }
                    String response = generateResponse(numbersList , resultList);
                    exchange.sendResponseHeaders(200 , response.getBytes().length);
                    output.write(response.getBytes());
                    output.flush();
                }
                else if(isFirstExtension(params))//query case
                {
                    String queryNumber = params.get("query").stream().findFirst().orElse("");//number in query case
                    var res = RomanNumeralConvertor.convert(queryNumber);

                    String response = generateResponse(queryNumber , res , "");
                    exchange.sendResponseHeaders(200 , response.getBytes().length);
                    output.write(response.getBytes());
                    output.flush();
                }
                else//incorrect input parameters case
                {
                    String exceptionMessage = "Incorrect input parameters";
                    exchange.sendResponseHeaders(500 , exceptionMessage.getBytes().length);
                    output.write(exceptionMessage.getBytes());
                    output.flush();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static String generateResponse(String queryNumber, String res , String sep) {
        String response = String.format("%s{\n%s\t\"input\" : \"%s\",\n%s\t\"output\" : \"%s\"\n%s}", sep , sep ,queryNumber , sep , res , sep);
        return response;
    }

    private static String generateResponse(ArrayList<String> queryNumbers,ArrayList<String> results) {
        String response = "{\n\t\"conversions\" : [\n";
        for(int i=0;i<queryNumbers.size();++i)
        {
            response+=generateResponse(queryNumbers.get(i) , results.get(i) , "\t\t");
            if(i!=queryNumbers.size()-1)response+=",\n";
        }
        response+="\n\t]\n}";
        return response;
    }


    private static boolean isFirstExtension(Map<String, List<String>> params) {
        return ((params.size() == 1) && params.containsKey("query"));
    }

    private static boolean isSecondExtension(Map<String, List<String>> params) {
        return (params.size()==2 && params.containsKey("min") && params.containsKey("max"));
    }


    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
            .map(s -> Arrays.copyOf(s.split("="), 2))
            .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

}
