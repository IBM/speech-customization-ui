package com.ibm.ttscustomization.ttsmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.*;
import java.io.IOException;
import java.nio.charset.Charset;

public class RestClientErrorHandler extends DefaultResponseErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(RestClientErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        return (statusCode != null && hasError(statusCode));
    }

    protected boolean hasError(HttpStatus statusCode) {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        handleError(response, statusCode);
    }

    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw new HttpClientErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            case SERVER_ERROR:
                throw new HttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            default:
                throw new UnknownHttpStatusCodeException(statusCode.value(), response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
        }
    }

    /**
     * Determine the HTTP status of the given response.
     * @param response the response to inspect
     * @return the associated HTTP status
     * @throws IOException in case of I/O errors
     * @throws UnknownHttpStatusCodeException in case of an unknown status code
     * that cannot be represented with the {@link HttpStatus} enum
     * @since 4.3.8
     * @deprecated as of 5.0, in favor of {@link #handleError(ClientHttpResponse, HttpStatus)}
     */
    @Deprecated
    protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        }
        catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }

    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null ? contentType.getCharset() : null);
    }

        public static class RestUtil {

        private RestUtil() {
            throw new IllegalAccessError("Utility class");
        }

        public static boolean isError(HttpStatus status) {
            HttpStatus.Series series = status.series();
            return HttpStatus.Series.CLIENT_ERROR.equals(series)
                    || HttpStatus.Series.SERVER_ERROR.equals(series);
        }
    }}