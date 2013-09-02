Cart Plugins - Cart API Engine for Java
=======================================

Cart API encoding and decoding engine for Java. Used by cart plugins implementing the [Cart API protocol]. If you are developing a new cart plugin in a Java-based environment, use the Java classes provided here to encode and decode the various Cart API protocol entities.

Classes
-------

* `appixia.engine.Engine` - The general engine class, use this class when handling a Cart API request. Contains utility functions that handle a new request, parse the request metadata and initialize the correct encoder/decoder classes according to the metadata. For handling a new Cart API request, all you have to do is use the function `Engine.handleRequest()` which parses all the relevant arguments and initializes the correct encoder.
* `appixia.engine.Helpers` - Class containing various static helper utility functions. For example, use `Helpers.createSuccessResponse()` to create a successful response to the Cart API request, or use `Helpers.dieOnError()` to create a failure response and exit.

Mediums
-------

Cart API supports various mediums for encoding/decoding protocol calls. For example, XML over HTTP POST and JSON over HTTP GET are two different mediums. For more information about mediums see [Cart API mediums reference].

* `XML` - Implementation of encoder/decoder for the XML over HTTP POST medium.
* `JSON` - Implementation of encoder/decoder for the the JSONP over HTTP GET medium.

Internal Classes
----------------

* `appixia.engine.Mediums.Encoder` - Base class for an encoder. All encoders (for the various supported mediums) inherit from it. Should not be used directly.

  [Cart API protocol]: http://kb.appixia.com/cartapi:ver1:introduction
  [Cart API mediums reference]: http://kb.appixia.com/cartapi:ver1:mediums
