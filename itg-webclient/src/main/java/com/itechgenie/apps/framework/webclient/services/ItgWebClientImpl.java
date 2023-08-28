package com.itechgenie.apps.framework.webclient.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import com.itechgenie.apps.framework.webclient.annotations.ItgWebClient;
import com.itechgenie.apps.framework.webclient.config.AppWebClientConfig;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItgWebClientImpl {

	@Autowired
	AppWebClientConfig appWebClientConfig;

	public ItgWebClientImpl(AppWebClientConfig config) {
		this.appWebClientConfig = config;
	}

	HttpClient httpClient;

	WebClient webClient;

	public Mono<Authentication> getUserInfo() {
		Mono<Authentication> auth = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
		log.debug("Obtained userAuthentication: " + auth);
		return auth;
	}

	@PostConstruct
	public void loadConfigs() {

		if (httpClient == null) {
			httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
					.responseTimeout(Duration.ofMillis(5000)).wiretap(appWebClientConfig.isDebug())
					.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
							.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
		}

		if (webClient == null) {
			webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)) // customize
					// the
					// timeout
					// options
					// here
					.baseUrl("https://jsonplaceholder.typicode.com").defaultCookie("cookie-name", "cookie-value")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
		}

	}

	public <T> T executeWebClient(String id, String uri, HttpMethod httpMethod,
			MultiValueMap<String, String> headersMap, Object body, Type elementType, Class<T> returnType) {
		log.info(id + " - uri: " + uri + " - headersMap: " + headersMap + " - body: " + body + " - elementType: "
				+ elementType + " - returnType: " + returnType);
		try {
			Mono<Authentication> auth =  getUserInfo();
			log.info("Obtained auth: " + auth);
		} catch (Exception e) {
			log.error("Exception in executeWebClient: " + e.getMessage(), e);
		}

		WebClient.RequestBodySpec requestSpec = webClient.method(httpMethod).uri(uri)
				.headers(headers -> headers.addAll(headersMap));

		if (body != null) {
			requestSpec.body(BodyInserters.fromValue(body));
		}

		WebClient.ResponseSpec responseSpec = requestSpec.retrieve();
		// if(Flux.class == elementType ) {
		if (((ParameterizedType) elementType).getRawType() == Flux.class || Flux.class.isAssignableFrom(returnType)) {
			log.info(id + ": " + "found flux ");
			return (T) responseSpec.bodyToFlux(returnType);
		} else if (((ParameterizedType) elementType).getRawType() == Mono.class) {
			log.info(id + ": " + "found mono ");
			return (T) responseSpec.bodyToMono(returnType);
		} else if (ResponseEntity.class.isAssignableFrom(returnType)) {
			log.info(id + ": " + "found ResponseEntity ");
			return (T) responseSpec.toEntity(returnType);
		} else if (isSimpleClass(returnType)) {
			log.info(id + ": " + "found simple ");
			return (T) responseSpec.toEntity(returnType);

			// return (T) responseSpec.toEntity(returnType.getClass());
		} else {
			throw new IllegalArgumentException("Unsupported return type: " + returnType);
		}
	}

	public <T> T execute(String id, String classname, Method method, ItgWebClient annotation, Object... args) {
		log.info("Inside ItgWebClientImpl.execute: id: " + id + " - " + classname + " - methodName: " + method.getName()
				+ " - attributes: " + annotation);

		Annotation[] annotations = method.getDeclaredAnnotations();
		String _uri = extractUri(method, args);

		MultiValueMap<String, String> headersMap = extractHeaders(annotation);

		Object body = null;

		if (args != null && args.length > 0) {
			body = args[0]; // we wil extend it later as needed !
		}
		HttpMethod _httpMethod = extractHttpMethod(annotations);

		Type elementType = method.getGenericReturnType();
		Class<?> returnType = extractElementType(elementType);

		log.info(id + ": " + "Return type of the method: " + returnType);
		log.info(id + ": " + "Element type of the method: " + elementType);

		return (T) executeWebClient(id, _uri, _httpMethod, headersMap, body, elementType, returnType);

	}

	private boolean isSimpleClass(Class<?> clazz) {
		return !clazz.isArray() && !Flux.class.isAssignableFrom(clazz) && !Mono.class.isAssignableFrom(clazz)
				&& !ResponseEntity.class.isAssignableFrom(clazz);
	}

	public Class<?> extractElementType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

			if (rawType instanceof Class<?>) {
				Class<?> rawClass = (Class<?>) rawType;
				if (Flux.class.isAssignableFrom(rawClass) || Mono.class.isAssignableFrom(rawClass)) {
					if (actualTypeArguments.length == 1) {
						Type argumentType = actualTypeArguments[0];
						if (argumentType instanceof ParameterizedType) {
							return (Class<?>) ((ParameterizedType) argumentType).getRawType();
						} else if (argumentType instanceof Class<?>) {
							return (Class<?>) argumentType;
						}
					}
				} else {
					return rawClass;
				}
			}
		} else if (type instanceof Class<?>) {
			return (Class<?>) type;
		}

		throw new IllegalArgumentException("Cannot extract element type from: " + type);
	}

	private HttpMethod extractHttpMethod(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof GetExchange) {
				return HttpMethod.GET;
			} else if (annotation instanceof PostExchange) {
				return HttpMethod.POST;
			} else if (annotation instanceof PutExchange) {
				return HttpMethod.PUT;
			} else if (annotation instanceof DeleteExchange) {
				return HttpMethod.DELETE;
			}
		}

		throw new IllegalArgumentException("No HTTP method annotation found");
	}

	private String __extractUri(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof GetExchange) {
				return ((GetExchange) annotation).value();
			} else if (annotation instanceof PostExchange) {
				return ((PostExchange) annotation).value();
			} else if (annotation instanceof PutExchange) {
				return ((PutExchange) annotation).value();
			} else if (annotation instanceof DeleteExchange) {
				return ((DeleteExchange) annotation).value();
			}
		}

		log.warn("No Url details found in annotation, treating it as an empty url");
		return "";
	}

	private String extractUri(Method method, Object... args) {

		Annotation[] annotations = method.getDeclaredAnnotations();
		String uri = "";
		for (Annotation annotation : annotations) {
			if (annotation instanceof GetExchange) {
				uri = ((GetExchange) annotation).value();
				uri = updatePathVariables(method, uri, args);
			} else if (annotation instanceof PostExchange) {
				uri = ((PostExchange) annotation).value();
				uri = updatePathVariables(method, uri);
			} else if (annotation instanceof PutExchange) {
				uri = ((PutExchange) annotation).value();
				uri = updatePathVariables(method, uri);
			} else if (annotation instanceof DeleteExchange) {
				uri = ((DeleteExchange) annotation).value();
				uri = updatePathVariables(method, uri);
			}
		}

		log.debug("Obtained uri: " + uri);
		return uri;
	}

	private MultiValueMap<String, String> extractHeaders(ItgWebClient annotation) {
		MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
		String[] headersArr = annotation.headers();
		if (headersArr != null) {
			Arrays.asList(headersArr).forEach((value) -> {
				if (value != null) {
					String[] _headers = value.split(":");
					if (_headers[0] != null && _headers[1] != null) {
						headersMap.add(_headers[0], _headers[1].trim());
					}
				}
			});
		}
		return headersMap;
	}

	public String updatePathVariables(Method method, String uri, Object... args) {

		if (uri.contains("{") && uri.contains("}")) {
			log.debug("Expecting path params: ");

			Map<String, String> pathParams = extractPathVariableValues(method, args);
			log.debug("Extracted path params: " + pathParams);

			if (pathParams != null && pathParams.size() > 0) {
				uri = replacePathVariables(uri, pathParams);
			}
		}

		return uri;
	}

	public static Map<String, String> extractPathVariableValues(Method method, Object[] args) {
		Map<String, String> pathVariables = new HashMap<>();

		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			PathVariable pathVariableAnnotation = parameter.getAnnotation(PathVariable.class);
			if (pathVariableAnnotation != null) {
				String pathVariableName = pathVariableAnnotation.value();
				String pathVariableValue = args[i].toString();
				pathVariables.put(pathVariableName, pathVariableValue);
			}
		}

		return pathVariables;
	}

	/*
	 * public static Map<String, String> extractPathVariableValues(Method method,
	 * String url) { Annotation[][] parameterAnnotations =
	 * method.getParameterAnnotations(); Map<String, String> pathVariableValues =
	 * new HashMap<>();
	 * 
	 * String pathPattern = url.replaceAll("\\{\\w+\\}", "(\\\\w+)");
	 * log.debug("pathPattern: " + pathPattern); Pattern pattern =
	 * Pattern.compile(pathPattern);
	 * 
	 * Matcher matcher = pattern.matcher(url); if (matcher.matches()) {
	 * log.debug("Inside matcher :"); for (int i = 1; i <= matcher.groupCount();
	 * i++) { String pathVariableName = matcher.group(i);
	 * log.debug("Inside matcher - pathVariableName: " + pathVariableName);
	 * Annotation[] annotations = parameterAnnotations[i - 1]; for (Annotation
	 * annotation : annotations) { if (annotation instanceof PathVariable) {
	 * PathVariable pathVariable = (PathVariable) annotation; String
	 * pathVariableValue = "${" + pathVariableName + "}";
	 * pathVariableValues.put(pathVariable.value(), pathVariableValue); } } } }
	 * 
	 * return pathVariableValues; }
	 */

	public static String replacePathVariables(String url, Map<String, String> pathVariables) {
		for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
			String placeholder = "{" + entry.getKey() + "}";
			String value = entry.getValue();
			url = url.replace(placeholder, value);
		}
		return url;
	}

}