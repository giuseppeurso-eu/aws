# J4S3 - Java Client for Amazon S3

A simple Java application illustrating usage of the AWS SDK for Java. Once you've built the project, you can use the generated executable jar to send/receive objects from/to Amazon S3 in a secure manner.

## Requirements
JDK 1.7+  
Maven 3  

The project includes also a dependency of eu.giuseppeurso.security.jca. In order to succesfully build J4S3, first check out and build the eu.giuseppeurso.security.jca artifact first (https://github.com/giuseppeurso-eu/java-security/tree/master/jca). 


## Build project
```
git clone https://github.com/giuseppeurso-eu/java-security
cd java-security/jca
mvn install

git clone https://github.com/giuseppeurso-eu/aws
cd aws/j4s3
mvn install
```    

## Running J4S3

This sample application connects to Amazon's [Simple Storage Service (S3)](http://aws.amazon.com/s3),
and uploads a file to that bucket. When you start making your own buckets, the S3 documentation provides a good overview of the [restrictions for bucket names]
(http://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html).

 Once you've built the project, you can use the generated executable jar to send/receive objects from/to Amazon S3 in a secure manner. Run jar located in the target dir and follow the interactive wizard.
 ```
 cd target
 java -jar j4s3.jar
```

## How J4S3 works

## License

This sample application is distributed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

