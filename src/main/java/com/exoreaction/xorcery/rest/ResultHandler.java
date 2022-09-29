package com.exoreaction.xorcery.rest;

import jakarta.ws.rs.WebApplicationException;
import org.eclipse.jetty.client.api.Result;

import java.util.function.Consumer;

import static org.eclipse.jetty.http.HttpStatus.ACCEPTED_202;
import static org.eclipse.jetty.http.HttpStatus.BAD_GATEWAY_502;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.CONFLICT_409;
import static org.eclipse.jetty.http.HttpStatus.CONTINUE_100;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.ENHANCE_YOUR_CALM_420;
import static org.eclipse.jetty.http.HttpStatus.EXPECTATION_FAILED_417;
import static org.eclipse.jetty.http.HttpStatus.FAILED_DEPENDENCY_424;
import static org.eclipse.jetty.http.HttpStatus.FORBIDDEN_403;
import static org.eclipse.jetty.http.HttpStatus.GATEWAY_TIMEOUT_504;
import static org.eclipse.jetty.http.HttpStatus.GONE_410;
import static org.eclipse.jetty.http.HttpStatus.HTTP_VERSION_NOT_SUPPORTED_505;
import static org.eclipse.jetty.http.HttpStatus.IM_A_TEAPOT_418;
import static org.eclipse.jetty.http.HttpStatus.INSUFFICIENT_STORAGE_507;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.http.HttpStatus.LENGTH_REQUIRED_411;
import static org.eclipse.jetty.http.HttpStatus.LOCKED_423;
import static org.eclipse.jetty.http.HttpStatus.LOOP_DETECTED_508;
import static org.eclipse.jetty.http.HttpStatus.METHOD_NOT_ALLOWED_405;
import static org.eclipse.jetty.http.HttpStatus.MISDIRECTED_REQUEST_421;
import static org.eclipse.jetty.http.HttpStatus.MOVED_PERMANENTLY_301;
import static org.eclipse.jetty.http.HttpStatus.MOVED_TEMPORARILY_302;
import static org.eclipse.jetty.http.HttpStatus.MULTIPLE_CHOICES_300;
import static org.eclipse.jetty.http.HttpStatus.MULTI_STATUS_207;
import static org.eclipse.jetty.http.HttpStatus.NETWORK_AUTHENTICATION_REQUIRED_511;
import static org.eclipse.jetty.http.HttpStatus.NON_AUTHORITATIVE_INFORMATION_203;
import static org.eclipse.jetty.http.HttpStatus.NOT_ACCEPTABLE_406;
import static org.eclipse.jetty.http.HttpStatus.NOT_EXTENDED_510;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.NOT_IMPLEMENTED_501;
import static org.eclipse.jetty.http.HttpStatus.NOT_MODIFIED_304;
import static org.eclipse.jetty.http.HttpStatus.NO_CONTENT_204;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.eclipse.jetty.http.HttpStatus.PARTIAL_CONTENT_206;
import static org.eclipse.jetty.http.HttpStatus.PAYLOAD_TOO_LARGE_413;
import static org.eclipse.jetty.http.HttpStatus.PAYMENT_REQUIRED_402;
import static org.eclipse.jetty.http.HttpStatus.PERMANENT_REDIRECT_308;
import static org.eclipse.jetty.http.HttpStatus.PRECONDITION_FAILED_412;
import static org.eclipse.jetty.http.HttpStatus.PRECONDITION_REQUIRED_428;
import static org.eclipse.jetty.http.HttpStatus.PROCESSING_102;
import static org.eclipse.jetty.http.HttpStatus.PROXY_AUTHENTICATION_REQUIRED_407;
import static org.eclipse.jetty.http.HttpStatus.RANGE_NOT_SATISFIABLE_416;
import static org.eclipse.jetty.http.HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE_431;
import static org.eclipse.jetty.http.HttpStatus.REQUEST_TIMEOUT_408;
import static org.eclipse.jetty.http.HttpStatus.RESET_CONTENT_205;
import static org.eclipse.jetty.http.HttpStatus.SEE_OTHER_303;
import static org.eclipse.jetty.http.HttpStatus.SERVICE_UNAVAILABLE_503;
import static org.eclipse.jetty.http.HttpStatus.SWITCHING_PROTOCOLS_101;
import static org.eclipse.jetty.http.HttpStatus.TEMPORARY_REDIRECT_307;
import static org.eclipse.jetty.http.HttpStatus.TOO_MANY_REQUESTS_429;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;
import static org.eclipse.jetty.http.HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS_451;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;
import static org.eclipse.jetty.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE_415;
import static org.eclipse.jetty.http.HttpStatus.UPGRADE_REQUIRED_426;
import static org.eclipse.jetty.http.HttpStatus.URI_TOO_LONG_414;
import static org.eclipse.jetty.http.HttpStatus.USE_PROXY_305;

public interface ResultHandler
        extends Consumer<Result> {
    @Override
    default void accept(Result result) {
        int status = result.getResponse().getStatus();
        if (status < 200) {
            status1xx(result);
        } else if (status < 300) {
            status2xx(result);
        } else if (status < 400) {
            status3xx(result);
        } else if (status < 500) {
            status4xx(result);
        } else if (status < 600) {
            status5xx(result);
        } else {
            statusOther(result);
        }
    }

    default void status1xx(Result result) {
        switch (result.getResponse().getStatus())
        {
            case CONTINUE_100: status100(result); break;
            case SWITCHING_PROTOCOLS_101: status101(result); break;
            case PROCESSING_102: status102(result); break;
            default: unknown(result);
        }
    }

    default void status100(Result result)
    {
        unknown(result);
    }
    default void status101(Result result)
    {
        unknown(result);
    }
    default void status102(Result result)
    {
        unknown(result);
    }

    default void status2xx(Result result) {
        switch (result.getResponse().getStatus())
        {
            case OK_200: status200(result); break;
            case CREATED_201: status201(result); break;
            case ACCEPTED_202: status202(result); break;
            case NON_AUTHORITATIVE_INFORMATION_203: status203(result); break;
            case NO_CONTENT_204: status204(result); break;
            case RESET_CONTENT_205: status205(result); break;
            case PARTIAL_CONTENT_206: status206(result); break;
            case MULTI_STATUS_207: status207(result); break;
            default: unknown(result);
        }
    }
    default void status200(Result result)
    {
        unknown(result);
    }
    default void status201(Result result)
    {
        unknown(result);
    }
    default void status202(Result result)
    {
        unknown(result);
    }
    default void status203(Result result)
    {
        unknown(result);
    }
    default void status204(Result result)
    {
        unknown(result);
    }
    default void status205(Result result)
    {
        unknown(result);
    }
    default void status206(Result result)
    {
        unknown(result);
    }
    default void status207(Result result)
    {
        unknown(result);
    }


    default void status3xx(Result result) {
        switch (result.getResponse().getStatus()) {

            case MULTIPLE_CHOICES_300: status300(result); break;
            case MOVED_PERMANENTLY_301: status301(result); break;
            case MOVED_TEMPORARILY_302: status302(result); break;
            case SEE_OTHER_303: status303(result); break;
            case NOT_MODIFIED_304: status304(result); break;
            case USE_PROXY_305: status305(result); break;
            case TEMPORARY_REDIRECT_307: status307(result); break;
            case PERMANENT_REDIRECT_308: status308(result); break;
            default: unknown(result);
        }
    }

    default void status300(Result result)
    {
        unknown(result);
    }
    default void status301(Result result)
    {
        unknown(result);
    }
    default void status302(Result result)
    {
        unknown(result);
    }
    default void status303(Result result)
    {
        unknown(result);
    }
    default void status304(Result result)
    {
        unknown(result);
    }
    default void status305(Result result)
    {
        unknown(result);
    }
    default void status307(Result result)
    {
        unknown(result);
    }
    default void status308(Result result)
    {
        unknown(result);
    }

    default void status4xx(Result result) {
        switch (result.getResponse().getStatus()) {

            case BAD_REQUEST_400: status400(result); break;
            case UNAUTHORIZED_401: status401(result); break;
            case PAYMENT_REQUIRED_402: status402(result); break;
            case FORBIDDEN_403: status403(result); break;
            case NOT_FOUND_404: status404(result); break;
            case METHOD_NOT_ALLOWED_405: status405(result); break;
            case NOT_ACCEPTABLE_406: status406(result); break;
            case PROXY_AUTHENTICATION_REQUIRED_407: status407(result); break;
            case REQUEST_TIMEOUT_408: status408(result); break;
            case CONFLICT_409: status409(result); break;
            case GONE_410: status410(result); break;
            case LENGTH_REQUIRED_411: status411(result); break;
            case PRECONDITION_FAILED_412: status412(result); break;
            case PAYLOAD_TOO_LARGE_413: status413(result); break;
            case URI_TOO_LONG_414: status414(result); break;
            case UNSUPPORTED_MEDIA_TYPE_415: status415(result); break;
            case RANGE_NOT_SATISFIABLE_416: status416(result); break;
            case EXPECTATION_FAILED_417: status417(result); break;
            case IM_A_TEAPOT_418: status418(result); break;
            case ENHANCE_YOUR_CALM_420: status420(result); break;
            case MISDIRECTED_REQUEST_421: status421(result); break;
            case UNPROCESSABLE_ENTITY_422: status422(result); break;
            case LOCKED_423: status423(result); break;
            case FAILED_DEPENDENCY_424: status424(result); break;
            case UPGRADE_REQUIRED_426: status426(result); break;
            case PRECONDITION_REQUIRED_428: status428(result); break;
            case TOO_MANY_REQUESTS_429: status429(result); break;
            case REQUEST_HEADER_FIELDS_TOO_LARGE_431: status431(result); break;
            case UNAVAILABLE_FOR_LEGAL_REASONS_451: status451(result); break;
            default: unknown(result);
        }
    }
    default void status400(Result result)
    {
        unknown(result);
    }
    default void status401(Result result)
    {
        unknown(result);
    }
    default void status402(Result result)
    {
        unknown(result);
    }
    default void status403(Result result)
    {
        unknown(result);
    }
    default void status404(Result result)
    {
        unknown(result);
    }
    default void status405(Result result)
    {
        unknown(result);
    }
    default void status406(Result result)
    {
        unknown(result);
    }
    default void status407(Result result)
    {
        unknown(result);
    }
    default void status408(Result result)
    {
        unknown(result);
    }
    default void status409(Result result)
    {
        unknown(result);
    }
    default void status410(Result result)
    {
        unknown(result);
    }
    default void status411(Result result)
    {
        unknown(result);
    }
    default void status412(Result result)
    {
        unknown(result);
    }
    default void status413(Result result)
    {
        unknown(result);
    }
    default void status414(Result result)
    {
        unknown(result);
    }
    default void status415(Result result)
    {
        unknown(result);
    }
    default void status416(Result result)
    {
        unknown(result);
    }

    default void status417(Result result)
    {
        unknown(result);
    }

    default void status418(Result result)
    {
        unknown(result);
    }

    default void status420(Result result)
    {
        unknown(result);
    }

    default void status421(Result result)
    {
        unknown(result);
    }

    default void status422(Result result)
    {
        unknown(result);
    }

    default void status423(Result result)
    {
        unknown(result);
    }

    default void status424(Result result)
    {
        unknown(result);
    }

    default void status426(Result result)
    {
        unknown(result);
    }

    default void status428(Result result)
    {
        unknown(result);
    }

    default void status429(Result result)
    {
        unknown(result);
    }

    default void status431(Result result)
    {
        unknown(result);
    }

    default void status451(Result result)
    {
        unknown(result);
    }

    
    default void status5xx(Result result) {
        switch (result.getResponse().getStatus()) {

            case INTERNAL_SERVER_ERROR_500: status500(result); break;
            case NOT_IMPLEMENTED_501: status501(result); break;
            case BAD_GATEWAY_502: status502(result); break;
            case SERVICE_UNAVAILABLE_503: status503(result); break;
            case GATEWAY_TIMEOUT_504: status504(result); break;
            case HTTP_VERSION_NOT_SUPPORTED_505: status505(result); break;
            case INSUFFICIENT_STORAGE_507: status507(result); break;
            case LOOP_DETECTED_508: status508(result); break;
            case NOT_EXTENDED_510: status510(result); break;
            case NETWORK_AUTHENTICATION_REQUIRED_511: status511(result); break;
            default: unknown(result);
        }

    }

    default void status500(Result result)
    {
        unknown(result);
    }
    default void status501(Result result)
    {
        unknown(result);
    }
    default void status502(Result result)
    {
        unknown(result);
    }
    default void status503(Result result)
    {
        unknown(result);
    }
    default void status504(Result result)
    {
        unknown(result);
    }
    default void status505(Result result)
    {
        unknown(result);
    }
    default void status507(Result result)
    {
        unknown(result);
    }
    default void status508(Result result)
    {
        unknown(result);
    }
    default void status510(Result result)
    {
        unknown(result);
    }
    default void status511(Result result)
    {
        unknown(result);
    }

    default void statusOther(Result result) {
        unknown(result);
    }

    default void unknown(Result result) {
        error(new WebApplicationException(result.getResponse().getReason(), result.getResponse().getStatus()));
    }

    default void error(Throwable throwable)
    {
    }
}
