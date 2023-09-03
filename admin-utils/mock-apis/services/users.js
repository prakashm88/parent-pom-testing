const db = require('../services/db');
const config = require('../config');
const axios = require('axios');

function getMultiple(page = 1) {
	const offset = (page - 1) * config.listPerPage;
	const data = db.query(`SELECT * FROM users LIMIT ?,?`, [offset, config.listPerPage]);
	const meta = { page };

	return {
		data,
		meta
	}
}

function getUserRoles(userObj) {
	if (!userObj || !userObj.userId) {
		let error = new Error("Invalid User id");
		error.statusCode = 400;

		throw error;
	}
	console.log("Fetch roles for user: " + userObj.userId)
	const data = db.run(`SELECT * FROM user_role where userid = ?`, userObj.userId);

	return {
		data
	}
}

function validateUpdate(user) {
	let messages = [];
	console.log("Obtained user obj: ", user);

	if (!user) {
		messages.push('No object is provided');
	}

	if (!user.id) {
		messages.push('id is empty');
	}

	if (messages.length) {
		let error = new Error(messages.join());
		error.statusCode = 400;

		throw error;
	}
}

function validateCreate(user) {
	let messages = [];

	console.log("Obtained user obj: ", user);

	if (!user) {
		messages.push('No object is provided');
	}

	if (!user.username) {
		messages.push('username is empty');
	}

	if (!user.name) {
		messages.push('name is empty');
	}

	if (!user.email) {
		messages.push('email is empty');
	}

	if (!user.phone) {
		messages.push('phone is empty');
	}

	if (!user.website) {
		messages.push('website is empty');
	}

	if (!user.company) {
		messages.push('company is empty');
	}


	if (messages.length) {
		let error = new Error(messages.join());
		error.statusCode = 400;

		throw error;
	}
}

function getRandomUser(req, res) {
	let url = `https://randomuser.me/api/`;

	axios({
		method: 'get',
		url,
		headers: { 'content-type': 'application/json' },
	})
		.then(function(response) {
			console.log(JSON.stringify(response.data));
			res.json(JSON.stringify(response.data));
		})
		.catch(function(error) {
			console.log(error);
		});
}

function createRandomUser(req, res) {

	let count = req.body?.userCount;

	if (count == null || count == undefined || count > 200) {
		let error = new Error("Invalid count!");
		error.statusCode = 400;

		throw error;
	}

	let url = `https://randomuser.me/api/?nat=gb,us,in&results=${count}`;

	axios({
		method: 'get',
		url,
		headers: { 'content-type': 'application/json' },
	})
		.then(function(response) {
			console.log(JSON.stringify(response.data));

			let users = response.data.results;

			let processedData = [];
			let erroredData = [];

			for (const user of users) {
				let userNew = {
					"name": user.name.first + " " + user.name.last,
					"username": user.login.username,
					"email": user.email,
					"phone": user.cell,
					"website": "http://itechgenie.com/apps/user/" + user.login.uuid,
					"company": user.name.first + " & Co."
				}
				console.log("processing : " + userNew);
				try {
					let createdUser = create(userNew);
					processedData.push(createdUser);
				} catch (e) {
					userNew.error = e;
					erroredData.push(userNew);
				}
			}

			res.json({ "data": { "message": processedData.length + " users created", erroredData: erroredData } });
		})
		.catch(function(error) {
			console.log(error);
		});
}

function __getUser(userId) {
	const data = db.get(`SELECT * FROM users where id=?`, userId);
	console.log("Obtained data: ", data);
	return {
		data
	}
}

function getUser(userId) {
	
	if (!userId) {
		let error = new Error("Invalid User id");
		error.statusCode = 400;

		throw error;
	}
	
	const data = db.get('SELECT usr.*,  ifnull(rol.role_name, \'NoRole\') as role, ' +
		' ifnull(rol.role_desc, \'No Role Assigned\') as role_desc FROM users usr ' +
		' LEFT JOIN user_role usr_r ON usr_r.userid = usr.id LEFT JOIN itg_roles rol ON usr_r.roleid = rol.id ' +
		' WHERE usr.id = ?', userId);
	console.log("Obtained data: ", data);
	return {
		data
	}
}

function deleteUser(userId) {
	const data = db.run(`delete FROM users where id=?`, userId);
	console.log("deleted data: ", data);
	return {
		data
	}
}

function updateUser(user) {

	validateUpdate(user);

	let actualUser = getUser(user.id);
	let updateUser = { ...actualUser.data, ...user };
	console.log("Data for update: ", updateUser);

	validateCreate(updateUser);
	const { id, name, username, email, phone, website, company } = updateUser;

	const result = db.run('update users set name =?, username =?, email =?, phone =?, website = ?, company =? where id =?', [name, username, email, phone, website, company, id]);

	console.log("Obtained result: ", result);
	return {
		result
	}
}

function create(userObj) {
	validateCreate(userObj);
	const { name, username, email, phone, website, company } = userObj;
	const result = db.run('INSERT INTO users("name","username","email","phone","website","company") VALUES (@name, @username, @email, @phone, @website, @company)', { name, username, email, phone, website, company });

	let message = 'Error in creating user';
	if (result.changes) {
		message = 'user created successfully';
	}

	return { message };
}

module.exports = {
	getMultiple,
	create,
	getRandomUser,
	createRandomUser,
	getUser,
	deleteUser,
	updateUser,
	getUserRoles
}