package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dal.Product;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * Deletes a product from the underlying DynamoDB table by a given id.
 */
public class DeleteProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private Logger LOG = Logger.getLogger(DeleteProductHandler.class);

	/**
	 * Receives the id via the path parameters attribute of the input. Then it calls the delete() method
	 * on the product instance passing it the id to delete the product.
	 * @param input the input
	 * @param context the context
	 * @return ApiGatewayResponse
	 */
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			Map<String,String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String productId = pathParameters.get("id");
			// get product by id
			boolean success = new Product().delete(productId);
			// send response back
			if (success) {
				String responseBody = "Product with id: [" + productId + "] deleted successfully.";
				return sendResponseBack(responseBody, 204);
			} else {
				String responseBody = "Product with id: [" + productId + "] not found.";
				return sendResponseBack(responseBody, 404);
			}

		} catch (Exception e) {
			LOG.error("Error in delete product: {}", e);
			// send error response back
			Response responseBody = new Response("Error in delete product.", input);
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
