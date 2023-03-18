package cn.kankancloud.jbp.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Slf4j
@Component
public class GlobalRequestLogFilter extends OncePerRequestFilter implements Ordered {
    @Value("${request.log.enable:true}")
    private Boolean logEnabled;

    @Value("${request.log.body:false}")
    private Boolean logBodyEnabled;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !logEnabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long st = System.currentTimeMillis();

        BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(response);

        try {
            Map<String, String> requestMap = this.getTypesafeRequestMap(request);

            final StringBuilder logMessage = new StringBuilder(
                    "Receive Request - ").append("[HTTP METHOD:")
                    .append(request.getMethod())
                    .append("] [PATH INFO:")
                    .append(request.getServletPath())
                    .append("] [REQUEST PARAMETERS:").append(requestMap);


            if (isMultiPartRequest(request.getContentType())) {
                logMessage.append("] [REMOTE ADDRESS:")
                        .append(request.getRemoteAddr()).append("]");
                log.info(logMessage.toString());

                filterChain.doFilter(request, bufferedResponse);

                return;
            }

            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(request);
            logMessage.append("] [REQUEST BODY:")
                    .append(bufferedRequest.getRequestBody())
                    .append("] [REMOTE ADDRESS:")
                    .append(request.getRemoteAddr()).append("]");

            log.info(logMessage.toString());

            filterChain.doFilter(bufferedRequest, bufferedResponse);

        } catch (Exception ex) {
            log.error("[Error for request:{}] [Error:{}]", request.getRequestURI(), ex.getMessage(), ex);
            throw ex;
        } finally {
            st = System.currentTimeMillis() - st;

            if (Boolean.TRUE.equals(logBodyEnabled) && !isOctetStreamResponse(bufferedResponse.getContentType())) {
                log.info("Response Request - [URL:{}] [COST:{} ms] [RESPONSE:{}]", request.getRequestURI(), st, bufferedResponse.getContent(2048));
            } else {
                log.info("Response Request - [URL:{}] [COST:{} ms]", request.getRequestURI(), st);
            }
        }
    }

    private boolean isMultiPartRequest(String contentType) {
        return StringUtils.isNotEmpty(contentType) && contentType.startsWith("multipart/");
    }

    private boolean isOctetStreamResponse(String contentType) {
        return contentType != null && (
                MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType) || contentType.contains("application/vnd")
        );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 8;
    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap<>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String) requestParamNames.nextElement();
            String requestParamValue;
            if (requestParamName.equalsIgnoreCase("password")) {
                requestParamValue = "********";
            } else {
                requestParamValue = request.getParameter(requestParamName);
            }

            typesafeRequestMap.put(requestParamName, requestParamValue);
        }

        return typesafeRequestMap;
    }

    private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private byte[] buffer = null;

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);

            // Read InputStream and store its content in a buffer.
            InputStream is = req.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) > 0) {
                baos.write(buf, 0, read);
            }

            this.buffer = baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(this.buffer);
            return new BufferedServletInputStream(bais);
        }

        String getRequestBody() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
            String line = null;
            StringBuilder inputBuffer = new StringBuilder();
            do {
                line = reader.readLine();
                if (null != line) {
                    inputBuffer.append(line.trim());
                }
            } while (line != null);
            reader.close();

            return inputBuffer.toString().trim();
        }

    }

    private static final class BufferedServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bais;

        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int available() {
            return this.bais.available();
        }

        @Override
        public int read() {
            return this.bais.read();
        }

        @Override
        public int read(byte[] buf, int off, int len) {
            return this.bais.read(buf, off, len);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // my required later
        }
    }

    public static class TeeServletOutputStream extends ServletOutputStream {

        private final TeeOutputStream targetStream;

        public TeeServletOutputStream(OutputStream one, OutputStream two) {
            targetStream = new TeeOutputStream(one, two);
        }

        @Override
        public void write(int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        @Override
        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // 预留
        }
    }

    public static class BufferedResponseWrapper implements HttpServletResponse {

        HttpServletResponse original;
        TeeServletOutputStream tee;
        ByteArrayOutputStream bos;

        public BufferedResponseWrapper(HttpServletResponse response) {
            original = response;
        }

        public String getContent() {
            if (bos == null) {
                return String.format("called %s too early", BufferedResponseWrapper.class.getCanonicalName());
            }

            return bos.toString();
        }

        public String getContent(int len) {
            if (bos == null) {
                return String.format("called %s too early", BufferedResponseWrapper.class.getCanonicalName());
            }

            byte[] bytes = bos.toByteArray();
            if (bytes.length > len) {
                return new String(Arrays.copyOf(bytes, len)) + "...";
            }

            return new String(bytes);
        }

        public PrintWriter getWriter() throws IOException {
            return original.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (tee == null) {
                bos = new ByteArrayOutputStream();
                tee = new TeeServletOutputStream(original.getOutputStream(), bos);
            }

            return tee;
        }

        @Override
        public String getCharacterEncoding() {
            return original.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            original.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            original.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long l) {
            original.setContentLengthLong(l);
        }

        @Override
        public void setContentType(String type) {
            original.setContentType(type);
        }

        @Override
        public void setBufferSize(int size) {
            original.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return original.getBufferSize();
        }

        @Override
        public void flushBuffer() throws IOException {
            tee.flush();
        }

        @Override
        public void resetBuffer() {
            original.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return original.isCommitted();
        }

        @Override
        public void reset() {
            original.reset();
        }

        @Override
        public void setLocale(Locale loc) {
            original.setLocale(loc);
        }

        @Override
        public Locale getLocale() {
            return original.getLocale();
        }

        @Override
        public void addCookie(Cookie cookie) {
            original.addCookie(cookie);
        }

        @Override
        public boolean containsHeader(String name) {
            return original.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return original.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return original.encodeRedirectURL(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeUrl(String url) {
            return original.encodeUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeRedirectUrl(String url) {
            return original.encodeRedirectUrl(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            original.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            original.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            original.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            original.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            original.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            original.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            original.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            original.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            original.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc) {
            original.setStatus(sc);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setStatus(int sc, String sm) {
            original.setStatus(sc, sm);
        }

        @Override
        public String getHeader(String arg0) {
            return original.getHeader(arg0);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return original.getHeaderNames();
        }

        @Override
        public Collection<String> getHeaders(String arg0) {
            return original.getHeaders(arg0);
        }

        @Override
        public int getStatus() {
            return original.getStatus();
        }

    }
}