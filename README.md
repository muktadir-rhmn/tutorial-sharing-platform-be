
# Design
![](docs/be_design.svg)

Each component is independent of other components. Each component has it's own data and no other component knows its data. 
One component don't even use the objects of other components, they interact with each other through a well defined interface.

Most of the components have two layers: the controller layer and the database layer. Database layer is responsible for database related operations. 
The controller layer uses the database layer to serve APIs. 

![](docs/be_module_design.svg)

The exceptions are 
- Auth Layer: Every request passes through Auth layer and this layer is responsible for
checking whether the request is from an authentic source. 
- Error Handler Layer: If any error is found, an exception is thrown and Error Handler layer is responsible for 
generating appropriate error response. 
- Cache Layer: If an API is set to be cached, this cache layer caches API response. Once cached, any further request to that API 
will be served from the cache, until it expires. 

## Resource Model
I tried to follow REST. 

The following collections represent the BE to FE:
- /users
- /hierarchy
- /tutorials
- /lessons
- /comments
- /notes
- /markings
- /evaluations

Each item in any of the collections has a unique identifier. The path `/{collectionName}/{itemID}` represents an item of a collection.

For example, each tutorial has a unique identifier. The path `/tutorials/5f312e48217a060239bfc782` represents a tutorial with ID `5f312e48217a060239bfc782`. 

## Scalability
No request-specific data is stored in this application. As a result, we can run as many instance of this application as we want --
we just have to add a load balancer for balancing the load among the instances. 

In case of database, we can use a MongoDB cluster. In case of cache, we can easily use a Redis cluster.

So, this application is easily scalable.

## Stacks Used
Programming Language: Java

Framework: Spring Boot

Database: MongoDB

Cache: Redis
