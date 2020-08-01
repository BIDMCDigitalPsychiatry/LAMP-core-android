
	try {
        

        console.log("client running");
        var http = require('http');
var versions_server = http.createServer( (request, response) => {
  response.end('Versions: ' + JSON.stringify(process.versions));
});
versions_server.listen(5000);
console.log('The node project client has started in port 5000.');

	} catch (error) {
		console.log(error);
	}



	