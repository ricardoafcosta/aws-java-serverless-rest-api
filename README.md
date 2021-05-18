# Serverless REST API in Java/Maven using DynamoDB
The sample serverless service will create a REST API for products. It will be deployed to AWS. The data will be stored in a DynamoDB table.

## Install Pre-requisites
- `node` and `npm`
- Install the JDK and NOT the Java JRE from [Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html). And set the following: `export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-10.jdk/Contents/Home`
- [Apache Maven](https://maven.apache.org/). After [downloading](https://maven.apache.org/download.html) and [installing](https://maven.apache.org/install.html) Apache Maven, please add the `apache-maven-x.x.x` folder to the PATH environment variable.

### Test pre-requisits
Test Java installation:
```
$ java --version

openjdk 13.0.2 2020-01-14
OpenJDK Runtime Environment (build 13.0.2+8)
OpenJDK 64-Bit Server VM (build 13.0.2+8, mixed mode, sharing)
```

Test Maven installation:
```cli
$ mvn -v

Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /usr/local/Cellar/maven/3.6.3_1/libexec
Java version: 1.8.0_222, vendor: AdoptOpenJDK, runtime: /Users/ricardoafcosta/.sdkman/candidates/java/8.0.222.hs-adpt/jre
Default locale: en_PT, platform encoding: UTF-8
OS name: "mac os x", version: "10.16", arch: "x86_64", family: "mac"
```

## Build the Java Project
```
$ cd aws-java-products-api
$ mvn clean install

[INFO] Scanning for projects...
[INFO]
[INFO] --------------------< com.serverless:products-api >---------------------
[INFO] Building products-api dev
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ products-api ---
[INFO] Deleting /Users/ricardocosta/Documents/Learning/AWS_Lambda_Udemy/mycode/aws-java-products-api/target

...
...

[INFO] --- maven-install-plugin:2.4:install (default-install) @ products-api ---
[INFO] Installing /Users/ricardocosta/Documents/Learning/AWS_Lambda_Udemy/mycode/aws-java-products-api/target/products-api-dev.jar to /Users/ricardocosta/.m2/repository/com/serverless/products-api/dev/products-api-dev.jar
[INFO] Installing /Users/ricardocosta/Documents/Learning/AWS_Lambda_Udemy/mycode/aws-java-products-api/pom.xml to /Users/ricardocosta/.m2/repository/com/serverless/products-api/dev/products-api-dev.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.775 s
[INFO] Finished at: 2021-05-17T11:23:35+01:00
[INFO] ------------------------------------------------------------------------
```

We can see that we have an artifact in the `target` folder named `products-api-dev.jar`.

## Deploy the Serverless app
```
$ sls deploy -v

Serverless: Packaging service...
Serverless: Uploading CloudFormation file to S3...
Serverless: Uploading artifacts...
Serverless: Uploading service products-api-dev.jar file to S3 (8.86 MB)...
Serverless: Validating template...
Serverless: Updating Stack...
Serverless: Checking Stack update progress...

...
...

Service Information
service: products-api
stage: dev
region: eu-west-2
stack: products-api-dev
resources: 28
api keys:
  None
endpoints:
  GET - https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products
  GET - https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products/{id}
  POST - https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products
  DELETE - https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products/{id}
functions:
  listProducts: products-api-dev-listProducts
  getProduct: products-api-dev-getProduct
  createProduct: products-api-dev-createProduct
  deleteProduct: products-api-dev-deleteProduct
layers:
  None
```

## Test the API
We can invoke each of the four functions that we created as part of the app.

# Create Product
```
$ curl -X POST https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products -d '{"name": "Product1", "price": 9.99}'
```

# List Products
```
$ curl https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products

[{"id":"fb5621e5-f072-4f4e-9573-5640b6d33ac9","name":"Product3","price":8.99},
{"id":"11dd50d0-51b7-4a26-8c45-b4524c2c56c5","name":"Product1","price":9.99},
{"id":"ef05ac05-a8c5-44b9-9be0-5f9651dcf602","name":"Product2","price":11.99}]
```
### No products found
```
$ curl https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/products

[]
```

# Get Product
```
$ curl https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/product/11dd50d0-51b7-4a26-8c45-b4524c2c56c5

{"id":"11dd50d0-51b7-4a26-8c45-b4524c2c56c5","name":"Product1","price":9.99}
```
### Product not found
```
$ curl https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/product/xxxxxxx

"Product with id: [xxxxxxx] not found."
```

# Delete Product
```
$ curl -X DELETE https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/product/11dd50d0-51b7-4a26-8c45-b4524c2c56c5
```

### Product not found
```
$ curl -X DELETE https://xxxxxxxxxx.execute-api.eu-west-2.amazonaws.com/dev/product/xxxxxxx

"Product with id: [xxxxxxx] not found."
```

## View the CloudWatch Logs
```
$ serverless logs --function getProduct

START RequestId: 34f45684-3dd0-11e8-bf8a-7f961671b2de Version: $LATEST
...

2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> DEBUG org.apache.http.wire:86 - http-outgoing-0 >> "{"TableName":"java-products-dev","ConsistentRead":true,"ScanIndexForward":true,"KeyConditionExpression":"id = :v1","ExpressionAttributeValues":{":v1":{"S":"6f1dfeb9-ea08-4161-8877-f6cc724b39e3"}}}"

...

2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> DEBUG org.apache.http.wire:86 - http-outgoing-0 << "{"Count":1,"Items":[{"price":{"N":"9.99"},"id":{"S":"11dd50d0-51b7-4a26-8c45-b4524c2c56c5"},"name":{"S":"Product1"}}],"ScannedCount":1}"

...

2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager:314 - Connection [id: 0][route: {s}->https://dynamodb.us-east-1.amazonaws.com:443] can be kept alive for 60.0 seconds
2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> DEBUG org.apache.http.impl.conn.PoolingHttpClientConnectionManager:320 - Connection released: [id: 0][route: {s}->https://dynamodb.us-east-1.amazonaws.com:443][total kept alive: 1; route allocated: 1 of 50; total allocated: 1 of 50]
2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> DEBUG com.amazonaws.request:87 - Received successful response: 200, AWS Request ID: MT1EV3AV07T9OD0MJH9VBJSIB7VV4KQNSO5AEMVJF66Q9ASUAAJG
2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> DEBUG com.amazonaws.requestId:136 - x-amzn-RequestId: MT1EV3AV07T9OD0MJH9VBJSIB7VV4KQNSO5AEMVJF66Q9ASUAAJG
2018-04-11 21:35:14 <34f45684-3dd0-11e8-bf8a-7f961671b2de> INFO  com.serverless.dal.Product:107 - Products - get(): product - Product [id=11dd50d0-51b7-4a26-8c45-b4524c2c56c5, name=Product1, price=$9.990000]
END RequestId: 34f45684-3dd0-11e8-bf8a-7f961671b2de
REPORT RequestId: 34f45684-3dd0-11e8-bf8a-7f961671b2de	Duration: 5147.00 ms	Billed Duration: 5200 ms 	Memory Size: 1024 MB	Max Memory Used: 97 MB
```

## View Metrics
View the metrics for the service:
```
$ sls metrics

Serverless: Deprecation warning: Starting with version 3.0.0, following property will be replaced:
              "provider.iamRoleStatements" -> "provider.iam.role.statements"
            More Info: https://www.serverless.com/framework/docs/deprecations/#PROVIDER_IAM_SETTINGS
Service wide metrics
May 16, 2021 2:07 PM - May 17, 2021 2:07 PM

Invocations: 13
Throttles: 0
Errors: 0
Duration (avg.): 3870.64ms
```

View the metrics for only one function:
```
$ sls metrics --function getProduct

Serverless: Deprecation warning: Starting with version 3.0.0, following property will be replaced:
              "provider.iamRoleStatements" -> "provider.iam.role.statements"
            More Info: https://www.serverless.com/framework/docs/deprecations/#PROVIDER_IAM_SETTINGS
getProduct
May 16, 2021 2:10 PM - May 17, 2021 2:10 PM

Invocations: 11
Throttles: 0
Errors: 0
Duration (avg.): 3553.82ms
```

---
For more info
https://www.serverless.com/blog/how-to-create-a-rest-api-in-java-using-dynamodb-and-serverless
