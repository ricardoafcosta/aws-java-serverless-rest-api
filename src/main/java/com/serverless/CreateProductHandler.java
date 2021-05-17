package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.dal.Product;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * Creates a new Product instance, and save the product to the underlying DynamoDB table.
 */
public class CreateProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private org.apache.log4j.Logger LOG = Logger.getLogger(CreateProductHandler.class);

	/**
	 * Reads the JSON data received via the body attribute from the input object passed in.
	 * This data is used to instantiate a new Product instance, and the save() method is
	 * called to save the product to the underlying DynamoDB table.
	 * @param input the input object
	 * @param context the context
	 * @return ApiGatewayResponse
	 */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: {}", (Throwable) input);

        try {
            // get the body from the input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            // create the Product object for post
            Product product = new Product();
            // product.setId(body.get("id").asText());
            product.setName(body.get("name").asText());
            product.setPrice((float) body.get("price").asDouble());
            product.save(product);
            // send the response back
            return sendResponseBack(product, 200);
        } catch (Exception e) {
            LOG.error("Error in saving product: ", e);
			// send the error response back
			Response responseBody = new Response("Error in saving product: ", input);
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
