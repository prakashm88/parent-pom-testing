const express = require('express');
const app = express();
const port = process.env.PORT || 3001;
const usersRouter = require('./routes/users');
var bodyParser = require('body-parser')

app.use(bodyParser.json());
  
app.get('/', (req, res) => {
	res.json({ message: 'alive' });
});

app.use('/users', usersRouter);

app.listen(port, () => {
	console.log(`Example app listening at http://localhost:${port}`);
});