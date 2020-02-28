var express = require('express');
var app = express();
var http = require('http').Server(app);
var redis = require('redis');
var io = require('socket.io')(http)

app.get('/', function(request, response) {
    response.sendFile(__dirname + '/index.html');
});

app.get('/update-page', function(request, response) {
    response.sendFile(__dirname + '/edit-page.html');
});

app.get('/download', function(req, res){
  const file = __dirname+'/index.html';
  res.download(file); // Set disposition and send it.
});

http.listen(3300, function(){
    console.log('listening on *:3300');
});