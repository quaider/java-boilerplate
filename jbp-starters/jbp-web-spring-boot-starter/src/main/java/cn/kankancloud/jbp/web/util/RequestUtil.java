package cn.kankancloud.jbp.web.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public final class RequestUtil {

    private RequestUtil() {
    }

    public static HttpServletRequest getRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getServletRequestAttributes().getResponse();
    }

    private static ServletRequestAttributes getServletRequestAttributes() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        Assert.notNull(servletRequestAttributes, "RequestUtil only supported in web environment");

        return servletRequestAttributes;
    }

    public static boolean isWebEnvironment() {
        return null != RequestContextHolder.getRequestAttributes();
    }

    public static List<String> getHeaderValues(HttpServletRequest request, String header) {
        Enumeration<String> headers = request.getHeaders(header);
        if (headers == null || !headers.hasMoreElements()) {
            return Lists.newArrayList();
        }

        List<String> result = new ArrayList<>();

        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (StringUtils.isEmpty(value)) {
                continue;
            }

            result.addAll(Arrays.stream(value.split(",")).filter(StringUtils::isNotEmpty).collect(Collectors.toList()));
        }

        return result;
    }

    public static String getHeaderWithUrlDecode(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        try {
            value = StringUtils.isEmpty(value) ? "" : URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(headerName + "编码不支持");
        }

        return value;
    }
}
