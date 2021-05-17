package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dal.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * Get a product from the underlying DynamoDB table by a given product id.
 */
public class GetProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(GetProductHandler.class);

	/**
	 * Receives the id via the path parameters attribute of the input. Then it calls the get() method on the product
	 * instance passes it the id to get back a matching product.
	 * @param input the input
	 * @param context the context
	 * @return ApiGatewayResponse
	 */
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			// get the pathParameters from the input
			Map<String, String> pathParameters = (Map<String, String>)input.get("pathParameters");
			String productId = pathParameters.get("id");
			// get the product by id
			Product product = new Product().get(productId);
			// send response back
			if (product != null) {
				return sendResponseBack(product, 200);
			} else {
				String responseBody = "Product with id: [" + productId + "] not found.";
				return sendResponseBack(responseBody, 404);
			}
		} catch (Exception e) {
			LOG.error("Error in retrieving product: ", e);
			// send the error response back
			Response responseBody = new Response("Error in retrieving product: ", input);
			return sendResponseBack(responseBody, 500);
		}
	}

	/**
	 * Creates a new ApiGatewayResponse and sends it back.
	 * If the call is successful, a 200 OK response is returned back.
	 * If no products with the matching id are found, a 404 Not Found response is returned back.
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
