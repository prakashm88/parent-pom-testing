const express = require('express');
const router = express.Router();
const users = require('../services/users');

/* GET users listing. */
router.get('/', function(req, res, next) {
	try {
		res.json(users.getMultiple(req.query.page));
	} catch (err) {
		console.error(`Error while getting users `, err.message);
		next(err);
	}
});

router.post('/roles', function(req, res, next) {
	try {
		console.log("get user role " + req.body) ;
		res.json(users.getUserRoles( req.body));
	} catch (err) {
		console.error(`Error while getting user roles `, err.message);
		next(err);
	}
});

router.get('/find/:userId', function(req, res, next) {
	let userId = req.params.userId;
	try {
		console.log("Find user " + userId) ;
		res.json(users.getUser(userId));
	} catch (err) {
		console.error(`Error while getting users ${userId}`, err.message);
		next(err);
	}
});

router.post('/find', function(req, res, next) {
	let userId = req.body.id;
	try {
		console.log("Find user " + userId) ;
		res.json(users.getUser(userId));
	} catch (err) {
		console.error(`Error while getting users ${userId}`, err.message);
		next(err);
	}
});

router.post('/delete/:userId', function(req, res, next) {
	let userId = req.params.userId;
	try {
		console.log("delete user " + userId) ;
		res.json(users.deleteUser(userId));
	} catch (err) {
		console.error(`Error while deleting users ${userId}`, err.message);
		next(err);
	}
});

router.post('/update', function(req, res, next) {
	try {
		console.log("update user " + req.body) ;
		res.json(users.updateUser( req.body));
	} catch (err) {
		console.error(`Error while update users`, err.message);
		next(err);
	}
});

/* POST users */
router.post('/', function(req, res, next) {
	try {
		console.log(req.body) ;
		res.json(users.create(req.body));
	} catch (err) {
		console.error(`Error while adding users `, err.message);
		next(err);
	}
});

router.get('/random', function(req, res, next) {
	try {
		console.log(req.body) ;
		users.getRandomUser(req, res);
	} catch (err) {
		console.error(`Error while adding users `, err.message);
		next(err);
	}
});

router.post('/random/create', function(req, res, next) {
	try {
		console.log(req.body) ;
		users.createRandomUser(req, res);
	} catch (err) {
		console.error(`Error while adding users `, err.message);
		next(err);
	}
});

module.exports = router;