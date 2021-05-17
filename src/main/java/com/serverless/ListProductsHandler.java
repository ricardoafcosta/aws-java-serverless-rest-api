package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dal.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Get the list of all products in the underlying DynamoDB table
 */
public class ListProductsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(ListProductsHandler.class);

	/**
	 * Calls the list() method on the product instance to get back a list of products.
	 * @param input the input
	 * @param context the context
	 * @return ApiGatewayResponse
	 */
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			// get all products
			List<Product> products = new Product().list();
			// send the response back
			return sendResponseBack(products, 200);
		} catch (Exception e) {
			LOG.error("Error in listing products: ", e);
			// send the error response back
			Response responseBody = new Response("Error in listing products: ", input);
			return sendResponseBack(responseBody, 500);
		}
	}

	/**
	 * Creates a new ApiGatewayResponse and sends it back.
	 * If the call is successful, a 200 OK response is returned back.
	 * In case of an error or exception, the exception is caught and a 500 Internal Server Error response is returned back.
	 * @param bodyResponse the body response depending on the success of the call
	 * @param statusCode the status code depending on the success of the call
	 * @return ApiGatewayResponse
	 */
	private ApiGatewayResponse sendResponseBack(Object bodyResponse, int statusCode) {
		return ApiGatewayResponse.builder()
				.setStatusCode(statusCode)
				.setObjectBody(bodyResponse)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
				.build();
	}
}
