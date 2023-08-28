const express = require('express');
const fs = require('fs');
const jwt = require('jsonwebtoken');
const bodyParser = require('body-parser');

const app = express();
app.use(bodyParser.json());

function generateToken(jsonPayload, privateKey, options = {}) {
	const defaultOptions = {
		algorithm: 'RS256',   // Use RS256 algorithm
	//	expiresIn: '1h',       // Token expiration time
	};

	const finalOptions = { ...defaultOptions, ...options };

	return jwt.sign(jsonPayload, privateKey, finalOptions);
}

// Load private key and public key
const privateKey = fs.readFileSync('private-key.pem', 'utf8');
//const publicKey = fs.readFileSync('public-key.pem', 'utf8');

app.post('/generate-token', (req, res) => {
	// const jsonPayload = { sub: '1234567890', name: 'John Doe', iat: Math.floor(Date.now() / 1000) };

	const requestBody = req.body; // Assuming the request body contains the payload
	if (!requestBody) {
		return res.status(400).json({ error: 'Missing request body' });
	}

	const expirationTime = Math.floor(Date.now() / 1000) + 60 * 60; // 60 minutes from now
	
	const jwtPayload = {
		...requestBody,
		appIdentifier: 'MyMockAPI', // Add a unique indicator for your app
		iss: 'http://mylocalapp:3000',
		expiresIn: expirationTime,
	};

	

	const jwtToken = generateToken(jwtPayload, privateKey);

	res.json({ token: jwtToken });
});

app.listen(3000, () => {
	console.log('Server is running on port 3000');
});
