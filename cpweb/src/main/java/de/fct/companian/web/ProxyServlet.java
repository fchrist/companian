/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.fct.companian.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A proxy that is used for AJAX cross-site scripting.<br>
 * <br>
 * This implementation is roughly based on
 * http://www.easywms.com/easywms/?q=en/how-send-cross-site-request-ajax
 * but improved in many points.
 * 
 * @author fabianc
 */
public class ProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ProxyServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                               IOException {
        fetch(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                   IOException {
        fetch(request, response);
    }

    private void fetch(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        BufferedInputStream webToProxyBuf = null;
        BufferedOutputStream proxyToClientBuf = null;
        HttpURLConnection con = null;

        int statusCode;
        int oneByte;
        String methodName;
        
        // Create request URL
        String urlString = request.getParameter("url");
        String queryString = request.getQueryString();
        queryString = queryString.replaceFirst("url=[^&]*(&)?", "");
        if (queryString != null && !queryString.isEmpty()) {
            urlString += "?" + queryString;
        }
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            logger.error("fetch() error creating well formed url", e);
        }

        if (url == null) {
            logger.error("fetch() cancelled because of malformed URL " + urlString);
            return;
        }         

        methodName = request.getMethod();
        if (logger.isDebugEnabled()) {
            logger.debug("fetch() method is " + methodName);
        }

        // Read content from request
        String utf8Content = null;
        if (methodName.equals("POST")) {
            BufferedInputStream clientToProxyBuf = null;
            try {
                clientToProxyBuf = new BufferedInputStream(request.getInputStream());
                ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();

                while ((oneByte = clientToProxyBuf.read()) != -1) {
                    outputByteArray.write(oneByte);
                }
                outputByteArray.flush();
                outputByteArray.close();

                utf8Content = URLDecoder.decode(outputByteArray.toString(), "UTF-8");

            } catch (IOException e) {
                logger.error("fetch() IO error reading from request", e);
            } finally {
                try {
                    clientToProxyBuf.close();
                } catch (Throwable t) {
                    // Okay, this may happen.
                }
            }
        }
        
        // Connecting to URL
        try {
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(methodName);
            con.setDoOutput(true);
            con.setDoInput(true);
            // HttpURLConnection.setFollowRedirects(false);
            con.setUseCaches(false);

            for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
                String headerName = e.nextElement().toString();
                con.setRequestProperty(headerName, request.getHeader(headerName));
            }

            con.connect();            
        } catch (IOException e) {
            logger.error("fetch() IO error while connecting to " + url, e);
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Throwable t) {
                    // Okay - this may happen.
                }
            }
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e2) {
                logger.error("fetch() IO error while sending response status code NOT_FOUND", e2);
            }
            return;
        }
        
        // Writing to URL
        OutputStreamWriter outwriter = null;        
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("fetch() writing UTF-8 encoded content" + utf8Content);
            }           
            outwriter = new OutputStreamWriter(con.getOutputStream());
            outwriter.write(utf8Content);  
            
        } catch (IOException e) {
            logger.error("fetch() error while writing to " + url);
        } finally {
            try {
                outwriter.flush();
                outwriter.close();            
            } catch (Throwable t) {
                // Okay - this may happen
            }
        }

        // Reading results from URL and writing to response
        ByteArrayOutputStream debugByteArray = null;
        try {
            webToProxyBuf = new BufferedInputStream(con.getInputStream());
            proxyToClientBuf = new BufferedOutputStream(response.getOutputStream());
            debugByteArray = new ByteArrayOutputStream();

            while ((oneByte = webToProxyBuf.read()) != -1) {
                proxyToClientBuf.write(oneByte);
                if (logger.isDebugEnabled()) {
                    debugByteArray.write(oneByte);
                }
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("fetch() received content: " + debugByteArray.toString());
            }
        } catch (IOException e) {
            logger.error("fetch() error while reading from " + url + " and writing to response");
        } finally {
            try {
                webToProxyBuf.close();
                proxyToClientBuf.flush();
                debugByteArray.flush();
                debugByteArray.close();
            } catch (Throwable t) {
                // Okay, this may happen.
            }
        }
        
        // Closing connection to URL
        try {            
            con.disconnect();
        } catch (Throwable t) {
            // Okay, this may happen
        }
        
        // Set response status
        try {
            statusCode = con.getResponseCode();
            response.setStatus(statusCode);
        } catch (IOException e) {
            logger.error("fetch() error getting the response code from " + url, e);
        }

        // Set response headers
        for (Map.Entry mapEntry : con.getHeaderFields().entrySet()) {
            if (mapEntry.getKey() != null) {
                response.setHeader(mapEntry.getKey().toString(), ((List) mapEntry.getValue()).get(0)
                        .toString());
            }
        }
        
        logger.debug("fetch() finished");
    }

}
