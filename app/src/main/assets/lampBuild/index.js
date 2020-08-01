const child_process = require('child_process');

async function client() {
	try {
		require('./client/index.js');
	} catch (error) {
		console.log(error);
	}

}

async function server() {
	try {
        require('./api/app.js');
	} catch (error) {
		console.log(error);
	}

}

client();
server();